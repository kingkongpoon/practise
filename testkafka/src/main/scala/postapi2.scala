import jodd.http.HttpRequest


/**
  * Description:
  * Author: tangli
  * Version: 1.0
  * Create Date Time: 2018/8/7 0007 下午 4:31.
  * Update Date Time:
  * see
  */

object postapi2 {
  def main(args : Array[String]):Unit ={
    val response = HttpRequest
      .post("http://192.168.15.174:10086/model/schema/create")
      .form("model_id", "10194",
            "data","asdf")
      .send() //这里与get的参数传递方式不同

//    val a = response.sendTo()

//    println(a)


  }

}
