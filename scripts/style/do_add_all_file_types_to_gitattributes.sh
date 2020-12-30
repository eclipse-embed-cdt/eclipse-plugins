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
# Add all file types to .gitattributes
##
git ls-files | grep -v '/xpacks/' | sed -E '-es@^.*/([^/]+)$@\1@' '-es@.+\.@\\\*\\.@'  | sort -u | while read i ; do
    if ! grep "^$i " .gitattributes > /dev/null
    then
        echo "MISSING $i in .gitattributes, adding as text, check if that is correct"
        echo "$i text # automatically added - please verify" >> .gitattributes
    fi
done
