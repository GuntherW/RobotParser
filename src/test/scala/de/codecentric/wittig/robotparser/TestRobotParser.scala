package de.codecentric.wittig.robotparser

import org.scalatest.FunSuite
import org.scalatest.Ignore

import scala.util.matching.Regex

/**
 * @author Gunther Wittig
 */
class TestRobotParser extends FunSuite {

  test("schluesselwort") {

    val s = """\#1"""
    val p = RobotParser.parse(RobotParser.schluesselwort, s)
    assert(p.get == """\#1""")
  }

  test("regex") {
    val wort = """0-9a-zA-ZäÄüÜöÖß\._:\-=\@\#\\\'\{\}$"""
    val p = new Regex(s"""(?<!$$)[$wort]+( [$wort]+)*""")
    assert(p.findFirstIn("""'${Index}'=='Bitte auswählen'""").isDefined)
    assert(p.findFirstIn("""${Index}'=='Bitte auswählen'""").isDefined)
  }

  test("aufruf-2") {

    val s = """  Continue for loop if  '${Index}'=='Bitte auswählen'"""
    val p = RobotParser.parse(RobotParser.aufruf, s)
    assert(p.get.schluesselwort == """Continue for loop if""")
    assert(p.get.parameter(0) == """'${Index}'=='Bitte auswählen'""")
  }

  test("aufruf-3") {

    val s = """   double click element  xpath=//div[@class='treffer ng-isolate-scope' and text() = 'Gastwirtschaft']"""
    val p = RobotParser.parse(RobotParser.aufruf, s)
    assert(p.get.schluesselwort == """double click element""")
    assert(p.get.parameter(0) == """xpath=//div[@class='treffer ng-isolate-scope' and text() = 'Gastwirtschaft']""")
  }

  test("aufruf ein parameter") {
    val s = """  Set Suite Variable  ${Eingabefelder}"""
    assert(RobotParser.parse(RobotParser.aufruf, s).get.schluesselwort == "Set Suite Variable")
    assert(RobotParser.parse(RobotParser.aufruf, s).get.parameter(0) == "${Eingabefelder}")
  }

  test("aufruf mit RunKeyword If") {
    val s = """   Run Keyword if  '${MindestsicherungenVorhanden}' == 'Nein'  Set Suite Variable  ${Eingabefelder}  lkj lkj """
    val p = RobotParser.parse(RobotParser.aufruf, s).get
    assert(p.schluesselwort == "Set Suite Variable")
    assert(p.parameter(1) == "lkj lkj")
  }

  test("zuweisung") {
    val s = """  ${Eingabefelder} =  Eingabefelder lesen   Eingabefelder.xlsx  Neugeschäft
        """
    val p = RobotParser.parse(RobotParser.zuweisung, s).get
    assert(p.variable(0) == "${Eingabefelder}")
  }

  test("zuweisung zu zwei variablen") {
    val s = """   ${RisikoVorhanden}  ${RisikoVorhanden2}=  Status  Should contain  ${GefundeneRisiken}  ${Risiko}"""
    val p = RobotParser.parse(RobotParser.zuweisung, s).get
    assert(p.variable(0) == "${RisikoVorhanden}")
    assert(p.variable(1) == "${RisikoVorhanden2}")
    assert(p.aufruf.schluesselwort == "Status")
  }

  test("zuweisung mit RunKeyword and return") {
    val s = """   ${RisikoVorhanden}=  Run Keyword and return  Status  Should contain  ${GefundeneRisiken}  ${Risiko}"""
    val p = RobotParser.parse(RobotParser.zuweisung, s).get
    assert(p.variable(0) == "${RisikoVorhanden}")
    assert(p.aufruf.schluesselwort == "Status")
  }

