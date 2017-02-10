package util

import org.scalatest.{FunSpec, Matchers}

/**
  * Created by armin.
  */
class RandomColourGeneratorSpec extends FunSpec with Matchers {
  describe("Random colours") {
    it("should generate valid colours") {
      for{ i <- 1 to 10000 }
        RandomColourGenerator.next should have length 7
      }
    }

}
