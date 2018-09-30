import java.sql.{ResultSet, DriverManager}
import java.sql.DriverManager
import java.sql.Connection

import scala.util.parsing.json.{JSONFormat, JSONObject, JSONArray, JSON}


object scalamysql {
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    def mysqlshow() {
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false"
      val username = "root"
      val password = "123456"

      var connection: Connection = null

      try {

        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)

        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("select * from kafkatest")
        while (resultSet.next()) {
          val name = resultSet.getString("text")
          //val password = resultSet.getString("body")
          println(name)
        }
      } catch {
        case e => e.printStackTrace
        //case _: Throwable => println("ERROR")
      }
      connection.close()
    }

    def mysqladd(kafkatext:String): Unit ={
      val driver = "com.mysql.jdbc.Driver"
      val url = "jdbc:mysql://localhost:3306/test"
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


    mysqlshow()
  }

}
