class Regex {
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
}
