package de.codecentric.wittig.robotparser

import org.scalatest.FunSuite
import org.scalatest.Ignore
import java.io.File
import de.codecentric.wittig.robot.file.FileUtil

/**
 * @author Gunther Wittig
 */
class TestRobotParserAusgabe extends FunSuite {

  test("console") {
    val file = new File("/home/gunther/play/robot/")
    val seq = FileUtil.recursiveListFiles(file).map(RobotParser(_)).map { _.get }.flatMap { _.keywords }
    val end = seq.map { x =>
      cypher(x.wort, x.arguments)
    }
    end.foreach { println }
  }

  def cypher(keyword: String, arguments: Option[List[String]]) = {
    keyword + cypherArguments(arguments)
  }

  def cypherArguments(arguments: Option[List[String]]) = {
    arguments match {
      case Some(args) => "{" + args.map(x => s"""name: "$x"""") + "}"
      case None       => ""
    }
  }
}
