#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

# Script to build the GNU ARM Eclipse QEMU distribution packages.
#
# Developed on OS X.
# Also tested on:
#   -
#
# The Windows and GNU/Linux packages are build using Docker containers.
# The build is structured in 2 steps, one running on the host machine
# and one running inside the Docker container.
#
# Note: The Windows 64-bits executable fails when timers are enabled.
#
# At first run, Docker will download/build 3 relatively large
# images (1-2GB) from Docker Hub.
#
# Prerequisites:
#
#   Docker
#   curl, git, automake, patch, tar, unzip
#
# When running on OS X, MacPorts with the following ports installed:
#
#   sudo port install libtool automake autoconf pkgconfig
#   sudo port install texinfo texlive
#

# Mandatory definition.
APP_NAME="QEMU"

APP_LC_NAME=$(echo "${APP_NAME}" | tr '[:upper:]' '[:lower:]')

# On Parallels virtual machines, prefer host Work folder.
# Second choice are Work folders on secondary disks.
# Final choice is a Work folder in HOME.
if [ -d /media/psf/Home/Work ]
then
  WORK_FOLDER=${WORK_FOLDER:-"/media/psf/Home/Work/${APP_LC_NAME}"}
elif [ -d /media/${USER}/Work ]
then
  WORK_FOLDER=${WORK_FOLDER:-"/media/${USER}/Work/${APP_LC_NAME}"}
elif [ -d /media/Work ]
then
  WORK_FOLDER=${WORK_FOLDER:-"/media/Work/${APP_LC_NAME}"}
else
  # Final choice, a Work folder in HOME.
  WORK_FOLDER=${WORK_FOLDER:-"${HOME}/Work/${APP_LC_NAME}"}
fi

# ----- Create Work folder. -----

echo
echo "Using \"${WORK_FOLDER}\" as Work folder..."

mkdir -p "${WORK_FOLDER}"


# ----- Parse actions and command line options. -----

ACTION=""
DO_BUILD_WIN32=""
DO_BUILD_WIN64=""
DO_BUILD_DEB32=""
DO_BUILD_DEB64=""
DO_BUILD_OSX=""
helper_script=""
do_no_strip=""

while [ $# -gt 0 ]
do
  case "$1" in

    clean|pull|checkout-dev|checkout-stable|build-images|preload-images)
      ACTION="$1"
      shift
      ;;

    --win32|--window32)
      DO_BUILD_WIN32="y"
      shift
      ;;
    --win64|--windows64)
      DO_BUILD_WIN64="y"
      shift
      ;;
    --deb32|--debian32)
      DO_BUILD_DEB32="y"
      shift
      ;;
    --deb64|--debian64)
      DO_BUILD_DEB64="y"
      shift
      ;;
    --osx)
      DO_BUILD_OSX="y"
      shift
      ;;

    --all)
      DO_BUILD_WIN32="y"
      DO_BUILD_WIN64="y"
      DO_BUILD_DEB32="y"
      DO_BUILD_DEB64="y"
      DO_BUILD_OSX="y"
      shift
      ;;

    --helper-script)
      helper_script=$2
      shift 2
      ;;

    --no-strip)
      do_no_strip="y"
      shift
      ;;

    --help)
      echo "Build the GNU ARM Eclipse ${APP_NAME} distributions."
      echo "Usage:"
      echo "    bash $0 [--helper-script file.sh] [--win32] [--win64] [--deb32] [--deb64] [--osx] [--all] [clean|pull|checkput-dev|checkout-stable|build-images] [--help]"
      echo
      exit 1
      ;;

    *)
      echo "Unknown action/option $1"
      exit 1
      ;;
  esac

done

# ----- Prepare build scripts. -----

