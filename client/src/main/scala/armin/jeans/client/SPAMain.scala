package armin.jeans.client

import armin.jeans.client.components.GlobalStyles
import armin.jeans.client.modules._
import armin.jeans.client.services.SPACircuit
import armin.jeans.client.logger._
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalacss.Defaults._
import scalacss.ScalaCssReact._

@JSExport("SPAMain")
object SPAMain extends js.JSApp {

  // Define the locations (pages) used in this application
  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TopManufacturersLoc extends Loc
  case object TopSellingSizesLoc extends Loc
  case object TopSellingMonthsLoc extends Loc
  case object TopSellingCountriesLoc extends Loc
  case object TopSellingColoursLoc extends Loc
  case object TopSellingStylesLoc extends Loc


  // configure the router
  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    
    val salesAndFilterWrapper = SPACircuit.connect(_.salesAndFilter)
    // wrap/connect components to the circuit
    (
      staticRoute(root, DashboardLoc) ~> renderR(ctl => Dashboard(ctl))
      | staticRoute("#topManufactureres", TopManufacturersLoc) ~> renderR(ctl => salesAndFilterWrapper(TopManufacturers(_)))
      | staticRoute("#topSellingSizes", TopSellingSizesLoc) ~> renderR(ctl => salesAndFilterWrapper(TopSellingSizes(_)))
      | staticRoute("#topSellingMonths", TopSellingMonthsLoc) ~> renderR(ctl => salesAndFilterWrapper(TopSellingMonths(_)))
      | staticRoute("#topSellingCountries", TopSellingCountriesLoc) ~> renderR(ctl => salesAndFilterWrapper(TopSellingCountries(_)))
      | staticRoute("#topSellingColours", TopSellingColoursLoc) ~> renderR(ctl => salesAndFilterWrapper(TopSellingColours(_)))
      | staticRoute("#topSellingStyles", TopSellingStylesLoc) ~> renderR(ctl => salesAndFilterWrapper(TopSellingStyles(_)))
      ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
  }.renderWith(layout)

  // base layout for all pages
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    <.div(
      // here we use plain Bootstrap class names as these are specific to the top level layout defined here
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", c.link(DashboardLoc)("Armin Jeans"))),
          <.div(^.className := "collapse navbar-collapse", MainMenu(c, r.page))

        )
      ),
      // currently active module is shown in this container
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(): Unit = {
    log.warn("Application starting")
    // send log messages also to the server
    log.enableServerLogging("/logging")
    log.info("This message goes to server as well")

    // create stylesheet
    GlobalStyles.addToDocument()
    // create the router
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    ReactDOM.render(router(), dom.document.getElementById("root"))
  }
}
