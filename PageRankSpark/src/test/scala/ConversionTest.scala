import org.apache.spark.sql.Row
import org.scalatest.FlatSpec

class ConversionTest extends FlatSpec with SparkSessionTestWrapper {
  import spark.implicits._
  "Dataframe" must "works for given text" in{
    //This is what the result from the xml parsing looks like
    val seqlist:List[(String, String)] = List(("Elena","John Bell"), ("Aron", "IEC"))
    val df = seqlist.toDF
    val firstRow: Row = Row("Elena","John Bell")
    assert(df.head() == firstRow)
  }
  "Rdd" must "for given dataframe be the same" in {
    //Checks if result is generated as required for passing to the next element in pagerank
    val seqlist:List[(String, String)] = List(("Elena","John Bell"), ("Aron", "IEC"))
    val df = seqlist.toDF
    val rddfromdf = df.rdd
    assert(df.head() == rddfromdf.collect().apply(0))
  }
}