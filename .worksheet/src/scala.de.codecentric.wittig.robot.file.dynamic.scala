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
	};import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(735); 
	
	val d = new Hallo;System.out.println("""d  : scala.de.codecentric.wittig.robot.file.dynamic.Hallo = """ + $show(d ));$skip(15); val res$0 = 
	d.wer = "lkj";System.out.println("""res0: scala.collection.mutable.Map[String,Any] = """ + $show(res$0));$skip(16); val res$1 = 
	d.warum   = 23;System.out.println("""res1: scala.collection.mutable.Map[String,Any] = """ + $show(res$1));$skip(23); val res$2 = 
         d.gunther = 5;System.out.println("""res2: scala.collection.mutable.Map[String,Any] = """ + $show(res$2));$skip(9); val res$3 = 
	d.warum;System.out.println("""res3: Any = """ + $show(res$3));$skip(14); val res$4 = 
	d.sum(1,2,3);System.out.println("""res4: Any = """ + $show(res$4));$skip(27); val res$5 = 
	d.concat("Hallo", "Welt");System.out.println("""res5: Any = """ + $show(res$5));$skip(30); val res$6 = 
	d.print(1,"zwei", new Hallo);System.out.println("""res6: Any = """ + $show(res$6));$skip(12); val res$7 = 
	d.sumMap();System.out.println("""res7: Any = """ + $show(res$7))}

	
}
