package de.codecentric.wittig.robotparser

import scala.util.parsing.combinator._
import java.text.Normalizer
import java.io.File
import scala.io.Source
import java.net.URL

/**
 * Parser for robot files
 * @author Gunther Wittig
 */
object RobotParser extends RegexParsers {

  override def skipWhitespace = false

  def eol = """\s*\r?\n""".r
  def ident = """(\t*[ ]{2,}\t*)|(\s*\t+\s*)""".r

  def setVariable = ident ~> """(?i)Set Variable""".r ~> ident ~> """[^\r\n\*]*""".r <~ eol ^^ { case a => Aufruf("Set Variable", List(a)) }
  def setVariableIf = ident ~> """(?i)Set Variable if""".r ~> """[^\r\n\*]*""".r ~ rep(eol ~> setVariableZeile) ^^ { case a ~ l => Aufruf(a, l) }
  def setVariableZeile = ident ~> "..." ~> ident ~> """[^\r\n\*]*""".r

  def waitUntilPageContains = """(?i)Wait until Page contains""".r ~ ident ~ variable
  def waitUntilElementIsVisible = """(?i)Wait until element is visible""".r ~ ident ~ variable

  def waitUntilKeywordSucceeds = """(?i)wait until keyword succeeds""".r ~> ident ~> schluesselwort ~ ident ~ schluesselwort ^^ { case a ~ b ~ c => (a, c) }

  def runKeywordIf = """(?i)Run Keyword if""".r ~> ident ~> """\S+( \S+)*""".r
  def runKeywordUnless = """(?i)Run Keyword unless""".r ~> ident ~> """\S+( \S+)*""".r
  def runKeywordAndReturn = """(?i)Run Keyword and return""".r
  def runKeywordAndReturnIf = """(?i)Run Keyword and return if""".r ~> ident ~> """\S+( \S+)*""".r
  def runKeywordAndIgnoreError = """(?i)Run Keyword and ignore Error""".r
  def preAufruf = runKeywordIf | runKeywordUnless | runKeywordAndReturnIf | runKeywordAndReturn | runKeywordAndIgnoreError | waitUntilKeywordSucceeds

  def documentation = ident ~> """[Documentation]""" ~> ident ~> """[^\r\n\*]*""".r
  def documentationLang = documentation ~ rep(eol ~> ident ~> "..." ~> """[^\r\n\*]*""".r) ^^ { case doc ~ docList => docList.foldLeft(doc)((l, r) => l + " " + r.trim) }

  def arguments = rep(eol) ~> ident ~> """[Arguments]""" ~> rep1(ident ~> variable)
  def argumentsLang = arguments ~ rep(eol ~> ident ~> "..." ~> rep1(ident ~> variable)) ^^ { case doc ~ docList => (doc :: docList).flatten }

  def returnValue = ident ~> """[Return]""" ~> rep1(ident ~> variable)

  def forSchleifenKopf = ident ~> ":FOR" ~ """[^\r\n]+""".r <~ eol
  def forSchleifenKorpus = ident ~> """\""" ~> zeile
  def forSchleife = forSchleifenKopf ~> rep1(forSchleifenKorpus)

  val wort = """0-9a-zA-ZäÄüÜöÖß\._:\-=\@\#\\\'"\{\}$<\(),/\[\];\*\+\!"""
  val wort2 = s"""[$wort]+( [$wort]+)*"""
  def schluesselwort = ("""(?![\[\*])""" + wort2).r ^^ { case a => a }
  def schluesselwortDefinition = schluesselwort ~ opt(argumentsLang) ^^ {
    case a ~ Some(b) => SchluesselwortDefinition(a, b)
    case a ~ None    => SchluesselwortDefinition(a, Nil)
  }

  def variable = """(\$|\@)\{.+?\}""".r ^^ { case a => a }

  def parameter = ident ~> (schluesselwort | variable) ^^ { case a => a }

