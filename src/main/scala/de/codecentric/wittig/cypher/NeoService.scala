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

/**
 * @author Gunther Wittig
 */
object NeoService extends App {

  import org.anormcypher._

  // Setup the Rest Client
  implicit val connection = Neo4jREST()
  //
  val path = "/home/gunther/play/robot/"
  val file = new File(path)
  val seq = FileUtil.recursiveListFiles(file).map(fn => (fn.getPath, RobotParser(fn))).map(s => (s._1, s._2.get))
  val seqInsert = seq.flatMap(g => (g._2.keywords.map { x => x.fileName = Some(g._1.replace(path, "")); x.baum = Some(g._2); x }))

  deleteAll
  insertKeywords
  insertRelations

  def insertRelations = {
    println("Anzahl Wörter: " + seqInsert.size)

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

    cypher.map(x => x.query + " - " + x.params).toList.foreach { println }
    println("Anzahl Relationen: " + cypher.size)
    val b = cypher.map(_.execute()).toList
    b.foreach(println)
    println("fertig")
  }

  def insertKeywords = {

    val end = seqInsert.sortBy(_.wort).zipWithIndex.map(cypher)
    val readableString = end.toList.mkString("\n") + ";"
    println("insert: " + Cypher(readableString).execute())
  }

  def deleteAll = {
    val s = """MATCH (n)
              | OPTIONAL MATCH (n)-[r]-()
              | DELETE n,r;
              | """.stripMargin

    val deleted = Cypher(s).execute
    println(s"deleted: $deleted")
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

    val allCountries = Cypher("start n=node(*) where n.type = 'Country' return n.population as population, n.code as code, n.name as name")

    // Transform the resulting Stream[CypherRow] to a List[(String,String)]
    val countries = allCountries.apply().map(row =>
      (row[Int]("population"), row[String]("code"), row[String]("name"))
    ).toList
    countries.foreach(println)
  }
}
