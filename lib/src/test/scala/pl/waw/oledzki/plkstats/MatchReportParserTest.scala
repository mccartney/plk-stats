package pl.waw.oledzki.plkstats

import org.junit.runner.RunWith
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MatchReportParserTest extends AnyWordSpec with Matchers {

  "MatchReportParser" should {
    "work" in {
      // given

      // when
      val game = new MatchReportParser().parseHtml("src/test/resources/2021-02-07-stal-legia.html")

      // test
      game.events.size should be(534)
      game.events.collect { case i: IndividualPlay => i }.count(_.who == PlayerReference("91", "D. Wyka")) should be(22)
      game.events.collect { case t: TeamPlay => t }.size should be(18)
      game.events.collect { case m: MatchEvent => m }.size should be(11)
    }

    "know all events" in {
      // given

      // when
      val game = new MatchReportParser().parseHtml("src/test/resources/2021-02-07-stal-legia.html")

      // provide debug info
      game.events.collect { case i: IndividualPlay => i.what }.groupBy(_.getClass).foreach {
        case (klass: Class[_], items) =>
          println(s"${items.size} - ${klass.getSimpleName}")
      }
      println("---------")
      game.events.collect { case i: IndividualPlay => i.what }.collect { case oi: OtherIndividualPlayAction => oi }.groupBy(_.what).foreach {
        case (what: String, items) =>
          println(s"${items.size} - |$what|")
      }

      // test
      game.events.collect { case i: IndividualPlay => i.what }.collect { case oi: OtherIndividualPlayAction => oi }.isEmpty shouldBe(true)
    }

    "parse the time properly" in {
      val sut = new MatchReportParser()

      sut.parseTime(1, "10:00:00") should be(TimeIndication(0, 0))
      sut.parseTime(1, "09:59:00") should be(TimeIndication(0, 1))
      sut.parseTime(1, "00:03:80") should be(TimeIndication(9, 56, 2))
      sut.parseTime(1, "00:00:00") should be(TimeIndication(10, 0))
      sut.parseTime(2, "10:00:00") should be(TimeIndication(10, 0))
      sut.parseTime(2, "00:00:00") should be(TimeIndication(20, 0))
      sut.parseTime(4, "00:04:40") should be(TimeIndication(39, 55, 6))
      // TODO overtime
    }
  }
}
