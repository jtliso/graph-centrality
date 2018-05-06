import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph,GraphLoader, VertexRDD}
import org.apache.spark.{SparkConf, SparkContext}

object EigenvectorCentrality {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("eigen").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    for(i <- 100 to 8200 by 100){
      var iter = 1000
      var convergence = 10.0
      val time0 = System.currentTimeMillis()

      //load graph
      var fname = "../data/random/"
      fname = fname.concat(i.toString).concat(".el")
      val graph = GraphLoader.edgeListFile(sc, fname)

      //create initial values
      val init = graph.mapVertices((vId, eigenvalue) => 0.0)

      var old =  graph.mapVertices((vId, eigenvalue) => 1.0 / graph.numVertices)
      var newVertices = old.vertices
      var flag = true
      var oldGraph = graph.outerJoinVertices(init.vertices) { (vid, deg, eigenValue) => eigenValue.getOrElse(0.0) }
        .mapEdges( e => e.attr.toDouble)

      do{
          val outDegrees: VertexRDD[Int] = graph.outDegrees

          //saving the old value if it is not first time
          if (flag == false) {
            old = init
          }else {
            flag = false
          }

          var rankGraph = old.outerJoinVertices(graph.outDegrees) { (vid, eigenvalue, deg) => deg.getOrElse(0)}
            // Set the weight on the edges based on the outdegree
            .mapTriplets(e => 1.0 / e.srcAttr)
            .outerJoinVertices(newVertices) { (vid, deg, eigenValue) => eigenValue.getOrElse(0.0) }
                newVertices = rankGraph.aggregateMessages[(Double)](
                  triplet => {
                    //calculate how much each vertex contributes to the destination vertex and send (Mapping)
                    triplet.sendToDst(triplet.srcAttr * triplet.attr)
                  },
                  // Add vertices old eigenvalues of inVertices to sum the eigenvalue of each vertex (Reducing)
                  (v, u) => (v + u)
          )

          rankGraph = rankGraph.outerJoinVertices(newVertices){ (vid, old, new) => new.getOrElse(0) }

          //calculate convergence as the sum of absolute differences of old and new eigenvalue of each vertex
          convergence = oldGraph
            .outerJoinVertices(rankGraph.vertices){(vid, old, new)=> math.abs(new.get-old)}
            .vertices.map(x => x._2).sum()

          oldGraph = rankGraph
          iter -= 1
    }while (convergence > 0.00015 && iter!=0)

     val time1 = System.currentTimeMillis()
     println(s"Eigenvector derived in ${(time1-time0)/1000.0} seconds")
    }
  }
}
