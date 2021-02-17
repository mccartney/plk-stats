package pl.waw.oledzki

package object plkstats {

  case class PlayerReference(number: String, name: String)

  sealed trait Event
  case class MatchEvent(what: String) extends Event

  trait Play extends Event
  case class TeamPlay(what: String) extends Play
  case class IndividualPlay(who: PlayerReference, what: String) extends Play

  case class Match(team1: String, team2: String, score1: Int, score2: Int, events: Seq[Event])
}
