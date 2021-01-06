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
# Remove execute permission from all files in the repository.
##
echo "Removing execute permission from all files in repository"
git ls-files -z | xargs --null --no-run-if-empty chmod -x

##
# Enforce certain file types to have execute permission.
# The .gitattributes is used as a filter to identify files to add execute
# permissions to. Patterns with this "# file permission +x" on the line before
# are considered (lines in .gitattributes starting with '#' are ignored).
##
awk '/# file permission \+x/{do getline; while ($0 ~ /^#/); print $1}' .gitattributes |
    while read i ; do
        echo "Adding execute permission to $i files"
        git ls-files -z -- "$i" | xargs --null --no-run-if-empty chmod +x
    done
