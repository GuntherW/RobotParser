package scala.de.codecentric.wittig.robot.file

object map {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(109); 
val myMap = Map("a" -> "apple", "b" -> "banana");System.out.println("""myMap  : scala.collection.immutable.Map[String,String] = """ + $show(myMap ));$skip(68); val res$0 = 
myMap map {
	case (k, "apple") => (k, "apfel")
	case pair => pair
};System.out.println("""res0: scala.collection.immutable.Map[String,String] = """ + $show(res$0));$skip(15); 

val A = (3,4);System.out.println("""A  : (Int, Int) = """ + $show(A ))}

}
