#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

# Script to build the GNU ARM Eclipse OpenOCD distribution packages.
#
# Developed on OS X.
# Also tested on:
#   -
#
# The Windows and GNU/Linux packages are build using Docker containers.
# The build is structured in 2 steps, one running on the host machine
# and one running inside the Docker container.
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
#   sudo port install cmake boost libconfuse swig-python
#   sudo port install texinfo texlive
#

# Mandatory definition.
APP_NAME="OpenOCD"

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

    --help)
      echo "Build the GNU ARM Eclipse ${APP_NAME} distributions."
      echo "Usage:"
      echo "    bash $0 helper_script [--win32] [--win64] [--deb32] [--deb64] [--osx] [--all] [clean|pull|checkput-dev|checkout-stable|build-images] [--help]"
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

# For updates, please check the corresponding pages.

# https://sourceforge.net/projects/libusb/files/libusb-1.0/
LIBUSB1_VERSION="1.0.19"
LIBUSB1_FOLDER="libusb-${LIBUSB1_VERSION}"
LIBUSB1="${LIBUSB1_FOLDER}"
LIBUSB1_ARCHIVE="${LIBUSB1}.tar.bz2"

# https://sourceforge.net/projects/libusb/files/libusb-compat-0.1/
LIBUSB0_VERSION="0.1.5"
LIBUSB0_FOLDER="libusb-compat-${LIBUSB0_VERSION}"
LIBUSB0="${LIBUSB0_FOLDER}"
LIBUSB0_ARCHIVE="${LIBUSB0_FOLDER}.tar.bz2"

# https://sourceforge.net/projects/libusb-win32/files/libusb-win32-releases/
LIBUSB_W32_PREFIX="libusb-win32"
LIBUSB_W32_VERSION="1.2.6.0"
LIBUSB_W32="${LIBUSB_W32_PREFIX}-${LIBUSB_W32_VERSION}"
LIBUSB_W32_FOLDER="${LIBUSB_W32_PREFIX}-src-${LIBUSB_W32_VERSION}"
LIBUSB_W32_ARCHIVE="${LIBUSB_W32_FOLDER}.zip"

#   http://www.intra2net.com/en/developer/libftdi/download.php
LIBFTDI_VERSION="1.2"
LIBFTDI_FOLDER="libftdi1-${LIBFTDI_VERSION}"
LIBFTDI_ARCHIVE="${LIBFTDI_FOLDER}.tar.bz2"
LIBFTDI="${LIBFTDI_FOLDER}"

# https://github.com/signal11/hidapi/downloads
HIDAPI_VERSION="0.7.0"
HIDAPI_FOLDER="hidapi-${HIDAPI_VERSION}"
HIDAPI="${HIDAPI_FOLDER}"
HIDAPI_ARCHIVE="${HIDAPI}.zip"


# ----- Process actions. -----

if [ "${ACTION}" == "clean" ]
then
  # Remove most build and temporary folders.
  echo
  echo "Remove most of the build folders..."

  rm -rf "${BUILD_FOLDER}"
  rm -rf "${WORK_FOLDER}/install"

  rm -rf "${WORK_FOLDER}/${LIBUSB1_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBUSB0_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBUSB_W32_FOLDER}"
  rm -rf "${WORK_FOLDER}/${LIBFTDI_FOLDER}"
  rm -rf "${WORK_FOLDER}/${HIDAPI_FOLDER}"

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

# ----- Process "preload-images" action. -----

if [ "${ACTION}" == "preload-images" ]
then
  echo
  echo "Check/Preload Docker images..."

  echo
  docker run --interactive --tty ilegeul/debian32:7-gnuarm-gcc \
  lsb_release --description --short

  echo
  docker run --interactive --tty ilegeul/debian:7-gnuarm-gcc \
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
  # Remove most build and temporary folders.
  echo
  echo "Build Docker images..."

  # Be sure it will not crash on errors, in case the images are already there.
  set +e

  docker build --tag "ilegeul/debian32:7-gnuarm-gcc" \
  https://github.com/ilg-ul/docker/raw/master/debian32/7-gnuarm-gcc/Dockerfile

  docker build --tag "ilegeul/debian:7-gnuarm-gcc" \
  https://github.com/ilg-ul/docker/raw/master/debian/7-gnuarm-gcc/Dockerfile

  docker build --tag "ilegeul/debian:8-gnuarm-gcc" \
  https://github.com/ilg-ul/docker/raw/master/debian/8-gnuarm-gcc/Dockerfile

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


