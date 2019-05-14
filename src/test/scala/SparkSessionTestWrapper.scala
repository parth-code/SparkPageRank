import org.apache.spark.sql.SparkSession
//This allows the Spark Session to be initialized once for all the tests, greatly speeding up execution
trait SparkSessionTestWrapper {
  lazy val spark:SparkSession = {
    SparkSession
      .builder()
      .master("local")
      .appName("spark test example")
      .getOrCreate()
  }

  lazy val sc = spark.sparkContext.setLogLevel("OFF")
}
