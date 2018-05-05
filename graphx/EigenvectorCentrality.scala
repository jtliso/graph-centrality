import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph,GraphLoader, VertexRDD}
import org.apache.spark.{SparkConf, SparkContext}

object EigenvectorCentrality {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local").set("spark.driver.allowMultipleContexts", "true")

    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    for(i <- 100 to 8200 by 100){
      val time0 = System.currentTimeMillis()
      var fname = "../data/random/"
      fname = fname.concat(i.toString).concat(".el")
      val graph = GraphLoader.edgeListFile(sc, fname)
      val nodeNumber = graph.numVertices
      val previousValue = graph.mapVertices((vId, eigenvalue) => 1.0 / nodeNumber)
      val zeroValue = graph.mapVertices((vId, eigenvalue) => 0.0)
      var iter = 1000
      var oldValue = previousValue
      var absolute = oldValue
      var newVertices = previousValue.vertices
      var convergence = 10d
      var flag = true
      var oldGraph = graph.outerJoinVertices(zeroValue.vertices) { (vid, deg, eigenValue) => eigenValue.getOrElse(0.0)
      }.mapEdges( e => e.attr.toDouble)
      do{
          //previousValue.vertices.sortBy(_._1).collect.foreach(v => println(v._1 , v._2))
          //previousValue.vertices.collect.foreach(v => println(v._2))
          val outDegrees: VertexRDD[Int] = graph.outDegrees
          if (flag==false) {oldValue = zeroValue}else{flag=false}
          var rankGraph = oldValue
            .outerJoinVertices(graph.outDegrees) { (vid, eigenvalue, deg) => deg.getOrElse(0)}
            // Set the weight on the edges based on the outdegree
            .mapTriplets(e => 1.0 / e.srcAttr)
            .outerJoinVertices(newVertices) { (vid, deg, eigenValue) => eigenValue.getOrElse(0.0) }
          newVertices = rankGraph.aggregateMessages[(Double)](
              triplet => { // Map Function
                    //calculate how much each vertex "contributes" to the destination vertex
                    triplet.sendToDst(triplet.srcAttr*triplet.attr)
                  },
                  // Add all vertices old eigenvalues of inVertices to sum the eigenvalue of each vertex
                  (a, b) => (a + b) // Reduce Function
          )
          rankGraph = rankGraph.outerJoinVertices(newVertices){ (vid, oldvalue, newvalue) => newvalue.getOrElse(0) }
          iter -= 1
          //calculate convergence as the sum of absolute differences of old and new eigenvalue of each vertex
          convergence = oldGraph
              .outerJoinVertices(rankGraph.vertices){(vid, oldvalue, newvalue)=> math.abs(newvalue.get-oldvalue)}
            .vertices.map(x => x._2).sum()
          oldGraph = rankGraph
          //println(s"Convergence is at ${convergence}")
          if (iter==0){
            //after the last iteration print the top 10 eigenvector centrality values and the attributes of some edges
            rankGraph.vertices.sortBy(-_._2).take(10).foreach(v => println(v))
            rankGraph.edges.take(10).foreach{println(_)}
          }
    }while (convergence > 0.00015 && iter!=0)
     val time1 = System.currentTimeMillis()
     println(s"Executed in ${(time1-time0)/1000.0} seconds")
    }
  }
}
