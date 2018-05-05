import org.apache.spark.graphx.{EdgeRDD, GraphLoader, VertexRDD,lib}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object ClosenessCentrality {
  def main(args: Array[String]) {
    //val time0 = System.currentTimeMillis()

    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)


    //for (i <- 100 to 8200 by 100){
      val time0 = System.currentTimeMillis()
      var fname = "../data/random/1000.el"
      //fname = fname.concat(i.toString).concat(".el")

      val graph = GraphLoader.edgeListFile(sc, fname)
	  var time1 = System.currentTimeMillis()
	  var run = (time1-time0)/10000.0
      println(s"Graph loaded in ${run} seconds")
	  
	  

      //create a new RDD with just VertexId to be used for shortest paths
      val vertexSeq = graph.vertices.map(v => v._1).collect().toSeq
      val spgraph = lib.ShortestPaths.run(graph, vertexSeq)
	  time1 = System.currentTimeMillis()
	  run = (time1-time0)/10000.0
      println(s"Shortest paths calc'd in ${run} seconds")
	  
 
      val closeness = spgraph.vertices.map(vertex => (vertex._1 , 1f/vertex._2
      .values.sum))
	  time1 = System.currentTimeMillis()
	  run = (time1-time0)/10000.0
      println(s"Closeness derived in ${run} seconds")
	  
      //sort vertices on descending degree value
      val sorted = closeness.sortBy(- _._2)

      //print time elapsed
      time1 = System.currentTimeMillis()
      run = (time1-time0)/10000.0
      println(s"Sorted in ${run} seconds")
  //}

    //print top 10 vertices
   // for ((vertexId, closeness) <- sorted.take(10)){
   //   println(s"Vertex with id ${vertexId} has a closeness degree of ${closeness}")
    //}
  }

}