# ----- Get the GNU ARM Eclipse OpenOCD git repository. -----

# The custom OpenOCD branch is available from the dedicated Git repository
# which is part of the GNU ARM Eclipse project hosted on SourceForge.
# Generally this branch follows the official OpenOCD master branch, 
# with updates after every OpenOCD public release.

if [ ! -d "${GIT_FOLDER}" ]
then

  cd "${WORK_FOLDER}"

  if [ "${USER}" == "ilg" ]
  then
    # Shortcut for ilg, who has full access to the repo.
    echo
    echo "Enter SourceForge password for git clone"
    git clone ssh://ilg-ul@git.code.sf.net/p/gnuarmeclipse/openocd gnuarmeclipse-${APP_LC_NAME}.git
  else
    # For regular read/only access, use the git url.
    git clone http://git.code.sf.net/p/gnuarmeclipse/openocd gnuarmeclipse-${APP_LC_NAME}.git
  fi

  # Change to the gnuarmeclipse branch. On subsequent runs use "git pull".
  cd "${GIT_FOLDER}"
  git checkout gnuarmeclipse-dev
  git submodule update

  # Prepare autotools.
  echo
  echo "bootstrap..."

  cd "${GIT_FOLDER}"
  ./bootstrap

fi

# Get the current Git branch name, to know if we are building the stable or
# the development release.
cd "${GIT_FOLDER}"
GIT_HEAD=$(git symbolic-ref -q --short HEAD)

# ----- Get current date. -----

# Use the UTC date as version in the name of the distribution file.
DISTRIBUTION_FILE_DATE=${DISTRIBUTION_FILE_DATE:-$(date -u +%Y%m%d%H%M)}

# ----- Get the USB libraries. -----

# Both USB libraries are available from a single project LIBUSB
# 	http://www.libusb.info
# with source files ready to download from SourceForge
# 	https://sourceforge.net/projects/libusb/files

# Download the new USB library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBUSB1_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "http://sourceforge.net/projects/libusb/files/libusb-1.0/${LIBUSB1_FOLDER}/${LIBUSB1_ARCHIVE}" \
    --output "${LIBUSB1_ARCHIVE}"
fi

