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

##
# Setup Eclipse Core Preferences
##
git ls-files  -- \*\*/.project | while read i ; do
    d=`dirname $i`;
    mkdir -p $d/.settings
    if ! test -e $d/.settings/org.eclipse.core.resources.prefs; then
        echo 'eclipse.preferences.version=1' > $d/.settings/org.eclipse.core.resources.prefs
        echo 'encoding/<project>=UTF-8' >> $d/.settings/org.eclipse.core.resources.prefs
    fi
    if ! grep 'encoding/<project>=UTF-8' $d/.settings/org.eclipse.core.resources.prefs > /dev/null; then
        echo 'encoding/<project>=UTF-8' >> $d/.settings/org.eclipse.core.resources.prefs
    fi
    if ! grep 'eclipse.preferences.version=1' $d/.settings/org.eclipse.core.resources.prefs > /dev/null; then
        echo 'eclipse.preferences.version=1' >> $d/.settings/org.eclipse.core.resources.prefs
    fi
done

##
# Copy JDT/PDE preferences
##
git ls-files  -- \*\*/.project ":!$COREPROJECT/.project" | while read i ; do
    d=`dirname $i`;
    natures=$(xmllint --xpath 'string(//projectDescription/natures)' $i)
    mkdir -p $d/.settings

    # JDT
    if [[ $natures == *"org.eclipse.jdt.core.javanature"* ]]; then
        cp $COREPROJECT/.settings/org.eclipse.jdt.* $d/.settings
        # For test plug-ins we are more lenient so don't warn on some items
        if echo $i | grep -E '\.tests?[/\.]' > /dev/null; then
            sed -i \
                '-es@org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral=warning@org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.discouragedReference=warning@org.eclipse.jdt.core.compiler.problem.discouragedReference=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.deprecation=warning@org.eclipse.jdt.core.compiler.problem.deprecation=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.discouragedReference=warning@org.eclipse.jdt.core.compiler.problem.discouragedReference=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.emptyStatement=warning@org.eclipse.jdt.core.compiler.problem.emptyStatement=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.fieldHiding=warning@org.eclipse.jdt.core.compiler.problem.fieldHiding=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.finalParameterBound=warning@org.eclipse.jdt.core.compiler.problem.finalParameterBound=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.forbiddenReference=error@org.eclipse.jdt.core.compiler.problem.forbiddenReference=warning@' \
                '-es@org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation=warning@org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.unusedLocal=warning@org.eclipse.jdt.core.compiler.problem.unusedLocal=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.unusedPrivateMember=warning@org.eclipse.jdt.core.compiler.problem.unusedPrivateMember=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.potentialNullReference=warning@org.eclipse.jdt.core.compiler.problem.potentialNullReference=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.rawTypeReference=warning@org.eclipse.jdt.core.compiler.problem.rawTypeReference=ignore@' \
                '-es@org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch=warning@org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch=ignore@' \
                $d/.settings/org.eclipse.jdt.core.prefs
        fi
    else
        rm -f $d/.settings/org.eclipse.jdt*.prefs
    fi

    # PDE
    if [[ $natures == *"org.eclipse.pde.PluginNature"* ]]; then
        cp $COREPROJECT/.settings/org.eclipse.pde.prefs $d/.settings
        cp $COREPROJECT/.settings/org.eclipse.pde.api.tools.prefs $d/.settings
        if echo $i | grep -E '\.tests?[/\.]' > /dev/null; then
            sed -i \
                '-es@compilers.p.not-externalized-att=1@compilers.p.not-externalized-att=2@' \
                $d/.settings/org.eclipse.pde.prefs
        fi
    else
        rm -f $d/.settings/org.eclipse.pde*.prefs
    fi
done

##
# Verify API Tooling is enabled for all non-test/example bundles
##
git ls-files  -- \*\*/.project | while read i ; do
    d=`dirname $i`;
    natures=$(xmllint --xpath 'string(//projectDescription/natures)' $i)
    if [[ $natures == *"org.eclipse.pde.PluginNature"* ]] && [[ $natures == *"org.eclipse.jdt.core.javanature"* ]]; then
        if [[ $natures != *"org.eclipse.pde.api.tools.apiAnalysisNature"* ]]; then
            if ! echo $i | grep -E '\.tests?[/\.]' > /dev/null && ! echo $i | grep -E '\.examples?[/\.]' > /dev/null; then
                echo "$d is missing API Tools Nature - Turn it on in Eclipse by 1) Right-click project 2) Plug-in tools -> API Tools Setup"
                exit 1
            fi
        fi
    fi
done

