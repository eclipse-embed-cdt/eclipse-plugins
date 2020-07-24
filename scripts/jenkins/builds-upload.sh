#!/bin/bash

set -u # run with unset flag error so that missing parameters cause build failure
set -e # error out on any failed commands
set -x # echo all commands used for debugging purposes

SSHUSER="genie.embed-cdt@projects-storage.eclipse.org"
SSH="ssh ${SSHUSER}"
SCP="scp"

P2ZIP=repositories/ilg.gnumcueclipse.repository/target/ilg.gnumcueclipse.repository-*.zip
# The download location is chosen to be a non-mirrored URL according to
#  https://wiki.eclipse.org/IT_Infrastructure_Doc#Use_mirror_sites.2Fsee_which_mirrors_are_mirroring_my_files.3F
# It is accessible at:
#  https://download.eclipse.org/embed-cdt/
DOWNLOAD_ROOT=/home/data/httpd/download.eclipse.org/embed-cdt
DOWNLOAD=${DOWNLOAD_ROOT}/builds/${BRANCH_NAME}

${SSH} /bin/bash -x << _EOF_
rm -rf ${DOWNLOAD}-temp
rm -rf ${DOWNLOAD}-last
mkdir -p ${DOWNLOAD}-temp
_EOF_
${SCP} ${P2ZIP} ${SSHUSER}:${DOWNLOAD}-temp/ilg.gnumcueclipse.repository.zip
${SSH} /bin/bash -x << _EOF_
(cd ${DOWNLOAD}-temp && unzip ilg.gnumcueclipse.repository.zip)
[ -d "${DOWNLOAD}" ] && mv ${DOWNLOAD} ${DOWNLOAD}-last
mv ${DOWNLOAD}-temp ${DOWNLOAD}
rm -rf ${DOWNLOAD}-last
_EOF_
