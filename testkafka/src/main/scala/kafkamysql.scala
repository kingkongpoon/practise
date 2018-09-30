import java.sql.{DriverManager, ResultSet}
import java.sql.DriverManager
import java.sql.Connection

//import kafka.KafkaTest

import scala.util.parsing.json.{JSON, JSONArray, JSONFormat, JSONObject}


object kafkamysql {
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    def mysqlshow(kafkatext:String) {
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false"
      val username = "root"
      val password = "123456"

      var connection: Connection = null

      try {

        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)

        val statement = connection.createStatement()
//
//        val text = "text"
//
//        val value = kafkatext


        val resultSet = statement.executeQuery("select * from kafkatest")
        while (resultSet.next()) {
          val name = resultSet.getString("text")
          val n = resultSet
          //val password = resultSet.getString("body")
          println("name, password = " + name)
        }
      } catch {
        case e => e.printStackTrace
        //case _: Throwable => println("ERROR")
      }
      connection.close()
    }

    def mysqladd(kafkatext:String): Unit ={
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false"
      val username = "root"
      val password = "123456"
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement
      // do database insert
      try {
        Class.forName(driver)


        val text = "text"

        val value = kafkatext

        var sqltext = s"INSERT INTO `kafkatest` (`$text`) VALUES ('$value')"

        println(sqltext)

        val rs2 = statement.executeUpdate(sqltext)

        connection.close
      }
      finally {
        connection.close
      }
    }


    def mysqlcontrol(kafkatext:String): Unit ={
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false"
      val username = "root"
      val password = "123456"
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement

      val text_0 : String = kafkatext.split(" ")(0).toUpperCase


      println(text_0)

//      text_0 match {
//          case "SELECT " => {}
//
//          case _ => {}
//      }

    }


    //============================================================================================
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



    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")//.set("logLevel","WARN")

    //val spark = new SparkSession()

    val sc = new SparkContext(conf)

    //val a = spark.readStream


    val ssc = new StreamingContext(sc, Seconds(1))
    //val ssc = new StreamingContext(conf, Seconds(1))

    //val textfile = ssc.sparkContext.textFile("")

    //val textsql = ssc.sparkContext

    //val sc = new SparkContext(config = conf)

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "master:9092,cdh001:9092,cdh002:9092,cdh003:9092,cdh004:9092,cdh005:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      //"auto.offset.reset" -> "earliest"
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    ssc.checkpoint("D:\\download\\分布式工具\\3")


    val topics = Array("sync_bi_schema_roleuser")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    //val wordCounts = stream.map(record => mysqlcontrol(record.value()))
    val wordCounts = stream.map(record => record.value.split(" ")).flatMap(lines => lines).map(words => (words , 1)).reduceByKey(_ + _)
    //val wordCounts = stream.map(record => record.value.split(" ")).flatMap(lines => lines).map(words => (words , 1)).reduceByKeyAndWindow(_ + _,Seconds(20))

//    def hi(name :String): String = {
//      val sname = name.replace(" ", "")
//      sname
//    }


    //val word = stream.map( x => print(x.value))//.map( y => y(0))


    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.foreachPartition { iter =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    }

    wordCounts.print()
    //word.print
    ssc.start()
    ssc.awaitTermination()


  }

}
