#!/bin/bash

find . -type d -name src ! -path "./.git/*" ! -path "**/templates/**" \
  | xargs -I % find % -type f ! -name '.DS_Store' ! -name '*.md' \
    -print -exec grep 'Copyright ' {} \;
