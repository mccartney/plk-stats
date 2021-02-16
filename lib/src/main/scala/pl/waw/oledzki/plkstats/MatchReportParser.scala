package pl.waw.oledzki.plkstats

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._



class MatchReportParser {

  def parseHtml(): Unit = {
    val browser = JsoupBrowser()
    val doc = browser.parseFile("src/test/resources/2021-02-07-stal-legia.html")
    val quarters = (doc >> elementList("#playbyplay .kwarta")).dropRight(1)
    quarters.foreach { q =>
      (q >> elementList("tr")).foreach { play =>
        val fields = play >> elementList("td")
        if (fields.nonEmpty) {
          require(fields.size == 5, s"There were ${fields.size}")
          val what1 :: score1 :: time :: score2 :: what2 :: _ = fields.map(_.innerHtml.trim)
          if (!(what1.isEmpty ^ what2.isEmpty)) {
            System.out.println(s"Bylo: |$what1|, |$what2|")
          }
        }
      }
    }
  }

}