  test("zeile") {
    val s = """  Set Suite Variable  ${Eingabefelder}  lkj lkj  ${oiu}
        """
    val s2 = """  ${Eingabefelder}=  Eingabefelder lesen   Eingabefelder.xlsx  Neugeschäft
        """
    val s3 = """   page should contain    Wagnis- und Risiko-Suche
      """
    val p = RobotParser.parse(RobotParser.zeile, s3).get
    assert(p.asInstanceOf[List[Aufruf]](0).schluesselwort == "page should contain")
    assert(p.asInstanceOf[List[Aufruf]](0).parameter(0) == "Wagnis- und Risiko-Suche")
  }
  //
  test("settings") {
    val settingsString = """*** Settings ***

Documentation   HuGO Schlüsselwörter technisch
Library       ExcelLibrary.py
Library   Collections
  """
    val f = RobotParser.parse(RobotParser.settings, settingsString).get
    assert(f.size == 3)
    assert(f(0).schluessel == "Documentation")
    assert(f(0).wert == "HuGO Schlüsselwörter technisch")
    assert(f(1).schluessel == "Library")
    assert(f(1).wert == "ExcelLibrary.py")
    assert(f(2).schluessel == "Library")
    assert(f(2).wert == "Collections")
  }

  test("variables") {
    val settingsString = """*** Variables ***
${TestdatenDatei}  06.01.2015 Testfälle Sach_Sprint14_neuer_Tarif.xls
${TestdatenBlatt}  Testfälle

${TestdatenID}  67
  """
    val f1 = RobotParser.parse(RobotParser.variables, settingsString)
    val f = f1.get
    assert(f.size == 3)
    assert(f(0).variable == """${TestdatenDatei}""")
    assert(f(1).variable == """${TestdatenBlatt}""")
    assert(f(2).variable == """${TestdatenID}""")
    assert(f(0).wert == """06.01.2015 Testfälle Sach_Sprint14_neuer_Tarif.xls""")
    assert(f(1).wert == """Testfälle""")
    assert(f(2).wert == """67""")
  }

