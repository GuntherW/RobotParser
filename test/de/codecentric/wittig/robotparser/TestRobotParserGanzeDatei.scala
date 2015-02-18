package de.codecentric.wittig.robotparser

import org.scalatest.FunSuite
import org.scalatest.Ignore
import java.io.File

/**
 * @author Gunther Wittig
 */
class TestRobotParserGanzeDatei extends FunSuite {
  test("Technisch.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Technisch.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val technischRobot = RobotParser(string)
    val r = technischRobot.get
    assert(r.settings(0).schluessel == "Documentation")
    assert(r.settings(0).wert == "HuGO Schlüsselwörter technisch")
    assert(r.settings.size == 6)

    assert(r.variables == None)
    assert(r.testcases == None)
    assert(r.keywords.size == 34)
  }

  test("Technisch.robot File") {
    val string = new File("/home/gunther/play/robot/Technisch.robot")
    val technischRobot = RobotParser(string)
    val r = technischRobot.get
    assert(r.settings(0).schluessel == "Documentation")
    assert(r.settings(0).wert == "HuGO Schlüsselwörter technisch")
    assert(r.settings.size == 6)

    assert(r.variables == None)
    assert(r.testcases == None)
    assert(r.keywords.size == 34)
  }

  test("Sachversicherung.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Sachversicherung.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(0).schluessel == "Library")
    assert(r.settings(0).wert == "Selenium2Library")
    assert(r.settings.size == 7)

    assert(r.variables.get(0).variable == "${UrlUmgebung}")
    assert(r.variables.get(0).wert == "http://imt2.provinzial.com/provinzial/layout_elemente/pronet_oberflaeche/layout_default/html/startWeiterleitungProvinzialNoSingleSignOn.htm")
    assert(r.keywords.size == 17)
  }

  test("Gasthof_Fortuna.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Gasthof_Fortuna.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(0).schluessel == "Library")
    assert(r.settings(0).wert == "Selenium2Library")
    assert(r.settings.size == 4)

    assert(r.variables.get(1).variable == "${Benutzer}")
    assert(r.variables.get(1).wert == "g888793")
    assert(r.keywords.size == 15)
  }

  test("Tanzcafe_Kunth.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Tanzcafe_Kunth.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(0).schluessel == "Library")
    assert(r.settings(0).wert == "Selenium2Library")
    assert(r.settings.size == 1)

    assert(r.variables.get(1).variable == "${Benutzer}")
    assert(r.variables.get(1).wert == "g888796")
    assert(r.keywords.size == 19)
  }

  test("Fachlich.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Fachlich.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(1).schluessel == "Library")
    assert(r.settings(1).wert == "Collections")
    assert(r.settings.size == 5)

    assert(r.keywords.size == 47)
  }

  test("Mehrbereichstarifierung.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Mehrbereichstarifierung.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(1).schluessel == "Library")
    assert(r.settings(1).wert == "Collections")
    assert(r.settings.size == 6)

    assert(r.keywords.size == 12)
  }

  test("Test.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/Test.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(1).schluessel == "Library")
    assert(r.settings(1).wert == "Collections")
    assert(r.settings.size == 2)

  }

  test("TestdatenHandhabung.robot") {
    import scala.io._
    val string = Source.fromFile("/home/gunther/play/robot/TestdatenHandhabung.robot").getLines().mkString("\n")
    assert(string.length() > 0)
    val robot = RobotParser(string)
    val r = robot.get
    assert(r.settings(1).schluessel == "Library")
    assert(r.settings(1).wert == "Collections")
    assert(r.settings.size == 7)
    assert(r.keywords.size == 42)

  }
}