build_script=$0
if [[ "${build_script}" != /* ]]
then
  # Make relative path absolute.
  build_script=$(pwd)/$0
fi

# Copy the current script to Work area, to later copy it into the install folder.
mkdir -p "${WORK_FOLDER}/scripts"
cp "${build_script}" "${WORK_FOLDER}/scripts/build-${APP_LC_NAME}.sh"


if [ -z "${helper_script}" ]
then
  if [ ! -f "${WORK_FOLDER}/scripts/build-helper.sh" ]
  then
    # Download helper script from SF git.
    echo "Downloading helper script..."
    curl -L "https://sourceforge.net/p/gnuarmeclipse/se/ci/develop/tree/scripts/build-helper.sh?format=raw" \
      --output "${WORK_FOLDER}/scripts/build-helper.sh"
  fi
else
  if [[ "${helper_script}" != /* ]]
  then
    # Make relative path absolute.
    helper_script="$(pwd)/${helper_script}"
  fi

  # Copy the current helper script to Work area, to later copy it into the install folder.
  mkdir -p "${WORK_FOLDER}/scripts"
  if [ "${helper_script}" != "${WORK_FOLDER}/scripts/build-helper.sh" ]
  then
    cp "${helper_script}" "${WORK_FOLDER}/scripts/build-helper.sh"
  fi
fi

helper_script="${WORK_FOLDER}/scripts/build-helper.sh"

BUILD_FOLDER="${WORK_FOLDER}/build"

# http://zlib.net
# https://sourceforge.net/projects/libpng/files/zlib/

LIBZ_VERSION="1.2.8"
LIBZ_FOLDER="zlib-${LIBZ_VERSION}"
LIBZ_ARCHIVE="${LIBZ_FOLDER}.tar.gz"
LIBZ_URL="https://sourceforge.net/projects/libpng/files/zlib/${LIBZ_VERSION}/${LIBZ_ARCHIVE}"


# SDL_image 1.2 requires PNG 12
# https://sourceforge.net/projects/libpng/files/libpng12/1.2.53/

LIBPNG_VERSION="1.6.17"
#LIBPNG_VERSION="1.2.53"
LIBPNG_SFOLDER="libpng16"
#LIBPNG_SFOLDER="libpng12"
LIBPNG_FOLDER="libpng-${LIBPNG_VERSION}"
LIBPNG_ARCHIVE="${LIBPNG_FOLDER}.tar.gz"
LIBPNG_URL="https://sourceforge.net/projects/libpng/files/${LIBPNG_SFOLDER}/${LIBPNG_VERSION}/${LIBPNG_ARCHIVE}"

# http://www.ijg.org
# http://www.ijg.org/files/

LIBJPG_VERSION="9a"
LIBJPG_FOLDER="jpeg-${LIBJPG_VERSION}"
LIBJPG_ARCHIVE="jpegsrc.v${LIBJPG_VERSION}.tar.gz"
LIBJPG_URL="http://www.ijg.org/files/${LIBJPG_ARCHIVE}"

# https://www.libsdl.org/download-1.2.php
# https://www.libsdl.org/release/SDL-1.2.15.tar.gz

LIBSDL_VERSION="1.2.15"
LIBSDL_FOLDER="SDL-${LIBSDL_VERSION}"
LIBSDL_ARCHIVE="${LIBSDL_FOLDER}.tar.gz"
LIBSDL_URL="https://www.libsdl.org/release/${LIBSDL_ARCHIVE}"


# https://www.libsdl.org/projects/SDL_image/release-1.2.html
# https://www.libsdl.org/projects/SDL_image/release/SDL_image-1.2.12.tar.gz

LIBSDL_IMAGE_VERSION="1.2.12"
LIBSDL_IMAGE_FOLDER="SDL_image-${LIBSDL_IMAGE_VERSION}"
LIBSDL_IMAGE_ARCHIVE="${LIBSDL_IMAGE_FOLDER}.tar.gz"
LIBSDL_IMAGE_URL="https://www.libsdl.org/projects/SDL_image/release/${LIBSDL_IMAGE_ARCHIVE}"


# ftp://sourceware.org/pub/libffi/libffi-3.2.1.tar.gz

LIBFFI_VERSION="3.2.1"
LIBFFI_FOLDER="libffi-${LIBFFI_VERSION}"
LIBFFI_ARCHIVE="${LIBFFI_FOLDER}.tar.gz"
LIBFFI_URL="ftp://sourceware.org/pub/libffi/${LIBFFI_ARCHIVE}"


# http://www.gnu.org/software/libiconv/
# http://ftp.gnu.org/pub/gnu/libiconv/

LIBICONV_VERSION="1.14"
LIBICONV_FOLDER="libiconv-${LIBICONV_VERSION}"
LIBICONV_ARCHIVE="${LIBICONV_FOLDER}.tar.gz"
LIBICONV_URL="http://ftp.gnu.org/pub/gnu/libiconv/${LIBICONV_ARCHIVE}"


# http://ftp.gnu.org/pub/gnu/gettext/

LIBGETTEXT_VERSION="0.19.5.1"
LIBGETTEXT_FOLDER="gettext-${LIBGETTEXT_VERSION}"
LIBGETTEXT_ARCHIVE="${LIBGETTEXT_FOLDER}.tar.xz"
LIBGETTEXT_URL="http://ftp.gnu.org/pub/gnu/gettext/${LIBGETTEXT_ARCHIVE}"


# http://ftp.gnome.org/pub/GNOME/sources/glib/2.44/glib-2.44.1.tar.xz

LIBGLIB_MVERSION="2.44"
LIBGLIB_VERSION="${LIBGLIB_MVERSION}.1"
LIBGLIB_FOLDER="glib-${LIBGLIB_VERSION}"
LIBGLIB_ARCHIVE="${LIBGLIB_FOLDER}.tar.xz"
LIBGLIB_URL="http://ftp.gnome.org/pub/GNOME/sources/glib/${LIBGLIB_MVERSION}/${LIBGLIB_ARCHIVE}"


# http://www.pixman.org
# http://cairographics.org/releases/

LIBPIXMAN_VERSION="0.32.6"
LIBPIXMAN_FOLDER="pixman-${LIBPIXMAN_VERSION}"
LIBPIXMAN_ARCHIVE="${LIBPIXMAN_FOLDER}.tar.gz"
LIBPIXMAN_URL="http://cairographics.org/releases/${LIBPIXMAN_ARCHIVE}"

# ----- Process actions. -----

if [ "${ACTION}" == "clean" ]
then
  # Remove most build and temporary folders.
  echo
  echo "Remove most of the build folders..."

  rm -rf "${BUILD_FOLDER}"
  rm -rf "${WORK_FOLDER}/install"
  rm -rf "${WORK_FOLDER}/${LIBZ_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBPNG_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBJPG_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBFFI_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBICONV_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBGETTEXT_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBGLIB_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBSDL_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBSDL_IMAGE_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBPIXMAN_FOLDER}"

  rm -rf "${WORK_FOLDER}/scripts"

  echo
  echo "Clean completed. Proceed with a regular build."

  exit 0
fi

# ----- Start build. -----

source "$helper_script" --start-timer

source "$helper_script" --detect-host

# ----- Define build constants. -----

GIT_FOLDER="${WORK_FOLDER}/gnuarmeclipse-${APP_LC_NAME}.git"

DOWNLOAD_FOLDER="${WORK_FOLDER}/download"

# ----- Prepare prerequisites. -----

source "$helper_script" --prepare-prerequisites

if [ -n "${DO_BUILD_WIN32}${DO_BUILD_WIN64}${DO_BUILD_DEB32}${DO_BUILD_DEB64}" ]
then
  source "$helper_script" --prepare-docker
fi

# ----- Process "preload-images" action. -----

if [ "${ACTION}" == "preload-images" ]
then
  echo
  echo "Check/Preload Docker images..."

  echo
  docker run --interactive --tty ilegeul/debian32:8-gnuarm-gcc-x11 \
  lsb_release --description --short

  echo
  docker run --interactive --tty ilegeul/debian:8-gnuarm-gcc-x11 \
  lsb_release --description --short

  echo
  docker run --interactive --tty ilegeul/debian:8-gnuarm-mingw \
  lsb_release --description --short

  echo
  docker images

  source "$helper_script" "--stop-timer"

  exit 0
fi


# ----- Process "build-images" action. -----

if [ "${ACTION}" == "build-images" ]
then
  echo
  echo "Build Docker images..."

  # Be sure it will not crash on errors, in case the images are already there.
  set +e

  docker build --tag "ilegeul/debian32:8-gnuarm-gcc-x11" \
  https://github.com/ilg-ul/docker/raw/master/debian32/8-gnuarm-gcc-x11/Dockerfile

  docker build --tag "ilegeul/debian:8-gnuarm-gcc-x11" \
  https://github.com/ilg-ul/docker/raw/master/debian/8-gnuarm-gcc-x11/Dockerfile

  docker build --tag "ilegeul/debian:8-gnuarm-mingw" \
  https://github.com/ilg-ul/docker/raw/master/debian/8-gnuarm-mingw/Dockerfile

  docker images

  source "$helper_script" "--stop-timer"

  exit 0
fi

# ----- Check some more prerequisites. -----

echo "Checking host automake..."
automake --version 2>/dev/null | grep automake

echo "Checking host patch..."
patch --version | grep patch

echo "Checking host tar..."
tar --version

echo "Checking host unzip..."
unzip | grep UnZip


do_repo_action() {

  # $1 = action (pull, checkout-dev, checkout-stable)

  # Update current branch and prepare autotools.
  echo
  if [ "${ACTION}" == "pull" ]
  then
    echo "Running git pull..."
  elif [ "${ACTION}" == "checkout-dev" ]
  then
    echo "Running git checkout gnuarmeclipse-dev & pull..."
  elif [ "${ACTION}" == "checkout-stable" ]
  then
    echo "Running git checkout gnuarmeclipse & pull..."
  fi

  if [ -d "${GIT_FOLDER}" ]
  then
    echo
    if [ "${USER}" == "ilg" ]
    then
      echo "Enter SourceForge password for git pull"
    fi

    cd "${GIT_FOLDER}"

    if [ "${ACTION}" == "checkout-dev" ]
    then
      git checkout gnuarmeclipse-dev
    elif [ "${ACTION}" == "checkout-stable" ]
    then
      git checkout gnuarmeclipse
    fi

    git pull
    git submodule update

    rm -rf "${BUILD_FOLDER}/${APP_LC_NAME}"

    echo
    echo "Pull completed. Proceed with a regular build."
    exit 0
  else
	echo "No git folder."
    exit 1
  fi

}


# ----- Process "pull|checkout-dev|checkout-stable" actions. -----

# For this to work, the following settings are required:
# git branch --set-upstream-to=origin/gnuarmeclipse-dev gnuarmeclipse-dev
# git branch --set-upstream-to=origin/gnuarmeclipse gnuarmeclipse

case "${ACTION}" in
  pull|checkout-dev|checkout-stable)
    do_repo_action "${ACTION}"
    ;;
esac


# ----- Get the GNU ARM Eclipse QEMU git repository. -----

# The custom QEMU branch is available from the dedicated Git repository
# which is part of the GNU ARM Eclipse project hosted on SourceForge.
# Generally this branch follows the official QEMU master branch,
# with updates after every QEMU public release.

if [ ! -d "${GIT_FOLDER}" ]
then

  cd "${WORK_FOLDER}"

  if [ "${USER}" == "ilg" ]
  then
    # Shortcut for ilg, who has full access to the repo.
    echo
    echo "Enter SourceForge password for git clone"
    git clone ssh://ilg-ul@git.code.sf.net/p/gnuarmeclipse/${APP_LC_NAME} gnuarmeclipse-${APP_LC_NAME}.git
  else
    # For regular read/only access, use the git url.
    git clone http://git.code.sf.net/p/gnuarmeclipse/${APP_LC_NAME} gnuarmeclipse-${APP_LC_NAME}.git
  fi

  # Change to the gnuarmeclipse branch. On subsequent runs use "git pull".
  cd "${GIT_FOLDER}"
  git checkout gnuarmeclipse-dev
  git submodule update

fi

# ----- Get the current Git branch name. -----

source "$helper_script" "--get-git-head"


# ----- Get current date. -----

source "$helper_script" "--get-current-date"

# ----- Get the Z library. -----

# Download the Z library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBZ_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBZ_URL}" --output "${LIBZ_ARCHIVE}"
fi

# Unpack the Z library -> done when building.

# ----- Get the PNG library. -----

# Download the PNG library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBPNG_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBPNG_URL}" --output "${LIBPNG_ARCHIVE}"
fi

# Unpack the PNG library.
if [ ! -d "${WORK_FOLDER}/${LIBPNG_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBPNG_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBPNG_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the JPG library. -----

# Download the JPG library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBJPG_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBJPG_URL}" --output "${LIBJPG_ARCHIVE}"
fi

# Unpack the JPG library.
if [ ! -d "${WORK_FOLDER}/${LIBJPG_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBJPG_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBJPG_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the SDL libraries. -----

# Download the SDL library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBSDL_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBSDL_URL}" --output "${LIBSDL_ARCHIVE}"
fi

# Unpack the SDL library.
if [ ! -d "${WORK_FOLDER}/${LIBSDL_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBSDL_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBSDL_FOLDER}"
  patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/sdl-1.2.15-no-CGDirectPaletteRef.patch"
  patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/sdl-1.2.15-x11.patch"
fi

# Download the SDL_image library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBSDL_IMAGE_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBSDL_IMAGE_URL}" --output "${LIBSDL_IMAGE_ARCHIVE}"
fi

# Unpack the SDL_image library.
if [ ! -d "${WORK_FOLDER}/${LIBSDL_IMAGE_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBSDL_IMAGE_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBSDL_IMAGE_FOLDER}"
  # patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the FFI library. -----

# Download the FFI library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBFFI_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBFFI_URL}" --output "${LIBFFI_ARCHIVE}"
fi

# Unpack the FFI library.
if [ ! -d "${WORK_FOLDER}/${LIBFFI_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBFFI_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBFFI_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the ICONV library. -----

# Download the ICONV library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBICONV_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBICONV_URL}" --output "${LIBICONV_ARCHIVE}"
fi

# Unpack the ICONV library.
if [ ! -d "${WORK_FOLDER}/${LIBICONV_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBICONV_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBICONV_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the GETTEXT library. -----

# Download the GETTEXT library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBGETTEXT_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBGETTEXT_URL}" --output "${LIBGETTEXT_ARCHIVE}"
fi

# Unpack the GETTEXT library.
if [ ! -d "${WORK_FOLDER}/${LIBGETTEXT_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBGETTEXT_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBGETTEXT_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the GLIB library. -----

# Download the GLIB library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBGLIB_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBGLIB_URL}" --output "${LIBGLIB_ARCHIVE}"
fi

# Unpack the GLIB library.
if [ ! -d "${WORK_FOLDER}/${LIBGLIB_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBGLIB_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBGLIB_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Get the PIXMAN library. -----

# Download the PIXMAN library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBPIXMAN_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${LIBPIXMAN_URL}" --output "${LIBPIXMAN_ARCHIVE}"
fi

# Unpack the PIXMAN library.
if [ ! -d "${WORK_FOLDER}/${LIBPIXMAN_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBPIXMAN_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBPIXMAN_FOLDER}"
  #patch -p0 -u --verbose < "${GIT_FOLDER}/gnuarmeclipse/patches/xxx.patch"
fi

# ----- Here insert the code to perform other downloads, if needed. -----

# v===========================================================================v
# Create the build script (needs to be separate for Docker).

script_name="build.sh"
script_file="${WORK_FOLDER}/scripts/${script_name}"

rm -f "${script_file}"
mkdir -p "$(dirname ${script_file})"
touch "${script_file}"

# Note: EOF is not quoted to allow local substitutions.
cat <<EOF >> "${script_file}"
#!/bin/bash
set -euo pipefail
IFS=\$'\n\t'

APP_NAME="${APP_NAME}"
APP_LC_NAME="${APP_LC_NAME}"
GIT_HEAD="${GIT_HEAD}"
DISTRIBUTION_FILE_DATE="${DISTRIBUTION_FILE_DATE}"

LIBZ_FOLDER="${LIBZ_FOLDER}"
LIBZ_ARCHIVE="${LIBZ_ARCHIVE}"

LIBPNG_FOLDER="${LIBPNG_FOLDER}"
LIBJPG_FOLDER="${LIBJPG_FOLDER}"
LIBSDL_FOLDER="${LIBSDL_FOLDER}"

LIBSDL_IMAGE_FOLDER="${LIBSDL_IMAGE_FOLDER}"
LIBSDL_IMAGE_ARCHIVE="${LIBSDL_IMAGE_ARCHIVE}"

LIBFFI_FOLDER="${LIBFFI_FOLDER}"
LIBICONV_FOLDER="${LIBICONV_FOLDER}"
LIBGETTEXT_FOLDER="${LIBGETTEXT_FOLDER}"
LIBGLIB_FOLDER="${LIBGLIB_FOLDER}"

LIBPIXMAN_FOLDER="${LIBPIXMAN_FOLDER}"

do_no_strip="${do_no_strip}"

EOF

# Note: EOF is quoted to prevent substitutions here.
cat <<'EOF' >> "${script_file}"

PKG_CONFIG_LIBDIR=${PKG_CONFIG_LIBDIR:-""}

# For just in case.
export LC_ALL="C"
export CONFIG_SHELL="/bin/bash"

script_name="$(basename "$0")"
args="$@"
docker_container_name=""

while [ $# -gt 0 ]
do
  case "$1" in
    --build-folder)
      build_folder="$2"
      shift 2
      ;;
    --docker-container-name)
      docker_container_name="$2"
      shift 2
      ;;
    --target-name)
      target_name="$2"
      shift 2
      ;;
    --target-bits)
      target_bits="$2"
      shift 2
      ;;
    --work-folder)
      work_folder="$2"
      shift 2
      ;;
    --output-folder)
      output_folder="$2"
      shift 2
      ;;
    --distribution-folder)
      distribution_folder="$2"
      shift 2
      ;;
    --install-folder)
      install_folder="$2"
      shift 2
      ;;
    --download-folder)
      download_folder="$2"
      shift 2
      ;;
    --helper-script)
      helper_script="$2"
      shift 2
      ;;
    *)
      echo "Unknown option $1, exit."
      exit 1
  esac
done

git_folder="${work_folder}/gnuarmeclipse-${APP_LC_NAME}.git"

DOWNLOAD_FOLDER="${work_folder}/download"

echo
uname -a

# Run the helper script in this shell, to get the support functions.
source "${helper_script}"

target_folder=${target_name}${target_bits:-""}

if [ "${target_name}" == "win" ]
then

  # For Windows targets, decide which cross toolchain to use.
  if [ ${target_bits} == "32" ]
  then
    cross_compile_prefix="i686-w64-mingw32"
  elif [ ${target_bits} == "64" ]
  then
    cross_compile_prefix="x86_64-w64-mingw32"
  fi

elif [ "${target_name}" == "osx" ]
then

  target_bits="64"

fi

if [ "${target_name}" == "debian" ]
then
  # for glib
  # apt-get install libffi-dev
  # apt-get -y install libx11-dev libxext-dev
  echo
fi

mkdir -p ${build_folder}
cd ${build_folder}

# ----- Test if various tools are present -----

echo
echo "Checking automake..."
automake --version 2>/dev/null | grep automake

if [ "${target_name}" != "osx" ]
then
  echo "Checking readelf..."
  readelf --version | grep readelf
fi

if [ "${target_name}" == "win" ]
then
  echo "Checking ${cross_compile_prefix}-gcc..."
  ${cross_compile_prefix}-gcc --version 2>/dev/null | egrep -e 'gcc|clang'

  echo "Checking unix2dos..."
  unix2dos --version 2>&1 | grep unix2dos

  echo "Checking makensis..."
  echo "makensis $(makensis -VERSION)"
else
  echo "Checking gcc..."
  gcc --version 2>/dev/null | egrep -e 'gcc|clang'
fi

if [ "${target_name}" == "osx" ]
then
  echo "Checking md5..."
  md5 -s "test"
else
  echo "Checking md5sum..."
  md5sum --version | grep md5sum
fi

# ----- Remove and recreate the output folder. -----

rm -rf "${output_folder}"
mkdir -p "${output_folder}"

# ----- Build and install the ZLIB library. -----

if [ ! -f "${install_folder}/lib/libz.a" ]
then

  rm -rf "${build_folder}/${LIBZ_FOLDER}"
  mkdir -p "${build_folder}/${LIBZ_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running zlib configure..."

  cd "${build_folder}"
  tar -xzvf "${DOWNLOAD_FOLDER}/${LIBZ_ARCHIVE}"

  cd "${build_folder}/${LIBZ_FOLDER}"

  if [ "${target_name}" == "win" ]
  then

    true

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "configure" \
      --prefix="${install_folder}"
      
  elif [ "${target_name}" == "osx" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "configure" \
      --prefix="${install_folder}"
      
  fi

  echo
  echo "Running zlib make install..."

  if [ "${target_name}" == "win" ]
  then

    sed -e 's/PREFIX =/#PREFIX =/' -e 's/STRIP = .*/STRIP = file /' -e 's/SHARED_MODE=0/SHARED_MODE=1/' win32/Makefile.gcc >win32/Makefile.gcc2

    CFLAGS="-m${target_bits} -pipe" \
    LDFLAGS="-v" \
    PREFIX="${cross_compile_prefix}-" \
    INCLUDE_PATH="${install_folder}/include" \
    LIBRARY_PATH="${install_folder}/lib" \
    BINARY_PATH="${install_folder}/bin" \
    make -f win32/Makefile.gcc2 install

  else

    # Build.
    make clean install
  fi

fi

# ----- Build and install the PNG library. -----

if [ ! -f "${install_folder}/lib/libpng.a" ]
then

  rm -rf "${build_folder}/${LIBPNG_FOLDER}"
  mkdir -p "${build_folder}/${LIBPNG_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libpng configure..."

  cd "${build_folder}/${LIBPNG_FOLDER}"

  # The explicit folders are needed to find zlib, pkg-config not used for it.
  if [ "${target_name}" == "win" ]
  then
    CPPFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPNG_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "debian" ]
  then
    CPPFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPNG_FOLDER}/configure" \
      --prefix="${install_folder}"
      
  elif [ "${target_name}" == "osx" ]
  then
    CPPFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPNG_FOLDER}/configure" \
      --prefix="${install_folder}"
      
  fi

  echo
  echo "Running libpng make install..."

  # Build.
  make clean install

