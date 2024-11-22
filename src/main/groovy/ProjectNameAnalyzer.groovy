import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.LowerCaseFilter
import org.apache.lucene.analysis.pattern.PatternTokenizer

import static Common.tokenRegex

class ProjectNameAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        var src = new PatternTokenizer(~tokenRegex, 0)
        var result = new LowerCaseFilter(src)
        new TokenStreamComponents(src, result)
    }
}
