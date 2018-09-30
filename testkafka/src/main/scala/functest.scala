object functest {

  def func(s1:String*): String ={
    var res = ""
    for(i <- s1){
      res += i
    }
    println(res)
    res
  }

  def runFunc(func:(Seq[String])=>String,s1:String*):String={
      func(s1)
  }

  def main(args : Array[String]):Unit={
    //直接调用一个函数，接受可变参数
    func("A1","B1","C1","D1")
    //一个能接收函数作为参数的函数，并接收可变参数
    runFunc(func,"A2","B2","C2","D2")
  }
}
