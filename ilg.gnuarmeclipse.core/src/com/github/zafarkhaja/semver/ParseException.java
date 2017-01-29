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

/**
 * Thrown to indicate an error during the parsing.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @since 0.7.0
 */
public class ParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -927431441640075695L;

	/**
	 * Constructs a {@code ParseException} instance with no error message.
	 */
	public ParseException() {
		super();
	}

	/**
	 * Constructs a {@code ParseException} instance with an error message.
	 *
	 * @param message
	 *            the error message
	 */
	public ParseException(String message) {
		super(message);
	}

	/**
	 * Constructs a {@code ParseException} instance with an error message and
	 * the cause exception.
	 *
	 * @param message
	 *            the error message
	 * @param cause
	 *            an exception that caused this exception
	 */
	public ParseException(String message, UnexpectedCharacterException cause) {
		super(message);
		initCause(cause);
	}

	/**
	 * Returns the string representation of this exception.
	 *
	 * @return the string representation of this exception
	 */
	@Override
	public String toString() {
		Throwable cause = getCause();
		String msg = getMessage();
		if (msg != null) {
			msg += ((cause != null) ? " (" + cause.toString() + ")" : "");
			return msg;
		}
		return ((cause != null) ? cause.toString() : "");
	}
}
