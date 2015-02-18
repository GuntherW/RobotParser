package de.codecentric.wittig.robot.file
import de.codecentric.wittig.robot.file._
object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(128); 

val r = "(er)(w)".r.unanchored;System.out.println("""r  : scala.util.matching.UnanchoredRegex = """ + $show(r ));$skip(15); 
val s = "werw";System.out.println("""s  : String = """ + $show(s ));$skip(17); val res$0 = 
r.findFirstIn(s);System.out.println("""res0: Option[String] = """ + $show(res$0));$skip(58); val res$1 = 

s match {
	case r(s,t) => s + "- " +t
	case _ => "nix"
};System.out.println("""res1: String = """ + $show(res$1));$skip(26); 

val p = "/home/gunther/";System.out.println("""p  : String = """ + $show(p ));$skip(38); 
val path = "/home/gunther/test.robot";System.out.println("""path  : String = """ + $show(path ));$skip(20); val res$2 = 
path.replace(p, "");System.out.println("""res2: String = """ + $show(res$2));$skip(32); 

val regex = """(.*)\.(.*)""".r;System.out.println("""regex  : scala.util.matching.Regex = """ + $show(regex ));$skip(70); val res$3 = 
"wer--wer" match {
	case regex(a,b) => a+ "-- "+b
	case a => "ll"+a
};System.out.println("""res3: String = """ + $show(res$3))}

}
