package de.codecentric.wittig.robot.file

import java.io.File

/**
 * Werkzeug um alle Robotdateien innerhalb eines Verzeichnis (inkl. Unterverzeichnis) zu finden.
 * @author Gunther Wittig
 *
 */
object FileUtil {

  def recursiveListFiles(f: File): Array[File] = {
    f match {
      case x if x.isFile() => Array(x)
      case _ => {
        val all = f.listFiles
        val robotFiles = all.filter { file => file.getPath.endsWith(".robot") }
        robotFiles ++ all.filter(_.isDirectory).flatMap(recursiveListFiles)
      }
    }
  }
}