# Unpack the new USB library.
if [ ! -d "${WORK_FOLDER}/${LIBUSB1_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBUSB1_ARCHIVE}"
fi

# http://www.libusb.org

# Download the old USB library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBUSB0_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "http://sourceforge.net/projects/libusb/files/libusb-compat-0.1/${LIBUSB0_FOLDER}/${LIBUSB0_ARCHIVE}" \
    --output "${LIBUSB0_ARCHIVE}"
fi

# Unpack the old USB library.
if [ ! -d "${WORK_FOLDER}/${LIBUSB0_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBUSB0_ARCHIVE}"
fi

# https://sourceforge.net/projects/libusb-win32

# Download the old Win32 USB library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBUSB_W32_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "http://sourceforge.net/projects/libusb-win32/files/libusb-win32-releases/${LIBUSB_W32_VERSION}/${LIBUSB_W32_ARCHIVE}" \
    --output "${LIBUSB_W32_ARCHIVE}"
fi

# Unpack the old Win32 USB library.
if [ ! -d "${WORK_FOLDER}/${LIBUSB_W32_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  unzip "${DOWNLOAD_FOLDER}/${LIBUSB_W32_ARCHIVE}"
fi


# ----- Get the FTDI library. -----

# There are two versions of the FDDI library; we recommend using the 
# open source one, available from intra2net.
#	http://www.intra2net.com/en/developer/libftdi/

# Download the FTDI library.
if [ ! -f "${DOWNLOAD_FOLDER}/${LIBFTDI_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "http://www.intra2net.com/en/developer/libftdi/download/${LIBFTDI_ARCHIVE}" \
    --output "${LIBFTDI_ARCHIVE}"
fi

# Unpack the FTDI library.
if [ ! -d "${WORK_FOLDER}/${LIBFTDI_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  tar -xjvf "${DOWNLOAD_FOLDER}/${LIBFTDI_ARCHIVE}"

  cd "${WORK_FOLDER}/${LIBFTDI_FOLDER}"
  # Patch to prevent the use of system libraries and force the use of local ones.
  patch -p0 < "${GIT_FOLDER}/gnuarmeclipse/patches/${LIBFTDI}-cmake-FindUSB1.patch"
fi

# ----- Get the HDI library. -----

# This is just a simple wrapper over libusb.
# http://www.signal11.us/oss/hidapi/

# Download the HDI library.
if [ ! -f "${DOWNLOAD_FOLDER}/${HIDAPI_ARCHIVE}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "https://github.com/downloads/signal11/hidapi/${HIDAPI_ARCHIVE}" \
    --output "${HIDAPI_ARCHIVE}"
fi

# Unpack the HDI library.
if [ ! -d "${WORK_FOLDER}/${HIDAPI_FOLDER}" ]
then
  cd "${WORK_FOLDER}"
  unzip "${DOWNLOAD_FOLDER}/${HIDAPI_ARCHIVE}"
fi


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

LIBUSB1_FOLDER="${LIBUSB1_FOLDER}"
LIBUSB0_FOLDER="${LIBUSB0_FOLDER}"
LIBUSB_W32="${LIBUSB_W32}"
LIBUSB_W32_FOLDER="${LIBUSB_W32_FOLDER}"
LIBFTDI_FOLDER="${LIBFTDI_FOLDER}"
HIDAPI_FOLDER="${HIDAPI_FOLDER}"
HIDAPI="${HIDAPI}"

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

mkdir -p ${build_folder}
cd ${build_folder}

# ----- Test if various tools are present -----

echo
echo "Checking automake..."
automake --version 2>/dev/null | grep automake

echo "Checking cmake..."
cmake --version | grep cmake

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

# ----- Remove and recreate the output folder. -----

rm -rf "${output_folder}"
mkdir -p "${output_folder}"

# ----- Build and install the new USB library. -----

if [ ! \( -f "${install_folder}/lib/libusb-1.0.a" -o \
          -f "${install_folder}/lib64/libusb-1.0.a" \) ]
then

  rm -rfv "${build_folder}/${LIBUSB1_FOLDER}"
  mkdir -p "${build_folder}/${LIBUSB1_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running configure libusb1..."

  cd "${build_folder}/${LIBUSB1_FOLDER}"

  if [ "${target_name}" == "win" ]
  then
    CFLAGS="-Wno-non-literal-null-conversion -m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/cross-pkg-config" \
    "${work_folder}/${LIBUSB1_FOLDER}/configure" \
      --host="${cross_compile_prefix}" \
      --prefix="${install_folder}"
  else
    CFLAGS="-Wno-non-literal-null-conversion -m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/cross-pkg-config" \
    "${work_folder}/${LIBUSB1_FOLDER}/configure" \
      --prefix="${install_folder}"
  fi

  echo
  echo "Running make libusb1..."

  # Build.
  make clean install

  if [ "${target_name}" == "win" ]
  then
    # Remove DLLs to force static link for final executable.
    rm -f "${install_folder}/bin/libusb-1.0.dll"
    rm -f "${install_folder}/lib/libusb-1.0.dll.a"
    rm -f "${install_folder}/lib/libusb-1.0.la"
  fi

fi

# ----- Build and install the old USB library. -----

if [ \( "${target_name}" != "win" \) -a \
     ! \( -f "${install_folder}/lib/libusb.a" -o \
          -f "${install_folder}/lib64/libusb.a" \) ]
then

  rm -rf "${build_folder}/${LIBUSB0_FOLDER}"
  mkdir -p "${build_folder}/${LIBUSB0_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running configure libusb0..."

  cd "${build_folder}/${LIBUSB0_FOLDER}"

  CFLAGS="-m${target_bits} -pipe" \
  \
  PKG_CONFIG_LIBDIR=\
"${install_folder}/lib/pkgconfig":\
"${install_folder}/lib64/pkgconfig" \
  \
  "${work_folder}/${LIBUSB0_FOLDER}/configure" \
    --prefix="${install_folder}"

  echo
  echo "Running make libusb0..."

  # Build.
  make clean install

fi

# ----- Build and install the old Win32 USB library. -----

if [ \( "${target_name}" == "win" \) -a \
     ! \( -f "${install_folder}/lib/libusb.a" -o \
          -f "${install_folder}/lib64/libusb.a" \)  ]
then

  mkdir -p "${build_folder}/${LIBUSB_W32}"

  cd "${build_folder}/${LIBUSB_W32}"
  cp -r "${work_folder}/${LIBUSB_W32_FOLDER}/"* \
    "${build_folder}/${LIBUSB_W32}"

  echo
  echo "Running make libusb-win32..."

  cd "${build_folder}/${LIBUSB_W32}"

  # Patch from:
  # https://gitorious.org/jtag-tools/openocd-mingw-build-scripts
  patch -p1 < "${git_folder}/gnuarmeclipse/patches/${LIBUSB_W32}-mingw-w64.patch"

  # Build.
  CFLAGS="-m${target_bits} -pipe" \
  make host_prefix=${cross_compile_prefix} host_prefix_x86=i686-w64-mingw32 dll

  mkdir -p "${install_folder}/bin"
  cp -v "${build_folder}/${LIBUSB_W32}/libusb0.dll" \
     "${install_folder}/bin"

  mkdir -p "${install_folder}/lib"
  cp -v "${build_folder}/${LIBUSB_W32}/libusb.a" \
     "${install_folder}/lib"

  mkdir -p "${install_folder}/lib/pkgconfig"
  sed -e "s|XXX|${install_folder}|" \
    "${git_folder}/gnuarmeclipse/pkgconfig/${LIBUSB_W32}.pc" \
    > "${install_folder}/lib/pkgconfig/libusb.pc"

  mkdir -p "${install_folder}/include/libusb"
  cp -v "${build_folder}/${LIBUSB_W32}/src/lusb0_usb.h" \
     "${install_folder}/include/libusb/usb.h"

fi

# ----- Build and install the FTDI library. -----

if [ ! \( -f "${install_folder}/lib/libftdi1.a" -o \
           -f "${install_folder}/lib64/libftdi1.a" \)  ]
then

  rm -rfv "${build_folder}/${LIBFTDI_FOLDER}"
  mkdir -p "${build_folder}/${LIBFTDI_FOLDER}"

  mkdir -p "${install_folder}"

  echo
  echo "Running cmake libftdi..."

  cd "${build_folder}/${LIBFTDI_FOLDER}"

  if [ "${target_name}" == "win" ]
  then

    # Configure.
    CFLAGS="-m${target_bits} -pipe" \
    \
    PKG_CONFIG_LIBDIR=\
"${install_folder}/lib/pkgconfig":\
"${install_folder}/lib64/pkgconfig" \
    \
    cmake \
    -DPKG_CONFIG_EXECUTABLE="${git_folder}/gnuarmeclipse/scripts/cross-pkg-config" \
    -DCMAKE_TOOLCHAIN_FILE="${work_folder}/${LIBFTDI_FOLDER}/cmake/Toolchain-${cross_compile_prefix}.cmake" \
    -DCMAKE_INSTALL_PREFIX="${install_folder}" \
    -DLIBUSB_INCLUDE_DIR="${install_folder}/include/libusb-1.0" \
    -DLIBUSB_LIBRARIES="${install_folder}/lib/libusb-1.0.a" \
    -DBUILD_TESTS:BOOL=off \
    -DFTDIPP:BOOL=off \
    -DPYTHON_BINDINGS:BOOL=off \
    -DEXAMPLES:BOOL=off \
    -DDOCUMENTATION:BOOL=off \
    -DFTDI_EEPROM:BOOL=off \
    "${work_folder}/${LIBFTDI_FOLDER}"

  else

    CFLAGS="-m${target_bits} -pipe" \
    \
    PKG_CONFIG_LIBDIR=\
"${install_folder}/lib/pkgconfig":\
"${install_folder}/lib64/pkgconfig" \
    \
    cmake \
    -DCMAKE_INSTALL_PREFIX="${install_folder}" \
    -DBUILD_TESTS:BOOL=off \
    -DFTDIPP:BOOL=off \
    -DPYTHON_BINDINGS:BOOL=off \
    -DEXAMPLES:BOOL=off \
    -DDOCUMENTATION:BOOL=off \
    -DFTDI_EEPROM:BOOL=off \
    "${work_folder}/${LIBFTDI_FOLDER}"

  fi

  echo
  echo "Running make libftdi..."

  # Build.
  make clean install

  if [ "${target_name}" == "win" ]
  then
    # Remove DLLs to force static link for final executable.
    rm -f "${install_folder}/bin/libftdi1.dll"
    rm -f "${install_folder}/bin/libftdi1-config"
    rm -f "${install_folder}/lib/libftdi1.dll.a"
    rm -f "${install_folder}/lib/pkgconfig/libftdipp1.pc"
  fi

fi


# ----- Build the new HDI library. -----

if [ "${target_name}" == "win" ]
then
  HIDAPI_TARGET="windows"
  HIDAPI_OBJECT="hid.o"
elif [ "${target_name}" == "osx" ]
then
  HIDAPI_TARGET="mac"
  HIDAPI_OBJECT="hid.o"
elif [ "${target_name}" == "debian" ]
then
  HIDAPI_TARGET="linux"
  HIDAPI_OBJECT="hid-libusb.o"
fi

if [ ! -f "${install_folder}/lib/libhid.a" ]
then

  rm -rfv "${build_folder}/${HIDAPI_FOLDER}"
  mkdir -p "${build_folder}/${HIDAPI_FOLDER}"

  cp -r "${work_folder}/${HIDAPI_FOLDER}/"* \
    "${build_folder}/${HIDAPI_FOLDER}"

  echo
  echo "Running make libhid..."

  cd "${build_folder}/${HIDAPI_FOLDER}/${HIDAPI_TARGET}"

  if [ "${target_name}" == "win" ]
  then

    CFLAGS="-m${target_bits} -pipe" \
    \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    make -f Makefile.mingw \
    CC=${cross_compile_prefix}-gcc \
    "${HIDAPI_OBJECT}"

    # Make just compiles the file. Create the archive and convert it to library.
    # No dynamic/shared libs involved.
    ar -r  libhid.a "${HIDAPI_OBJECT}"
    ${cross_compile_prefix}-ranlib libhid.a

  else

    CFLAGS="-m${target_bits} -pipe" \
    \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    make clean "${HIDAPI_OBJECT}"

    # Make just compiles the file. Create the archive.
    # No dynamic/shared libs involved.
    ar -r libhid.a ${HIDAPI_OBJECT}
    ranlib libhid.a

  fi

  mkdir -p "${install_folder}/lib"
  cp -v libhid.a \
     "${install_folder}/lib"

  mkdir -p "${install_folder}/lib/pkgconfig"
  sed -e "s|XXX|${install_folder}|" \
    "${git_folder}/gnuarmeclipse/pkgconfig/${HIDAPI}-${HIDAPI_TARGET}.pc" \
    > "${install_folder}/lib/pkgconfig/hidapi.pc"

  mkdir -p "${install_folder}/include/hidapi"
  cp -v "${work_folder}/${HIDAPI_FOLDER}/hidapi/hidapi.h" \
     "${install_folder}/include/hidapi"

fi

# Create the build folder.
mkdir -p "${build_folder}/openocd"

# ----- Configure OpenOCD. Use the same options as Freddie Chopin. -----

if [ ! -f "${build_folder}/openocd/config.h" ]
then

  echo
  echo "Running configure OpenOCD..."

  if [ "${target_name}" == "win" ]
  then

    cd "${build_folder}/openocd"

    # All variables below are passed on the command line before 'configure'.
    # Be sure all these lines end in '\' to ensure lines are concatenated.
    OUTPUT_DIR="${build_folder}" \
    \
    CPPFLAGS="-m${target_bits} -pipe" \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/cross-pkg-config" \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig" \
    PKG_CONFIG_PREFIX="${install_folder}" \
    \
    bash "${git_folder}/configure" \
    --build="$(uname -m)-linux-gnu" \
    --host="${cross_compile_prefix}" \
    --prefix="${install_folder}/openocd"  \
    --datarootdir="${install_folder}" \
    --infodir="${install_folder}/openocd/info"  \
    --localedir="${install_folder}/openocd/locale"  \
    --mandir="${install_folder}/openocd/man"  \
    --docdir="${install_folder}/openocd/doc"  \
    --enable-aice \
    --enable-amtjtagaccel \
    --enable-armjtagew \
    --enable-cmsis-dap \
    --enable-dummy \
    --enable-ftdi \
    --enable-gw16012 \
    --enable-jlink \
    --enable-jtag_vpi \
    --enable-opendous \
    --enable-openjtag_ftdi \
    --enable-osbdm \
    --enable-legacy-ft2232_libftdi \
    --enable-parport \
    --disable-parport-ppdev \
    --enable-parport-giveio \
    --enable-presto_libftdi \
    --enable-remote-bitbang \
    --enable-rlink \
    --enable-stlink \
    --enable-ti-icdi \
    --enable-ulink \
    --enable-usb-blaster-2 \
    --enable-usb_blaster_libftdi \
    --enable-usbprog \
    --enable-vsllink \
    | tee "${output_folder}/configure-output.txt"
    # Note: don't forget to update the INFO.txt file after changing these.

  elif [ "${target_name}" == "debian" ]
  then

    LD_LIBRARY_PATH=${LD_LIBRARY_PATH:-""}

    cd "${build_folder}/openocd"

    # All variables below are passed on the command line before 'configure'.
    # Be sure all these lines end in '\' to ensure lines are concatenated.
    # On some machines libftdi ends in lib64, so we refer both lib & lib64
    CPPFLAGS="-m${target_bits} -pipe" \
    LDFLAGS='-Wl,-rpath=\$$ORIGIN -lpthread' \
    \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    LD_LIBRARY_PATH="${install_folder}/lib":"${install_folder}/lib64":"${LD_LIBRARY_PATH}" \
    \
    bash "${git_folder}/configure" \
    --prefix="${install_folder}/openocd"  \
    --datarootdir="${install_folder}" \
    --infodir="${install_folder}/openocd/info"  \
    --localedir="${install_folder}/openocd/locale"  \
    --mandir="${install_folder}/openocd/man"  \
    --docdir="${install_folder}/openocd/doc"  \
    --enable-aice \
    --enable-amtjtagaccel \
    --enable-armjtagew \
    --enable-cmsis-dap \
    --enable-dummy \
    --enable-ftdi \
    --enable-gw16012 \
    --enable-jlink \
    --enable-jtag_vpi \
    --enable-opendous \
    --enable-openjtag_ftdi \
    --enable-osbdm \
    --enable-legacy-ft2232_libftdi \
    --enable-parport \
    --disable-parport-ppdev \
    --enable-parport-giveio \
    --enable-presto_libftdi \
    --enable-remote-bitbang \
    --enable-rlink \
    --enable-stlink \
    --enable-ti-icdi \
    --enable-ulink \
    --enable-usb-blaster-2 \
    --enable-usb_blaster_libftdi \
    --enable-usbprog \
    --enable-vsllink \
    | tee "${output_folder}/configure-output.txt"
    # Note: don't forget to update the INFO.txt file after changing these.

    # Note: a very important detail here is LDFLAGS='-Wl,-rpath=\$$ORIGIN which
    # adds a special record to the ELF file asking the loader to search for the 
    # libraries first in the same folder where the executable is located. The 
    # task is complicated due to the multiple substitutions that are done on 
    # the way, and need to be escaped.

  elif [ "${target_name}" == "osx" ]
  then

    DYLD_LIBRARY_PATH=${DYLD_LIBRARY_PATH:-""}

    cd "${build_folder}/openocd"

    # All variables below are passed on the command line before 'configure'.
    # Be sure all these lines end in '\' to ensure lines are concatenated.
    CPPFLAGS="-m${target_bits} -pipe" \
    \
    PKG_CONFIG_LIBDIR="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    DYLD_LIBRARY_PATH="${install_folder}/lib":"${DYLD_LIBRARY_PATH}" \
    \
    bash "${git_folder}/configure" \
    --prefix="${install_folder}/openocd"  \
    --datarootdir="${install_folder}" \
    --infodir="${install_folder}/openocd/info"  \
    --localedir="${install_folder}/openocd/locale"  \
    --mandir="${install_folder}/openocd/man"  \
    --docdir="${install_folder}/openocd/doc"  \
    --enable-aice \
    --disable-amtjtagaccel \
    --enable-armjtagew \
    --enable-cmsis-dap \
    --enable-dummy \
    --enable-ftdi \
    --disable-gw16012 \
    --enable-jlink \
    --disable-jtag_vpi \
    --enable-opendous \
    --enable-openjtag_ftdi \
    --enable-osbdm \
    --enable-legacy-ft2232_libftdi \
    --disable-parport \
    --disable-parport-ppdev \
    --disable-parport-giveio \
    --enable-presto_libftdi \
    --enable-remote-bitbang \
    --enable-rlink \
    --enable-stlink \
    --enable-ti-icdi \
    --enable-ulink \
    --enable-usb-blaster-2 \
    --enable-usb_blaster_libftdi \
    --enable-usbprog \
    --enable-vsllink \
    | tee "${output_folder}/configure-output.txt"
    # Note: don't forget to update the INFO.txt file after changing these.

  fi

fi

cd "${build_folder}/${APP_LC_NAME}"
cp config.* "${output_folder}"


# ----- Full build, with documentation. -----

if [ ! \( -f "${build_folder}/openocd/src/openocd" \) -a \
     ! \( -f "${build_folder}/openocd/src/openocd.exe" \) ]
then

  # The bindir and pkgdatadir are required to configure bin and scripts folders
  # at the same level in the hierarchy.

  echo
  echo "Running make all..."

  cd "${build_folder}/${APP_LC_NAME}"
  make bindir="bin" pkgdatadir="" all pdf html \
  | tee "${output_folder}/make-all-output.txt"

fi

# ----- Full install, including documentation. -----

echo
echo "Running make install..."

rm -rf "${install_folder}/${APP_LC_NAME}"
mkdir -p "${install_folder}/${APP_LC_NAME}"

cd "${build_folder}/${APP_LC_NAME}"

make install install-pdf install-html install-man \
| tee "${output_folder}/make-install-output.txt"

# ----- Copy dynamic libraries to the install bin folder. -----

if [ "${target_name}" == "win" ]
then

  # Probably due to a VirtualBox shared folder bug, the following fails with:
  # i686-w64-mingw32-strip:/root/Host/Work/openocd/install/win32/openocd/bin/stE4wx0V: Protocol error
  # ${cross_compile_prefix}-strip "${install_folder}/openocd/bin/openocd.exe"

  do_strip ${cross_compile_prefix}-strip \
    "${install_folder}/openocd/bin/openocd.exe"

  echo
  echo "Copying DLLs..."

  # Identify the current cross gcc version, to locate the specific dll folder.
  CROSS_GCC_VERSION=$(${cross_compile_prefix}-gcc --version | grep 'gcc' | sed -e 's/.*\s\([0-9]*\)[.]\([0-9]*\)[.]\([0-9]*\).*/\1.\2.\3/')
  CROSS_GCC_VERSION_SHORT=$(echo $CROSS_GCC_VERSION | sed -e 's/\([0-9]*\)[.]\([0-9]*\)[.]\([0-9]*\).*/\1.\2/')
  SUBLOCATION="-win32"

  echo "${CROSS_GCC_VERSION}" "${CROSS_GCC_VERSION_SHORT}" "${SUBLOCATION}"

  if [ "${target_bits}" == "32" ]
  then
    do_copy_gcc_dll "libgcc_s_sjlj-1.dll"
  elif [ "${target_bits}" == "64" ]
  then
    do_copy_gcc_dll "libgcc_s_seh-1.dll"
  fi

  do_copy_libwinpthread_dll

  # Copy possible DLLs. Currently only libusb0.dll is dynamic, all other 
  # are also compiled as static.
  cp -v "${install_folder}/bin/"*.dll "${install_folder}/openocd/bin"

elif [ "${target_name}" == "debian" ]
then

  do_strip strip "${install_folder}/openocd/bin/openocd"

  echo
  echo "Copying shared libs..."

  if [ "${target_bits}" == "64" ]
  then
    distro_machine="x86_64"
  elif [ "${target_bits}" == "32" ]
  then
    distro_machine="i386"
  fi

  ILIB=$(find "${install_folder}/lib"* -type f -name 'libusb-1.0.so.*.*' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" \
    "${install_folder}/openocd/bin"
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2.\3/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
  fi

  ILIB=$(find "${install_folder}/lib"* -type f -name 'libusb-0.1.so.*.*' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" \
    "${install_folder}/openocd/bin"
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2.\3/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
  fi

  ILIB=$(find "${install_folder}/lib"* -type f -name 'libftdi1.so.*.*' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" \
    "${install_folder}/openocd/bin"
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2.\3/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
  fi

  # Add libudev.so locally.
  ILIB=$(find /lib/${distro_machine}-linux-gnu /usr/lib/${distro_machine}-linux-gnu -type f -name 'libudev.so.*.*' -print)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" \
    "${install_folder}/openocd/bin"
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2.\3/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
    ILIB_SHORT="$(echo $ILIB_BASE | sed -e 's/\([[:alnum:]]*\)[.]\([[:alnum:]]*\)[.]\([[:digit:]]*\)[.].*/\1.\2/')"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "${ILIB_SHORT}")
  else
    echo
    echo 'WARNING: libudev.so not copied locally!'
    exit 1
  fi

  # Add librt.so.1 locally, to be sure it is available always.
  ILIB=$(find /lib/${distro_machine}-linux-gnu /usr/lib/${distro_machine}-linux-gnu -type f -name 'librt-*.so' -print | grep -v i686)
  if [ ! -z "${ILIB}" ]
  then
    echo "Found ${ILIB}"
    ILIB_BASE="$(basename ${ILIB})"
    /usr/bin/install -v -c -m 644 "${ILIB}" \
    "${install_folder}/openocd/bin"
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "librt.so.1")
    (cd "${install_folder}/openocd/bin"; ln -sv "${ILIB_BASE}" "librt.so")
  else
    echo
    echo "WARNING: librt.so not copied locally!"
    exit 1
  fi

elif [ "${target_name}" == "osx" ]
then

  do_strip strip "${install_folder}/openocd/bin/openocd"

  echo
  echo "Copying dynamic libs..."

  # Post-process dynamic libraries paths to be relative to executable folder.

  # otool -L "${install_folder}/openocd/bin/openocd"
  install_name_tool -change "libftdi1.2.dylib" "@executable_path/libftdi1.2.dylib" \
    "${install_folder}/openocd/bin/openocd"
  install_name_tool -change "${install_folder}/lib/libusb-1.0.0.dylib" \
    "@executable_path/libusb-1.0.0.dylib" "${install_folder}/openocd/bin/openocd"
  install_name_tool -change "${install_folder}/lib/libusb-0.1.4.dylib" \
    "@executable_path/libusb-0.1.4.dylib" "${install_folder}/openocd/bin/openocd"
  otool -L "${install_folder}/openocd/bin/openocd"

  DLIB="libftdi1.2.dylib"
  cp "${install_folder}/lib/libftdi1.2.2.0.dylib" \
    "${install_folder}/openocd/bin/${DLIB}"
  # otool -L "${install_folder}/openocd/bin/${DLIB}"
  install_name_tool -id "${DLIB}" "${install_folder}/openocd/bin/${DLIB}"
  install_name_tool -change "${install_folder}/lib/libusb-1.0.0.dylib" \
    "@executable_path/libusb-1.0.0.dylib" "${install_folder}/openocd/bin/${DLIB}"
  otool -L "${install_folder}/openocd/bin/${DLIB}"

  DLIB="libusb-0.1.4.dylib"
  cp "${install_folder}/lib/libusb-0.1.4.dylib" \
    "${install_folder}/openocd/bin/${DLIB}"
  # otool -L "${install_folder}/openocd/bin/${DLIB}"
  install_name_tool -id "${DLIB}" "${install_folder}/openocd/bin/${DLIB}"
  install_name_tool -change "${install_folder}/lib/libusb-1.0.0.dylib" \
    "@executable_path/libusb-1.0.0.dylib" "${install_folder}/openocd/bin/${DLIB}"
  otool -L "${install_folder}/openocd/bin/${DLIB}"

  DLIB="libusb-1.0.0.dylib"
  cp "${install_folder}/lib/libusb-1.0.0.dylib" \
    "${install_folder}/openocd/bin/${DLIB}"
  # otool -L "${install_folder}/openocd/bin/${DLIB}"
  install_name_tool -id "${DLIB}" "${install_folder}/openocd/bin/${DLIB}"
  otool -L "${install_folder}/openocd/bin/${DLIB}"

fi

# ----- Copy the license files. -----

echo
echo "Copying license files..."

do_copy_license "${git_folder}" "openocd"
do_copy_license "${work_folder}/${HIDAPI_FOLDER}" "${HIDAPI_FOLDER}"
do_copy_license "${work_folder}/${LIBFTDI_FOLDER}" "${LIBFTDI_FOLDER}"
do_copy_license "${work_folder}/${LIBUSB1_FOLDER}" "${LIBUSB1_FOLDER}"

if [ "${target_name}" == "win" ]
then
  do_copy_license "${work_folder}/${LIBUSB_W32_FOLDER}" "${LIBUSB_W32}"
else
  do_copy_license "${work_folder}/${LIBUSB0_FOLDER}" "${LIBUSB0_FOLDER}"
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
  distribution_file_version=$(cat "${git_folder}/gnuarmeclipse/VERSION")-${DISTRIBUTION_FILE_DATE}
elif [ "${GIT_HEAD}" == "gnuarmeclipse-dev" ]
then
  distribution_file_version=$(cat "${git_folder}/gnuarmeclipse/VERSION-dev")-${DISTRIBUTION_FILE_DATE}-dev
fi

distribution_executable_name="openocd"

source "$helper_script" --create-distribution

# Requires ${distribution_file} and ${result}
source "$helper_script" --completed

exit 0

EOF
# The above marker must start in the first column.
# ^===========================================================================^


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
    --docker-image ilegeul/debian:7-gnuarm-gcc
fi

# ----- Build the Debian 32-bits distribution. -----

if [ "${DO_BUILD_DEB32}" == "y" ]
then
  do_build_target "Creating Debian 32-bits archive..." \
    --target-name debian \
    --target-bits 32 \
    --docker-image ilegeul/debian32:7-gnuarm-gcc
fi

# ----- Build the OS X distribution. -----

if [ "${HOST_UNAME}" == "Darwin" ]
then
  if [ "${DO_BUILD_OSX}" == "y" ]
  then
    do_build_target "Creating OS X package..." \
      --target-name osx
  fi
fi

source "$helper_script" "--stop-timer"

# ----- Done. -----
exit 0
