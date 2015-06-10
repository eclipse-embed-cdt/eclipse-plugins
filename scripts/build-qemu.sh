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

# ----- Process actions. -----

if [ "${ACTION}" == "clean" ]
then
  # Remove most build and temporary folders.
  echo
  echo "Remove most of the build folders..."

  rm -rf "${BUILD_FOLDER}"
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

  # Add DTC module.
  cd "${GIT_FOLDER}"
  git submodule update --init dtc
  git submodule update --init pixman

  # Change to the gnuarmeclipse branch. On subsequent runs use "git pull".
  cd "${GIT_FOLDER}"
  git checkout gnuarmeclipse-dev
  git submodule update

fi

# ----- Get the current Git branch name. -----

source "$helper_script" "--get-git-head"


# ----- Get current date. -----

source "$helper_script" "--get-current-date"

# ----- Here insert the code to perform the downloads, if ever needed. -----

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
  md5sum --version
fi

# ----- Remove and recreate the output folder. -----

rm -rf "${output_folder}"
mkdir -p "${output_folder}"

# ----- Here insert the code to build the libraries, if ever needed. -----

# ----- Create the build folder. -----

mkdir -p "${build_folder}/${APP_LC_NAME}"

# ----- Configure QEMU. -----

if [ ! -f "${build_folder}/qemu/config-host.mak" ]
then

  echo
  echo "Running configure QEMU..."

  # All variables are passed on the command line before 'configure'.
  # Be sure all these lines end in '\' to ensure lines are concatenated.

  if [ "${target_name}" == "win" ]
  then

    # Windows target, 32/64-bit
    cd "${build_folder}/qemu"

    LDFLAGS="-L${install_folder}/lib" \
    \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/${cross_compile_prefix}-pkg-config" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --cross-prefix="${cross_compile_prefix}-" \
      \
      --extra-cflags="-pipe -I${install_folder}/include -Wno-missing-format-attribute -D_POSIX=1 -mthreads" \
      --extra-ldflags="-v" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/qemu" \
      --bindir="${install_folder}/qemu/bin" \
      --docdir="${install_folder}/qemu/doc" \
      --mandir="${install_folder}/qemu/man" \
      --enable-trace-backend=stderr \
      | tee "${output_folder}/configure-output.txt"

  elif [ "${target_name}" == "debian" ]
  then

    # Linux target
    cd "${build_folder}/qemu"

    LDFLAGS="-v -Wl,-rpath=\$\$ORIGIN" \
    \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --static \
      \
      --extra-cflags="-pipe -I${install_folder}/include -Wno-missing-format-attribute" \
      --extra-ldflags="-L${install_folder}/lib" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/qemu" \
      --bindir="${install_folder}/qemu/bin" \
      --docdir="${install_folder}/qemu/doc" \
      --mandir="${install_folder}/qemu/man" \
      --enable-trace-backend=stderr \
    | tee "${output_folder}/configure-output.txt"

    # Note: a very important detail here is LDFLAGS='-Wl,-rpath=\$$ORIGIN which
    # adds a special record to the ELF file asking the loader to search for the
    # libraries first in the same folder where the executable is located. The
    # task is complicated due to the multiple substitutions that are done on
    # the way, and need to be escaped.

  elif [ "${target_name}" == "osx" ]
  then

    # OS X target
    cd "${build_folder}/qemu"

    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "${git_folder}/configure" \
      --extra-cflags="-pipe -I${install_folder}/include -Wno-missing-format-attribute" \
      --extra-ldflags="-v -L${install_folder}/lib" \
      --target-list="gnuarmeclipse-softmmu" \
      --prefix="${install_folder}/qemu" \
      --bindir="${install_folder}/qemu/bin" \
      --docdir="${install_folder}/qemu/doc" \
      --mandir="${install_folder}/qemu/man" \
      --enable-trace-backend=stderr \
    | tee "${output_folder}/configure-output.txt"

    # Configure fails for --static

  fi

fi

cd "${build_folder}/${APP_LC_NAME}"
cp config.* "${output_folder}"

# ----- Full build, with documentation. -----

if [ ! \( -f "${build_folder}/qemu/gnuarmeclipse-softmmu/qemu-system-gnuarmeclipse" \) -a \
     ! \( -f "${build_folder}/qemu/gnuarmeclipse-softmmu/qemu-system-gnuarmeclipse.exe" \) ]
