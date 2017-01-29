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
package com.github.zafarkhaja.semver.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple stream class used to represent a stream of characters or tokens.
 *
 * @param <E>
 *            the type of elements held in this stream
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @see com.github.zafarkhaja.semver.VersionParser
 * @see com.github.zafarkhaja.semver.expr.Lexer
 * @see com.github.zafarkhaja.semver.expr.ExpressionParser
 * @since 0.7.0
 */
public class Stream<E> implements Iterable<E> {

	/**
	 * The {@code ElementType} interface represents types of the elements held
	 * by this stream and can be used for stream filtering.
	 *
	 * @param <E>
	 *            type of elements held by this stream
	 */
	public static interface ElementType<E> {

		/**
		 * Checks if the specified element matches this type.
		 *
		 * @param element
		 *            the element to be tested
		 * @return {@code true} if the element matches this type or
		 *         {@code false} otherwise
		 */
		boolean isMatchedBy(E element);
	}

	/**
	 * The array holding all the elements of this stream.
	 */
	private final E[] elements;

	/**
	 * The current offset which is incremented when an element is consumed.
	 *
	 * @see #consume()
	 */
	private int offset = 0;

	/**
	 * Constructs a stream containing the specified elements.
	 *
	 * The stream does not store the real elements but the defensive copy.
	 *
	 * @param elements
	 *            the elements to be streamed
	 */
	public Stream(E[] elements) {
		this.elements = elements.clone();
	}

	/**
	 * Consumes the next element in this stream.
	 *
	 * @return the next element in this stream or {@code null} if no more
	 *         elements left
	 */
	public E consume() {
		if (offset >= elements.length) {
			return null;
		}
		return elements[offset++];
	}

	/**
	 * Consumes the next element in this stream only if it is of the expected
	 * types.
	 *
	 * @param <T>
	 *            represents the element type of this stream, removes the
	 *            "unchecked generic array creation for varargs parameter"
	 *            warnings
	 * @param expected
	 *            the types which are expected
	 * @return the next element in this stream
	 * @throws UnexpectedElementException
	 *             if the next element is of an unexpected type
	 */
	public <T extends ElementType<E>> E consume(T... expected) {
		E lookahead = lookahead(1);
		for (ElementType<E> type : expected) {
			if (type.isMatchedBy(lookahead)) {
				return consume();
			}
		}
		throw new UnexpectedElementException(lookahead, offset, expected);
	}

	/**
	 * Pushes back one element at a time.
	 */
	public void pushBack() {
		if (offset > 0) {
			offset--;
		}
	}

	/**
	 * Returns the next element in this stream without consuming it.
	 *
	 * @return the next element in this stream
	 */
	public E lookahead() {
		return lookahead(1);
	}

	/**
	 * Returns the element at the specified position in this stream without
	 * consuming it.
	 *
	 * @param position
	 *            the position of the element to return
	 * @return the element at the specified position or {@code null} if no more
	 *         elements left
	 */
	public E lookahead(int position) {
		int idx = offset + position - 1;
		if (idx < elements.length) {
			return elements[idx];
		}
		return null;
	}

	/**
	 * Returns the current offset of this stream.
	 *
	 * @return the current offset of this stream
	 */
	public int currentOffset() {
		return offset;
	}

	/**
	 * Checks if the next element in this stream is of the expected types.
	 *
	 * @param <T>
	 *            represents the element type of this stream, removes the
	 *            "unchecked generic array creation for varargs parameter"
	 *            warnings
	 * @param expected
	 *            the expected types
	 * @return {@code true} if the next element is of the expected types or
	 *         {@code false} otherwise
	 */
	public <T extends ElementType<E>> boolean positiveLookahead(T... expected) {
		for (ElementType<E> type : expected) {
			if (type.isMatchedBy(lookahead(1))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if there exists an element in this stream of the expected types
	 * before the specified type.
	 *
	 * @param <T>
	 *            represents the element type of this stream, removes the
	 *            "unchecked generic array creation for varargs parameter"
	 *            warnings
	 * @param before
	 *            the type before which to search
	 * @param expected
	 *            the expected types
	 * @return {@code true} if there is an element of the expected types before
	 *         the specified type or {@code false} otherwise
	 */
	public <T extends ElementType<E>> boolean positiveLookaheadBefore(ElementType<E> before, T... expected) {
		E lookahead;
		for (int i = 1; i <= elements.length; i++) {
			lookahead = lookahead(i);
			if (before.isMatchedBy(lookahead)) {
				break;
			}
			for (ElementType<E> type : expected) {
				if (type.isMatchedBy(lookahead)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if there is an element in this stream of the expected types until
	 * the specified position.
	 *
	 * @param <T>
	 *            represents the element type of this stream, removes the
	 *            "unchecked generic array creation for varargs parameter"
	 *            warnings
	 * @param until
	 *            the position until which to search
	 * @param expected
	 *            the expected types
	 * @return {@code true} if there is an element of the expected types until
	 *         the specified position or {@code false} otherwise
	 */
	public <T extends ElementType<E>> boolean positiveLookaheadUntil(int until, T... expected) {
		for (int i = 1; i <= until; i++) {
			for (ElementType<E> type : expected) {
				if (type.isMatchedBy(lookahead(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an iterator over elements that are left in this stream.
	 *
	 * @return an iterator of the remaining elements in this stream
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			/**
			 * The index to indicate the current position of this iterator.
			 *
			 * The starting point is set to the current value of this stream's
			 * offset, so that it doesn't iterate over consumed elements.
			 */
			private int index = offset;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return index < elements.length;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public E next() {
				if (index >= elements.length) {
					throw new NoSuchElementException();
				}
				return elements[index++];
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Returns an array containing all of the elements that are left in this
	 * stream.
	 *
	 * The returned array is a safe copy.
	 *
	 * @return an array containing all of elements in this stream
	 */
	public E[] toArray() {
		return Arrays.copyOfRange(elements, offset, elements.length);
	}
}
