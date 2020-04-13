package kmeans.process

import java.util.concurrent.atomic.AtomicInteger

import kmeans.methods.DistanceCalculatorMethod
import kmeans.modell.{Cluster, ClusterSettings, Seed}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

object Kmeans{
  type IterationsResult = (Boolean,Set[Cluster])
  type CheckFunction = (Boolean) => Boolean

  private val MIN_CLUSTER_COUNT = 3

  private case class ClosestCluster(cluster: Cluster, distance2Cluster: Double)

}

class Kmeans(seedsSet: Set[Seed],
             clusterSettings: ClusterSettings,
             distanceCalculator: DistanceCalculatorMethod) {

  import Kmeans._

  //iteration counter
  private val counter = new AtomicInteger()

  private val targetClusterCount = clusterSettings.targetClusterCount
  require(targetClusterCount >= MIN_CLUSTER_COUNT)

  private var endOfAlogrithemCheck: CheckFunction = _

  // root call for K-Means
  def process(implicit executionContext: ExecutionContext): Future[Set[Cluster]] = {

    setCheckFunction()

    val clusters: Set[Cluster] =
      clusterSettings.initClusterWithCentroidOption match{
        case Some(clusters) => clusters.toSet
        case None           => Cluster.fillWithRandom(seedsSet,targetClusterCount)
      }

    // init clusters with seeds
    val kMeansResultFuture = for{
      lclusters         <- addSeedsToClosestClusters(seedsSet.toBuffer,clusters)
      clusterFuture     <- applyKMeans(lclusters)
    }yield clusterFuture

    kMeansResultFuture.onComplete( _ =>  println(s"Iterations needed: ${counter.intValue()}") )
    kMeansResultFuture
  }

  def applyKMeans(clusters: Set[Cluster])
                 (implicit executionContext: ExecutionContext): Future[Set[Cluster]] = {

    // calc centroids
    val calcFutures = clusters.map(Cluster.calcNewCentroid(_))

    val iterationFuture = for{
      _                   <- Future.successful( counter.incrementAndGet())
      result              <- Future.sequence(calcFutures) // in parallel
      moveSeedsToClusters <- moveSeedsToClusters(result)
    }yield moveSeedsToClusters

    // loop over results till cluster wont change
    iterationFuture.flatMap{ result =>
      val clusterChanged = result._1
      val resultCluster = result._2
      if(endOfAlogrithemCheck(clusterChanged))
        applyKMeans(resultCluster)
      else
        Future.successful(resultCluster)
    }
  }


  private def moveSeedsToClusters(lCluster: Set[Cluster])
                                 (implicit executionContext: ExecutionContext): Future[IterationsResult] = {

    for{
      //remember the hashcodes to compare them later
      origHashCodes  <- Future(lCluster.map(_.hashCode()))

      // clear seeds in cluster and create a copy
      allSeeds       <- Future(lCluster.flatMap(_.clear).toBuffer)

      // add seed to the closest cluster/centroid
      result         <- addSeedsToClosestClusters(allSeeds,lCluster)

      //get hashcodes after adding seeds
      afterMvCodes   <- Future(result.map(_.hashCode()))

      // return false if a cluster changed
      clusterChanged <- Future(!origHashCodes.iterator.sameElements(afterMvCodes.iterator))

    }yield (clusterChanged,result)
  }

  private def findSmallestDistanceToCluster(currentSeed: Seed, clusters: Set[Cluster]): ClosestCluster = {

    clusters.map { cluster: Cluster =>
      // at this point we always should have a centroid
      val centroidValue: Seq[Double] = cluster.currentCentroid.get.value
      val currentDistance = calcDistance2Centroid(centroidValue,currentSeed.value)
      ClosestCluster(cluster,currentDistance)
    }.minBy(_.distance2Cluster)
  }


  private def calcDistance2Centroid(centroid: Seq[Double], seed: Seq[Double]): Double =
    distanceCalculator.calculate(centroid,seed)


  private def addSeedsToClosestClusters(seeds: mutable.Iterable[Seed], clusters: Set[Cluster])
                                       (implicit executionContext: ExecutionContext): Future[Set[Cluster]] = {

    //map over each seed and calc distance to each cluster
    val clusterMap = seeds.map { currentSeed =>
        val closest = findSmallestDistanceToCluster(currentSeed, clusters)
        (closest,currentSeed)
    }.groupBy(_._1.cluster.clusterId)

    //append the seed to its current cluster
    val appendToClustersFuture = clusterMap.keySet.map{ key =>
      Future{
        clusterMap(key).map(x => x._1.cluster.seeds.append(x._2))
      }
    }
    //append to cluster in paralell
    Future.sequence(appendToClustersFuture)
      .map( _=> clusters) // just return clusters
  }

  private def withMaxIterationCheck(maxInt: Int): CheckFunction = { clusterChanged =>
    clusterChanged && (counter.get <= maxInt)
  }
  private def tillReady(): CheckFunction = clusterChanged => clusterChanged

  private def setCheckFunction(): Unit = {
    val function: CheckFunction = clusterSettings.maxIterationsOption match {
      case Some(maxIteration) => withMaxIterationCheck(maxIteration)
      case None               => tillReady()
    }
    endOfAlogrithemCheck = function
  }

}
