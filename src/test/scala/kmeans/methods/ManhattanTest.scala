package kmeans.methods

import kmeans.modell.Seed
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ManhattanTest extends AnyWordSpec with Matchers {


  "Manhattan method" should {
    "calc correctly" in {

      val seq1 = Seed(10, 20, 30)
      val seq2 = Seed(5, 10, 15)

      val expectedResult = 30

      val distance = Manhattan.calculate(seq1.value,seq2.value)

      distance shouldEqual expectedResult
    }
  }
}
