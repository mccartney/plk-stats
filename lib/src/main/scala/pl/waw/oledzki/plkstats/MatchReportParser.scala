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
              case IndividualPlayExpression(playerNo, playerName, action) =>
                IndividualPlay(PlayerReference(playerNo, playerName), classifyIndividualPlay(action))
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

  object Actions {

    val CelnyZaTrzy = "celny .* za 3".r
    val CelnyZaDwa = "celny ((?:.* za 2)|(?:wsad)|(?:alley-oop)|(?:lay-up.*))".r
    val CelnyRzutWolny = "celny rzut wolny.*".r
    val NiecelnyZaTrzy = "niecelny .* za 3".r
    val NiecelnyZaDwa = "niecelny ((?:.* za 2)|(?:lay-up.*))".r
    val NiecelnyRzutWolny = "niecelny rzut wolny.*".r
    val Strata = "strata (?:- )?(.*)".r
    val Przechwyt = "przechwyt".r
    val Zbiórka = "zbiórka w (.*)".r
    val Asysta = "asysta".r
    val Faulowany = "faulowany".r
    val FaulOsobisty = "faul osobisty".r
    val FaulWAtaku = "faul w ataku".r
    val Zmiana = "zmiana - (.*)".r
    val RzutSedziowski = "(.*) rzut sędziowski".r
    val Blok = "blok".r
  }

  private def classifyIndividualPlay(action: String): IndividualPlayAction = {
    import Actions._

    action match {
      // TODO subtypes for points scored/missed throws, currently the info is ignored
      case Strata(how) => Turnover(how)
      case Przechwyt() => Steal
      case CelnyRzutWolny() => PointsScored(1)
      case CelnyZaDwa(_) => PointsScored(2)
      case CelnyZaTrzy() => PointsScored(3)
      case NiecelnyRzutWolny() => MissedThrow(1)
      case NiecelnyZaDwa(_) => MissedThrow(2)
      case NiecelnyZaTrzy() => MissedThrow(3)
      case Zbiórka(where) if where == "ataku" => OffensiveRebound
      case Zbiórka(where) if where == "obronie" => DefensiveRebound
      case Asysta() => Assist
      case Faulowany() => BeingFouled
      case FaulOsobisty() => DefensiveFoul
      case FaulWAtaku() => OffensiveFoul
      case Zmiana(how) if how == "wejście" => SubstitutedIn
      case Zmiana(how) if how == "zejście" => SubstitutedOut
      case Blok() => Block
      case RzutSedziowski(how) if how == "wygrany" => TipOffWon
      case RzutSedziowski(how) if how == "przegrany" => TipOffLost
      case what => OtherIndividualPlayAction(what)
    }
  }
}
