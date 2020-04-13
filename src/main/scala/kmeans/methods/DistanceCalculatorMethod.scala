package kmeans.methods

trait DistanceCalculatorMethod {
  def calculate(first: Seq[Double], second: Seq[Double]): Double

  def printReadable: String
}


