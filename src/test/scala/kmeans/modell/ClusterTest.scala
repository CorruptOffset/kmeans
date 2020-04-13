package kmeans.modell

import java.util.concurrent.TimeUnit

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContext}

class ClusterTest extends AnyWordSpec with Matchers {

  val duration: FiniteDuration = FiniteDuration(10,TimeUnit.SECONDS)

  val max = 9
  val seeds: Seq[Seed] = (0 to max).map{ i =>

    val d1 = i
    val d2 = i + 10
    val d3 = i + 100

    Seed(d1,d2,d3)
  }

  "Cluster" should{

    "calc centroid correctly" in {

      val expectedSeed = Seed(4.5,14.5,104.5)
      
      val cluster = Cluster.withCentroid(seeds.head)

      seeds.foreach( i => cluster.seeds.append(i))

      cluster.seeds.size shouldBe (max + 1)

      val future = Cluster.calcNewCentroid(cluster)(ExecutionContext.global)

      val result = Await.result(future,duration)

      result.currentCentroid.get.value shouldEqual expectedSeed.value
      println(cluster)
    }

    "clusters with same elements have same hashcode" in {
      val cluster1 = new Cluster()
      val cluster2 = new Cluster()

      seeds.foreach{seed =>
        cluster1.seeds.append(seed)
        cluster2.seeds.append(seed)
      }

      cluster1.hashCode() shouldEqual  cluster2.hashCode()

    }
  }

}
