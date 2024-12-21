import static org.codehaus.groovy.util.StringUtil.bar
import static com.diogonunes.jcolor.Ansi.colorize
import static com.diogonunes.jcolor.Attribute.BLUE_TEXT
import static com.diogonunes.jcolor.Attribute.MAGENTA_TEXT

class Common {
    public static String tokenRegex = /(?ix) # ignore case, enable whitespace & comments
        \b                                   # word boundary
        (                                    # start capture of all terms
            (                                # capture project name term
                (apache|eclipse)\s           # foundation name
                (commons\s)?                 # optional subproject name
                (
                    ?!(groovy                # negative lookahead for excluded words
                    | and   | license  | users
                    | https | projects | software
                    | or    | prefixes | technologies)
                )\w+
            )                                # end capture project name term
            |                                # alternatively
            (                                # capture non-project term
                \w+?\b                       # non-greedily match any other words
            )                                # end capture non-project term
        )                                    # end capture term
    /

    public static String baseDir = '/projects/groovy-website/site/src/site/blog'

    static display(Map<String, Integer> data, int max, int scale = 1) {
        data.each { k, v ->
            var label = "$k ($v)"
            var color = k.startsWith('apache') ? MAGENTA_TEXT() : BLUE_TEXT()
            println "${label.padRight(32)} ${colorize(bar(v * scale, 0, max, max), color)}"
        }
    }
}
