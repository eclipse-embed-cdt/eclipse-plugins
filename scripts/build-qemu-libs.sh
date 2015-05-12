

if false
then

# ----- Get the Zlib library. -----

# The Zlib library is available from
#   http://www.zlib.net
# with source files ready to download from SourceForge
#   https://sourceforge.net/projects/libpng/files/zlib

ZLIB_VERSION="1.2.8"
ZLIB_FOLDER="zlib-${ZLIB_VERSION}"
ZLIB_ARCHIVE="${ZLIB_FOLDER}.tar.gz"
ZLIB_DOWNLOAD_URL="http://sourceforge.net/projects/libpng/files/\
zlib/${ZLIB_VERSION}/${ZLIB_ARCHIVE}"

do_download --url "${ZLIB_DOWNLOAD_URL}" --archive-name "${ZLIB_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"


# ----- Get the iconv library. -----

# The iconv library is available from
#   https://www.gnu.org/software/libiconv/
# with source files ready to download from
#   http://ftpmirror.gnu.org/libiconv

ICONV_VERSION="1.14"
ICONV_VERSION_RELEASE="${ICONV_VERSION}"
ICONV_FOLDER="libiconv-${ICONV_VERSION_RELEASE}"
ICONV_ARCHIVE="${ICONV_FOLDER}.tar.gz"
ICONV_DOWNLOAD_URL="http://ftpmirror.gnu.org/libiconv/${ICONV_ARCHIVE}"

do_download --url "${ICONV_DOWNLOAD_URL}" --archive-name "${ICONV_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"


# ----- Get the gettext library. -----

# The gettext library is available from
#   https://www.gnu.org/software/gettext/
# with source files ready to download from
#   http://ftp.gnu.org/gnu/gettext/

GETTEXT_VERSION="0.19"
GETTEXT_VERSION_RELEASE="${GETTEXT_VERSION}.4"
GETTEXT_FOLDER="gettext-${GETTEXT_VERSION_RELEASE}"
GETTEXT_ARCHIVE="${GETTEXT_FOLDER}.tar.gz"
GETTEXT_DOWNLOAD_URL="http://ftp.gnu.org/gnu/gettext/${GETTEXT_ARCHIVE}"

do_download --url "${GETTEXT_DOWNLOAD_URL}" --archive-name "${GETTEXT_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"


# ----- Get the libffi library. -----

LIBFFI_VERSION="3.2.1"
LIBFFI_FOLDER="libffi-${LIBFFI_VERSION}"
LIBFFI_ARCHIVE="${LIBFFI_FOLDER}.tar.gz"
LIBFFI_DOWNLOAD_URL="ftp://sourceware.org/pub/\
libffi/${LIBFFI_ARCHIVE}"

do_download --url "${LIBFFI_DOWNLOAD_URL}" --archive-name "${LIBFFI_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"


# ----- Get the GLib library. -----

# The GLib library is available from
#   https://developer.gnome.org/glib/
# with source files ready to download from
#   http://ftp.gnome.org/pub/GNOME/sources/glib/

GLIB_VERSION="2.42"
GLIB_VERSION_RELEASE="${GLIB_VERSION}.2"
GLIB_FOLDER="glib-${GLIB_VERSION_RELEASE}"
GLIB_ARCHIVE="${GLIB_FOLDER}.tar.xz"
GLIB_DOWNLOAD_URL="http://ftp.gnome.org/pub/GNOME/sources/glib/\
${GLIB_VERSION}/${GLIB_ARCHIVE}"

do_download --url "${GLIB_DOWNLOAD_URL}" --archive-name "${GLIB_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"


# ----- Get the pixman library. -----

# Although available as a submodule in the QEMU git, we prefer to build
# the library separately, to have a better control.
PIXMAN_VERSION="0.32.6"
PIXMAN_FOLDER="pixman-${PIXMAN_VERSION}"
PIXMAN_ARCHIVE="${PIXMAN_FOLDER}.tar.bz2"
PIXMAN_DOWNLOAD_URL="http://xorg.freedesktop.org/releases/individual/lib/\
${PIXMAN_ARCHIVE}"

do_download --url "${PIXMAN_DOWNLOAD_URL}" --archive-name "${PIXMAN_ARCHIVE}" \
  --download-folder "${DOWNLOAD_FOLDER}"

fi





if false
then

# Build and install the Zlib library.
if [ ! \( -d "${build_folder}/${ZLIB_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libz.a" -o \
          -f "${install_folder}/lib64/libz.a" \) ]
