#! /bin/bash

# This script runs on Mac OS X and is intended to
# publish the ilg.gnuarmeclipse-repository site
# to the File Release System (FRS) folder on SourceForge

TEST=""

if [ $# -gt 0 ] && [ "$1" = "test" ]
then
  TEST="-test"
  shift
fi

SF_FOLDER="Eclipse/updates"

echo "Updating $SF_FOLDER$TEST"

if [ $# -gt 0 ] && [ "$1" == "dry" ]
then
  DRY="dry"
  shift
  echo "Dry run"
fi

if [ $# -gt 0 ] && [ "$1" == "force" ]
then
  FORCE="force"
  shift
  echo "Force write"
fi

SF_USER=ilg-ul
SF_DESTINATION="$SF_USER,gnuarmeclipse@frs.sourceforge.net:/home/frs/project/g/gn/gnuarmeclipse/$SF_FOLDER$TEST"
SOURCE_LIST="."

# -c skip based on checksum, not mod-time & size

RSYNC_OPTS="-vrCt --exclude=scripts --exclude=.*"
RSYNC_OPTS+=" --delete"

if [ "${DRY}" == "dry" ]
then
  RSYNC_OPTS+=" -n"
fi

if [ "${FORCE}" == "force" ]
then
  RSYNC_OPTS+=" --ignore-times"
else
  RSYNC_OPTS+=" --checksum"
fi

if [ ! -d ../ilg.gnuarmeclipse-repository/target/repository ]
then
  echo "No repository folder found"
  exit 1
fi

cd ../ilg.gnuarmeclipse-repository/target

echo "Rsync-ing SourceForge ${SF_FOLDER}${TEST} site (${RSYNC_OPTS})"
(cd repository; rsync -e ssh ${RSYNC_OPTS} ${SOURCE_LIST} ${SF_DESTINATION})

if [ "${TEST}" == "-test" ]
then
  echo "Published on the test site."
else
  echo "Published on the main site."
fi

if [ -f *-SNAPSHOT.zip ]
then
  NUMDATE=$(ls repository/plugins/ilg.gnuarmeclipse.managedbuild.cross* | sed -e 's/.*_[0-9]*[.][0-9]*[.][0-9]*[.]\([0-9]*\)[.]jar/\1/')
  ARCHIVE_PREFIX=$(ls *-SNAPSHOT.zip | sed -e 's/\(.*\)-SNAPSHOT[.]zip/\1/')

  ARCHIVE_FOLDER="../../../../archive"
  if [ "${TEST}" == "-test" ]
  then
    ARCHIVE_FOLDER="${ARCHIVE_FOLDER}/internal"
  else
    ARCHIVE_FOLDER="${ARCHIVE_FOLDER}/releases/plug-ins"
  fi
  
  mkdir -p "${ARCHIVE_FOLDER}"

  P="${ARCHIVE_FOLDER}/${ARCHIVE_PREFIX}-${NUMDATE}.zip"
  AP="$(cd "$(dirname "${P}")"; pwd)/$(basename "${P}")"

  mv -f "${ARCHIVE_PREFIX}-SNAPSHOT.zip" "${AP}"
  (cd "${ARCHIVE_FOLDER}"; shasum -a 256 -p "${AP}" >"${AP}.sha")

  echo "Archive available from \"${AP}\""
fi

if [ "${TEST}" == "-test" ]
then
  echo "When final, don't forget to publish the archive too!"
fi


