package kmeans.methods

object Manhattan extends DistanceCalculatorMethod {

  override def calculate(first: Seq[Double], second: Seq[Double]): Double = {
    val tupleList = first zip second
    val distance =
      tupleList
        .map(x => Math.abs(x._1 - x._2))
        .sum
    distance
  }

  override def printReadable: String = "|a - b|"
}
