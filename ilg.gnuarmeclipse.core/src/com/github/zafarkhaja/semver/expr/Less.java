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
package com.github.zafarkhaja.semver.expr;

import com.github.zafarkhaja.semver.Version;

/**
 * Expression for the comparison "less than" operator.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @since 0.7.0
 */
class Less implements Expression {

	/**
	 * The parsed version, the right-hand operand of the "less than" operator.
	 */
	private final Version parsedVersion;

	/**
	 * Constructs a {@code Less} expression with the parsed version.
	 *
	 * @param parsedVersion
	 *            the parsed version
	 */
	Less(Version parsedVersion) {
		this.parsedVersion = parsedVersion;
	}

	/**
	 * Checks if the current version is less than the parsed version.
	 *
	 * @param version
	 *            the version to compare to, the left-hand operand of the
	 *            "less than" operator
	 * @return {@code true} if the version is less than the parsed version or
	 *         {@code false} otherwise
	 */
	@Override
	public boolean interpret(Version version) {
		return version.lessThan(parsedVersion);
	}
}
