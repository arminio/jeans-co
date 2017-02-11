package armin.jeans.client.services

import java.util.Date

import diode.ActionResult._
import diode.RootModelRW
import diode.data._
import armin.jeans.shared._
import org.scalatest.{FunSpec, Matchers}

class ArminJeansAppCircuitSpec extends FunSpec with Matchers {

  describe("SalesHandler") {
    val model
    = Ready(
      Sales(
        Seq(
          Sale(new Date(), "Country0", "Maufaturer1", "M", "32", "Red", "Skinny", 10)
        )
      )
    )

    val build = new SalesHandler(new RootModelRW(model))
    it("should handle refreshing the sales using an effect") {
      val h = build
      val result = h.handle(RefreshSales)
      println(result)
      result match {
        case EffectOnly(effects) =>
          effects.size shouldBe 1
        case _ =>
          assert(false)
      }
    }

    it("should handle updating the sales") {
      val salesFromServer =
        Seq(
          Sale(new Date(), "Country1", "Maufaturer1", "M", "32", "Red", "Skinny", 10),
          Sale(new Date(), "Country1", "Maufaturer2", "M", "32", "Red", "Skinny", 10),
          Sale(new Date(), "Country1", "Maufaturer3", "M", "32", "Red", "Skinny", 10)

        )

      val h = build
      val result = h.handle(UpdateAllSales(salesFromServer))
      assert(result == ModelUpdate(Ready(Sales(salesFromServer))))
    }
  }


  describe("FilterUpdateHandler") {

    val model
    = SaleFilter.empty.copy(manufacturer = Some("Manufacturer1"))

    val newSalesFilter = model.copy(manufacturer = Some("Adidas"))

    val build = new FilterUpdateHandler(new RootModelRW(model))

    it("should handle Updating Sales Filter") {
      val h = build
      val result = h.handle(UpdatedSalesFilter(newSalesFilter))
      result shouldBe ModelUpdate(newSalesFilter)
    }

    it("should handle Reset Sales Filter") {
      val h = build
      val result = h.handle(ResetSalesFilter)
      result shouldBe ModelUpdate(SaleFilter.empty)
    }
  }

}
