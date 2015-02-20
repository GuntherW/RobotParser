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
import de.codecentric.wittig.robotparser.Schluesselwort

/**
 * @author Gunther Wittig
 */
case class NeoService(path: String) extends LazyLogging {

  import org.anormcypher._

  // Setup the Rest Client
  implicit val connection = Neo4jREST()

  val regexString = """(.*)\.(.*)"""
  val regex = regexString.r

  val file = new File(path)
  val files = FileUtil.recursiveListFiles(file)
  val seq = files.map(fn => (fn.getPath, RobotParser(fn)))
  val seqInsert = seq.flatMap(g => (g._2.get.keywords.map { x => x.fileName = Some(g._1.replace(path + "/", "")); x.baum = Some(g._2.get); x }))
  val seqTestInsert = seq.flatMap { g =>
    {
      g._2.get.testcases.getOrElse(List.empty[Schluesselwort]).map {
        x => x.fileName = Some(g._1.replace(path + "/", "")); x.baum = Some(g._2.get); x
      }
    }
  }
  val mapSchluesselwoerter = Map("Schluesselwort" -> seqInsert, "Testcase" -> seqTestInsert)

  def insertRelations(art: String) = {
    println(s"Anzahl $art: " + mapSchluesselwoerter(art).size)

    val wortZuZeilen = for {
      sw <- mapSchluesselwoerter(art)
      z <- sw.zeilen
    } yield (sw, z)

    val wortZuAufruf = wortZuZeilen.map {
      case (s, b: Aufruf)    => s -> b
      case (s, b: Zuweisung) => s -> b.aufruf
    }

    val cypher = wortZuAufruf.map {
      case (wort, aufruf) => Cypher(s"""
        MATCH (a:$art), (b:Schluesselwort)
        WHERE a.name = '${wort.wort}'
        AND a.filename = '${wort.fileName.get}'
        ${whereKlauselAufruf(wort, aufruf)}
        CREATE (a) -[r:RUFTAUF {parameter: '${aufruf.parameter.mkString(", ")}'}]->(b)
    		""")
    }

    val b = cypher.map(_.execute()).toList
    println(s"Relationen für $art importiert: " + cypher.size)
  }
  def whereKlauselAufruf(wort: Schluesselwort, aufruf: Aufruf) = {
    aufruf.schluesselwort match {
      case regex(prefix, s) => {
        s"""
        AND b.name = '${s}'
        AND b.filename = '${prefix}.robot'"""
      }
      case _ => {
        val fi = "'" + wort.fileName.get + "'" :: wort.importe
        s"""
        AND b.name = '${aufruf.schluesselwort}'
        AND b.filename in [ ${fi.mkString(", ")}]"""
      }
    }
  }

  def insertKeywords = {

    val end = seqInsert.sortBy(_.wort).zipWithIndex.map(createCypherSchluesselwort)
    val readableString = end.toList.mkString("\n") + ";"
    Cypher(readableString).execute()
    println("Schlüsselwörter aufgenommen: " + end.size)
  }
  def insertTestKeywords = {

    val end = seqTestInsert.sortBy(_.wort).zipWithIndex.map(createCypherTestSchluesselwort)
    val readableString = end.toList.mkString("\n") + ";"
    val erfolg = Cypher(readableString).execute()
    println("TestSchlüsselwörter aufgenommen: " + end.size)
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
    val query = Cypher(s)
    logger.debug(query.query)
    query.as(str("n.name") ~ str("n.filename") ~ str("m.name") ~ str("m.filename") map (flatten) *)
  }

  def createCypherSchluesselwort(tuple: (Schluesselwort, Int)) = {
    val (sw, i) = tuple
    s"""create  (n$i:Schluesselwort { name: "${sw.wort}" ${createCypherArguments(sw.arguments)} ${createCypherReturns(sw.returnValue)}, filename: "${sw.fileName.get}", documentation: "${StringEscapeUtils.escapeJava(sw.documentation.getOrElse(""))}"})""".stripMargin
  }
  def createCypherTestSchluesselwort(tuple: (Schluesselwort, Int)) = {
    val (sw, i) = tuple
    s"""create  (n$i:Testcase { name: "${sw.wort}" ${createCypherArguments(sw.arguments)} ${createCypherReturns(sw.returnValue)}, filename: "${sw.fileName.get}", documentation: "${StringEscapeUtils.escapeJava(sw.documentation.getOrElse(""))}"})""".stripMargin
  }

  def createCypherArguments(arguments: Option[List[String]]) = {
    arguments match {
      case Some(args) => " , argumente: \"" + args.mkString(",") + "\""
      case None       => ""
    }
  }
  def createCypherReturns(returns: Option[List[String]]) = {
    returns match {
      case Some(args) => " , return: \"" + args.mkString(",") + "\""
      case None       => ""
    }
  }
}
