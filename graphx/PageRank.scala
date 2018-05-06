import org.apache.spark.graphx.GraphLoader
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object PageRank {
  def main(args: Array[String]) {
    val time0 = System.currentTimeMillis()

    val conf = new SparkConf().setAppName("pagerank").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)


    var l = List[Double]()

    for( i <- 100 to 8200 by 100){
      val time0 = System.currentTimeMillis()
      var fname = "../data/random/"
      fname = fname.concat(i.toString).concat(".el")

      // Load the edges as a graph
      val graph = GraphLoader.edgeListFile(sc, fname)

      val ranks = graph.pageRank(0.0001).vertices

      // Join the ranks with the usernames
      val vertices = sc.textFile(fname).map { line =>  val
      fields = line
        .split("\t")
        (fields(0).toLong, fields(1))
      }

      val ranks = vertices.join(ranks).map {
        case (id, (vname, rank)) => (vname, rank)
      }

      val sorted = ranksByUsername.sortBy(- _._2)
      val time1 = System.currentTimeMillis()
      val run = (time1-time0)/10000.0
      println(s"PageRank derived in ${run} seconds")
      l ::= run    
   }
 }


}
