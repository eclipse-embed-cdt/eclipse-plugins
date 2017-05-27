/*
 * The MIT License
 *
 * Copyright 2012-2015 Zafar Khaja <zafarkhaja@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.zafarkhaja.semver;

import java.util.Arrays;

/**
 * The {@code MetadataVersion} class is used to represent the pre-release
 * version and the build metadata.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @since 0.2.0
 */
class MetadataVersion implements Comparable<MetadataVersion> {

	/**
	 * Null metadata, the implementation of the Null Object design pattern.
	 */
	static final MetadataVersion NULL = new NullMetadataVersion();

	/**
	 * The implementation of the Null Object design pattern.
	 */
	private static class NullMetadataVersion extends MetadataVersion {

		/**
		 * Constructs a {@code NullMetadataVersion} instance.
		 */
		public NullMetadataVersion() {
			super(null);
		}

		/**
		 * @throws NullPointerException
		 *             as Null metadata cannot be incremented
		 */
		@Override
		MetadataVersion increment() {
			throw new NullPointerException("Metadata version is NULL");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object other) {
			return other instanceof NullMetadataVersion;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(MetadataVersion other) {
			if (!equals(other)) {
				/**
				 * Pre-release versions have a lower precedence than the
				 * associated normal version. (SemVer p.9)
				 */
				return 1;
			}
			return 0;
		}
	}

	/**
	 * The array containing the version's identifiers.
	 */
	private final String[] idents;

	/**
	 * Constructs a {@code MetadataVersion} instance with identifiers.
	 * 
	 * @param identifiers
	 *            the version's identifiers
	 */
	MetadataVersion(String[] identifiers) {
		idents = identifiers;
	}

	/**
	 * Increments the metadata version.
	 *
	 * @return a new instance of the {@code MetadataVersion} class
	 */
	MetadataVersion increment() {
		String[] ids = idents;
		String lastId = ids[ids.length - 1];
		if (isInt(lastId)) {
			int intId = Integer.parseInt(lastId);
			ids[ids.length - 1] = String.valueOf(++intId);
		} else {
			ids = Arrays.copyOf(ids, ids.length + 1);
			ids[ids.length - 1] = String.valueOf(1);
		}
		return new MetadataVersion(ids);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MetadataVersion)) {
			return false;
		}
		return compareTo((MetadataVersion) other) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(idents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String ident : idents) {
			sb.append(ident).append(".");
		}
		return sb.deleteCharAt(sb.lastIndexOf(".")).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(MetadataVersion other) {
		if (other == MetadataVersion.NULL) {
			/**
			 * Pre-release versions have a lower precedence than the associated
			 * normal version. (SemVer p.9)
			 */
			return -1;
		}
		int result = compareIdentifierArrays(other.idents);
		if (result == 0) {
			/**
			 * A larger set of pre-release fields has a higher precedence than a
			 * smaller set, if all of the preceding identifiers are equal.
			 * (SemVer p.11)
			 */
			result = idents.length - other.idents.length;
		}
		return result;
	}

	/**
	 * Compares two arrays of identifiers.
	 *
	 * @param otherIdents
	 *            the identifiers of the other version
	 * @return integer result of comparison compatible with the
	 *         {@code Comparable.compareTo} method
	 */
	private int compareIdentifierArrays(String[] otherIdents) {
		int result = 0;
		int length = getLeastCommonArrayLength(idents, otherIdents);
		for (int i = 0; i < length; i++) {
			result = compareIdentifiers(idents[i], otherIdents[i]);
			if (result != 0) {
				break;
			}
		}
		return result;
	}

	/**
	 * Returns the size of the smallest array.
	 *
	 * @param arr1
	 *            the first array
	 * @param arr2
	 *            the second array
	 * @return the size of the smallest array
	 */
	private int getLeastCommonArrayLength(String[] arr1, String[] arr2) {
		return arr1.length <= arr2.length ? arr1.length : arr2.length;
	}

	/**
	 * Compares two identifiers.
	 *
	 * @param ident1
	 *            the first identifier
	 * @param ident2
	 *            the second identifier
	 * @return integer result of comparison compatible with the
	 *         {@code Comparable.compareTo} method
	 */
	private int compareIdentifiers(String ident1, String ident2) {
		if (isInt(ident1) && isInt(ident2)) {
			return Integer.parseInt(ident1) - Integer.parseInt(ident2);
		} else {
			return ident1.compareTo(ident2);
		}
	}

	/**
	 * Checks if the specified string is an integer.
	 *
	 * @param str
	 *            the string to check
	 * @return {@code true} if the specified string is an integer or
	 *         {@code false} otherwise
	 */
	private boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
