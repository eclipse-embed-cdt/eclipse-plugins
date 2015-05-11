#!/bin/bash
set -euo pipefail
#IFS=$'\n\t'

# Multi-platform helper for OpenOCD & QEMU builds, using Docker.

while [ $# -gt 0 ]
do
  case "$1" in

    --start-timer) # ----------------------------------------------------------

      BEGIN_SEC=$(date +%s)
      echo "Script \"$0\" started at $(date)."
      ;;

    --stop-timer) # -----------------------------------------------------------

      END_SEC=$(date +%s)
      echo
      echo "Script \"$0\" completed at $(date)."
      DELTA_SEC=$((END_SEC-BEGIN_SEC))
      if [ ${DELTA_SEC} -lt 100 ]
      then
        echo "Duration: ${DELTA_SEC} seconds."
      else
        DELTA_MIN=$(((DELTA_SEC+30)/60))
        echo "Duration: ${DELTA_MIN} minutes."
      fi
      ;;

    --detect-host) # ----------------------------------------------------------

      HOST_DISTRO_NAME=""
      HOST_UNAME="$(uname)"
      if [ "${HOST_UNAME}" == "Darwin" ]
      then
        HOST_BITS="64"
        HOST_MACHINE="x86_64"

        HOST_DISTRO_NAME=Darwin
        HOST_DISTRO_LC_NAME=darwin

      elif [ "${HOST_UNAME}" == "Linux" ]
      then
        # ----- Determine distribution name and word size -----

        set +e
        HOST_DISTRO_NAME=$(lsb_release -si)
        set -e

        if [ -z "${HOST_DISTRO_NAME}" ]
        then
          echo "Please install the lsb core package and rerun."
          HOST_DISTRO_NAME="Linux"
        fi

        if [ "$(uname -m)" == "x86_64" ]
        then
          HOST_BITS="64"
          HOST_MACHINE="x86_64"
        elif [ "$(uname -m)" == "i686" ]
        then
          HOST_BITS="32"
          HOST_MACHINE="i386"
        else
          echo "Unknown uname -m $(uname -m)"
          exit 1
        fi

        HOST_DISTRO_LC_NAME=$(echo ${HOST_DISTRO_NAME} | tr "[:upper:]" "[:lower:]")

      else
        echo "Unknown uname ${HOST_UNAME}"
        exit 1
      fi

      echo
      echo "Running on ${HOST_DISTRO_NAME} ${HOST_BITS}-bits."


      # When running on Docker, the host Work folder is used, if available.
      HOST_WORK_FOLDER="${WORK_FOLDER}/../../Host/Work/${APP_LC_NAME}"

      DOCKER_HOST_WORK="/root/Host/Work/${APP_LC_NAME}"
      DOCKER_GIT_FOLDER="${DOCKER_HOST_WORK}/${APP_LC_NAME}.git"
      DOCKER_BUILD="/root/build"
      ;;

    --prepare-prerequisites) # ------------------------------------------------

      if [ "${HOST_UNAME}" == "Darwin" ]
      then
        # Prepare MacPorts environment.

        export PATH=/opt/local/bin:/opt/local/sbin:$PATH
        echo
        echo "Adding MacPorts to PATH..."
        echo "Checking MacPorts..."
        set +e
        port version
        if [ $? != 0 ]
        then
          echo "Please install MacPorts and rerun."
          exit 1
        fi
        set -e
      fi

      echo
      echo "Checking Docker..."
      set +e
      docker --version
      if [ $? != 0 ]
      then
        echo "Please install Docker (https://docs.docker.com/installation/) and rerun."
        exit 1
      fi
      set -e

      if [ "${HOST_UNAME}" == "Darwin" ]
      then
        echo "Checking boot2docker..."
        if [ $(boot2docker status) == "running" ]
        then
          echo "boot2docker running."
        else
          echo "Starting boot2docker..."
          boot2docker start
        fi
        echo "Preparing Docker environment..."
        eval "$(boot2docker shellinit)"
      fi

      echo
      echo "Checking host curl..."
      curl --version | grep curl

      echo "Checking host git..."
      git --version
      ;;

    --completed) # ----- Runs inside Docker contained -------------------------
      echo
      if [ "${result}" == "0" ]
      then
        echo "Build completed."
        echo "Distribution file ${distribution_file} created."
      else
        echo "Build failed."
      fi

      echo
      echo "Script \"$(basename $0)\" completed."
      ;;

    *) # ----------------------------------------------------------------------
      echo "Unknown option $1, exit."
      exit 1
      ;;
  esac

  shift
done


# ----- Functions used in the host build script. -----

# v===========================================================================v
do_build_target() {

  message="$1"
  shift

  echo
  echo "================================================================================"
  echo "${message}"

  target_bits=""
  docker_image=""

  while [ $# -gt 0 ]
  do
    case "$1" in
      --target-name)
        target_name="$2"
        shift 2
        ;;
      --target-bits)
        target_bits="$2"
        shift 2
        ;;
      --docker-image)
        docker_image="$2"
        shift 2
        ;;
      *)
        echo "Unknown option $1, exit."
        exit 1
    esac
  done

  # Must be located before adjusting target_bits for osx.
  target_folder=${target_name}${target_bits}

  cross_compile_prefix=""
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

  if [ -n "${docker_image}" ]
  then

    run_docker_script \
      --script "${DOCKER_HOST_WORK}/scripts/${script_name}" \
      --docker-image "${docker_image}" \
      --docker-container-name "${APP_LC_NAME}-${target_folder}-build" \
      -- \
      --build-folder "${DOCKER_HOST_WORK}/build/${target_folder}" \
      --target-name "${target_name}" \
      --target-bits "${target_bits}" \
      --output-folder "${DOCKER_HOST_WORK}/output/${target_folder}" \
      --distribution-folder "${DOCKER_HOST_WORK}/output" \
      --install-folder "${DOCKER_HOST_WORK}/install/${target_folder}" \
      --helper-script "${DOCKER_HOST_WORK}/scripts/build-helper.sh" \
      --work-folder "${DOCKER_HOST_WORK}"

  else

    run_local_script \
      --script "${script_file}" \
      -- \
      --build-folder "${WORK_FOLDER}/build/${target_folder}" \
      --target-name "${target_name}" \
      --output-folder "${WORK_FOLDER}/output/${target_folder}" \
      --distribution-folder "${WORK_FOLDER}/output" \
      --install-folder "${WORK_FOLDER}/install/${target_folder}" \
      --helper-script "${WORK_FOLDER}/scripts/build-helper.sh" \
      --work-folder "${WORK_FOLDER}"

  fi
}

