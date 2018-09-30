import java.sql.DriverManager

import org.apache.hive.service.cli.thrift.TCLIService.Client
import org.apache.log4j.{Level, Logger}
import org.apache.spark._
import org.apache.spark.sql.SQLContext
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.{TFastFramedTransport, TSocket}

object mysqlcontrol {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop\\hadoop_home_bin")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setAppName("MYSQL").setMaster("local[2]")

    val sc = new SparkContext(config = conf)

    val sparksql = new SQLContext(sc)


    //定义一个读入数据的函数，返回一个dataframe
    def readTable(dbname : String ,dbtable : String)={
      val table = sparksql.read.format("jdbc")
        .option("url",s"jdbc:mysql://localhost:3306/${dbname}?useSSL=false")
        .option("dbtable",s"${dbtable}")
        .option("user","")
        .option("password","")
        .load()

        table
    }



    val dtable = readTable("test","client")
    val ctable = readTable("test","client")
//
    ctable.registerTempTable("ctable")
    dtable.registerTempTable("dtable")
//
//


    import org.apache.spark.sql.functions.{regexp_replace,udf,concat,concat_ws,array,lit}
    dtable.show(100,false)


    """两列字符合并"""
    //1，定义一个函数，传入两列的数据拼接
    def addWord(word1 : String,word2 : String) = {word1 + "-" + word2}

    //2，定义一个函数，传入边长参数，做拼接
    def addWordAll(seq: String,cols:String*) = {
      cols.mkString(seq)
      }


    // 注册到udf中，传入名字和函数，已定义的函数体
    //sparksql.udf.register("wordadd",addWord(_,_))
    sparksql.udf.register("wordadd",addWord _)
    sparksql.udf.register("wordaddAll",addWordAll _)

    val udf_addwordall = udf(addWordAll _)

    //2，隐式函数，作用与上面一样wordadd作用一样
    sparksql.udf.register("wordadd2",(s1:String,s2:String) => {s1 + "-" + s2})

    //SQL方式调用
    sparksql.sql("select wordadd(firsttime,lasttime) udfword from ctable").show
    sparksql.sql("select wordadd2(firsttime,lasttime) udfword from ctable").show

    sparksql.sql("select concat_ws('-',uid,firsttime,lasttime) udfwordAll from ctable").show

    //DSL调用
    ctable.select(udf_addwordall(lit("+"),array(ctable("uid"),ctable("firsttime"),ctable("lasttime")))).show()

    //SQL函数
    sparksql.sql("select concat_ws('-',firsttime,lasttime) udfwordconcat from ctable").show

    //新建一列空值
    def nullValue(col : String) = "Nil"

    //时间处理
    import scala.util.matching.Regex
    def timeStandard(col : String)={
      col.slice(0,4) + "-" + col.slice(4,6) + "-" +col.slice(6,8)
    }

    sparksql.udf.register("timeStandard",timeStandard _)

    //DSL调用之前写的函数
    val udf_addWord = udf(addWord _)

    val udf_nullWalue = udf(nullValue _)

    val table_addword = dtable.withColumn("did2",udf_addWord(dtable("uid"),dtable("did")))

    table_addword.show()

    val table_nullvalue = table_addword.withColumn("nuvalue",udf_nullWalue(table_addword("uid")))

    table_nullvalue.show()

    //调用时间处理自定义函数
    val udf_timestandard = udf(timeStandard _)

    val table_time = table_nullvalue.withColumn("timestandard",udf_timestandard(table_nullvalue("atime")))


    //新增一列NULL列，和上面nullValue函数作用一样
    table_nullvalue.withColumn("999",lit("NULL")).show

    table_time.show()

    table_time.registerTempTable("tabletime")



    //sparksql.sql("SELECT timestandard ,SUM(cost) total FROM tabletime GROUP BY timestandard ORDER BY timestandard DESC").show


    println("=="*30)

    //timeStandard函数注册后，用到SQL中
    //sparksql.sql("""select uid ,timeStandard(firsttime) ftime,timeStandard(lasttime) ltime from ctable left join dtable on (ctable.uid = dtable.uid)""").show

    val timestand = sparksql.sql("""SELECT c1.uid ,timeStandard(c1.firsttime) ftime,timeStandard(c1.lasttime) ltime from ctable c1""")//.cache()//.show(100,false)



   // sparksql.sql("""SELECT c1.uid,timeStandard(c1.firsttime) ts,d1.cost,d1.did FROM ctable c1 ,dtable d1 WHERE c1.uid = d1.uid""").show
    sparksql.sql("""SELECT *,timeStandard(d1.atime) ts from dtable d1 left join ctable c1 ON (d1.uid = c1.uid)""").show(100,false)
    sparksql.sql("""SELECT sum(d1.cost),d1.atime from dtable d1 right join ctable c1 ON (d1.uid = c1.uid) GROUP BY d1.atime""").show(100,false)
    //println(timestand.count())

    //timestand.registerTempTable("c1")

    //sparksql.sql("select * from c1 right join dtable on (c1.uid = dtable.uid)").show(100,false)

    //RIGHT JOIN dtable d1 ON (c1.uid = d1.uid)
  }
}
