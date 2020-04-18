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
DOWNLOAD=/home/data/httpd/download.eclipse.org/embed-cdt/builds/${BRANCH_NAME}

${SSH} rm -rf ${DOWNLOAD}-temp
${SSH} rm -rf ${DOWNLOAD}-last
${SSH} mkdir -p ${DOWNLOAD}-temp
${SCP} ${P2ZIP} ${SSHUSER}:${DOWNLOAD}-temp/ilg.gnumcueclipse.repository.zip
${SSH} "cd ${DOWNLOAD}-temp && unzip ilg.gnumcueclipse.repository.zip"
${SSH} mv ${DOWNLOAD} ${DOWNLOAD}-last
${SSH} mv ${DOWNLOAD}-temp ${DOWNLOAD}
${SSH} rm -rf ${DOWNLOAD}-last
