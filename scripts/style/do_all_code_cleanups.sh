#!/bin/bash
###############################################################################
# Copyright (c) 2020 Kichwa Coders Canada Inc and others.
#
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
###############################################################################

# This script calls all the sub-scripts that do code cleanups

set -e

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

if test -z "$(git status -s)"; then
    echo "Tree looks clean - ready to start cleanups!"
else
    echo "Tree is dirty - commit your changes before running this"
    exit 1
fi

${DIR}/do_add_all_file_types_to_gitattributes.sh
${DIR}/do_project_settings.sh
${DIR}/do_format_code.sh
${DIR}/do_remove_trailing_whitespace.sh
${DIR}/do_fix_file_permissions.sh

###
# The code cleanups that are done on save in Eclipse cannot be done
# from a script. Instead do these steps manually in Eclipse:
# - start from a git tree with no uncommitted changes
# - some files the global cleanup fails on (not sure why), so:
#   - Open LiquidParser, make a whitespace edit and save
#   - Open LiquidWalker, make a whitespace edit and save
# - Select all projects in Eclipse's Package Explorer
# - Select Source -> Clean Up (you may need to be in Java or PDE perspective)
# - Use any of the embedcdt profiles (e.g. org.eclipse.embedcdt.core -- Unmanaged profile 'CDT'). The
#     do_project_settings.sh ensures that all projects have the same settings in this regard
# - Press Finish
# - resore to liqp and semver changes with
#    - git checkout -- plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/liqp
#    - git checkout -- plugins/org.eclipse.embedcdt.core/src/org/eclipse/embedcdt/core/zafarkhaja
# - review and commit the changes