fi

# ----- Build and install the JPG library. -----

if [ ! -f "${install_folder}/lib/libjpeg.a" ]
then

  rm -rf "${build_folder}/${LIBJPG_FOLDER}"
  mkdir -p "${build_folder}/${LIBJPG_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libjpg configure..."

  cd "${build_folder}/${LIBJPG_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBJPG_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBJPG_FOLDER}/configure" \
      --prefix="${install_folder}"
      
  elif [ "${target_name}" == "osx" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBJPG_FOLDER}/configure" \
      --prefix="${install_folder}"
      
  fi

  echo
  echo "Running libjpg make install..."

  # Build.
  make clean install

fi

# ----- Build and install the SDL library. -----

if [ ! -f "${install_folder}/lib/libSDL.a" ]
then

  rm -rf "${build_folder}/${LIBSDL_FOLDER}"
  mkdir -p "${build_folder}/${LIBSDL_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libSDL configure..."

  cd "${build_folder}/${LIBSDL_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}" \
      \
      --disable-stdio-redirect

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_FOLDER}/configure" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "osx" ]
  then

    # The system does not longer provide X11 support, so use MacPorts
    CFLAGS="-m${target_bits} -pipe -Wno-deprecated-declarations" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_FOLDER}/configure" \
      --prefix="${install_folder}" \
      --x-includes="/opt/local/include"

  fi

  echo
  echo "Running libSDL make install..."

  # Build.
  make clean install