then
  # Clean build folder.
  rm -rf "${build_folder}/${ZLIB_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  # Unpack locally.
  cd "${build_folder}"
  tar -xzf "${download_folder}/${ZLIB_ARCHIVE}"

  if [ "${target_name}" == "win" ]
  then

    # See https://aur.archlinux.org/packages/mingw-w64-zlib/
    cd "${build_folder}/${ZLIB_FOLDER}"
    sed -ie "s/dllwrap/${cross_compile_prefix}-dllwrap/p" win32/Makefile.gcc

    echo
    echo "make ${ZLIB_FOLDER}..."

    # Build & install. Definitions added inside make
    make -f win32/Makefile.gcc install \
    CFLAGS="-m${target_bits} -pipe -O2 -g -Wall -Wp,-D_FORTIFY_SOURCE=2 -fexceptions --param=ssp-buffer-size=4 -Wno-implicit-function-declaration" \
    PREFIX="${cross_compile_prefix}-" \
    BINARY_PATH="${install_folder}/bin" \
    INCLUDE_PATH="${install_folder}/include" \
    LIBRARY_PATH="${install_folder}/lib" \

    # Only the static version is used.
    install -m644 -t "${install_folder}/lib" libz.a
    #install -m644 -t "${install_folder}/lib" libz.dll.a

    "${cross_compile_prefix}-ranlib" "${install_folder}/lib/libz.a"

  else

    # See: https://www.archlinux.org/packages/core/x86_64/zlib/

    echo
    echo "configure ${ZLIB_FOLDER}..."

    cd "${build_folder}/${ZLIB_FOLDER}"

    # Configure native
    CFLAGS="-m${target_bits} -pipe" \
    \
    bash "./configure" \
      --static \
      --prefix="${install_folder}" \

    echo
    echo "make ${ZLIB_FOLDER}..."

    # Build. 'all' better be explicit.
    make all docs
    make install

  fi

  # Please note that Zlib generates a lib/pkgconfig/zlib.pc file.
fi

# Build and install the iconv library.
if [ ! \( -d "${build_folder}/${ICONV_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libiconv.a" -o \
          -f "${install_folder}/lib64/libiconv.a" \) ]
then
  # Clean build folder.
  rm -rf "${build_folder}/${ICONV_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  cd "${build_folder}"
  tar -xzf "${download_folder}/${ICONV_ARCHIVE}"

  echo
  echo "configure ${ICONV_FOLDER}..."

  if [ "${target_name}" == "win" ]
  then

    # See: https://aur.archlinux.org/packages/mingw-w64-libiconv/

    ICONV_ARCH_FOLDER="mingw-w64-libiconv"
    ICONV_ARCH_VERSION_RELEASE="${ICONV_VERSION}-9"
    ICONV_ARCH_ARCHIVE="${ICONV_ARCH_FOLDER}-${ICONV_ARCH_VERSION_RELEASE}.tar.gz"

    rm -rf "${build_folder}/${ICONV_ARCH_FOLDER}"
    cd "${build_folder}"
    tar -xzf "${QEMU_PATCHES_FOLDER}/arch/${ICONV_ARCH_ARCHIVE}"

    cd "${build_folder}/${ICONV_FOLDER}"
    if [ "${ICONV_VERSION}" == "1.14" ]
    then

      patch -p2 -i "../${ICONV_ARCH_FOLDER}/00-wchar-libiconv-1.14.patch"
      patch -p2 -i "../${ICONV_ARCH_FOLDER}/01-reloc-libiconv-1.14.patch"
      patch -p2 -i "../${ICONV_ARCH_FOLDER}/02-reloc-libiconv-1.14.patch"
      patch -p2 -i "../${ICONV_ARCH_FOLDER}/03-cygwin-libiconv-1.14.patch"
      patch -p2 -i "../${ICONV_ARCH_FOLDER}/libiconv-1.14-2-mingw.patch"

    fi

    # Configure cross
    # The bash is required to keep libtool happy, otherwise it crashes.
    # "--enable-shared" is required by later builds when shared.
    cd "${build_folder}/${ICONV_FOLDER}"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits}" \
    \
    bash "./configure" \
      --host="${cross_compile_prefix}" \
      --program-prefix="${cross_compile_prefix}" \
      \
      --prefix=${install_folder} \
      --enable-static \
      --disable-shared \
      --disable-nls \

  else

    # See: https://aur.archlinux.org/packages/libiconv/

    # Configure native
    cd "${build_folder}/${ICONV_FOLDER}"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits}" \
    \
    bash "./configure" \
      --prefix=${install_folder} \
      --enable-static \
      --disable-shared \
      --disable-nls \


    cp -f /usr/include/stdio.h srclib/stdio.in.h
    # There is a "cp -f /usr/include/stdio.h srclib/stdio.in.h" in arch,
    # but it is not clear if it is needed.
  fi

  echo
  echo "make ${ICONV_FOLDER}..."

  # Build. 'all' must be explicit.
  make all SHELL=/bin/bash
  make install

  # Please note that libiconv does not create pkgconfig files and needs to be
  # refered manually.

