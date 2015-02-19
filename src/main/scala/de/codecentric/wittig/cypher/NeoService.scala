package de.codecentric.wittig.cypher

import java.io.File
import de.codecentric.wittig.robot.file.FileUtil
import de.codecentric.wittig.robotparser.RobotParser
import de.codecentric.wittig.robotparser.Schluesselwort
import org.apache.commons.lang3.StringEscapeUtils
import de.codecentric.wittig.robotparser.Aufruf
import de.codecentric.wittig.robotparser.Zuweisung
import de.codecentric.wittig.robotparser.Zeile
import org.anormcypher.Cypher
import org.anormcypher.Neo4jREST
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.scalalogging.Logger
import org.slf4j._

/**
 * @author Gunther Wittig
 */
case class NeoService(path: String) extends LazyLogging {

  import org.anormcypher._

  // Setup the Rest Client
  implicit val connection = Neo4jREST()
  //
  //  val path = "/home/gunther/play/robot/"
  val file = new File(path)
  val seq = FileUtil.recursiveListFiles(file).map(fn => (fn.getPath, RobotParser(fn))).map(s => (s._1, s._2.get))
  val seqInsert = seq.flatMap(g => (g._2.keywords.map { x => x.fileName = Some(g._1.replace(path, "")); x.baum = Some(g._2); x }))

  //  deleteAll
  //  insertKeywords
  //  insertRelations

  def insertRelations = {
    println("Anzahl Wörter: " + seqInsert.size)
    logger.debug("Anzahl Wörter: " + seqInsert.size)

    val wortZuZeilen = for {
      sw <- seqInsert
      z <- sw.zeilen
    } yield (sw, z)

    val wortZuWort = wortZuZeilen.map {
      case (s, b: Aufruf)    => s -> b
      case (s, b: Zuweisung) => s -> b.aufruf
    }

    val regexString = """(.*)\.(.*)"""
    val regex = regexString.r
    def whereKlauselAufruf(wort: Schluesselwort, aufruf: Aufruf) = {
      aufruf.schluesselwort match {
        case regex(prefix, s) => {
          s"""
        AND b.name = '${s}'
        AND b.filename = '${prefix}.robot'"""
        }
        case _ => {
          s"""
        AND b.name = '${aufruf.schluesselwort}'
        AND b.filename in [ '${wort.fileName.get}',  ${wort.importe.mkString(", ")}]"""
        }
      }
    }
    val cypher = wortZuWort.map {
      case (wort, aufruf) => Cypher(s"""
        MATCH (a:Schluesselwort), (b:Schluesselwort)
        WHERE a.name = '${wort.wort}'
        AND a.filename = '${wort.fileName.get}'
        ${whereKlauselAufruf(wort, aufruf)}
        CREATE (a) -[r:RUFTAUF {parameter: '${aufruf.parameter.mkString(", ")}'}]->(b)
    		""")
    }

    //    cypher.map(x => x.query + " - " + x.params).toList.foreach { println }
    val b = cypher.map(_.execute()).toList
    println("Relationen importiert: " + cypher.size)
  }

  def insertKeywords = {

    val end = seqInsert.sortBy(_.wort).zipWithIndex.map(cypher)
    val readableString = end.toList.mkString("\n") + ";"
    Cypher(readableString).execute()
    println("Schlüsselwörter aufgenommen: " + end.size)
  }

  def deleteAll = {
    val s = """MATCH (n)
              | OPTIONAL MATCH (n)-[r]-()
              | DELETE n,r;
              | """.stripMargin

    val deleted = Cypher(s).execute()
    println(s"all entities deleted: $deleted")
  }

  def getRelationen(name: String) = {
    import org.anormcypher.CypherParser._
    val s = s"""START n = node(*)
      MATCH p =  n-[:RUFTAUF]->m
      where m.name = '$name'
      RETURN n.name, n.filename,m.name, m.filename
      """
    val aa = Cypher(s)
    logger.debug(aa.query)
    Cypher(s).as(str("n.name") ~ str("n.filename") ~ str("m.name") ~ str("m.filename") map (flatten) *)
    //    aa.apply().map { row =>
    //      (row[String]("n.name"), row[String]("m.name"))
    //    }.toList
  }

  def cypher(tuple: (Schluesselwort, Int)) = {
    val (sw, i) = tuple
    s"""create  (n$i:Schluesselwort { name: "${sw.wort}" ${cypherArguments(sw.arguments)} ${cypherReturns(sw.returnValue)}, filename: "${sw.fileName.get}", documentation: "${StringEscapeUtils.escapeJava(sw.documentation.getOrElse(""))}"})""".stripMargin
  }

  def cypherArguments(arguments: Option[List[String]]) = {
    arguments match {
      case Some(args) => " , argumente: \"" + args.mkString(",") + "\""
      case None       => ""
    }
  }
  def cypherReturns(returns: Option[List[String]]) = {
    returns match {
      case Some(args) => " , return: \"" + args.mkString(",") + "\""
      case None       => ""
    }
  }

  /**
   * Zur Anschauung
   */
  def test = {

    val s = s"""START n = node(*)
      MATCH p =  n-[:RUFTAUF]->m
      where n.name = 'Gefahr setzen'
      RETURN n.name,m.name
      """
    val all = Cypher(s)
    val l = all.apply().map { row =>
      (row[String]("n.name"), row[String]("m.name"))
    }.toList
    //    val allCountries = Cypher("start n=node(*) where n.type = 'Country' return n.population as population, n.code as code, n.name as name")
    //
    //    // Transform the resulting Stream[CypherRow] to a List[(String,String)]
    //    val countries = allCountries.apply().map(row =>
    //      (row[Int]("population"), row[String]("code"), row[String]("name"))
    //    ).toList
    //    countries.foreach(println)
    l
  }
}
