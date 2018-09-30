object kafkaproducer {
  def main(args: Array[String]): Unit = {
    import java.util.Properties
    import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
    import org.apache.kafka.common.serialization.StringSerializer

    val BROKER_LIST = "192.168.2.226:9092"
    val TOPIC = "world"

    val props = new Properties()
    props.put("bootstrap.servers", BROKER_LIST)
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    val producer = new KafkaProducer[String, String](props)


    producer.send(new ProducerRecord(TOPIC, "key-" + "99999", "msg-" + "thie is kingkong come on and count !!"))


//    for(i <- 0 to 100){
//      val a = producer.send(new ProducerRecord(TOPIC, "key-" + "99999", "msg-" + "thie is kingkong come on and count !!"))
//      println(a.toString)
//    }

   //println(a.toString)

  }
}
