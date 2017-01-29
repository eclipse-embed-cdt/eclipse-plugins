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

import com.github.zafarkhaja.semver.util.Stream;
import com.github.zafarkhaja.semver.util.UnexpectedElementException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import static com.github.zafarkhaja.semver.VersionParser.CharType.*;

/**
 * A parser for the SemVer Version.
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 * @since 0.7.0
 */
class VersionParser implements Parser<Version> {

	/**
	 * Valid character types.
	 */
	static enum CharType implements Stream.ElementType<Character> {

		DIGIT {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				if (chr == null) {
					return false;
				}
				return chr >= '0' && chr <= '9';
			}
		},
		LETTER {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				if (chr == null) {
					return false;
				}
				return (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z');
			}
		},
		DOT {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				if (chr == null) {
					return false;
				}
				return chr == '.';
			}
		},
		HYPHEN {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				if (chr == null) {
					return false;
				}
				return chr == '-';
			}
		},
		PLUS {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				if (chr == null) {
					return false;
				}
				return chr == '+';
			}
		},
		EOI {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				return chr == null;
			}
		},
		ILLEGAL {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isMatchedBy(Character chr) {
				EnumSet<CharType> itself = EnumSet.of(ILLEGAL);
				for (CharType type : EnumSet.complementOf(itself)) {
					if (type.isMatchedBy(chr)) {
						return false;
					}
				}
				return true;
			}
		};

		/**
		 * Gets the type for a given character.
		 *
		 * @param chr
		 *            the character to get the type for
		 * @return the type of the specified character
		 */
		static CharType forCharacter(Character chr) {
			for (CharType type : values()) {
				if (type.isMatchedBy(chr)) {
					return type;
				}
			}
			return null;
		}
	}

	/**
	 * The stream of characters.
	 */
	private final Stream<Character> chars;

	/**
	 * Constructs a {@code VersionParser} instance with the input string to
	 * parse.
	 *
	 * @param input
	 *            the input string to parse
	 * @throws IllegalArgumentException
	 *             if the input string is {@code NULL} or empty
	 */
	VersionParser(String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("Input string is NULL or empty");
		}
		Character[] elements = new Character[input.length()];
		for (int i = 0; i < input.length(); i++) {
			elements[i] = input.charAt(i);
		}
		chars = new Stream<Character>(elements);
	}

	/**
	 * Parses the input string.
	 *
	 * @param input
	 *            the input string to parse
	 * @return a valid version object
	 * @throws ParseException
	 *             when there is a grammar error
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	@Override
	public Version parse(String input) {
		return parseValidSemVer();
	}

	/**
	 * Parses the whole version including pre-release version and build
	 * metadata.
	 *
	 * @param version
	 *            the version string to parse
	 * @return a valid version object
	 * @throws IllegalArgumentException
	 *             if the input string is {@code NULL} or empty
	 * @throws ParseException
	 *             when there is a grammar error
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	static Version parseValidSemVer(String version) {
		VersionParser parser = new VersionParser(version);
		return parser.parseValidSemVer();
	}

	/**
	 * Parses the version core.
	 *
	 * @param versionCore
	 *            the version core string to parse
	 * @return a valid normal version object
	 * @throws IllegalArgumentException
	 *             if the input string is {@code NULL} or empty
	 * @throws ParseException
	 *             when there is a grammar error
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	static NormalVersion parseVersionCore(String versionCore) {
		VersionParser parser = new VersionParser(versionCore);
		return parser.parseVersionCore();
	}

	/**
	 * Parses the pre-release version.
	 *
	 * @param preRelease
	 *            the pre-release version string to parse
	 * @return a valid pre-release version object
	 * @throws IllegalArgumentException
	 *             if the input string is {@code NULL} or empty
	 * @throws ParseException
	 *             when there is a grammar error
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	static MetadataVersion parsePreRelease(String preRelease) {
		VersionParser parser = new VersionParser(preRelease);
		return parser.parsePreRelease();
	}

	/**
	 * Parses the build metadata.
	 *
	 * @param build
	 *            the build metadata string to parse
	 * @return a valid build metadata object
	 * @throws IllegalArgumentException
	 *             if the input string is {@code NULL} or empty
	 * @throws ParseException
	 *             when there is a grammar error
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	static MetadataVersion parseBuild(String build) {
		VersionParser parser = new VersionParser(build);
		return parser.parseBuild();
	}

	/**
	 * Parses the {@literal <valid semver>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <valid semver> ::= <version core>
	 *                  | <version core> "-" <pre-release>
	 *                  | <version core> "+" <build>
	 *                  | <version core> "-" <pre-release> "+" <build>
	 * }
	 * </pre>
	 *
	 * @return a valid version object
	 */
	private Version parseValidSemVer() {
		NormalVersion normal = parseVersionCore();
		MetadataVersion preRelease = MetadataVersion.NULL;
		MetadataVersion build = MetadataVersion.NULL;

		Character next = consumeNextCharacter(HYPHEN, PLUS, EOI);
		if (HYPHEN.isMatchedBy(next)) {
			preRelease = parsePreRelease();
			next = consumeNextCharacter(PLUS, EOI);
			if (PLUS.isMatchedBy(next)) {
				build = parseBuild();
			}
		} else if (PLUS.isMatchedBy(next)) {
			build = parseBuild();
		}
		consumeNextCharacter(EOI);
		return new Version(normal, preRelease, build);
	}

	/**
	 * Parses the {@literal <version core>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <version core> ::= <major> "." <minor> "." <patch>
	 * }
	 * </pre>
	 *
	 * @return a valid normal version object
	 */
	private NormalVersion parseVersionCore() {
		int major = Integer.parseInt(numericIdentifier());
		consumeNextCharacter(DOT);
		int minor = Integer.parseInt(numericIdentifier());
		int patch;
		try {
			consumeNextCharacter(DOT);
			patch = Integer.parseInt(numericIdentifier());
		} catch (UnexpectedCharacterException e) {
			patch = 0;
		}
		return new NormalVersion(major, minor, patch);
	}

	/**
	 * Parses the {@literal <pre-release>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <pre-release> ::= <dot-separated pre-release identifiers>
	 *
	 * <dot-separated pre-release identifiers> ::= <pre-release identifier>
	 *    | <pre-release identifier> "." <dot-separated pre-release identifiers>
	 * }
	 * </pre>
	 *
	 * @return a valid pre-release version object
	 */
	private MetadataVersion parsePreRelease() {
		ensureValidLookahead(DIGIT, LETTER, HYPHEN);
		List<String> idents = new ArrayList<String>();
		do {
			idents.add(preReleaseIdentifier());
			if (chars.positiveLookahead(DOT)) {
				consumeNextCharacter(DOT);
				continue;
			}
			break;
		} while (true);
		return new MetadataVersion(idents.toArray(new String[idents.size()]));
	}

	/**
	 * Parses the {@literal <pre-release identifier>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <pre-release identifier> ::= <alphanumeric identifier>
	 *                            | <numeric identifier>
	 * }
	 * </pre>
	 *
	 * @return a single pre-release identifier
	 */
	private String preReleaseIdentifier() {
		checkForEmptyIdentifier();
		CharType boundary = nearestCharType(DOT, PLUS, EOI);
		if (chars.positiveLookaheadBefore(boundary, LETTER, HYPHEN)) {
			return alphanumericIdentifier();
		} else {
			return numericIdentifier();
		}
	}

	/**
	 * Parses the {@literal <build>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <build> ::= <dot-separated build identifiers>
	 *
	 * <dot-separated build identifiers> ::= <build identifier>
	 *                | <build identifier> "." <dot-separated build identifiers>
	 * }
	 * </pre>
	 *
	 * @return a valid build metadata object
	 */
	private MetadataVersion parseBuild() {
		ensureValidLookahead(DIGIT, LETTER, HYPHEN);
		List<String> idents = new ArrayList<String>();
		do {
			idents.add(buildIdentifier());
			if (chars.positiveLookahead(DOT)) {
				consumeNextCharacter(DOT);
				continue;
			}
			break;
		} while (true);
		return new MetadataVersion(idents.toArray(new String[idents.size()]));
	}

	/**
	 * Parses the {@literal <build identifier>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <build identifier> ::= <alphanumeric identifier>
	 *                      | <digits>
	 * }
	 * </pre>
	 *
	 * @return a single build identifier
	 */
	private String buildIdentifier() {
		checkForEmptyIdentifier();
		CharType boundary = nearestCharType(DOT, EOI);
		if (chars.positiveLookaheadBefore(boundary, LETTER, HYPHEN)) {
			return alphanumericIdentifier();
		} else {
			return digits();
		}
	}

	/**
	 * Parses the {@literal <numeric identifier>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <numeric identifier> ::= "0"
	 *                        | <positive digit>
	 *                        | <positive digit> <digits>
	 * }
	 * </pre>
	 *
	 * @return a string representing the numeric identifier
	 */
	private String numericIdentifier() {
		checkForLeadingZeroes();
		return digits();
	}

	/**
	 * Parses the {@literal <alphanumeric identifier>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <alphanumeric identifier> ::= <non-digit>
	 *             | <non-digit> <identifier characters>
	 *             | <identifier characters> <non-digit>
	 *             | <identifier characters> <non-digit> <identifier characters>
	 * }
	 * </pre>
	 *
	 * @return a string representing the alphanumeric identifier
	 */
	private String alphanumericIdentifier() {
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(consumeNextCharacter(DIGIT, LETTER, HYPHEN));
		} while (chars.positiveLookahead(DIGIT, LETTER, HYPHEN));
		return sb.toString();
	}

	/**
	 * Parses the {@literal <digits>} non-terminal.
	 *
	 * <pre>
	 * {@literal
	 * <digits> ::= <digit>
	 *            | <digit> <digits>
	 * }
	 * </pre>
	 *
	 * @return a string representing the digits
	 */
	private String digits() {
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(consumeNextCharacter(DIGIT));
		} while (chars.positiveLookahead(DIGIT));
		return sb.toString();
	}

	/**
	 * Finds the nearest character type.
	 *
	 * @param types
	 *            the character types to choose from
	 * @return the nearest character type or {@code EOI}
	 */
	private CharType nearestCharType(CharType... types) {
		for (Character chr : chars) {
			for (CharType type : types) {
				if (type.isMatchedBy(chr)) {
					return type;
				}
			}
		}
		return EOI;
	}

	/**
	 * Checks for leading zeroes in the numeric identifiers.
	 *
	 * @throws ParseException
	 *             if a numeric identifier has leading zero(es)
	 */
	private void checkForLeadingZeroes() {
		Character la1 = chars.lookahead(1);
		Character la2 = chars.lookahead(2);
		if (la1 != null && la1 == '0' && DIGIT.isMatchedBy(la2)) {
			throw new ParseException("Numeric identifier MUST NOT contain leading zeroes");
		}
	}

	/**
	 * Checks for empty identifiers in the pre-release version or build
	 * metadata.
	 *
	 * @throws ParseException
	 *             if the pre-release version or build metadata have empty
	 *             identifier(s)
	 */
	private void checkForEmptyIdentifier() {
		Character la = chars.lookahead(1);
		if (DOT.isMatchedBy(la) || PLUS.isMatchedBy(la) || EOI.isMatchedBy(la)) {
			throw new ParseException("Identifiers MUST NOT be empty",
					new UnexpectedCharacterException(la, chars.currentOffset(), DIGIT, LETTER, HYPHEN));
		}
	}

	/**
	 * Tries to consume the next character in the stream.
	 *
	 * @param expected
	 *            the expected types of the next character
	 * @return the next character in the stream
	 * @throws UnexpectedCharacterException
	 *             when encounters an unexpected character type
	 */
	private Character consumeNextCharacter(CharType... expected) {
		try {
			return chars.consume(expected);
		} catch (UnexpectedElementException e) {
			throw new UnexpectedCharacterException(e);
		}
	}

	/**
	 * Checks if the next character in the stream is valid.
	 *
	 * @param expected
	 *            the expected types of the next character
	 * @throws UnexpectedCharacterException
	 *             if the next character is not valid
	 */
	private void ensureValidLookahead(CharType... expected) {
		if (!chars.positiveLookahead(expected)) {
			throw new UnexpectedCharacterException(chars.lookahead(1), chars.currentOffset(), expected);
		}
	}
}
