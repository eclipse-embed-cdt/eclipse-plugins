#! /bin/bash

# Get absolute path of shell script. Supports:
# source ./script (When called by the . dot operator)
# Absolute path /path/to/script
# Relative path like ./script
# /path/dir1/../dir2/dir3/../script
# When called from symlink
# When symlink is nested eg) foo->dir1/dir2/bar bar->./../doe doe->script
# When caller changes the scripts name

pushd . > /dev/null
SCRIPT_PATH="${BASH_SOURCE[0]}";
  while([ -h "${SCRIPT_PATH}" ]) do 
    cd "`dirname "${SCRIPT_PATH}"`"
    SCRIPT_PATH="$(readlink "`basename "${SCRIPT_PATH}"`")"; 
  done
cd "`dirname "${SCRIPT_PATH}"`" > /dev/null
SCRIPT_PATH="`pwd`";
popd  > /dev/null
#echo "script=[${SCRIPT_PATH}]"
#echo "pwd   =[`pwd`]"

(cd "${SCRIPT_PATH}"; sh publish-updates.sh test)

echo "Install new software from http://gnuarmeclipse.sourceforge.net/updates-test"
