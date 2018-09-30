import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.functions._

object writedata {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop-common-2.2.0-bin")
    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName("WRITE DATA").setMaster("local[2]")

    val sc = new SparkContext(conf)

    val sparksql = new SQLContext(sc)

    val text = sparksql.read.format("csv")
        .option("header",false).load("hdfs://namenode:9000//BItest/test/*/*/")
    text.show(100)
    println(text.count())
//    text.groupBy("c1").pivot("c2").agg(("c3","sum")).show()
//    val output = text.groupBy("c1").pivot("c2").agg(("c3","sum"))
//
//    val colNames = output.columns
//
//    var df1 = output
//    for (colName <- colNames.tail) {
//      df1 = df1.withColumn(colName, col(colName).cast(LongType))
//    }
//
//    df1.show()
//    output.write.mode("overwrite").format("csv")
//      .option("encoding","utf8")
//      .option("header","true")
//      .save("D:\\F\\qingmu\\2018\\7month\\bicodev2art\\ETLdata\\output")
//    text.registerTempTable("abc")
//    sparksql.sql("select did,uid, case when cost < 250 then 0 when cost < 300 then 1 else cost end ac from abc").show
//    sparksql.sql("select *,abs(did) from abc").show

    //    text.show()

//    text.write.mode("append").format("jdbc").option("url", "jdbc:mysql://localhost:3306/test?useSSL=false")
//      .option("dbtable", "detail")
//      .option("user", "root")
//      .option("password", "123456").save()

  }
}
