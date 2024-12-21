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

import static Common.*

var analyzer = new ProjectNameAnalyzer()
var indexDir = new ByteBuffersDirectory()
var config = new IndexWriterConfig(analyzer)

new IndexWriter(indexDir, config).withCloseable { writer ->
    var indexedWithFreq = new FieldType(stored: true,
        indexOptions: IndexOptions.DOCS_AND_FREQS,
        storeTermVectors: true)
    new File(baseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
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

println "\nFrequency of total hits mentioning a project (top 10):"
var termFreq = terms.collectEntries { term -> [term.text(), reader.totalTermFreq(term)] }
display(termFreq.sort(byReverseValue).take(10), 50)

println "\nFrequency of documents mentioning a project (top 10):"
var docFreq = terms.collectEntries { term -> [term.text(), reader.docFreq(term)] }
display(docFreq.sort(byReverseValue).take(10), 20, 2)

var parser = new QueryParser("content", analyzer)
var searcher = new IndexSearcher(reader)
var query = parser.parse('emoji*')
var results = searcher.search(query, 10)
println "\nTotal documents with hits for $query --> $results.totalHits"
results.scoreDocs.each {
    var doc = storedFields.document(it.doc)
    println "${doc.get('name')}"
}
