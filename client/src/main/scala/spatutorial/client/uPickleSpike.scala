package spatutorial.client

import java.lang.Long
import java.util.Date

import prickle._
import spatutorial.shared.Sale

sealed trait Fruit
case class Apple(isJuicy: Boolean) extends Fruit
case class Lemon(sourness: Double) extends Fruit
case class FruitSalad(components: Seq[Fruit]) extends Fruit
case object TheDurian extends Fruit

object Example extends App {

  import upickle.default._
  println(write(12.5: Double))

  import upickle.Js
  implicit val date2Writer = upickle.default.Writer[Date]{
    case t => Js.Num(t.getTime)
  }
  implicit val date2Reader = upickle.default.Reader[Date]{
    case Js.Num(n) =>
      new Date(n.toLong)
  }
//  object Date{
//  }

  private val sale = Sale(new Date(), "Iran", "Albasco", "M", "12", "red", "Stle", 22)
  println(write(sale))

  val s = read[Sale]("""{"orderDate":1486504003199,"deliveryCountry":"Iran","manufacturer":"Albasco","gender":"M","size":"12","colour":"red","style":"Stle","count":22}""")

  println(s)
  val ss = read[Seq[Sale]]("""[{"orderDate":1453420800000,"deliveryCountry":"Ice Land","manufacturer":"Wrangled Jeans","gender":"M","size":"54","colour":"Blue","style":"Boot leg","count":41},{"orderDate":1454284800000,"deliveryCountry":"Germany","manufacturer":"The Hipster Jeans Company","gender":"M","size":"28","colour":"Blue","style":"Boot leg","count":65},{"orderDate":1458691200000,"deliveryCountry":"Ice Land","manufacturer":"Zara","gender":"F","size":"42","colour":"Yellow","style":"Long Sleeve","count":24},{"orderDate":1459378800000,"deliveryCountry":"Germany","manufacturer":"Zara","gender":"F","size":"18","colour":"Yellow","style":"Long Sleeve","count":44},{"orderDate":1461970800000,"deliveryCountry":"Ice Land","manufacturer":"Wrangled Jeans","gender":"M","size":"41","colour":"White","style":"Long Sleeve","count":21},{"orderDate":1464130800000,"deliveryCountry":"Ireland","manufacturer":"Zara","gender":"F","size":"62","colour":"Red","style":"Short Sleeve","count":51},{"orderDate":1472770800000,"deliveryCountry":"United Kingdom","manufacturer":"Lee","gender":"M","size":"38","colour":"Red","style":"Relaxed","count":33},{"orderDate":1474412400000,"deliveryCountry":"United Kingdom","manufacturer":"Wrangled Jeans","gender":"F","size":"64","colour":"Yellow","style":"Skinny","count":42},{"orderDate":1477872000000,"deliveryCountry":"Sweden","manufacturer":"Denzil Jeans","gender":"F","size":"46","colour":"White","style":"Boot leg","count":61},{"orderDate":1479772800000,"deliveryCountry":"Italy","manufacturer":"Zara","gender":"M","size":"62","colour":"Black","style":"Relaxed","count":19}]""")
  println(ss)

  println(write(ss))






  println("\n1. No preparation is needed to pickle or unpickle values whose static type is exactly known:")

  val apples = Seq(Apple(true), Apple(false))
  val pickledApples = Pickle.intoString(apples)
  val rehydratedApples = Unpickle[Seq[Apple]].fromString(pickledApples)


  println(s"A bunch of Apples: ${apples}")
  println(s"Pickled apples: ${pickledApples}")
  println(s"Rehydrated apples: ${rehydratedApples}\n")


  println("2. To pickle a class hierarchy (aka 'Sum Type'), create a CompositePickler and enumerate the concrete types")

  //implict defs/vals should have an explicitly declared type to work properly
  implicit val fruitPickler: PicklerPair[Fruit] = CompositePickler[Fruit].
    concreteType[Apple].concreteType[Lemon].concreteType[FruitSalad].concreteType[TheDurian.type]

  val sourLemon = Lemon(sourness = 100.0)
  //fruitSalad's concrete type has been forgotten, replaced by more general supertype
  val fruitSalad: Fruit = FruitSalad(Seq(Apple(true), sourLemon, sourLemon, TheDurian))

  val fruitPickles = Pickle.intoString(fruitSalad)

  val rehydratedSalad = Unpickle[Fruit].fromString(fruitPickles)

  println(s"Notice how the fruit salad has multiple references to the same object 'sourLemon':\n${fruitSalad}")

  println(s"In the JSON, 2nd and subsequent occurences of an object are replaced by refs:\n${fruitPickles}")

  println(s"The rehydrated object graph doesnt contain duplicated lemons:\n${rehydratedSalad}\n")
}