#! /bin/bash

# This script runs on Mac OS X ans is intended to 
# publishes the ilg.gnuarmeclipse-repository site
# to the File Release System (FRS) folder on SourceForge

TEST=""

if [ $# -gt 0 ] && [ "$1" = "test" ]
then
  TEST="-test"
  shift
fi

SF_FOLDER="Eclipse/updates"

echo "Updating $SF_FOLDER$TEST"

if [ $# -gt 0 ] && [ "$1" = "dry" ]
then
  DRY="dry"
  shift
  echo "Dry run"
fi


SF_USER=ilg-ul
SF_DESTINATION="$SF_USER,gnuarmeclipse@frs.sourceforge.net:/home/frs/project/g/gn/gnuarmeclipse/$SF_FOLDER$TEST"
SOURCE_LIST="."

# -c skip based on checksum, not mod-time & size

RSYNC_OPTS="-vrCct --exclude=scripts --exclude=.*"
RSYNC_OPTS+=" --delete"

if [ "$DRY" = "dry" ]
then
  RSYNC_OPTS+=" -n"
fi

if [ ! -d ../ilg.gnuarmeclipse-repository/target/repository ]
then
  echo "No repository folder found"
  exit 1
fi

cd ../ilg.gnuarmeclipse-repository/target

echo "Rsync-ing SourceForge $SF_FOLDER$TEST site"
(cd repository; rsync -e ssh $RSYNC_OPTS $SOURCE_LIST $SF_DESTINATION)

if [ "$TEST" = "test" ]
then
  echo "Published on the test site"
else
  echo "Published on the main site. Don't forget to publish the archive too!"
fi

if [ -f *-SNAPSHOT.zip ]
then
  NUMDATE=$(ls repository/plugins/ilg.gnuarmeclipse.managedbuild.cross* | sed -e 's/.*_[0-9]*[.][0-9]*[.][0-9]*[.]\([0-9]*\)[.]jar/\1/')
  ARCHIVE_PREFIX=$(ls *-SNAPSHOT.zip | sed -e 's/\(.*\)-SNAPSHOT[.]zip/\1/')

  ARCHIVE_FOLDER=~/tmp/gnuarmeclipse-archive
  
  if [ ! -d $ARCHIVE_FOLDER ]
  then
    mkdir -p $ARCHIVE_FOLDER
  fi
  mv -fv $ARCHIVE_PREFIX-SNAPSHOT.zip $ARCHIVE_FOLDER/$ARCHIVE_PREFIX-$NUMDATE.zip
fi


