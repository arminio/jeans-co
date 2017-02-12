package spike

import java.util.Date

import armin.jeans.client.modules._
import armin.jeans.client.services._
import armin.jeans.shared.{Api, Sale, SaleFilter}
import diode.{ActionHandler, Circuit, Effect, ModelRW}
import diode.react.{ModelProxy, ReactConnectProxy, ReactConnector}
import japgolly.scalajs.react._
import japgolly.scalajs.react.test.ReactTestUtils
import org.scalajs.jquery.jQuery
import org.scalatest.{FunSpec, Matchers}


class TopSellingUISpec extends FunSpec with Matchers {

  describe("Top Selling (react components)") {


    val testSales = Sales(Seq(
      Sale(new Date(1486934429000l), "UK", "Adidas", "Gender:M", "size-Large", "Red", "style-Skinny", 19), // Feb 17
      Sale(new Date(1489353629000l), "Germany", "Nike", "Gender:F", "size-Small", "Blue", "style-Bootleg", 88) // March 17
    ))



    class TestAppCircuit(sales: Sales, saleFilter: SaleFilter = SaleFilter.empty) extends Circuit[RootModel] with ReactConnector[RootModel] {

      class TestHandler[M](modelRW: ModelRW[M, Sales]) extends ActionHandler(modelRW) {
        override def handle = {
          case _ => noChange
        }
      }

      override protected def initialModel = {
        RootModel(SalesAndFilter(testSales, saleFilter))
      }

      override protected val actionHandler = composeHandlers(
        new TestHandler(zoomRW(_.salesAndFilter.sales)((m,v)=>m))
      )
    }

    val appCircuit = new TestAppCircuit (testSales)
    val topSellingPages = Seq(
      (TopManufacturers.apply _,"TopManufacturers"),
      (TopSellingCountries.apply _,"TopSellingCountries"),
      (TopSellingColours.apply _,"TopSellingColours"),
      (TopSellingMonths.apply _,"TopSellingMonths"),
      (TopSellingSizes.apply _,"TopSellingSizes"),
      (TopSellingStyles.apply _,"TopSellingStyles")

    )
    describe("filter area") {

      it("should render the filter area with default selections") {
        val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)

        topSellingPages foreach { page =>
          val unmountedComponent = reactConnectProxy(page._1)

          val mountedComponent = ReactTestUtils.renderIntoDocument(unmountedComponent)

          val div = ReactTestUtils.scryRenderedDOMComponentsWithClass(mountedComponent, "filter-area")

          val text = jQuery(div).text()

          val pageNameHint = s"Page that errored: ${page._2}"
          assert(text.contains("Use these filters to get more targeted statistics:"), pageNameHint)
          assert(text.contains("Filters:"), pageNameHint)
          assert(text.contains("Select Colour"), pageNameHint)
          assert(text.contains("Select Country"), pageNameHint)
          assert(text.contains("Select Gender"), pageNameHint)
          assert(text.contains("Select Size"), pageNameHint)
          assert(text.contains("Select Style"), pageNameHint)
          assert(text.contains("Reset"), pageNameHint)
        }

      }
    }

    describe("top selling items (table)") {

      def getTableBodyAsText(unmountedComponent: ReactComponentU[(ModelProxy[SalesAndFilter]) => ReactElement, SalesAndFilter, Any, TopNode]) = {
        val mountedComponent = ReactTestUtils.renderIntoDocument(unmountedComponent)

        val div = ReactTestUtils.findRenderedDOMComponentWithClass(mountedComponent, "top-selling-list")

        println(div)
        val tableRows = jQuery(div).find("tbody tr")
        tableRows
      }

      val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
      it("should list the Top Manufacturers") {
        getTableBodyAsText(reactConnectProxy(TopManufacturers(_))).text() shouldBe "Nike88Adidas19"
      }
      it("should list the Top Selling Countries") {
        getTableBodyAsText(reactConnectProxy(TopSellingCountries(_))).text() shouldBe "Germany88UK19"
      }
      it("should list the Top Selling Colours") {
        getTableBodyAsText(reactConnectProxy(TopSellingColours(_))).text() shouldBe "Blue88Red19"
      }
      it("should list the Top Selling Months") {
        getTableBodyAsText(reactConnectProxy(TopSellingMonths(_))).text() shouldBe "2017-March882017-February19"
      }
      it("should list the Top Selling Sizes") {
        getTableBodyAsText(reactConnectProxy(TopSellingSizes(_))).text() shouldBe "size-Small88size-Large19"
      }
      it("should list the Top Selling Styles") {
        getTableBodyAsText(reactConnectProxy(TopSellingStyles(_))).text() shouldBe "style-Bootleg88style-Skinny19"
      }
    }
  }
}