
import java.sql.DriverManager


object testpresto {
  def main(args:Array[String]):Unit={
    val driver="com.facebook.presto.jdbc.PrestoDriver"
    Class.forName(driver)
    val connection=DriverManager.getConnection("jdbc:presto://node1:9999/hive/default","hive",null)
    val sql="select * from t_10002 order by interaction__thedate"
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
