package de.codecentric.wittig.robot.file
import de.codecentric.wittig.robot.file._
object test {
val s1 = "hallo"                                  //> s1  : String = hallo
val s2 = "welt"                                   //> s2  : String = welt
val s3 = "gunther"                                //> s3  : String = gunther
val s = f"$s1%s$s2%20s$s3"                        //> s  : String = hallo                weltgunther

}