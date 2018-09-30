import org.apache.spark.sql.SQLContext

object kafka {
  def main(args: Array[String]): Unit = {
    import org.apache.kafka.clients.consumer.ConsumerRecord
    import org.apache.spark._
    import org.apache.spark.streaming._
    import org.apache.kafka.common.serialization.StringDeserializer
    import org.apache.spark.streaming.kafka010._
    import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
    import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
    import org.apache.spark.streaming.kafka010.KafkaUtils.createDirectStream
    import org.apache.spark.streaming.kafka010.KafkaUtils.createRDD
    import org.apache.kafka.common.TopicPartition
    import org.apache.log4j.{Level, Logger}
    import org.apache.spark.sql._


    //val a = new TopicPartition("test1",0)

    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")//.set("logLevel","WARN")
    val ssc = new StreamingContext(conf, Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.15.199:6667",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      //"auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

//    ssc.checkpoint("D:\\download\\分布式工具\\2")


    val topics = Array("test1")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val wordCounts = stream.map(record => record.value.split(" ")).flatMap(lines => lines).map(words => (words , 1)).reduceByKey(_ + _)
    //val wordCounts = stream.map(record => record.value.split(" ")).flatMap(lines => lines).map(words => (words , 1)).reduceByKeyAndWindow(_ + _,Seconds(20))

//    def hi(name :String): String = {
//      val sname = name.replace(" ", "")
//      sname
//    }
//
//
//    //https://bbs.csdn.net/topics/392020622
//
//
//    stream.foreachRDD { rdd =>
//      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//      rdd.foreachPartition { iter =>
//        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
//        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
//      }
//    }

    wordCounts.print()
    //word.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
