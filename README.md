Source code for: https://groovy.apache.org/blog/groovy-lucene

Output for the `runMatcher` (just ignore the first line) and `runLucene` tasks:

<pre>
Total documents with hits for content:apache* content:eclipse* --> 28 hits
classifying-iris-flowers-with-deep.adoc: [eclipse deeplearning4j:5, apache commons math:1, apache spark:2]
fruity-eclipse-collections.adoc: [eclipse collections:9, apache commons math:1]
groovy-list-processing-cheat-sheet.adoc: [eclipse collections:4, apache commons collections:3]
groovy-null-processing.adoc: [eclipse collections:6, apache commons collections:4]
matrix-calculations-with-groovy-apache.adoc: [apache commons math:6, eclipse deeplearning4j:1, apache commons:1]
apache-nlpcraft-with-groovy.adoc: [apache nlpcraft:5]
community-over-code-eu-2024.adoc: [apache ofbiz:1, apache commons math:2, apache ignite:1]
community-over-code-na-2023.adoc: [apache ignite:8, apache commons numbers:1, apache commons csv:1]
deck-of-cards-with-groovy.adoc: [eclipse collections:5]
deep-learning-and-eclipse-collections.adoc: [eclipse collections:7, eclipse deeplearning4j:2]
detecting-objects-with-groovy-the.adoc: [apache mxnet:12]
fun-with-obfuscated-groovy.adoc: [apache commons math:1]
groovy-2-5-clibuilder-renewal.adoc: [apache commons cli:2]
groovy-graph-databases.adoc: [apache age:11, apache hugegraph:3, apache tinkerpop:3]
groovy-haiku-processing.adoc: [eclipse collections:3]
groovy-lucene.adoc: [apache lucene:1, apache commons:1, apache commons math:2]
groovy-pekko-gpars.adoc: [apache pekko:4]
groovy-record-performance.adoc: [apache commons codec:1]
handling-byte-order-mark-characters.adoc: [apache commons io:1]
lego-bricks-with-groovy.adoc: [eclipse collections:6]
natural-language-processing-with-groovy.adoc: [apache opennlp:2, apache spark:1]
reading-and-writing-csv-files.adoc: [apache commons csv:1]
set-operations-with-groovy.adoc: [eclipse collections:3]
solving-simple-optimization-problems-with-groovy.adoc: [apache commons math:5, apache kie:1]
using-groovy-with-apache-wayang.adoc: [apache wayang:9, apache spark:7, apache flink:1, apache commons csv:1, apache ignite:1]
whiskey-clustering-with-groovy-and.adoc: [apache ignite:7, apache wayang:1, apache spark:2, apache commons csv:2]
wordle-checker.adoc: [eclipse collections:3]
zipping-collections-with-groovy.adoc: [eclipse collections:4]

eclipse collections (50)         ██████████████████████████████████████████████████▏
apache commons math (18)         ██████████████████▏
apache ignite (17)               █████████████████▏
apache spark (12)                ████████████▏
apache mxnet (12)                ████████████▏
apache age (11)                  ███████████▏
apache wayang (10)               ██████████▏
eclipse deeplearning4j (8)       ████████▏
apache commons collections (7)   ███████▏
apache nlpcraft (5)              █████▏
apache commons csv (5)           █████▏
apache pekko (4)                 ████▏
apache hugegraph (3)             ███▏
apache tinkerpop (3)             ███▏
apache commons (2)               ██▏
apache commons cli (2)           ██▏
apache opennlp (2)               ██▏
apache ofbiz (1)                 █▏
apache commons numbers (1)       █▏
apache lucene (1)                █▏
apache commons codec (1)         █▏
apache commons io (1)            █▏
apache kie (1)                   █▏
apache flink (1)                 █▏
</pre>

Output for the `runLuceneFacets` task:

<pre>
apache-nlpcraft-with-groovy.adoc: [apache nlpcraft:5]
classifying-iris-flowers-with-deep.adoc: [eclipse deeplearning4j:5, apache commons math:1, apache spark:2]
community-over-code-eu-2024.adoc: [apache ofbiz:1, apache commons math:2, apache ignite:1]
community-over-code-na-2023.adoc: [apache ignite:8, apache commons numbers:1, apache commons csv:1]
deck-of-cards-with-groovy.adoc: [eclipse collections:5]
deep-learning-and-eclipse-collections.adoc: [eclipse collections:7, eclipse deeplearning4j:2]
detecting-objects-with-groovy-the.adoc: [apache mxnet:12]
fruity-eclipse-collections.adoc: [eclipse collections:9, apache commons math:1]
fun-with-obfuscated-groovy.adoc: [apache commons math:1]
groovy-2-5-clibuilder-renewal.adoc: [apache commons cli:2]
groovy-graph-databases.adoc: [apache age:11, apache hugegraph:3, apache tinkerpop:3]
groovy-haiku-processing.adoc: [eclipse collections:3]
groovy-list-processing-cheat-sheet.adoc: [eclipse collections:4, apache commons collections:3]
groovy-lucene.adoc: [apache lucene:1, apache commons:1, apache commons math:2]
groovy-null-processing.adoc: [eclipse collections:6, apache commons collections:4]
groovy-pekko-gpars.adoc: [apache pekko:4]
groovy-record-performance.adoc: [apache commons codec:1]
handling-byte-order-mark-characters.adoc: [apache commons io:1]
lego-bricks-with-groovy.adoc: [eclipse collections:6]
matrix-calculations-with-groovy-apache.adoc: [apache commons math:6, eclipse deeplearning4j:1, apache commons:1]
natural-language-processing-with-groovy.adoc: [apache opennlp:2, apache spark:1]
reading-and-writing-csv-files.adoc: [apache commons csv:1]
set-operations-with-groovy.adoc: [eclipse collections:3]
solving-simple-optimization-problems-with-groovy.adoc: [apache commons math:5, apache kie:1]
using-groovy-with-apache-wayang.adoc: [apache wayang:9, apache spark:7, apache flink:1, apache commons csv:1, apache ignite:1]
whiskey-clustering-with-groovy-and.adoc: [apache ignite:7, apache wayang:1, apache spark:2, apache commons csv:2]
wordle-checker.adoc: [eclipse collections:3]
zipping-collections-with-groovy.adoc: [eclipse collections:4]

dim=projectHitCounts path=[] value=-1 childCount=24
  eclipse collections (50)
  apache commons math (18)
  apache ignite (17)
  apache spark (12)
  apache mxnet (12)
  apache age (11)
  apache wayang (10)
  eclipse deeplearning4j (8)
  apache commons collections (7)
  apache nlpcraft (5)


dim=projectFileCounts path=[] value=-1 childCount=24
  eclipse collections (10)
  apache commons math (7)
  apache spark (4)
  apache ignite (4)
  apache commons csv (4)
  eclipse deeplearning4j (3)
  apache commons collections (2)
  apache commons (2)
  apache wayang (2)
  apache nlpcraft (1)


dim=projectNameCounts path=[] value=-1 childCount=2
  apache (21)
  eclipse (12)
</pre>
