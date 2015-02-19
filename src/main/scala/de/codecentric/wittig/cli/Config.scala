package de.codecentric.wittig.cli

import java.io.File

case class Config(dir: File = new File("."), verbose: Boolean = false, debug: Boolean = false,
                  init: Boolean = false)
