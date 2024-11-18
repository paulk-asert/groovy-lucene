import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StringField
import org.apache.lucene.facet.FacetField
import org.apache.lucene.facet.FacetsCollectorManager
import org.apache.lucene.facet.FacetsConfig
import org.apache.lucene.facet.taxonomy.AssociationAggregationFunction
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts
import org.apache.lucene.facet.taxonomy.IntAssociationFacetField
import org.apache.lucene.facet.taxonomy.TaxonomyFacetIntAssociations
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.store.ByteBuffersDirectory
import static Regex.tokenRegex

var analyzer = new ApacheProjectAnalyzer()
var indexDir = new ByteBuffersDirectory()
var taxonDir = new ByteBuffersDirectory()
var config = new IndexWriterConfig(analyzer)
var indexWriter = new IndexWriter(indexDir, config)
var taxonWriter = new DirectoryTaxonomyWriter(taxonDir)

var fConfig = new FacetsConfig().tap {
    setHierarchical("projectNameCounts", true)
    setMultiValued("projectNameCounts", true)
    setMultiValued("projectFileCounts", true)
    setMultiValued("projectHitCounts", true)
    setIndexFieldName('projectHitCounts', '$projectHitCounts')
}

var blogBaseDir = '/projects/apache-websites/groovy-website/site/src/site/blog'
new File(blogBaseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
    var m = file.text =~ tokenRegex
    var projects = m*.get(0)*.toLowerCase()*.replaceAll('\n', ' ').countBy()
    file.withReader { br ->
        var document = new Document()
        var fieldType = new FieldType(stored: true,
            indexOptions: IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS,
            storeTermVectors: true,
            storeTermVectorPositions: true,
            storeTermVectorOffsets: true)
        document.add(new Field('content', br.text, fieldType))
        document.add(new StringField('name', file.name, Field.Store.YES))
        if (projects) {
            println "$file.name: $projects"
            projects.each { k, v ->
                document.add(new IntAssociationFacetField(v, "projectHitCounts", k))
                document.add(new FacetField("projectFileCounts", k))
                document.add(new FacetField("projectNameCounts", k.split()))
            }
        }
        indexWriter.addDocument(fConfig.build(taxonWriter, document))
    }
}
indexWriter.close()
taxonWriter.close()

var reader = DirectoryReader.open(indexDir)
var searcher = new IndexSearcher(reader)
var taxonReader = new DirectoryTaxonomyReader(taxonDir)
var fcm = new FacetsCollectorManager()
var fc = FacetsCollectorManager.search(searcher, new MatchAllDocsQuery(), 10, fcm).facetsCollector()
var projects = new TaxonomyFacetIntAssociations('$projectHitCounts', taxonReader, fConfig, fc, AssociationAggregationFunction.SUM)
var facets = new FastTaxonomyFacetCounts(taxonReader, fConfig, fc)
var hitCounts = projects.getTopChildren(10, "projectHitCounts")
var fileCounts = facets.getTopChildren(10, "projectFileCounts")
var nameCounts = facets.getTopChildren(10, "projectNameCounts")

println "\n$hitCounts"
println "\n$fileCounts"
println "\n$nameCounts"
