# spark-clipboard

Allows loading the contents of clipboard as a Spark data frame. This is targeted for testing and e.g. Stack Overflow users. Given a printed data frame
```
+----+----+
|foo |bar |
+----+----+
|   1|   a|
|   2|   b|
+----+----+
```
it requires some overhead to load that as a spark data frame.

This project contains two components to help developers.
1. A hadoop file format that can read clipboard. This allows reading the contents without storing it as an intermediate file.
2. A Spark input format that can parse the `Dataset.show` output string as a data frame. The idea is based on discussion in this thread https://stackoverflow.com/questions/48427185/how-to-make-good-reproducible-apache-spark-examples/48454013#48454013

The file system needs to be registered when creating the Spark session:

```
val spark = SparkSession
  .builder()
  .master("local")
  .config("fs.clipboard.impl", "com.github.ollik1.clipboard.ClipboardFileSystem")
  .getOrCreate()
```

After this, it is possible to create data frames from copied print outputs:
```
val df = spark.read
  .format("com.github.ollik1.clipboard")
  .load("clipboard:///*")
```

Naturally, the components are independent from each other and it is possible to read files containing Spark show outputs or read pure CSV or any other supported format from clipboard.