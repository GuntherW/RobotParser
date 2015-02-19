package de.codecentric.wittig.robotparser

import org.scalatest.FunSuite
import java.text.Normalizer

class StringTest extends FunSuite {

  val s1 = "ä"
  val s2 = "ä" // a\u0308
  test("string is unequal") {
    assert(s1 != s2)
  }

  test("normalizer") {
    assert(Normalizer.isNormalized(s1, Normalizer.Form.NFC))
    assert(!Normalizer.isNormalized(s1, Normalizer.Form.NFD))
    assert(Normalizer.isNormalized(s1, Normalizer.Form.NFKC))
    assert(!Normalizer.isNormalized(s1, Normalizer.Form.NFKD))
    assert(!Normalizer.isNormalized(s2, Normalizer.Form.NFC))
    assert(Normalizer.isNormalized(s2, Normalizer.Form.NFD))
    assert(!Normalizer.isNormalized(s2, Normalizer.Form.NFKC))
    assert(Normalizer.isNormalized(s2, Normalizer.Form.NFKD))
  }

  test("normalize") {
    val ns1 = Normalizer.normalize(s1, Normalizer.Form.NFC)
    val ns2 = Normalizer.normalize(s2, Normalizer.Form.NFC)
    assert(ns1 == ns2)
  }

  def normalize(string: String) = Normalizer.normalize(string, Normalizer.Form.NFC)
}