  test("testcases") {
    val testString = """*** Test Cases ***
Lesetest
        Datei lesen
        dsfs
Zweitertest
    Hallo Welt"""
    val f = RobotParser.parse(RobotParser.testcase, testString).get
    assert(f.size == 2)
    assert(f(0).wort == "Lesetest")
    assert(f(0).zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Datei lesen")
    assert(f(1).wort == "Zweitertest")
    assert(f(1).zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Hallo Welt")

  }

  test("keywordZeile") {
    val testStrin2 = """    Set Suite Variable  ${Eingabefelder}  lkj lkj  ${oiu}"""
    val testString = """	  Set Suite Variable  ${Eingabefelder}  lkj lkj  ${oiu}
  			  """
    val f = RobotParser.parse(RobotParser.aufruf, testStrin2).get
    assert(f.asInstanceOf[Aufruf].schluesselwort == "Set Suite Variable")
    assert(f.asInstanceOf[Aufruf].parameter(0) == "${Eingabefelder}")

  }
  test("keywordTest") {
    val testString = """Initialisierung
     Set Suite Variable  ${Eingabefelder}  lkj lkj  ${oiu}
     """
    val f = RobotParser.parse(RobotParser.keyword, testString).get
    assert(f.wort == "Initialisierung")
    assert(f.zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Set Suite Variable")
    assert(f.zeilen(0).asInstanceOf[Aufruf].parameter(0) == "${Eingabefelder}")
  }

  test("documentation") {
    val s = "  [Documentation]  Liest alle Spaltennamen und -nummern aus der Exceldatei ${Dateiname}, Blatt ${Blattname} und baut daraus ein Python Dict."
    val p = RobotParser.parse(RobotParser.documentation, s)
    assert(p.get == "Liest alle Spaltennamen und -nummern aus der Exceldatei ${Dateiname}, Blatt ${Blattname} und baut daraus ein Python Dict.")
  }

  test("documentation mehrzeilig") {
    val s = """  [Documentation]  Setzt eine Schadenprävention.
   ...  Argument Bereich: String  Bsp.:  Bereich 1 - KaiserStr. 23, 40543 Düsseldorf"""
    val p = RobotParser.parse(RobotParser.documentationLang, s)
    assert(p.get == "Setzt eine Schadenprävention. Argument Bereich: String  Bsp.:  Bereich 1 - KaiserStr. 23, 40543 Düsseldorf")
  }

  test("arguments") {
    val s = """  [Arguments]  ${Dateiname}  ${Blattname}"""
    val p = RobotParser.parse(RobotParser.arguments, s).get
    assert(p.size == 2)
    assert(p(0) == """${Dateiname}""")
    assert(p(1) == """${Blattname}""")
  }

  test("arguments mehrzeilig") {
    val s = """  [Arguments]  ${Dateiname}  ${Blattname}
      ...  ${drei}  ${vier}"""
    val p = RobotParser.parse(RobotParser.argumentsLang, s).get
    assert(p.size == 4)
    assert(p(0) == """${Dateiname}""")
    assert(p(1) == """${Blattname}""")
    assert(p(2) == """${drei}""")
    assert(p(3) == """${vier}""")

  }

  test("keyword einfach") {
    val testString = """Initialisierung
    [Documentation]  Hans Wurst
    Alle Eingabefelder einlesen  ${EingabefelderDatei}  ${EingabefelderBlatt}
    Open browser   ${UrlUmgebung}
    Set Window Size  1000  1000
    [Return]  ${UrlUmgebung}  ${UrlUmgebung2}
"""
    val fb = RobotParser.parse(RobotParser.keyword, testString)
    val f = fb.get
    assert(f.wort == "Initialisierung")
    assert(f.zeilen.size == 3)
    assert(f.documentation.get == "Hans Wurst")
    assert(f.zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Alle Eingabefelder einlesen")
    assert(f.returnValue.get(0) == """${UrlUmgebung}""")
    assert(f.returnValue.get(1) == """${UrlUmgebung2}""")
  }

  test("keyword") {
    val testString = """Initialisierung
    [Documentation]  Hans Wurst
    Alle Eingabefelder einlesen  ${EingabefelderDatei}  ${EingabefelderBlatt}
    Open browser   ${UrlUmgebung}
    Set Window Size  1000  1000
    :FOR  ${i}  IN RANGE  100  101
    \   ${Status}  ${Rueckgabewert} =  Run Keyword and ignore Error  Durchlauf  ${i}
    \   Run Keyword if  '${Status}' == 'FAIL'  Log  : Abbruch  console=true
    [Return]  ${UrlUmgebung}  ${UrlUmgebung2}
"""
    val fb = RobotParser.parse(RobotParser.keyword, testString)
    val f = fb.get
    assert(f.wort == "Initialisierung")
    assert(f.zeilen.size == 5)
    assert(f.documentation.get == "Hans Wurst")
    assert(f.zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Alle Eingabefelder einlesen")
    assert(f.returnValue.get(0) == """${UrlUmgebung}""")
    assert(f.returnValue.get(1) == """${UrlUmgebung2}""")
  }

  test("*** Keywords ***") {
    val testString = """*** Keywords ***
Initialisierung
       Alle Eingabefelder einlesen  ${EingabefelderDatei}  ${EingabefelderBlatt}
       Open browser   ${UrlUmgebung}
       Set Window Size  1000  1000

Verkauf starten
     Klick  Hauptmenu:Verkauf
     Klick  Hauptmenu:Hugo

Waehle PFF-Baustein    [Arguments]    ${Baustein}
    Run keyword if    '${Baustein}'=='Sach mit EA'    Klick  Produktauswahl:SachMitEA
    Run keyword if    '${Baustein}'=='Sach'    Klick  Produktauswahl:Sach
    Run keyword if    '${Baustein}'=='Ertragsausfall'    Klick  Produktauswahl:Ertragsausfall
    Run keyword if    '${Baustein}'=='Haftpflicht'    Klick  Produktauswahl:Haftpflicht

Waehle PFF-Bausteine    [Arguments]    @{Bausteine}
   Should not be empty    @{Bausteine}
   Auf Element warten  Produktauswahl:Sach
   :FOR  ${Praevention}  IN RANGE  5
   \    Eine Schadenprävention anlegen  ${Praevention}  ${Bereich}
"""
    val f = RobotParser.parse(RobotParser.keywords, testString).get
    assert(f(0).wort == "Initialisierung")
    assert(f(0).zeilen.size == 3)
    assert(f(1).wort == "Verkauf starten")
    assert(f(1).zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Klick")
    assert(f(1).zeilen(0).asInstanceOf[Aufruf].parameter(0) == "Hauptmenu:Verkauf")
    assert(f.size == 4)
    assert(f(3).zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Should not be empty")
    assert(f(3).zeilen(2).asInstanceOf[Aufruf].schluesselwort == "Eine Schadenprävention anlegen")
  }

  test("schluesselwortDefinition") {
    val s = """Alle Eingabefelder einlesen  [Arguments]  ${Dateiname}  ${Blattname}"""
    val p = RobotParser.parse(RobotParser.schluesselwortDefinition, s).get
    assert(p.name == "Alle Eingabefelder einlesen")
  }

  test("schluesselwortDefinition ohne Parameter") {
    val s = """Alle Eingabefelder einlesen"""
    val p = RobotParser.parse(RobotParser.schluesselwortDefinition, s).get
    assert(p.name == "Alle Eingabefelder einlesen")
  }

  test("runKeywordIf") {
    val s = """Run Keyword if  ("checked" in "${Class}")"""
    val p = RobotParser.parse(RobotParser.runKeywordIf, s)
    assert(p.get == """("checked" in "${Class}")""")
  }

  test("robot") {
    val testString = """*** Settings ***

Library       ExcelLibrary.py
Library   Collections

*** Test Cases ***
Lesetest
    Datei lesen

*** Keywords ***
Datei lesen
       ${Eingabefelder}   Eingabefelder lesen   Eingabefelder.xlsx  Neugeschäft
       Set Suite Variable  ${Eingabefelder}
       # ${Feld}  Get from dictionary  ${Eingabefelder}  Haftpflicht:WagnisText
       ${Typ}  ${Index}  Eingabefeld holen  ${Eingabefelder}  Haftpflicht:WagnisText
    """
    val f = RobotParser.parse(RobotParser.robot, testString)
    assert(f.get.settings(0).schluessel == """Library""")
    assert(f.get.settings(0).wert == """ExcelLibrary.py""")
    assert(f.get.variables == None)
  }

  test("For Schleife") {
    val s = """   :FOR  ${i}  IN RANGE  100  101
    \   ${Status}  ${Rueckgabewert} =  Run Keyword and ignore Error  Durchlauf  ${i}
    \   ${Status2}  =  Run Keyword and ignore Error  Durchlauf2  ${i}
"""
    val p = RobotParser.parse(RobotParser.forSchleife, s)
    val pa = p.get
    assert(pa(0).isInstanceOf[List[Zuweisung]])
    assert(pa(0).asInstanceOf[List[Zuweisung]](0).aufruf.schluesselwort == "Durchlauf")
    assert(pa(1).asInstanceOf[List[Zuweisung]](0).aufruf.schluesselwort == "Durchlauf2")
  }

  test("For Schleife2") {
    val s = """   :FOR  ${i}  IN RANGE  100  101
    \   ${Status}  ${Rueckgabewert} =  Run Keyword and ignore Error  Durchlauf  ${i}
    \   Run Keyword if  '${Status}' == 'FAIL'  Log  : Abbruch  console=true"""
    val p = RobotParser.parse(RobotParser.forSchleife, s).get
    assert(p(0).isInstanceOf[List[Zuweisung]])
    assert(p(0).asInstanceOf[List[Zuweisung]](0).aufruf.schluesselwort == "Durchlauf")
    assert(p(1).asInstanceOf[List[Aufruf]](0).schluesselwort == "Log")
  }

  test("waitUntilKeywordSucceeds") {
    val s = """wait until keyword succeeds  60s  1s"""
    val p = RobotParser.parse(RobotParser.waitUntilKeywordSucceeds, s).get
    assert(p == ("60s", "1s"))
  }

  test("robot2") {
    val testString = """
*** Settings ***

Library       ExcelLibrary.py
Library   Collections

*** Variables ***
${UrlUmgebung}    http://imt2.provinzial.com/provinzial/layout_elemente/pronet_oberflaeche/layout_default/html/startWeiterleitungProvinzialNoSingleSignOn.htm
${Benutzer}    g888796
${Passwort}   Abcdef01
${EingabefelderDatei}  Eingabefelder.xlsx
${EingabefelderBlatt}  Neugeschäft
${KonfigurationsDatei}  Konfiguration.xlsx

${TestdatenDatei}  06.01.2015 Testfälle Sach_Sprint14_neuer_Tarif.xls
${TestdatenBlatt}  Testfälle
${TestdatenID}  67

${TestdatenDateiAntrag}  20150127 Testfälle Antragsfragen_Sprint15.xls
${TestdatenBlattAntrag}  Testfälle
${TestdatenIDAntrag}  1

*** Test Cases ***
Lesetest
  Datei lesen

*** Keywords ***
Initialisierung
     Alle Eingabefelder einlesen  ${EingabefelderDatei}  ${EingabefelderBlatt}
     Open browser   ${UrlUmgebung}
     Set Window Size  1000  1000
     [Return]  ${UrlUmgebung}

Verkauf starten
     Klick  Hauptmenu:Verkauf
     Klick  Hauptmenu:Hugo
   # Execute javascript  javascript:(function(){navigierenMitParameter('verkauf&HHG/Neu','de.rpv.pronet.beratung.ao.UC1_BeratungenAnzeigenAO','beratung_schnellstart','115','de.rpv.pronet.beratung.prozesse.Beratung');})();

Gasthof Fortuna
     Risikoauswahl
     Neugeschaeft
     Grunddaten
     Versicherungssummenermittlung Gebaeude
     Gefahrengruppen
     Versicherungssumme fuer EA
     Plus-Bausteine Sachversicherung
     Haftpflicht Allgemeine Daten
     Haftpflicht Umweltversicherung
     Haftpflicht Plusbausteine
     Beitragsuebersicht
     Antragsfragen
     pause execution

Alle Spaltennamen einlesen  [Arguments]  ${Dateiname}  ${Blattname}
     [Documentation]  lkj asdf
     ${Spaltennamen}  Read Dictionary  ${Dateiname}  ${Blattname}
     Set Suite Variable  ${Spaltennamen}
  """
    val f = RobotParser(testString)
    assert(f.get.settings(0).schluessel == """Library""")
    assert(f.get.settings(0).wert == """ExcelLibrary.py""")
    assert(f.get.testcases.get(0).wort == """Lesetest""")
    assert(f.get.testcases.get(0).zeilen(0).asInstanceOf[Aufruf].schluesselwort == "Datei lesen")
    assert(f.get.variables.get.size == 12)
    assert(f.get.variables.get(0).variable == """${UrlUmgebung}""")
    assert(f.get.variables.get(2).wert == """Abcdef01""")

    assert(f.get.keywords.size == 4)
    assert(f.get.keywords(0).wort == "Initialisierung")
    assert(f.get.keywords(0).zeilen.size == 3)
    assert(f.get.keywords(0).returnValue.get(0) == "${UrlUmgebung}")
    assert(f.get.keywords(1).wort == "Verkauf starten")
    assert(f.get.keywords(1).zeilen.size == 2)
    assert(f.get.keywords(2).wort == "Gasthof Fortuna")
    assert(f.get.keywords(2).zeilen.size == 13)
    assert(f.get.keywords(3).wort == "Alle Spaltennamen einlesen")
    assert(f.get.keywords(3).zeilen.size == 2)
    assert(f.get.keywords(3).arguments.get(0) == "${Dateiname}")
    assert(f.get.keywords(3).arguments.get(1) == "${Blattname}")
  }

}
