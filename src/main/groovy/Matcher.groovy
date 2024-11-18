import static Regex.tokenRegex
import static org.codehaus.groovy.util.StringUtil.bar

var blogBaseDir = '/projects/apache-websites/groovy-website/site/src/site/blog'
var histogram = [:].withDefault { 0 }

new File(blogBaseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
    var m = file.text =~ tokenRegex
    var projects = m*.get(2).grep()*.toLowerCase()*.replaceAll('\n', ' ')
    var counts = projects.countBy()
    if (counts) {
        println "$file.name: $counts"
        counts.each { k, v -> histogram[k] += v }
    }
}

println()

histogram.sort { e -> -e.value }.each { k, v ->
    var label = "$k ($v)"
    println "${label.padRight(32)} ${bar(v, 0, 50, 50)}"
}
