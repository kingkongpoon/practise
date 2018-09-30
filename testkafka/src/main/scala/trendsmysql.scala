import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object trendsmysql {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    //val conf = new SparkConf().setAppName("MYSQL").setMaster("local[2]")

   // val sc = new SparkContext(conf)

    //val sparksql = new SQLContext(sc)

    val sparksql = SparkSession.builder()
      .appName("appName")
      .master("local[2]")
      .getOrCreate()

    val sc = sparksql.sparkContext

    //val ssc = new StreamingContext(sc, Seconds(5))


    def readTable(dbname: String, dbtable: String) = {
      sparksql.read.format("jdbc")
        .option("url", s"jdbc:mysql://localhost:3306/${dbname}?useSSL=false")
        .option("dbtable", s"${dbtable}")
        .option("user", "root")
        .option("password", "123456")
        .load().createOrReplaceTempView(s"${dbtable}")
    }

    def call(info:String)= {
      val table = sparksql.read.format("jdbc")
        .option("url", s"jdbc:mysql://localhost:3306/test?useSSL=false")
        .option("dbtable", info)
        .option("user", "root")
        .option("password", "123456")
        .load()
      table
    }

    val tablelist = List[String]("client", "detail", "kafkatest", "sparktest")

    val json_list = scala.collection.mutable.Map[String,org.apache.spark.sql.DataFrame]()

    for (i <- tablelist) {readTable("test", i)}

    for(i <- tablelist) {
      json_list(i) = call(i)
    }

   // for (i <- tablelist) {sparksql.sql(s"select * from ${i}").show}
//    sparksql.sql("select d1.uid,d1.atime,sum(d1.cost) from client c1 right join detail d1 on c1.uid = d1.uid group by d1.uid,d1.atime order by d1.uid")
//      .show(100,false)
    import org.apache.spark.sql.functions.col
    json_list("detail").filter(col("cost").===(100)).show()

  }
}
