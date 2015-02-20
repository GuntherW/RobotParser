package de.codecentric.wittig.robot.file
import de.codecentric.wittig.robot.file._
import java.io._
object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(142); 
	val l = Some(List(1,2,3,4));System.out.println("""l  : Some[List[Int]] = """ + $show(l ));$skip(14); 
	val n = None;System.out.println("""n  : None.type = """ + $show(n ));$skip(39); val res$0 = 
	n.getOrElse(List.empty[Int]).map(_+2);System.out.println("""res0: List[Int] = """ + $show(res$0));$skip(25); 

	val path = "../robot/";System.out.println("""path  : String = """ + $show(path ));$skip(24); 
	val f = new File(path);System.out.println("""f  : java.io.File = """ + $show(f ));$skip(20); val res$1 = 
	f.getCanonicalFile;System.out.println("""res1: java.io.File = """ + $show(res$1));$skip(11); val res$2 = 
	f.getPath;System.out.println("""res2: String = """ + $show(res$2));$skip(19); val res$3 = 
	f.getAbsolutePath;System.out.println("""res3: String = """ + $show(res$3))}
}