fi

# Build and install the gettext library.
if [ ! \( -d "${build_folder}/${GETTEXT_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libasprintf.a" -o \
          -f "${install_folder}/lib64/libasprintf.a" \) ]
then
  # Clean build folder.
  rm -rf "${build_folder}/${GETTEXT_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  cd "${build_folder}"
  tar -xzf "${download_folder}/${GETTEXT_ARCHIVE}"

  echo
  echo "configure ${GETTEXT_FOLDER}/gettext-runtime..."

  if [ "${target_name}" == "win" ]
  then

    # See: https://aur.archlinux.org/packages/mingw-w64-gettext/

    # Configure cross
    cd "${build_folder}/${GETTEXT_FOLDER}/gettext-runtime"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    \
    bash "./configure" \
      --host="${cross_compile_prefix}" \
      --enable-threads=win32 \
      \
      --prefix="${install_folder}" \
      --disable-java \
      --disable-native-java \
      --disable-csharp \
      --without-emacs \
      --enable-static \
      --disable-shared \
      --disable-libtool-lock \

  else

    # See: https://www.archlinux.org/packages/core/x86_64/gettext/

    # Configure native
    cd "${build_folder}/${GETTEXT_FOLDER}/gettext-runtime"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -I${install_folder}/include" \
    LDFLAGS="-L${install_folder}/lib" \
    \
    bash "./configure" \
      --prefix="${install_folder}" \
      --disable-java \
      --disable-native-java \
      --disable-csharp \
      --without-emacs \
      --enable-static \
      --disable-shared \
      --disable-libtool-lock \

  fi

  echo
  echo "make ${GETTEXT_FOLDER}..."

  # Build
  make
  make install

  # Please note that gettext does not create pkgconfig files and needs to be
  # refered manually.

fi

# Build and install the new Zlib library.
if [ ! \( -d "${build_folder}/${LIBFFI_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libffi.a" -o \
          -f "${install_folder}/lib64/libffi.a" \) ]
then
  # Clean build folder.
  rm -rf "${build_folder}/${LIBFFI_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  # Unpack locally.
  cd "${build_folder}"
  tar -xzf "${download_folder}/${LIBFFI_ARCHIVE}"

  if [ "${target_name}" == "win" ]
  then

    # See https://aur.archlinux.org/packages/mingw-w64-libffi/

    echo
    echo "configure ${LIBFFI_FOLDER}..."

    cd "${build_folder}/${LIBFFI_FOLDER}"
    # Configure cross
    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -pipe" \
    \
    bash "./configure" \
      --host="${cross_compile_prefix}" \
      \
      --prefix="${install_folder}" \
      --enable-pax_emutramp \
      --enable-static \
      --disable-shared \

  else

    # See: https://www.archlinux.org/packages/core/x86_64/libffi/

    echo
    echo "configure ${LIBFFI_FOLDER}..."

    cd "${build_folder}/${LIBFFI_FOLDER}"
    # Configure native
    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -pipe" \
    \
    bash "./configure" \
      --prefix="${install_folder}" \
      --enable-pax_emutramp \
      --enable-static \
      --disable-shared \

  fi

  echo
  echo "make ${LIBFFI_FOLDER}..."

  # Build. 'all' better be explicit.
  make all
  make install

  # Please note that LIBFFI generates a lib/pkgconfig/libffi.pc file.
fi


# Build and install the GLib library.
if [ ! \( -d "${build_folder}/${GLIB_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libglib-2.0.a" -o \
          -f "${install_folder}/lib64/libglib-2.0.a" \) ]
