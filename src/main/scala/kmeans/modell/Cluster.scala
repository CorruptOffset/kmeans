package kmeans.modell

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}


object Cluster{
  def withCentroid(seed: Seed): Cluster =
    new Cluster()
      .centroid(seed)

  def calcNewCentroid(cluster: Cluster) (implicit executionContext: ExecutionContext): Future[Cluster] = Future{

    val lSeeds = cluster.seeds

    if(lSeeds.nonEmpty){
      val dims = lSeeds.head.dimensions
      val rawCentroid = lSeeds.foldLeft(Seed.empty(dims))(sum2Seeds)
      val correctedValues = rawCentroid.value.map(_ / lSeeds.size)
      cluster.centroid( Seed(correctedValues: _*))
    }else{
      println(s"$this has no seeds to calc a centroid")
    }

    def sum2Seeds: (Seed,Seed) => Seed = (first: Seed, second: Seed) => {
      val summedSeed =
        first.value.zip(second.value)
          .map{ tuple: (Double, Double) => tuple._1 + tuple._2 }
          .toArray
      Seed(summedSeed: _*)
    }
    cluster
  }

  def fillWithRandom(seeds: Iterable[Seed],targetClusterCount: Int): Set[Cluster] = {
    Seed.randomSeeds(seeds,targetClusterCount).map(Cluster.withCentroid).toSet
  }

}

class Cluster() {

  // hold seeds here within cluster
  val seeds: mutable.Buffer[Seed] = ArrayBuffer.empty[Seed]

  //simple unique id
  val clusterId: String = s"${IdUtil.nextIntCode}"

  //centroid of the cluster
  var currentCentroid: Option[Seed] = None

  def centroid(newCentroid: Seed): Cluster = {
    currentCentroid = Some(newCentroid)
    this
  }

  // clear clusters internal list and return copy of collection
  def clear: mutable.Iterable[Seed] = {
    val orig = seeds.clone()
    seeds.clear()
    orig
  }

  override def toString: String =
    s"""
       |ClusterId: $clusterId
       |Size: ${seeds.size}
       |Centroid: $currentCentroid
       |Hashcode: $hashCode
       |""".stripMargin

  //needed to check for changes within cluster
  override def hashCode(): Int = seeds.hashCode()
}