fi

if [ ! -f "${install_folder}/lib/libSDL_image.a" ]
then

  rm -rf "${build_folder}/${LIBSDL_IMAGE_FOLDER}"
  mkdir -p "${build_folder}/${LIBSDL_IMAGE_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libSDL_image configure..."

  cd "${build_folder}/${LIBSDL_IMAGE_FOLDER}"

  # The explicit folders are needed to find jpeg, pkg-config not used for it.
  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    LIBS="-lpng16 -ljpeg" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_IMAGE_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --enable-jpg \
      --disable-jpg-shared \
      --enable-png \
      --disable-png-shared \
      --disable-bmp \
      --disable-gif \
      --disable-lbm \
      --disable-pcx \
      --disable-pnm \
      --disable-tga \
      --disable-tif \
      --disable-tif-shared \
      --disable-xcf \
      --disable-xpm \
      --disable-xv \
      --disable-webp \
      --disable-webp-shared

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    LIBPNG_LIBS="-lpng16 -ljpeg" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_IMAGE_FOLDER}/configure" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --enable-jpg \
      --disable-jpg-shared \
      --enable-png \
      --disable-png-shared \
      --disable-bmp \
      --disable-gif \
      --disable-lbm \
      --disable-pcx \
      --disable-pnm \
      --disable-tga \
      --disable-tif \
      --disable-tif-shared \
      --disable-xcf \
      --disable-xpm \
      --disable-xv \
      --disable-webp \
      --disable-webp-shared

  elif [ "${target_name}" == "osx" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    LIBS="-lpng16 -ljpeg" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBSDL_IMAGE_FOLDER}/configure" \
      --prefix="${install_folder}" \
      \
      --enable-jpg \
      --disable-jpg-shared \
      --enable-png \
      --disable-png-shared \
      --disable-bmp \
      --disable-gif \
      --disable-lbm \
      --disable-pcx \
      --disable-pnm \
      --disable-tga \
      --disable-tif \
      --disable-tif-shared \
      --disable-xcf \
      --disable-xpm \
      --disable-xv \
      --disable-webp \
      --disable-webp-shared

  fi

  echo
  echo "Running libSDL_image make install..."

  # Build.
  make clean install
