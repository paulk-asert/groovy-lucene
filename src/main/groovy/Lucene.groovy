import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.vectorhighlight.FieldPhraseList
import org.apache.lucene.search.vectorhighlight.FieldQuery
import org.apache.lucene.search.vectorhighlight.FieldTermStack
import org.apache.lucene.store.ByteBuffersDirectory

import static Common.*

var analyzer = new ProjectNameAnalyzer()
var indexDir = new ByteBuffersDirectory()
var config = new IndexWriterConfig(analyzer)

new IndexWriter(indexDir, config).withCloseable { writer ->
    new File(baseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
        file.withReader { br ->
            var document = new Document()
            var fieldType = new FieldType(stored: true,
                indexOptions: IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS,
                storeTermVectors: true,
                storeTermVectorPositions: true,
                storeTermVectorOffsets: true)
            document.add(new Field('content', br.text, fieldType))
            document.add(new StringField('name', file.name, Field.Store.YES))
            writer.addDocument(document)
        }
    }
}

var reader = DirectoryReader.open(indexDir)
var searcher = new IndexSearcher(reader)
var parser = new QueryParser("content", analyzer)

var query = parser.parse(/apache\ * OR eclipse\ */)
var results = searcher.search(query, 30)
println "Total documents with hits for $query --> $results.totalHits\n"

var storedFields = searcher.storedFields()
var histogram = [:].withDefault { 0 }
results.scoreDocs.each { ScoreDoc scoreDoc ->
    var doc = storedFields.document(scoreDoc.doc)
    var found = handleHit(scoreDoc, query, reader)
    println "${doc.get('name')}: ${found*.replaceAll('\n', ' ').countBy()}"
    found.each { histogram[it.replaceAll('\n', ' ')] += 1 }
}

println "\nFrequency of total hits mentioning a project (top 10):"
display(histogram.sort { e -> -e.value }.take(10), 50)

List<String> handleHit(ScoreDoc hit, Query query, DirectoryReader dirReader) {
    boolean phraseHighlight = true
    boolean fieldMatch = true
    var fieldQuery = new FieldQuery(query, dirReader, phraseHighlight, fieldMatch)
    var stack = new FieldTermStack(dirReader, hit.doc, 'content', fieldQuery)
    var phrases = new FieldPhraseList(stack, fieldQuery)
    phrases.phraseList*.termsInfos*.text.flatten()
}
