#! /bin/bash
set -euo pipefail
IFS=$'\n\t'

# Script to enumerate all assembly arm/startup_*.s files and convert
# them all to vector_*.c.

if [ $# -lt 2 ]
then
  echo "Using: bash $(basename $0) source-folder destination-folder"
  exit 1
fi

if [ ! -d $2 ]
then
  echo "$2 not a folder"
  exit 1
fi

echo
echo "$2"
rm -f "$2"/vectors_*.c

for f in "$1"/startup_*.s
do
  startup=$(basename ${f})
  vectors=$(echo ${startup} | sed -e 's/startup/vectors/' -e 's/[.]s/.c/')
  echo "${startup} -> ${vectors}"
  bash "$(dirname $0)/convert-arm-asm.sh" "${f}" >"$2/${vectors}"
done
