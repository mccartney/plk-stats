package pl.waw.oledzki

package object plkstats {

  case class PlayerReference(number: String, name: String)

  sealed trait Event
  case class MatchEvent(what: String) extends Event

  trait Play extends Event
  case class TeamPlay(what: String) extends Play
  case class IndividualPlay(who: PlayerReference, what: IndividualPlayAction) extends Play

  sealed trait IndividualPlayAction

  case class PointsScored(count: Int) extends IndividualPlayAction
  case class MissedThrow(points: Int) extends IndividualPlayAction
  case class Turnover(how: String) extends IndividualPlayAction
  case object Steal extends IndividualPlayAction
  trait Rebound extends IndividualPlayAction
  case object OffensiveRebound extends Rebound
  case object DefensiveRebound extends Rebound
  case object Assist extends IndividualPlayAction
  case object Block extends IndividualPlayAction

  case object BeingFouled extends IndividualPlayAction
  trait FoulPlay extends IndividualPlayAction
  case object DefensiveFoul extends FoulPlay
  case object OffensiveFoul extends FoulPlay


  case object SubstitutedIn extends IndividualPlayAction
  case object SubstitutedOut extends IndividualPlayAction

  case object TipOffLost extends IndividualPlayAction
  case object TipOffWon extends IndividualPlayAction

  case class OtherIndividualPlayAction(what: String) extends IndividualPlayAction


  case class Match(team1: String, team2: String, score1: Int, score2: Int, events: Seq[Event])
}
