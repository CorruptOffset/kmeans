package kmeans.modell

import java.util.concurrent.atomic.AtomicInteger

private[kmeans] object IdUtil {
  protected val DEFAULT_LOD_FACTOR = 0

  private val counter =  new AtomicInteger()

  def nextIntCode: Int = counter.incrementAndGet

  def count = counter.get()

}
