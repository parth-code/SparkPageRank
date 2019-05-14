Design:

The project is designed to ingest xml using dtd.
1) The xml is taken by databricks. I can resolve only article tags because using union on more tags
    caused issues with mismatch.
2) Certain rows have (null, null). These are filtered out.
3) The result is then converted into a RDD and flatmapped. The authors are in the form of a wrappedarray, which is converted to a list. Since these are of the form (name, orcid), the name is extracted. The publisher is taken from the second element of RDD. Both are combined into a single list. Cartesian product is taken of these elements both in forward and reverse and this is passed to the links RDD, which consists of tuples. Duplicates are not allowed.
4) The algorithm used is at the link: https://github.com/abbas-taher/pagerank-example-spark2.0-deep-dive/blob/master/SparkPageRank.scala
    It has been modified for this data and works as follows:
    1. Each row of the RDD is converted to a tuple consisting of the source and destination link.(from step 3)
        Then distinct destination links are combined in a seq(String), which serves as value and source as the key.
    2. Each source link is taken in a map, and it is given an initial page rank value of 1.0.
    3. The links and ranking maps are combined and a flatmap is created, which provides scores for each destination link.
        Each destination link gets a score, which is the ranking of source / no of outgoing links.
    4. The score is then updated using the formula, (alpha)/n * 1 + (1-alpha)* previous_score.
        alpha is a value which is given. n is the number of values. Since here, we take each value individually
        instead of as a matrix, it equals 1.
    5. The step 4 is repeated multiple times (depending on the value of iterations defined in application.conf).
    6. The rank scores are updated and then outputted to a file.

Logging:
Done using log4j, which comes with spark. Set the variable in the main class file to other values if required more to print in console.

Tests:
There are 6 tests in total, across 6 suites. I created 2 tests in ConversionTest beforehand, to test the rdd to Dataframe conversions. Since they are used in the program, I left them there. There are 4 tests in PageRankTest. These are involving components of the PageRank algorithm. The tests use a simpler form of the input generated here, to test the algorithm.
I have used the SparkSessionTestWrapper trait, since it allow the spark session to be initialized just once, allowing for faster testing.
There are 2 warnings coming up regarding unchecked variables, which I am unable to suppress.

Limitations:
The main limitation is that the program is not able to take on other tags of the dblp.xml file due to the certain issues with mismatching data types.
