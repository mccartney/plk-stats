package pl.waw.oledzki.plkstats

import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.style.theme.XChartTheme
import org.knowm.xchart.{BitmapEncoder, HeatMapChart}

import java.awt.Color
import scala.jdk.CollectionConverters._

object HeatMapChartsPerMatch extends App {

  val game = new MatchReportParser().parseHtml(args.head)

  case class Scored(who: PlayerReference, matchMinute: Int, count: Int)

  val pointsHistory = game.plays
    .collect { case ip: IndividualPlay => ip }
    .filter(_.what.isInstanceOf[PointsScored])
    .map(play => Scored(play.who, play.time.matchMinute, play.what.asInstanceOf[PointsScored].count))

  val pointsHistoryPerMinute: Map[(PlayerReference, Int), Int] =
    pointsHistory
      .groupBy(scored => (scored.who, scored.matchMinute))
      .map{ case (x, scoreds) => (x, scoreds.map(_.count).sum)}

  val scoringPlayers = pointsHistory.map(_.who).toSet.toList
    .sortBy((who:PlayerReference) => f"${-who.whichTeam} ${who.number.toInt}%2d")

  val chart = new HeatMapChart(900, 300, new XChartTheme())
  chart.addSeries("punkty",
    (0 to 39).toList.asJava,
    scoringPlayers.map(player => s"${player.name} #${player.number}").asJava,
    scoringPlayers.flatMap { player =>
      (0 to 39).map { minute =>
        Array[Number](
          minute,
          scoringPlayers.indexOf(player),
          pointsHistoryPerMinute.getOrElse((player, minute), 0).asInstanceOf[Number]
        )
      }
    }.asJava
  )
  chart.setXAxisTitle("minuta meczu")
  val rangeColors = Array(
    new Color(255, 255, 255),
    new Color(255, 153, 51),
    new Color(255, 80, 80),
    new Color(255, 31, 0),
    new Color(204, 51, 0))
  chart.getStyler.setRangeColors(rangeColors)
  chart.getStyler.setShowValue(true)
  chart.getStyler.setHeatMapValueDecimalPattern("#")
  chart.getStyler.setValueFontColor(Color.WHITE)
  chart.getStyler.setPlotContentSize(1.0d)

  BitmapEncoder.saveBitmap(chart, "/tmp/punkty.png", BitmapFormat.PNG)
}
