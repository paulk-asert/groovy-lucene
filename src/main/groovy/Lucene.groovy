import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.LowerCaseFilter
import org.apache.lucene.analysis.pattern.PatternTokenizer
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

import static Regex.tokenRegex
import static org.codehaus.groovy.util.StringUtil.bar

var analyzer = new ApacheProjectAnalyzer()
var indexDir = new ByteBuffersDirectory()
var config = new IndexWriterConfig(analyzer)
var writer = new IndexWriter(indexDir, config)

var blogBaseDir = '/projects/apache-websites/groovy-website/site/src/site/blog'
new File(blogBaseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
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
writer.close()

var reader = DirectoryReader.open(indexDir)
var searcher = new IndexSearcher(reader)
var parser = new QueryParser("content", analyzer)

var query = parser.parse('apache* OR eclipse*')
var results = searcher.search(query, 30)
println "Total documents with hits for $query --> $results.totalHits"

var storedFields = searcher.storedFields()
var histogram = [:].withDefault { 0 }
results.scoreDocs.each { ScoreDoc doc ->
    var document = storedFields.document(doc.doc)
    var found = handleHit(doc, query, reader)
    println "${document.get('name')}: ${found*.replaceAll('\n', ' ').countBy()}"
    found.each { histogram[it.replaceAll('\n', ' ')] += 1 }
}

println()

histogram.sort { e -> -e.value }.each { k, v ->
    var label = "$k ($v)"
    println "${label.padRight(32)} ${bar(v, 0, 50, 50)}"
}

List<String> handleHit(ScoreDoc hit, Query query, DirectoryReader dirReader) {
    boolean phraseHighlight = Boolean.TRUE
    boolean fieldMatch = Boolean.TRUE
    FieldQuery fieldQuery = new FieldQuery(query, dirReader, phraseHighlight, fieldMatch)
    FieldTermStack stack = new FieldTermStack(dirReader, hit.doc, 'content', fieldQuery)
    FieldPhraseList phrases = new FieldPhraseList(stack, fieldQuery)
    phrases.phraseList*.termsInfos*.text.flatten()
}

class ApacheProjectAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        var src = new PatternTokenizer(~tokenRegex, 0)
        var result = new LowerCaseFilter(src)
        new TokenStreamComponents(src, result)
    }
}
