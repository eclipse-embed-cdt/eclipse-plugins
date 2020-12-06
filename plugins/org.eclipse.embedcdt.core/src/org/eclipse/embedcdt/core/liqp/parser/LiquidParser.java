// $ANTLR 3.5.2 org.eclipse.embedcdt.core.liqp/parser/Liquid.g 2020-12-06 19:17:12

  package org.eclipse.embedcdt.core.liqp.parser;


import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;


@SuppressWarnings("all")
public class LiquidParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGNMENT", "ATTRIBUTES", "And", 
		"Assign", "BLOCK", "Break", "CAPTURE", "CASE", "CBr", "COMMENT", "CPar", 
		"CUSTOM_TAG", "CUSTOM_TAG_BLOCK", "CYCLE", "CaptureEnd", "CaptureStart", 
		"CaseEnd", "CaseStart", "Col", "Comma", "CommentEnd", "CommentStart", 
		"Contains", "Continue", "Cycle", "DStr", "Digit", "Dot", "DotDot", "DoubleNum", 
		"ELSE", "ELSIF", "Else", "Elsif", "Empty", "EndId", "Eq", "EqSign", "FILTER", 
		"FILTERS", "FOR_ARRAY", "FOR_BLOCK", "FOR_RANGE", "False", "ForEnd", "ForStart", 
		"GROUP", "Gt", "GtEq", "HASH", "IF", "INCLUDE", "INDEX", "Id", "IfEnd", 
		"IfStart", "In", "Include", "LOOKUP", "Letter", "LongNum", "Lt", "LtEq", 
		"Minus", "NEq", "NO_SPACE", "Nil", "NoSpace", "OBr", "OPar", "OUTPUT", 
		"Or", "Other", "OutEnd", "OutEndDefaultStrip", "OutEndStrip", "OutStart", 
		"OutStartDefaultStrip", "OutStartStrip", "PARAMS", "PLAIN", "Pipe", "QMark", 
		"RAW", "RawEnd", "RawStart", "SStr", "Str", "TABLE", "TableEnd", "TableStart", 
		"TagEnd", "TagEndDefaultStrip", "TagEndStrip", "TagStart", "TagStartDefaultStrip", 
		"TagStartStrip", "True", "UNLESS", "UnlessEnd", "UnlessStart", "WHEN", 
		"WITH", "WS", "When", "WhitespaceChar", "With"
	};
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

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public LiquidParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public LiquidParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return LiquidParser.tokenNames; }
	@Override public String getGrammarFileName() { return "org.eclipse.embedcdt.core.liqp/parser/Liquid.g"; }



	  private Flavor flavor = Flavor.LIQUID;

	  public LiquidParser(Flavor flavor, TokenStream input) {
	    this(input, new RecognizerSharedState());
	    this.flavor = flavor;
	  }

	  @Override
	  public void reportError(RecognitionException e) {
	    throw new RuntimeException(e); 
	  }


	public static class parse_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "parse"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:152:1: parse : block EOF -> block ;
	public final LiquidParser.parse_return parse() throws RecognitionException {
		LiquidParser.parse_return retval = new LiquidParser.parse_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token EOF2=null;
		ParserRuleReturnScope block1 =null;

		CommonTree EOF2_tree=null;
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:153:2: ( block EOF -> block )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:153:4: block EOF
			{
			pushFollow(FOLLOW_block_in_parse231);
			block1=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block1.getTree());
			EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_parse233); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EOF.add(EOF2);

			// AST REWRITE
			// elements: block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 153:14: -> block
			{
				adaptor.addChild(root_0, stream_block.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "parse"


	public static class block_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "block"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:157:1: block : ( options {greedy=true; } : atom )* -> ^( BLOCK ( atom )* ) ;
	public final LiquidParser.block_return block() throws RecognitionException {
		LiquidParser.block_return retval = new LiquidParser.block_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope atom3 =null;

		RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:2: ( ( options {greedy=true; } : atom )* -> ^( BLOCK ( atom )* ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:4: ( options {greedy=true; } : atom )*
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:4: ( options {greedy=true; } : atom )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==TagStart) ) {
					int LA1_2 = input.LA(2);
					if ( (LA1_2==Assign||LA1_2==Break||LA1_2==CaptureStart||LA1_2==CaseStart||LA1_2==CommentStart||(LA1_2 >= Continue && LA1_2 <= Cycle)||LA1_2==ForStart||LA1_2==Id||LA1_2==IfStart||LA1_2==Include||LA1_2==RawStart||LA1_2==TableStart||LA1_2==UnlessStart) ) {
						alt1=1;
					}

				}
				else if ( (LA1_0==Other||LA1_0==OutStart) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:28: atom
					{
					pushFollow(FOLLOW_atom_in_block261);
					atom3=atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_atom.add(atom3.getTree());
					}
					break;

				default :
					break loop1;
				}
			}

			// AST REWRITE
			// elements: atom
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 158:35: -> ^( BLOCK ( atom )* )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:38: ^( BLOCK ( atom )* )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:158:46: ( atom )*
				while ( stream_atom.hasNext() ) {
					adaptor.addChild(root_1, stream_atom.nextTree());
				}
				stream_atom.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "block"


	public static class atom_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "atom"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:161:1: atom : ( tag | output | assignment | Other -> PLAIN[$Other.text] );
	public final LiquidParser.atom_return atom() throws RecognitionException {
		LiquidParser.atom_return retval = new LiquidParser.atom_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Other7=null;
		ParserRuleReturnScope tag4 =null;
		ParserRuleReturnScope output5 =null;
		ParserRuleReturnScope assignment6 =null;

		CommonTree Other7_tree=null;
		RewriteRuleTokenStream stream_Other=new RewriteRuleTokenStream(adaptor,"token Other");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:162:2: ( tag | output | assignment | Other -> PLAIN[$Other.text] )
			int alt2=4;
			switch ( input.LA(1) ) {
			case TagStart:
				{
				int LA2_1 = input.LA(2);
				if ( (LA2_1==Break||LA2_1==CaptureStart||LA2_1==CaseStart||LA2_1==CommentStart||(LA2_1 >= Continue && LA2_1 <= Cycle)||LA2_1==ForStart||LA2_1==Id||LA2_1==IfStart||LA2_1==Include||LA2_1==RawStart||LA2_1==TableStart||LA2_1==UnlessStart) ) {
					alt2=1;
				}
				else if ( (LA2_1==Assign) ) {
					alt2=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 2, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case OutStart:
				{
				alt2=2;
				}
				break;
			case Other:
				{
				alt2=4;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:162:4: tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_tag_in_atom283);
					tag4=tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, tag4.getTree());

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:163:4: output
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_output_in_atom288);
					output5=output();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, output5.getTree());

					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:164:4: assignment
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_assignment_in_atom293);
					assignment6=assignment();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, assignment6.getTree());

					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:165:4: Other
					{
					Other7=(Token)match(input,Other,FOLLOW_Other_in_atom298); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Other.add(Other7);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 165:10: -> PLAIN[$Other.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(PLAIN, (Other7!=null?Other7.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"


	public static class tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:168:1: tag : ( custom_tag | raw_tag | comment_tag | if_tag | unless_tag | case_tag | cycle_tag | for_tag | table_tag | capture_tag | include_tag | break_tag | continue_tag );
	public final LiquidParser.tag_return tag() throws RecognitionException {
		LiquidParser.tag_return retval = new LiquidParser.tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope custom_tag8 =null;
		ParserRuleReturnScope raw_tag9 =null;
		ParserRuleReturnScope comment_tag10 =null;
		ParserRuleReturnScope if_tag11 =null;
		ParserRuleReturnScope unless_tag12 =null;
		ParserRuleReturnScope case_tag13 =null;
		ParserRuleReturnScope cycle_tag14 =null;
		ParserRuleReturnScope for_tag15 =null;
		ParserRuleReturnScope table_tag16 =null;
		ParserRuleReturnScope capture_tag17 =null;
		ParserRuleReturnScope include_tag18 =null;
		ParserRuleReturnScope break_tag19 =null;
		ParserRuleReturnScope continue_tag20 =null;


		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:169:2: ( custom_tag | raw_tag | comment_tag | if_tag | unless_tag | case_tag | cycle_tag | for_tag | table_tag | capture_tag | include_tag | break_tag | continue_tag )
			int alt3=13;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==TagStart) ) {
				switch ( input.LA(2) ) {
				case Id:
					{
					alt3=1;
					}
					break;
				case RawStart:
					{
					alt3=2;
					}
					break;
				case CommentStart:
					{
					alt3=3;
					}
					break;
				case IfStart:
					{
					alt3=4;
					}
					break;
				case UnlessStart:
					{
					alt3=5;
					}
					break;
				case CaseStart:
					{
					alt3=6;
					}
					break;
				case Cycle:
					{
					alt3=7;
					}
					break;
				case ForStart:
					{
					alt3=8;
					}
					break;
				case TableStart:
					{
					alt3=9;
					}
					break;
				case CaptureStart:
					{
					alt3=10;
					}
					break;
				case Include:
					{
					alt3=11;
					}
					break;
				case Break:
					{
					alt3=12;
					}
					break;
				case Continue:
					{
					alt3=13;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 3, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:169:4: custom_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_custom_tag_in_tag314);
					custom_tag8=custom_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, custom_tag8.getTree());

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:170:4: raw_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_raw_tag_in_tag319);
					raw_tag9=raw_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, raw_tag9.getTree());

					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:171:4: comment_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_comment_tag_in_tag324);
					comment_tag10=comment_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, comment_tag10.getTree());

					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:172:4: if_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_if_tag_in_tag329);
					if_tag11=if_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, if_tag11.getTree());

					}
					break;
				case 5 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:173:4: unless_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_unless_tag_in_tag334);
					unless_tag12=unless_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, unless_tag12.getTree());

					}
					break;
				case 6 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:174:4: case_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_case_tag_in_tag339);
					case_tag13=case_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, case_tag13.getTree());

					}
					break;
				case 7 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:175:4: cycle_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_cycle_tag_in_tag344);
					cycle_tag14=cycle_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cycle_tag14.getTree());

					}
					break;
				case 8 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:176:4: for_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_for_tag_in_tag349);
					for_tag15=for_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, for_tag15.getTree());

					}
					break;
				case 9 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:177:4: table_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_table_tag_in_tag354);
					table_tag16=table_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, table_tag16.getTree());

					}
					break;
				case 10 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:178:4: capture_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_capture_tag_in_tag359);
					capture_tag17=capture_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, capture_tag17.getTree());

					}
					break;
				case 11 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:179:4: include_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_include_tag_in_tag364);
					include_tag18=include_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, include_tag18.getTree());

					}
					break;
				case 12 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:180:4: break_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_break_tag_in_tag369);
					break_tag19=break_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, break_tag19.getTree());

					}
					break;
				case 13 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:181:4: continue_tag
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_continue_tag_in_tag374);
					continue_tag20=continue_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, continue_tag20.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "tag"


	public static class custom_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "custom_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:184:1: custom_tag : ( TagStart Id ( custom_tag_parameters )? TagEnd -> ^( CUSTOM_TAG Id ( custom_tag_parameters )? ) ) ( ( custom_tag_block )=> custom_tag_block -> ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block ) )? ;
	public final LiquidParser.custom_tag_return custom_tag() throws RecognitionException {
		LiquidParser.custom_tag_return retval = new LiquidParser.custom_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart21=null;
		Token Id22=null;
		Token TagEnd24=null;
		ParserRuleReturnScope custom_tag_parameters23 =null;
		ParserRuleReturnScope custom_tag_block25 =null;

		CommonTree TagStart21_tree=null;
		CommonTree Id22_tree=null;
		CommonTree TagEnd24_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_custom_tag_parameters=new RewriteRuleSubtreeStream(adaptor,"rule custom_tag_parameters");
		RewriteRuleSubtreeStream stream_custom_tag_block=new RewriteRuleSubtreeStream(adaptor,"rule custom_tag_block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:2: ( ( TagStart Id ( custom_tag_parameters )? TagEnd -> ^( CUSTOM_TAG Id ( custom_tag_parameters )? ) ) ( ( custom_tag_block )=> custom_tag_block -> ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block ) )? )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:4: ( TagStart Id ( custom_tag_parameters )? TagEnd -> ^( CUSTOM_TAG Id ( custom_tag_parameters )? ) ) ( ( custom_tag_block )=> custom_tag_block -> ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block ) )?
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:4: ( TagStart Id ( custom_tag_parameters )? TagEnd -> ^( CUSTOM_TAG Id ( custom_tag_parameters )? ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:5: TagStart Id ( custom_tag_parameters )? TagEnd
			{
			TagStart21=(Token)match(input,TagStart,FOLLOW_TagStart_in_custom_tag386); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart21);

			Id22=(Token)match(input,Id,FOLLOW_Id_in_custom_tag388); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id22);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:17: ( custom_tag_parameters )?
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( ((LA4_0 >= ASSIGNMENT && LA4_0 <= TableStart)||(LA4_0 >= TagEndDefaultStrip && LA4_0 <= With)) ) {
				alt4=1;
			}
			switch (alt4) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:17: custom_tag_parameters
					{
					pushFollow(FOLLOW_custom_tag_parameters_in_custom_tag390);
					custom_tag_parameters23=custom_tag_parameters();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_custom_tag_parameters.add(custom_tag_parameters23.getTree());
					}
					break;

			}

			TagEnd24=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_custom_tag393); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd24);

			// AST REWRITE
			// elements: Id, custom_tag_parameters
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 185:47: -> ^( CUSTOM_TAG Id ( custom_tag_parameters )? )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:50: ^( CUSTOM_TAG Id ( custom_tag_parameters )? )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CUSTOM_TAG, "CUSTOM_TAG"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:185:66: ( custom_tag_parameters )?
				if ( stream_custom_tag_parameters.hasNext() ) {
					adaptor.addChild(root_1, stream_custom_tag_parameters.nextTree());
				}
				stream_custom_tag_parameters.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:4: ( ( custom_tag_block )=> custom_tag_block -> ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block ) )?
			int alt5=2;
			switch ( input.LA(1) ) {
				case TagStart:
					{
					int LA5_1 = input.LA(2);
					if ( (synpred1_Liquid()) ) {
						alt5=1;
					}
					}
					break;
				case OutStart:
					{
					int LA5_2 = input.LA(2);
					if ( (synpred1_Liquid()) ) {
						alt5=1;
					}
					}
					break;
				case Other:
					{
					int LA5_3 = input.LA(2);
					if ( (synpred1_Liquid()) ) {
						alt5=1;
					}
					}
					break;
			}
			switch (alt5) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:5: ( custom_tag_block )=> custom_tag_block
					{
					pushFollow(FOLLOW_custom_tag_block_in_custom_tag416);
					custom_tag_block25=custom_tag_block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_custom_tag_block.add(custom_tag_block25.getTree());
					// AST REWRITE
					// elements: custom_tag_block, custom_tag_parameters, Id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 186:47: -> ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:50: ^( CUSTOM_TAG_BLOCK Id ( custom_tag_parameters )? custom_tag_block )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CUSTOM_TAG_BLOCK, "CUSTOM_TAG_BLOCK"), root_1);
						adaptor.addChild(root_1, stream_Id.nextNode());
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:72: ( custom_tag_parameters )?
						if ( stream_custom_tag_parameters.hasNext() ) {
							adaptor.addChild(root_1, stream_custom_tag_parameters.nextTree());
						}
						stream_custom_tag_parameters.reset();

						adaptor.addChild(root_1, stream_custom_tag_block.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "custom_tag"


	public static class custom_tag_block_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "custom_tag_block"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:189:1: custom_tag_block : ( options {greedy=false; } : atom )* TagStart EndId TagEnd -> ^( BLOCK ( atom )* ) ;
	public final LiquidParser.custom_tag_block_return custom_tag_block() throws RecognitionException {
		LiquidParser.custom_tag_block_return retval = new LiquidParser.custom_tag_block_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart27=null;
		Token EndId28=null;
		Token TagEnd29=null;
		ParserRuleReturnScope atom26 =null;

		CommonTree TagStart27_tree=null;
		CommonTree EndId28_tree=null;
		CommonTree TagEnd29_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_EndId=new RewriteRuleTokenStream(adaptor,"token EndId");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:2: ( ( options {greedy=false; } : atom )* TagStart EndId TagEnd -> ^( BLOCK ( atom )* ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:4: ( options {greedy=false; } : atom )* TagStart EndId TagEnd
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:4: ( options {greedy=false; } : atom )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==TagStart) ) {
					int LA6_1 = input.LA(2);
					if ( (LA6_1==EndId) ) {
						alt6=2;
					}
					else if ( (LA6_1==Assign||LA6_1==Break||LA6_1==CaptureStart||LA6_1==CaseStart||LA6_1==CommentStart||(LA6_1 >= Continue && LA6_1 <= Cycle)||LA6_1==ForStart||LA6_1==Id||LA6_1==IfStart||LA6_1==Include||LA6_1==RawStart||LA6_1==TableStart||LA6_1==UnlessStart) ) {
						alt6=1;
					}

				}
				else if ( (LA6_0==Other||LA6_0==OutStart) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:29: atom
					{
					pushFollow(FOLLOW_atom_in_custom_tag_block455);
					atom26=atom();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_atom.add(atom26.getTree());
					}
					break;

				default :
					break loop6;
				}
			}

			TagStart27=(Token)match(input,TagStart,FOLLOW_TagStart_in_custom_tag_block459); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart27);

			EndId28=(Token)match(input,EndId,FOLLOW_EndId_in_custom_tag_block461); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EndId.add(EndId28);

			TagEnd29=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_custom_tag_block463); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd29);

			// AST REWRITE
			// elements: atom
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 190:58: -> ^( BLOCK ( atom )* )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:61: ^( BLOCK ( atom )* )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BLOCK, "BLOCK"), root_1);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:190:69: ( atom )*
				while ( stream_atom.hasNext() ) {
					adaptor.addChild(root_1, stream_atom.nextTree());
				}
				stream_atom.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "custom_tag_block"


	public static class raw_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "raw_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:193:1: raw_tag : TagStart RawStart TagEnd raw_body TagStart RawEnd TagEnd -> raw_body ;
	public final LiquidParser.raw_tag_return raw_tag() throws RecognitionException {
		LiquidParser.raw_tag_return retval = new LiquidParser.raw_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart30=null;
		Token RawStart31=null;
		Token TagEnd32=null;
		Token TagStart34=null;
		Token RawEnd35=null;
		Token TagEnd36=null;
		ParserRuleReturnScope raw_body33 =null;

		CommonTree TagStart30_tree=null;
		CommonTree RawStart31_tree=null;
		CommonTree TagEnd32_tree=null;
		CommonTree TagStart34_tree=null;
		CommonTree RawEnd35_tree=null;
		CommonTree TagEnd36_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_RawEnd=new RewriteRuleTokenStream(adaptor,"token RawEnd");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_RawStart=new RewriteRuleTokenStream(adaptor,"token RawStart");
		RewriteRuleSubtreeStream stream_raw_body=new RewriteRuleSubtreeStream(adaptor,"rule raw_body");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:194:2: ( TagStart RawStart TagEnd raw_body TagStart RawEnd TagEnd -> raw_body )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:194:4: TagStart RawStart TagEnd raw_body TagStart RawEnd TagEnd
			{
			TagStart30=(Token)match(input,TagStart,FOLLOW_TagStart_in_raw_tag483); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart30);

			RawStart31=(Token)match(input,RawStart,FOLLOW_RawStart_in_raw_tag485); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RawStart.add(RawStart31);

			TagEnd32=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_raw_tag487); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd32);

			pushFollow(FOLLOW_raw_body_in_raw_tag489);
			raw_body33=raw_body();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_raw_body.add(raw_body33.getTree());
			TagStart34=(Token)match(input,TagStart,FOLLOW_TagStart_in_raw_tag491); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart34);

			RawEnd35=(Token)match(input,RawEnd,FOLLOW_RawEnd_in_raw_tag493); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RawEnd.add(RawEnd35);

			TagEnd36=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_raw_tag495); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd36);

			// AST REWRITE
			// elements: raw_body
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 194:61: -> raw_body
			{
				adaptor.addChild(root_0, stream_raw_body.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "raw_tag"


	public static class raw_body_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "raw_body"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:197:1: raw_body : other_than_tag_start -> RAW[$other_than_tag_start.text] ;
	public final LiquidParser.raw_body_return raw_body() throws RecognitionException {
		LiquidParser.raw_body_return retval = new LiquidParser.raw_body_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope other_than_tag_start37 =null;

		RewriteRuleSubtreeStream stream_other_than_tag_start=new RewriteRuleSubtreeStream(adaptor,"rule other_than_tag_start");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:198:2: ( other_than_tag_start -> RAW[$other_than_tag_start.text] )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:198:4: other_than_tag_start
			{
			pushFollow(FOLLOW_other_than_tag_start_in_raw_body510);
			other_than_tag_start37=other_than_tag_start();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_other_than_tag_start.add(other_than_tag_start37.getTree());
			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 198:25: -> RAW[$other_than_tag_start.text]
			{
				adaptor.addChild(root_0, (CommonTree)adaptor.create(RAW, (other_than_tag_start37!=null?input.toString(other_than_tag_start37.start,other_than_tag_start37.stop):null)));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "raw_body"


	public static class comment_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "comment_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:201:1: comment_tag : TagStart CommentStart TagEnd comment_body TagStart CommentEnd TagEnd -> comment_body ;
	public final LiquidParser.comment_tag_return comment_tag() throws RecognitionException {
		LiquidParser.comment_tag_return retval = new LiquidParser.comment_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart38=null;
		Token CommentStart39=null;
		Token TagEnd40=null;
		Token TagStart42=null;
		Token CommentEnd43=null;
		Token TagEnd44=null;
		ParserRuleReturnScope comment_body41 =null;

		CommonTree TagStart38_tree=null;
		CommonTree CommentStart39_tree=null;
		CommonTree TagEnd40_tree=null;
		CommonTree TagStart42_tree=null;
		CommonTree CommentEnd43_tree=null;
		CommonTree TagEnd44_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_CommentEnd=new RewriteRuleTokenStream(adaptor,"token CommentEnd");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_CommentStart=new RewriteRuleTokenStream(adaptor,"token CommentStart");
		RewriteRuleSubtreeStream stream_comment_body=new RewriteRuleSubtreeStream(adaptor,"rule comment_body");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:202:2: ( TagStart CommentStart TagEnd comment_body TagStart CommentEnd TagEnd -> comment_body )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:202:4: TagStart CommentStart TagEnd comment_body TagStart CommentEnd TagEnd
			{
			TagStart38=(Token)match(input,TagStart,FOLLOW_TagStart_in_comment_tag526); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart38);

			CommentStart39=(Token)match(input,CommentStart,FOLLOW_CommentStart_in_comment_tag528); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CommentStart.add(CommentStart39);

			TagEnd40=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_comment_tag530); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd40);

			pushFollow(FOLLOW_comment_body_in_comment_tag532);
			comment_body41=comment_body();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_comment_body.add(comment_body41.getTree());
			TagStart42=(Token)match(input,TagStart,FOLLOW_TagStart_in_comment_tag534); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart42);

			CommentEnd43=(Token)match(input,CommentEnd,FOLLOW_CommentEnd_in_comment_tag536); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CommentEnd.add(CommentEnd43);

			TagEnd44=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_comment_tag538); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd44);

			// AST REWRITE
			// elements: comment_body
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 202:73: -> comment_body
			{
				adaptor.addChild(root_0, stream_comment_body.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "comment_tag"


	public static class comment_body_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "comment_body"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:205:1: comment_body : other_than_tag_start -> COMMENT[$other_than_tag_start.text] ;
	public final LiquidParser.comment_body_return comment_body() throws RecognitionException {
		LiquidParser.comment_body_return retval = new LiquidParser.comment_body_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope other_than_tag_start45 =null;

		RewriteRuleSubtreeStream stream_other_than_tag_start=new RewriteRuleSubtreeStream(adaptor,"rule other_than_tag_start");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:206:2: ( other_than_tag_start -> COMMENT[$other_than_tag_start.text] )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:206:4: other_than_tag_start
			{
			pushFollow(FOLLOW_other_than_tag_start_in_comment_body553);
			other_than_tag_start45=other_than_tag_start();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_other_than_tag_start.add(other_than_tag_start45.getTree());
			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 206:25: -> COMMENT[$other_than_tag_start.text]
			{
				adaptor.addChild(root_0, (CommonTree)adaptor.create(COMMENT, (other_than_tag_start45!=null?input.toString(other_than_tag_start45.start,other_than_tag_start45.stop):null)));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "comment_body"


	public static class other_than_tag_start_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "other_than_tag_start"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:209:1: other_than_tag_start : (~ TagStart )* ;
	public final LiquidParser.other_than_tag_start_return other_than_tag_start() throws RecognitionException {
		LiquidParser.other_than_tag_start_return retval = new LiquidParser.other_than_tag_start_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set46=null;

		CommonTree set46_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:210:2: ( (~ TagStart )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:210:4: (~ TagStart )*
			{
			root_0 = (CommonTree)adaptor.nil();


			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:210:4: (~ TagStart )*
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( ((LA7_0 >= ASSIGNMENT && LA7_0 <= TagEndStrip)||(LA7_0 >= TagStartDefaultStrip && LA7_0 <= With)) ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					set46=input.LT(1);
					if ( (input.LA(1) >= ASSIGNMENT && input.LA(1) <= TagEndStrip)||(input.LA(1) >= TagStartDefaultStrip && input.LA(1) <= With) ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set46));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					break loop7;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "other_than_tag_start"


	public static class if_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "if_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:213:1: if_tag : TagStart IfStart expr TagEnd block ( elsif_tag )* ( else_tag )? TagStart IfEnd TagEnd -> ^( IF expr block ( elsif_tag )* ^( ELSE ( else_tag )? ) ) ;
	public final LiquidParser.if_tag_return if_tag() throws RecognitionException {
		LiquidParser.if_tag_return retval = new LiquidParser.if_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart47=null;
		Token IfStart48=null;
		Token TagEnd50=null;
		Token TagStart54=null;
		Token IfEnd55=null;
		Token TagEnd56=null;
		ParserRuleReturnScope expr49 =null;
		ParserRuleReturnScope block51 =null;
		ParserRuleReturnScope elsif_tag52 =null;
		ParserRuleReturnScope else_tag53 =null;

		CommonTree TagStart47_tree=null;
		CommonTree IfStart48_tree=null;
		CommonTree TagEnd50_tree=null;
		CommonTree TagStart54_tree=null;
		CommonTree IfEnd55_tree=null;
		CommonTree TagEnd56_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_IfStart=new RewriteRuleTokenStream(adaptor,"token IfStart");
		RewriteRuleTokenStream stream_IfEnd=new RewriteRuleTokenStream(adaptor,"token IfEnd");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_else_tag=new RewriteRuleSubtreeStream(adaptor,"rule else_tag");
		RewriteRuleSubtreeStream stream_elsif_tag=new RewriteRuleSubtreeStream(adaptor,"rule elsif_tag");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:2: ( TagStart IfStart expr TagEnd block ( elsif_tag )* ( else_tag )? TagStart IfEnd TagEnd -> ^( IF expr block ( elsif_tag )* ^( ELSE ( else_tag )? ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:4: TagStart IfStart expr TagEnd block ( elsif_tag )* ( else_tag )? TagStart IfEnd TagEnd
			{
			TagStart47=(Token)match(input,TagStart,FOLLOW_TagStart_in_if_tag582); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart47);

			IfStart48=(Token)match(input,IfStart,FOLLOW_IfStart_in_if_tag584); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_IfStart.add(IfStart48);

			pushFollow(FOLLOW_expr_in_if_tag586);
			expr49=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr49.getTree());
			TagEnd50=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_if_tag588); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd50);

			pushFollow(FOLLOW_block_in_if_tag590);
			block51=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block51.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:39: ( elsif_tag )*
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==TagStart) ) {
					int LA8_1 = input.LA(2);
					if ( (LA8_1==Elsif) ) {
						alt8=1;
					}

				}

				switch (alt8) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:39: elsif_tag
					{
					pushFollow(FOLLOW_elsif_tag_in_if_tag592);
					elsif_tag52=elsif_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_elsif_tag.add(elsif_tag52.getTree());
					}
					break;

				default :
					break loop8;
				}
			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:50: ( else_tag )?
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==TagStart) ) {
				int LA9_1 = input.LA(2);
				if ( (LA9_1==Else) ) {
					alt9=1;
				}
			}
			switch (alt9) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:50: else_tag
					{
					pushFollow(FOLLOW_else_tag_in_if_tag595);
					else_tag53=else_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_else_tag.add(else_tag53.getTree());
					}
					break;

			}

			TagStart54=(Token)match(input,TagStart,FOLLOW_TagStart_in_if_tag598); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart54);

			IfEnd55=(Token)match(input,IfEnd,FOLLOW_IfEnd_in_if_tag600); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_IfEnd.add(IfEnd55);

			TagEnd56=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_if_tag602); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd56);

			// AST REWRITE
			// elements: expr, block, elsif_tag, else_tag
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 214:82: -> ^( IF expr block ( elsif_tag )* ^( ELSE ( else_tag )? ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:85: ^( IF expr block ( elsif_tag )* ^( ELSE ( else_tag )? ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IF, "IF"), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_1, stream_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:101: ( elsif_tag )*
				while ( stream_elsif_tag.hasNext() ) {
					adaptor.addChild(root_1, stream_elsif_tag.nextTree());
				}
				stream_elsif_tag.reset();

				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:112: ^( ELSE ( else_tag )? )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ELSE, "ELSE"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:214:119: ( else_tag )?
				if ( stream_else_tag.hasNext() ) {
					adaptor.addChild(root_2, stream_else_tag.nextTree());
				}
				stream_else_tag.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "if_tag"


	public static class elsif_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "elsif_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:217:1: elsif_tag : TagStart Elsif expr TagEnd block -> ^( ELSIF expr block ) ;
	public final LiquidParser.elsif_tag_return elsif_tag() throws RecognitionException {
		LiquidParser.elsif_tag_return retval = new LiquidParser.elsif_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart57=null;
		Token Elsif58=null;
		Token TagEnd60=null;
		ParserRuleReturnScope expr59 =null;
		ParserRuleReturnScope block61 =null;

		CommonTree TagStart57_tree=null;
		CommonTree Elsif58_tree=null;
		CommonTree TagEnd60_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Elsif=new RewriteRuleTokenStream(adaptor,"token Elsif");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:218:2: ( TagStart Elsif expr TagEnd block -> ^( ELSIF expr block ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:218:4: TagStart Elsif expr TagEnd block
			{
			TagStart57=(Token)match(input,TagStart,FOLLOW_TagStart_in_elsif_tag633); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart57);

			Elsif58=(Token)match(input,Elsif,FOLLOW_Elsif_in_elsif_tag635); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Elsif.add(Elsif58);

			pushFollow(FOLLOW_expr_in_elsif_tag637);
			expr59=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr59.getTree());
			TagEnd60=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_elsif_tag639); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd60);

			pushFollow(FOLLOW_block_in_elsif_tag641);
			block61=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block61.getTree());
			// AST REWRITE
			// elements: block, expr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 218:37: -> ^( ELSIF expr block )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:218:40: ^( ELSIF expr block )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ELSIF, "ELSIF"), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_1, stream_block.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "elsif_tag"


	public static class else_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "else_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:221:1: else_tag : TagStart Else TagEnd block -> block ;
	public final LiquidParser.else_tag_return else_tag() throws RecognitionException {
		LiquidParser.else_tag_return retval = new LiquidParser.else_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart62=null;
		Token Else63=null;
		Token TagEnd64=null;
		ParserRuleReturnScope block65 =null;

		CommonTree TagStart62_tree=null;
		CommonTree Else63_tree=null;
		CommonTree TagEnd64_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Else=new RewriteRuleTokenStream(adaptor,"token Else");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:222:2: ( TagStart Else TagEnd block -> block )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:222:4: TagStart Else TagEnd block
			{
			TagStart62=(Token)match(input,TagStart,FOLLOW_TagStart_in_else_tag662); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart62);

			Else63=(Token)match(input,Else,FOLLOW_Else_in_else_tag664); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Else.add(Else63);

			TagEnd64=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_else_tag666); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd64);

			pushFollow(FOLLOW_block_in_else_tag668);
			block65=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block65.getTree());
			// AST REWRITE
			// elements: block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 222:31: -> block
			{
				adaptor.addChild(root_0, stream_block.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "else_tag"


	public static class unless_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "unless_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:225:1: unless_tag : TagStart UnlessStart expr TagEnd block ( else_tag )? TagStart UnlessEnd TagEnd -> ^( UNLESS expr block ^( ELSE ( else_tag )? ) ) ;
	public final LiquidParser.unless_tag_return unless_tag() throws RecognitionException {
		LiquidParser.unless_tag_return retval = new LiquidParser.unless_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart66=null;
		Token UnlessStart67=null;
		Token TagEnd69=null;
		Token TagStart72=null;
		Token UnlessEnd73=null;
		Token TagEnd74=null;
		ParserRuleReturnScope expr68 =null;
		ParserRuleReturnScope block70 =null;
		ParserRuleReturnScope else_tag71 =null;

		CommonTree TagStart66_tree=null;
		CommonTree UnlessStart67_tree=null;
		CommonTree TagEnd69_tree=null;
		CommonTree TagStart72_tree=null;
		CommonTree UnlessEnd73_tree=null;
		CommonTree TagEnd74_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_UnlessStart=new RewriteRuleTokenStream(adaptor,"token UnlessStart");
		RewriteRuleTokenStream stream_UnlessEnd=new RewriteRuleTokenStream(adaptor,"token UnlessEnd");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_else_tag=new RewriteRuleSubtreeStream(adaptor,"rule else_tag");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:2: ( TagStart UnlessStart expr TagEnd block ( else_tag )? TagStart UnlessEnd TagEnd -> ^( UNLESS expr block ^( ELSE ( else_tag )? ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:4: TagStart UnlessStart expr TagEnd block ( else_tag )? TagStart UnlessEnd TagEnd
			{
			TagStart66=(Token)match(input,TagStart,FOLLOW_TagStart_in_unless_tag683); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart66);

			UnlessStart67=(Token)match(input,UnlessStart,FOLLOW_UnlessStart_in_unless_tag685); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_UnlessStart.add(UnlessStart67);

			pushFollow(FOLLOW_expr_in_unless_tag687);
			expr68=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr68.getTree());
			TagEnd69=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_unless_tag689); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd69);

			pushFollow(FOLLOW_block_in_unless_tag691);
			block70=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block70.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:43: ( else_tag )?
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0==TagStart) ) {
				int LA10_1 = input.LA(2);
				if ( (LA10_1==Else) ) {
					alt10=1;
				}
			}
			switch (alt10) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:43: else_tag
					{
					pushFollow(FOLLOW_else_tag_in_unless_tag693);
					else_tag71=else_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_else_tag.add(else_tag71.getTree());
					}
					break;

			}

			TagStart72=(Token)match(input,TagStart,FOLLOW_TagStart_in_unless_tag696); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart72);

			UnlessEnd73=(Token)match(input,UnlessEnd,FOLLOW_UnlessEnd_in_unless_tag698); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_UnlessEnd.add(UnlessEnd73);

			TagEnd74=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_unless_tag700); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd74);

			// AST REWRITE
			// elements: expr, else_tag, block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 226:79: -> ^( UNLESS expr block ^( ELSE ( else_tag )? ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:82: ^( UNLESS expr block ^( ELSE ( else_tag )? ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNLESS, "UNLESS"), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_1, stream_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:102: ^( ELSE ( else_tag )? )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ELSE, "ELSE"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:226:109: ( else_tag )?
				if ( stream_else_tag.hasNext() ) {
					adaptor.addChild(root_2, stream_else_tag.nextTree());
				}
				stream_else_tag.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "unless_tag"


	public static class case_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "case_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:229:1: case_tag : TagStart CaseStart expr TagEnd ( Other )? ( when_tag )+ ( else_tag )? TagStart CaseEnd TagEnd -> ^( CASE expr ( when_tag )+ ^( ELSE ( else_tag )? ) ) ;
	public final LiquidParser.case_tag_return case_tag() throws RecognitionException {
		LiquidParser.case_tag_return retval = new LiquidParser.case_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart75=null;
		Token CaseStart76=null;
		Token TagEnd78=null;
		Token Other79=null;
		Token TagStart82=null;
		Token CaseEnd83=null;
		Token TagEnd84=null;
		ParserRuleReturnScope expr77 =null;
		ParserRuleReturnScope when_tag80 =null;
		ParserRuleReturnScope else_tag81 =null;

		CommonTree TagStart75_tree=null;
		CommonTree CaseStart76_tree=null;
		CommonTree TagEnd78_tree=null;
		CommonTree Other79_tree=null;
		CommonTree TagStart82_tree=null;
		CommonTree CaseEnd83_tree=null;
		CommonTree TagEnd84_tree=null;
		RewriteRuleTokenStream stream_CaseEnd=new RewriteRuleTokenStream(adaptor,"token CaseEnd");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_CaseStart=new RewriteRuleTokenStream(adaptor,"token CaseStart");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_Other=new RewriteRuleTokenStream(adaptor,"token Other");
		RewriteRuleSubtreeStream stream_else_tag=new RewriteRuleSubtreeStream(adaptor,"rule else_tag");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
		RewriteRuleSubtreeStream stream_when_tag=new RewriteRuleSubtreeStream(adaptor,"rule when_tag");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:2: ( TagStart CaseStart expr TagEnd ( Other )? ( when_tag )+ ( else_tag )? TagStart CaseEnd TagEnd -> ^( CASE expr ( when_tag )+ ^( ELSE ( else_tag )? ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:4: TagStart CaseStart expr TagEnd ( Other )? ( when_tag )+ ( else_tag )? TagStart CaseEnd TagEnd
			{
			TagStart75=(Token)match(input,TagStart,FOLLOW_TagStart_in_case_tag728); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart75);

			CaseStart76=(Token)match(input,CaseStart,FOLLOW_CaseStart_in_case_tag730); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CaseStart.add(CaseStart76);

			pushFollow(FOLLOW_expr_in_case_tag732);
			expr77=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr77.getTree());
			TagEnd78=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_case_tag734); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd78);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:35: ( Other )?
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==Other) ) {
				alt11=1;
			}
			switch (alt11) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:35: Other
					{
					Other79=(Token)match(input,Other,FOLLOW_Other_in_case_tag736); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Other.add(Other79);

					}
					break;

			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:42: ( when_tag )+
			int cnt12=0;
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==TagStart) ) {
					int LA12_1 = input.LA(2);
					if ( (LA12_1==When) ) {
						alt12=1;
					}

				}

				switch (alt12) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:42: when_tag
					{
					pushFollow(FOLLOW_when_tag_in_case_tag739);
					when_tag80=when_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_when_tag.add(when_tag80.getTree());
					}
					break;

				default :
					if ( cnt12 >= 1 ) break loop12;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(12, input);
					throw eee;
				}
				cnt12++;
			}

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:52: ( else_tag )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==TagStart) ) {
				int LA13_1 = input.LA(2);
				if ( (LA13_1==Else) ) {
					alt13=1;
				}
			}
			switch (alt13) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:52: else_tag
					{
					pushFollow(FOLLOW_else_tag_in_case_tag742);
					else_tag81=else_tag();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_else_tag.add(else_tag81.getTree());
					}
					break;

			}

			TagStart82=(Token)match(input,TagStart,FOLLOW_TagStart_in_case_tag745); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart82);

			CaseEnd83=(Token)match(input,CaseEnd,FOLLOW_CaseEnd_in_case_tag747); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CaseEnd.add(CaseEnd83);

			TagEnd84=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_case_tag749); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd84);

			// AST REWRITE
			// elements: else_tag, expr, when_tag
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 230:86: -> ^( CASE expr ( when_tag )+ ^( ELSE ( else_tag )? ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:89: ^( CASE expr ( when_tag )+ ^( ELSE ( else_tag )? ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CASE, "CASE"), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				if ( !(stream_when_tag.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_when_tag.hasNext() ) {
					adaptor.addChild(root_1, stream_when_tag.nextTree());
				}
				stream_when_tag.reset();

				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:111: ^( ELSE ( else_tag )? )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ELSE, "ELSE"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:230:118: ( else_tag )?
				if ( stream_else_tag.hasNext() ) {
					adaptor.addChild(root_2, stream_else_tag.nextTree());
				}
				stream_else_tag.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "case_tag"


	public static class when_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "when_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:233:1: when_tag : TagStart When term ( ( Or | Comma ) term )* TagEnd block -> ^( WHEN ( term )+ block ) ;
	public final LiquidParser.when_tag_return when_tag() throws RecognitionException {
		LiquidParser.when_tag_return retval = new LiquidParser.when_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart85=null;
		Token When86=null;
		Token Or88=null;
		Token Comma89=null;
		Token TagEnd91=null;
		ParserRuleReturnScope term87 =null;
		ParserRuleReturnScope term90 =null;
		ParserRuleReturnScope block92 =null;

		CommonTree TagStart85_tree=null;
		CommonTree When86_tree=null;
		CommonTree Or88_tree=null;
		CommonTree Comma89_tree=null;
		CommonTree TagEnd91_tree=null;
		RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_When=new RewriteRuleTokenStream(adaptor,"token When");
		RewriteRuleTokenStream stream_Or=new RewriteRuleTokenStream(adaptor,"token Or");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_term=new RewriteRuleSubtreeStream(adaptor,"rule term");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:2: ( TagStart When term ( ( Or | Comma ) term )* TagEnd block -> ^( WHEN ( term )+ block ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:4: TagStart When term ( ( Or | Comma ) term )* TagEnd block
			{
			TagStart85=(Token)match(input,TagStart,FOLLOW_TagStart_in_when_tag778); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart85);

			When86=(Token)match(input,When,FOLLOW_When_in_when_tag780); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_When.add(When86);

			pushFollow(FOLLOW_term_in_when_tag782);
			term87=term();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_term.add(term87.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:23: ( ( Or | Comma ) term )*
			loop15:
			while (true) {
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( (LA15_0==Comma||LA15_0==Or) ) {
					alt15=1;
				}

				switch (alt15) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:24: ( Or | Comma ) term
					{
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:24: ( Or | Comma )
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==Or) ) {
						alt14=1;
					}
					else if ( (LA14_0==Comma) ) {
						alt14=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 14, 0, input);
						throw nvae;
					}

					switch (alt14) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:25: Or
							{
							Or88=(Token)match(input,Or,FOLLOW_Or_in_when_tag786); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_Or.add(Or88);

							}
							break;
						case 2 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:30: Comma
							{
							Comma89=(Token)match(input,Comma,FOLLOW_Comma_in_when_tag790); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_Comma.add(Comma89);

							}
							break;

					}

					pushFollow(FOLLOW_term_in_when_tag793);
					term90=term();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_term.add(term90.getTree());
					}
					break;

				default :
					break loop15;
				}
			}

			TagEnd91=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_when_tag797); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd91);

			pushFollow(FOLLOW_block_in_when_tag799);
			block92=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block92.getTree());
			// AST REWRITE
			// elements: term, block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 234:57: -> ^( WHEN ( term )+ block )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:234:60: ^( WHEN ( term )+ block )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(WHEN, "WHEN"), root_1);
				if ( !(stream_term.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_term.hasNext() ) {
					adaptor.addChild(root_1, stream_term.nextTree());
				}
				stream_term.reset();

				adaptor.addChild(root_1, stream_block.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "when_tag"


	public static class cycle_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "cycle_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:237:1: cycle_tag : TagStart Cycle cycle_group expr ( Comma expr )* TagEnd -> ^( CYCLE cycle_group ( expr )+ ) ;
	public final LiquidParser.cycle_tag_return cycle_tag() throws RecognitionException {
		LiquidParser.cycle_tag_return retval = new LiquidParser.cycle_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart93=null;
		Token Cycle94=null;
		Token Comma97=null;
		Token TagEnd99=null;
		ParserRuleReturnScope cycle_group95 =null;
		ParserRuleReturnScope expr96 =null;
		ParserRuleReturnScope expr98 =null;

		CommonTree TagStart93_tree=null;
		CommonTree Cycle94_tree=null;
		CommonTree Comma97_tree=null;
		CommonTree TagEnd99_tree=null;
		RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_Cycle=new RewriteRuleTokenStream(adaptor,"token Cycle");
		RewriteRuleSubtreeStream stream_cycle_group=new RewriteRuleSubtreeStream(adaptor,"rule cycle_group");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:238:2: ( TagStart Cycle cycle_group expr ( Comma expr )* TagEnd -> ^( CYCLE cycle_group ( expr )+ ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:238:4: TagStart Cycle cycle_group expr ( Comma expr )* TagEnd
			{
			TagStart93=(Token)match(input,TagStart,FOLLOW_TagStart_in_cycle_tag821); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart93);

			Cycle94=(Token)match(input,Cycle,FOLLOW_Cycle_in_cycle_tag823); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Cycle.add(Cycle94);

			pushFollow(FOLLOW_cycle_group_in_cycle_tag825);
			cycle_group95=cycle_group();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_cycle_group.add(cycle_group95.getTree());
			pushFollow(FOLLOW_expr_in_cycle_tag827);
			expr96=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr96.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:238:36: ( Comma expr )*
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==Comma) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:238:37: Comma expr
					{
					Comma97=(Token)match(input,Comma,FOLLOW_Comma_in_cycle_tag830); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Comma.add(Comma97);

					pushFollow(FOLLOW_expr_in_cycle_tag832);
					expr98=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr98.getTree());
					}
					break;

				default :
					break loop16;
				}
			}

			TagEnd99=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_cycle_tag836); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd99);

			// AST REWRITE
			// elements: cycle_group, expr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 238:57: -> ^( CYCLE cycle_group ( expr )+ )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:238:60: ^( CYCLE cycle_group ( expr )+ )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CYCLE, "CYCLE"), root_1);
				adaptor.addChild(root_1, stream_cycle_group.nextTree());
				if ( !(stream_expr.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_expr.hasNext() ) {
					adaptor.addChild(root_1, stream_expr.nextTree());
				}
				stream_expr.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cycle_tag"


	public static class cycle_group_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "cycle_group"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:241:1: cycle_group : ( ( expr Col )=> expr Col )? -> ^( GROUP ( expr )? ) ;
	public final LiquidParser.cycle_group_return cycle_group() throws RecognitionException {
		LiquidParser.cycle_group_return retval = new LiquidParser.cycle_group_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Col101=null;
		ParserRuleReturnScope expr100 =null;

		CommonTree Col101_tree=null;
		RewriteRuleTokenStream stream_Col=new RewriteRuleTokenStream(adaptor,"token Col");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:2: ( ( ( expr Col )=> expr Col )? -> ^( GROUP ( expr )? ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:4: ( ( expr Col )=> expr Col )?
			{
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:4: ( ( expr Col )=> expr Col )?
			int alt17=2;
			switch ( input.LA(1) ) {
				case DoubleNum:
					{
					int LA17_1 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case LongNum:
					{
					int LA17_2 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case Str:
					{
					int LA17_3 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case True:
					{
					int LA17_4 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case False:
					{
					int LA17_5 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case Nil:
					{
					int LA17_6 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case NoSpace:
					{
					int LA17_7 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case Id:
					{
					int LA17_8 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case Continue:
					{
					int LA17_9 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case OBr:
					{
					int LA17_10 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case Empty:
					{
					int LA17_11 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
				case OPar:
					{
					int LA17_12 = input.LA(2);
					if ( (synpred2_Liquid()) ) {
						alt17=1;
					}
					}
					break;
			}
			switch (alt17) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:5: ( expr Col )=> expr Col
					{
					pushFollow(FOLLOW_expr_in_cycle_group866);
					expr100=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr100.getTree());
					Col101=(Token)match(input,Col,FOLLOW_Col_in_cycle_group868); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Col.add(Col101);

					}
					break;

			}

			// AST REWRITE
			// elements: expr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 242:29: -> ^( GROUP ( expr )? )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:32: ^( GROUP ( expr )? )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(GROUP, "GROUP"), root_1);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:40: ( expr )?
				if ( stream_expr.hasNext() ) {
					adaptor.addChild(root_1, stream_expr.nextTree());
				}
				stream_expr.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cycle_group"


	public static class for_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "for_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:245:1: for_tag : ( for_array | for_range );
	public final LiquidParser.for_tag_return for_tag() throws RecognitionException {
		LiquidParser.for_tag_return retval = new LiquidParser.for_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope for_array102 =null;
		ParserRuleReturnScope for_range103 =null;


		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:246:2: ( for_array | for_range )
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==TagStart) ) {
				int LA18_1 = input.LA(2);
				if ( (LA18_1==ForStart) ) {
					int LA18_2 = input.LA(3);
					if ( (LA18_2==Id) ) {
						int LA18_3 = input.LA(4);
						if ( (LA18_3==In) ) {
							int LA18_4 = input.LA(5);
							if ( (LA18_4==OPar) ) {
								alt18=2;
							}
							else if ( (LA18_4==Continue||LA18_4==Id||LA18_4==OBr) ) {
								alt18=1;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 18, 4, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 18, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 18, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 18, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:246:4: for_array
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_for_array_in_for_tag890);
					for_array102=for_array();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, for_array102.getTree());

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:247:4: for_range
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_for_range_in_for_tag895);
					for_range103=for_range();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, for_range103.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "for_tag"


	public static class for_array_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "for_array"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:250:1: for_array : TagStart ForStart Id In lookup ( attribute )* TagEnd for_block TagStart ForEnd TagEnd -> ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LiquidParser.for_array_return for_array() throws RecognitionException {
		LiquidParser.for_array_return retval = new LiquidParser.for_array_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart104=null;
		Token ForStart105=null;
		Token Id106=null;
		Token In107=null;
		Token TagEnd110=null;
		Token TagStart112=null;
		Token ForEnd113=null;
		Token TagEnd114=null;
		ParserRuleReturnScope lookup108 =null;
		ParserRuleReturnScope attribute109 =null;
		ParserRuleReturnScope for_block111 =null;

		CommonTree TagStart104_tree=null;
		CommonTree ForStart105_tree=null;
		CommonTree Id106_tree=null;
		CommonTree In107_tree=null;
		CommonTree TagEnd110_tree=null;
		CommonTree TagStart112_tree=null;
		CommonTree ForEnd113_tree=null;
		CommonTree TagEnd114_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_In=new RewriteRuleTokenStream(adaptor,"token In");
		RewriteRuleTokenStream stream_ForEnd=new RewriteRuleTokenStream(adaptor,"token ForEnd");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_ForStart=new RewriteRuleTokenStream(adaptor,"token ForStart");
		RewriteRuleSubtreeStream stream_lookup=new RewriteRuleSubtreeStream(adaptor,"rule lookup");
		RewriteRuleSubtreeStream stream_for_block=new RewriteRuleSubtreeStream(adaptor,"rule for_block");
		RewriteRuleSubtreeStream stream_attribute=new RewriteRuleSubtreeStream(adaptor,"rule attribute");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:251:2: ( TagStart ForStart Id In lookup ( attribute )* TagEnd for_block TagStart ForEnd TagEnd -> ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:251:4: TagStart ForStart Id In lookup ( attribute )* TagEnd for_block TagStart ForEnd TagEnd
			{
			TagStart104=(Token)match(input,TagStart,FOLLOW_TagStart_in_for_array910); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart104);

			ForStart105=(Token)match(input,ForStart,FOLLOW_ForStart_in_for_array912); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ForStart.add(ForStart105);

			Id106=(Token)match(input,Id,FOLLOW_Id_in_for_array914); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id106);

			In107=(Token)match(input,In,FOLLOW_In_in_for_array916); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_In.add(In107);

			pushFollow(FOLLOW_lookup_in_for_array918);
			lookup108=lookup();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_lookup.add(lookup108.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:251:35: ( attribute )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( (LA19_0==Id) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:251:35: attribute
					{
					pushFollow(FOLLOW_attribute_in_for_array920);
					attribute109=attribute();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_attribute.add(attribute109.getTree());
					}
					break;

				default :
					break loop19;
				}
			}

			TagEnd110=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_for_array923); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd110);

			pushFollow(FOLLOW_for_block_in_for_array928);
			for_block111=for_block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_for_block.add(for_block111.getTree());
			TagStart112=(Token)match(input,TagStart,FOLLOW_TagStart_in_for_array933); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart112);

			ForEnd113=(Token)match(input,ForEnd,FOLLOW_ForEnd_in_for_array935); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ForEnd.add(ForEnd113);

			TagEnd114=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_for_array937); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd114);

			// AST REWRITE
			// elements: attribute, Id, lookup, for_block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 254:4: -> ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:254:7: ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FOR_ARRAY, "FOR_ARRAY"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				adaptor.addChild(root_1, stream_lookup.nextTree());
				adaptor.addChild(root_1, stream_for_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:254:39: ^( ATTRIBUTES ( attribute )* )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ATTRIBUTES, "ATTRIBUTES"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:254:52: ( attribute )*
				while ( stream_attribute.hasNext() ) {
					adaptor.addChild(root_2, stream_attribute.nextTree());
				}
				stream_attribute.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "for_array"


	public static class for_range_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "for_range"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:257:1: for_range : TagStart ForStart Id In OPar expr DotDot expr CPar ( attribute )* TagEnd block TagStart ForEnd TagEnd -> ^( FOR_RANGE Id expr expr block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LiquidParser.for_range_return for_range() throws RecognitionException {
		LiquidParser.for_range_return retval = new LiquidParser.for_range_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart115=null;
		Token ForStart116=null;
		Token Id117=null;
		Token In118=null;
		Token OPar119=null;
		Token DotDot121=null;
		Token CPar123=null;
		Token TagEnd125=null;
		Token TagStart127=null;
		Token ForEnd128=null;
		Token TagEnd129=null;
		ParserRuleReturnScope expr120 =null;
		ParserRuleReturnScope expr122 =null;
		ParserRuleReturnScope attribute124 =null;
		ParserRuleReturnScope block126 =null;

		CommonTree TagStart115_tree=null;
		CommonTree ForStart116_tree=null;
		CommonTree Id117_tree=null;
		CommonTree In118_tree=null;
		CommonTree OPar119_tree=null;
		CommonTree DotDot121_tree=null;
		CommonTree CPar123_tree=null;
		CommonTree TagEnd125_tree=null;
		CommonTree TagStart127_tree=null;
		CommonTree ForEnd128_tree=null;
		CommonTree TagEnd129_tree=null;
		RewriteRuleTokenStream stream_CPar=new RewriteRuleTokenStream(adaptor,"token CPar");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_In=new RewriteRuleTokenStream(adaptor,"token In");
		RewriteRuleTokenStream stream_ForEnd=new RewriteRuleTokenStream(adaptor,"token ForEnd");
		RewriteRuleTokenStream stream_OPar=new RewriteRuleTokenStream(adaptor,"token OPar");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_DotDot=new RewriteRuleTokenStream(adaptor,"token DotDot");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_ForStart=new RewriteRuleTokenStream(adaptor,"token ForStart");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
		RewriteRuleSubtreeStream stream_attribute=new RewriteRuleSubtreeStream(adaptor,"rule attribute");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:258:2: ( TagStart ForStart Id In OPar expr DotDot expr CPar ( attribute )* TagEnd block TagStart ForEnd TagEnd -> ^( FOR_RANGE Id expr expr block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:258:4: TagStart ForStart Id In OPar expr DotDot expr CPar ( attribute )* TagEnd block TagStart ForEnd TagEnd
			{
			TagStart115=(Token)match(input,TagStart,FOLLOW_TagStart_in_for_range970); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart115);

			ForStart116=(Token)match(input,ForStart,FOLLOW_ForStart_in_for_range972); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ForStart.add(ForStart116);

			Id117=(Token)match(input,Id,FOLLOW_Id_in_for_range974); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id117);

			In118=(Token)match(input,In,FOLLOW_In_in_for_range976); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_In.add(In118);

			OPar119=(Token)match(input,OPar,FOLLOW_OPar_in_for_range978); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_OPar.add(OPar119);

			pushFollow(FOLLOW_expr_in_for_range980);
			expr120=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr120.getTree());
			DotDot121=(Token)match(input,DotDot,FOLLOW_DotDot_in_for_range982); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_DotDot.add(DotDot121);

			pushFollow(FOLLOW_expr_in_for_range984);
			expr122=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr122.getTree());
			CPar123=(Token)match(input,CPar,FOLLOW_CPar_in_for_range986); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CPar.add(CPar123);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:258:55: ( attribute )*
			loop20:
			while (true) {
				int alt20=2;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==Id) ) {
					alt20=1;
				}

				switch (alt20) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:258:55: attribute
					{
					pushFollow(FOLLOW_attribute_in_for_range988);
					attribute124=attribute();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_attribute.add(attribute124.getTree());
					}
					break;

				default :
					break loop20;
				}
			}

			TagEnd125=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_for_range991); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd125);

			pushFollow(FOLLOW_block_in_for_range996);
			block126=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block126.getTree());
			TagStart127=(Token)match(input,TagStart,FOLLOW_TagStart_in_for_range1001); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart127);

			ForEnd128=(Token)match(input,ForEnd,FOLLOW_ForEnd_in_for_range1003); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_ForEnd.add(ForEnd128);

			TagEnd129=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_for_range1005); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd129);

			// AST REWRITE
			// elements: expr, Id, attribute, expr, block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 261:4: -> ^( FOR_RANGE Id expr expr block ^( ATTRIBUTES ( attribute )* ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:261:7: ^( FOR_RANGE Id expr expr block ^( ATTRIBUTES ( attribute )* ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FOR_RANGE, "FOR_RANGE"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_1, stream_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:261:38: ^( ATTRIBUTES ( attribute )* )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ATTRIBUTES, "ATTRIBUTES"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:261:51: ( attribute )*
				while ( stream_attribute.hasNext() ) {
					adaptor.addChild(root_2, stream_attribute.nextTree());
				}
				stream_attribute.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "for_range"


	public static class for_block_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "for_block"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:264:1: for_block : a= block ( TagStart Else TagEnd b= block )? -> ^( FOR_BLOCK block ( block )? ) ;
	public final LiquidParser.for_block_return for_block() throws RecognitionException {
		LiquidParser.for_block_return retval = new LiquidParser.for_block_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart130=null;
		Token Else131=null;
		Token TagEnd132=null;
		ParserRuleReturnScope a =null;
		ParserRuleReturnScope b =null;

		CommonTree TagStart130_tree=null;
		CommonTree Else131_tree=null;
		CommonTree TagEnd132_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Else=new RewriteRuleTokenStream(adaptor,"token Else");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:2: (a= block ( TagStart Else TagEnd b= block )? -> ^( FOR_BLOCK block ( block )? ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:4: a= block ( TagStart Else TagEnd b= block )?
			{
			pushFollow(FOLLOW_block_in_for_block1042);
			a=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(a.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:12: ( TagStart Else TagEnd b= block )?
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==TagStart) ) {
				int LA21_1 = input.LA(2);
				if ( (LA21_1==Else) ) {
					alt21=1;
				}
			}
			switch (alt21) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:13: TagStart Else TagEnd b= block
					{
					TagStart130=(Token)match(input,TagStart,FOLLOW_TagStart_in_for_block1045); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagStart.add(TagStart130);

					Else131=(Token)match(input,Else,FOLLOW_Else_in_for_block1047); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Else.add(Else131);

					TagEnd132=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_for_block1049); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd132);

					pushFollow(FOLLOW_block_in_for_block1053);
					b=block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_block.add(b.getTree());
					}
					break;

			}

			// AST REWRITE
			// elements: block, block
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 265:44: -> ^( FOR_BLOCK block ( block )? )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:47: ^( FOR_BLOCK block ( block )? )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FOR_BLOCK, "FOR_BLOCK"), root_1);
				adaptor.addChild(root_1, stream_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:265:65: ( block )?
				if ( stream_block.hasNext() ) {
					adaptor.addChild(root_1, stream_block.nextTree());
				}
				stream_block.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "for_block"


	public static class attribute_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "attribute"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:268:1: attribute : Id Col expr -> ^( Id expr ) ;
	public final LiquidParser.attribute_return attribute() throws RecognitionException {
		LiquidParser.attribute_return retval = new LiquidParser.attribute_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Id133=null;
		Token Col134=null;
		ParserRuleReturnScope expr135 =null;

		CommonTree Id133_tree=null;
		CommonTree Col134_tree=null;
		RewriteRuleTokenStream stream_Col=new RewriteRuleTokenStream(adaptor,"token Col");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:269:2: ( Id Col expr -> ^( Id expr ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:269:4: Id Col expr
			{
			Id133=(Token)match(input,Id,FOLLOW_Id_in_attribute1077); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id133);

			Col134=(Token)match(input,Col,FOLLOW_Col_in_attribute1079); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Col.add(Col134);

			pushFollow(FOLLOW_expr_in_attribute1081);
			expr135=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr135.getTree());
			// AST REWRITE
			// elements: Id, expr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 269:16: -> ^( Id expr )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:269:19: ^( Id expr )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot(stream_Id.nextNode(), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "attribute"


	public static class table_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "table_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:272:1: table_tag : TagStart TableStart Id In lookup ( attribute )* TagEnd block TagStart TableEnd TagEnd -> ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LiquidParser.table_tag_return table_tag() throws RecognitionException {
		LiquidParser.table_tag_return retval = new LiquidParser.table_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart136=null;
		Token TableStart137=null;
		Token Id138=null;
		Token In139=null;
		Token TagEnd142=null;
		Token TagStart144=null;
		Token TableEnd145=null;
		Token TagEnd146=null;
		ParserRuleReturnScope lookup140 =null;
		ParserRuleReturnScope attribute141 =null;
		ParserRuleReturnScope block143 =null;

		CommonTree TagStart136_tree=null;
		CommonTree TableStart137_tree=null;
		CommonTree Id138_tree=null;
		CommonTree In139_tree=null;
		CommonTree TagEnd142_tree=null;
		CommonTree TagStart144_tree=null;
		CommonTree TableEnd145_tree=null;
		CommonTree TagEnd146_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_In=new RewriteRuleTokenStream(adaptor,"token In");
		RewriteRuleTokenStream stream_TableEnd=new RewriteRuleTokenStream(adaptor,"token TableEnd");
		RewriteRuleTokenStream stream_TableStart=new RewriteRuleTokenStream(adaptor,"token TableStart");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_lookup=new RewriteRuleSubtreeStream(adaptor,"rule lookup");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
		RewriteRuleSubtreeStream stream_attribute=new RewriteRuleSubtreeStream(adaptor,"rule attribute");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:2: ( TagStart TableStart Id In lookup ( attribute )* TagEnd block TagStart TableEnd TagEnd -> ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:4: TagStart TableStart Id In lookup ( attribute )* TagEnd block TagStart TableEnd TagEnd
			{
			TagStart136=(Token)match(input,TagStart,FOLLOW_TagStart_in_table_tag1100); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart136);

			TableStart137=(Token)match(input,TableStart,FOLLOW_TableStart_in_table_tag1102); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TableStart.add(TableStart137);

			Id138=(Token)match(input,Id,FOLLOW_Id_in_table_tag1104); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id138);

			In139=(Token)match(input,In,FOLLOW_In_in_table_tag1106); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_In.add(In139);

			pushFollow(FOLLOW_lookup_in_table_tag1108);
			lookup140=lookup();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_lookup.add(lookup140.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:37: ( attribute )*
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( (LA22_0==Id) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:37: attribute
					{
					pushFollow(FOLLOW_attribute_in_table_tag1110);
					attribute141=attribute();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_attribute.add(attribute141.getTree());
					}
					break;

				default :
					break loop22;
				}
			}

			TagEnd142=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_table_tag1113); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd142);

			pushFollow(FOLLOW_block_in_table_tag1115);
			block143=block();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_block.add(block143.getTree());
			TagStart144=(Token)match(input,TagStart,FOLLOW_TagStart_in_table_tag1117); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart144);

			TableEnd145=(Token)match(input,TableEnd,FOLLOW_TableEnd_in_table_tag1119); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TableEnd.add(TableEnd145);

			TagEnd146=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_table_tag1121); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd146);

			// AST REWRITE
			// elements: Id, lookup, block, attribute
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 273:86: -> ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:89: ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(TABLE, "TABLE"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				adaptor.addChild(root_1, stream_lookup.nextTree());
				adaptor.addChild(root_1, stream_block.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:113: ^( ATTRIBUTES ( attribute )* )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ATTRIBUTES, "ATTRIBUTES"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:273:126: ( attribute )*
				while ( stream_attribute.hasNext() ) {
					adaptor.addChild(root_2, stream_attribute.nextTree());
				}
				stream_attribute.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "table_tag"


	public static class capture_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "capture_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:276:1: capture_tag : TagStart CaptureStart ( Id TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id block ) | Str TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id[$Str.text] block ) ) ;
	public final LiquidParser.capture_tag_return capture_tag() throws RecognitionException {
		LiquidParser.capture_tag_return retval = new LiquidParser.capture_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart147=null;
		Token CaptureStart148=null;
		Token Id149=null;
		Token TagEnd150=null;
		Token TagStart152=null;
		Token CaptureEnd153=null;
		Token TagEnd154=null;
		Token Str155=null;
		Token TagEnd156=null;
		Token TagStart158=null;
		Token CaptureEnd159=null;
		Token TagEnd160=null;
		ParserRuleReturnScope block151 =null;
		ParserRuleReturnScope block157 =null;

		CommonTree TagStart147_tree=null;
		CommonTree CaptureStart148_tree=null;
		CommonTree Id149_tree=null;
		CommonTree TagEnd150_tree=null;
		CommonTree TagStart152_tree=null;
		CommonTree CaptureEnd153_tree=null;
		CommonTree TagEnd154_tree=null;
		CommonTree Str155_tree=null;
		CommonTree TagEnd156_tree=null;
		CommonTree TagStart158_tree=null;
		CommonTree CaptureEnd159_tree=null;
		CommonTree TagEnd160_tree=null;
		RewriteRuleTokenStream stream_Str=new RewriteRuleTokenStream(adaptor,"token Str");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_CaptureStart=new RewriteRuleTokenStream(adaptor,"token CaptureStart");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_CaptureEnd=new RewriteRuleTokenStream(adaptor,"token CaptureEnd");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:277:2: ( TagStart CaptureStart ( Id TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id block ) | Str TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id[$Str.text] block ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:277:4: TagStart CaptureStart ( Id TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id block ) | Str TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id[$Str.text] block ) )
			{
			TagStart147=(Token)match(input,TagStart,FOLLOW_TagStart_in_capture_tag1151); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart147);

			CaptureStart148=(Token)match(input,CaptureStart,FOLLOW_CaptureStart_in_capture_tag1153); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CaptureStart.add(CaptureStart148);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:277:26: ( Id TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id block ) | Str TagEnd block TagStart CaptureEnd TagEnd -> ^( CAPTURE Id[$Str.text] block ) )
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==Id) ) {
				alt23=1;
			}
			else if ( (LA23_0==Str) ) {
				alt23=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:277:28: Id TagEnd block TagStart CaptureEnd TagEnd
					{
					Id149=(Token)match(input,Id,FOLLOW_Id_in_capture_tag1157); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Id.add(Id149);

					TagEnd150=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_capture_tag1159); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd150);

					pushFollow(FOLLOW_block_in_capture_tag1161);
					block151=block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_block.add(block151.getTree());
					TagStart152=(Token)match(input,TagStart,FOLLOW_TagStart_in_capture_tag1163); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagStart.add(TagStart152);

					CaptureEnd153=(Token)match(input,CaptureEnd,FOLLOW_CaptureEnd_in_capture_tag1165); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaptureEnd.add(CaptureEnd153);

					TagEnd154=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_capture_tag1167); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd154);

					// AST REWRITE
					// elements: block, Id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 277:72: -> ^( CAPTURE Id block )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:277:75: ^( CAPTURE Id block )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CAPTURE, "CAPTURE"), root_1);
						adaptor.addChild(root_1, stream_Id.nextNode());
						adaptor.addChild(root_1, stream_block.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:278:28: Str TagEnd block TagStart CaptureEnd TagEnd
					{
					Str155=(Token)match(input,Str,FOLLOW_Str_in_capture_tag1207); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Str.add(Str155);

					TagEnd156=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_capture_tag1209); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd156);

					pushFollow(FOLLOW_block_in_capture_tag1211);
					block157=block();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_block.add(block157.getTree());
					TagStart158=(Token)match(input,TagStart,FOLLOW_TagStart_in_capture_tag1213); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagStart.add(TagStart158);

					CaptureEnd159=(Token)match(input,CaptureEnd,FOLLOW_CaptureEnd_in_capture_tag1215); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaptureEnd.add(CaptureEnd159);

					TagEnd160=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_capture_tag1217); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd160);

					// AST REWRITE
					// elements: Id, block
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 278:72: -> ^( CAPTURE Id[$Str.text] block )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:278:75: ^( CAPTURE Id[$Str.text] block )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(CAPTURE, "CAPTURE"), root_1);
						adaptor.addChild(root_1, (CommonTree)adaptor.create(Id, (Str155!=null?Str155.getText():null)));
						adaptor.addChild(root_1, stream_block.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "capture_tag"


	public static class include_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "include_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:282:1: include_tag : TagStart Include ({...}? => file_name_as_str TagEnd -> ^( INCLUDE file_name_as_str ^( WITH ) ) |a= Str ( With b= Str )? TagEnd -> ^( INCLUDE $a ^( WITH ( $b)? ) ) ) ;
	public final LiquidParser.include_tag_return include_tag() throws RecognitionException {
		LiquidParser.include_tag_return retval = new LiquidParser.include_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token a=null;
		Token b=null;
		Token TagStart161=null;
		Token Include162=null;
		Token TagEnd164=null;
		Token With165=null;
		Token TagEnd166=null;
		ParserRuleReturnScope file_name_as_str163 =null;

		CommonTree a_tree=null;
		CommonTree b_tree=null;
		CommonTree TagStart161_tree=null;
		CommonTree Include162_tree=null;
		CommonTree TagEnd164_tree=null;
		CommonTree With165_tree=null;
		CommonTree TagEnd166_tree=null;
		RewriteRuleTokenStream stream_Str=new RewriteRuleTokenStream(adaptor,"token Str");
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Include=new RewriteRuleTokenStream(adaptor,"token Include");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleTokenStream stream_With=new RewriteRuleTokenStream(adaptor,"token With");
		RewriteRuleSubtreeStream stream_file_name_as_str=new RewriteRuleSubtreeStream(adaptor,"rule file_name_as_str");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:283:2: ( TagStart Include ({...}? => file_name_as_str TagEnd -> ^( INCLUDE file_name_as_str ^( WITH ) ) |a= Str ( With b= Str )? TagEnd -> ^( INCLUDE $a ^( WITH ( $b)? ) ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:283:4: TagStart Include ({...}? => file_name_as_str TagEnd -> ^( INCLUDE file_name_as_str ^( WITH ) ) |a= Str ( With b= Str )? TagEnd -> ^( INCLUDE $a ^( WITH ( $b)? ) ) )
			{
			TagStart161=(Token)match(input,TagStart,FOLLOW_TagStart_in_include_tag1266); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart161);

			Include162=(Token)match(input,Include,FOLLOW_Include_in_include_tag1268); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Include.add(Include162);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:283:21: ({...}? => file_name_as_str TagEnd -> ^( INCLUDE file_name_as_str ^( WITH ) ) |a= Str ( With b= Str )? TagEnd -> ^( INCLUDE $a ^( WITH ( $b)? ) ) )
			int alt25=2;
			int LA25_0 = input.LA(1);
			if ( (LA25_0==Str) ) {
				int LA25_1 = input.LA(2);
				if ( (LA25_1==TagEnd) ) {
					int LA25_3 = input.LA(3);
					if ( ((this.flavor == Flavor.JEKYLL)) ) {
						alt25=1;
					}
					else if ( (true) ) {
						alt25=2;
					}

				}
				else if ( (LA25_1==With) ) {
					int LA25_4 = input.LA(3);
					if ( (LA25_4==Str) ) {
						int LA25_6 = input.LA(4);
						if ( (LA25_6==TagEnd) ) {
							int LA25_7 = input.LA(5);
							if ( ((this.flavor == Flavor.JEKYLL)) ) {
								alt25=1;
							}
							else if ( (true) ) {
								alt25=2;
							}

						}
						else if ( ((LA25_6 >= ASSIGNMENT && LA25_6 <= TableStart)||(LA25_6 >= TagEndDefaultStrip && LA25_6 <= With)) && ((this.flavor == Flavor.JEKYLL))) {
							alt25=1;
						}

					}
					else if ( ((LA25_4 >= ASSIGNMENT && LA25_4 <= SStr)||(LA25_4 >= TABLE && LA25_4 <= With)) && ((this.flavor == Flavor.JEKYLL))) {
						alt25=1;
					}

				}
				else if ( ((LA25_1 >= ASSIGNMENT && LA25_1 <= TableStart)||(LA25_1 >= TagEndDefaultStrip && LA25_1 <= WhitespaceChar)) && ((this.flavor == Flavor.JEKYLL))) {
					alt25=1;
				}

			}
			else if ( ((LA25_0 >= ASSIGNMENT && LA25_0 <= SStr)||(LA25_0 >= TABLE && LA25_0 <= TableStart)||(LA25_0 >= TagEndDefaultStrip && LA25_0 <= With)) && ((this.flavor == Flavor.JEKYLL))) {
				alt25=1;
			}

			switch (alt25) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:283:23: {...}? => file_name_as_str TagEnd
					{
					if ( !((this.flavor == Flavor.JEKYLL)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "include_tag", "this.flavor == Flavor.JEKYLL");
					}
					pushFollow(FOLLOW_file_name_as_str_in_include_tag1297);
					file_name_as_str163=file_name_as_str();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_file_name_as_str.add(file_name_as_str163.getTree());
					TagEnd164=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_include_tag1299); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd164);

					// AST REWRITE
					// elements: file_name_as_str
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 284:50: -> ^( INCLUDE file_name_as_str ^( WITH ) )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:284:53: ^( INCLUDE file_name_as_str ^( WITH ) )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INCLUDE, "INCLUDE"), root_1);
						adaptor.addChild(root_1, stream_file_name_as_str.nextTree());
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:284:80: ^( WITH )
						{
						CommonTree root_2 = (CommonTree)adaptor.nil();
						root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(WITH, "WITH"), root_2);
						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:23: a= Str ( With b= Str )? TagEnd
					{
					a=(Token)match(input,Str,FOLLOW_Str_in_include_tag1344); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Str.add(a);

					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:29: ( With b= Str )?
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==With) ) {
						alt24=1;
					}
					switch (alt24) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:30: With b= Str
							{
							With165=(Token)match(input,With,FOLLOW_With_in_include_tag1347); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_With.add(With165);

							b=(Token)match(input,Str,FOLLOW_Str_in_include_tag1351); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_Str.add(b);

							}
							break;

					}

					TagEnd166=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_include_tag1355); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd166);

					// AST REWRITE
					// elements: a, b
					// token labels: a, b
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleTokenStream stream_a=new RewriteRuleTokenStream(adaptor,"token a",a);
					RewriteRuleTokenStream stream_b=new RewriteRuleTokenStream(adaptor,"token b",b);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 285:50: -> ^( INCLUDE $a ^( WITH ( $b)? ) )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:53: ^( INCLUDE $a ^( WITH ( $b)? ) )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INCLUDE, "INCLUDE"), root_1);
						adaptor.addChild(root_1, stream_a.nextNode());
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:80: ^( WITH ( $b)? )
						{
						CommonTree root_2 = (CommonTree)adaptor.nil();
						root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(WITH, "WITH"), root_2);
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:285:88: ( $b)?
						if ( stream_b.hasNext() ) {
							adaptor.addChild(root_2, stream_b.nextNode());
						}
						stream_b.reset();

						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "include_tag"


	public static class break_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "break_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:289:1: break_tag : TagStart Break TagEnd -> Break ;
	public final LiquidParser.break_tag_return break_tag() throws RecognitionException {
		LiquidParser.break_tag_return retval = new LiquidParser.break_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart167=null;
		Token Break168=null;
		Token TagEnd169=null;

		CommonTree TagStart167_tree=null;
		CommonTree Break168_tree=null;
		CommonTree TagEnd169_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Break=new RewriteRuleTokenStream(adaptor,"token Break");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:290:2: ( TagStart Break TagEnd -> Break )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:290:4: TagStart Break TagEnd
			{
			TagStart167=(Token)match(input,TagStart,FOLLOW_TagStart_in_break_tag1419); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart167);

			Break168=(Token)match(input,Break,FOLLOW_Break_in_break_tag1421); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Break.add(Break168);

			TagEnd169=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_break_tag1423); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd169);

			// AST REWRITE
			// elements: Break
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 290:26: -> Break
			{
				adaptor.addChild(root_0, stream_Break.nextNode());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "break_tag"


	public static class continue_tag_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "continue_tag"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:293:1: continue_tag : TagStart Continue TagEnd -> Continue ;
	public final LiquidParser.continue_tag_return continue_tag() throws RecognitionException {
		LiquidParser.continue_tag_return retval = new LiquidParser.continue_tag_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart170=null;
		Token Continue171=null;
		Token TagEnd172=null;

		CommonTree TagStart170_tree=null;
		CommonTree Continue171_tree=null;
		CommonTree TagEnd172_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_Continue=new RewriteRuleTokenStream(adaptor,"token Continue");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:294:2: ( TagStart Continue TagEnd -> Continue )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:294:4: TagStart Continue TagEnd
			{
			TagStart170=(Token)match(input,TagStart,FOLLOW_TagStart_in_continue_tag1438); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart170);

			Continue171=(Token)match(input,Continue,FOLLOW_Continue_in_continue_tag1440); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Continue.add(Continue171);

			TagEnd172=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_continue_tag1442); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd172);

			// AST REWRITE
			// elements: Continue
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 294:29: -> Continue
			{
				adaptor.addChild(root_0, stream_Continue.nextNode());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "continue_tag"


	public static class output_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "output"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:297:1: output : OutStart expr ( filter )* OutEnd -> ^( OUTPUT expr ^( FILTERS ( filter )* ) ) ;
	public final LiquidParser.output_return output() throws RecognitionException {
		LiquidParser.output_return retval = new LiquidParser.output_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token OutStart173=null;
		Token OutEnd176=null;
		ParserRuleReturnScope expr174 =null;
		ParserRuleReturnScope filter175 =null;

		CommonTree OutStart173_tree=null;
		CommonTree OutEnd176_tree=null;
		RewriteRuleTokenStream stream_OutEnd=new RewriteRuleTokenStream(adaptor,"token OutEnd");
		RewriteRuleTokenStream stream_OutStart=new RewriteRuleTokenStream(adaptor,"token OutStart");
		RewriteRuleSubtreeStream stream_filter=new RewriteRuleSubtreeStream(adaptor,"rule filter");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:2: ( OutStart expr ( filter )* OutEnd -> ^( OUTPUT expr ^( FILTERS ( filter )* ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:4: OutStart expr ( filter )* OutEnd
			{
			OutStart173=(Token)match(input,OutStart,FOLLOW_OutStart_in_output1457); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_OutStart.add(OutStart173);

			pushFollow(FOLLOW_expr_in_output1459);
			expr174=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr174.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:18: ( filter )*
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( (LA26_0==Pipe) ) {
					alt26=1;
				}

				switch (alt26) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:18: filter
					{
					pushFollow(FOLLOW_filter_in_output1461);
					filter175=filter();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_filter.add(filter175.getTree());
					}
					break;

				default :
					break loop26;
				}
			}

			OutEnd176=(Token)match(input,OutEnd,FOLLOW_OutEnd_in_output1464); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_OutEnd.add(OutEnd176);

			// AST REWRITE
			// elements: expr, filter
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 298:33: -> ^( OUTPUT expr ^( FILTERS ( filter )* ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:36: ^( OUTPUT expr ^( FILTERS ( filter )* ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(OUTPUT, "OUTPUT"), root_1);
				adaptor.addChild(root_1, stream_expr.nextTree());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:50: ^( FILTERS ( filter )* )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FILTERS, "FILTERS"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:298:60: ( filter )*
				while ( stream_filter.hasNext() ) {
					adaptor.addChild(root_2, stream_filter.nextTree());
				}
				stream_filter.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "output"


	public static class filter_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "filter"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:301:1: filter : Pipe Id ( params )? -> ^( FILTER Id ^( PARAMS ( params )? ) ) ;
	public final LiquidParser.filter_return filter() throws RecognitionException {
		LiquidParser.filter_return retval = new LiquidParser.filter_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Pipe177=null;
		Token Id178=null;
		ParserRuleReturnScope params179 =null;

		CommonTree Pipe177_tree=null;
		CommonTree Id178_tree=null;
		RewriteRuleTokenStream stream_Pipe=new RewriteRuleTokenStream(adaptor,"token Pipe");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleSubtreeStream stream_params=new RewriteRuleSubtreeStream(adaptor,"rule params");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:2: ( Pipe Id ( params )? -> ^( FILTER Id ^( PARAMS ( params )? ) ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:4: Pipe Id ( params )?
			{
			Pipe177=(Token)match(input,Pipe,FOLLOW_Pipe_in_filter1490); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Pipe.add(Pipe177);

			Id178=(Token)match(input,Id,FOLLOW_Id_in_filter1492); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id178);

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:12: ( params )?
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==Col) ) {
				alt27=1;
			}
			switch (alt27) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:12: params
					{
					pushFollow(FOLLOW_params_in_filter1494);
					params179=params();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_params.add(params179.getTree());
					}
					break;

			}

			// AST REWRITE
			// elements: Id, params
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 302:20: -> ^( FILTER Id ^( PARAMS ( params )? ) )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:23: ^( FILTER Id ^( PARAMS ( params )? ) )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FILTER, "FILTER"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:35: ^( PARAMS ( params )? )
				{
				CommonTree root_2 = (CommonTree)adaptor.nil();
				root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(PARAMS, "PARAMS"), root_2);
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:302:44: ( params )?
				if ( stream_params.hasNext() ) {
					adaptor.addChild(root_2, stream_params.nextTree());
				}
				stream_params.reset();

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "filter"


	public static class params_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "params"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:305:1: params : Col expr ( Comma expr )* -> ( expr )+ ;
	public final LiquidParser.params_return params() throws RecognitionException {
		LiquidParser.params_return retval = new LiquidParser.params_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Col180=null;
		Token Comma182=null;
		ParserRuleReturnScope expr181 =null;
		ParserRuleReturnScope expr183 =null;

		CommonTree Col180_tree=null;
		CommonTree Comma182_tree=null;
		RewriteRuleTokenStream stream_Col=new RewriteRuleTokenStream(adaptor,"token Col");
		RewriteRuleTokenStream stream_Comma=new RewriteRuleTokenStream(adaptor,"token Comma");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:306:2: ( Col expr ( Comma expr )* -> ( expr )+ )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:306:4: Col expr ( Comma expr )*
			{
			Col180=(Token)match(input,Col,FOLLOW_Col_in_params1521); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Col.add(Col180);

			pushFollow(FOLLOW_expr_in_params1523);
			expr181=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr181.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:306:13: ( Comma expr )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==Comma) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:306:14: Comma expr
					{
					Comma182=(Token)match(input,Comma,FOLLOW_Comma_in_params1526); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Comma.add(Comma182);

					pushFollow(FOLLOW_expr_in_params1528);
					expr183=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr183.getTree());
					}
					break;

				default :
					break loop28;
				}
			}

			// AST REWRITE
			// elements: expr
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 306:27: -> ( expr )+
			{
				if ( !(stream_expr.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_expr.hasNext() ) {
					adaptor.addChild(root_0, stream_expr.nextTree());
				}
				stream_expr.reset();

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "params"


	public static class assignment_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "assignment"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:309:1: assignment : TagStart Assign Id EqSign expr ( filter )? TagEnd -> ^( ASSIGNMENT Id ( filter )? expr ) ;
	public final LiquidParser.assignment_return assignment() throws RecognitionException {
		LiquidParser.assignment_return retval = new LiquidParser.assignment_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token TagStart184=null;
		Token Assign185=null;
		Token Id186=null;
		Token EqSign187=null;
		Token TagEnd190=null;
		ParserRuleReturnScope expr188 =null;
		ParserRuleReturnScope filter189 =null;

		CommonTree TagStart184_tree=null;
		CommonTree Assign185_tree=null;
		CommonTree Id186_tree=null;
		CommonTree EqSign187_tree=null;
		CommonTree TagEnd190_tree=null;
		RewriteRuleTokenStream stream_TagStart=new RewriteRuleTokenStream(adaptor,"token TagStart");
		RewriteRuleTokenStream stream_EqSign=new RewriteRuleTokenStream(adaptor,"token EqSign");
		RewriteRuleTokenStream stream_Assign=new RewriteRuleTokenStream(adaptor,"token Assign");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_TagEnd=new RewriteRuleTokenStream(adaptor,"token TagEnd");
		RewriteRuleSubtreeStream stream_filter=new RewriteRuleSubtreeStream(adaptor,"rule filter");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:2: ( TagStart Assign Id EqSign expr ( filter )? TagEnd -> ^( ASSIGNMENT Id ( filter )? expr ) )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:4: TagStart Assign Id EqSign expr ( filter )? TagEnd
			{
			TagStart184=(Token)match(input,TagStart,FOLLOW_TagStart_in_assignment1546); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagStart.add(TagStart184);

			Assign185=(Token)match(input,Assign,FOLLOW_Assign_in_assignment1548); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Assign.add(Assign185);

			Id186=(Token)match(input,Id,FOLLOW_Id_in_assignment1550); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_Id.add(Id186);

			EqSign187=(Token)match(input,EqSign,FOLLOW_EqSign_in_assignment1552); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EqSign.add(EqSign187);

			pushFollow(FOLLOW_expr_in_assignment1554);
			expr188=expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_expr.add(expr188.getTree());
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:35: ( filter )?
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==Pipe) ) {
				alt29=1;
			}
			switch (alt29) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:35: filter
					{
					pushFollow(FOLLOW_filter_in_assignment1556);
					filter189=filter();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_filter.add(filter189.getTree());
					}
					break;

			}

			TagEnd190=(Token)match(input,TagEnd,FOLLOW_TagEnd_in_assignment1559); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TagEnd.add(TagEnd190);

			// AST REWRITE
			// elements: filter, expr, Id
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 310:50: -> ^( ASSIGNMENT Id ( filter )? expr )
			{
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:53: ^( ASSIGNMENT Id ( filter )? expr )
				{
				CommonTree root_1 = (CommonTree)adaptor.nil();
				root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ASSIGNMENT, "ASSIGNMENT"), root_1);
				adaptor.addChild(root_1, stream_Id.nextNode());
				// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:310:69: ( filter )?
				if ( stream_filter.hasNext() ) {
					adaptor.addChild(root_1, stream_filter.nextTree());
				}
				stream_filter.reset();

				adaptor.addChild(root_1, stream_expr.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "assignment"


	public static class expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:313:1: expr : or_expr ;
	public final LiquidParser.expr_return expr() throws RecognitionException {
		LiquidParser.expr_return retval = new LiquidParser.expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope or_expr191 =null;


		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:314:2: ( or_expr )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:314:4: or_expr
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_or_expr_in_expr1583);
			or_expr191=or_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, or_expr191.getTree());

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "expr"


	public static class or_expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "or_expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:317:1: or_expr : and_expr ( Or ^ and_expr )* ;
	public final LiquidParser.or_expr_return or_expr() throws RecognitionException {
		LiquidParser.or_expr_return retval = new LiquidParser.or_expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Or193=null;
		ParserRuleReturnScope and_expr192 =null;
		ParserRuleReturnScope and_expr194 =null;

		CommonTree Or193_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:318:2: ( and_expr ( Or ^ and_expr )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:318:4: and_expr ( Or ^ and_expr )*
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_and_expr_in_or_expr1594);
			and_expr192=and_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, and_expr192.getTree());

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:318:13: ( Or ^ and_expr )*
			loop30:
			while (true) {
				int alt30=2;
				int LA30_0 = input.LA(1);
				if ( (LA30_0==Or) ) {
					alt30=1;
				}

				switch (alt30) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:318:14: Or ^ and_expr
					{
					Or193=(Token)match(input,Or,FOLLOW_Or_in_or_expr1597); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Or193_tree = (CommonTree)adaptor.create(Or193);
					root_0 = (CommonTree)adaptor.becomeRoot(Or193_tree, root_0);
					}

					pushFollow(FOLLOW_and_expr_in_or_expr1600);
					and_expr194=and_expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, and_expr194.getTree());

					}
					break;

				default :
					break loop30;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "or_expr"


	public static class and_expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "and_expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:321:1: and_expr : contains_expr ( And ^ contains_expr )* ;
	public final LiquidParser.and_expr_return and_expr() throws RecognitionException {
		LiquidParser.and_expr_return retval = new LiquidParser.and_expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token And196=null;
		ParserRuleReturnScope contains_expr195 =null;
		ParserRuleReturnScope contains_expr197 =null;

		CommonTree And196_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:322:2: ( contains_expr ( And ^ contains_expr )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:322:4: contains_expr ( And ^ contains_expr )*
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_contains_expr_in_and_expr1613);
			contains_expr195=contains_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, contains_expr195.getTree());

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:322:18: ( And ^ contains_expr )*
			loop31:
			while (true) {
				int alt31=2;
				int LA31_0 = input.LA(1);
				if ( (LA31_0==And) ) {
					alt31=1;
				}

				switch (alt31) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:322:19: And ^ contains_expr
					{
					And196=(Token)match(input,And,FOLLOW_And_in_and_expr1616); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					And196_tree = (CommonTree)adaptor.create(And196);
					root_0 = (CommonTree)adaptor.becomeRoot(And196_tree, root_0);
					}

					pushFollow(FOLLOW_contains_expr_in_and_expr1619);
					contains_expr197=contains_expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, contains_expr197.getTree());

					}
					break;

				default :
					break loop31;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "and_expr"


	public static class contains_expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "contains_expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:325:1: contains_expr : eq_expr ( Contains ^ eq_expr )? ;
	public final LiquidParser.contains_expr_return contains_expr() throws RecognitionException {
		LiquidParser.contains_expr_return retval = new LiquidParser.contains_expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Contains199=null;
		ParserRuleReturnScope eq_expr198 =null;
		ParserRuleReturnScope eq_expr200 =null;

		CommonTree Contains199_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:326:2: ( eq_expr ( Contains ^ eq_expr )? )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:326:4: eq_expr ( Contains ^ eq_expr )?
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_eq_expr_in_contains_expr1632);
			eq_expr198=eq_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, eq_expr198.getTree());

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:326:12: ( Contains ^ eq_expr )?
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==Contains) ) {
				alt32=1;
			}
			switch (alt32) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:326:13: Contains ^ eq_expr
					{
					Contains199=(Token)match(input,Contains,FOLLOW_Contains_in_contains_expr1635); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Contains199_tree = (CommonTree)adaptor.create(Contains199);
					root_0 = (CommonTree)adaptor.becomeRoot(Contains199_tree, root_0);
					}

					pushFollow(FOLLOW_eq_expr_in_contains_expr1638);
					eq_expr200=eq_expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, eq_expr200.getTree());

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "contains_expr"


	public static class eq_expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "eq_expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:329:1: eq_expr : rel_expr ( ( Eq | NEq ) ^ rel_expr )* ;
	public final LiquidParser.eq_expr_return eq_expr() throws RecognitionException {
		LiquidParser.eq_expr_return retval = new LiquidParser.eq_expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set202=null;
		ParserRuleReturnScope rel_expr201 =null;
		ParserRuleReturnScope rel_expr203 =null;

		CommonTree set202_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:330:2: ( rel_expr ( ( Eq | NEq ) ^ rel_expr )* )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:330:4: rel_expr ( ( Eq | NEq ) ^ rel_expr )*
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_rel_expr_in_eq_expr1651);
			rel_expr201=rel_expr();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, rel_expr201.getTree());

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:330:13: ( ( Eq | NEq ) ^ rel_expr )*
			loop33:
			while (true) {
				int alt33=2;
				int LA33_0 = input.LA(1);
				if ( (LA33_0==Eq||LA33_0==NEq) ) {
					alt33=1;
				}

				switch (alt33) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:330:14: ( Eq | NEq ) ^ rel_expr
					{
					set202=input.LT(1);
					set202=input.LT(1);
					if ( input.LA(1)==Eq||input.LA(1)==NEq ) {
						input.consume();
						if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set202), root_0);
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_rel_expr_in_eq_expr1663);
					rel_expr203=rel_expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, rel_expr203.getTree());

					}
					break;

				default :
					break loop33;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "eq_expr"


	public static class rel_expr_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "rel_expr"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:333:1: rel_expr : term ( ( LtEq | Lt | GtEq | Gt ) ^ term )? ;
	public final LiquidParser.rel_expr_return rel_expr() throws RecognitionException {
		LiquidParser.rel_expr_return retval = new LiquidParser.rel_expr_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set205=null;
		ParserRuleReturnScope term204 =null;
		ParserRuleReturnScope term206 =null;

		CommonTree set205_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:334:2: ( term ( ( LtEq | Lt | GtEq | Gt ) ^ term )? )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:334:4: term ( ( LtEq | Lt | GtEq | Gt ) ^ term )?
			{
			root_0 = (CommonTree)adaptor.nil();


			pushFollow(FOLLOW_term_in_rel_expr1676);
			term204=term();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, term204.getTree());

			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:334:9: ( ( LtEq | Lt | GtEq | Gt ) ^ term )?
			int alt34=2;
			int LA34_0 = input.LA(1);
			if ( ((LA34_0 >= Gt && LA34_0 <= GtEq)||(LA34_0 >= Lt && LA34_0 <= LtEq)) ) {
				alt34=1;
			}
			switch (alt34) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:334:10: ( LtEq | Lt | GtEq | Gt ) ^ term
					{
					set205=input.LT(1);
					set205=input.LT(1);
					if ( (input.LA(1) >= Gt && input.LA(1) <= GtEq)||(input.LA(1) >= Lt && input.LA(1) <= LtEq) ) {
						input.consume();
						if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set205), root_0);
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_term_in_rel_expr1696);
					term206=term();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, term206.getTree());

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rel_expr"


	public static class term_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "term"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:337:1: term : ( DoubleNum | LongNum | Str | True | False | Nil | ( NoSpace )+ -> NO_SPACE[$text] | lookup | Empty | OPar expr CPar -> expr );
	public final LiquidParser.term_return term() throws RecognitionException {
		LiquidParser.term_return retval = new LiquidParser.term_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token DoubleNum207=null;
		Token LongNum208=null;
		Token Str209=null;
		Token True210=null;
		Token False211=null;
		Token Nil212=null;
		Token NoSpace213=null;
		Token Empty215=null;
		Token OPar216=null;
		Token CPar218=null;
		ParserRuleReturnScope lookup214 =null;
		ParserRuleReturnScope expr217 =null;

		CommonTree DoubleNum207_tree=null;
		CommonTree LongNum208_tree=null;
		CommonTree Str209_tree=null;
		CommonTree True210_tree=null;
		CommonTree False211_tree=null;
		CommonTree Nil212_tree=null;
		CommonTree NoSpace213_tree=null;
		CommonTree Empty215_tree=null;
		CommonTree OPar216_tree=null;
		CommonTree CPar218_tree=null;
		RewriteRuleTokenStream stream_NoSpace=new RewriteRuleTokenStream(adaptor,"token NoSpace");
		RewriteRuleTokenStream stream_CPar=new RewriteRuleTokenStream(adaptor,"token CPar");
		RewriteRuleTokenStream stream_OPar=new RewriteRuleTokenStream(adaptor,"token OPar");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:338:2: ( DoubleNum | LongNum | Str | True | False | Nil | ( NoSpace )+ -> NO_SPACE[$text] | lookup | Empty | OPar expr CPar -> expr )
			int alt36=10;
			switch ( input.LA(1) ) {
			case DoubleNum:
				{
				alt36=1;
				}
				break;
			case LongNum:
				{
				alt36=2;
				}
				break;
			case Str:
				{
				alt36=3;
				}
				break;
			case True:
				{
				alt36=4;
				}
				break;
			case False:
				{
				alt36=5;
				}
				break;
			case Nil:
				{
				alt36=6;
				}
				break;
			case NoSpace:
				{
				alt36=7;
				}
				break;
			case Continue:
			case Id:
			case OBr:
				{
				alt36=8;
				}
				break;
			case Empty:
				{
				alt36=9;
				}
				break;
			case OPar:
				{
				alt36=10;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 36, 0, input);
				throw nvae;
			}
			switch (alt36) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:338:4: DoubleNum
					{
					root_0 = (CommonTree)adaptor.nil();


					DoubleNum207=(Token)match(input,DoubleNum,FOLLOW_DoubleNum_in_term1709); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					DoubleNum207_tree = (CommonTree)adaptor.create(DoubleNum207);
					adaptor.addChild(root_0, DoubleNum207_tree);
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:339:4: LongNum
					{
					root_0 = (CommonTree)adaptor.nil();


					LongNum208=(Token)match(input,LongNum,FOLLOW_LongNum_in_term1714); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					LongNum208_tree = (CommonTree)adaptor.create(LongNum208);
					adaptor.addChild(root_0, LongNum208_tree);
					}

					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:340:4: Str
					{
					root_0 = (CommonTree)adaptor.nil();


					Str209=(Token)match(input,Str,FOLLOW_Str_in_term1719); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Str209_tree = (CommonTree)adaptor.create(Str209);
					adaptor.addChild(root_0, Str209_tree);
					}

					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:341:4: True
					{
					root_0 = (CommonTree)adaptor.nil();


					True210=(Token)match(input,True,FOLLOW_True_in_term1724); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					True210_tree = (CommonTree)adaptor.create(True210);
					adaptor.addChild(root_0, True210_tree);
					}

					}
					break;
				case 5 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:342:4: False
					{
					root_0 = (CommonTree)adaptor.nil();


					False211=(Token)match(input,False,FOLLOW_False_in_term1729); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					False211_tree = (CommonTree)adaptor.create(False211);
					adaptor.addChild(root_0, False211_tree);
					}

					}
					break;
				case 6 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:343:4: Nil
					{
					root_0 = (CommonTree)adaptor.nil();


					Nil212=(Token)match(input,Nil,FOLLOW_Nil_in_term1734); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Nil212_tree = (CommonTree)adaptor.create(Nil212);
					adaptor.addChild(root_0, Nil212_tree);
					}

					}
					break;
				case 7 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:344:4: ( NoSpace )+
					{
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:344:4: ( NoSpace )+
					int cnt35=0;
					loop35:
					while (true) {
						int alt35=2;
						int LA35_0 = input.LA(1);
						if ( (LA35_0==NoSpace) ) {
							alt35=1;
						}

						switch (alt35) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:344:4: NoSpace
							{
							NoSpace213=(Token)match(input,NoSpace,FOLLOW_NoSpace_in_term1739); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_NoSpace.add(NoSpace213);

							}
							break;

						default :
							if ( cnt35 >= 1 ) break loop35;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(35, input);
							throw eee;
						}
						cnt35++;
					}

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 344:19: -> NO_SPACE[$text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(NO_SPACE, input.toString(retval.start,input.LT(-1))));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:345:4: lookup
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_lookup_in_term1756);
					lookup214=lookup();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, lookup214.getTree());

					}
					break;
				case 9 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:346:4: Empty
					{
					root_0 = (CommonTree)adaptor.nil();


					Empty215=(Token)match(input,Empty,FOLLOW_Empty_in_term1761); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Empty215_tree = (CommonTree)adaptor.create(Empty215);
					adaptor.addChild(root_0, Empty215_tree);
					}

					}
					break;
				case 10 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:347:4: OPar expr CPar
					{
					OPar216=(Token)match(input,OPar,FOLLOW_OPar_in_term1766); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OPar.add(OPar216);

					pushFollow(FOLLOW_expr_in_term1768);
					expr217=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr217.getTree());
					CPar218=(Token)match(input,CPar,FOLLOW_CPar_in_term1770); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CPar.add(CPar218);

					// AST REWRITE
					// elements: expr
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 347:19: -> expr
					{
						adaptor.addChild(root_0, stream_expr.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "term"


	public static class lookup_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "lookup"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:350:1: lookup : ( id ( index )* ( QMark )? -> ^( LOOKUP id ( index )* ( QMark )? ) | OBr Str CBr ( QMark )? -> ^( LOOKUP Id[$Str.text] ( QMark )? ) | OBr Id CBr ( QMark )? -> ^( LOOKUP Id[\"@\" + $Id.text] ( QMark )? ) );
	public final LiquidParser.lookup_return lookup() throws RecognitionException {
		LiquidParser.lookup_return retval = new LiquidParser.lookup_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token QMark221=null;
		Token OBr222=null;
		Token Str223=null;
		Token CBr224=null;
		Token QMark225=null;
		Token OBr226=null;
		Token Id227=null;
		Token CBr228=null;
		Token QMark229=null;
		ParserRuleReturnScope id219 =null;
		ParserRuleReturnScope index220 =null;

		CommonTree QMark221_tree=null;
		CommonTree OBr222_tree=null;
		CommonTree Str223_tree=null;
		CommonTree CBr224_tree=null;
		CommonTree QMark225_tree=null;
		CommonTree OBr226_tree=null;
		CommonTree Id227_tree=null;
		CommonTree CBr228_tree=null;
		CommonTree QMark229_tree=null;
		RewriteRuleTokenStream stream_Str=new RewriteRuleTokenStream(adaptor,"token Str");
		RewriteRuleTokenStream stream_CBr=new RewriteRuleTokenStream(adaptor,"token CBr");
		RewriteRuleTokenStream stream_QMark=new RewriteRuleTokenStream(adaptor,"token QMark");
		RewriteRuleTokenStream stream_Id=new RewriteRuleTokenStream(adaptor,"token Id");
		RewriteRuleTokenStream stream_OBr=new RewriteRuleTokenStream(adaptor,"token OBr");
		RewriteRuleSubtreeStream stream_index=new RewriteRuleSubtreeStream(adaptor,"rule index");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:2: ( id ( index )* ( QMark )? -> ^( LOOKUP id ( index )* ( QMark )? ) | OBr Str CBr ( QMark )? -> ^( LOOKUP Id[$Str.text] ( QMark )? ) | OBr Id CBr ( QMark )? -> ^( LOOKUP Id[\"@\" + $Id.text] ( QMark )? ) )
			int alt41=3;
			int LA41_0 = input.LA(1);
			if ( (LA41_0==Continue||LA41_0==Id) ) {
				alt41=1;
			}
			else if ( (LA41_0==OBr) ) {
				int LA41_2 = input.LA(2);
				if ( (LA41_2==Str) ) {
					alt41=2;
				}
				else if ( (LA41_2==Id) ) {
					alt41=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 41, 0, input);
				throw nvae;
			}

			switch (alt41) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:4: id ( index )* ( QMark )?
					{
					pushFollow(FOLLOW_id_in_lookup1785);
					id219=id();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_id.add(id219.getTree());
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:7: ( index )*
					loop37:
					while (true) {
						int alt37=2;
						int LA37_0 = input.LA(1);
						if ( (LA37_0==Dot||LA37_0==OBr) ) {
							alt37=1;
						}

						switch (alt37) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:7: index
							{
							pushFollow(FOLLOW_index_in_lookup1787);
							index220=index();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_index.add(index220.getTree());
							}
							break;

						default :
							break loop37;
						}
					}

					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:14: ( QMark )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==QMark) ) {
						alt38=1;
					}
					switch (alt38) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:14: QMark
							{
							QMark221=(Token)match(input,QMark,FOLLOW_QMark_in_lookup1790); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_QMark.add(QMark221);

							}
							break;

					}

					// AST REWRITE
					// elements: id, QMark, index
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 351:23: -> ^( LOOKUP id ( index )* ( QMark )? )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:26: ^( LOOKUP id ( index )* ( QMark )? )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LOOKUP, "LOOKUP"), root_1);
						adaptor.addChild(root_1, stream_id.nextTree());
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:38: ( index )*
						while ( stream_index.hasNext() ) {
							adaptor.addChild(root_1, stream_index.nextTree());
						}
						stream_index.reset();

						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:351:45: ( QMark )?
						if ( stream_QMark.hasNext() ) {
							adaptor.addChild(root_1, stream_QMark.nextNode());
						}
						stream_QMark.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:352:4: OBr Str CBr ( QMark )?
					{
					OBr222=(Token)match(input,OBr,FOLLOW_OBr_in_lookup1812); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OBr.add(OBr222);

					Str223=(Token)match(input,Str,FOLLOW_Str_in_lookup1814); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Str.add(Str223);

					CBr224=(Token)match(input,CBr,FOLLOW_CBr_in_lookup1816); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CBr.add(CBr224);

					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:352:16: ( QMark )?
					int alt39=2;
					int LA39_0 = input.LA(1);
					if ( (LA39_0==QMark) ) {
						alt39=1;
					}
					switch (alt39) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:352:16: QMark
							{
							QMark225=(Token)match(input,QMark,FOLLOW_QMark_in_lookup1818); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_QMark.add(QMark225);

							}
							break;

					}

					// AST REWRITE
					// elements: QMark
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 352:23: -> ^( LOOKUP Id[$Str.text] ( QMark )? )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:352:26: ^( LOOKUP Id[$Str.text] ( QMark )? )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LOOKUP, "LOOKUP"), root_1);
						adaptor.addChild(root_1, (CommonTree)adaptor.create(Id, (Str223!=null?Str223.getText():null)));
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:352:49: ( QMark )?
						if ( stream_QMark.hasNext() ) {
							adaptor.addChild(root_1, stream_QMark.nextNode());
						}
						stream_QMark.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:353:4: OBr Id CBr ( QMark )?
					{
					OBr226=(Token)match(input,OBr,FOLLOW_OBr_in_lookup1836); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OBr.add(OBr226);

					Id227=(Token)match(input,Id,FOLLOW_Id_in_lookup1838); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Id.add(Id227);

					CBr228=(Token)match(input,CBr,FOLLOW_CBr_in_lookup1840); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CBr.add(CBr228);

					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:353:15: ( QMark )?
					int alt40=2;
					int LA40_0 = input.LA(1);
					if ( (LA40_0==QMark) ) {
						alt40=1;
					}
					switch (alt40) {
						case 1 :
							// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:353:15: QMark
							{
							QMark229=(Token)match(input,QMark,FOLLOW_QMark_in_lookup1842); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_QMark.add(QMark229);

							}
							break;

					}

					// AST REWRITE
					// elements: QMark, Id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 353:23: -> ^( LOOKUP Id[\"@\" + $Id.text] ( QMark )? )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:353:26: ^( LOOKUP Id[\"@\" + $Id.text] ( QMark )? )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(LOOKUP, "LOOKUP"), root_1);
						adaptor.addChild(root_1, (CommonTree)adaptor.create(Id, "@" + (Id227!=null?Id227.getText():null)));
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:353:54: ( QMark )?
						if ( stream_QMark.hasNext() ) {
							adaptor.addChild(root_1, stream_QMark.nextNode());
						}
						stream_QMark.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lookup"


	public static class id_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "id"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:356:1: id : ( Id | Continue -> Id[$Continue.text] );
	public final LiquidParser.id_return id() throws RecognitionException {
		LiquidParser.id_return retval = new LiquidParser.id_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Id230=null;
		Token Continue231=null;

		CommonTree Id230_tree=null;
		CommonTree Continue231_tree=null;
		RewriteRuleTokenStream stream_Continue=new RewriteRuleTokenStream(adaptor,"token Continue");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:357:2: ( Id | Continue -> Id[$Continue.text] )
			int alt42=2;
			int LA42_0 = input.LA(1);
			if ( (LA42_0==Id) ) {
				alt42=1;
			}
			else if ( (LA42_0==Continue) ) {
				alt42=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 42, 0, input);
				throw nvae;
			}

			switch (alt42) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:357:4: Id
					{
					root_0 = (CommonTree)adaptor.nil();


					Id230=(Token)match(input,Id,FOLLOW_Id_in_id1867); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					Id230_tree = (CommonTree)adaptor.create(Id230);
					adaptor.addChild(root_0, Id230_tree);
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:358:4: Continue
					{
					Continue231=(Token)match(input,Continue,FOLLOW_Continue_in_id1872); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Continue.add(Continue231);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 358:13: -> Id[$Continue.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Continue231!=null?Continue231.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "id"


	public static class id2_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "id2"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:361:1: id2 : ( id | Empty -> Id[$Empty.text] | CaptureStart -> Id[$CaptureStart.text] | CaptureEnd -> Id[$CaptureEnd.text] | CommentStart -> Id[$CommentStart.text] | CommentEnd -> Id[$CommentEnd.text] | RawStart -> Id[$RawStart.text] | RawEnd -> Id[$RawEnd.text] | IfStart -> Id[$IfStart.text] | Elsif -> Id[$Elsif.text] | IfEnd -> Id[$IfEnd.text] | UnlessStart -> Id[$UnlessStart.text] | UnlessEnd -> Id[$UnlessEnd.text] | Else -> Id[$Else.text] | Contains -> Id[$Contains.text] | CaseStart -> Id[$CaseStart.text] | CaseEnd -> Id[$CaseEnd.text] | When -> Id[$When.text] | Cycle -> Id[$Cycle.text] | ForStart -> Id[$ForStart.text] | ForEnd -> Id[$ForEnd.text] | In -> Id[$In.text] | And -> Id[$And.text] | Or -> Id[$Or.text] | TableStart -> Id[$TableStart.text] | TableEnd -> Id[$TableEnd.text] | Assign -> Id[$Assign.text] | True -> Id[$True.text] | False -> Id[$False.text] | Nil -> Id[$Nil.text] | Include -> Id[$Include.text] | With -> Id[$With.text] | EndId -> Id[$EndId.text] | Break -> Id[$Break.text] );
	public final LiquidParser.id2_return id2() throws RecognitionException {
		LiquidParser.id2_return retval = new LiquidParser.id2_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Empty233=null;
		Token CaptureStart234=null;
		Token CaptureEnd235=null;
		Token CommentStart236=null;
		Token CommentEnd237=null;
		Token RawStart238=null;
		Token RawEnd239=null;
		Token IfStart240=null;
		Token Elsif241=null;
		Token IfEnd242=null;
		Token UnlessStart243=null;
		Token UnlessEnd244=null;
		Token Else245=null;
		Token Contains246=null;
		Token CaseStart247=null;
		Token CaseEnd248=null;
		Token When249=null;
		Token Cycle250=null;
		Token ForStart251=null;
		Token ForEnd252=null;
		Token In253=null;
		Token And254=null;
		Token Or255=null;
		Token TableStart256=null;
		Token TableEnd257=null;
		Token Assign258=null;
		Token True259=null;
		Token False260=null;
		Token Nil261=null;
		Token Include262=null;
		Token With263=null;
		Token EndId264=null;
		Token Break265=null;
		ParserRuleReturnScope id232 =null;

		CommonTree Empty233_tree=null;
		CommonTree CaptureStart234_tree=null;
		CommonTree CaptureEnd235_tree=null;
		CommonTree CommentStart236_tree=null;
		CommonTree CommentEnd237_tree=null;
		CommonTree RawStart238_tree=null;
		CommonTree RawEnd239_tree=null;
		CommonTree IfStart240_tree=null;
		CommonTree Elsif241_tree=null;
		CommonTree IfEnd242_tree=null;
		CommonTree UnlessStart243_tree=null;
		CommonTree UnlessEnd244_tree=null;
		CommonTree Else245_tree=null;
		CommonTree Contains246_tree=null;
		CommonTree CaseStart247_tree=null;
		CommonTree CaseEnd248_tree=null;
		CommonTree When249_tree=null;
		CommonTree Cycle250_tree=null;
		CommonTree ForStart251_tree=null;
		CommonTree ForEnd252_tree=null;
		CommonTree In253_tree=null;
		CommonTree And254_tree=null;
		CommonTree Or255_tree=null;
		CommonTree TableStart256_tree=null;
		CommonTree TableEnd257_tree=null;
		CommonTree Assign258_tree=null;
		CommonTree True259_tree=null;
		CommonTree False260_tree=null;
		CommonTree Nil261_tree=null;
		CommonTree Include262_tree=null;
		CommonTree With263_tree=null;
		CommonTree EndId264_tree=null;
		CommonTree Break265_tree=null;
		RewriteRuleTokenStream stream_Or=new RewriteRuleTokenStream(adaptor,"token Or");
		RewriteRuleTokenStream stream_In=new RewriteRuleTokenStream(adaptor,"token In");
		RewriteRuleTokenStream stream_CaptureStart=new RewriteRuleTokenStream(adaptor,"token CaptureStart");
		RewriteRuleTokenStream stream_RawEnd=new RewriteRuleTokenStream(adaptor,"token RawEnd");
		RewriteRuleTokenStream stream_Break=new RewriteRuleTokenStream(adaptor,"token Break");
		RewriteRuleTokenStream stream_True=new RewriteRuleTokenStream(adaptor,"token True");
		RewriteRuleTokenStream stream_Include=new RewriteRuleTokenStream(adaptor,"token Include");
		RewriteRuleTokenStream stream_EndId=new RewriteRuleTokenStream(adaptor,"token EndId");
		RewriteRuleTokenStream stream_False=new RewriteRuleTokenStream(adaptor,"token False");
		RewriteRuleTokenStream stream_CaptureEnd=new RewriteRuleTokenStream(adaptor,"token CaptureEnd");
		RewriteRuleTokenStream stream_RawStart=new RewriteRuleTokenStream(adaptor,"token RawStart");
		RewriteRuleTokenStream stream_ForStart=new RewriteRuleTokenStream(adaptor,"token ForStart");
		RewriteRuleTokenStream stream_With=new RewriteRuleTokenStream(adaptor,"token With");
		RewriteRuleTokenStream stream_Empty=new RewriteRuleTokenStream(adaptor,"token Empty");
		RewriteRuleTokenStream stream_UnlessStart=new RewriteRuleTokenStream(adaptor,"token UnlessStart");
		RewriteRuleTokenStream stream_Elsif=new RewriteRuleTokenStream(adaptor,"token Elsif");
		RewriteRuleTokenStream stream_UnlessEnd=new RewriteRuleTokenStream(adaptor,"token UnlessEnd");
		RewriteRuleTokenStream stream_IfStart=new RewriteRuleTokenStream(adaptor,"token IfStart");
		RewriteRuleTokenStream stream_CaseEnd=new RewriteRuleTokenStream(adaptor,"token CaseEnd");
		RewriteRuleTokenStream stream_TableStart=new RewriteRuleTokenStream(adaptor,"token TableStart");
		RewriteRuleTokenStream stream_IfEnd=new RewriteRuleTokenStream(adaptor,"token IfEnd");
		RewriteRuleTokenStream stream_CommentStart=new RewriteRuleTokenStream(adaptor,"token CommentStart");
		RewriteRuleTokenStream stream_Cycle=new RewriteRuleTokenStream(adaptor,"token Cycle");
		RewriteRuleTokenStream stream_Nil=new RewriteRuleTokenStream(adaptor,"token Nil");
		RewriteRuleTokenStream stream_When=new RewriteRuleTokenStream(adaptor,"token When");
		RewriteRuleTokenStream stream_CommentEnd=new RewriteRuleTokenStream(adaptor,"token CommentEnd");
		RewriteRuleTokenStream stream_ForEnd=new RewriteRuleTokenStream(adaptor,"token ForEnd");
		RewriteRuleTokenStream stream_And=new RewriteRuleTokenStream(adaptor,"token And");
		RewriteRuleTokenStream stream_TableEnd=new RewriteRuleTokenStream(adaptor,"token TableEnd");
		RewriteRuleTokenStream stream_Else=new RewriteRuleTokenStream(adaptor,"token Else");
		RewriteRuleTokenStream stream_Contains=new RewriteRuleTokenStream(adaptor,"token Contains");
		RewriteRuleTokenStream stream_CaseStart=new RewriteRuleTokenStream(adaptor,"token CaseStart");
		RewriteRuleTokenStream stream_Assign=new RewriteRuleTokenStream(adaptor,"token Assign");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:362:2: ( id | Empty -> Id[$Empty.text] | CaptureStart -> Id[$CaptureStart.text] | CaptureEnd -> Id[$CaptureEnd.text] | CommentStart -> Id[$CommentStart.text] | CommentEnd -> Id[$CommentEnd.text] | RawStart -> Id[$RawStart.text] | RawEnd -> Id[$RawEnd.text] | IfStart -> Id[$IfStart.text] | Elsif -> Id[$Elsif.text] | IfEnd -> Id[$IfEnd.text] | UnlessStart -> Id[$UnlessStart.text] | UnlessEnd -> Id[$UnlessEnd.text] | Else -> Id[$Else.text] | Contains -> Id[$Contains.text] | CaseStart -> Id[$CaseStart.text] | CaseEnd -> Id[$CaseEnd.text] | When -> Id[$When.text] | Cycle -> Id[$Cycle.text] | ForStart -> Id[$ForStart.text] | ForEnd -> Id[$ForEnd.text] | In -> Id[$In.text] | And -> Id[$And.text] | Or -> Id[$Or.text] | TableStart -> Id[$TableStart.text] | TableEnd -> Id[$TableEnd.text] | Assign -> Id[$Assign.text] | True -> Id[$True.text] | False -> Id[$False.text] | Nil -> Id[$Nil.text] | Include -> Id[$Include.text] | With -> Id[$With.text] | EndId -> Id[$EndId.text] | Break -> Id[$Break.text] )
			int alt43=34;
			switch ( input.LA(1) ) {
			case Continue:
			case Id:
				{
				alt43=1;
				}
				break;
			case Empty:
				{
				alt43=2;
				}
				break;
			case CaptureStart:
				{
				alt43=3;
				}
				break;
			case CaptureEnd:
				{
				alt43=4;
				}
				break;
			case CommentStart:
				{
				alt43=5;
				}
				break;
			case CommentEnd:
				{
				alt43=6;
				}
				break;
			case RawStart:
				{
				alt43=7;
				}
				break;
			case RawEnd:
				{
				alt43=8;
				}
				break;
			case IfStart:
				{
				alt43=9;
				}
				break;
			case Elsif:
				{
				alt43=10;
				}
				break;
			case IfEnd:
				{
				alt43=11;
				}
				break;
			case UnlessStart:
				{
				alt43=12;
				}
				break;
			case UnlessEnd:
				{
				alt43=13;
				}
				break;
			case Else:
				{
				alt43=14;
				}
				break;
			case Contains:
				{
				alt43=15;
				}
				break;
			case CaseStart:
				{
				alt43=16;
				}
				break;
			case CaseEnd:
				{
				alt43=17;
				}
				break;
			case When:
				{
				alt43=18;
				}
				break;
			case Cycle:
				{
				alt43=19;
				}
				break;
			case ForStart:
				{
				alt43=20;
				}
				break;
			case ForEnd:
				{
				alt43=21;
				}
				break;
			case In:
				{
				alt43=22;
				}
				break;
			case And:
				{
				alt43=23;
				}
				break;
			case Or:
				{
				alt43=24;
				}
				break;
			case TableStart:
				{
				alt43=25;
				}
				break;
			case TableEnd:
				{
				alt43=26;
				}
				break;
			case Assign:
				{
				alt43=27;
				}
				break;
			case True:
				{
				alt43=28;
				}
				break;
			case False:
				{
				alt43=29;
				}
				break;
			case Nil:
				{
				alt43=30;
				}
				break;
			case Include:
				{
				alt43=31;
				}
				break;
			case With:
				{
				alt43=32;
				}
				break;
			case EndId:
				{
				alt43=33;
				}
				break;
			case Break:
				{
				alt43=34;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 43, 0, input);
				throw nvae;
			}
			switch (alt43) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:362:4: id
					{
					root_0 = (CommonTree)adaptor.nil();


					pushFollow(FOLLOW_id_in_id21888);
					id232=id();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, id232.getTree());

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:363:4: Empty
					{
					Empty233=(Token)match(input,Empty,FOLLOW_Empty_in_id21893); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Empty.add(Empty233);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 363:17: -> Id[$Empty.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Empty233!=null?Empty233.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:364:4: CaptureStart
					{
					CaptureStart234=(Token)match(input,CaptureStart,FOLLOW_CaptureStart_in_id21910); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaptureStart.add(CaptureStart234);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 364:17: -> Id[$CaptureStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CaptureStart234!=null?CaptureStart234.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:365:4: CaptureEnd
					{
					CaptureEnd235=(Token)match(input,CaptureEnd,FOLLOW_CaptureEnd_in_id21920); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaptureEnd.add(CaptureEnd235);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 365:17: -> Id[$CaptureEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CaptureEnd235!=null?CaptureEnd235.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:366:4: CommentStart
					{
					CommentStart236=(Token)match(input,CommentStart,FOLLOW_CommentStart_in_id21932); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CommentStart.add(CommentStart236);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 366:17: -> Id[$CommentStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CommentStart236!=null?CommentStart236.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:367:4: CommentEnd
					{
					CommentEnd237=(Token)match(input,CommentEnd,FOLLOW_CommentEnd_in_id21942); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CommentEnd.add(CommentEnd237);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 367:17: -> Id[$CommentEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CommentEnd237!=null?CommentEnd237.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 7 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:368:4: RawStart
					{
					RawStart238=(Token)match(input,RawStart,FOLLOW_RawStart_in_id21954); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RawStart.add(RawStart238);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 368:17: -> Id[$RawStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (RawStart238!=null?RawStart238.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:369:4: RawEnd
					{
					RawEnd239=(Token)match(input,RawEnd,FOLLOW_RawEnd_in_id21968); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RawEnd.add(RawEnd239);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 369:17: -> Id[$RawEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (RawEnd239!=null?RawEnd239.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 9 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:370:4: IfStart
					{
					IfStart240=(Token)match(input,IfStart,FOLLOW_IfStart_in_id21984); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IfStart.add(IfStart240);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 370:17: -> Id[$IfStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (IfStart240!=null?IfStart240.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 10 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:371:4: Elsif
					{
					Elsif241=(Token)match(input,Elsif,FOLLOW_Elsif_in_id21999); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Elsif.add(Elsif241);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 371:17: -> Id[$Elsif.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Elsif241!=null?Elsif241.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 11 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:372:4: IfEnd
					{
					IfEnd242=(Token)match(input,IfEnd,FOLLOW_IfEnd_in_id22016); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_IfEnd.add(IfEnd242);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 372:17: -> Id[$IfEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (IfEnd242!=null?IfEnd242.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 12 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:373:4: UnlessStart
					{
					UnlessStart243=(Token)match(input,UnlessStart,FOLLOW_UnlessStart_in_id22033); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_UnlessStart.add(UnlessStart243);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 373:17: -> Id[$UnlessStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (UnlessStart243!=null?UnlessStart243.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 13 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:374:4: UnlessEnd
					{
					UnlessEnd244=(Token)match(input,UnlessEnd,FOLLOW_UnlessEnd_in_id22044); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_UnlessEnd.add(UnlessEnd244);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 374:17: -> Id[$UnlessEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (UnlessEnd244!=null?UnlessEnd244.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 14 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:375:4: Else
					{
					Else245=(Token)match(input,Else,FOLLOW_Else_in_id22057); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Else.add(Else245);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 375:17: -> Id[$Else.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Else245!=null?Else245.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 15 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:376:4: Contains
					{
					Contains246=(Token)match(input,Contains,FOLLOW_Contains_in_id22075); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Contains.add(Contains246);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 376:17: -> Id[$Contains.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Contains246!=null?Contains246.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 16 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:377:4: CaseStart
					{
					CaseStart247=(Token)match(input,CaseStart,FOLLOW_CaseStart_in_id22089); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaseStart.add(CaseStart247);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 377:17: -> Id[$CaseStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CaseStart247!=null?CaseStart247.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 17 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:378:4: CaseEnd
					{
					CaseEnd248=(Token)match(input,CaseEnd,FOLLOW_CaseEnd_in_id22102); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CaseEnd.add(CaseEnd248);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 378:17: -> Id[$CaseEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (CaseEnd248!=null?CaseEnd248.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 18 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:379:4: When
					{
					When249=(Token)match(input,When,FOLLOW_When_in_id22117); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_When.add(When249);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 379:17: -> Id[$When.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (When249!=null?When249.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 19 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:380:4: Cycle
					{
					Cycle250=(Token)match(input,Cycle,FOLLOW_Cycle_in_id22135); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Cycle.add(Cycle250);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 380:17: -> Id[$Cycle.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Cycle250!=null?Cycle250.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 20 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:381:4: ForStart
					{
					ForStart251=(Token)match(input,ForStart,FOLLOW_ForStart_in_id22152); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ForStart.add(ForStart251);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 381:17: -> Id[$ForStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (ForStart251!=null?ForStart251.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 21 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:382:4: ForEnd
					{
					ForEnd252=(Token)match(input,ForEnd,FOLLOW_ForEnd_in_id22166); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ForEnd.add(ForEnd252);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 382:17: -> Id[$ForEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (ForEnd252!=null?ForEnd252.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 22 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:383:4: In
					{
					In253=(Token)match(input,In,FOLLOW_In_in_id22182); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_In.add(In253);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 383:17: -> Id[$In.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (In253!=null?In253.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 23 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:384:4: And
					{
					And254=(Token)match(input,And,FOLLOW_And_in_id22202); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_And.add(And254);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 384:17: -> Id[$And.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (And254!=null?And254.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 24 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:385:4: Or
					{
					Or255=(Token)match(input,Or,FOLLOW_Or_in_id22221); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Or.add(Or255);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 385:17: -> Id[$Or.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Or255!=null?Or255.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 25 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:386:4: TableStart
					{
					TableStart256=(Token)match(input,TableStart,FOLLOW_TableStart_in_id22241); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TableStart.add(TableStart256);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 386:17: -> Id[$TableStart.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (TableStart256!=null?TableStart256.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 26 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:387:4: TableEnd
					{
					TableEnd257=(Token)match(input,TableEnd,FOLLOW_TableEnd_in_id22253); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TableEnd.add(TableEnd257);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 387:17: -> Id[$TableEnd.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (TableEnd257!=null?TableEnd257.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 27 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:388:4: Assign
					{
					Assign258=(Token)match(input,Assign,FOLLOW_Assign_in_id22267); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Assign.add(Assign258);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 388:17: -> Id[$Assign.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Assign258!=null?Assign258.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 28 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:389:4: True
					{
					True259=(Token)match(input,True,FOLLOW_True_in_id22283); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_True.add(True259);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 389:17: -> Id[$True.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (True259!=null?True259.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 29 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:390:4: False
					{
					False260=(Token)match(input,False,FOLLOW_False_in_id22301); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_False.add(False260);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 390:17: -> Id[$False.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (False260!=null?False260.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 30 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:391:4: Nil
					{
					Nil261=(Token)match(input,Nil,FOLLOW_Nil_in_id22318); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Nil.add(Nil261);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 391:17: -> Id[$Nil.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Nil261!=null?Nil261.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 31 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:392:4: Include
					{
					Include262=(Token)match(input,Include,FOLLOW_Include_in_id22337); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Include.add(Include262);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 392:17: -> Id[$Include.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Include262!=null?Include262.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 32 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:393:4: With
					{
					With263=(Token)match(input,With,FOLLOW_With_in_id22352); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_With.add(With263);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 393:17: -> Id[$With.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (With263!=null?With263.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 33 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:394:4: EndId
					{
					EndId264=(Token)match(input,EndId,FOLLOW_EndId_in_id22370); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_EndId.add(EndId264);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 394:17: -> Id[$EndId.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (EndId264!=null?EndId264.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 34 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:395:4: Break
					{
					Break265=(Token)match(input,Break,FOLLOW_Break_in_id22387); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Break.add(Break265);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 395:17: -> Id[$Break.text]
					{
						adaptor.addChild(root_0, (CommonTree)adaptor.create(Id, (Break265!=null?Break265.getText():null)));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "id2"


	public static class index_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "index"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:398:1: index : ( Dot id2 -> ^( HASH id2 ) | OBr expr CBr -> ^( INDEX expr ) );
	public final LiquidParser.index_return index() throws RecognitionException {
		LiquidParser.index_return retval = new LiquidParser.index_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token Dot266=null;
		Token OBr268=null;
		Token CBr270=null;
		ParserRuleReturnScope id2267 =null;
		ParserRuleReturnScope expr269 =null;

		CommonTree Dot266_tree=null;
		CommonTree OBr268_tree=null;
		CommonTree CBr270_tree=null;
		RewriteRuleTokenStream stream_CBr=new RewriteRuleTokenStream(adaptor,"token CBr");
		RewriteRuleTokenStream stream_Dot=new RewriteRuleTokenStream(adaptor,"token Dot");
		RewriteRuleTokenStream stream_OBr=new RewriteRuleTokenStream(adaptor,"token OBr");
		RewriteRuleSubtreeStream stream_id2=new RewriteRuleSubtreeStream(adaptor,"rule id2");
		RewriteRuleSubtreeStream stream_expr=new RewriteRuleSubtreeStream(adaptor,"rule expr");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:399:2: ( Dot id2 -> ^( HASH id2 ) | OBr expr CBr -> ^( INDEX expr ) )
			int alt44=2;
			int LA44_0 = input.LA(1);
			if ( (LA44_0==Dot) ) {
				alt44=1;
			}
			else if ( (LA44_0==OBr) ) {
				alt44=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}

			switch (alt44) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:399:4: Dot id2
					{
					Dot266=(Token)match(input,Dot,FOLLOW_Dot_in_index2410); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_Dot.add(Dot266);

					pushFollow(FOLLOW_id2_in_index2412);
					id2267=id2();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_id2.add(id2267.getTree());
					// AST REWRITE
					// elements: id2
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 399:17: -> ^( HASH id2 )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:399:20: ^( HASH id2 )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(HASH, "HASH"), root_1);
						adaptor.addChild(root_1, stream_id2.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:400:4: OBr expr CBr
					{
					OBr268=(Token)match(input,OBr,FOLLOW_OBr_in_index2430); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OBr.add(OBr268);

					pushFollow(FOLLOW_expr_in_index2432);
					expr269=expr();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_expr.add(expr269.getTree());
					CBr270=(Token)match(input,CBr,FOLLOW_CBr_in_index2434); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_CBr.add(CBr270);

					// AST REWRITE
					// elements: expr
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (CommonTree)adaptor.nil();
					// 400:17: -> ^( INDEX expr )
					{
						// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:400:20: ^( INDEX expr )
						{
						CommonTree root_1 = (CommonTree)adaptor.nil();
						root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INDEX, "INDEX"), root_1);
						adaptor.addChild(root_1, stream_expr.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "index"


	public static class file_name_as_str_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "file_name_as_str"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:403:1: file_name_as_str : other_than_tag_end -> Str[$text] ;
	public final LiquidParser.file_name_as_str_return file_name_as_str() throws RecognitionException {
		LiquidParser.file_name_as_str_return retval = new LiquidParser.file_name_as_str_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope other_than_tag_end271 =null;

		RewriteRuleSubtreeStream stream_other_than_tag_end=new RewriteRuleSubtreeStream(adaptor,"rule other_than_tag_end");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:404:2: ( other_than_tag_end -> Str[$text] )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:404:4: other_than_tag_end
			{
			pushFollow(FOLLOW_other_than_tag_end_in_file_name_as_str2453);
			other_than_tag_end271=other_than_tag_end();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_other_than_tag_end.add(other_than_tag_end271.getTree());
			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 404:23: -> Str[$text]
			{
				adaptor.addChild(root_0, (CommonTree)adaptor.create(Str, input.toString(retval.start,input.LT(-1))));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "file_name_as_str"


	public static class custom_tag_parameters_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "custom_tag_parameters"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:407:1: custom_tag_parameters : other_than_tag_end -> Str[$text] ;
	public final LiquidParser.custom_tag_parameters_return custom_tag_parameters() throws RecognitionException {
		LiquidParser.custom_tag_parameters_return retval = new LiquidParser.custom_tag_parameters_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		ParserRuleReturnScope other_than_tag_end272 =null;

		RewriteRuleSubtreeStream stream_other_than_tag_end=new RewriteRuleSubtreeStream(adaptor,"rule other_than_tag_end");

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:408:2: ( other_than_tag_end -> Str[$text] )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:408:4: other_than_tag_end
			{
			pushFollow(FOLLOW_other_than_tag_end_in_custom_tag_parameters2469);
			other_than_tag_end272=other_than_tag_end();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_other_than_tag_end.add(other_than_tag_end272.getTree());
			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (CommonTree)adaptor.nil();
			// 408:23: -> Str[$text]
			{
				adaptor.addChild(root_0, (CommonTree)adaptor.create(Str, input.toString(retval.start,input.LT(-1))));
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "custom_tag_parameters"


	public static class other_than_tag_end_return extends ParserRuleReturnScope {
		CommonTree tree;
		@Override
		public CommonTree getTree() { return tree; }
	};


	// $ANTLR start "other_than_tag_end"
	// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:411:1: other_than_tag_end : (~ TagEnd )+ ;
	public final LiquidParser.other_than_tag_end_return other_than_tag_end() throws RecognitionException {
		LiquidParser.other_than_tag_end_return retval = new LiquidParser.other_than_tag_end_return();
		retval.start = input.LT(1);

		CommonTree root_0 = null;

		Token set273=null;

		CommonTree set273_tree=null;

		try {
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:412:2: ( (~ TagEnd )+ )
			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:412:4: (~ TagEnd )+
			{
			root_0 = (CommonTree)adaptor.nil();


			// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:412:4: (~ TagEnd )+
			int cnt45=0;
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( ((LA45_0 >= ASSIGNMENT && LA45_0 <= TableStart)||(LA45_0 >= TagEndDefaultStrip && LA45_0 <= With)) ) {
					alt45=1;
				}

				switch (alt45) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:
					{
					set273=input.LT(1);
					if ( (input.LA(1) >= ASSIGNMENT && input.LA(1) <= TableStart)||(input.LA(1) >= TagEndDefaultStrip && input.LA(1) <= With) ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set273));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt45 >= 1 ) break loop45;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(45, input);
					throw eee;
				}
				cnt45++;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "other_than_tag_end"

	// $ANTLR start synpred1_Liquid
	public final void synpred1_Liquid_fragment() throws RecognitionException {
		// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:5: ( custom_tag_block )
		// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:186:6: custom_tag_block
		{
		pushFollow(FOLLOW_custom_tag_block_in_synpred1_Liquid412);
		custom_tag_block();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_Liquid

	// $ANTLR start synpred2_Liquid
	public final void synpred2_Liquid_fragment() throws RecognitionException {
		// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:5: ( expr Col )
		// org.eclipse.embedcdt.core.liqp/parser/Liquid.g:242:6: expr Col
		{
		pushFollow(FOLLOW_expr_in_synpred2_Liquid860);
		expr();
		state._fsp--;
		if (state.failed) return;

		match(input,Col,FOLLOW_Col_in_synpred2_Liquid862); if (state.failed) return;

		}

	}
	// $ANTLR end synpred2_Liquid

	// Delegated rules

	public final boolean synpred2_Liquid() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_Liquid_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_Liquid() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_Liquid_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}



	public static final BitSet FOLLOW_block_in_parse231 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_parse233 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_block261 = new BitSet(new long[]{0x0000000000000002L,0x0000000400011000L});
	public static final BitSet FOLLOW_tag_in_atom283 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_output_in_atom288 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignment_in_atom293 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Other_in_atom298 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_custom_tag_in_tag314 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_raw_tag_in_tag319 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comment_tag_in_tag324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_if_tag_in_tag329 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unless_tag_in_tag334 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_case_tag_in_tag339 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cycle_tag_in_tag344 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_for_tag_in_tag349 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_table_tag_in_tag354 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_capture_tag_in_tag359 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_include_tag_in_tag364 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_break_tag_in_tag369 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_continue_tag_in_tag374 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_custom_tag386 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_custom_tag388 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFFFFFFFFFFL});
	public static final BitSet FOLLOW_custom_tag_parameters_in_custom_tag390 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_custom_tag393 = new BitSet(new long[]{0x0000000000000002L,0x0000000400011000L});
	public static final BitSet FOLLOW_custom_tag_block_in_custom_tag416 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_custom_tag_block455 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_TagStart_in_custom_tag_block459 = new BitSet(new long[]{0x0000008000000000L});
	public static final BitSet FOLLOW_EndId_in_custom_tag_block461 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_custom_tag_block463 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_raw_tag483 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
	public static final BitSet FOLLOW_RawStart_in_raw_tag485 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_raw_tag487 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFBFFFFFFFFL});
	public static final BitSet FOLLOW_raw_body_in_raw_tag489 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_raw_tag491 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
	public static final BitSet FOLLOW_RawEnd_in_raw_tag493 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_raw_tag495 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_other_than_tag_start_in_raw_body510 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_comment_tag526 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_CommentStart_in_comment_tag528 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_comment_tag530 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFBFFFFFFFFL});
	public static final BitSet FOLLOW_comment_body_in_comment_tag532 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_comment_tag534 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_CommentEnd_in_comment_tag536 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_comment_tag538 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_other_than_tag_start_in_comment_body553 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_if_tag582 = new BitSet(new long[]{0x0800000000000000L});
	public static final BitSet FOLLOW_IfStart_in_if_tag584 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_if_tag586 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_if_tag588 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_if_tag590 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_elsif_tag_in_if_tag592 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_else_tag_in_if_tag595 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_if_tag598 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_IfEnd_in_if_tag600 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_if_tag602 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_elsif_tag633 = new BitSet(new long[]{0x0000002000000000L});
	public static final BitSet FOLLOW_Elsif_in_elsif_tag635 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_elsif_tag637 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_elsif_tag639 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_elsif_tag641 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_else_tag662 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_Else_in_else_tag664 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_else_tag666 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_else_tag668 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_unless_tag683 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
	public static final BitSet FOLLOW_UnlessStart_in_unless_tag685 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_unless_tag687 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_unless_tag689 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_unless_tag691 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_else_tag_in_unless_tag693 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_unless_tag696 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
	public static final BitSet FOLLOW_UnlessEnd_in_unless_tag698 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_unless_tag700 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_case_tag728 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_CaseStart_in_case_tag730 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_case_tag732 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_case_tag734 = new BitSet(new long[]{0x0000000000000000L,0x0000000400001000L});
	public static final BitSet FOLLOW_Other_in_case_tag736 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_when_tag_in_case_tag739 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_else_tag_in_case_tag742 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_case_tag745 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_CaseEnd_in_case_tag747 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_case_tag749 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_when_tag778 = new BitSet(new long[]{0x0000000000000000L,0x0000100000000000L});
	public static final BitSet FOLLOW_When_in_when_tag780 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_term_in_when_tag782 = new BitSet(new long[]{0x0000000000800000L,0x0000000080000800L});
	public static final BitSet FOLLOW_Or_in_when_tag786 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_Comma_in_when_tag790 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_term_in_when_tag793 = new BitSet(new long[]{0x0000000000800000L,0x0000000080000800L});
	public static final BitSet FOLLOW_TagEnd_in_when_tag797 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_when_tag799 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_cycle_tag821 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_Cycle_in_cycle_tag823 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_cycle_group_in_cycle_tag825 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_cycle_tag827 = new BitSet(new long[]{0x0000000000800000L,0x0000000080000000L});
	public static final BitSet FOLLOW_Comma_in_cycle_tag830 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_cycle_tag832 = new BitSet(new long[]{0x0000000000800000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_cycle_tag836 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_cycle_group866 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_Col_in_cycle_group868 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_for_array_in_for_tag890 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_for_range_in_for_tag895 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_for_array910 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_ForStart_in_for_array912 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_for_array914 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_In_in_for_array916 = new BitSet(new long[]{0x0200000008000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_lookup_in_for_array918 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_attribute_in_for_array920 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_for_array923 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_for_block_in_for_array928 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_for_array933 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_ForEnd_in_for_array935 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_for_array937 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_for_range970 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_ForStart_in_for_range972 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_for_range974 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_In_in_for_range976 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_OPar_in_for_range978 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_for_range980 = new BitSet(new long[]{0x0000000100000000L});
	public static final BitSet FOLLOW_DotDot_in_for_range982 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_for_range984 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_CPar_in_for_range986 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_attribute_in_for_range988 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_for_range991 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_for_range996 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_for_range1001 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_ForEnd_in_for_range1003 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_for_range1005 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_for_block1042 = new BitSet(new long[]{0x0000000000000002L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_for_block1045 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_Else_in_for_block1047 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_for_block1049 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_for_block1053 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Id_in_attribute1077 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_Col_in_attribute1079 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_attribute1081 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_table_tag1100 = new BitSet(new long[]{0x0000000000000000L,0x0000000040000000L});
	public static final BitSet FOLLOW_TableStart_in_table_tag1102 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_table_tag1104 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_In_in_table_tag1106 = new BitSet(new long[]{0x0200000008000000L,0x0000000000000100L});
	public static final BitSet FOLLOW_lookup_in_table_tag1108 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_attribute_in_table_tag1110 = new BitSet(new long[]{0x0200000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_table_tag1113 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_table_tag1115 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_table_tag1117 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_TableEnd_in_table_tag1119 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_table_tag1121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_capture_tag1151 = new BitSet(new long[]{0x0000000000080000L});
	public static final BitSet FOLLOW_CaptureStart_in_capture_tag1153 = new BitSet(new long[]{0x0200000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_Id_in_capture_tag1157 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_capture_tag1159 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_capture_tag1161 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_capture_tag1163 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_CaptureEnd_in_capture_tag1165 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_capture_tag1167 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Str_in_capture_tag1207 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_capture_tag1209 = new BitSet(new long[]{0x0000000000000000L,0x0000000400011000L});
	public static final BitSet FOLLOW_block_in_capture_tag1211 = new BitSet(new long[]{0x0000000000000000L,0x0000000400000000L});
	public static final BitSet FOLLOW_TagStart_in_capture_tag1213 = new BitSet(new long[]{0x0000000000040000L});
	public static final BitSet FOLLOW_CaptureEnd_in_capture_tag1215 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_capture_tag1217 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_include_tag1266 = new BitSet(new long[]{0x2000000000000000L});
	public static final BitSet FOLLOW_Include_in_include_tag1268 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00007FFF7FFFFFFFL});
	public static final BitSet FOLLOW_file_name_as_str_in_include_tag1297 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_include_tag1299 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Str_in_include_tag1344 = new BitSet(new long[]{0x0000000000000000L,0x0000400080000000L});
	public static final BitSet FOLLOW_With_in_include_tag1347 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_Str_in_include_tag1351 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_include_tag1355 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_break_tag1419 = new BitSet(new long[]{0x0000000000000200L});
	public static final BitSet FOLLOW_Break_in_break_tag1421 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_break_tag1423 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TagStart_in_continue_tag1438 = new BitSet(new long[]{0x0000000008000000L});
	public static final BitSet FOLLOW_Continue_in_continue_tag1440 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_continue_tag1442 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OutStart_in_output1457 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_output1459 = new BitSet(new long[]{0x0000000000000000L,0x0000000000202000L});
	public static final BitSet FOLLOW_filter_in_output1461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000202000L});
	public static final BitSet FOLLOW_OutEnd_in_output1464 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Pipe_in_filter1490 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_filter1492 = new BitSet(new long[]{0x0000000000400002L});
	public static final BitSet FOLLOW_params_in_filter1494 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Col_in_params1521 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_params1523 = new BitSet(new long[]{0x0000000000800002L});
	public static final BitSet FOLLOW_Comma_in_params1526 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_params1528 = new BitSet(new long[]{0x0000000000800002L});
	public static final BitSet FOLLOW_TagStart_in_assignment1546 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_Assign_in_assignment1548 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_assignment1550 = new BitSet(new long[]{0x0000020000000000L});
	public static final BitSet FOLLOW_EqSign_in_assignment1552 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_assignment1554 = new BitSet(new long[]{0x0000000000000000L,0x0000000080200000L});
	public static final BitSet FOLLOW_filter_in_assignment1556 = new BitSet(new long[]{0x0000000000000000L,0x0000000080000000L});
	public static final BitSet FOLLOW_TagEnd_in_assignment1559 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_or_expr_in_expr1583 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_and_expr_in_or_expr1594 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
	public static final BitSet FOLLOW_Or_in_or_expr1597 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_and_expr_in_or_expr1600 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
	public static final BitSet FOLLOW_contains_expr_in_and_expr1613 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_And_in_and_expr1616 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_contains_expr_in_and_expr1619 = new BitSet(new long[]{0x0000000000000042L});
	public static final BitSet FOLLOW_eq_expr_in_contains_expr1632 = new BitSet(new long[]{0x0000000004000002L});
	public static final BitSet FOLLOW_Contains_in_contains_expr1635 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_eq_expr_in_contains_expr1638 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_rel_expr_in_eq_expr1651 = new BitSet(new long[]{0x0000010000000002L,0x0000000000000010L});
	public static final BitSet FOLLOW_set_in_eq_expr1654 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_rel_expr_in_eq_expr1663 = new BitSet(new long[]{0x0000010000000002L,0x0000000000000010L});
	public static final BitSet FOLLOW_term_in_rel_expr1676 = new BitSet(new long[]{0x0018000000000002L,0x0000000000000006L});
	public static final BitSet FOLLOW_set_in_rel_expr1679 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_term_in_rel_expr1696 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DoubleNum_in_term1709 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LongNum_in_term1714 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Str_in_term1719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_True_in_term1724 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_False_in_term1729 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Nil_in_term1734 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NoSpace_in_term1739 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
	public static final BitSet FOLLOW_lookup_in_term1756 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Empty_in_term1761 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OPar_in_term1766 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_term1768 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_CPar_in_term1770 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_lookup1785 = new BitSet(new long[]{0x0000000080000002L,0x0000000000400100L});
	public static final BitSet FOLLOW_index_in_lookup1787 = new BitSet(new long[]{0x0000000080000002L,0x0000000000400100L});
	public static final BitSet FOLLOW_QMark_in_lookup1790 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OBr_in_lookup1812 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_Str_in_lookup1814 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_CBr_in_lookup1816 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
	public static final BitSet FOLLOW_QMark_in_lookup1818 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OBr_in_lookup1836 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_Id_in_lookup1838 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_CBr_in_lookup1840 = new BitSet(new long[]{0x0000000000000002L,0x0000000000400000L});
	public static final BitSet FOLLOW_QMark_in_lookup1842 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Id_in_id1867 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Continue_in_id1872 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_id21888 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Empty_in_id21893 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CaptureStart_in_id21910 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CaptureEnd_in_id21920 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CommentStart_in_id21932 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CommentEnd_in_id21942 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RawStart_in_id21954 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RawEnd_in_id21968 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IfStart_in_id21984 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Elsif_in_id21999 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IfEnd_in_id22016 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UnlessStart_in_id22033 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_UnlessEnd_in_id22044 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Else_in_id22057 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Contains_in_id22075 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CaseStart_in_id22089 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CaseEnd_in_id22102 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_When_in_id22117 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Cycle_in_id22135 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ForStart_in_id22152 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ForEnd_in_id22166 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_In_in_id22182 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_And_in_id22202 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Or_in_id22221 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TableStart_in_id22241 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TableEnd_in_id22253 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Assign_in_id22267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_True_in_id22283 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_False_in_id22301 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Nil_in_id22318 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Include_in_id22337 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_With_in_id22352 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EndId_in_id22370 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Break_in_id22387 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Dot_in_index2410 = new BitSet(new long[]{0x3E0380F01F3C02C0L,0x000051A063000840L});
	public static final BitSet FOLLOW_id2_in_index2412 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OBr_in_index2430 = new BitSet(new long[]{0x0200804208000000L,0x00000020080003C1L});
	public static final BitSet FOLLOW_expr_in_index2432 = new BitSet(new long[]{0x0000000000001000L});
	public static final BitSet FOLLOW_CBr_in_index2434 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_other_than_tag_end_in_file_name_as_str2453 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_other_than_tag_end_in_custom_tag_parameters2469 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_custom_tag_block_in_synpred1_Liquid412 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_expr_in_synpred2_Liquid860 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_Col_in_synpred2_Liquid862 = new BitSet(new long[]{0x0000000000000002L});
}