fi

# ----- Build and install the FFI library. -----

if [ ! -f "${install_folder}/lib/libffi.a" ]
then

  rm -rf "${build_folder}/${LIBFFI_FOLDER}"
  mkdir -p "${build_folder}/${LIBFFI_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libffi configure..."

  cd "${build_folder}/${LIBFFI_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBFFI_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}" \

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBFFI_FOLDER}/configure" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "osx" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBFFI_FOLDER}/configure" \
      --prefix="${install_folder}"

  fi

  echo
  echo "Running libffi make install..."

  # Build.
  make clean install

fi

# ----- Build and install the ICONV library. -----

if [ "${target_name}" != "debian" ]
then

if [ ! -f "${install_folder}/lib/libiconv.la" ]
then

  rm -rf "${build_folder}/${LIBICONV_FOLDER}"
  mkdir -p "${build_folder}/${LIBICONV_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libiconv configure..."

  cd "${build_folder}/${LIBICONV_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBICONV_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --disable-nls

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBICONV_FOLDER}/configure" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --disable-nls

  elif [ "${target_name}" == "osx" ]
  then

    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBICONV_FOLDER}/configure" \
      --prefix="${install_folder}"

  fi

  echo
  echo "Running libiconv make install..."

  # Build.
  make clean install

fi

fi

# ----- Build and install the GETTEXT library. -----

if [ "${target_name}" != "debian" ]
then

if [ ! -f "${install_folder}/lib/libintl.a" ]
then

  rm -rf "${build_folder}/${LIBGETTEXT_FOLDER}"
  mkdir -p "${build_folder}/${LIBGETTEXT_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running gettext configure..."

  cd "${build_folder}/${LIBGETTEXT_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGETTEXT_FOLDER}/gettext-runtime/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --disable-java \
      --disable-native-java \
      --enable-csharp \
      --disable-c++ \
      --disable-libasprintf \
      --disable-openmp \
      --without-bzip2 \
      --without-xz \
      --without-emacs \
      --without-lispdir \
      --without-cvs

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGETTEXT_FOLDER}/gettext-runtime/configure" \
      --prefix="${install_folder}" \
      --with-gnu-ld \
      \
      --disable-java \
      --disable-native-java \
      --enable-csharp \
      --disable-c++ \
      --disable-libasprintf \
      --disable-openmp \
      --without-bzip2 \
      --without-xz \
      --without-emacs \
      --without-lispdir \
      --without-cvs

  elif [ "${target_name}" == "osx" ]
  then

    # The system does not longer provide X11 support, so use MacPorts
    CFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGETTEXT_FOLDER}/gettext-runtime/configure" \
      --prefix="${install_folder}" \
      \
      --disable-java \
      --disable-native-java \
      --enable-csharp \
      --disable-c++ \
      --disable-libasprintf \
      --disable-openmp \
      --without-bzip2 \
      --without-xz \
      --without-emacs \
      --without-lispdir \
      --without-cvs

  fi

  echo
  echo "Running gettext make install..."

  # Build.
  make clean install

