// $ANTLR 3.5.2 org.eclipse.embedcdt.core.liqp/parser/Liquid.g 2020-12-06 19:17:12

  package org.eclipse.embedcdt.core.liqp.parser;


import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("all")
public class LiquidLexer extends Lexer {
	public static final int EOF=-1;
	public static final int ASSIGNMENT=4;
	public static final int ATTRIBUTES=5;
	public static final int And=6;
	public static final int Assign=7;
	public static final int BLOCK=8;
	public static final int Break=9;
	public static final int CAPTURE=10;
	public static final int CASE=11;
	public static final int CBr=12;
	public static final int COMMENT=13;
	public static final int CPar=14;
	public static final int CUSTOM_TAG=15;
	public static final int CUSTOM_TAG_BLOCK=16;
	public static final int CYCLE=17;
	public static final int CaptureEnd=18;
	public static final int CaptureStart=19;
	public static final int CaseEnd=20;
	public static final int CaseStart=21;
	public static final int Col=22;
	public static final int Comma=23;
	public static final int CommentEnd=24;
	public static final int CommentStart=25;
	public static final int Contains=26;
	public static final int Continue=27;
	public static final int Cycle=28;
	public static final int DStr=29;
	public static final int Digit=30;
	public static final int Dot=31;
	public static final int DotDot=32;
	public static final int DoubleNum=33;
	public static final int ELSE=34;
	public static final int ELSIF=35;
	public static final int Else=36;
	public static final int Elsif=37;
	public static final int Empty=38;
	public static final int EndId=39;
	public static final int Eq=40;
	public static final int EqSign=41;
	public static final int FILTER=42;
	public static final int FILTERS=43;
	public static final int FOR_ARRAY=44;
	public static final int FOR_BLOCK=45;
	public static final int FOR_RANGE=46;
	public static final int False=47;
	public static final int ForEnd=48;
	public static final int ForStart=49;
	public static final int GROUP=50;
	public static final int Gt=51;
	public static final int GtEq=52;
	public static final int HASH=53;
	public static final int IF=54;
	public static final int INCLUDE=55;
	public static final int INDEX=56;
	public static final int Id=57;
	public static final int IfEnd=58;
	public static final int IfStart=59;
	public static final int In=60;
	public static final int Include=61;
	public static final int LOOKUP=62;
	public static final int Letter=63;
	public static final int LongNum=64;
	public static final int Lt=65;
	public static final int LtEq=66;
	public static final int Minus=67;
	public static final int NEq=68;
	public static final int NO_SPACE=69;
	public static final int Nil=70;
	public static final int NoSpace=71;
	public static final int OBr=72;
	public static final int OPar=73;
	public static final int OUTPUT=74;
	public static final int Or=75;
	public static final int Other=76;
	public static final int OutEnd=77;
	public static final int OutEndDefaultStrip=78;
	public static final int OutEndStrip=79;
	public static final int OutStart=80;
	public static final int OutStartDefaultStrip=81;
	public static final int OutStartStrip=82;
	public static final int PARAMS=83;
	public static final int PLAIN=84;
	public static final int Pipe=85;
	public static final int QMark=86;
	public static final int RAW=87;
	public static final int RawEnd=88;
	public static final int RawStart=89;
	public static final int SStr=90;
	public static final int Str=91;
	public static final int TABLE=92;
	public static final int TableEnd=93;
	public static final int TableStart=94;
	public static final int TagEnd=95;
	public static final int TagEndDefaultStrip=96;
	public static final int TagEndStrip=97;
	public static final int TagStart=98;
	public static final int TagStartDefaultStrip=99;
	public static final int TagStartStrip=100;
	public static final int True=101;
	public static final int UNLESS=102;
	public static final int UnlessEnd=103;
	public static final int UnlessStart=104;
	public static final int WHEN=105;
	public static final int WITH=106;
	public static final int WS=107;
	public static final int When=108;
	public static final int WhitespaceChar=109;
	public static final int With=110;


	  private boolean stripSpacesAroundTags = false;

	  private boolean inTag = false;
	  private boolean inRaw = false;

	  public LiquidLexer(boolean stripSpacesAroundTags, CharStream input) {
	    this(input, new RecognizerSharedState());
	    this.stripSpacesAroundTags = stripSpacesAroundTags;
	  }

	  private boolean openStripTagAhead() {

	    int indexLA = 1;

	    while(Character.isSpaceChar(input.LA(indexLA)) || input.LA(indexLA) == '\r' || input.LA(indexLA) == '\n') {
	      indexLA++;
	    }

	    return stripSpacesAroundTags
	        ? input.LA(indexLA) == '{' && (input.LA(indexLA + 1) == '{' || input.LA(indexLA + 1) == '\u0025')
	        : input.LA(indexLA) == '{' && (input.LA(indexLA + 1) == '{' || input.LA(indexLA + 1) == '\u0025') && input.LA(indexLA + 2) == '-';
	  }

	  private boolean openRawEndTagAhead() {

	    if(!openTagAhead()) {
	      return false;
	    }

	    int indexLA = 3;

	    while(Character.isSpaceChar(input.LA(indexLA))) {
	      indexLA++;
	    }

	    return input.LA(indexLA) == 'e' &&
	           input.LA(indexLA + 1) == 'n' &&
	           input.LA(indexLA + 2) == 'd' &&
	           input.LA(indexLA + 3) == 'r' &&
	           input.LA(indexLA + 4) == 'a' &&
	           input.LA(indexLA + 5) == 'w';
	  }

	  private boolean openTagAhead() {
	    return input.LA(1) == '{' && (input.LA(2) == '{' || input.LA(2) == '\u0025');
	  }
	  
	  @Override
	  public void reportError(RecognitionException e) {
	    throw new RuntimeException(e); 
	  }

