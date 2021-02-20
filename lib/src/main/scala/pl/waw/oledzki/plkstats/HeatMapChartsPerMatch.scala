package pl.waw.oledzki.plkstats

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.{BitmapEncoder, HeatMapChart}
import org.knowm.xchart.style.theme.{Theme, XChartTheme}

import scala.jdk.CollectionConverters._

object HeatMapChartsPerMatch extends App {

  val game = new MatchReportParser().parseHtml("src/test/resources/2021-02-07-stal-legia.html")

  case class Scored(who: PlayerReference, matchMinute: Int, count: Int)

  val pointsHistory2 = game.events
    .collect { case ip: IndividualPlay => ip }
    .filter(_.what.isInstanceOf[PointsScored])
  println(pointsHistory2)


  val pointsHistory = game.events
    .collect { case ip: IndividualPlay => ip }
    .filter(_.what.isInstanceOf[PointsScored])
    .map(play => Scored(play.who, play.time.matchMinute, play.what.asInstanceOf[PointsScored].count))

  val pointsHistoryPerMinute: Map[(PlayerReference, Int), Int] =
    pointsHistory
      .groupBy(scored => (scored.who, scored.matchMinute))
      .map{ case (x, scoreds) => (x, scoreds.map(_.count).sum)}

  val scoringPlayers = pointsHistory.map(_.who).toSet.toList

  println(pointsHistoryPerMinute)

  val chart = new HeatMapChart(800, 300, new XChartTheme())
  chart.addSeries("punkty", (0 to 39).toList.asJava, scoringPlayers.map(_.name).asJava,
    scoringPlayers.flatMap { player =>
      (0 to 39).map { minute =>
        val points = pointsHistoryPerMinute.getOrElse((player, minute), 0)
        if (points > 0) {
          println(s">>>>>>>>>> MIN = ${minute} PLAYER = ${player} INDEX=${scoringPlayers.indexOf(player)} POINTS = $points")
        }
        Array[Number](
          minute,
          scoringPlayers.indexOf(player),
          points.asInstanceOf[Number]
        )
      }
    }.asJava
  )

  BitmapEncoder.saveBitmap(chart, "/tmp/punkty.png", BitmapFormat.PNG)
}
