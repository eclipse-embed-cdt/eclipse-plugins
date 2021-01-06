#!/bin/bash
###############################################################################
# Copyright (c) 2018, 2020 Kichwa Coders Ltd and others.
#
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
###############################################################################

set -e

##
# Remove trailing whitespace.
# The .gitattributes is used as a filter to identify files to check. Patterns
# with this "# check trailing whitespace" on the line before are checked
# (lines in .gitattributes starting with '#' are ignored).
##
awk '/# remove trailing whitespace/{do getline; while ($0 ~ /^#/); print $1}' .gitattributes |
    while read i ; do
        echo "Removing trailing whitespace on $i files"
        git ls-files -z -- "$i" ":!./plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/liqp" ":!./plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/zafarkhaja/semver" | xargs --null --no-run-if-empty sed -i 's/[ \t]*$//'
    done
