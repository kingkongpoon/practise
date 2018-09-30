import java.sql.DriverManager
import java.util.concurrent.{Executors, ExecutorService}
/**
  * Description:
  * Author: tangli
  * Version: 1.0
  * Create Date Time: 2018/8/27 0027 下午 4:49.
  * Update Date Time:
  * see
  */

object scalaThrift {
  def main(args : Array[String]):Unit={
    val threadPool:ExecutorService=Executors.newFixedThreadPool(1)
    try {
      //提交5个线程
      for(i <- 1 to 1){
        //threadPool.submit(new ThreadDemo("thread"+i))
        threadPool.execute(new ThreadDemo("thread"+i))
      }
    }finally {
      threadPool.shutdown()
    }

    class ThreadDemo(threadName:String) extends Runnable{
      override def run(){
        val driver="org.apache.hive.jdbc.HiveDriver"
        Class.forName(driver)
        val (url,username,userpasswd)=("jdbc:hive2://node1:10000","","")
        val connection=DriverManager.getConnection(url,username,userpasswd)
        connection.prepareStatement("use default").execute()
        val sql="select * from t_10002"
        val statement=connection.prepareStatement(sql)
        val rs=statement.executeQuery()
        //获取列数
        val colnums = rs.getMetaData().getColumnCount
        while(rs.next()){
          //列索引从1开始
          for(i <- Array.range(1, colnums + 1)){
            print(s"${rs.getMetaData.getColumnLabel(i)}:${rs.getString(i)} \t ")
          }
          println()
        }
        }
      }
    }


//    val driver="org.apache.hive.jdbc.HiveDriver"
//    Class.forName(driver)
//    val (url,username,userpasswd)=("jdbc:hive2://node1:10000","","")
//    val connection=DriverManager.getConnection(url,username,userpasswd)
//    connection.prepareStatement("use default").execute()
//    val sql="SELECT trade__thedate AS numbers_0,trade__alipay_trade_amt AS numbers_1,etl_x3y5__cal_9 AS numbers_2,logistics__alipay_trade_cnt AS numbers_3,flow__pv AS numbers_4,flow__uv AS numbers_5,etl_x3y5__cal_10 AS numbers_6,etl_x3y5__cal_1 AS numbers_7,trade__per_cust_trans AS numbers_8,etl_x3y5__cal_12 AS numbers_9,trade__alipay_auction_num AS numbers_10,etl_x3y5__cal_4 AS numbers_11,conversion__cr AS numbers_12,etl_x3y5__cal_13 AS numbers_13,trade__alipay_winner_num AS numbers_14,trade__alipay_winner_num-trade__trade_repeat_num AS numbers_15,trade__trade_repeat_num AS numbers_16,interaction__collect_cnt AS numbers_17,interaction__collect_cnt_sum AS numbers_18 FROM t_10001"
//    val statement=connection.prepareStatement(sql)
//    val rs=statement.executeQuery()
//    //获取列数
//    val colnums = rs.getMetaData().getColumnCount
//    while(rs.next()){
//      //列索引从1开始
//      for(i <- Array.range(1, colnums + 1)){
//        print(s"${rs.getMetaData.getColumnLabel(i)}:${rs.getString(i)} \t ")
//      }
//      println()
//    }

}
