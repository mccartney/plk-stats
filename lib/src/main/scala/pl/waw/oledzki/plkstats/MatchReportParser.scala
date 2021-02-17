package pl.waw.oledzki.plkstats

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

class MatchReportParser {

  val IndividualPlayExpression = "([0-9]+), (.+), (.+)".r

  def parseHtml(filePath: String): Match = {
    val browser = JsoupBrowser()
    val doc = browser.parseFile(filePath)
    val quarters = (doc >> elementList("#playbyplay .kwarta")).dropRight(1)
    val events: Seq[Event] = quarters.flatMap { q =>
      (q >> elementList("tr")).flatMap { play =>
        val fields = play >> elementList("td")
        if (fields.nonEmpty) {
          require(fields.size == 5, s"There were ${fields.size}")
          val what1 :: score1 :: time :: score2 :: what2 :: _ = fields.map(_.innerHtml.trim)
          if (what1 == what2) {
            Some(MatchEvent(what1))
          } else {
            require(what1.isEmpty ^ what2.isEmpty)
            val what = Seq(what1, what2).mkString("")
            Some(what match {
              case IndividualPlayExpression(playerNo, playerName, action) => IndividualPlay(PlayerReference(playerNo, playerName), action)
              case _ => TeamPlay(what)
            })
          }
        } else {
          None
        }
      }
    }
    val todo = -1
    Match("TODO", "TODO", todo, todo, events)
  }
}
