import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.PostingsEnum
import org.apache.lucene.index.Term
import org.apache.lucene.index.TermsEnum
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper
import org.apache.lucene.queries.spans.SpanNearQuery
import org.apache.lucene.queries.spans.SpanQuery
import org.apache.lucene.queries.spans.SpanTermQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.DocIdSetIterator
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.RegexpQuery
import org.apache.lucene.store.ByteBuffersDirectory

import static Common.baseDir

var analyzer = new StandardAnalyzer()
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

IndexReader reader = DirectoryReader.open(indexDir)
var searcher = new IndexSearcher(reader)

var projects = ['math', 'spark', 'lucene', 'collections', 'deeplearning4j',
                'beam', 'wayang', 'csv', 'io', 'numbers', 'ignite', 'mxnet',
                'age', 'nlpcraft', 'pekko', 'kie', 'tinkerpop', 'commons',
                'cli', 'opennlp', 'ofbiz', 'codec', 'hugegraph', 'flink']
var suffix = new SpanMultiTermQueryWrapper(new RegexpQuery(
    new Term('content', "(${projects.join('|')})")))

// look for apache commons <suffix>
SpanQuery[] spanTerms = ['apache', 'commons'].collect {
    new SpanTermQuery(new Term('content', it))
} + suffix
var apacheCommons = new SpanNearQuery(spanTerms, 0, true)

// look for (apache|eclipse) <suffix>
var foundation = new SpanMultiTermQueryWrapper(new RegexpQuery(
    new Term('content', '(apache|eclipse)')))
var otherProject = new SpanNearQuery([foundation, suffix] as SpanQuery[], 0, true)

var builder = new BooleanQuery.Builder(minimumNumberShouldMatch: 1)
builder.add(otherProject, BooleanClause.Occur.SHOULD)
builder.add(apacheCommons, BooleanClause.Occur.SHOULD)
var query = builder.build()
var results = searcher.search(query, 30)
println "Total documents with hits for $query --> $results.totalHits"

var vectors = reader.termVectors()
var storedFields = reader.storedFields()

var emojis = [:].withDefault { [] as Set }
for (docId in 0..<reader.maxDoc()) {
    String name = storedFields.document(docId).get('name')
    TermsEnum terms = vectors.get(docId, 'content').iterator()
    while (terms.next() != null) {
        PostingsEnum postingsEnum = terms.postings(null, PostingsEnum.ALL)
        while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            var string = terms.term().utf8ToString()
            if (string.codePoints().allMatch(Character::isEmojiPresentation)) {
                emojis[name] += string
            }
        }
    }
}
emojis.collect { k, v -> "$k: ${v.join(', ')}" }.each { println it }
