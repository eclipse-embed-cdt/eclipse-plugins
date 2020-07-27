#!/bin/bash

set -u # run with unset flag error so that missing parameters cause build failure
set -e # error out on any failed commands
set -x # echo all commands used for debugging purposes

SSHUSER="genie.embed-cdt@projects-storage.eclipse.org"
SSH="ssh ${SSHUSER}"
SCP="scp"

# The download location is chosen to be a non-mirrored URL according to
#  https://wiki.eclipse.org/IT_Infrastructure_Doc#Use_mirror_sites.2Fsee_which_mirrors_are_mirroring_my_files.3F
# It is accessible at:
#  https://download.eclipse.org/embed-cdt/
DOWNLOAD_ROOT="/home/data/httpd/download.eclipse.org/embed-cdt"
UPDATES_DEST="${DOWNLOAD_ROOT}//builds/${BRANCH_NAME}"


VERSION=$(ls repositories/ilg.gnumcueclipse.repository/target/ilg.gnumcueclipse.repository-*.zip | sed -e 's/.*repository-\([0-9]*[.][0-9]*[.][0-9]*\)-\(.*\)[.]zip/\1/')
NUMDATE=$(ls repositories/ilg.gnumcueclipse.repository/target/repository/plugins/ilg.gnumcueclipse.core* | sed -e 's/.*core_\([0-9]*[.][0-9]*[.][0-9]*\)[.]\([0-9]*\)[.]jar/\2/')

ARCHIVE_NAME="ilg.gnumcueclipse.repository-${VERSION}-${NUMDATE}"

${SSH} /bin/bash -x << _EOF_
rm -rvf "${UPDATES_DEST}-temp"
rm -rvf "${UPDATES_DEST}-last"
mkdir -pv "${UPDATES_DEST}-temp"
_EOF_

(
  cd "repositories/ilg.gnumcueclipse.repository/target"

  if [ -f "ilg.gnumcueclipse.repository-${VERSION}-SNAPSHOT.zip" ]
  then
    # Rename the SNAPSHOT part to the actual timestamp.
    mv -v "ilg.gnumcueclipse.repository-${VERSION}-SNAPSHOT.zip" "${ARCHIVE_NAME}.zip" 
  fi

  ${SCP} -v "${ARCHIVE_NAME}.zip" "${SSHUSER}:${UPDATES_DEST}-temp/${ARCHIVE_NAME}.zip"

  shasum -a 256 -p "${ARCHIVE_NAME}.zip" >"${ARCHIVE_NAME}.zip.sha"
  ${SCP} -v "${ARCHIVE_NAME}.zip.sha" "${SSHUSER}:${UPDATES_DEST}-temp/${ARCHIVE_NAME}.zip.sha"
)

${SSH} /bin/bash -x << _EOF_
(cd ${UPDATES_DEST}-temp && unzip "${ARCHIVE_NAME}.zip")

if [ -d "${UPDATES_DEST}" ] 
then
  mv -v "${UPDATES_DEST}" "${UPDATES_DEST}-last"
fi
mv -v "${UPDATES_DEST}-temp" "${UPDATES_DEST}"
rm -rvf "${UPDATES_DEST}-last"

ls -lLR "${UPDATES_DEST}"
_EOF_

