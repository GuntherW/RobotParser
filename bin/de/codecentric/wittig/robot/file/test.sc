package de.codecentric.wittig.robot.file
import de.codecentric.wittig.robot.file._
object test {

val r = "(er)(w)".r.unanchored                    //> r  : scala.util.matching.UnanchoredRegex = (er)(w)
val s = "werw"                                    //> s  : String = werw
r.findFirstIn(s)                                  //> res0: Option[String] = Some(erw)

s match {
	case r(s,t) => s + "- " +t
	case _ => "nix"
}                                                 //> res1: String = er- w

val p = "/home/gunther/"                          //> p  : String = /home/gunther/
val path = "/home/gunther/test.robot"             //> path  : String = /home/gunther/test.robot
path.replace(p, "")                               //> res2: String = test.robot

val regex = """(.*)\.(.*)""".r                    //> regex  : scala.util.matching.Regex = (.*)\.(.*)
"wer--wer" match {
	case regex(a,b) => a+ "-- "+b
	case a => "ll"+a
}                                                 //> res3: String = llwer--wer

}