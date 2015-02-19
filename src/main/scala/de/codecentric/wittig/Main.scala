package de.codecentric.wittig

import scopt.OptionParser
import de.codecentric.wittig.cli._
import java.io.File

object Main extends App {

  val parser = new OptionParser[Config]("robotparser") {
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
  // parser.parse returns Option[C]
  parser.parse(args, Config()) match {
    case Some(config) => {
      println("Hurra" + config)
    }

    case None =>
    // arguments are bad, error message will have been displayed
  }

}
