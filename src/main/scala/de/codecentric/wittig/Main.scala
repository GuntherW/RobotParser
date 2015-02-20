package de.codecentric.wittig

import scopt.OptionParser
import de.codecentric.wittig.cli._
import de.codecentric.wittig.cypher._
import java.io.File
import com.typesafe.scalalogging.LazyLogging

object Main extends App with LazyLogging {

  val parser = createParamParser
  // parser.parse returns Option[C]
  parser.parse(args, Config()) match {
    case Some(config) => {
      println("Config: " + config)
      if (!config.dir.exists()) {
        println(s"dir: ${config.dir.getPath} doesn't exist. (${config.dir.getAbsolutePath})")
        System.exit(1)
      }
      val neoService = NeoService(config.dir.getAbsolutePath)
      if (config.init) {
        neoService.deleteAll
        neoService.insertKeywords
        neoService.insertTestKeywords
        neoService.insertRelations("Schluesselwort")
        neoService.insertRelations("Testcase")
      }

      println("Bitte geben Sie den Namen des Schlüsselwortes ein:")
      for (ln <- io.Source.stdin.getLines) ausgabe(ln)

      def ausgabe(s: String) = {
        if (s == "exit") {
          println("Das Programm wird beendet.")
          System.exit(0)
        }
        logger.debug(s"Suche für '$s' wird gestartet:")
        val rel = neoService.getRelationen(s)
        rel.map { case (a, b, c, d) => s"${b.padTo(40, ' ')}${a.padTo(40, ' ')} -> $c\t($d)" }.foreach { println }
      }
    }

    case None =>
    // arguments are bad, error message will have been displayed
  }

  /**
   *
   */
  def createParamParser = {
    new OptionParser[Config]("robotparser") {
      head("robotparser", "1.0")
      opt[File]("dir") valueName ("<file>") action { (x, c) =>
        c.copy(dir = x)
      } text ("dir of robot files (ending with .robot). Default: '.'")
      opt[Unit]('v', "verbose") action { (_, c) =>
        c.copy(verbose = true)
      } text ("verbose is a flag")
      opt[Unit]('i', "init") action { (_, c) =>
        c.copy(init = true)
      } text ("this option will delete all entities from db and insert new")
      note("some notes.\n")
      help("help") text ("prints this usage text")
    }
  }
}
