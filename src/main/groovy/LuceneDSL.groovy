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
import org.apache.lucene.index.Term
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper
import org.apache.lucene.queries.spans.SpanNearQuery
import org.apache.lucene.queries.spans.SpanQuery
import org.apache.lucene.queries.spans.SpanTermQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
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

def span(... spanTerms) {
    SpanQuery[] terms = spanTerms.collect { term -> if (term instanceof String) new SpanTermQuery(new Term('content', term)) else new SpanMultiTermQueryWrapper(new RegexpQuery(new Term('content', term.toString())))
    }
    new SpanNearQuery(terms, 0, true)
}

Query.metaClass.or = { Query other ->
    var builder = new BooleanQuery.Builder(minimumNumberShouldMatch: 1)
    builder.add(delegate, BooleanClause.Occur.SHOULD)
    builder.add(other, BooleanClause.Occur.SHOULD)
    builder.build()
}

var suffix = "(${projects.join('|')})"
var query = span('apache', 'commons', ~suffix) | span(~'(apache|eclipse)', ~suffix)
var results = searcher.search(query, 30)
println "Total documents with hits for $query --> $results.totalHits"

query = span('jackson', 'databind') | span(~'virt.*', 'threads')
results = searcher.search(query, 30)
println "Total documents with hits for $query --> $results.totalHits"
