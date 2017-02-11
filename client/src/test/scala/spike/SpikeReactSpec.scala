package spike

import japgolly.scalajs.react.test.ReactTestUtils
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{ReactComponentB, _}
import org.scalajs.jquery.jQuery
import org.scalatest.{FunSpec, Matchers}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

//import utest._
//import utest.framework.TestSuite

object Egg {
  val HelloMessage = ReactComponentB[String]("HelloMessage")
    .render($ => <.div(^.className := "abc", "Hello ", $.props))
    .build

}

class SpikeReactSpec extends FunSpec with Matchers {

  describe("chicken") {


    it("should clunk") {
      val message = Egg.HelloMessage("Armin")
            val comp = ReactTestUtils.renderIntoDocument(message)
            val div = ReactTestUtils.findRenderedDOMComponentWithClass(comp, "abc")

            println(s"thing: $div ")
            val text = jQuery(div).text()
            //      println(s"thing: ${DebugJs inspectObject text}")
            assert(text == "Hello Armin")
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