fi

fi

# ----- Build and install the GLIB library. -----

if [ ! -f "${install_folder}/lib/libglib-2.0.la" ]
then

  rm -rf "${build_folder}/${LIBGLIB_FOLDER}"
  mkdir -p "${build_folder}/${LIBGLIB_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libglib configure..."

  cd "${build_folder}/${LIBGLIB_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGLIB_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGLIB_FOLDER}/configure" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "osx" ]
  then
    # To find libintl, add explicit paths.
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBGLIB_FOLDER}/configure" \
      --prefix="${install_folder}"

  fi

  echo
  echo "Running libglib make install..."

  # Build.
  make clean install

fi

# ----- Build and install the PIXMAN library. -----

if [ ! -f "${install_folder}/lib/libpixman-1.a" ]
then

  rm -rf "${build_folder}/${LIBPIXMAN_FOLDER}"
  mkdir -p "${build_folder}/${LIBPIXMAN_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running libpixman configure..."

  cd "${build_folder}/${LIBPIXMAN_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPIXMAN_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "debian" ]
  then
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPIXMAN_FOLDER}/configure" \
      --prefix="${install_folder}"

  elif [ "${target_name}" == "osx" ]
  then
    # To find libintl, add explicit paths.
    CFLAGS="-m${target_bits} -pipe -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${work_folder}/${LIBPIXMAN_FOLDER}/configure" \
      --prefix="${install_folder}"

  fi

  echo
  echo "Running libpixman make install..."

  # Build.
  make clean install

fi

# ----- Here insert the code to build more libraries, if needed. -----

# ----- Create the build folder. -----

mkdir -p "${build_folder}/${APP_LC_NAME}"

# ----- Configure QEMU. -----

if [ ! -f "${build_folder}/${APP_LC_NAME}/config-host.mak" ]
then

  echo
  echo "Running QEMU configure..."

  # All variables are passed on the command line before 'configure'.
  # Be sure all these lines end in '\' to ensure lines are concatenated.

  if [ "${target_name}" == "win" ]
  then

    # Windows target, 32/64-bit
    cd "${build_folder}/${APP_LC_NAME}"

    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --cross-prefix="${cross_compile_prefix}-" \
      \
      --extra-cflags="-g -pipe -I${install_folder}/include -Wno-missing-format-attribute -Wno-pointer-to-int-cast -D_POSIX=1 -mthreads" \
      --extra-ldflags="-v -L${install_folder}/lib" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/${APP_LC_NAME}" \
      --bindir="${install_folder}/${APP_LC_NAME}/bin" \
      --docdir="${install_folder}/${APP_LC_NAME}/doc" \
      --mandir="${install_folder}/${APP_LC_NAME}/man" \
      --enable-trace-backend=stderr \
      | tee "${output_folder}/configure-output.txt"

  elif [ "${target_name}" == "debian" ]
  then

    # Linux target
    cd "${build_folder}/${APP_LC_NAME}"

    #LDFLAGS="-v -Wl,-rpath=\$\$ORIGIN" \

    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --extra-cflags="-g -pipe -I${install_folder}/include -Wno-missing-format-attribute -Wno-error=format=" \
      --extra-ldflags="-v -Wl,-rpath=\$\$ORIGIN -L${install_folder}/lib" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/${APP_LC_NAME}" \
      --bindir="${install_folder}/${APP_LC_NAME}/bin" \
      --docdir="${install_folder}/${APP_LC_NAME}/doc" \
      --mandir="${install_folder}/${APP_LC_NAME}/man" \
      --enable-trace-backend=stderr \
    | tee "${output_folder}/configure-output.txt"

    # Note: a very important detail here is
    #   --extra-ldflags='-Wl,-rpath=\$$ORIGIN
    # which adds a special record to the ELF file asking the loader to search
    # for the libraries first in the same folder where the executable is
    # located. The task is complicated due to the multiple substitutions
    # that are done on the way, and need to be escaped.

  elif [ "${target_name}" == "osx" ]
  then

    # OS X target
    cd "${build_folder}/${APP_LC_NAME}"

    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --extra-cflags="-g -pipe -I${install_folder}/include -Wno-missing-format-attribute" \
      --extra-ldflags="-v -L${install_folder}/lib" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/${APP_LC_NAME}" \
      --bindir="${install_folder}/${APP_LC_NAME}/bin" \
      --docdir="${install_folder}/${APP_LC_NAME}/doc" \
      --mandir="${install_folder}/${APP_LC_NAME}/man" \
      --enable-trace-backend=stderr \
    | tee "${output_folder}/configure-output.txt"

    # Configure fails for --static

  fi

fi

cd "${build_folder}/${APP_LC_NAME}"
cp config.* "${output_folder}"

# ----- Full build, with documentation. -----

if [ ! \( -f "${build_folder}/${APP_LC_NAME}/gnuarmeclipse-softmmu/qemu-system-gnuarmeclipse" \) -a \
     ! \( -f "${build_folder}/${APP_LC_NAME}/gnuarmeclipse-softmmu/qemu-system-gnuarmeclipse.exe" \) ]
then

  echo
  echo "Running QEMU make all..."

  cd "${build_folder}/${APP_LC_NAME}"
  make all pdf \
  | tee "${output_folder}/make-all-output.txt"

fi

# ----- Full install, including documentation. -----

echo
echo "Running QEMU make install..."

# Always clear the destination folder, to have a consistent package.
rm -rf "${install_folder}/${APP_LC_NAME}"

# Exhaustive install, including documentation.

cd "${build_folder}/${APP_LC_NAME}"
make install install-pdf

# cd "${build_folder}/${APP_LC_NAME}/pixman"
# make install


# Remove useless files

# rm -rf "${install_folder}/${APP_LC_NAME}/etc"

# ----- Copy dynamic libraries to the install bin folder. -----

