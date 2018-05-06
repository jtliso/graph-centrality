import scala.io.Source
import scala.collection.mutable.HashMap
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._

object DegreeCentrality {

  def main(args: Array[String]) {
    val time0 = System.currentTimeMillis()

    val conf = new SparkConf().setAppName("degree").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)

   for(i <- 100 to 8200 by 100){
      val time0 = System.currentTimeMillis()
      var fname = "../data/random/"
      fname = fname.concat(i.toString).concat(".el")

      val graph = GraphLoader.edgeListFile(sc, fname)

      //normalization factor is 1/node_count-1
      val normFactor:Float = 1f/(graph.vertices.count()-1)
      val degrees: VertexRDD[Int] = graph.degrees.persist()

      //sort vertices on descending degree value
      val normalized = degrees.map((s => (s._1, s._2*normFactor)))
      val vertices = sc.textFile(fname).map { line =>  val
      fields = line
        .split("\t")
        (fields(0).toLong, fields(1))
      }

      val ranks = vertices.join(normalized).map {
        case (id, (verts, score)) => (verts, score)
      }

      val sorted = ranks.sortBy(- _._2)

      //print time elapsed
      val time1 = System.currentTimeMillis()
      println(s"Degree derived in ${(time1-time0)/1000.0} seconds")
    }
  }


}
