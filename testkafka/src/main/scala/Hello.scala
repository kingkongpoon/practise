import org.apache.spark._


import org.apache.kafka.common.serialization.StringDeserializer

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by prince on 2018/5/17.
  */
object Hello {

  val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")//.set("logLevel","WARN")
  val ssc = new StreamingContext(conf, Seconds(1))

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("ReadKafka").master("local[*]").getOrCreate()
    val sparkContext = spark.sparkContext
    val ssc = new StreamingContext(sparkContext, Seconds(10))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "node2:6667",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "kafka.group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val topics = Array("test1")
    val stream = KafkaUtils
      .createDirectStream(ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))

    val input = stream.flatMap(line => {
      Some(line.value.toString)
    })
    input.foreachRDD(rdd => {
      if (!rdd.isEmpty()){
        val spark1 = SparkSession.builder.getOrCreate
        val df = spark1.read.json(rdd)

        df.createOrReplaceTempView("temp")
        val ans = spark1.sql("select user_id,order_id,goods_title,pay_time, target_percent from temp").rdd.map(x => {
          ( x.getString(0), x.getString(1),x.getString(2),x.getString(3),x.getString(4))

        }).saveAsTextFile("hdfs://192.168.15.198/bi")

      }
    })

    ssc.start()
    ssc.awaitTermination()
  }
}