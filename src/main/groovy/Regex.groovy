class Regex {
    public static String tokenRegex = /(?ix) # ignore case, enable whitespace & comments
        \b                                   # word boundary
        (                                    # start capture of all terms
            (                                # capture project name
                (apache|eclipse)\s           # foundation name
                (commons\s)?                 # optional subproject name
                    (                        # capture next word unless excluded word
                        ?!(
                            groovy           # excluded words
                          | and
                          | license
                          | users
                          | software
                          | projects
                          | https
                          | or
                          | prefixes
                          | technologies
                          )
                    )\w+                     # end capture #2
            )
            |                                # alternatively
            (                                # capture non-project word
                (?!(apache|eclipse))
                \w+
            )                                # end capture #3
        )                                    # end capture #1
    /
}
