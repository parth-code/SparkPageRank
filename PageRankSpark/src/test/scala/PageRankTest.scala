import org.scalatest.FlatSpec

class PageRankTest extends FlatSpec with SparkSessionTestWrapper {
  import spark.implicits._
  //To check if links are generated in form of keys and values ie. mapping

  //Unchecked warnings generated because links are of the form [(Any, Iterable[Any])]
  //Since wrapped arrays are simply arrays converted by mapping,
  // I have used a simple example to test the main function itself
  "The links" must " be generated in this fashion" in {
    val dfrdd = List(("Elena","John Bell"), ("Elena", "Acto Inf")).toDF().rdd
    val links = dfrdd
      .map{linklist =>
        (linklist(0), linklist(1))
      }
      .distinct()
      .groupByKey()
      .cache()

    assert(links.collect().head.isInstanceOf[(String, Iterable[String])])
    assert(links.collect().head == ("Elena", Iterable("John Bell", "Acto Inf")))
  }

  //Checks if the algorithm works for my example
  "The ranks" must "initialize to 1.0" in {
    val dfrdd = List(("Elena","John Bell"), ("Elena", "Acto Inf")).toDF().rdd
    val links = dfrdd
      .map{linklist =>
        (linklist(0), linklist(1))
      }
      .distinct()
      .groupByKey()
      .cache()
    //Initializes values of link keys to 1.0
    val ranks = links.mapValues(v => 1.0)
    assert(ranks.collect().head == ("Elena", 1.0))
  }

  //Checks first itersation of pagerank for a simple example.
  "Single iteration of pagerank" must "returns" in {
    val dfrdd = List(("Elena","John Bell"), ("Elena", "Acto Inf")).toDF().rdd
    val links = dfrdd
      .map{linklist =>
        (linklist(0), linklist(1))
      }
      .distinct()
      .groupByKey()
      .cache()
    //Initializes values of link keys to 1.0
    var ranks = links.mapValues(v => 1.0)
    val contribs = links.join(ranks).values.flatMap{ case (urls, rank) =>
      val size = urls.size
      urls.map(url => (url, rank / size))
    }
    ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
    assert(ranks.collect().head == ("Acto Inf",0.575))
  }

  "Cross product" must "yield correct answer"in {
    val links = List("A", "B", "C")
    def crossInList(value: List[Any]): List[(Any, Any)] = {
      val uniquePairs = for {
        (x, idxX) <- value.zipWithIndex
        (y, idxY) <- value.zipWithIndex
        if idxX < idxY
      } yield (x, y)
      uniquePairs
    }
    def result = crossInList(links)
    
  }

}
