package de.codecentric.wittig.robot.file
import de.codecentric.wittig.robot.file._
import java.io._
object test {
	val l = Some(List(1,2,3,4))               //> l  : Some[List[Int]] = Some(List(1, 2, 3, 4))
	val n = None                              //> n  : None.type = None
	n.getOrElse(List.empty[Int]).map(_+2)     //> res0: List[Int] = List()

	val path = "../robot/"                    //> path  : String = ../robot/
	val f = new File(path)                    //> f  : java.io.File = ../robot
	f.getCanonicalFile                        //> res1: java.io.File = /home/robot
	f.getPath                                 //> res2: String = ../robot
	f.getAbsolutePath                         //> res3: String = /home/gunther/../robot
}