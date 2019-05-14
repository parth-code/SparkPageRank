### **PageRank on the DBLP dataset using Spark**

#### Installation requirements:

1) Spark 2.4.x

2) Hadoop 3.x.x

3) Java 8

4) Scala 2.1x.x

5) Sbt version 2.1x.x

I installed hadoop and spark on windows and thus, did not need cloudera or hortonworks for executing my jars.

The algorithm is modified from the implementation at 
  `https://github.com/abbas-taher/pagerank-example-spark2.0-deep-dive/blob/master/SparkPageRank.scala`

#### How to run:

1)Get the **dblp.xml** and **dblp.dtd** file from https://dblp.uni-trier.de/. Place both in same folder.

2)Go to the project folder and run the following:
  `sbt clean compile`

3)Method 1: Without jar

    1) `sbt run <input-file-location> <output-file-location>` in terminal(cmd or in Intellij). 
    
    Use absolute paths and '\\' instead of '\'(Windows). 
    
    If running using Intellij, add the argument -Xmx6000m in Run-> Edit Configuration-> VM Options
    
    This increases memory allocation to the VM.
  
  Method 2: Using jar
    
    1) Create the fat jar using
      ~sbt assembly
    
    2) use
      `spark-submit --class prtest pagerank.jar <input-file-location> <output-file-location>`

4) The output folder will have files called **part-xxxxx**. Open as a text file. This is the required result.

Tests can be run using `sbt clean compile test`

##### Output Format:
The output generated will be of the form

>  (University of Paris-Sud, Orsay, France,0.8154981739701659)

>  (Elena,0.9850243302878131)

>  (John Bell,1.4621033282930214)

>  (University of Nice Sophia Antipolis, France,0.5678480111313514)

>  (Joseph Fourier University, Grenoble, France,0.8154981739701659)

>  (Elena Zheleva,1.3690036520596678)

>  (Acta Inf.,0.9850243302878131)
