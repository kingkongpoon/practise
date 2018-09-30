import java.text.SimpleDateFormat
import java.util.Calendar
import scala.collection.mutable.ArrayBuffer

object dateformat {
  def dateReckon(date:String,diff:Int):String={
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val dt = dateFormat.parse(date)
    val rightNow = Calendar.getInstance()
    rightNow.setTime(dt)
    rightNow.add(Calendar.MONTH, -diff)
    dateFormat.format(rightNow.getTime)
  }

  def main(args:Array[String]):Unit={
    val redate = dateReckon("2018-06-30",1)
    println(redate)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val dt = dateFormat.parse("2018-09-17")
    println(dt.getDate)
  }
}