if [ "${target_name}" == "win" ]
then

  if [ -z "${do_no_strip}" ]
  then
    do_strip ${cross_compile_prefix}-strip \
      "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse.exe"
  fi

  echo
  echo "Copying DLLs..."

  # Identify the current cross gcc version, to locate the specific dll folder.
  CROSS_GCC_VERSION=$(${cross_compile_prefix}-gcc --version | grep 'gcc' | sed -e 's/.*\s\([0-9]*\)[.]\([0-9]*\)[.]\([0-9]*\).*/\1.\2.\3/')
  CROSS_GCC_VERSION_SHORT=$(echo $CROSS_GCC_VERSION | sed -e 's/\([0-9]*\)[.]\([0-9]*\)[.]\([0-9]*\).*/\1.\2/')
  SUBLOCATION="-win32"

  echo "${CROSS_GCC_VERSION}" "${CROSS_GCC_VERSION_SHORT}" "${SUBLOCATION}"

  # find "/usr/lib/gcc/${cross_compile_prefix}" -name '*.dll'
  # find "/usr/${cross_compile_prefix}" -name '*.dll'

  if [ "${target_bits}" == "32" ]
  then

    do_copy_gcc_dll "libgcc_s_sjlj-1.dll"
    do_copy_gcc_dll "libssp-0.dll"
    do_copy_gcc_dll "libstdc++-6.dll"

    do_copy_libwinpthread_dll

  elif [ "${target_bits}" == "64" ]
  then

    do_copy_gcc_dll "libgcc_s_seh-1.dll"
    do_copy_gcc_dll "libssp-0.dll"
    do_copy_gcc_dll "libstdc++-6.dll"

  fi

  # Copy all compiled DLLs
  cp -v "${install_folder}/bin/"*.dll "${install_folder}/${APP_LC_NAME}/bin"

  if [ -z "${do_no_strip}" ]
  then
    do_strip ${cross_compile_prefix}-strip "${install_folder}/${APP_LC_NAME}/bin/"*.dll
  fi

  # Remove some unexpected files.
  # rm -f "${install_folder}/${APP_LC_NAME}/bin/target-x86_64.conf"
  # rm -f "${install_folder}/${APP_LC_NAME}/bin/trace-events"
  rm -f "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipsew.exe"

