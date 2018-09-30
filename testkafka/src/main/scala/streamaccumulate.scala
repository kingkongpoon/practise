object streamaccumulate {
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



    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")//.set("logLevel","WARN")

    val sc = new SparkContext(config = conf)

    val ssc = new StreamingContext(sc, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "namenode:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      //"auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    ssc.checkpoint("D:\\download\\分布式工具\\accumulator1")


    val topics = Array("test1")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )


    """去重统计，类似UV的统计"""
    val wordS = stream.flatMap(_.value.split(" ")).map((_,1))

    val wordCounts = wordS.updateStateByKey(
      (values:Seq[Int],statu:Option[Int]) => {
        var newvalue =statu.getOrElse(0)
        values.foreach(newvalue += _)
        Option(newvalue)
      }
    )

    val accum = sc.longAccumulator("uv")

    wordCounts.foreachRDD(f => {
      if(f.count > accum.value){
        println(f.count)
        accum.add(f.count - accum.value)
      }
    })
///==================================================================================================

    """按key做累加统计，类似金额实时统计"""
     val addFunc = (currValues: Seq[Int], prevValueState: Option[Int]) => {
      //通过Spark内部的reduceByKey按key规约，然后这里传入某key当前批次的Seq/List,再计算当前批次的总和
     val currentCount = currValues.sum
      // 已累加的值
     val previousCount = prevValueState.getOrElse(0)
      // 返回累加后的结果，是一个Option[Int]类型
      Some(currentCount + previousCount)
    }

    //val lines = ssc.socketTextStream(args(0), args(1).toInt)
    val words = stream.map(x => x.value).map(y => y.split(":"))
    val pairs = words.map(word => {println("=================");(word(0), word(1).toInt)})

    //val currWordCounts = pairs.reduceByKey(_ + _)
    //currWordCounts.print()

    val totalWordCounts = pairs.updateStateByKey[Int](addFunc)


    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.foreachPartition { _ =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
    }


    //println(price.value.toString)
    wordCounts.print()
    //totalWordCounts.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
