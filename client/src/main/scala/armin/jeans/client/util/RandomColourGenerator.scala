package armin.jeans.client.util

import scala.util.Random


object RandomColourGenerator {

  val r = Random

  def of(n: Int): Seq[String] = (0 to n).map(_ => next)

  def next =
    s"#$randomHex$randomHex$randomHex"

  private def randomHex = {
    val x = Integer.toHexString(r.nextInt(256))
    if (x.length == 1) s"0$x" else x

  }

  def main(args: Array[String]): Unit = {
    for (i <- 0 to 10000) println(randomHex)
  }
}