# v===========================================================================v
run_docker_script() {

  while [ $# -gt 0 ]
  do
    case "$1" in
      --script)
        docker_script="$2"
        shift 2
        ;;
      --docker-image)
        docker_image="$2"
        shift 2
        ;;
      --docker-container-name)
        docker_container_name="$2"
        shift 2
        ;;
      --)
        shift
        break;
        ;;
    esac
  done

  set +e
  # Remove a possible previously crashed container.
  docker rm --force "${docker_container_name}" > /dev/null 2> /dev/null
  set -e

  echo
  echo "Running \"$(basename "${docker_script}")\" script inside \"${docker_container_name}\" container..."

  # Run the second pass script in a fresh Docker container.
  docker run \
    --name="${docker_container_name}" \
    --tty \
    --hostname "docker" \
    --workdir="/root" \
    --volume="${WORK_PARENT_FOLDER}:/root/Host/Work" \
    ${docker_image} \
    /bin/bash "${docker_script}" \
      --docker-container-name "${docker_container_name}" \
      $@

  # Remove the container.
  docker rm --force "${docker_container_name}"
}

# v===========================================================================v
run_local_script() {

  while [ $# -gt 0 ]
  do
    case "$1" in
      --script)
        local_script="$2"
        shift 2
        ;;
      --)
        shift
        break;
        ;;
    esac
  done

  echo
  echo "Running \"$(basename "${local_script}")\" script locally..."

  # Run the second pass script in a local sub-shell.
  /bin/bash "${local_script}" \
    $@
}
# ^===========================================================================^

# ----- Functions used in the Docker script. -----

# v===========================================================================v
do_copy_gcc_dll() {

  # First try Ubuntu specific locations,
  # then do a long full search.

  if [ -f "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION}/$1" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION}/$1" \
      "${install_folder}/${APP_LC_NAME}/bin"
  elif [ -f "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}/$1" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}/$1" \
      "${install_folder}/${APP_LC_NAME}/bin"
  elif [ -f "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}${SUBLOCATION}/$1" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}${SUBLOCATION}/$1" \
      "${install_folder}/${APP_LC_NAME}/bin"
  else
    echo "Searching /usr for $1..."
    SJLJ_PATH=$(find /usr \! -readable -prune -o -name $1 -print | grep ${cross_compile_prefix})
    cp -v ${SJLJ_PATH} "${install_folder}/${APP_LC_NAME}/bin"
  fi
}

# v===========================================================================v
do_copy_gcc_dlls() {

  if [ -d "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION}/" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION}/"*.dll \
      "${install_folder}/${APP_LC_NAME}/bin"
  elif [ -d "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}/" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}/"*.dll \
      "${install_folder}/${APP_LC_NAME}/bin"
  elif [ -d "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}${SUBLOCATION}/" ]
  then
    cp -v "/usr/lib/gcc/${cross_compile_prefix}/${CROSS_GCC_VERSION_SHORT}${SUBLOCATION}/"*.dll \
      "${install_folder}/${APP_LC_NAME}/bin"
  else
    echo "No DLLs"
    exit 1
  fi
}

# v===========================================================================v
do_copy_libwinpthread_dll() {

  if [ -f "/usr/${cross_compile_prefix}/lib/libwinpthread-1.dll" ]
  then
    cp "/usr/${cross_compile_prefix}/lib/libwinpthread-1.dll" \
      "${install_folder}/${APP_LC_NAME}/bin"
  else
    echo "Searching /usr for libwinpthread-1.dll..."
    PTHREAD_PATH=$(find /usr \! -readable -prune -o -name 'libwinpthread-1.dll' -print | grep ${cross_compile_prefix})
    cp -v "${PTHREAD_PATH}" "${install_folder}/${APP_LC_NAME}/bin"
  fi
}

# v===========================================================================v
do_copy_license() {

  # $1 - absolute path to input folder
  # $2 - name of output folder below INSTALL_FOLDER

  # Iterate all files in a folder and install some of them in the
  # destination folder
  echo "$2"
  for f in "$1/"*
  do
    if [ -f "$f" ]
    then
      if [[ "$f" =~ AUTHORS.*|NEWS.*|COPYING.*|README.*|LICENSE.*|FAQ.*|DEPENDENCIES.*|THANKS.* ]]
      then
        /usr/bin/install -d -m 0755 "${install_folder}/${APP_LC_NAME}/license/$2"
        /usr/bin/install -v -c -m 644 "$f" "${install_folder}/${APP_LC_NAME}/license/$2"
      fi
    fi
  done
}

# v===========================================================================v
do_unix2dos() {

  if [ "${target_name}" == "win" ]
  then
    while (($#))
    do
      unix2dos "$1"
      shift
    done
  fi
}
# ^===========================================================================^


# Continue in calling script.

