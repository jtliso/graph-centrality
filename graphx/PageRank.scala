import org.apache.spark.graphx.GraphLoader
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object PageRank {
  def main(args: Array[String]) {
    val time0 = System.currentTimeMillis()

    val conf = new SparkConf().setAppName("informationRetrieval2016").setMaster("local").set("spark.driver.allowMultipleContexts", "true")
    val sc = new SparkContext(conf)
    val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    // Load the edges as a graph
    val graph = GraphLoader.edgeListFile(sc, "../data/bio-yeast.el")

    val ranks = graph.pageRank(0.0001).vertices
    // Join the ranks with the usernames
    val users = sc.textFile("../data/bio-yeast.el").map { line =>  val
    fields = line
      .split("\t")
      (fields(0).toLong, fields(1))
    }
    val ranksByUsername = users.join(ranks).map {
      case (id, (username, rank)) => (username, rank)
    }

    val sorted = ranksByUsername.sortBy(- _._2)
    val time1 = System.currentTimeMillis()
    println(s"Executed in ${(time1-time0)/1000.0} seconds")
    
    //print top 10 vertices
    for ((vertexId, degree) <- sorted.take(10)){
      println(s"User: ${vertexId} has a pagerank degree of ${degree}")
    }

 }


}
