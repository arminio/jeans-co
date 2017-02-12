package armin.jeans.client.components

import java.util.Date

import armin.jeans.client.modules._
import armin.jeans.client.services._
import armin.jeans.shared.{Sale, SaleFilter}
import diode.react.{ModelProxy, ReactConnectProxy, ReactConnector}
import diode.{ActionHandler, Circuit, ModelRW}
import japgolly.scalajs.react._
import japgolly.scalajs.react.test.ReactTestUtils
import org.scalajs.jquery.jQuery
import org.scalatest.{FunSpec, Matchers}


class TestAppCircuit(sales: Sales, saleFilter: SaleFilter = SaleFilter.empty) extends Circuit[RootModel] with ReactConnector[RootModel] {

  class TestHandler[M](modelRW: ModelRW[M, Sales]) extends ActionHandler(modelRW) {
    override def handle = {
      case _ => noChange
    }
  }

  override protected def initialModel = {
    RootModel(SalesAndFilter(sales, saleFilter))
  }

  override protected val actionHandler = composeHandlers(
    new TestHandler(zoomRW(_.salesAndFilter.sales)((m,v)=>m))
  )
}


/**
  * The assertions in this test are not ideal!
  * The reason being I was unable to traverse the dom (after doing a find) as jquery was not being nice to me today.
  */
class TopSellingUISpec extends FunSpec with Matchers {

  describe("Top Selling (react components)") {


    val feb2017 = new Date(1486934429000l)
    val march2017 = new Date(1489353629000l)

    val testSales = Sales(Seq(
      Sale(feb2017, "UK", "Adidas", "Gender:M", "size-Large", "Red", "style-Skinny", 19),
      Sale(march2017, "Germany", "Nike", "Gender:F", "size-Small", "Blue", "style-Bootleg", 88)
    ))

    val topSellingPages = Seq(
      (TopManufacturers.apply _,"TopManufacturers"),
      (TopSellingCountries.apply _,"TopSellingCountries"),
      (TopSellingColours.apply _,"TopSellingColours"),
      (TopSellingMonths.apply _,"TopSellingMonths"),
      (TopSellingSizes.apply _,"TopSellingSizes"),
      (TopSellingStyles.apply _,"TopSellingStyles")

    )
    describe("filter area rendering") {
      val appCircuit = new TestAppCircuit (testSales)

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

    describe("items table") {

      def getTableBodyAsText(unmountedComponent: ReactComponentU[(ModelProxy[SalesAndFilter]) => ReactElement, SalesAndFilter, Any, TopNode]) = {

        val mountedComponent = ReactTestUtils.renderIntoDocument(unmountedComponent)
        val div = ReactTestUtils.findRenderedDOMComponentWithClass(mountedComponent, "top-selling-list")
        val tableRows = jQuery(div).find("tbody tr")
        tableRows
      }

      describe("top selling items (table) rendering") {

        val appCircuit = new TestAppCircuit (testSales)
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

      describe("Filtering items") {

        it("should filter the Top Manufacturers") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(manufacturer = Some("Adidas")))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopManufacturers(_))).text() shouldBe "Adidas19"
        }
        it("should filter the Top Selling Countries") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(deliveryCountry = Some("Germany")))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopSellingCountries(_))).text() shouldBe "Germany88"
        }
        it("should filter the Top Selling Colours") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(colour = Some("Blue")))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopSellingColours(_))).text() shouldBe "Blue88"
        }
        it("should filter the Top Selling Months") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(orderDate = Some(feb2017)))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopSellingMonths(_))).text() shouldBe "2017-February19"
        }
        it("should filter the Top Selling Sizes") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(size = Some("size-Small")))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopSellingSizes(_))).text() shouldBe "size-Small88"
        }
        it("should filter the Top Selling Styles") {
          val appCircuit = new TestAppCircuit (testSales, SaleFilter.empty.copy(style = Some("style-Skinny")))
          val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)
          getTableBodyAsText(reactConnectProxy(TopSellingStyles(_))).text() shouldBe "style-Skinny19"
        }
      }
    }

  }
}