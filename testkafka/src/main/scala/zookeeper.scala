object zookeeper {
  import java.io.IOException;
  import java.util.List;

  import org.apache.zookeeper.KeeperException;
  import org.apache.zookeeper.WatchedEvent;
  import org.apache.zookeeper.Watcher;
  import org.apache.zookeeper.ZooKeeper;
  import scala.collection.mutable.ArrayBuffer

  def main(args: Array[String]): Unit = {
    val connectString = "192.168.2.226:2181"
    val sessionTimeout = 4000
    //val watcher = Watcher

    val zooKeeper = new ZooKeeper(connectString, sessionTimeout, null)

    val zl = zooKeeper.getChildren("/consumers", true)

    //val data = zooKeeper.getData("/cluster")
    println(zl)

    val data : Array[Byte] = zooKeeper.getData("/brokers/topics/test/partitions/0/state",true,null)

    println("===========")
    for(i <- data) print(i.toChar)
    println()
    println("===========")
  }

}

//ArrayBuffer(123, 34, 118, 101, 114, 115, 105, 111, 110, 34, 58, 34, 49, 34, 44, 34, 105, 100, 34, 58, 34, 78, 100, 101, 87, 65, 106, 51, 113, 83, 81, 87, 121, 56, 53, 69, 115, 120, 54, 121, 74, 109, 81, 34, 125)

//ArrayBuffer(123, 34, 118, 101, 114, 115, 105, 111, 110, 34, 58, 49, 44, 34, 115, 117, 98, 115, 99, 114, 105, 112, 116, 105, 111, 110, 34, 58, 123, 34, 116, 101, 115, 116, 34, 58, 49, 125, 44, 34, 112, 97, 116, 116, 101, 114, 110, 34, 58, 34, 119, 104, 105, 116, 101, 95, 108, 105, 115, 116, 34, 44, 34, 116, 105, 109, 101, 115, 116, 97, 109, 112, 34, 58, 34, 49, 53, 50, 56, 55, 55, 54, 52, 53, 48, 48, 55, 48, 34, 125)
