#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

# Script to cross build the 32/64-bit Windows version of Build Tools 
# with MinGW-w64 on GNU/Linux.
# Developed on OS X using a Debian 8 x64 Docker container.
# The build is structured in 2 steps, one running on the host machine
# and one running inside the Docker container.

# Note: the 64-bit is not yet functional, BusyBox sh.exe fails.

# At first run, Docker will download/build a relatively large
# image (2.5GB) from Docker Hub.

# Prerequisites:
#
#   Docker
#   curl, git, automake, patch, tar, unzip
#
# sudo apt-get install git libtool autoconf automake autotools-dev pkg-config
# sudo apt-get install texinfo texlive dos2unix nsis
# sudo apt-get install mingw-w64 mingw-w64-tools mingw-w64-i686-dev 
# sudo apt-get install autopoint gettext

# Mandatory definition.
APP_NAME="Windows Build Tools"
APP_LC_NAME="build-tools"

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
    --all)
      DO_BUILD_WIN32="y"
      DO_BUILD_WIN64="y"
      shift
      ;;

    --helper-script)
      helper_script=$2
      shift 2
      ;;

    --help)
      echo "Build the GNU ARM Eclipse ${APP_NAME} distributions."
      echo "Usage:"
      echo "    bash $0 [--helper-script file.sh] [--win32] [--win64] [--all] [clean|pull|checkput-dev|checkout-stable|build-images] [--help]"
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

# ----- Process actions. -----

if [ "${ACTION}" == "clean" ]
then
  # Remove most build and temporary folders.
  echo
  echo "Remove most of the build folders..."

  rm -rf "${BUILD_FOLDER}"
  rm -rf "${BUILD_FOLDER}/msys2"
  rm -rf "${WORK_FOLDER}/install"

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

# ----- Get the GNU ARM Eclipse Build Tools git repository. -----

# The custom Build Tools is available from the dedicated Git repository
# which is part of the GNU ARM Eclipse project hosted on SourceForge.

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

fi

# ----- Get the current Git branch name. -----

# source "$helper_script" "--get-git-head"


# ----- Get current date. -----

source "$helper_script" "--get-current-date"

# ----- Get make. -----

# The make executable is built using the source package from  
# the open source MSYS2 project.
# https://sourceforge.net/projects/msys2/

MSYS2_MAKE_PACK_URL_BASE="http://sourceforge.net/projects/msys2/files"

# http://sourceforge.net/projects/msys2/files/REPOS/MSYS2/Sources/
# http://sourceforge.net/projects/msys2/files/REPOS/MSYS2/Sources/make-4.1-3.src.tar.gz/download


MAKE_VERSION="4.1"
MSYS2_MAKE_VERSION_RELEASE="${MAKE_VERSION}-4"

MSYS2_MAKE_PACK_ARCH="make-${MSYS2_MAKE_VERSION_RELEASE}.src.tar.gz"
MSYS2_MAKE_PACK_URL="${MSYS2_MAKE_PACK_URL_BASE}/REPOS/MSYS2/Sources/${MSYS2_MAKE_PACK_ARCH}"

if [ ! -f "${DOWNLOAD_FOLDER}/${MSYS2_MAKE_PACK_ARCH}" ]
then
  mkdir -p "${DOWNLOAD_FOLDER}"

  cd "${DOWNLOAD_FOLDER}"
  curl -L "${MSYS2_MAKE_PACK_URL}" \
    --output "${MSYS2_MAKE_PACK_ARCH}"
fi

MAKE_ARCH="make-${MAKE_VERSION}.tar.bz2"
if [ ! -f "${WORK_FOLDER}/msys2/make/${MAKE_ARCH}" ]
then
  mkdir -p "${WORK_FOLDER}/msys2"

  echo
  echo "Unpacking ${MSYS2_MAKE_PACK_ARCH}..."
  cd "${WORK_FOLDER}/msys2"
  tar -xvf "${DOWNLOAD_FOLDER}/${MSYS2_MAKE_PACK_ARCH}"
fi


# ----- Get BusyBox. -----

# http://intgat.tigress.co.uk/rmy/busybox/index.html
# https://github.com/rmyorston/busybox-w32

BUSYBOX_COMMIT="ec39cb770ddd5c0e085d5c4ee10be65bab5e7a44"
# BUSYBOX_COMMIT=master
BUSYBOX_ARCHIVE="${BUSYBOX_COMMIT}.zip"
BUSYBOX_URL="https://github.com/rmyorston/busybox-w32/archive/${BUSYBOX_ARCHIVE}"

