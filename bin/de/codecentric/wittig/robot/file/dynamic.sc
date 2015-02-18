package scala.de.codecentric.wittig.robot.file

object dynamic {
import scala.language.dynamics

	class Hallo extends Dynamic{
		val map = scala.collection.mutable.Map.empty[String, Any]
		def selectDynamic(name:String) = {
			map.get(name).getOrElse(sys.error("method unknown"))
		}
		
		def updateDynamic(name:String)(obj:Any) = {
			map += name -> obj
		}
		
		def applyDynamic(name:String)(args: Any*) = {
			name match {
				case "sum" => args.asInstanceOf[Seq[Int]].sum
				case "sumMap" => map.map(x=> x._2).filter(x => x.isInstanceOf[Int]).map(_.asInstanceOf[Int]).sum
				case "concat" => args.asInstanceOf[Seq[String]].reduceLeft((a,b) => a+ " "+b)
				case "print" => args.foreach { println}
			}
		}
	}
	
	val d = new Hallo                         //> d  : scala.de.codecentric.wittig.robot.file.dynamic.Hallo = scala.de.codecen
                                                  //| tric.wittig.robot.file.dynamic$Hallo@65601e00
	d.wer = "lkj"                             //> res0: scala.collection.mutable.Map[String,Any] = Map(wer -> lkj)
	d.warum   = 23                            //> res1: scala.collection.mutable.Map[String,Any] = Map(warum -> 23, wer -> lkj
                                                  //| )
         d.gunther = 5                            //> res2: scala.collection.mutable.Map[String,Any] = Map(warum -> 23, wer -> lkj
                                                  //| , gunther -> 5)
	d.warum                                   //> res3: Any = 23
	d.sum(1,2,3)                              //> res4: Any = 6
	d.concat("Hallo", "Welt")                 //> res5: Any = Hallo Welt
	d.print(1,"zwei", new Hallo)              //> 1
                                                  //| zwei
                                                  //| scala.de.codecentric.wittig.robot.file.dynamic$Hallo@3ec7d45e
                                                  //| res6: Any = ()
	d.sumMap()                                //> res7: Any = 28

	
}