elif [ "${target_name}" == "debian" ]
then

  if [ -z "${do_no_strip}" ]
  then
    do_strip strip "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  fi

  echo
  echo "Copying shared libs..."

  if [ "${target_bits}" == "64" ]
  then
    distro_machine="x86_64"
  elif [ "${target_bits}" == "32" ]
  then
    distro_machine="i386"
  fi

  do_copy_user_so libSDL-1.2
  do_copy_user_so libSDL_image-1.2
  do_copy_user_so libpng16

  do_copy_user_so libjpeg
  do_copy_user_so libffi

  do_copy_user_so libgthread-2.0
  do_copy_user_so libglib-2.0
  do_copy_user_so libpixman-1

  # do_copy_system_dll libpcre
  do_copy_user_so libz

  do_copy_librt_so

  ILIB=$(find /lib/${distro_machine}-linux-gnu /usr/lib/${distro_machine}-linux-gnu -type f -name 'libutil-*.so' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found system ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin"
    (cd "${install_folder}/${APP_LC_NAME}/bin"; ln -sv "${ILIB_BASE}" "libutil.so.1")
    (cd "${install_folder}/${APP_LC_NAME}/bin"; ln -sv "${ILIB_BASE}" "libutil.so")
  else
    echo libutil not found
    exit 1
  fi

if false
then
  ILIB=$(find /lib/${distro_machine}-linux-gnu /usr/lib/${distro_machine}-linux-gnu -type f -name 'libpthread-*.so' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found system ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin"
    (cd "${install_folder}/${APP_LC_NAME}/bin"; ln -sv "${ILIB_BASE}" "libpthread.so.0")
    (cd "${install_folder}/${APP_LC_NAME}/bin"; ln -sv "${ILIB_BASE}" "libpthread.so")
  else
    echo libpthread not found
    exit 1
  fi
fi

elif [ "${target_name}" == "osx" ]
then

  if [ -z "${do_no_strip}" ]
  then
    do_strip strip "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  fi

  echo
  echo "Copying dynamic libs..."

  # Copy the dynamic libraries to the same folder where the application file is.
  # Post-process dynamic libraries paths to be relative to executable folder.

  echo
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libgnutls.28.dylib" \
    "@executable_path/libgnutls.28.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libusb-1.0.0.dylib" \
    "@executable_path/libusb-1.0.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libz.1.dylib" \
    "@executable_path/libz.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libpixman-1.0.dylib" \
    "@executable_path/libpixman-1.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libSDL-1.2.0.dylib" \
    "@executable_path/libSDL-1.2.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libSDL_image-1.2.0.dylib" \
    "@executable_path/libSDL_image-1.2.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libX11.6.dylib" \
    "@executable_path/libX11.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libgthread-2.0.0.dylib" \
    "@executable_path/libgthread-2.0.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libglib-2.0.0.dylib" \
    "@executable_path/libglib-2.0.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libintl.8.dylib" \
    "@executable_path/libintl.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "${install_folder}/lib/libpixman-1.0.dylib" \
    "@executable_path/libpixman-1.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/qemu-system-gnuarmeclipse"

  echo
  # Local
  ILIB=libSDL-1.2.0.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  # Local
  ILIB=libSDL_image-1.2.0.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libSDL-1.2.0.dylib" \
    "@executable_path/libSDL-1.2.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libpng16.16.dylib" \
    "@executable_path/libpng16.16.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libjpeg.9.dylib" \
    "@executable_path/libjpeg.9.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libz.1.dylib" \
    "@executable_path/libz.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  # Local
  ILIB=libpng16.16.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libz.1.dylib" \
    "@executable_path/libz.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  # Local
  ILIB=libjpeg.9.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libz.1.dylib" \
    "@executable_path/libz.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libffi.6.dylib
  cp -v "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libiconv.2.dylib
  cp -v "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libintl.8.dylib
  cp -v "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id ${ILIB} "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libiconv.2.dylib" \
    "@executable_path/libiconv.2.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"


  echo
  ILIB=libglib-2.0.0.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libiconv.2.dylib" \
    "@executable_path/libiconv.2.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libintl.8.dylib" \
    "@executable_path/libintl.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libgthread-2.0.0.dylib
  cp "${install_folder}/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${DLIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libglib-2.0.0.dylib" \
    "@executable_path/libglib-2.0.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libiconv.2.dylib" \
    "@executable_path/libiconv.2.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "${install_folder}/lib/libintl.8.dylib" \
    "@executable_path/libintl.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  # Different input name
  ILIB=libz.1.dylib
  cp -v "/opt/local/lib/libz.1.2.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id ${ILIB} "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libgnutls.28.dylib
  cp -v "/opt/local/lib/${ILIB}" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id libgnutls.28.dylib "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libz.1.dylib" \
    "@executable_path/libz.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libiconv.2.dylib" \
    "@executable_path/libiconv.2.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libp11-kit.0.dylib" \
    "@executable_path/libp11-kit.0.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libtasn1.6.dylib" \
    "@executable_path/libtasn1.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libnettle.4.dylib" \
    "@executable_path/libnettle.4.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libhogweed.2.dylib" \
    "@executable_path/libhogweed.2.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libgmp.10.dylib" \
    "@executable_path/libgmp.10.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libintl.8.dylib" \
    "@executable_path/libintl.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libp11-kit.0.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libffi.6.dylib" \
    "@executable_path/libffi.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libintl.8.dylib" \
    "@executable_path/libintl.8.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libtasn1.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libnettle.4.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libhogweed.2.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libnettle.4.dylib" \
    "@executable_path/libnettle.4.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libgmp.10.dylib" \
    "@executable_path/libgmp.10.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libgmp.10.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libpixman-1.0.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libX11.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libxcb.1.dylib" \
    "@executable_path/libxcb.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libXext.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libX11.6.dylib" \
    "@executable_path/libX11.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libXrandr.2.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libXext.6.dylib" \
    "@executable_path/libXext.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libXrender.1.dylib" \
    "@executable_path/libXrender.1.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libX11.6.dylib" \
    "@executable_path/libX11.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libXrender.1.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libX11.6.dylib" \
    "@executable_path/libX11.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"


  echo
  ILIB=libxcb.1.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libXau.6.dylib" \
    "@executable_path/libXau.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libXdmcp.6.dylib" \
    "@executable_path/libXdmcp.6.dylib" \
    "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libXau.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  echo
  ILIB=libXdmcp.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  # otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"
  otool -L "${install_folder}/${APP_LC_NAME}/bin/${ILIB}"

  # Do not strip resulting dylib files!

fi

# ----- Copy the images files. -----

mkdir -p "${install_folder}/${APP_LC_NAME}/share/qemu/images"
cp "${git_folder}/gnuarmeclipse/images/"* \
  "${install_folder}/${APP_LC_NAME}/share/qemu/images"

# ----- Copy the license files. -----

echo
echo "Copying license files..."

do_copy_license "${git_folder}" "qemu-$(cat ${git_folder}/VERSION)"
do_copy_license "${work_folder}/${LIBGETTEXT_FOLDER}" "${LIBGETTEXT_FOLDER}"
do_copy_license "${work_folder}/${LIBGLIB_FOLDER}" "${LIBGLIB_FOLDER}"
do_copy_license "${work_folder}/${LIBPNG_FOLDER}" "${LIBPNG_FOLDER}"
do_copy_license "${work_folder}/${LIBJPG_FOLDER}" "${LIBJPG_FOLDER}"
do_copy_license "${work_folder}/${LIBSDL_FOLDER}" "${LIBSDL_FOLDER}"
do_copy_license "${work_folder}/${LIBSDL_IMAGE_FOLDER}" "${LIBSDL_IMAGE_FOLDER}"
do_copy_license "${work_folder}/${LIBFFI_FOLDER}" "${LIBFFI_FOLDER}"
do_copy_license "${work_folder}/${LIBZ_FOLDER}" "${LIBZ_FOLDER}"
do_copy_license "${work_folder}/${LIBPIXMAN_FOLDER}" "${LIBPIXMAN_FOLDER}"

if [ "${target_name}" != "debian" ]
then
  do_copy_license "${work_folder}/${LIBICONV_FOLDER}" "${LIBICONV_FOLDER}"
fi

if [ "${target_name}" == "win" ]
then
  # For Windows, process cr lf
  find "${install_folder}/${APP_LC_NAME}/license" -type f \
    -exec unix2dos {} \;
fi

# ----- Copy the GNU ARM Eclipse info files. -----

source "$helper_script" --copy-info


# ----- Create the distribution package. -----

mkdir -p "${output_folder}"

if [ "${GIT_HEAD}" == "gnuarmeclipse" ]
then
  distribution_file_version=$(cat "${git_folder}/VERSION")-${DISTRIBUTION_FILE_DATE}
elif [ "${GIT_HEAD}" == "gnuarmeclipse-dev" ]
then
  distribution_file_version=$(cat "${git_folder}/VERSION")-${DISTRIBUTION_FILE_DATE}-dev
fi

distribution_executable_name="qemu-system-gnuarmeclipse"

source "$helper_script" --create-distribution

# Requires ${distribution_file} and ${result}
source "$helper_script" --completed

exit 0

EOF
# The above marker must start in the first column.
# ^===========================================================================^


# ----- Build the OS X distribution. -----

if [ "${HOST_UNAME}" == "Darwin" ]
then
  if [ "${DO_BUILD_OSX}" == "y" ]
  then
    do_build_target "Creating OS X package..." \
      --target-name osx
  fi
fi

# ----- Build the Windows 64-bits distribution. -----

if [ "${DO_BUILD_WIN64}" == "y" ]
then
  do_build_target "Creating Windows 64-bits setup..." \
    --target-name win \
    --target-bits 64 \
    --docker-image ilegeul/debian:8-gnuarm-mingw
fi

# ----- Build the Windows 32-bits distribution. -----

if [ "${DO_BUILD_WIN32}" == "y" ]
then
  do_build_target "Creating Windows 32-bits setup..." \
    --target-name win \
    --target-bits 32 \
    --docker-image ilegeul/debian:8-gnuarm-mingw
fi

# ----- Build the Debian 64-bits distribution. -----

if [ "${DO_BUILD_DEB64}" == "y" ]
then
  do_build_target "Creating Debian 64-bits archive..." \
    --target-name debian \
    --target-bits 64 \
    --docker-image ilegeul/debian:8-gnuarm-gcc-x11
fi

# ----- Build the Debian 32-bits distribution. -----

if [ "${DO_BUILD_DEB32}" == "y" ]
then
  do_build_target "Creating Debian 32-bits archive..." \
    --target-name debian \
    --target-bits 32 \
    --docker-image ilegeul/debian32:8-gnuarm-gcc-x11
fi

cat "${WORK_FOLDER}/output/"*.md5

source "$helper_script" "--stop-timer"

# ----- Done. -----
exit 0