BUSYBOX_SRC_FOLDER="${WORK_FOLDER}/busybox-w32-${BUSYBOX_COMMIT}"

if [ ! -f "${DOWNLOAD_FOLDER}/${BUSYBOX_ARCHIVE}" ]
then
  cd "${DOWNLOAD_FOLDER}"
  curl -L "${BUSYBOX_URL}" --output "${BUSYBOX_ARCHIVE}"
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
DISTRIBUTION_FILE_DATE="${DISTRIBUTION_FILE_DATE}"

MAKE_VERSION="${MAKE_VERSION}"
MAKE_ARCH="${MAKE_ARCH}"

BUSYBOX_COMMIT="${BUSYBOX_COMMIT}"
BUSYBOX_ARCHIVE="${BUSYBOX_ARCHIVE}"

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

echo "Checking ${cross_compile_prefix}-gcc..."
${cross_compile_prefix}-gcc --version 2>/dev/null | egrep -e 'gcc|clang'

echo "Checking unix2dos..."
unix2dos --version 2>&1 | grep unix2dos

echo "Checking makensis..."
echo "makensis $(makensis -VERSION)"


# ----- Remove and recreate the output folder. -----

rm -rf "${output_folder}"
mkdir -p "${output_folder}"

# ----- Create the build folder. -----

mkdir -p "${build_folder}/${APP_LC_NAME}"


# ----- Build make. -----

make_build_folder="${build_folder}/make-${MAKE_VERSION}"
if [ ! -d "${make_build_folder}" ]
then
  mkdir -p "${build_folder}"

  cd "${build_folder}"
  echo
  echo "Unpacking ${MAKE_ARCH}..."
  tar -xvf "${work_folder}/msys2/make/${MAKE_ARCH}"

  cd "${make_build_folder}"
  patch -p1 -i "${work_folder}/msys2/make/make-autoconf.patch"
fi

if [ ! -f "${make_build_folder}/config.h" ]
then

  echo
  echo "Running make autoreconf..."
  autoreconf -fi

  echo
  echo "Running make configure..."

  cd "${make_build_folder}"

  bash "configure" \
  --host=${cross_compile_prefix} \
  --prefix="${install_folder}/make-${MAKE_VERSION}"  \
  --without-libintl-prefix \
  --without-libiconv-prefix \
  ac_cv_dos_paths=yes \
  | tee "${output_folder}/configure-output.txt"

fi

cd "${make_build_folder}"
cp config.* "${output_folder}"

cd "${make_build_folder}"
make all \
| tee "${output_folder}/make-all-output.txt"

# Always clear the destination folder, to have a consistent package.
echo
echo "Removing install..."
rm -rf "${install_folder}"

make install \
| tee "${output_folder}/make-install-output.txt"

do_strip ${cross_compile_prefix}-strip "${install_folder}/make-${MAKE_VERSION}/bin/make.exe"

# ----- Copy files to the install bin folder -----

echo 
mkdir -p "${install_folder}/build-tools/bin"
cp -v "${install_folder}/make-${MAKE_VERSION}/bin/make.exe" \
 "${install_folder}/build-tools/bin"


# ----- Build BusyBox. -----

busybox_build_folder="${build_folder}/busybox-w32-${BUSYBOX_COMMIT}"

if [ ! -f "${busybox_build_folder}/.config" ]
then

  if [ ! -d "${busybox_build_folder}" ]
  then

    cd "${build_folder}"
    unzip "${download_folder}/${BUSYBOX_ARCHIVE}"

    cd "${busybox_build_folder}/configs"
    sed \
    -e 's/CONFIG_CROSS_COMPILER_PREFIX=".*"/CONFIG_CROSS_COMPILER_PREFIX="i686-w64-mingw32-"/' \
    <mingw32_defconfig >gnuarmeclipse_32_mingw_defconfig

    sed \
    -e 's/CONFIG_CROSS_COMPILER_PREFIX=".*"/CONFIG_CROSS_COMPILER_PREFIX="x86_64-w64-mingw32-"/' \
    <mingw32_defconfig >gnuarmeclipse_64_mingw_defconfig

  fi

  echo 
  echo "Running BusyBox make gnuarmeclipse_${target_bits}_mingw_defconfig..."

  cd "${busybox_build_folder}"
  make "gnuarmeclipse_${target_bits}_mingw_defconfig"

