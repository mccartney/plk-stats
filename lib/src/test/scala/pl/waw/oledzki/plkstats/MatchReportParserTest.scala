package pl.waw.oledzki.plkstats

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MatchReportParserTest extends AnyWordSpec {

  "MatchReportParser" should {
    "work" in {
      new MatchReportParser().parseHtml()
    }
  }
}
