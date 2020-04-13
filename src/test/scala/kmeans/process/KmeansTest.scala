package kmeans.process


import java.util.concurrent.{Executors, TimeUnit}

import kmeans.methods.{Euclidian, Manhattan}
import kmeans.modell.{Cluster, ClusterSettings, Seed}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
import scala.util.Random



class KmeansTest extends AnyWordSpec with Matchers  {

  val context = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(12))


  val duration = FiniteDuration(15,TimeUnit.MINUTES)


  "This test" should {

    "return same  results" in {

      val max = 10000

      val targetCluster = 3

      val seeds = (0 to max).map{ i =>

        val d1 = i
        val d2 = i + 10
        val d3 = i + 100
        val d4 = i + 1000
        //val d5 = i + 1000

        Seed(d1,d2,d3,d4)//,d4)
      }

      // take first X Seeds as Clustercentroids
      val firstClusters = seeds.take(targetCluster).map(Cluster.withCentroid).toList
      val settings = ClusterSettings(Some(firstClusters),targetCluster,maxIterationsOption = Some(20))

      //Method to use for calc distance between seeds
      val method = Euclidian

      val ta = System.nanoTime()
      val kmeans = new Kmeans(seeds.toSet,settings, method)
      val tb = System.nanoTime()
      println("Elapsed time init: " + (tb - ta)/ 1000000000.0 + "s")

      val t0 = System.nanoTime()

      val clusterFuture = kmeans.process(context)
      val result: Set[Cluster] = Await.result(clusterFuture,duration)
      result.size shouldBe settings.targetClusterCount

      val t1 = System.nanoTime()
      println("Elapsed time: " + (t1 - t0) / 1000000000.0 + "s")
    }


    "heavy load" in {

      val settings = ClusterSettings(None,3)

      val max = 10000
      def randomIndex = Random.nextInt(max)
      val seeds = (0 to max).map{ _ =>

        val d1 = randomIndex.toDouble
        val d2 = randomIndex.toDouble
        val d3 = randomIndex.toDouble
        val d4 = randomIndex.toDouble
        val d5 = randomIndex.toDouble

        Seed(d1,d2,d3,d4,d5)
      }

      seeds.head.dimensions shouldBe 5

      val ta = System.nanoTime()
      val kmeans = new Kmeans(seeds.toSet,settings, Manhattan)
      val tb = System.nanoTime()
      println("Elapsed time init: " + (tb - ta) + "ns")

      val t0 = System.nanoTime()

      val clusterFuture = kmeans.process(context)
      val result: Set[Cluster] = Await.result(clusterFuture,duration)
      result.size shouldBe settings.targetClusterCount

      val t1 = System.nanoTime()
      println("Elapsed time: " + (t1 - t0) + "ns")



    }
  }

}