	  private String strip(String text, boolean singleQuoted) {
	    return text.substring(1, text.length() - 1);
	  }


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public LiquidLexer() {} 
	public LiquidLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public LiquidLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "org.eclipse.embedcdt.core.liqp/parser/Liquid.g"; }

	// $ANTLR start "OutStartDefaultStrip"
	public final void mOutStartDefaultStrip() throws RecognitionException {
		try {
			int _type = OutStartDefaultStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:416:22: ({...}? => ( WhitespaceChar )* '{{' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:416:24: {...}? => ( WhitespaceChar )* '{{'
			{
			if ( !((stripSpacesAroundTags)) ) {
				throw new FailedPredicateException(input, "OutStartDefaultStrip", "stripSpacesAroundTags");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:416:51: ( WhitespaceChar )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( ((LA1_0 >= '\t' && LA1_0 <= '\n')||LA1_0=='\r'||LA1_0==' ') ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop1;
				}
			}

			match("{{"); 

			inTag=true; _type=OutStart;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutStartDefaultStrip"

	// $ANTLR start "OutEndDefaultStrip"
	public final void mOutEndDefaultStrip() throws RecognitionException {
		try {
			int _type = OutEndDefaultStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:417:22: ({...}? => '}}' ( WhitespaceChar )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:417:24: {...}? => '}}' ( WhitespaceChar )*
			{
			if ( !((stripSpacesAroundTags)) ) {
				throw new FailedPredicateException(input, "OutEndDefaultStrip", "stripSpacesAroundTags");
			}
			match("}}"); 

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:417:56: ( WhitespaceChar )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( ((LA2_0 >= '\t' && LA2_0 <= '\n')||LA2_0=='\r'||LA2_0==' ') ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop2;
				}
			}

			inTag=false; _type=OutEnd;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutEndDefaultStrip"

	// $ANTLR start "TagStartDefaultStrip"
	public final void mTagStartDefaultStrip() throws RecognitionException {
		try {
			int _type = TagStartDefaultStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:418:22: ({...}? => ( WhitespaceChar )* '{%' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:418:24: {...}? => ( WhitespaceChar )* '{%'
			{
			if ( !((stripSpacesAroundTags)) ) {
				throw new FailedPredicateException(input, "TagStartDefaultStrip", "stripSpacesAroundTags");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:418:51: ( WhitespaceChar )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( ((LA3_0 >= '\t' && LA3_0 <= '\n')||LA3_0=='\r'||LA3_0==' ') ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop3;
				}
			}

			match("{%"); 

			inTag=true; _type=TagStart;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagStartDefaultStrip"

	// $ANTLR start "TagEndDefaultStrip"
	public final void mTagEndDefaultStrip() throws RecognitionException {
		try {
			int _type = TagEndDefaultStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:419:22: ({...}? => '%}' ( WhitespaceChar )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:419:24: {...}? => '%}' ( WhitespaceChar )*
			{
			if ( !((stripSpacesAroundTags)) ) {
				throw new FailedPredicateException(input, "TagEndDefaultStrip", "stripSpacesAroundTags");
			}
			match("%}"); 

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:419:56: ( WhitespaceChar )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( ((LA4_0 >= '\t' && LA4_0 <= '\n')||LA4_0=='\r'||LA4_0==' ') ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop4;
				}
			}

			inTag=false; _type=TagEnd;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagEndDefaultStrip"

	// $ANTLR start "OutStartStrip"
	public final void mOutStartStrip() throws RecognitionException {
		try {
			int _type = OutStartStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:421:15: ( ( WhitespaceChar )* '{{-' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:421:17: ( WhitespaceChar )* '{{-'
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:421:17: ( WhitespaceChar )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( ((LA5_0 >= '\t' && LA5_0 <= '\n')||LA5_0=='\r'||LA5_0==' ') ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop5;
				}
			}

			match("{{-"); 

			inTag=true; _type=OutStart;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutStartStrip"

	// $ANTLR start "OutEndStrip"
	public final void mOutEndStrip() throws RecognitionException {
		try {
			int _type = OutEndStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:422:15: ( '-}}' ( WhitespaceChar )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:422:17: '-}}' ( WhitespaceChar )*
			{
			match("-}}"); 

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:422:23: ( WhitespaceChar )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( ((LA6_0 >= '\t' && LA6_0 <= '\n')||LA6_0=='\r'||LA6_0==' ') ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop6;
				}
			}

			inTag=false; _type=OutEnd;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutEndStrip"

	// $ANTLR start "TagStartStrip"
	public final void mTagStartStrip() throws RecognitionException {
		try {
			int _type = TagStartStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:423:15: ( ( WhitespaceChar )* '{%-' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:423:17: ( WhitespaceChar )* '{%-'
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:423:17: ( WhitespaceChar )*
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( ((LA7_0 >= '\t' && LA7_0 <= '\n')||LA7_0=='\r'||LA7_0==' ') ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop7;
				}
			}

			match("{%-"); 

			inTag=true; _type=TagStart;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagStartStrip"

	// $ANTLR start "TagEndStrip"
	public final void mTagEndStrip() throws RecognitionException {
		try {
			int _type = TagEndStrip;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:424:15: ( '-%}' ( WhitespaceChar )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:424:17: '-%}' ( WhitespaceChar )*
			{
			match("-%}"); 

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:424:23: ( WhitespaceChar )*
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||LA8_0=='\r'||LA8_0==' ') ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop8;
				}
			}

			inTag=false; _type=TagEnd;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagEndStrip"

	// $ANTLR start "OutStart"
	public final void mOutStart() throws RecognitionException {
		try {
			int _type = OutStart;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:426:10: ( '{{' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:426:12: '{{'
			{
			match("{{"); 

			inTag=true;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutStart"

	// $ANTLR start "OutEnd"
	public final void mOutEnd() throws RecognitionException {
		try {
			int _type = OutEnd;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:427:10: ( '}}' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:427:12: '}}'
			{
			match("}}"); 

			inTag=false;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OutEnd"

	// $ANTLR start "TagStart"
	public final void mTagStart() throws RecognitionException {
		try {
			int _type = TagStart;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:428:10: ( '{%' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:428:12: '{%'
			{
			match("{%"); 

			inTag=true;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagStart"

	// $ANTLR start "TagEnd"
	public final void mTagEnd() throws RecognitionException {
		try {
			int _type = TagEnd;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:429:10: ( '%}' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:429:12: '%}'
			{
			match("%}"); 

			inTag=false;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TagEnd"

	// $ANTLR start "Str"
	public final void mStr() throws RecognitionException {
		try {
			int _type = Str;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:431:5: ({...}? => ( SStr | DStr ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:431:7: {...}? => ( SStr | DStr )
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Str", "inTag");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:431:18: ( SStr | DStr )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0=='\'') ) {
				alt9=1;
			}
			else if ( (LA9_0=='\"') ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:431:19: SStr
					{
					mSStr(); 

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:431:26: DStr
					{
					mDStr(); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Str"

	// $ANTLR start "DotDot"
	public final void mDotDot() throws RecognitionException {
		try {
			int _type = DotDot;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:433:11: ({...}? => '..' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:433:13: {...}? => '..'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "DotDot", "inTag");
			}
			match(".."); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DotDot"

	// $ANTLR start "Dot"
	public final void mDot() throws RecognitionException {
		try {
			int _type = Dot;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:434:11: ({...}? => '.' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:434:13: {...}? => '.'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Dot", "inTag");
			}
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Dot"

	// $ANTLR start "NEq"
	public final void mNEq() throws RecognitionException {
		try {
			int _type = NEq;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:435:11: ({...}? => '!=' | '<>' )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0=='!') && ((inTag))) {
				alt10=1;
			}
			else if ( (LA10_0=='<') ) {
				alt10=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:435:13: {...}? => '!='
					{
					if ( !((inTag)) ) {
						throw new FailedPredicateException(input, "NEq", "inTag");
					}
					match("!="); 

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:435:31: '<>'
					{
					match("<>"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NEq"

	// $ANTLR start "Eq"
	public final void mEq() throws RecognitionException {
		try {
			int _type = Eq;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:436:11: ({...}? => '==' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:436:13: {...}? => '=='
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Eq", "inTag");
			}
			match("=="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Eq"

	// $ANTLR start "EqSign"
	public final void mEqSign() throws RecognitionException {
		try {
			int _type = EqSign;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:437:11: ({...}? => '=' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:437:13: {...}? => '='
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "EqSign", "inTag");
			}
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EqSign"

	// $ANTLR start "GtEq"
	public final void mGtEq() throws RecognitionException {
		try {
			int _type = GtEq;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:438:11: ({...}? => '>=' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:438:13: {...}? => '>='
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "GtEq", "inTag");
			}
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GtEq"

	// $ANTLR start "Gt"
	public final void mGt() throws RecognitionException {
		try {
			int _type = Gt;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:439:11: ({...}? => '>' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:439:13: {...}? => '>'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Gt", "inTag");
			}
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Gt"

	// $ANTLR start "LtEq"
	public final void mLtEq() throws RecognitionException {
		try {
			int _type = LtEq;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:440:11: ({...}? => '<=' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:440:13: {...}? => '<='
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "LtEq", "inTag");
			}
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LtEq"

	// $ANTLR start "Lt"
	public final void mLt() throws RecognitionException {
		try {
			int _type = Lt;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:441:11: ({...}? => '<' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:441:13: {...}? => '<'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Lt", "inTag");
			}
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Lt"

	// $ANTLR start "Minus"
	public final void mMinus() throws RecognitionException {
		try {
			int _type = Minus;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:442:11: ({...}? => '-' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:442:13: {...}? => '-'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Minus", "inTag");
			}
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Minus"

	// $ANTLR start "Pipe"
	public final void mPipe() throws RecognitionException {
		try {
			int _type = Pipe;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:443:11: ({...}? => '|' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:443:13: {...}? => '|'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Pipe", "inTag");
			}
			match('|'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Pipe"

	// $ANTLR start "Col"
	public final void mCol() throws RecognitionException {
		try {
			int _type = Col;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:444:11: ({...}? => ':' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:444:13: {...}? => ':'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Col", "inTag");
			}
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Col"

	// $ANTLR start "Comma"
	public final void mComma() throws RecognitionException {
		try {
			int _type = Comma;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:445:11: ({...}? => ',' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:445:13: {...}? => ','
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Comma", "inTag");
			}
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Comma"

	// $ANTLR start "OPar"
	public final void mOPar() throws RecognitionException {
		try {
			int _type = OPar;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:446:11: ({...}? => '(' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:446:13: {...}? => '('
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "OPar", "inTag");
			}
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OPar"

	// $ANTLR start "CPar"
	public final void mCPar() throws RecognitionException {
		try {
			int _type = CPar;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:447:11: ({...}? => ')' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:447:13: {...}? => ')'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "CPar", "inTag");
			}
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CPar"

	// $ANTLR start "OBr"
	public final void mOBr() throws RecognitionException {
		try {
			int _type = OBr;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:448:11: ({...}? => '[' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:448:13: {...}? => '['
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "OBr", "inTag");
			}
			match('['); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OBr"

	// $ANTLR start "CBr"
	public final void mCBr() throws RecognitionException {
		try {
			int _type = CBr;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:449:11: ({...}? => ']' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:449:13: {...}? => ']'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "CBr", "inTag");
			}
			match(']'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CBr"

	// $ANTLR start "QMark"
	public final void mQMark() throws RecognitionException {
		try {
			int _type = QMark;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:450:11: ({...}? => '?' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:450:13: {...}? => '?'
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "QMark", "inTag");
			}
			match('?'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QMark"

	// $ANTLR start "DoubleNum"
	public final void mDoubleNum() throws RecognitionException {
		try {
			int _type = DoubleNum;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:11: ({...}? => ( '-' )? ( Digit )+ ({...}? => '.' ( Digit )* |) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:13: {...}? => ( '-' )? ( Digit )+ ({...}? => '.' ( Digit )* |)
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "DoubleNum", "inTag");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:24: ( '-' )?
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0=='-') ) {
				alt11=1;
			}
			switch (alt11) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:24: '-'
					{
					match('-'); 
					}
					break;

			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:29: ( Digit )+
			int cnt12=0;
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( ((LA12_0 >= '0' && LA12_0 <= '9')) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt12 >= 1 ) break loop12;
					EarlyExitException eee = new EarlyExitException(12, input);
					throw eee;
				}
				cnt12++;
			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:36: ({...}? => '.' ( Digit )* |)
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0=='.') && ((input.LA(1) == '.' && input.LA(2) != '.'))) {
				alt14=1;
			}

			switch (alt14) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:38: {...}? => '.' ( Digit )*
					{
					if ( !((input.LA(1) == '.' && input.LA(2) != '.')) ) {
						throw new FailedPredicateException(input, "DoubleNum", "input.LA(1) == '.' && input.LA(2) != '.'");
					}
					match('.'); 
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:452:88: ( Digit )*
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( ((LA13_0 >= '0' && LA13_0 <= '9')) ) {
							alt13=1;
						}

						switch (alt13) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop13;
						}
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:453:38: 
					{
					_type = LongNum;
					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DoubleNum"

	// $ANTLR start "LongNum"
	public final void mLongNum() throws RecognitionException {
		try {
			int _type = LongNum;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:455:11: ({...}? => ( '-' )? ( Digit )+ )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:455:13: {...}? => ( '-' )? ( Digit )+
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "LongNum", "inTag");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:455:24: ( '-' )?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0=='-') ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:455:24: '-'
					{
					match('-'); 
					}
					break;

			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:455:29: ( Digit )+
			int cnt16=0;
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( ((LA16_0 >= '0' && LA16_0 <= '9')) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt16 >= 1 ) break loop16;
					EarlyExitException eee = new EarlyExitException(16, input);
					throw eee;
				}
				cnt16++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LongNum"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:456:11: ({...}? => ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:456:13: {...}? => ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "WS", "inTag");
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:456:24: ( ' ' | '\\t' | '\\r' | '\\n' )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( ((LA17_0 >= '\t' && LA17_0 <= '\n')||LA17_0=='\r'||LA17_0==' ') ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt17 >= 1 ) break loop17;
					EarlyExitException eee = new EarlyExitException(17, input);
					throw eee;
				}
				cnt17++;
			}

			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "Id"
	public final void mId() throws RecognitionException {
		try {
			int _type = Id;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:459:2: ({...}? => ( Letter | '_' ) ( Letter | '_' | '-' | Digit )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:459:4: {...}? => ( Letter | '_' ) ( Letter | '_' | '-' | Digit )*
			{
			if ( !((inTag)) ) {
				throw new FailedPredicateException(input, "Id", "inTag");
			}
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:459:30: ( Letter | '_' | '-' | Digit )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( (LA18_0=='-'||(LA18_0 >= '0' && LA18_0 <= '9')||(LA18_0 >= 'A' && LA18_0 <= 'Z')||LA18_0=='_'||(LA18_0 >= 'a' && LA18_0 <= 'z')) ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop18;
				}
			}


			     if(getText().equals("capture"))           _type = CaptureStart;
			     else if(getText().equals("endcapture"))   _type = CaptureEnd;
			     else if(getText().equals("comment"))      _type = CommentStart;
			     else if(getText().equals("endcomment"))   _type = CommentEnd;
			     else if(getText().equals("raw"))        { _type = RawStart; inRaw = true; }
			     else if(getText().equals("endraw"))     { _type = RawEnd; inRaw = false; }
			     else if(getText().equals("if"))           _type = IfStart;
			     else if(getText().equals("elsif"))        _type = Elsif;
			     else if(getText().equals("endif"))        _type = IfEnd;
			     else if(getText().equals("unless"))       _type = UnlessStart;
			     else if(getText().equals("endunless"))    _type = UnlessEnd;
			     else if(getText().equals("else"))         _type = Else;
			     else if(getText().equals("contains"))     _type = Contains;
			     else if(getText().equals("case"))         _type = CaseStart;
			     else if(getText().equals("endcase"))      _type = CaseEnd;
			     else if(getText().equals("when"))         _type = When;
			     else if(getText().equals("cycle"))        _type = Cycle;
			     else if(getText().equals("for"))          _type = ForStart;
			     else if(getText().equals("endfor"))       _type = ForEnd;
			     else if(getText().equals("in"))           _type = In;
			     else if(getText().equals("and"))          _type = And;
			     else if(getText().equals("or"))           _type = Or;
			     else if(getText().equals("tablerow"))     _type = TableStart;
			     else if(getText().equals("endtablerow"))  _type = TableEnd;
			     else if(getText().equals("assign"))       _type = Assign;
			     else if(getText().equals("true"))         _type = True;
			     else if(getText().equals("false"))        _type = False;
			     else if(getText().equals("nil"))          _type = Nil;
			     else if(getText().equals("null"))         _type = Nil;
			     else if(getText().equals("include"))      _type = Include;
			     else if(getText().equals("with"))         _type = With;
			     else if(getText().startsWith("end"))      _type = EndId;
			     else if(getText().equals("break"))        _type = Break;
			     else if(getText().startsWith("continue")) _type = Continue;
			     else if(getText().startsWith("empty"))    _type = Empty;
			   
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Id"

	// $ANTLR start "Other"
	public final void mOther() throws RecognitionException {
		try {
			int _type = Other;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:500:2: ( ({...}? => . )+ | ({...}? => . )+ )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( ((LA21_0 >= '\u0000' && LA21_0 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {
				int LA21_1 = input.LA(2);
				if ( ((!openStripTagAhead() && !inTag && !openTagAhead())) ) {
					alt21=1;
				}
				else if ( ((!inTag && inRaw && !openRawEndTagAhead())) ) {
					alt21=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 21, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			switch (alt21) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:500:4: ({...}? => . )+
					{
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:500:4: ({...}? => . )+
					int cnt19=0;
					loop19:
					while (true) {
						int alt19=2;
						int LA19_0 = input.LA(1);
						if ( ((LA19_0 >= '\u0000' && LA19_0 <= '\uFFFF')) && ((!openStripTagAhead() && !inTag && !openTagAhead()))) {
							alt19=1;
						}

						switch (alt19) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:500:5: {...}? => .
							{
							if ( !((!openStripTagAhead() && !inTag && !openTagAhead())) ) {
								throw new FailedPredicateException(input, "Other", "!openStripTagAhead() && !inTag && !openTagAhead()");
							}
							matchAny(); 
							}
							break;

						default :
							if ( cnt19 >= 1 ) break loop19;
							EarlyExitException eee = new EarlyExitException(19, input);
							throw eee;
						}
						cnt19++;
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:501:4: ({...}? => . )+
					{
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:501:4: ({...}? => . )+
					int cnt20=0;
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( ((LA20_0 >= '\u0000' && LA20_0 <= '\uFFFF')) && ((!inTag && inRaw && !openRawEndTagAhead()))) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:501:5: {...}? => .
							{
							if ( !((!inTag && inRaw && !openRawEndTagAhead())) ) {
								throw new FailedPredicateException(input, "Other", "!inTag && inRaw && !openRawEndTagAhead()");
							}
							matchAny(); 
							}
							break;

						default :
							if ( cnt20 >= 1 ) break loop20;
							EarlyExitException eee = new EarlyExitException(20, input);
							throw eee;
						}
						cnt20++;
					}

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Other"

	// $ANTLR start "NoSpace"
	public final void mNoSpace() throws RecognitionException {
		try {
			int _type = NoSpace;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:505:2: (~ ( ' ' | '\\t' | '\\r' | '\\n' ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
			{
			if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\b')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\u001F')||(input.LA(1) >= '!' && input.LA(1) <= '\uFFFF') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NoSpace"

	// $ANTLR start "WhitespaceChar"
	public final void mWhitespaceChar() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:509:25: ( ' ' | '\\t' | '\\r' | '\\n' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WhitespaceChar"

	// $ANTLR start "Letter"
	public final void mLetter() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:511:17: ( 'a' .. 'z' | 'A' .. 'Z' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Letter"

	// $ANTLR start "Digit"
	public final void mDigit() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:512:17: ( '0' .. '9' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Digit"

	// $ANTLR start "SStr"
	public final void mSStr() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:513:17: ( '\\'' (~ '\\'' )* '\\'' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:513:19: '\\'' (~ '\\'' )* '\\''
			{
			match('\''); 
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:513:24: (~ '\\'' )*
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( ((LA22_0 >= '\u0000' && LA22_0 <= '&')||(LA22_0 >= '(' && LA22_0 <= '\uFFFF')) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop22;
				}
			}

			match('\''); 
			setText(strip(getText(), true));
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SStr"

	// $ANTLR start "DStr"
	public final void mDStr() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:514:17: ( '\"' (~ '\"' )* '\"' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:514:19: '\"' (~ '\"' )* '\"'
			{
			match('\"'); 
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:514:23: (~ '\"' )*
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( ((LA23_0 >= '\u0000' && LA23_0 <= '!')||(LA23_0 >= '#' && LA23_0 <= '\uFFFF')) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop23;
				}
			}

			match('\"'); 
			setText(strip(getText(), false));
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DStr"

	// $ANTLR start "CommentStart"
	public final void mCommentStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:516:23: ( 'CommentStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:516:25: 'CommentStart'
			{
			match("CommentStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CommentStart"

	// $ANTLR start "CommentEnd"
	public final void mCommentEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:517:21: ( 'CommentEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:517:23: 'CommentEnd'
			{
			match("CommentEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CommentEnd"

	// $ANTLR start "RawStart"
	public final void mRawStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:518:19: ( 'RawStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:518:21: 'RawStart'
			{
			match("RawStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RawStart"

	// $ANTLR start "RawEnd"
	public final void mRawEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:519:17: ( 'RawEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:519:19: 'RawEnd'
			{
			match("RawEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RawEnd"

	// $ANTLR start "IfStart"
	public final void mIfStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:520:18: ( 'IfStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:520:20: 'IfStart'
			{
			match("IfStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IfStart"

	// $ANTLR start "IfEnd"
	public final void mIfEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:521:16: ( 'IfEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:521:18: 'IfEnd'
			{
			match("IfEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IfEnd"

	// $ANTLR start "Elsif"
	public final void mElsif() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:522:16: ( 'Elsif' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:522:18: 'Elsif'
			{
			match("Elsif"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Elsif"

	// $ANTLR start "UnlessStart"
	public final void mUnlessStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:523:22: ( 'UnlessStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:523:24: 'UnlessStart'
			{
			match("UnlessStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UnlessStart"

	// $ANTLR start "UnlessEnd"
	public final void mUnlessEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:524:20: ( 'UnlessEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:524:22: 'UnlessEnd'
			{
			match("UnlessEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UnlessEnd"

	// $ANTLR start "Else"
	public final void mElse() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:525:15: ( 'Else' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:525:17: 'Else'
			{
			match("Else"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Else"

	// $ANTLR start "Contains"
	public final void mContains() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:526:19: ( 'contains' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:526:21: 'contains'
			{
			match("contains"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Contains"

	// $ANTLR start "CaseStart"
	public final void mCaseStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:527:20: ( 'CaseStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:527:22: 'CaseStart'
			{
			match("CaseStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CaseStart"

	// $ANTLR start "CaseEnd"
	public final void mCaseEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:528:18: ( 'CaseEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:528:20: 'CaseEnd'
			{
			match("CaseEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CaseEnd"

	// $ANTLR start "When"
	public final void mWhen() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:529:15: ( 'When' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:529:17: 'When'
			{
			match("When"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "When"

	// $ANTLR start "Cycle"
	public final void mCycle() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:530:16: ( 'Cycle' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:530:18: 'Cycle'
			{
			match("Cycle"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Cycle"

	// $ANTLR start "ForStart"
	public final void mForStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:531:19: ( 'ForStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:531:21: 'ForStart'
			{
			match("ForStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ForStart"

	// $ANTLR start "ForEnd"
	public final void mForEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:532:17: ( 'ForEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:532:19: 'ForEnd'
			{
			match("ForEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ForEnd"

	// $ANTLR start "In"
	public final void mIn() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:533:13: ( 'In' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:533:15: 'In'
			{
			match("In"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "In"

	// $ANTLR start "And"
	public final void mAnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:534:14: ( 'And' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:534:16: 'And'
			{
			match("And"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "And"

	// $ANTLR start "Or"
	public final void mOr() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:535:13: ( 'Or' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:535:15: 'Or'
			{
			match("Or"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Or"

	// $ANTLR start "TableStart"
	public final void mTableStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:536:21: ( 'TableStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:536:23: 'TableStart'
			{
			match("TableStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TableStart"

	// $ANTLR start "TableEnd"
	public final void mTableEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:537:19: ( 'TableEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:537:21: 'TableEnd'
			{
			match("TableEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TableEnd"

	// $ANTLR start "Assign"
	public final void mAssign() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:538:17: ( 'Assign' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:538:19: 'Assign'
			{
			match("Assign"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Assign"

	// $ANTLR start "True"
	public final void mTrue() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:539:15: ( 'True' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:539:17: 'True'
			{
			match("True"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "True"

	// $ANTLR start "False"
	public final void mFalse() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:540:16: ( 'False' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:540:18: 'False'
			{
			match("False"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "False"

	// $ANTLR start "Nil"
	public final void mNil() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:541:14: ( 'Nil' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:541:16: 'Nil'
			{
			match("Nil"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Nil"

	// $ANTLR start "Include"
	public final void mInclude() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:542:18: ( 'Include' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:542:20: 'Include'
			{
			match("Include"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Include"

	// $ANTLR start "With"
	public final void mWith() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:543:15: ( 'With' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:543:17: 'With'
			{
			match("With"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "With"

	// $ANTLR start "CaptureStart"
	public final void mCaptureStart() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:544:23: ( 'CaptureStart' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:544:25: 'CaptureStart'
			{
			match("CaptureStart"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CaptureStart"

	// $ANTLR start "CaptureEnd"
	public final void mCaptureEnd() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:545:21: ( 'CaptureEnd' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:545:23: 'CaptureEnd'
			{
			match("CaptureEnd"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CaptureEnd"

	// $ANTLR start "EndId"
	public final void mEndId() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:546:16: ( 'EndId' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:546:18: 'EndId'
			{
			match("EndId"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EndId"

	// $ANTLR start "Break"
	public final void mBreak() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:547:16: ( 'Break' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:547:18: 'Break'
			{
			match("Break"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Break"

	// $ANTLR start "Continue"
	public final void mContinue() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:548:19: ( 'Continue' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:548:21: 'Continue'
			{
			match("Continue"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Continue"

	// $ANTLR start "Empty"
	public final void mEmpty() throws RecognitionException {
		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:549:16: ( 'Empty' )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:549:18: 'Empty'
			{
			match("Empty"); 

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "Empty"

	@Override
	public void mTokens() throws RecognitionException {
		// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:8: ( OutStartDefaultStrip | OutEndDefaultStrip | TagStartDefaultStrip | TagEndDefaultStrip | OutStartStrip | OutEndStrip | TagStartStrip | TagEndStrip | OutStart | OutEnd | TagStart | TagEnd | Str | DotDot | Dot | NEq | Eq | EqSign | GtEq | Gt | LtEq | Lt | Minus | Pipe | Col | Comma | OPar | CPar | OBr | CBr | QMark | DoubleNum | LongNum | WS | Id | Other | NoSpace )
		int alt24=37;
		alt24 = dfa24.predict(input);
		switch (alt24) {
			case 1 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:10: OutStartDefaultStrip
				{
				mOutStartDefaultStrip(); 

				}
				break;
			case 2 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:31: OutEndDefaultStrip
				{
				mOutEndDefaultStrip(); 

				}
				break;
			case 3 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:50: TagStartDefaultStrip
				{
				mTagStartDefaultStrip(); 

				}
				break;
			case 4 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:71: TagEndDefaultStrip
				{
				mTagEndDefaultStrip(); 

				}
				break;
			case 5 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:90: OutStartStrip
				{
				mOutStartStrip(); 

				}
				break;
			case 6 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:104: OutEndStrip
				{
				mOutEndStrip(); 

				}
				break;
			case 7 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:116: TagStartStrip
				{
				mTagStartStrip(); 

				}
				break;
			case 8 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:130: TagEndStrip
				{
				mTagEndStrip(); 

				}
				break;
			case 9 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:142: OutStart
				{
				mOutStart(); 

				}
				break;
			case 10 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:151: OutEnd
				{
				mOutEnd(); 

				}
				break;
			case 11 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:158: TagStart
				{
				mTagStart(); 

				}
				break;
			case 12 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:167: TagEnd
				{
				mTagEnd(); 

				}
				break;
			case 13 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:174: Str
				{
				mStr(); 

				}
				break;
			case 14 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:178: DotDot
				{
				mDotDot(); 

				}
				break;
			case 15 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:185: Dot
				{
				mDot(); 

				}
				break;
			case 16 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:189: NEq
				{
				mNEq(); 

				}
				break;
			case 17 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:193: Eq
				{
				mEq(); 

				}
				break;
			case 18 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:196: EqSign
				{
				mEqSign(); 

				}
				break;
			case 19 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:203: GtEq
				{
				mGtEq(); 

				}
				break;
			case 20 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:208: Gt
				{
				mGt(); 

				}
				break;
			case 21 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:211: LtEq
				{
				mLtEq(); 

				}
				break;
			case 22 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:216: Lt
				{
				mLt(); 

				}
				break;
			case 23 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:219: Minus
				{
				mMinus(); 

				}
				break;
			case 24 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:225: Pipe
				{
				mPipe(); 

				}
				break;
			case 25 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:230: Col
				{
				mCol(); 

				}
				break;
			case 26 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:234: Comma
				{
				mComma(); 

				}
				break;
			case 27 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:240: OPar
				{
				mOPar(); 

				}
				break;
			case 28 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:245: CPar
				{
				mCPar(); 

				}
				break;
			case 29 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:250: OBr
				{
				mOBr(); 

				}
				break;
			case 30 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:254: CBr
				{
				mCBr(); 

				}
				break;
			case 31 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:258: QMark
				{
				mQMark(); 

				}
				break;
			case 32 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:264: DoubleNum
				{
				mDoubleNum(); 

				}
				break;
			case 33 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:274: LongNum
				{
				mLongNum(); 

				}
				break;
			case 34 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:282: WS
				{
				mWS(); 

				}
				break;
			case 35 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:285: Id
				{
				mId(); 

				}
				break;
			case 36 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:288: Other
				{
				mOther(); 

				}
				break;
			case 37 :
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:1:294: NoSpace
				{
				mNoSpace(); 

				}
				break;

		}
	}


	protected DFA24 dfa24 = new DFA24(this);
	static final String DFA24_eotS =
		"\1\uffff\1\31\1\35\1\37\1\41\1\44\1\50\1\53\1\55\1\57\1\62\1\64\1\66\1"+
		"\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\100\1\102\1\103\1\32\2\uffff"+
		"\1\110\1\112\1\uffff\1\115\1\uffff\1\117\1\uffff\2\32\1\uffff\1\123\1"+
		"\32\1\124\1\uffff\1\32\1\125\1\uffff\1\126\1\uffff\1\130\1\uffff\1\131"+
		"\1\132\1\uffff\1\134\1\uffff\1\136\11\uffff\1\151\1\uffff\1\154\2\uffff"+
		"\1\156\1\157\1\uffff\1\160\1\uffff\1\163\2\uffff\1\166\1\uffff\1\171\1"+
		"\uffff\1\175\1\177\26\uffff\1\u0086\23\uffff\1\u0089\1\uffff\1\u008b\16"+
		"\uffff";
	static final String DFA24_eofS =
		"\u008d\uffff";
	static final String DFA24_minS =
		"\30\0\1\45\1\0\1\uffff\7\0\2\175\42\0\1\uffff\4\0\1\uffff\6\0\1\uffff"+
		"\4\0\1\uffff\3\0\1\uffff\1\0\1\uffff\1\0\11\uffff\2\0\2\uffff\1\0\1\uffff"+
		"\3\0\2\uffff\1\0\2\uffff\1\0\2\uffff\1\0\2\uffff\4\0\6\uffff\1\0\2\uffff"+
		"\1\0\1\uffff\1\0\1\uffff";
	static final String DFA24_maxS =
		"\30\uffff\1\173\1\0\1\uffff\2\uffff\1\0\1\uffff\1\0\1\uffff\1\0\2\175"+
		"\1\0\3\uffff\1\0\2\uffff\1\0\1\uffff\1\0\1\uffff\1\0\2\uffff\1\0\1\uffff"+
		"\1\0\1\uffff\11\0\1\uffff\1\0\1\uffff\2\0\2\uffff\1\uffff\1\uffff\1\0"+
		"\1\uffff\1\0\1\uffff\1\uffff\1\0\1\uffff\1\0\2\uffff\1\uffff\4\0\1\uffff"+
		"\3\0\1\uffff\1\0\1\uffff\1\0\11\uffff\1\uffff\1\0\2\uffff\1\0\1\uffff"+
		"\3\0\2\uffff\1\0\2\uffff\1\0\2\uffff\1\0\2\uffff\1\uffff\1\0\1\uffff\1"+
		"\0\6\uffff\1\0\2\uffff\1\0\1\uffff\1\0\1\uffff";
	static final String DFA24_acceptS =
		"\32\uffff\1\44\53\uffff\1\42\4\uffff\1\45\6\uffff\1\27\4\uffff\1\17\3"+
		"\uffff\1\26\1\uffff\1\22\1\uffff\1\24\1\30\1\31\1\32\1\33\1\34\1\35\1"+
		"\36\1\37\2\uffff\1\40\1\41\1\uffff\1\43\3\uffff\1\1\1\11\1\uffff\1\3\1"+
		"\13\1\uffff\1\2\1\12\1\uffff\1\4\1\14\4\uffff\1\15\1\16\1\20\1\25\1\21"+
		"\1\23\1\uffff\1\5\1\7\1\uffff\1\6\1\uffff\1\10";
	static final String DFA24_specialS =
		"\1\125\1\102\1\7\1\74\1\110\1\134\1\107\1\122\1\121\1\126\1\116\1\10\1"+
		"\62\1\14\1\37\1\57\1\63\1\70\1\72\1\75\1\101\1\77\1\67\1\112\1\103\1\30"+
		"\1\uffff\1\65\1\123\1\40\1\16\1\36\1\52\1\41\1\56\1\66\1\24\1\6\1\61\1"+
		"\111\1\42\1\76\1\120\1\43\1\34\1\15\1\104\1\44\1\105\1\106\1\22\1\71\1"+
		"\17\1\100\1\20\1\27\1\31\1\32\1\35\1\45\1\50\1\51\1\53\1\117\1\55\1\23"+
		"\1\54\1\47\1\115\1\12\1\uffff\1\73\1\127\1\114\1\132\1\uffff\1\140\1\130"+
		"\1\113\1\133\1\21\1\131\1\uffff\1\46\1\0\1\1\1\2\1\uffff\1\3\1\4\1\13"+
		"\1\uffff\1\5\1\uffff\1\11\11\uffff\1\60\1\25\2\uffff\1\33\1\uffff\1\135"+
		"\1\137\1\142\2\uffff\1\145\2\uffff\1\136\2\uffff\1\141\2\uffff\1\64\1"+
		"\143\1\124\1\146\6\uffff\1\26\2\uffff\1\144\1\uffff\1\147\1\uffff}>";
	static final String[] DFA24_transitionS = {
			"\11\27\2\1\2\27\1\1\22\27\1\1\1\11\1\7\2\27\1\4\1\27\1\6\1\20\1\21\2"+
			"\27\1\17\1\5\1\10\1\27\12\25\1\16\1\27\1\12\1\13\1\14\1\24\1\27\32\26"+
			"\1\22\1\27\1\23\1\27\1\26\1\27\32\26\1\2\1\15\1\3\uff82\27",
			"\11\32\2\1\2\32\1\1\22\32\1\1\132\32\1\30\uff84\32",
			"\45\32\1\34\125\32\1\33\uff84\32",
			"\175\32\1\36\uff82\32",
			"\175\32\1\40\uff82\32",
			"\45\32\1\43\12\32\12\45\103\32\1\42\uff82\32",
			"\47\46\1\47\uffd8\46",
			"\42\51\1\52\uffdd\51",
			"\56\32\1\54\uffd1\32",
			"\75\32\1\56\uffc2\32",
			"\75\32\1\61\1\60\uffc1\32",
			"\75\32\1\63\uffc2\32",
			"\75\32\1\65\uffc2\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\0\32",
			"\56\32\1\77\1\32\12\45\uffc6\32",
			"\55\32\1\101\2\32\12\101\7\32\32\101\4\32\1\101\1\32\32\101\uff85\32",
			"\0\32",
			"\1\105\125\uffff\1\104",
			"\1\uffff",
			"",
			"\55\32\1\107\uffd2\32",
			"\55\32\1\111\uffd2\32",
			"\1\uffff",
			"\11\32\2\114\2\32\1\114\22\32\1\114\uffdf\32",
			"\1\uffff",
			"\11\32\2\116\2\32\1\116\22\32\1\116\uffdf\32",
			"\1\uffff",
			"\1\120",
			"\1\121",
			"\1\uffff",
			"\56\32\1\77\1\32\12\45\uffc6\32",
			"\47\46\1\47\uffd8\46",
			"\0\32",
			"\1\uffff",
			"\42\51\1\52\uffdd\51",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\60\32\12\150\uffc6\32",
			"\1\uffff",
			"\55\32\1\101\2\32\12\101\7\32\32\101\4\32\1\101\1\32\32\101\uff85\32",
			"\1\uffff",
			"\1\uffff",
			"\55\32\1\107\uffd2\32",
			"\55\32\1\111\uffd2\32",
			"",
			"\0\32",
			"\1\uffff",
			"\0\32",
			"\1\uffff",
			"",
			"\11\32\2\114\2\32\1\114\22\32\1\114\uffdf\32",
			"\1\uffff",
			"\11\32\2\116\2\32\1\116\22\32\1\116\uffdf\32",
			"\1\uffff",
			"\11\32\2\174\2\32\1\174\22\32\1\174\uffdf\32",
			"\11\32\2\176\2\32\1\176\22\32\1\176\uffdf\32",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\60\32\12\150\uffc6\32",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"\11\32\2\174\2\32\1\174\22\32\1\174\uffdf\32",
			"\1\uffff",
			"\11\32\2\176\2\32\1\176\22\32\1\176\uffdf\32",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			""
	};

	static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
	static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
	static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
	static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
	static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
	static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
	static final short[][] DFA24_transition;

	static {
		int numStates = DFA24_transitionS.length;
		DFA24_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
		}
	}

	protected class DFA24 extends DFA {

		public DFA24(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 24;
			this.eot = DFA24_eot;
			this.eof = DFA24_eof;
			this.min = DFA24_min;
			this.max = DFA24_max;
			this.accept = DFA24_accept;
			this.special = DFA24_special;
			this.transition = DFA24_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( OutStartDefaultStrip | OutEndDefaultStrip | TagStartDefaultStrip | TagEndDefaultStrip | OutStartStrip | OutEndStrip | TagStartStrip | TagEndStrip | OutStart | OutEnd | TagStart | TagEnd | Str | DotDot | Dot | NEq | Eq | EqSign | GtEq | Gt | LtEq | Lt | Minus | Pipe | Col | Comma | OPar | CPar | OBr | CBr | QMark | DoubleNum | LongNum | WS | Id | Other | NoSpace );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA24_84 = input.LA(1);
						 
						int index24_84 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 128;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_84);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA24_85 = input.LA(1);
						 
						int index24_85 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 128;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_85);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA24_86 = input.LA(1);
						 
						int index24_86 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 129;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_86);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA24_88 = input.LA(1);
						 
						int index24_88 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 130;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_88);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA24_89 = input.LA(1);
						 
						int index24_89 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 130;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_89);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA24_92 = input.LA(1);
						 
						int index24_92 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 132;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_92);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA24_37 = input.LA(1);
						 
						int index24_37 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_37=='.') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 63;}
						else if ( ((LA24_37 >= '0' && LA24_37 <= '9')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 37;}
						else if ( ((LA24_37 >= '\u0000' && LA24_37 <= '-')||LA24_37=='/'||(LA24_37 >= ':' && LA24_37 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 83;
						 
						input.seek(index24_37);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA24_2 = input.LA(1);
						 
						int index24_2 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_2=='{') ) {s = 27;}
						else if ( (LA24_2=='%') ) {s = 28;}
						else if ( ((LA24_2 >= '\u0000' && LA24_2 <= '$')||(LA24_2 >= '&' && LA24_2 <= 'z')||(LA24_2 >= '|' && LA24_2 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 29;
						 
						input.seek(index24_2);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA24_11 = input.LA(1);
						 
						int index24_11 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_11=='=') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 51;}
						else if ( ((LA24_11 >= '\u0000' && LA24_11 <= '<')||(LA24_11 >= '>' && LA24_11 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 52;
						 
						input.seek(index24_11);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA24_94 = input.LA(1);
						 
						int index24_94 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 133;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_94);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA24_69 = input.LA(1);
						 
						int index24_69 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_69=='-') ) {s = 73;}
						else if ( ((LA24_69 >= '\u0000' && LA24_69 <= ',')||(LA24_69 >= '.' && LA24_69 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 111;
						 
						input.seek(index24_69);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA24_90 = input.LA(1);
						 
						int index24_90 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 131;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_90);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA24_13 = input.LA(1);
						 
						int index24_13 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_13 >= '\u0000' && LA24_13 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 55;
						 
						input.seek(index24_13);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA24_45 = input.LA(1);
						 
						int index24_45 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 87;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_45);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA24_30 = input.LA(1);
						 
						int index24_30 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_30 >= '\t' && LA24_30 <= '\n')||LA24_30=='\r'||LA24_30==' ') && (((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 76;}
						else if ( ((LA24_30 >= '\u0000' && LA24_30 <= '\b')||(LA24_30 >= '\u000B' && LA24_30 <= '\f')||(LA24_30 >= '\u000E' && LA24_30 <= '\u001F')||(LA24_30 >= '!' && LA24_30 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 77;
						 
						input.seek(index24_30);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA24_52 = input.LA(1);
						 
						int index24_52 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 93;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_52);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA24_54 = input.LA(1);
						 
						int index24_54 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 95;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_54);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA24_80 = input.LA(1);
						 
						int index24_80 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_80 >= '\t' && LA24_80 <= '\n')||LA24_80=='\r'||LA24_80==' ') ) {s = 124;}
						else if ( ((LA24_80 >= '\u0000' && LA24_80 <= '\b')||(LA24_80 >= '\u000B' && LA24_80 <= '\f')||(LA24_80 >= '\u000E' && LA24_80 <= '\u001F')||(LA24_80 >= '!' && LA24_80 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 125;
						 
						input.seek(index24_80);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA24_50 = input.LA(1);
						 
						int index24_50 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 91;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_50);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA24_65 = input.LA(1);
						 
						int index24_65 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_65=='-'||(LA24_65 >= '0' && LA24_65 <= '9')||(LA24_65 >= 'A' && LA24_65 <= 'Z')||LA24_65=='_'||(LA24_65 >= 'a' && LA24_65 <= 'z')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 65;}
						else if ( ((LA24_65 >= '\u0000' && LA24_65 <= ',')||(LA24_65 >= '.' && LA24_65 <= '/')||(LA24_65 >= ':' && LA24_65 <= '@')||(LA24_65 >= '[' && LA24_65 <= '^')||LA24_65=='`'||(LA24_65 >= '{' && LA24_65 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 108;
						 
						input.seek(index24_65);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA24_36 = input.LA(1);
						 
						int index24_36 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 82;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_36);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA24_105 = input.LA(1);
						 
						int index24_105 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 106;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_105);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA24_134 = input.LA(1);
						 
						int index24_134 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 106;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_134);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA24_55 = input.LA(1);
						 
						int index24_55 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 96;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_55);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA24_25 = input.LA(1);
						 
						int index24_25 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 70;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_25);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA24_56 = input.LA(1);
						 
						int index24_56 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 97;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_56);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA24_57 = input.LA(1);
						 
						int index24_57 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 98;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_57);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA24_108 = input.LA(1);
						 
						int index24_108 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 109;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_108);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA24_44 = input.LA(1);
						 
						int index24_44 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_44 >= '\u0000' && LA24_44 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 86;
						 
						input.seek(index24_44);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA24_58 = input.LA(1);
						 
						int index24_58 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 99;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_58);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA24_31 = input.LA(1);
						 
						int index24_31 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_31);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA24_14 = input.LA(1);
						 
						int index24_14 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_14 >= '\u0000' && LA24_14 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 56;
						 
						input.seek(index24_14);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA24_29 = input.LA(1);
						 
						int index24_29 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_29);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA24_33 = input.LA(1);
						 
						int index24_33 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_33);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA24_40 = input.LA(1);
						 
						int index24_40 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_40);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA24_43 = input.LA(1);
						 
						int index24_43 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_43);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA24_47 = input.LA(1);
						 
						int index24_47 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_47);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA24_59 = input.LA(1);
						 
						int index24_59 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 100;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_59);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA24_83 = input.LA(1);
						 
						int index24_83 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 106;}
						else if ( ((inTag)) ) {s = 107;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_83);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA24_67 = input.LA(1);
						 
						int index24_67 = input.index();
						input.rewind();
						s = -1;
						if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_67);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA24_60 = input.LA(1);
						 
						int index24_60 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 101;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_60);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA24_61 = input.LA(1);
						 
						int index24_61 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 102;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_61);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA24_32 = input.LA(1);
						 
						int index24_32 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_32 >= '\t' && LA24_32 <= '\n')||LA24_32=='\r'||LA24_32==' ') && (((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 78;}
						else if ( ((LA24_32 >= '\u0000' && LA24_32 <= '\b')||(LA24_32 >= '\u000B' && LA24_32 <= '\f')||(LA24_32 >= '\u000E' && LA24_32 <= '\u001F')||(LA24_32 >= '!' && LA24_32 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 79;
						 
						input.seek(index24_32);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA24_62 = input.LA(1);
						 
						int index24_62 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 103;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_62);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA24_66 = input.LA(1);
						 
						int index24_66 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 109;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_66);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA24_64 = input.LA(1);
						 
						int index24_64 = input.index();
						input.rewind();
						s = -1;
						if ( ((inTag)) ) {s = 106;}
						else if ( ((inTag)) ) {s = 107;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						else if ( (true) ) {s = 75;}
						 
						input.seek(index24_64);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA24_34 = input.LA(1);
						 
						int index24_34 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_34=='}') ) {s = 80;}
						else s = 26;
						 
						input.seek(index24_34);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA24_15 = input.LA(1);
						 
						int index24_15 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_15 >= '\u0000' && LA24_15 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 57;
						 
						input.seek(index24_15);
						if ( s>=0 ) return s;
						break;

					case 48 : 
						int LA24_104 = input.LA(1);
						 
						int index24_104 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_104 >= '0' && LA24_104 <= '9')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 104;}
						else if ( ((LA24_104 >= '\u0000' && LA24_104 <= '/')||(LA24_104 >= ':' && LA24_104 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 134;
						 
						input.seek(index24_104);
						if ( s>=0 ) return s;
						break;

					case 49 : 
						int LA24_38 = input.LA(1);
						 
						int index24_38 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_38=='\'') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 39;}
						else if ( ((LA24_38 >= '\u0000' && LA24_38 <= '&')||(LA24_38 >= '(' && LA24_38 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 38;}
						else s = 26;
						 
						input.seek(index24_38);
						if ( s>=0 ) return s;
						break;

					case 50 : 
						int LA24_12 = input.LA(1);
						 
						int index24_12 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_12=='=') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 53;}
						else if ( ((LA24_12 >= '\u0000' && LA24_12 <= '<')||(LA24_12 >= '>' && LA24_12 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 54;
						 
						input.seek(index24_12);
						if ( s>=0 ) return s;
						break;

					case 51 : 
						int LA24_16 = input.LA(1);
						 
						int index24_16 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_16 >= '\u0000' && LA24_16 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 58;
						 
						input.seek(index24_16);
						if ( s>=0 ) return s;
						break;

					case 52 : 
						int LA24_124 = input.LA(1);
						 
						int index24_124 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_124 >= '\t' && LA24_124 <= '\n')||LA24_124=='\r'||LA24_124==' ') ) {s = 124;}
						else if ( ((LA24_124 >= '\u0000' && LA24_124 <= '\b')||(LA24_124 >= '\u000B' && LA24_124 <= '\f')||(LA24_124 >= '\u000E' && LA24_124 <= '\u001F')||(LA24_124 >= '!' && LA24_124 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 137;
						 
						input.seek(index24_124);
						if ( s>=0 ) return s;
						break;

					case 53 : 
						int LA24_27 = input.LA(1);
						 
						int index24_27 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_27=='-') ) {s = 71;}
						else if ( ((LA24_27 >= '\u0000' && LA24_27 <= ',')||(LA24_27 >= '.' && LA24_27 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 72;
						 
						input.seek(index24_27);
						if ( s>=0 ) return s;
						break;

					case 54 : 
						int LA24_35 = input.LA(1);
						 
						int index24_35 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_35=='}') ) {s = 81;}
						else s = 26;
						 
						input.seek(index24_35);
						if ( s>=0 ) return s;
						break;

					case 55 : 
						int LA24_22 = input.LA(1);
						 
						int index24_22 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_22=='-'||(LA24_22 >= '0' && LA24_22 <= '9')||(LA24_22 >= 'A' && LA24_22 <= 'Z')||LA24_22=='_'||(LA24_22 >= 'a' && LA24_22 <= 'z')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 65;}
						else if ( ((LA24_22 >= '\u0000' && LA24_22 <= ',')||(LA24_22 >= '.' && LA24_22 <= '/')||(LA24_22 >= ':' && LA24_22 <= '@')||(LA24_22 >= '[' && LA24_22 <= '^')||LA24_22=='`'||(LA24_22 >= '{' && LA24_22 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 66;
						 
						input.seek(index24_22);
						if ( s>=0 ) return s;
						break;

					case 56 : 
						int LA24_17 = input.LA(1);
						 
						int index24_17 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_17 >= '\u0000' && LA24_17 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 59;
						 
						input.seek(index24_17);
						if ( s>=0 ) return s;
						break;

					case 57 : 
						int LA24_51 = input.LA(1);
						 
						int index24_51 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_51 >= '\u0000' && LA24_51 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 92;
						 
						input.seek(index24_51);
						if ( s>=0 ) return s;
						break;

					case 58 : 
						int LA24_18 = input.LA(1);
						 
						int index24_18 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_18 >= '\u0000' && LA24_18 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 60;
						 
						input.seek(index24_18);
						if ( s>=0 ) return s;
						break;

					case 59 : 
						int LA24_71 = input.LA(1);
						 
						int index24_71 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_71 >= '\u0000' && LA24_71 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 112;
						 
						input.seek(index24_71);
						if ( s>=0 ) return s;
						break;

					case 60 : 
						int LA24_3 = input.LA(1);
						 
						int index24_3 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_3=='}') ) {s = 30;}
						else if ( ((LA24_3 >= '\u0000' && LA24_3 <= '|')||(LA24_3 >= '~' && LA24_3 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 31;
						 
						input.seek(index24_3);
						if ( s>=0 ) return s;
						break;

					case 61 : 
						int LA24_19 = input.LA(1);
						 
						int index24_19 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_19 >= '\u0000' && LA24_19 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 61;
						 
						input.seek(index24_19);
						if ( s>=0 ) return s;
						break;

					case 62 : 
						int LA24_41 = input.LA(1);
						 
						int index24_41 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_41=='\"') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 42;}
						else if ( ((LA24_41 >= '\u0000' && LA24_41 <= '!')||(LA24_41 >= '#' && LA24_41 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 41;}
						else s = 26;
						 
						input.seek(index24_41);
						if ( s>=0 ) return s;
						break;

					case 63 : 
						int LA24_21 = input.LA(1);
						 
						int index24_21 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_21=='.') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 63;}
						else if ( ((LA24_21 >= '0' && LA24_21 <= '9')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 37;}
						else if ( ((LA24_21 >= '\u0000' && LA24_21 <= '-')||LA24_21=='/'||(LA24_21 >= ':' && LA24_21 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 64;
						 
						input.seek(index24_21);
						if ( s>=0 ) return s;
						break;

					case 64 : 
						int LA24_53 = input.LA(1);
						 
						int index24_53 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_53 >= '\u0000' && LA24_53 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 94;
						 
						input.seek(index24_53);
						if ( s>=0 ) return s;
						break;

					case 65 : 
						int LA24_20 = input.LA(1);
						 
						int index24_20 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_20 >= '\u0000' && LA24_20 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 62;
						 
						input.seek(index24_20);
						if ( s>=0 ) return s;
						break;

					case 66 : 
						int LA24_1 = input.LA(1);
						 
						int index24_1 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_1=='{') ) {s = 24;}
						else if ( ((LA24_1 >= '\t' && LA24_1 <= '\n')||LA24_1=='\r'||LA24_1==' ') ) {s = 1;}
						else if ( ((LA24_1 >= '\u0000' && LA24_1 <= '\b')||(LA24_1 >= '\u000B' && LA24_1 <= '\f')||(LA24_1 >= '\u000E' && LA24_1 <= '\u001F')||(LA24_1 >= '!' && LA24_1 <= 'z')||(LA24_1 >= '|' && LA24_1 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 25;
						 
						input.seek(index24_1);
						if ( s>=0 ) return s;
						break;

					case 67 : 
						int LA24_24 = input.LA(1);
						 
						int index24_24 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_24=='{') ) {s = 68;}
						else if ( (LA24_24=='%') ) {s = 69;}
						else s = 26;
						 
						input.seek(index24_24);
						if ( s>=0 ) return s;
						break;

					case 68 : 
						int LA24_46 = input.LA(1);
						 
						int index24_46 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_46 >= '\u0000' && LA24_46 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 88;
						 
						input.seek(index24_46);
						if ( s>=0 ) return s;
						break;

					case 69 : 
						int LA24_48 = input.LA(1);
						 
						int index24_48 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_48 >= '\u0000' && LA24_48 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 89;
						 
						input.seek(index24_48);
						if ( s>=0 ) return s;
						break;

					case 70 : 
						int LA24_49 = input.LA(1);
						 
						int index24_49 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_49 >= '\u0000' && LA24_49 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 90;
						 
						input.seek(index24_49);
						if ( s>=0 ) return s;
						break;

					case 71 : 
						int LA24_6 = input.LA(1);
						 
						int index24_6 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_6 >= '\u0000' && LA24_6 <= '&')||(LA24_6 >= '(' && LA24_6 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 38;}
						else if ( (LA24_6=='\'') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 39;}
						else s = 40;
						 
						input.seek(index24_6);
						if ( s>=0 ) return s;
						break;

					case 72 : 
						int LA24_4 = input.LA(1);
						 
						int index24_4 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_4=='}') ) {s = 32;}
						else if ( ((LA24_4 >= '\u0000' && LA24_4 <= '|')||(LA24_4 >= '~' && LA24_4 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 33;
						 
						input.seek(index24_4);
						if ( s>=0 ) return s;
						break;

					case 73 : 
						int LA24_39 = input.LA(1);
						 
						int index24_39 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_39 >= '\u0000' && LA24_39 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 84;
						 
						input.seek(index24_39);
						if ( s>=0 ) return s;
						break;

					case 74 : 
						int LA24_23 = input.LA(1);
						 
						int index24_23 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_23 >= '\u0000' && LA24_23 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 67;
						 
						input.seek(index24_23);
						if ( s>=0 ) return s;
						break;

					case 75 : 
						int LA24_78 = input.LA(1);
						 
						int index24_78 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_78 >= '\t' && LA24_78 <= '\n')||LA24_78=='\r'||LA24_78==' ') && (((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 78;}
						else if ( ((LA24_78 >= '\u0000' && LA24_78 <= '\b')||(LA24_78 >= '\u000B' && LA24_78 <= '\f')||(LA24_78 >= '\u000E' && LA24_78 <= '\u001F')||(LA24_78 >= '!' && LA24_78 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 121;
						 
						input.seek(index24_78);
						if ( s>=0 ) return s;
						break;

					case 76 : 
						int LA24_73 = input.LA(1);
						 
						int index24_73 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_73 >= '\u0000' && LA24_73 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 115;
						 
						input.seek(index24_73);
						if ( s>=0 ) return s;
						break;

					case 77 : 
						int LA24_68 = input.LA(1);
						 
						int index24_68 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_68=='-') ) {s = 71;}
						else if ( ((LA24_68 >= '\u0000' && LA24_68 <= ',')||(LA24_68 >= '.' && LA24_68 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 110;
						 
						input.seek(index24_68);
						if ( s>=0 ) return s;
						break;

					case 78 : 
						int LA24_10 = input.LA(1);
						 
						int index24_10 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_10=='>') ) {s = 48;}
						else if ( (LA24_10=='=') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 49;}
						else if ( ((LA24_10 >= '\u0000' && LA24_10 <= '<')||(LA24_10 >= '?' && LA24_10 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 50;
						 
						input.seek(index24_10);
						if ( s>=0 ) return s;
						break;

					case 79 : 
						int LA24_63 = input.LA(1);
						 
						int index24_63 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_63 >= '0' && LA24_63 <= '9')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 104;}
						else if ( ((LA24_63 >= '\u0000' && LA24_63 <= '/')||(LA24_63 >= ':' && LA24_63 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 105;
						 
						input.seek(index24_63);
						if ( s>=0 ) return s;
						break;

					case 80 : 
						int LA24_42 = input.LA(1);
						 
						int index24_42 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_42 >= '\u0000' && LA24_42 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 85;
						 
						input.seek(index24_42);
						if ( s>=0 ) return s;
						break;

					case 81 : 
						int LA24_8 = input.LA(1);
						 
						int index24_8 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_8=='.') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 44;}
						else if ( ((LA24_8 >= '\u0000' && LA24_8 <= '-')||(LA24_8 >= '/' && LA24_8 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 45;
						 
						input.seek(index24_8);
						if ( s>=0 ) return s;
						break;

					case 82 : 
						int LA24_7 = input.LA(1);
						 
						int index24_7 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_7 >= '\u0000' && LA24_7 <= '!')||(LA24_7 >= '#' && LA24_7 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 41;}
						else if ( (LA24_7=='\"') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 42;}
						else s = 43;
						 
						input.seek(index24_7);
						if ( s>=0 ) return s;
						break;

					case 83 : 
						int LA24_28 = input.LA(1);
						 
						int index24_28 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_28=='-') ) {s = 73;}
						else if ( ((LA24_28 >= '\u0000' && LA24_28 <= ',')||(LA24_28 >= '.' && LA24_28 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 74;
						 
						input.seek(index24_28);
						if ( s>=0 ) return s;
						break;

					case 84 : 
						int LA24_126 = input.LA(1);
						 
						int index24_126 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_126 >= '\t' && LA24_126 <= '\n')||LA24_126=='\r'||LA24_126==' ') ) {s = 126;}
						else if ( ((LA24_126 >= '\u0000' && LA24_126 <= '\b')||(LA24_126 >= '\u000B' && LA24_126 <= '\f')||(LA24_126 >= '\u000E' && LA24_126 <= '\u001F')||(LA24_126 >= '!' && LA24_126 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 139;
						 
						input.seek(index24_126);
						if ( s>=0 ) return s;
						break;

					case 85 : 
						int LA24_0 = input.LA(1);
						s = -1;
						if ( ((LA24_0 >= '\t' && LA24_0 <= '\n')||LA24_0=='\r'||LA24_0==' ') ) {s = 1;}
						else if ( (LA24_0=='{') ) {s = 2;}
						else if ( (LA24_0=='}') ) {s = 3;}
						else if ( (LA24_0=='%') ) {s = 4;}
						else if ( (LA24_0=='-') ) {s = 5;}
						else if ( (LA24_0=='\'') ) {s = 6;}
						else if ( (LA24_0=='\"') ) {s = 7;}
						else if ( (LA24_0=='.') ) {s = 8;}
						else if ( (LA24_0=='!') ) {s = 9;}
						else if ( (LA24_0=='<') ) {s = 10;}
						else if ( (LA24_0=='=') ) {s = 11;}
						else if ( (LA24_0=='>') ) {s = 12;}
						else if ( (LA24_0=='|') ) {s = 13;}
						else if ( (LA24_0==':') ) {s = 14;}
						else if ( (LA24_0==',') ) {s = 15;}
						else if ( (LA24_0=='(') ) {s = 16;}
						else if ( (LA24_0==')') ) {s = 17;}
						else if ( (LA24_0=='[') ) {s = 18;}
						else if ( (LA24_0==']') ) {s = 19;}
						else if ( (LA24_0=='?') ) {s = 20;}
						else if ( ((LA24_0 >= '0' && LA24_0 <= '9')) ) {s = 21;}
						else if ( ((LA24_0 >= 'A' && LA24_0 <= 'Z')||LA24_0=='_'||(LA24_0 >= 'a' && LA24_0 <= 'z')) ) {s = 22;}
						else if ( ((LA24_0 >= '\u0000' && LA24_0 <= '\b')||(LA24_0 >= '\u000B' && LA24_0 <= '\f')||(LA24_0 >= '\u000E' && LA24_0 <= '\u001F')||(LA24_0 >= '#' && LA24_0 <= '$')||LA24_0=='&'||(LA24_0 >= '*' && LA24_0 <= '+')||LA24_0=='/'||LA24_0==';'||LA24_0=='@'||LA24_0=='\\'||LA24_0=='^'||LA24_0=='`'||(LA24_0 >= '~' && LA24_0 <= '\uFFFF')) ) {s = 23;}
						if ( s>=0 ) return s;
						break;

					case 86 : 
						int LA24_9 = input.LA(1);
						 
						int index24_9 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_9=='=') && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 46;}
						else if ( ((LA24_9 >= '\u0000' && LA24_9 <= '<')||(LA24_9 >= '>' && LA24_9 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 47;
						 
						input.seek(index24_9);
						if ( s>=0 ) return s;
						break;

					case 87 : 
						int LA24_72 = input.LA(1);
						 
						int index24_72 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 113;}
						else if ( (!((((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 114;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_72);
						if ( s>=0 ) return s;
						break;

					case 88 : 
						int LA24_77 = input.LA(1);
						 
						int index24_77 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 119;}
						else if ( (!((((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 120;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_77);
						if ( s>=0 ) return s;
						break;

					case 89 : 
						int LA24_81 = input.LA(1);
						 
						int index24_81 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_81 >= '\t' && LA24_81 <= '\n')||LA24_81=='\r'||LA24_81==' ') ) {s = 126;}
						else if ( ((LA24_81 >= '\u0000' && LA24_81 <= '\b')||(LA24_81 >= '\u000B' && LA24_81 <= '\f')||(LA24_81 >= '\u000E' && LA24_81 <= '\u001F')||(LA24_81 >= '!' && LA24_81 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 127;
						 
						input.seek(index24_81);
						if ( s>=0 ) return s;
						break;

					case 90 : 
						int LA24_74 = input.LA(1);
						 
						int index24_74 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 116;}
						else if ( (!((((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 117;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_74);
						if ( s>=0 ) return s;
						break;

					case 91 : 
						int LA24_79 = input.LA(1);
						 
						int index24_79 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 122;}
						else if ( (!((((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 123;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_79);
						if ( s>=0 ) return s;
						break;

					case 92 : 
						int LA24_5 = input.LA(1);
						 
						int index24_5 = input.index();
						input.rewind();
						s = -1;
						if ( (LA24_5=='}') ) {s = 34;}
						else if ( (LA24_5=='%') ) {s = 35;}
						else if ( ((LA24_5 >= '0' && LA24_5 <= '9')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())||(inTag)))) {s = 37;}
						else if ( ((LA24_5 >= '\u0000' && LA24_5 <= '$')||(LA24_5 >= '&' && LA24_5 <= '/')||(LA24_5 >= ':' && LA24_5 <= '|')||(LA24_5 >= '~' && LA24_5 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 36;
						 
						input.seek(index24_5);
						if ( s>=0 ) return s;
						break;

					case 93 : 
						int LA24_110 = input.LA(1);
						 
						int index24_110 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 113;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_110);
						if ( s>=0 ) return s;
						break;

					case 94 : 
						int LA24_118 = input.LA(1);
						 
						int index24_118 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 119;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_118);
						if ( s>=0 ) return s;
						break;

					case 95 : 
						int LA24_111 = input.LA(1);
						 
						int index24_111 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 116;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_111);
						if ( s>=0 ) return s;
						break;

					case 96 : 
						int LA24_76 = input.LA(1);
						 
						int index24_76 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA24_76 >= '\t' && LA24_76 <= '\n')||LA24_76=='\r'||LA24_76==' ') && (((stripSpacesAroundTags)||(!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 76;}
						else if ( ((LA24_76 >= '\u0000' && LA24_76 <= '\b')||(LA24_76 >= '\u000B' && LA24_76 <= '\f')||(LA24_76 >= '\u000E' && LA24_76 <= '\u001F')||(LA24_76 >= '!' && LA24_76 <= '\uFFFF')) && (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead())))) {s = 26;}
						else s = 118;
						 
						input.seek(index24_76);
						if ( s>=0 ) return s;
						break;

					case 97 : 
						int LA24_121 = input.LA(1);
						 
						int index24_121 = input.index();
						input.rewind();
						s = -1;
						if ( ((stripSpacesAroundTags)) ) {s = 122;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_121);
						if ( s>=0 ) return s;
						break;

					case 98 : 
						int LA24_112 = input.LA(1);
						 
						int index24_112 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 135;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_112);
						if ( s>=0 ) return s;
						break;

					case 99 : 
						int LA24_125 = input.LA(1);
						 
						int index24_125 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 138;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_125);
						if ( s>=0 ) return s;
						break;

					case 100 : 
						int LA24_137 = input.LA(1);
						 
						int index24_137 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 138;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_137);
						if ( s>=0 ) return s;
						break;

					case 101 : 
						int LA24_115 = input.LA(1);
						 
						int index24_115 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 136;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_115);
						if ( s>=0 ) return s;
						break;

					case 102 : 
						int LA24_127 = input.LA(1);
						 
						int index24_127 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 140;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_127);
						if ( s>=0 ) return s;
						break;

					case 103 : 
						int LA24_139 = input.LA(1);
						 
						int index24_139 = input.index();
						input.rewind();
						s = -1;
						if ( (!((((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))))) ) {s = 140;}
						else if ( (((!inTag && inRaw && !openRawEndTagAhead())||(!openStripTagAhead() && !inTag && !openTagAhead()))) ) {s = 26;}
						 
						input.seek(index24_139);
						if ( s>=0 ) return s;
						break;
			}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 24, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
