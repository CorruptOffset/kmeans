package kmeans.methods

import kmeans.modell.Seed
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec



class EuclidianTest extends AnyWordSpec with Matchers {

  "Euclidian method" should {
    "calc correctly" in {

      val seq1 = Seed(10, 20, 30)
      val seq2 = Seed(5, 10, 15)

      val expectedResult = Math.sqrt(350)

      val distance = Euclidian.calculate(seq1.value,seq2.value)

      distance shouldEqual expectedResult
    }
  }

}
