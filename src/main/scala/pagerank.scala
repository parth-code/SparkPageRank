import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}

import scala.collection.JavaConverters._

object pagerank {
  val config = ConfigFactory.load("application.conf")
  val logger1 = LogManager.getRootLogger
  val logger = LogManager.getLogger("PageRankSpark")
  //  Defines level of the rootLogger, ie. the spark outputs.
  //  Changing this can cause an avalanche of logs. Use with caution
  logger1.setLevel(Level.WARN)
  //Defines level of the user logging.
  logger.setLevel(Level.DEBUG)

  def main(args: Array[String]): Unit = {
    System.setProperty("entityExpansionLimit", "10000000")
//    val path = config.getString("xmlFile")
//    Creating sparksession

    val proflist: List[String] = config.getStringList("prof_list")
      .asScala
      .toList
    val taglist: List[String] = config.getStringList("tag_list")
      .asScala
      .toList
//  Initializing spark
    val spark = SparkSession
      .builder
      .config("spark.master", "local[*]") //This property is to be set to yarn while running on AWS
      .getOrCreate()
    logger.debug("Started sparksession")
//  Used for lower level stuff like parallelize
    val sc = spark.sparkContext

//  Getting all the article tags
    val articledf = spark
      .read
      .format("com.databricks.spark.xml")
      .option("rowTag", "article")
      .load(args(0))
      .select("author", "journal")
      .toDF("name", "publ")

////  Getting all the inproceeding tags
//    val inproceedingsdf = spark
//      .read
//      .format("com.databricks.spark.xml")
//      .option("rowTag", "inproceedings")
//      .load(args(0))
//      .select("author", "booktitle")
//      .toDF("name", "publ")
//
////  Getting all the book tags
//    val bookdf = spark
//      .read
//      .format("com.databricks.spark.xml")
//      .option("rowTag", "book")
//      .load(args(0))
//      .select("author", "publisher")
//      .toDF("name", "publ")
//
////  Combines them together
    val resultdf = articledf
//      .union(inproceedingsdf)
//      .union(bookdf)

//    def childEquals(value: List[String], names:List[String]):Boolean = {
//      val range = names.indices.toList
//      range.foreach( valueIndex =>
//        names.foreach(auth =>
//          if(auth.equals(value(valueIndex)))
//            return true))
//      false
//    }

//Getting cartesian product of all elements in list without duplicates
    def crossInList(value: List[Any]): List[(Any, Any)] = {
      val uniquePairs = for {
        (x, idxX) <- value.zipWithIndex
        (y, idxY) <- value.zipWithIndex
        if idxX < idxY
      } yield (x, y)
      uniquePairs
    }
//  All null values are deleted
    val result = resultdf.filter( articledf.col("name").isNotNull)

    val links: RDD[(Any, Iterable[Any])] = result.rdd.flatMap{
      case(arr) =>
        val authors:List[Any] = arr.getSeq(0).toList
        val authorlist = authors.map{case(x:Row) => x(0)}
        val publishers = arr.get(1)
        val totalList: List[Any] = authorlist :+ publishers

        crossInList(totalList) ::: crossInList(totalList.reverse)
    }
      .distinct()
      .groupByKey()
      .cache()

    //    links.foreach(println(_))
    logger.debug("Created list of authors and publications")


//  These are required to resolve a particular problem with java.io.ioexception: no filesystem for scheme file
    val hadoopConfig: Configuration = spark.sparkContext.hadoopConfiguration

    hadoopConfig.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)

    hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)

//  Sparkconfig required for lower level stuff

    logger.debug("Created rdd")
//  Pagerank code used here

    val iters = config.getInt("iter")
//  Sets pagerank scores of all keys to 1.0

    var ranks = links.mapValues(v => 1.0)
//    Finds actual scores for all nodes over 10 iterations.

    (0 until iters).foreach { i =>
//    rank / no of outgoing links
      val contribs = links.join(ranks).values.flatMap{ case (urls, rank) =>
        val size = urls.size
        urls.map(url => (url, rank / size))
      }
//  taking alpha = 0.15 for alpha*1 + (1-alpha)*each node's score
      ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
    }
    logger.debug("Completed pagerank iterations")
    val output:Array[(Any, Double)] = ranks.collect()
//  Creating RDD from output array and saving it in a file.
    sc.parallelize(output).saveAsTextFile(args(1))//"D:\\441Homework\\PageRankSpark\\result")
    logger.debug("completed writing to file")
    spark.stop()
  }
}
