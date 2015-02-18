package scala.de.codecentric.wittig.robot.file

object map {
val myMap = Map("a" -> "apple", "b" -> "banana")  //> myMap  : scala.collection.immutable.Map[String,String] = Map(a -> apple, b -
                                                  //| > banana)
myMap map {
	case (k, "apple") => (k, "apfel")
	case pair => pair
}                                                 //> res0: scala.collection.immutable.Map[String,String] = Map(a -> apfel, b -> b
                                                  //| anana)

val A = (3,4)                                     //> A  : (Int, Int) = (3,4)

}