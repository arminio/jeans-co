package grouper

import spatutorial.shared.Types.{Colour, Country, Manufacturer, Size, Style}
import spatutorial.shared.{DateUtils, Sale, SaleFilter}

import scala.scalajs.js



case class TopSellingManufacturer(manufacturer: Manufacturer, count: Int)

case class TopSellingSizes(size: Size, count: Int)

case class TopSellingMonths(month: String, count: Int)

case class TopSellingCountries(country: Country, count: Int)

case class TopSellingColours(colour:Colour, count: Int)

case class TopSellingStyles(style:Style, count: Int)

object SalesGrouper {


  def topSellingManufacturer(sales: Seq[Sale],
                             saleFilter: SaleFilter): Seq[TopSellingManufacturer] = {

    topSellings(sales, saleFilter, _.manufacturer)
      .map(mc => TopSellingManufacturer(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }


  def topSellingSizes(sales: Seq[Sale],
                      saleFilter: SaleFilter): Seq[TopSellingSizes] = {

    topSellings(sales, saleFilter, _.size)
      .map(mc => TopSellingSizes(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }


  def topSellingMonths(sales: Seq[Sale],
                       saleFilter: SaleFilter): Seq[TopSellingMonths] = {

    topSellings(sales, saleFilter, s => {
      val orderDate = new js.Date(s.orderDate.getTime)
      orderDate.getFullYear() + "-" + DateUtils.monthNames(orderDate.getMonth)
    }).map(mc => TopSellingMonths(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }

  def topSellingCountries(sales: Seq[Sale],
                       saleFilter: SaleFilter): Seq[TopSellingCountries] = {

    topSellings(sales, saleFilter, _.deliveryCountry)
      .map(mc => TopSellingCountries(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }

  def topSellingColours(sales: Seq[Sale],
                       saleFilter: SaleFilter): Seq[TopSellingColours] = {

    topSellings(sales, saleFilter, _.colour)
      .map(mc => TopSellingColours(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }

  def topSellingStyles(sales: Seq[Sale],
                       saleFilter: SaleFilter): Seq[TopSellingStyles] = {

    topSellings(sales, saleFilter, _.style)
      .map(mc => TopSellingStyles(mc._1, mc._2)).toSeq.sortBy(_.count).reverse
  }



  private def topSellings[T](sales: Seq[Sale], saleFilter: SaleFilter, groupByF: Sale => T): Map[T, Int] = {
    filterSales(sales, saleFilter)
      .groupBy(groupByF)
      .mapValues(calculateCount)
  }



  def filterSales(sales: Seq[Sale], saleFilter: SaleFilter): Seq[Sale] = {
    sales
      .toStream
      .filter(s => saleFilter.orderDate.fold(true)(_ == s.orderDate))
      .filter(s => saleFilter.deliveryCountry.fold(true)(_ == s.deliveryCountry))
      .filter(s => saleFilter.manufacturer.fold(true)(_ == s.manufacturer))
      .filter(s => saleFilter.gender.fold(true)(_ == s.gender))
      .filter(s => saleFilter.size.fold(true)(_ == s.size))
      .filter(s => saleFilter.colour.fold(true)(_ == s.colour))
      .filter(s => saleFilter.style.fold(true)(_ == s.style))
  }

  private def calculateCount(sales: Seq[Sale]): Int = {
    sales.foldLeft(0)((acc, s) => acc + s.count)
  }


}
