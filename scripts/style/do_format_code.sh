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
# This script is reused by other projects, if so, COREPROJECT should be set
# to the project to use a basis for project settings
##
: ${COREPROJECT:=plugins/org.eclipse.embedcdt.core}


if test -z "$(git status -s)"; then
    echo "Tree looks clean - ready to start formatting!"
else
    echo "Tree is dirty - code formatting can't run, see comment near the end of this script"
    exit 1
fi


##
# Format code
##
: ${ECLIPSE:=~/buildtools/eclipse-SDK-4.13/eclipse}
if test -e check_code_cleanliness_workspace; then
    echo check_code_cleanliness_workspace needs to be deleted
    exit 1
fi
${ECLIPSE} \
    -consolelog -nosplash -application org.eclipse.jdt.core.JavaCodeFormatter \
    -config $PWD/$COREPROJECT/.settings/org.eclipse.jdt.core.prefs \
    $PWD -data check_code_cleanliness_workspace
rm -rf check_code_cleanliness_workspace

###
# The liqp and semver files maintain their code formatting. This isn't possible to enforce
# in any reasonable way with JDT settings, so instead after formatting, restore these
# files.
# This is why we have to start with a clean tree before formatting starts
##
git checkout -- plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/liqp
git checkout -- plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/zafarkhaja
