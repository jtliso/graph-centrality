import org.apache.spark.graphx.{EdgeRDD, GraphLoader, VertexRDD,lib}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object ClosenessCentrality {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("closeness").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)


    for (i <- 100 to 8200 by 100){
      val time0 = System.currentTimeMillis()
      var fname = "../data/random/1000.el"
      fname = fname.concat(i.toString).concat(".el")

      val graph = GraphLoader.edgeListFile(sc, fname)
      val vertexSequence = graph.vertices.map(v => v._1).collect().toSeq
      val shorts = lib.ShortestPaths.run(graph, vertexSequence)
 
      val closeness = shorts.vertices.map(vertex => (vertex._1 , 1f/vertex._2.values.sum))
      val time1 = System.currentTimeMillis()
      val run = (time1-time0)/10000.0
      println(s"Closeness derived in ${run} seconds")
	  
      //sort vertices on descending degree value
      val sorted = closeness.sortBy(- _._2)
  }
  }

}
