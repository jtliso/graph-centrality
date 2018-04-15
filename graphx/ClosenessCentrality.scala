import org.apache.spark.graphx.{EdgeRDD, GraphLoader, VertexRDD,lib}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object ClosenessCentrality {
  def main(args: Array[String]) {
    val time0 = System.currentTimeMillis()

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    val graph = GraphLoader.edgeListFile(sc, "/C:/Users/JT/Documents/CS/CS560/graphx/data/bio-yeast.el")

    //create a new RDD with just VertexId to be used for shortest paths
    // algorithm
    val vertexSeq = graph.vertices.map(v => v._1).collect().toSeq

    val spgraph = lib.ShortestPaths.run(graph, vertexSeq)
    val closeness = spgraph.vertices.map(vertex => (vertex._1 , 1f/vertex._2
      .values.sum))

    //sort vertices on descending degree value
    val sorted = closeness.sortBy(- _._2)

    //print time elapsed
    val time1 = System.currentTimeMillis()
    println(s"Executed in ${(time1-time0)/1000.0} seconds")

    //print top 10 vertices
    for ((vertexId, closeness) <- sorted.take(10)){
      println(s"Vertex with id ${vertexId} has a closeness degree of ${closeness}")
    }
  }

}
