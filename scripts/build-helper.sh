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

      APP_LC_NAME=$(echo "${APP_NAME}" | tr '[:upper:]' '[:lower:]')
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

      WORK_PARENT_FOLDER="${HOME}/Work"
      # Create Work folder.
      WORK_FOLDER="${WORK_PARENT_FOLDER}/${APP_LC_NAME}"
      mkdir -p "${WORK_FOLDER}"

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

    *) # ----------------------------------------------------------------------
      echo "Unknown option $1, exit."
      exit 1
      ;;
  esac

  shift
done

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

# Continue in calling script.

