import java.io.File

import scala.util.Try
import scala.util.control.Breaks._


object scalatry {
  def main(args:Array[String]):Unit={
    val file = new File("D:\\F\\qingmu\\2018\\7month\\bicodev2art\\ETLjsonDemo")
    println(file.exists())
    val k = Map[String,String]("a"->"e")
    val l = List[Int](1,2,3,4,5,6,7,8,9,10)
//    k("e")
    for(i <- l){
      breakable{
        if(Try(k("a")).isSuccess) break;//当a等于3时跳出breakable块
        println(i+" =======================")
      }
    }
  }

}
