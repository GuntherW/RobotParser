package de.codecentric.wittig.scala.file
import de.codecentric.wittig.scala.file._
object test {



import scala.io._
import java.io.File;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(175); 
val dir = "/home/gunther/play/robot/";System.out.println("""dir  : String = """ + $show(dir ));$skip(78); val res$0 = 
FileUtil.recursiveListFiles(new File(dir)).foreach { x => println(x.getPath)};System.out.println("""res0: <error> = """ + $show(res$0));$skip(55); 
 val list = FileUtil.recursiveListFiles(new File(dir));System.out.println("""list  : <error> = """ + $show(list ));$skip(92); val res$1 = 
Source.fromFile("/home/gunther/play/robot/Gasthof_Fortuna.robot").getLines().mkString("\n");System.out.println("""res1: String = """ + $show(res$1));$skip(51); val res$2 = 
 list.foreach { x =>
  println(fileToString(x))
 };System.out.println("""res2: <error> = """ + $show(res$2));$skip(86); 
	def fileToString(file:File) = {
		Source.fromFile(file).getLines().mkString("\n")
	};System.out.println("""fileToString: (file: java.io.File)String""")}
}
