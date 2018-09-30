import scala.reflect.runtime.{universe => ru}
import scala.collection.mutable.ArrayBuffer
/**
  * Description:
  * Author: tangli
  * Version: 1.0
  * Create Date Time: 2018/8/2 0002 下午 4:11.
  * Update Date Time:
  * see
  */

object B {
  def main(args: Array[String]): Unit = {
    val se = Seq(classOf[String],classOf[String])
    val sa = List[String]("321","999")
    val classA = Class.forName("AAAA")
    val mm = classA.getDeclaredMethod(s"t5",classOf[String]).invoke(new AAAA("a"),"123")
    val mm1 = classA.getDeclaredMethod(s"t6",se:_*).invoke(new AAAA("a"),sa:_*)


    println(mm1)

    def a(a:String*):String={
      var b =""
      for(i <- a){
        b += i
      }
      b
    }

    println(a("a","c","d","e"))
//    val ma = classA.getMethods
//    val m = ma(0)

//    println(mm)
//    for(i <- ma){println(i.getName)}

//    val method = classA.getDeclaredMethod("fun1",classOf[String])
//    val z = method.invoke(classA.newInstance(),"5")// 如果是Int的话,要用new Integer(5)
//    println(z)


//    val classMirror = ru.runtimeMirror(getClass.getClassLoader)
//    val classTest = classMirror.reflect(new AAAA)
//    val methods = ru.typeOf[AAAA]
//    val method = methods.decl(ru.TermName("t")).asMethod
//    val result = classTest.reflectMethod(method)()
//    println(result)
  }

  /*
如果类A如下定义:
class A(a:String)
则需要在反射时候添加构造器
val cons = classA.getConstructors
val newInst = cons(0).newInstance("你好")
method.invoke(newInst, "哈哈")
*/

}
