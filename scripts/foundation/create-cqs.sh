#!/usr/bin/env bash

# -----------------------------------------------------------------------------
# Safety settings (see https://gist.github.com/ilg-ul/383869cbb01f61a51c4d).

if [[ ! -z ${DEBUG} ]]
then
  set ${DEBUG} # Activate the expand mode if DEBUG is anything but empty.
else
  DEBUG=""
fi

set -o errexit # Exit if command failed.
set -o pipefail # Exit if pipe failed.
set -o nounset # Exit if variable not set.

# Remove the initial space and instead use '\n'.
IFS=$'\n\t'

# -----------------------------------------------------------------------------
# Identify the script location, to reach, for example, the helper scripts.

script_path="$0"
if [[ "${script_path}" != /* ]]
then
  # Make relative path absolute.
  script_path="$(pwd)/$0"
fi

script_name="$(basename "${script_path}")"

script_folder_path="$(dirname "${script_path}")"
script_folder_name="$(basename "${script_folder_path}")"

# =============================================================================

version="6.0.0"

embed_cdt_plugins_path=$(dirname $(dirname "${script_folder_path}"))
echo "Source path: '${embed_cdt_plugins_path}'"

folder_name=embed-cdt-cqs
dest_path=${HOME}/tmp/${folder_name}
echo "Destination path: '${dest_path}'"

rm -rf ${dest_path}
mkdir -p ${dest_path}


cd ${dest_path}
mkdir -p main semver template-core template-stm template-ad template-freescale template-cortexm

echo
echo "Copy the entire Git repo..."
cp -r ${embed_cdt_plugins_path}/* main

echo "Remove jar files..."
find main -name '*.jar' -exec rm -v {} \;

echo "Remove target folders..."
rm -rf $(find main -type d -name 'target' -print)

echo
echo "Move the semver folders..."
mv -v "main/plugins/org.eclipse.embedcdt.core/src/com/github/zafarkhaja/semver"/* semver

echo
echo "Move the ad folders..."
mv -v "main/plugins/org.eclipse.embedcdt.templates.ad/templates/aducm36x_exe_c_project/vendor"/* template-ad

echo
echo "Move the freescale folders..."
mv -v "main/plugins/org.eclipse.embedcdt.templates.freescale/templates/micro-os-plus"/* template-freescale

echo
echo "Move the stm folders..."
mv -v "main/plugins/org.eclipse.embedcdt.templates.stm/templates/micro-os-plus"/* template-stm

echo
echo "Move the cortexm folders..."
mv -v "main/plugins/org.eclipse.embedcdt.templates.cortexm/templates/cortexm_project/Vendor" template-core/cortexm

echo
echo "Move the ilg authored content..."
mkdir -p template-core/common
mv -v "main/plugins/org.eclipse.embedcdt.templates.core/templates/common"/* template-core/common

mkdir -p template-core/cortexm
mv -v "main/plugins/org.eclipse.embedcdt.templates.cortexm/templates/"/* template-core/cortexm

mkdir -p template-core/sifive
mv -v "main/plugins/org.eclipse.embedcdt.templates.sifive/templates/"* template-core/sifive

mkdir -p template-core/stm
mv -v "main/plugins/org.eclipse.embedcdt.templates.stm/templates"/* template-core/stm

mkdir -p template-core/ad
mv -v "main/plugins/org.eclipse.embedcdt.templates.ad/templates"/* template-core/ad

mkdir -p template-core/freescale
mv -v "main/plugins/org.eclipse.embedcdt.templates.freescale/templates"/* template-core/freescale

echo
echo "Creating archives..."

zip -r "main-${version}.zip" "main"
echo "main-${version}.zip done"
echo
zip -r "semver-${version}.zip" "semver"
echo "semver-${version}.zip done"
echo
zip -r "template-core-${version}.zip" "template-core"
echo "template-core-${version}.zip done"
echo
zip -r "template-ad-${version}.zip" "template-ad"
echo "template-ad-${version}.zip done"
echo
zip -r "template-cortexm-${version}.zip" "template-cortexm"
echo "template-cortexm-${version}.zip done"
echo
zip -r "template-freescale-${version}.zip" "template-freescale"
echo "template-freescale-${version}.zip done"
echo
zip -r "template-stm-${version}.zip" "template-stm"
echo "template-stm-${version}.zip done"

echo
pwd
ls -lL "$(pwd)"

echo
echo "Done"


