package de.codecentric.wittig.cypher

object Test extends App {

  import org.anormcypher._
  // Setup the Rest Client
  implicit val connection = Neo4jREST()
  Cypher("Delete n ").execute()
  println("test")
}
