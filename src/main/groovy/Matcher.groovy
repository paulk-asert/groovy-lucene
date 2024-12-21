import static Common.*

var histogram = [:].withDefault { 0 }

new File(baseDir).traverse(nameFilter: ~/.*\.adoc/) { file ->
    var m = file.text =~ tokenRegex
    var projects = m*.get(2).grep()*.toLowerCase()*.replaceAll('\n', ' ')
    var counts = projects.countBy()
    if (counts) {
        println "$file.name: $counts"
        counts.each { k, v -> histogram[k] += v }
    }
}

println "\nFrequency of total hits mentioning a project:"
display(histogram.sort { e -> -e.value }, 50)
