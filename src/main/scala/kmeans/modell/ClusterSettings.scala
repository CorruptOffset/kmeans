package kmeans.modell

object ClusterSettings{

  def withTargetCount(count: Int): ClusterSettings = ClusterSettings(None,count)

  def withInitCentroids(initialSeeds: List[Seed]): ClusterSettings = {
    val targetClusterCount = initialSeeds.size
    // create initialClusters with Centroids
    val initialClustersWithCentroid = initialSeeds.map(Cluster.withCentroid)
    ClusterSettings(Some(initialClustersWithCentroid),targetClusterCount)
  }
}

case class ClusterSettings private(initClusterWithCentroidOption: Option[List[Cluster]],
                                   targetClusterCount: Int,
                                   maxIterationsOption: Option[Int] = None)
