s/[[:blank:]]*[.]word[[:blank:]]*\([[:alnum:]_]*\)[[:blank:]]*[/][*][[:blank:]]*\(.*\)[*][/]/	\1,	\/\/ \2/
s/[[:blank:]]*[.]word[[:blank:]]*\([[:alnum:]_]*\)[[:blank:]]*[/][/][[:blank:]]*\(.*\)/	\1,	\/\/ \2/
s/[[:blank:]]*[.]word[[:blank:]]*\([[:alnum:]_]*\)[[:blank:]]*/	\1, \/\//
s/[[:blank:]]*[.]word[[:blank:]]*\([[:alnum:]_]*\)[[:blank:]]*\(.+\)/	\1, \2/