fi

if [ ! -f "${busybox_build_folder}/busybox.exe" ]
then

  echo 
  echo "Running BusyBox make..."

  cd "${busybox_build_folder}"
  make

fi

# ----- Copy BusyBox with 3 different names. -----

echo
echo "Installing BusyBox..."

mkdir -p "${install_folder}/build-tools/bin"
cp -v "${busybox_build_folder}/busybox.exe" "${install_folder}/build-tools/bin/busybox.exe"
do_strip ${cross_compile_prefix}-strip "${install_folder}/build-tools/bin/busybox.exe"

cp -v "${install_folder}/build-tools/bin/busybox.exe" "${install_folder}/build-tools/bin/sh.exe"
cp -v "${install_folder}/build-tools/bin/busybox.exe" "${install_folder}/build-tools/bin/rm.exe"
cp -v "${install_folder}/build-tools/bin/busybox.exe" "${install_folder}/build-tools/bin/echo.exe"

# ----- Copy the license files. -----

echo
echo "Copying license files..."

do_copy_license "${make_build_folder}" "make-${MAKE_VERSION}"
do_copy_license "${busybox_build_folder}" "busybox"

# For Windows, process cr lf
find "${install_folder}/${APP_LC_NAME}/license" -type f \
  -exec unix2dos {} \;


# ----- Copy the GNU ARM Eclipse info files. -----

echo 
echo "Copying info files..."

mkdir -p "${install_folder}/build-tools/gnuarmeclipse"

cp -v "${git_folder}/gnuarmeclipse/info/INFO.txt" \
  "${install_folder}/build-tools/INFO.txt"
unix2dos "${install_folder}/build-tools/INFO.txt"
cp -v "${git_folder}/gnuarmeclipse/info/BUILD.txt" \
  "${install_folder}/build-tools/gnuarmeclipse/BUILD.txt"
unix2dos "${install_folder}/build-tools/gnuarmeclipse/BUILD.txt"
cp -v "${git_folder}/gnuarmeclipse/info/CHANGES.txt" \
  "${install_folder}/build-tools/gnuarmeclipse/"
unix2dos "${install_folder}/build-tools/gnuarmeclipse/CHANGES.txt"

# Copy the current build script
cp -v "${work_folder}/scripts/build-${APP_LC_NAME}.sh" \
  "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/build-${APP_LC_NAME}.sh"
do_unix2dos "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/build-${APP_LC_NAME}.sh"

# Copy the current build helper script
cp -v "${work_folder}/scripts/build-helper.sh" \
  "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/build-helper.sh"
do_unix2dos "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/build-helper.sh"

cp -v "${output_folder}/config.log" \
  "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/config.log"
do_unix2dos "${install_folder}/${APP_LC_NAME}/gnuarmeclipse/config.log"

# Not passed as is, used by makensis for the MUI_PAGE_LICENSE; must be DOS.
cp -v "${git_folder}/COPYING" \
  "${install_folder}/build-tools/COPYING"
unix2dos "${install_folder}/build-tools/COPYING"


# ----- Create the distribution setup. -----

mkdir -p "${output_folder}"

echo
echo "Creating setup..."
echo

distribution_file_version=$(cat "${git_folder}/gnuarmeclipse/VERSION")-${DISTRIBUTION_FILE_DATE}
distribution_file="${distribution_folder}/gnuarmeclipse-${APP_LC_NAME}-${target_folder}-${distribution_file_version}-setup.exe"

# Not passed as it, used by makensis for the MUI_PAGE_LICENSE; must be DOS.
cp "${git_folder}/COPYING" \
  "${install_folder}/${APP_LC_NAME}/COPYING"
unix2dos "${install_folder}/${APP_LC_NAME}/COPYING"

nsis_folder="${git_folder}/gnuarmeclipse/nsis"
nsis_file="${nsis_folder}/gnuarmeclipse-${APP_LC_NAME}.nsi"

cd "${build_folder}"
makensis -V4 -NOCD \
  -DINSTALL_FOLDER="${install_folder}/${APP_LC_NAME}" \
  -DNSIS_FOLDER="${nsis_folder}" \
  -DOUTFILE="${distribution_file}" \
  -DW${target_bits} \
  -DBITS=${target_bits} \
  -DVERSION=${distribution_file_version} \
  "${nsis_file}"
result="$?"

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


source "$helper_script" "--stop-timer"

# ----- Done. -----
exit 0
