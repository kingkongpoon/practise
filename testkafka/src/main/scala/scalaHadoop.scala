import org.apache.hadoop.fs.{FileStatus, FileSystem, FileUtil, Path}
import java.net.URI
import org.apache.hadoop.conf.Configuration


object scalaHadoop {
  def main(args:Array[String]):Unit={
    System.setProperty("hadoop.home.dir", "D:\\download\\分布式工具\\hadoop\\hadoop-common-2.2.0-bin")
    val conf = new Configuration();// 加載配制文件
    val uri = new URI("hdfs://namenode:9000/"); // 要连接的资源位置
    val fs = FileSystem.get(uri,conf,"root")
    val files = fs.listStatus(new Path("/BItest/Spark/out/"))
    val a = files.filter( _.getPath.getName.contains("part"))
    println("/BItest/Spark/out/" + a(0).getPath.getName)
//    for(i <- a){
//      println(i.getPath.getName)
//    }
    val targetName = new Path("/bi/model/777.json")
    val copyfile = new Path("/bi/model/888666") // hadoop文件对象, 类似java的File类

//    val isResult = fs.rename()
    FileUtil.copy(fs, targetName, fs, copyfile, false, conf)
//    println(isResult)
  }
}
