package grouper

import java.util.Date

import org.scalatest.{FunSpec, Matchers}
import armin.jeans.shared.{Sale, SaleFilter}

import scala.scalajs.js


/**
  * Created by armin.
  */
class SalesGrouperSpec extends FunSpec with Matchers {
  val male = "M"
  val female = "F"

  val country1 = "country1"
  val country2 = "country2"

  val manufacturer1 = "m1"
  val manufacturer2 = "m2"

  val size1 = "1"
  val size2 = "2"
  val size3 = "3"

  val orderDate = new Date()
  val anotherOrderDate = new Date(2015, 1, 1)

  describe("filterSales") {
    val sales = Seq(
      Sale(orderDate, country1, manufacturer1, male, "12", "red", "some-style", 1),
      Sale(orderDate, country1, manufacturer1, female, "12", "red", "some-style", 2),
      Sale(orderDate, country1, manufacturer2, female, "50", "red", "no-style", 5),
      Sale(anotherOrderDate, country1, manufacturer2, female, "red", "red", "but-style", 50),
      Sale(anotherOrderDate, country2, manufacturer2, female, "55", "yellow", "any-style", 400)
    )

    it("should not filter any sales if no filter given") {
      SalesGrouper.filterSales(sales, SaleFilter.empty) shouldBe sales
    }

    it("should filter sales by order date") {
      SalesGrouper.filterSales(sales, SaleFilter(orderDate = Some(orderDate))) shouldBe sales.take(3)
    }

    it("should filter sales by deliveryCountry") {
      SalesGrouper.filterSales(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe sales.take(4)
    }

    it("should filter sales by manufacturer") {
      SalesGrouper.filterSales(sales, SaleFilter(manufacturer = Some(manufacturer1))) shouldBe sales.take(2)
    }

    it("should filter sales by gender") {
      SalesGrouper.filterSales(sales, SaleFilter(gender = Some(male))) shouldBe sales.take(1)
    }

    it("should filter sales by size") {
      SalesGrouper.filterSales(sales, SaleFilter(size = Some("12"))) shouldBe sales.take(2)
    }

    it("should filter sales by colour") {
      SalesGrouper.filterSales(sales, SaleFilter(colour = Some("red"))) shouldBe sales.take(4)
    }

    it("should filter sales by style") {
      SalesGrouper.filterSales(sales, SaleFilter(style = Some("some-style"))) shouldBe sales.take(2)
    }

  }

  describe("Manufacturers") {

    it("should group top Manufacturers by country and gender the results should be sorted descending by count") {
      val sales = Seq(
        Sale(orderDate, country1, manufacturer1, male, "12", "red", "some-style", 1),
        Sale(orderDate, country1, manufacturer1, male, "38", "blue", "some-style", 2),
        Sale(orderDate, country1, manufacturer2, male, "12", "red", "some-style", 5),
        Sale(orderDate, country1, manufacturer1, female, "12", "red", "some-style", 50),
        Sale(orderDate, country2, manufacturer2, female, "12", "red", "some-style", 400)
      )

      SalesGrouper.topSellingManufacturer(sales, SaleFilter(deliveryCountry = Some(country1), gender = Some(male))) shouldBe
        Seq(TopSellingManufacturer(manufacturer2, 5), TopSellingManufacturer(manufacturer1, 1 + 2))
    }

    it("should group top Manufacturers by country and the results should be sorted descending by count") {
      val sales = Seq(
        Sale(orderDate, country1, manufacturer1, male, "12", "red", "some-style", 1),
        Sale(orderDate, country1, manufacturer1, male, "38", "blue", "some-style", 2),
        Sale(orderDate, country1, manufacturer2, female, "12", "red", "some-style", 5),
        Sale(orderDate, country1, manufacturer1, female, "12", "red", "some-style", 50),
        Sale(orderDate, country2, manufacturer2, female, "12", "red", "some-style", 400)
      )

      SalesGrouper.topSellingManufacturer(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe
        Seq(TopSellingManufacturer(manufacturer1, 1 + 2 + 50), TopSellingManufacturer(manufacturer2, 5))
    }
  }

  describe("sizes") {

    it("should group top sizes by country and results should be sorted descending by count") {
      val sales = Seq(
        Sale(orderDate, country1, manufacturer1, male, size1, "red", "some-style", 1),
        Sale(orderDate, country1, manufacturer1, male, size1, "blue", "some-style", 2),
        Sale(orderDate, country1, manufacturer2, male, size1, "red", "some-style", 5),
        Sale(orderDate, country1, manufacturer1, female, size2, "red", "some-style", 50),
        Sale(orderDate, country2, manufacturer2, female, size1, "red", "some-style", 400)
      )

      SalesGrouper.topSellingSizes(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe
        Seq(TopSellingSizes(size2, 50), TopSellingSizes(size1, 1 + 2 + 5))
    }

  }

  describe("colours") {

    it("should group top colours and results should be sorted descending by count") {
      val sales = Seq(
        Sale(orderDate, country1, manufacturer1, male, size1, "red", "some-style", 1),
        Sale(orderDate, country1, manufacturer1, male, size1, "blue", "some-style", 2),
        Sale(orderDate, country1, manufacturer2, male, size1, "red", "some-style", 5),
        Sale(orderDate, country1, manufacturer1, female, size2, "red", "some-style", 50),
        Sale(orderDate, country2, manufacturer2, female, size1, "red", "some-style", 400)
      )

      SalesGrouper.topSellingColours(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe
        Seq(TopSellingColours("red", 1 + 5 + 50), TopSellingColours("blue", 2))
    }

  }

  describe("Styles") {

    it("should group top Styles and results should be sorted descending by count") {
      val sales = Seq(
        Sale(orderDate, country1, manufacturer1, male, size1, "red", "some-style", 10),
        Sale(orderDate, country1, manufacturer1, male, size1, "blue", "some-style", 20),
        Sale(orderDate, country1, manufacturer2, male, size1, "red", "another-style", 5),
        Sale(orderDate, country1, manufacturer1, female, size2, "red", "any-style", 50),
        Sale(orderDate, country2, manufacturer2, female, size1, "red", "some-style", 400)
      )

      SalesGrouper.topSellingStyles(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe
        Seq(
          TopSellingStyles("any-style", 50),
          TopSellingStyles("some-style", 10 + 20),
          TopSellingStyles("another-style", 5))
    }

  }

  describe("top selling months") {
    import util.DateUtils._

    val jan1 = new js.Date(2016, 0, 1)
    val jan2 = new js.Date(2016, 0, 2)
    val jan3 = new js.Date(2016, 0, 3)

    val feb1 = new js.Date(2016, 1, 1)
    val feb2 = new js.Date(2016, 1, 2)
    val feb3 = new js.Date(2016, 1, 3)

    val mar1 = new js.Date(2016, 2, 1)

    it("should group by top selling months globally and results should be sorted descending by count") {

      val sales = Seq(
        Sale(jan1, country1, manufacturer1, male, size1, "red", "some-style", 1),
        Sale(jan2, country1, manufacturer1, male, size1, "blue", "some-style", 2),
        Sale(jan3, country1, manufacturer1, male, size1, "red", "some-style", 3),
        Sale(feb1, country1, manufacturer1, female, size2, "red", "some-style", 11),
        Sale(feb2, country1, manufacturer1, female, size2, "red", "some-style", 12),
        Sale(feb3, country1, manufacturer1, female, size2, "red", "some-style", 13),
        Sale(mar1, country1, manufacturer1, female, size1, "red", "some-style", 900)
      )

      SalesGrouper.topSellingMonths(sales, SaleFilter()) shouldBe
        Seq(
          TopSellingMonths("2016-March", 900),
          TopSellingMonths("2016-February", 11 + 12 + 13),
          TopSellingMonths("2016-January", 1 + 2 + 3)
        )
    }

    it("should group by top selling months, filter by country and results should be sorted descending by count") {

      val sales = Seq(
        Sale(toJavaDate(jan1), country1, manufacturer1, male, size1, "red", "some-style", 1),
        Sale(toJavaDate(jan2), country1, manufacturer1, male, size1, "blue", "some-style", 2),
        Sale(toJavaDate(feb1), country2, manufacturer1, female, size2, "red", "some-style", 11),
        Sale(toJavaDate(mar1), country1, manufacturer1, female, size1, "red", "some-style", 900)
      )

      SalesGrouper.topSellingMonths(sales, SaleFilter(deliveryCountry = Some(country1))) shouldBe
        Seq(
          TopSellingMonths("2016-March", 900),
          TopSellingMonths("2016-January", 1 + 2)
        )
    }
  }


}