then

  echo
  echo "Running make all..."

  cd "${build_folder}/qemu"
  make all pdf \
  | tee "${output_folder}/make-all-output.txt"

fi

# ----- Full install, including documentation. -----

echo
echo "Running make install..."

# Always clear the destination folder, to have a consistent package.
rm -rf "${install_folder}/${APP_LC_NAME}"

# Exhaustive install, including documentation.

cd "${build_folder}/${APP_LC_NAME}"
make install install-pdf

# Remove useless files

# rm -rf "${install_folder}/qemu/etc"

# ----- Copy dynamic libraries to the install bin folder. -----

if [ "${target_name}" == "win" ]
then

  do_strip ${cross_compile_prefix}-strip \
    "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse.exe"

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

  if [ "${target_bits}" == "32" ]
  then
    for f in libglib-2.0-0.dll zlib1.dll libgthread-2.0-0.dll intl.dll
    do
      cp -v "/usr/${cross_compile_prefix}/bin/${f}" "${install_folder}/qemu/bin"
    done
  elif [ "${target_bits}" == "64" ]
  then
    for f in libintl-8.dll libiconv-2.dll libglib-2.0-0.dll libpixman-1-0.dll zlib1.dll
    do
      cp -v "/usr/${cross_compile_prefix}/bin/${f}" "${install_folder}/qemu/bin"
    done
  fi

  do_strip ${cross_compile_prefix}-strip "${install_folder}/qemu/bin/"*.dll

  # Remove some unexpected files.
  rm -f "${install_folder}/qemu/bin/target-x86_64.conf"
  rm -f "${install_folder}/qemu/bin/trace-events"

elif [ "${target_name}" == "debian" ]
then

  do_strip strip "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"

  # No need to copy any shared libraries, the build is static.

elif [ "${target_name}" == "osx" ]
then

  do_strip strip "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"

  echo
  echo "Copying dynamic libs..."

  # Copy the dynamic libraries to the same folder where the application file is.
  # Post-process dynamic libraries paths to be relative to executable folder.

  echo
  # otool -L "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libgnutls.28.dylib" "@executable_path/libgnutls.28.dylib" \
    "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"
  install_name_tool -change "/opt/local/lib/libusb-1.0.0.dylib" "@executable_path/libusb-1.0.0.dylib" \
    "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"
  otool -L "${install_folder}/qemu/bin/qemu-system-gnuarmeclipse"

  echo
  ILIB=libgnutls.28.dylib
  cp -v "/opt/local/lib/${ILIB}" \
    "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id libgnutls.28.dylib "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libz.1.dylib" "@executable_path/libz.1.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libiconv.2.dylib" "@executable_path/libiconv.2.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libp11-kit.0.dylib" "@executable_path/libp11-kit.0.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libtasn1.6.dylib" "@executable_path/libtasn1.6.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libnettle.4.dylib" "@executable_path/libnettle.4.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libhogweed.2.dylib" "@executable_path/libhogweed.2.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libgmp.10.dylib" "@executable_path/libgmp.10.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libintl.8.dylib" "@executable_path/libintl.8.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  # Different input name
  ILIB=libz.1.dylib
  cp -v "/opt/local/lib/libz.1.2.8.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id ${ILIB} "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libiconv.2.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libp11-kit.0.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libffi.6.dylib" "@executable_path/libffi.6.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libintl.8.dylib" "@executable_path/libintl.8.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libtasn1.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libnettle.4.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libhogweed.2.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libnettle.4.dylib" "@executable_path/libnettle.4.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libgmp.10.dylib" "@executable_path/libgmp.10.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libgmp.10.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libintl.8.dylib
  cp -v "/opt/local/lib/${ILIB}" \
    "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id ${ILIB} "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -change "/opt/local/lib/libiconv.2.dylib" "@executable_path/libiconv.2.dylib" \
    "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  echo
  ILIB=libffi.6.dylib
  cp -v "/opt/local/lib/${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  # otool -L "${install_folder}/qemu/bin/${ILIB}"
  install_name_tool -id "${ILIB}" "${install_folder}/qemu/bin/${ILIB}"
  otool -L "${install_folder}/qemu/bin/${ILIB}"

  # Do not strip resulting dylib files!

fi

# ----- Copy the license files. -----

echo
echo "Copying license files..."

do_copy_license "${git_folder}" "qemu-$(cat ${git_folder}/VERSION)"

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

cat "${WORK_FOLDER}/output/"*.md5

source "$helper_script" "--stop-timer"

# ----- Done. -----
exit 0