  def aufrufZu: Parser[Aufruf] = setVariable | setVariableIf | aufruf
  def aufruf = opt(ident ~ preAufruf) ~> ident ~> (schluesselwort ~ rep(parameter)) ^^ {
    case a ~ b => Aufruf(a.trim, b.map(_.trim()))
  }
  def zuweisung = (rep1(ident ~> variable) ~ opt("""\s*=""".r) ~ aufrufZu) ^^ { case a ~ b ~ c => Zuweisung(a.map(_.trim), c) }
  def variablenzuweisung = variable ~ opt("=") ~ parameter ^^ { case a ~ b ~ c => VarZuweisung(a.trim, c) }

  def zeile: Parser[List[Zeile]] = (forSchleife | zuweisung | aufruf) <~ rep(eol) ^^ {
    case a: List[List[Zeile]] => a.flatten
    case b: Zeile             => List(b)
  }

  def settingsCaption = rep(eol) ~> """*** Settings ***""" <~ rep1(eol)
  def settings = settingsCaption ~> rep(schluesselwort ~ parameter <~ rep1(eol)) ^^ { case li => li.map(x => Settings(x._1.trim(), x._2.trim())) }

  def variablesCaption = rep(eol) ~> """*** Variables ***""" <~ rep1(eol)
  def variables = variablesCaption ~> rep(variablenzuweisung <~ rep1(eol))

  def testcasesCaption = rep(eol) ~> """*** Test Cases ***"""
  def testcase = testcasesCaption ~> rep(rep(eol) ~> keyword)

  def keywordsCaption = rep(eol) ~> """*** Keywords ***""" <~ rep1(eol)
  def keywords = keywordsCaption ~> rep(rep(eol) ~> keyword)
  def keyword = schluesselwort ~ opt(argumentsLang) ~ eol ~ opt(documentationLang <~ eol) ~ rep(zeile) ~ opt(returnValue <~ eol) ^^ { case s ~ args ~ eol1 ~ doc ~ l ~ r => Schluesselwort(s, args, l.flatten, doc, r) }

  def robot = settings ~ opt(variables) ~ opt(testcase) ~ keywords ^^ { case s ~ v ~ t ~ k => RobotTree(s, v, t, k) }

  def apply(robotAsString: String): ParseResult[RobotTree] = {
    val removedComments = robotAsString.replaceAll("""(\s*)(?<!\\)#.*(\r?\n)""", "$2") //.replaceAll("""(?m)^\s+$""", "")
    val normalized = Normalizer.normalize(removedComments, Normalizer.Form.NFC)
    parse(robot, normalized)
  }

  def apply(robotAsFile: File): ParseResult[RobotTree] = {
    this(robotAsFile.toURI.toURL)
  }

  def apply(url: URL): ParseResult[RobotTree] = {
    val s = Source.fromURL(url).getLines().mkString("\n")
    this(s)
  }

}

case class RobotTree(settings: List[Settings], variables: Option[List[VarZuweisung]], testcases: Option[List[Schluesselwort]], keywords: List[Schluesselwort])
case class Settings(schluessel: String, wert: String)
case class Testcase(name: String, schritte: Zeile)
trait Zeile
case class Aufruf(schluesselwort: String, parameter: List[String]) extends Zeile
case class Zuweisung(variable: List[String], aufruf: Aufruf) extends Zeile
case class VarZuweisung(variable: String, wert: String) extends Zeile
case class Schluesselwort(wort: String, arguments: Option[List[String]], zeilen: List[Zeile], documentation: Option[String] = None, returnValue: Option[List[String]] = None, var fileName: Option[String] = None, var baum: Option[RobotTree] = None) {
  def importe = {
    baum.get.settings.filter(_.schluessel == "Resource").map("'" + _.wert + "'")
  }
}
case class SchluesselwortDefinition(name: String, parameter: List[String])
