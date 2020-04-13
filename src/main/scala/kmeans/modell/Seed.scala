package kmeans.modell

import scala.collection.mutable
import scala.util.Random

object Seed {

  // fetch random seeds (amount based on parameter) to use this seeds as centroids on creation of empty cluster
  def randomSeeds(seeds: Iterable[Seed], targetSetSize: Int): mutable.Iterable[Seed] = {
    val seedList = seeds.toList

    val buffer = mutable.Buffer.empty[Int]
    while (buffer.size != targetSetSize){
      val randomIndex = Random.nextInt(seeds.size)
      buffer.append(randomIndex)
    }
    buffer.map(seedList(_))
  }

  // empty Seed with 0 filled
  def empty(size: Int): Seed = {
    val array = Array.ofDim[Double](size)
    Seed(array: _*)
  }

}

case class Seed(value: Double*) {
  val dimensions: Int = value.toArray.length

  val seedId: String = {
    val seq: Seq[Double] = value.toVector
    s"${seq.mkString(",")}"
  }

  override def hashCode(): Int = seedId.hashCode

  override def toString: String =
    s"""
       |Id: $seedId
       |hashCode: $hashCode
       |""".stripMargin

  override def equals(that: Any): Boolean = that match {
    case that: Seed => that.canEqual(this) && this.hashCode == that.hashCode
    case _ => false
  }
}


