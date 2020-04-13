package kmeans.methods

object Euclidian extends DistanceCalculatorMethod {

  val POWER_FACTOR = 2

  override def calculate(first: Seq[Double], second: Seq[Double]): Double = {
    val tupleList = first zip second
    val distance =
      tupleList
        .map(x => Math.pow(x._1 - x._2,POWER_FACTOR))
        .sum
    Math.sqrt(distance)
  }

  override def printReadable: String = "root( (a - b)² )"
}