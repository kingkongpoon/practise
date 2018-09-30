import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import java.sql.DriverManager
import java.sql.Connection

import org.apache.spark.sql.SQLContext

import scala.util.parsing.json.JSON

object kafkamysqlV2 {
  def main(args: Array[String]): Unit = {


    """数据库处理逻辑代码"""


    //将一个字符串转为key:value
    def regixJson(json:Option[Any]) =json match {
      case Some(map: Map[String, Any]) => map
    }

    def mysqlJudge(kafkatext:String)={

      val text = kafkatext

      val text_out_json = JSON.parseFull(text)

      val out_json =regixJson(text_out_json)

      //获取外层head的json
      val head_msg = regixJson(out_json.get("head"))
//      println(head_msg)
      //获取head里面type的值
      val head_type = head_msg.get("type").head
      val head_table = head_msg.get("table").head
//      println(head_type)

      head_type match {
          //INSERT情况处理
        case "INSERT" => {
          val after_msg = regixJson(out_json.get("after"))
          val after_keys = after_msg.keys
          var colnames : String = ""
          var values : String =""
          for(i <- after_keys){
            colnames += i + ","
            values += "'" + after_msg.get(i).head.toString + "',"
          }
          //去掉字符串最后一个元素，“，”
          colnames = colnames.init
          values = values.init
          var sqltext = s"INSERT INTO `$head_table`($colnames) VALUES ($values)"
          println(sqltext)
          sqltext
        }
        case "UPDATE" => {
          "DROP TABLE test.kafkatest"
        }

        case _ => {
          ""
        }
      }

    }


    def mysqlControl(kafkatext:String): String ={

      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false"
      val username = "root"
      val password = "123456"
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()


      try{
        Class.forName(driver)

        val text = kafkatext
        val sqltext =mysqlJudge(kafkatext)

//
        val rs2 = statement.executeUpdate(sqltext)
//
        connection.close
//
        sqltext
      }
      finally {
        connection.close
      }

    }



    """Streaming运行代码"""


    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("kafkamysql")

    val sc = new SparkContext(config = sparkConf)

    //sc.setCheckpointDir("D:\\download\\分布式工具\\4")

    val sparksql = new SQLContext(sc)

    var detail_data = sparksql.read .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/test?useSSL=false")
      .option("dbtable", "detail")
      .option("user", "root")
      .option("password", "123456")
      .load()

    var client_data = sparksql.read .format("jdbc")
      .option("url", "jdbc:mysql://localhost:3306/test?useSSL=false")
      .option("dbtable", "client")
      .option("user", "root")
      .option("password", "123456")
      .load()

    detail_data.show(20,false)

    client_data.show(20,false)

    detail_data = detail_data.union(detail_data)

    detail_data.show(100,false)

    val ssc = new StreamingContext(sc,Seconds(1))


    ssc.checkpoint("D:\\download\\分布式工具\\4")

    val kafkaParams = Map[String, Object](
      //"bootstrap.servers" -> "master:9092,cdh001:9092,cdh002:9092,cdh003:9092,cdh004:9092,cdh005:9092",
      "bootstrap.servers" -> "namenode:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      //"auto.offset.reset" -> "earliest"
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val topics = Array("test1")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val kafkatext = stream.map(record => (record.value)).map( word => mysqlControl(word))
//

    kafkatext.print()


    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.foreachPartition { iter =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    }


    ssc.start()
    ssc.awaitTermination()

  }




}


