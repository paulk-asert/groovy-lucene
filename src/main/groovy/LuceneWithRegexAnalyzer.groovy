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
import org.apache.lucene.index.PostingsEnum
import org.apache.lucene.index.Term
import org.apache.lucene.index.TermsEnum
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.DocIdSetIterator
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.ByteBuffersDirectory

import static Regex.tokenRegex
import static org.codehaus.groovy.util.StringUtil.bar

var blogBaseDir = '/projects/apache-websites/groovy-website/site/src/site/blog'
var analyzer = new ApacheProjectAnalyzer()
var indexDir = new ByteBuffersDirectory()
var config = new IndexWriterConfig(analyzer)

new IndexWriter(indexDir, config).withCloseable { writer ->
    var indexedWithFreq = new FieldType(stored: true,
        indexOptions: IndexOptions.DOCS_AND_FREQS,
        storeTermVectors: true)
    new File(blogBaseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
        file.withReader { br ->
            var document = new Document()
            document.add(new Field('content', br.text, indexedWithFreq))
            document.add(new StringField('name', file.name, Field.Store.YES))
            writer.addDocument(document)
        }
    }
}

var reader = DirectoryReader.open(indexDir)
var vectors = reader.termVectors()
var storedFields = reader.storedFields()

Set projects = []
for (docId in 0..<reader.maxDoc()) {
    String name = storedFields.document(docId).get('name')
    TermsEnum terms = vectors.get(docId, 'content').iterator()
    var found = [:]
    while (terms.next() != null) {
        PostingsEnum postingsEnum = terms.postings(null, PostingsEnum.ALL)
        while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            int freq = postingsEnum.freq()
            var string = terms.term().utf8ToString().replaceAll('\n', ' ')
            if (string.startsWith('apache ') || string.startsWith('eclipse ')) {
                found[string] = freq
            }
        }
    }
    if (found) {
        println "$name: $found"
        projects += found.keySet()
    }
}

var terms = projects.collect { name -> new Term('content', name) }
var byReverseValue = { e -> -e.value }

println "\nFrequency of documents mentioning a project (top 10)"
var docFreq = terms.collectEntries { term -> [term.text(), reader.docFreq(term)] }
docFreq.sort(byReverseValue).take(10).each { k, v ->
    var label = "$k ($v)"
    println "${label.padRight(32)} ${bar(v * 2, 0, 20, 20)}"
}

println "\nFrequency of total hits mentioning a project (top 10)"
var termFreq = terms.collectEntries { term -> [term.text(), reader.totalTermFreq(term)] }
termFreq.sort(byReverseValue).take(10).each { k, v ->
    var label = "$k ($v)"
    println "${label.padRight(32)} ${bar(v, 0, 50, 50)}"
}

var parser = new QueryParser("content", analyzer)
var searcher = new IndexSearcher(reader)
var query = parser.parse('emoji*')
var results = searcher.search(query, 10)
println "\nTotal documents with hits for $query --> $results.totalHits"
results.scoreDocs.each {
    var doc = storedFields.document(it.doc)
    println "${doc.get('name')}"
}

class ApacheProjectAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        var src = new PatternTokenizer(~tokenRegex, 0)
        var result = new LowerCaseFilter(src)
        new TokenStreamComponents(src, result)
    }
}