then

  # See: https://aur.archlinux.org/packages/mingw-w64-glib2/

  # Clean build folder.
  rm -rf "${build_folder}/${GLIB_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  cd "${build_folder}"
  tar -xJf "${download_folder}/${GLIB_ARCHIVE}"

  echo
  echo "configure ${GLIB_FOLDER}..."

  if [ "${target_name}" == "win" ]
  then

    GLIB_ARCH_FOLDER="mingw-w64-glib2"
    GLIB_ARCH_VERSION_RELEASE="${GLIB_VERSION_RELEASE}-1"
    GLIB_ARCH_ARCHIVE="${GLIB_ARCH_FOLDER}-${GLIB_ARCH_VERSION_RELEASE}.tar.gz"

    rm -rf "${build_folder}/${GLIB_ARCH_FOLDER}"
    cd "${build_folder}"
    tar -xzf "${QEMU_PATCHES_FOLDER}/arch/${GLIB_ARCH_ARCHIVE}"

    cd "${build_folder}/${GLIB_FOLDER}"
    if [ "${GLIB_VERSION_RELEASE}" == "2.42.2" ]
    then
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0001-Use-CreateFile-on-Win32-to-make-sure-g_unlink-always.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0003-g_abort.all.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/glib-prefer-constructors-over-DllMain.patch"
      patch -Np0 -i "../${GLIB_ARCH_FOLDER}/glib-send-log-messages-to-correct-stdout-and-stderr.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0015-fix-stat.all.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0021-use-64bit-stat-for-localfile-size-calc.all.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0024-return-actually-written-data-in-printf.all.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0001-gsocket-block-when-errno-says-it-will-block.patch"
      patch -Np1 -i "../${GLIB_ARCH_FOLDER}/0027-no_sys_if_nametoindex.patch"

      # Add -lole32 to the list of the refered libraries, it is required by QEMU
      patch -p0 < "${QEMU_PATCHES_FOLDER}/${GLIB_FOLDER}.patch"

      # detection of if_nametoindex fails as part of libiphlpapi.a
      sed -i "s|#undef HAVE_IF_NAMETOINDEX|#define HAVE_IF_NAMETOINDEX 1|g" config.h.in
  
      NOCONFIGURE=1 bash "./autogen.sh"

    else

      echo "Unsupported GLIB version"
      exit 1
    fi

    # Configure cross
    cd "${build_folder}/${GLIB_FOLDER}"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -I${install_folder}/include" \
    LDFLAGS="-v -L${install_folder}/lib" \
    \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/${cross_compile_prefix}-pkg-config" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "./configure" \
      --host="${cross_compile_prefix}" \
      \
      --prefix="${install_folder}" \
      --with-libiconv=gnu \
      --enable-static \
      --disable-shared \
      --disable-selinux \
      --with-pcre=internal \
      --disable-fam \

  else

    # Configure native; --with-libiconv=gnu mandatory
    cd "${build_folder}/${GLIB_FOLDER}"

    CONFIG_SHELL="/bin/bash" \
    CFLAGS="-m${target_bits} -I${install_folder}/include" \
    LDFLAGS="-v -L${install_folder}/lib" \
    \
    PKG_CONFIG="${QEMU_GIT_FOLDER}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "./configure" \
      --prefix="${install_folder}" \
      --with-libiconv=gnu \
      --enable-static \
      --disable-shared \
      --disable-selinux \
      --with-pcre=internal \
      --disable-fam \

  fi

  echo
  echo "make ${GLIB_FOLDER}..."

  # Build
  make
  make install

  # Please note that GLIB generates a lot of lib/pkgconfig/*.pc files.

fi


# Build and install the new Zlib library.
if [ ! \( -d "${build_folder}/${PIXMAN_FOLDER}" \) -o \
     ! \( -f "${install_folder}/lib/libpixman-1.a" -o \
          -f "${install_folder}/lib64/libpixman-1.a" \) ]
then
  # Clean build folder.
  rm -rf "${build_folder}/${PIXMAN_FOLDER}"

  # Prepare install folder.
  mkdir -p "${install_folder}"

  # Unpack locally.
  cd "${build_folder}"
  tar -xjf "${download_folder}/${PIXMAN_ARCHIVE}"

  if [ "${target_name}" == "win" ]
  then

    # See https://aur.archlinux.org/packages/mingw-w64-pixman/

    echo
    echo "configure ${PIXMAN_FOLDER}..."

    cd "${build_folder}/${PIXMAN_FOLDER}"

    # Configure cross
    CFLAGS="-m${target_bits} -pipe" \
    LDFLAGS="-v" \
    \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/${cross_compile_prefix}-pkg-config" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "./configure" \
      --host="${cross_compile_prefix}" \
      \
      --prefix="${install_folder}" \
      --disable-gtk \
      --enable-static \
      --disable-shared \

  else

    # See: https://www.archlinux.org/packages/extra/x86_64/pixman/

    echo
    echo "configure ${PIXMAN_FOLDER}..."

    cd "${build_folder}/${PIXMAN_FOLDER}"

    # Configure native
    CFLAGS="-v -m${target_bits} -pipe" \
    LDFLAGS="-v" \
    \
    PKG_CONFIG="${git_folder}/gnuarmeclipse/scripts/pkg-config-dbg" \
    PKG_CONFIG_PATH="${install_folder}/lib/pkgconfig":"${install_folder}/lib64/pkgconfig" \
    \
    bash "./configure" \
      --prefix="${install_folder}" \
      --disable-gtk \
      --disable-libpng \
      --enable-static \
      --disable-shared \

  fi

  echo
  echo "make ${PIXMAN_FOLDER}..."

  # Build. Tests fail on OS X, so disable them.
  make SUBDIRS=pixman
  make install SUBDIRS=pixman

  # Please note that PIXMAN generates a lib/pkgconfig/pixman-1.pc file.
fi

fi

