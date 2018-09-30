package main.scala
import java.util.Calendar

import com.alibaba.fastjson.JSON
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaWarehousing {
  case class Commodity_effect(thedate:String,auction_id:String,auction_name:String,auction_online_state:String,auction_link:String,ipv:String,iuv:String,per_visit_duration:String,bounce_rate:String,trade_cr:String,alipay_trade_cr:String,cr:String,gmv_trade_amt:String,gmv_auction_num:String,gmv_winner_num:String,alipay_trade_amt:String,alipay_auction_cnt:String,cart_itm_qty_sum:String,uv_value:String,click_times:String,click_rate:String,exposure_qty:String,collect_cnt:String,search_guide_buyers:String,per_cust_trans:String,search_conversion:String,search_traffic:String,alipay_winner_num:String,refunds_amt:String,refunds_cnt:String)
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("ReadKafka").getOrCreate()
    val sparkContext = spark.sparkContext
    val ssc = new StreamingContext(sparkContext, Seconds(3))
//    Logger.getLogger("org").setLevel(Level.ERROR)

    //创建kafka消费者
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.15.199:6667,192.168.15.198:6667,192.168.15.202:6667",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "kafka",
      "auto.offset.reset" -> "earliest    ",
      "enable.auto.commit" -> (false: java.lang.Boolean))
    val topics = Array("commodity_effect")
    Logger.getLogger("org").setLevel(Level.ERROR)
    val stream = KafkaUtils
      .createDirectStream(ssc, PreferConsistent, Subscribe[String, String](topics, kafkaParams))
//    val input = stream.flatMap(line => {
//      Some(line.value.toString)
//    })
    val input = stream.map(line=> {
      //json字符串转json对象
      val jsonObj = JSON.parseObject(line.value())
      //店铺id
      val shopid = jsonObj.get("shop_id").toString
      //表名
      val table = jsonObj.get("table_name").toString
      //时间
      val time = jsonObj.get("version").toString
      //模块
      val module=jsonObj.get("module_name").toString
      //String转Int
      val resInt = Integer.parseInt(time)
      //时间戳转时间
      val cal = Calendar.getInstance()
      cal.setTimeInMillis(resInt * 1000L)
      //年份
      val year: Int = cal.get(Calendar.YEAR)
      //月份
      val month: Int = cal.get(Calendar.MONTH) + 1
      //日
      val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        //路径地址
        val path = "hdfs://node1/shop_space/" + shopid + "/cloud_data/"+module+"/" + table + "/" + year + "/"+month + "/" + day
        val spark1 = SparkSession.builder.getOrCreate
        if(jsonObj.get("complete").toString.equals("false")) {
    //定义一个Affairsl的List
        val x9 = List(Commodity_effect(jsonObj.get("terminal").toString, jsonObj.get("auction_id").toString, jsonObj.get("auction_name").toString, jsonObj.get("auction_online_state").toString
         , jsonObj.get("auction_link").toString, jsonObj.get("ipv").toString, jsonObj.get("iuv").toString, jsonObj.get("per_visit_duration").toString, jsonObj.get("bounce_rate").toString
         , jsonObj.get("trade_cr").toString, jsonObj.get("alipay_trade_cr").toString, jsonObj.get("cr").toString, jsonObj.get("gmv_trade_amt").toString, jsonObj.get("gmv_auction_num").toString
         , jsonObj.get("gmv_winner_num").toString, jsonObj.get("alipay_trade_amt").toString, jsonObj.get("alipay_auction_cnt").toString
          , jsonObj.get("cart_itm_qty_sum").toString, jsonObj.get("uv_value").toString, jsonObj.get("click_times").toString, jsonObj.get("click_rate").toString, jsonObj.get("exposure_qty").toString, jsonObj.get("collect_cnt").toString
         , jsonObj.get("search_guide_buyers").toString, jsonObj.get("per_cust_trans").toString, jsonObj.get("search_conversion").toString, jsonObj.get("search_traffic").toString
          , jsonObj.get("alipay_winner_num").toString, jsonObj.get("refunds_amt").toString, jsonObj.get("refunds_cnt").toString))
    //创建Dataframe
      println(x9)
    val data1 = spark1.sqlContext.createDataFrame(x9)
    //注册成一个表
    val data3 = data1.registerTempTable("temp")
    //实现数据追加写入HDFS
    val data2 = spark1.sql("select * from temp").write.mode(SaveMode.Append).csv(path)

  }
    })

    input.print(100000000)
    ssc.start()
    ssc.awaitTermination()
  }
}
