import com.alibaba.fastjson.JSONObject
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header
import org.antlr.stringtemplate.language.ActionEvaluator.NameValuePair
import org.apache.http.HttpEntity
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import scala.collection.mutable.ArrayBuffer

/**
  * Description:
  * Author: tangli
  * Version: 1.0
  * Create Date Time: 2018/8/7 0007 下午 2:45.
  * Update Date Time:
  * see
  */

object postapi {
  def main(args : Array[String]):Unit = {
//    val jsonObj = new JSONObject
//
//    val datalist:List[Map[String,String]] = List[Map[String,String]](Map("name" ->"字段名","alias" -> "别名","type" ->"字段类型"),Map("name" ->"字段名2","alias" -> "别名2","type" ->"字段类型2"))
//
//
//    println(datalist)
//
//    jsonObj.put("model_id","10000")
//    jsonObj.put("data","asdf")
//
//    println(jsonObj)
//
    val url = "http://192.168.15.174:10086/model/schema/create"
    val client = HttpClients.createDefault()
//    val post: HttpPost = new HttpPost(url)
//    //设置提交参数为application/json
//    post.addHeader("Content-Type", "application/x-www-form-urlencoded")
//    post.setEntity(new StringEntity(jsonObj.toString))
//
//    val response: CloseableHttpResponse = client.execute(post)
//    //返回结果
//    val allHeaders = post.getAllHeaders
//    val entity: HttpEntity = response.getEntity
//    val string = EntityUtils.toString(entity, "UTF-8")
//
//    for(j <- allHeaders){println(j)}
////    println(allHeaders)
//    println(string)

import scala.collection.mutable.ArrayBuffer
import java.util

//    val httpClient = Utils.getHttpClient

    val post = new HttpPost(url)
    val list = new util.ArrayList[BasicNameValuePair]

    list.add(new BasicNameValuePair("model_id", "909090"))
    list.add(new BasicNameValuePair("data", """[{"name":"字段名","alias":"别名","type":"字段类型"}]"""))
//    list.add(new BasicNameValuePair("ID", "1"))
    val uefEntity = new UrlEncodedFormEntity(list, "UTF-8")
    post.setEntity(uefEntity)

    val httpResponse = client.execute(post)

    val text = httpResponse.getEntity

    println(text)











  }


}
