class Regex {
    public static tokenRegex = $/(?ix) # ignore case, enable whitespace/comments
        \b                          # word boundary
        (                           # start capture of project name
            (apache|eclipse)\s      # foundation name
            (commons\s)?            # optional subproject name
                (                   # capture next word unless excluded word
                    ?!(
                        groovy      # excluded words
                      | and
                      | license
                      | users
                      | software
                      | projects
                      | https
                      | technologies
                      )
                )\w+                # end capture #2
        )                           # end capture #1
    /$
}
