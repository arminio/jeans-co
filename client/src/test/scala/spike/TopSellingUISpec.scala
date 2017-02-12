package spike

import java.util.Date

import armin.jeans.client.modules._
import armin.jeans.client.services.JeansAppCircuit.{composeHandlers, zoomRW}
import armin.jeans.client.services._
import armin.jeans.shared.{Sale, SaleFilter}
import diode.Circuit
import diode.react.{ReactConnectProxy, ReactConnector}
import japgolly.scalajs.react.{ReactComponentB, _}
import japgolly.scalajs.react.test.{DebugJs, ReactTestUtils}
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.jquery.jQuery
import org.scalatest.{FunSpec, Matchers}


//!!
//object Egg {
//  val HelloMessage = ReactComponentB[String]("HelloMessage")
//    .render($ => <.div(^.className := "abc", "Hello ", $.props))
//    .build
//}

class TopSellingUISpec extends FunSpec with Matchers {

  describe("Top Selling (react components)") {
//
//
//    it("should clunk") {
//      val message = Egg.HelloMessage("Armin")
//            val comp = ReactTestUtils.renderIntoDocument(message)
//            val div = ReactTestUtils.findRenderedDOMComponentWithClass(comp, "abc")
//
//            println(s"thing: $div ")
//            val text = jQuery(div).text()
//            //      println(s"thing: ${DebugJs inspectObject text}")
//            assert(text == "Hello Armin")
//    }


    object TestAppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
      // initial application model
      override protected def initialModel = RootModel(SalesAndFilter(Sales(Seq(Sale(new Date(), "Country1", "Maufaturer1", "M", "32", "Red", "Skinny", 10))), SaleFilter.empty))
      // combine all handlers into one
      override protected val actionHandler = composeHandlers(
        new SalesHandler(zoomRW(_.salesAndFilter.sales)((m, v) => m.copy(salesAndFilter = m.salesAndFilter.copy(sales = v)))),
        new FilterUpdateHandler(zoomRW(_.salesAndFilter.saleFilter)((m, v) => m.copy(salesAndFilter = m.salesAndFilter.copy(saleFilter = v))))
      )
    }

    val appCircuit = TestAppCircuit
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

    ignore("top selling items (table)") {

      it("should list the top sellers") {
        val reactConnectProxy: ReactConnectProxy[SalesAndFilter] = appCircuit.connect(_.salesAndFilter)

        topSellingPages foreach { page =>
          val unmountedComponent = reactConnectProxy(page._1)

          val mountedComponent = ReactTestUtils.renderIntoDocument(unmountedComponent)

          val div = ReactTestUtils.scryRenderedDOMComponentsWithClass(mountedComponent, "top-selling-list")

          val text = jQuery(div).text()

          println(text)
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

}

//  val tests = TestSuite {
//    //    'hello {
//    //      'world {
//    //        val x = 1
//    //        val y = 2
//    //        assert(x != y)
//    //        (x, y)
//    //      }
//    //    }
//    //    'test2 {
//    //      val a = 1
//    //      val b = 1
//    //      assert(a == b)
//
//    'testPostFunction {
//      assert(true)
//
//    }
//
//    'test3 {
//      val message = HelloMessage("Armin")
//      val comp = ReactTestUtils.renderIntoDocument(message)
//      val div = ReactTestUtils.findRenderedDOMComponentWithClass(comp, "abc")
//
//      println(s"thing: $div ")
//      val text = jQuery(div).text()
//      //      println(s"thing: ${DebugJs inspectObject text}")
//      assert(text == "Hello Armin0")
//    }
//
//  }
}