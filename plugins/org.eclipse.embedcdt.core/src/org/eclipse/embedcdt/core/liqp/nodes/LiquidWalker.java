// $ANTLR 3.5.2 org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g 2020-12-06 19:17:12

  package org.eclipse.embedcdt.core.liqp.nodes;

  import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.eclipse.embedcdt.core.liqp.filters.Filter;
import org.eclipse.embedcdt.core.liqp.parser.Flavor;
import org.eclipse.embedcdt.core.liqp.tags.Tag;

@SuppressWarnings("all")
public class LiquidWalker extends TreeParser {
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
	public TreeParser[] getDelegates() {
		return new TreeParser[] {};
	}

	// delegators


	public LiquidWalker(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public LiquidWalker(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return LiquidWalker.tokenNames; }
	@Override public String getGrammarFileName() { return "org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g"; }



	  private Map<String, Tag> tags;
	  private Map<String, Filter> filters;
	  private Flavor flavor;
	  private boolean isRootBlock = true;

	  public LiquidWalker(TreeNodeStream nodes, Map<String, Tag> tags, Map<String, Filter> filters) {
	    this(nodes, tags, filters, Flavor.LIQUID);
	  }

	  public LiquidWalker(TreeNodeStream nodes, Map<String, Tag> tags, Map<String, Filter> filters, Flavor flavor) {
	    super(nodes);
	    this.tags = tags;
	    this.filters = filters;
	    this.flavor = flavor;
	  }



	// $ANTLR start "walk"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:64:1: walk returns [LNode node] : block ;
	public final LNode walk() throws RecognitionException {
		LNode node = null;


		BlockNode block1 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:65:2: ( block )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:65:4: block
			{
			pushFollow(FOLLOW_block_in_walk50);
			block1=block();
			state._fsp--;

			node = block1;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "walk"



	// $ANTLR start "block"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:68:1: block returns [BlockNode node] : ^( BLOCK ( atom )* ) ;
	public final BlockNode block() throws RecognitionException {
		BlockNode node = null;


		LNode atom2 =null;


		  node = new BlockNode(isRootBlock);
		  isRootBlock = false;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:73:2: ( ^( BLOCK ( atom )* ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:73:4: ^( BLOCK ( atom )* )
			{
			match(input,BLOCK,FOLLOW_BLOCK_in_block72); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:73:12: ( atom )*
				loop1:
				while (true) {
					int alt1=2;
					int LA1_0 = input.LA(1);
					if ( (LA1_0==ASSIGNMENT||(LA1_0 >= Break && LA1_0 <= CASE)||LA1_0==COMMENT||(LA1_0 >= CUSTOM_TAG && LA1_0 <= CYCLE)||LA1_0==Continue||LA1_0==FOR_ARRAY||LA1_0==FOR_RANGE||(LA1_0 >= IF && LA1_0 <= INCLUDE)||LA1_0==OUTPUT||LA1_0==PLAIN||LA1_0==RAW||LA1_0==TABLE||LA1_0==UNLESS) ) {
						alt1=1;
					}

					switch (alt1) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:73:13: atom
						{
						pushFollow(FOLLOW_atom_in_block75);
						atom2=atom();
						state._fsp--;

						node.add(atom2);
						}
						break;

					default :
						break loop1;
					}
				}

				match(input, Token.UP, null); 
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "block"



	// $ANTLR start "atom"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:76:1: atom returns [LNode node] : ( tag | output | assignment | PLAIN );
	public final LNode atom() throws RecognitionException {
		LNode node = null;


		CommonTree PLAIN6=null;
		LNode tag3 =null;
		OutputNode output4 =null;
		TagNode assignment5 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:77:2: ( tag | output | assignment | PLAIN )
			int alt2=4;
			switch ( input.LA(1) ) {
			case Break:
			case CAPTURE:
			case CASE:
			case COMMENT:
			case CUSTOM_TAG:
			case CUSTOM_TAG_BLOCK:
			case CYCLE:
			case Continue:
			case FOR_ARRAY:
			case FOR_RANGE:
			case IF:
			case INCLUDE:
			case RAW:
			case TABLE:
			case UNLESS:
				{
				alt2=1;
				}
				break;
			case OUTPUT:
				{
				alt2=2;
				}
				break;
			case ASSIGNMENT:
				{
				alt2=3;
				}
				break;
			case PLAIN:
				{
				alt2=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:77:4: tag
					{
					pushFollow(FOLLOW_tag_in_atom95);
					tag3=tag();
					state._fsp--;

					node = tag3;
					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:78:4: output
					{
					pushFollow(FOLLOW_output_in_atom109);
					output4=output();
					state._fsp--;

					node = output4;
					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:79:4: assignment
					{
					pushFollow(FOLLOW_assignment_in_atom120);
					assignment5=assignment();
					state._fsp--;

					node = assignment5;
					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:80:4: PLAIN
					{
					PLAIN6=(CommonTree)match(input,PLAIN,FOLLOW_PLAIN_in_atom127); 
					node = new AtomNode((PLAIN6!=null?PLAIN6.getText():null));
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "atom"



	// $ANTLR start "tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:83:1: tag returns [LNode node] : ( raw_tag | comment_tag | if_tag | unless_tag | case_tag | cycle_tag | for_tag | table_tag | capture_tag | include_tag | custom_tag | custom_tag_block | break_tag | continue_tag );
	public final LNode tag() throws RecognitionException {
		LNode node = null;


		LNode raw_tag7 =null;
		LNode comment_tag8 =null;
		LNode if_tag9 =null;
		LNode unless_tag10 =null;
		LNode case_tag11 =null;
		LNode cycle_tag12 =null;
		LNode for_tag13 =null;
		LNode table_tag14 =null;
		LNode capture_tag15 =null;
		LNode include_tag16 =null;
		LNode custom_tag17 =null;
		LNode custom_tag_block18 =null;
		LNode break_tag19 =null;
		LNode continue_tag20 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:84:2: ( raw_tag | comment_tag | if_tag | unless_tag | case_tag | cycle_tag | for_tag | table_tag | capture_tag | include_tag | custom_tag | custom_tag_block | break_tag | continue_tag )
			int alt3=14;
			switch ( input.LA(1) ) {
			case RAW:
				{
				alt3=1;
				}
				break;
			case COMMENT:
				{
				alt3=2;
				}
				break;
			case IF:
				{
				alt3=3;
				}
				break;
			case UNLESS:
				{
				alt3=4;
				}
				break;
			case CASE:
				{
				alt3=5;
				}
				break;
			case CYCLE:
				{
				alt3=6;
				}
				break;
			case FOR_ARRAY:
			case FOR_RANGE:
				{
				alt3=7;
				}
				break;
			case TABLE:
				{
				alt3=8;
				}
				break;
			case CAPTURE:
				{
				alt3=9;
				}
				break;
			case INCLUDE:
				{
				alt3=10;
				}
				break;
			case CUSTOM_TAG:
				{
				alt3=11;
				}
				break;
			case CUSTOM_TAG_BLOCK:
				{
				alt3=12;
				}
				break;
			case Break:
				{
				alt3=13;
				}
				break;
			case Continue:
				{
				alt3=14;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}
			switch (alt3) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:84:4: raw_tag
					{
					pushFollow(FOLLOW_raw_tag_in_tag149);
					raw_tag7=raw_tag();
					state._fsp--;

					node = raw_tag7;
					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:85:4: comment_tag
					{
					pushFollow(FOLLOW_comment_tag_in_tag165);
					comment_tag8=comment_tag();
					state._fsp--;

					node = comment_tag8;
					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:86:4: if_tag
					{
					pushFollow(FOLLOW_if_tag_in_tag177);
					if_tag9=if_tag();
					state._fsp--;

					node = if_tag9;
					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:87:4: unless_tag
					{
					pushFollow(FOLLOW_unless_tag_in_tag194);
					unless_tag10=unless_tag();
					state._fsp--;

					node = unless_tag10;
					}
					break;
				case 5 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:88:4: case_tag
					{
					pushFollow(FOLLOW_case_tag_in_tag207);
					case_tag11=case_tag();
					state._fsp--;

					node = case_tag11;
					}
					break;
				case 6 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:89:4: cycle_tag
					{
					pushFollow(FOLLOW_cycle_tag_in_tag222);
					cycle_tag12=cycle_tag();
					state._fsp--;

					node = cycle_tag12;
					}
					break;
				case 7 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:90:4: for_tag
					{
					pushFollow(FOLLOW_for_tag_in_tag236);
					for_tag13=for_tag();
					state._fsp--;

					node = for_tag13;
					}
					break;
				case 8 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:91:4: table_tag
					{
					pushFollow(FOLLOW_table_tag_in_tag252);
					table_tag14=table_tag();
					state._fsp--;

					node = table_tag14;
					}
					break;
				case 9 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:92:4: capture_tag
					{
					pushFollow(FOLLOW_capture_tag_in_tag266);
					capture_tag15=capture_tag();
					state._fsp--;

					node = capture_tag15;
					}
					break;
				case 10 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:93:4: include_tag
					{
					pushFollow(FOLLOW_include_tag_in_tag278);
					include_tag16=include_tag();
					state._fsp--;

					node = include_tag16;
					}
					break;
				case 11 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:94:4: custom_tag
					{
					pushFollow(FOLLOW_custom_tag_in_tag290);
					custom_tag17=custom_tag();
					state._fsp--;

					node = custom_tag17;
					}
					break;
				case 12 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:95:4: custom_tag_block
					{
					pushFollow(FOLLOW_custom_tag_block_in_tag303);
					custom_tag_block18=custom_tag_block();
					state._fsp--;

					node = custom_tag_block18;
					}
					break;
				case 13 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:96:4: break_tag
					{
					pushFollow(FOLLOW_break_tag_in_tag310);
					break_tag19=break_tag();
					state._fsp--;

					node = break_tag19;
					}
					break;
				case 14 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:97:4: continue_tag
					{
					pushFollow(FOLLOW_continue_tag_in_tag324);
					continue_tag20=continue_tag();
					state._fsp--;

					node = continue_tag20;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "tag"



	// $ANTLR start "raw_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:100:1: raw_tag returns [LNode node] : RAW ;
	public final LNode raw_tag() throws RecognitionException {
		LNode node = null;


		CommonTree RAW21=null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:101:2: ( RAW )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:101:4: RAW
			{
			RAW21=(CommonTree)match(input,RAW,FOLLOW_RAW_in_raw_tag345); 
			node = new TagNode("raw", tags.get("raw"), new AtomNode((RAW21!=null?RAW21.getText():null)));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "raw_tag"



	// $ANTLR start "comment_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:104:1: comment_tag returns [LNode node] : COMMENT ;
	public final LNode comment_tag() throws RecognitionException {
		LNode node = null;


		CommonTree COMMENT22=null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:105:2: ( COMMENT )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:105:4: COMMENT
			{
			COMMENT22=(CommonTree)match(input,COMMENT,FOLLOW_COMMENT_in_comment_tag362); 
			node = new TagNode("comment", tags.get("comment"), new AtomNode((COMMENT22!=null?COMMENT22.getText():null)));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "comment_tag"



	// $ANTLR start "if_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:108:1: if_tag returns [LNode node] : ^( IF e1= expr b1= block ( ^( ELSIF e2= expr b2= block ) )* ^( ELSE (b3= block )? ) ) ;
	public final LNode if_tag() throws RecognitionException {
		LNode node = null;


		LNode e1 =null;
		BlockNode b1 =null;
		LNode e2 =null;
		BlockNode b2 =null;
		BlockNode b3 =null;

		List<LNode> nodes = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:110:2: ( ^( IF e1= expr b1= block ( ^( ELSIF e2= expr b2= block ) )* ^( ELSE (b3= block )? ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:110:4: ^( IF e1= expr b1= block ( ^( ELSIF e2= expr b2= block ) )* ^( ELSE (b3= block )? ) )
			{
			match(input,IF,FOLLOW_IF_in_if_tag384); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_expr_in_if_tag395);
			e1=expr();
			state._fsp--;

			pushFollow(FOLLOW_block_in_if_tag399);
			b1=block();
			state._fsp--;

			nodes.add(e1); nodes.add(b1);
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:111:7: ( ^( ELSIF e2= expr b2= block ) )*
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0==ELSIF) ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:111:8: ^( ELSIF e2= expr b2= block )
					{
					match(input,ELSIF,FOLLOW_ELSIF_in_if_tag411); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_if_tag415);
					e2=expr();
					state._fsp--;

					pushFollow(FOLLOW_block_in_if_tag419);
					b2=block();
					state._fsp--;

					nodes.add(e2); nodes.add(b2);
					match(input, Token.UP, null); 

					}
					break;

				default :
					break loop4;
				}
			}

			match(input,ELSE,FOLLOW_ELSE_in_if_tag435); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:112:23: (b3= block )?
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==BLOCK) ) {
					alt5=1;
				}
				switch (alt5) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:112:24: b3= block
						{
						pushFollow(FOLLOW_block_in_if_tag448);
						b3=block();
						state._fsp--;

						nodes.add(new AtomNode("TRUE")); nodes.add(b3);
						}
						break;

				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("if", tags.get("if"), nodes.toArray(new LNode[nodes.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "if_tag"



	// $ANTLR start "unless_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:117:1: unless_tag returns [LNode node] : ^( UNLESS expr b1= block ^( ELSE (b2= block )? ) ) ;
	public final LNode unless_tag() throws RecognitionException {
		LNode node = null;


		BlockNode b1 =null;
		BlockNode b2 =null;
		LNode expr23 =null;

		List<LNode> nodes = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:119:2: ( ^( UNLESS expr b1= block ^( ELSE (b2= block )? ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:119:4: ^( UNLESS expr b1= block ^( ELSE (b2= block )? ) )
			{
			match(input,UNLESS,FOLLOW_UNLESS_in_unless_tag486); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_expr_in_unless_tag488);
			expr23=expr();
			state._fsp--;

			pushFollow(FOLLOW_block_in_unless_tag492);
			b1=block();
			state._fsp--;

			nodes.add(expr23); nodes.add(b1);
			match(input,ELSE,FOLLOW_ELSE_in_unless_tag500); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:120:11: (b2= block )?
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==BLOCK) ) {
					alt6=1;
				}
				switch (alt6) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:120:12: b2= block
						{
						pushFollow(FOLLOW_block_in_unless_tag505);
						b2=block();
						state._fsp--;

						nodes.add(new AtomNode(null)); nodes.add(b2);
						}
						break;

				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("unless", tags.get("unless"), nodes.toArray(new LNode[nodes.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "unless_tag"



	// $ANTLR start "case_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:124:1: case_tag returns [LNode node] : ^( CASE expr ( when_tag[nodes] )+ ^( ELSE ( block )? ) ) ;
	public final LNode case_tag() throws RecognitionException {
		LNode node = null;


		LNode expr24 =null;
		BlockNode block25 =null;

		List<LNode> nodes = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:126:2: ( ^( CASE expr ( when_tag[nodes] )+ ^( ELSE ( block )? ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:126:4: ^( CASE expr ( when_tag[nodes] )+ ^( ELSE ( block )? ) )
			{
			match(input,CASE,FOLLOW_CASE_in_case_tag543); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_expr_in_case_tag545);
			expr24=expr();
			state._fsp--;

			nodes.add(expr24);
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:127:5: ( when_tag[nodes] )+
			int cnt7=0;
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( (LA7_0==WHEN) ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:127:6: when_tag[nodes]
					{
					pushFollow(FOLLOW_when_tag_in_case_tag556);
					when_tag(nodes);
					state._fsp--;

					}
					break;

				default :
					if ( cnt7 >= 1 ) break loop7;
					EarlyExitException eee = new EarlyExitException(7, input);
					throw eee;
				}
				cnt7++;
			}

			match(input,ELSE,FOLLOW_ELSE_in_case_tag570); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:128:11: ( block )?
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==BLOCK) ) {
					alt8=1;
				}
				switch (alt8) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:128:12: block
						{
						pushFollow(FOLLOW_block_in_case_tag573);
						block25=block();
						state._fsp--;

						nodes.add(nodes.get(0)); nodes.add(block25);
						}
						break;

				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("case", tags.get("case"), nodes.toArray(new LNode[nodes.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "case_tag"



	// $ANTLR start "when_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:132:1: when_tag[List<LNode> nodes] : ^( WHEN ( expr )+ block ) ;
	public final void when_tag(List<LNode> nodes) throws RecognitionException {
		LNode expr26 =null;
		BlockNode block27 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:133:2: ( ^( WHEN ( expr )+ block ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:133:4: ^( WHEN ( expr )+ block )
			{
			match(input,WHEN,FOLLOW_WHEN_in_when_tag599); 
			match(input, Token.DOWN, null); 
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:133:11: ( expr )+
			int cnt9=0;
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==And||LA9_0==Contains||LA9_0==DoubleNum||LA9_0==Empty||LA9_0==Eq||LA9_0==False||(LA9_0 >= Gt && LA9_0 <= GtEq)||LA9_0==LOOKUP||(LA9_0 >= LongNum && LA9_0 <= LtEq)||(LA9_0 >= NEq && LA9_0 <= Nil)||LA9_0==Or||LA9_0==Str||LA9_0==True) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:133:12: expr
					{
					pushFollow(FOLLOW_expr_in_when_tag602);
					expr26=expr();
					state._fsp--;

					nodes.add(expr26);
					}
					break;

				default :
					if ( cnt9 >= 1 ) break loop9;
					EarlyExitException eee = new EarlyExitException(9, input);
					throw eee;
				}
				cnt9++;
			}

			pushFollow(FOLLOW_block_in_when_tag608);
			block27=block();
			state._fsp--;

			match(input, Token.UP, null); 

			nodes.add(block27);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "when_tag"



	// $ANTLR start "cycle_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:136:1: cycle_tag returns [LNode node] : ^( CYCLE cycle_group (e= expr )+ ) ;
	public final LNode cycle_tag() throws RecognitionException {
		LNode node = null;


		LNode e =null;
		LNode cycle_group28 =null;

		List<LNode> nodes = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:138:2: ( ^( CYCLE cycle_group (e= expr )+ ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:138:4: ^( CYCLE cycle_group (e= expr )+ )
			{
			match(input,CYCLE,FOLLOW_CYCLE_in_cycle_tag631); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_cycle_group_in_cycle_tag633);
			cycle_group28=cycle_group();
			state._fsp--;

			nodes.add(cycle_group28);
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:138:56: (e= expr )+
			int cnt10=0;
			loop10:
			while (true) {
				int alt10=2;
				int LA10_0 = input.LA(1);
				if ( (LA10_0==And||LA10_0==Contains||LA10_0==DoubleNum||LA10_0==Empty||LA10_0==Eq||LA10_0==False||(LA10_0 >= Gt && LA10_0 <= GtEq)||LA10_0==LOOKUP||(LA10_0 >= LongNum && LA10_0 <= LtEq)||(LA10_0 >= NEq && LA10_0 <= Nil)||LA10_0==Or||LA10_0==Str||LA10_0==True) ) {
					alt10=1;
				}

				switch (alt10) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:138:57: e= expr
					{
					pushFollow(FOLLOW_expr_in_cycle_tag640);
					e=expr();
					state._fsp--;

					nodes.add(e);
					}
					break;

				default :
					if ( cnt10 >= 1 ) break loop10;
					EarlyExitException eee = new EarlyExitException(10, input);
					throw eee;
				}
				cnt10++;
			}

			match(input, Token.UP, null); 

			node = new TagNode("cycle", tags.get("cycle"), nodes.toArray(new LNode[nodes.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "cycle_tag"



	// $ANTLR start "cycle_group"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:142:1: cycle_group returns [LNode node] : ^( GROUP ( expr )? ) ;
	public final LNode cycle_group() throws RecognitionException {
		LNode node = null;


		LNode expr29 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:143:2: ( ^( GROUP ( expr )? ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:143:4: ^( GROUP ( expr )? )
			{
			match(input,GROUP,FOLLOW_GROUP_in_cycle_group667); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:143:12: ( expr )?
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0==And||LA11_0==Contains||LA11_0==DoubleNum||LA11_0==Empty||LA11_0==Eq||LA11_0==False||(LA11_0 >= Gt && LA11_0 <= GtEq)||LA11_0==LOOKUP||(LA11_0 >= LongNum && LA11_0 <= LtEq)||(LA11_0 >= NEq && LA11_0 <= Nil)||LA11_0==Or||LA11_0==Str||LA11_0==True) ) {
					alt11=1;
				}
				switch (alt11) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:143:12: expr
						{
						pushFollow(FOLLOW_expr_in_cycle_group669);
						expr29=expr();
						state._fsp--;

						}
						break;

				}

				match(input, Token.UP, null); 
			}

			node = expr29;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "cycle_group"



	// $ANTLR start "for_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:146:1: for_tag returns [LNode node] : ( for_array | for_range );
	public final LNode for_tag() throws RecognitionException {
		LNode node = null;


		LNode for_array30 =null;
		LNode for_range31 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:147:2: ( for_array | for_range )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==FOR_ARRAY) ) {
				alt12=1;
			}
			else if ( (LA12_0==FOR_RANGE) ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:147:4: for_array
					{
					pushFollow(FOLLOW_for_array_in_for_tag688);
					for_array30=for_array();
					state._fsp--;

					node = for_array30;
					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:148:4: for_range
					{
					pushFollow(FOLLOW_for_range_in_for_tag695);
					for_range31=for_range();
					state._fsp--;

					node = for_range31;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "for_tag"



	// $ANTLR start "for_array"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:151:1: for_array returns [LNode node] : ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LNode for_array() throws RecognitionException {
		LNode node = null;


		CommonTree Id32=null;
		LookupNode lookup33 =null;
		TreeRuleReturnScope for_block34 =null;
		LNode attribute35 =null;


		  List<LNode> expressions = new ArrayList<LNode>();
		  expressions.add(new AtomNode(true));

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:156:2: ( ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:156:4: ^( FOR_ARRAY Id lookup for_block ^( ATTRIBUTES ( attribute )* ) )
			{
			match(input,FOR_ARRAY,FOLLOW_FOR_ARRAY_in_for_array717); 
			match(input, Token.DOWN, null); 
			Id32=(CommonTree)match(input,Id,FOLLOW_Id_in_for_array719); 
			pushFollow(FOLLOW_lookup_in_for_array721);
			lookup33=lookup();
			state._fsp--;

			expressions.add(new AtomNode((Id32!=null?Id32.getText():null))); expressions.add(lookup33);
			pushFollow(FOLLOW_for_block_in_for_array736);
			for_block34=for_block();
			state._fsp--;

			expressions.add((for_block34!=null?((LiquidWalker.for_block_return)for_block34).node1:null)); expressions.add((for_block34!=null?((LiquidWalker.for_block_return)for_block34).node2:null));
			match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_for_array761); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:158:20: ( attribute )*
				loop13:
				while (true) {
					int alt13=2;
					int LA13_0 = input.LA(1);
					if ( (LA13_0==Id) ) {
						alt13=1;
					}

					switch (alt13) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:158:21: attribute
						{
						pushFollow(FOLLOW_attribute_in_for_array764);
						attribute35=attribute();
						state._fsp--;

						expressions.add(attribute35);
						}
						break;

					default :
						break loop13;
					}
				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("for", tags.get("for"), expressions.toArray(new LNode[expressions.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "for_array"



	// $ANTLR start "for_range"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:163:1: for_range returns [LNode node] : ^( FOR_RANGE Id from= expr to= expr block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LNode for_range() throws RecognitionException {
		LNode node = null;


		CommonTree Id36=null;
		LNode from =null;
		LNode to =null;
		BlockNode block37 =null;
		LNode attribute38 =null;


		  List<LNode> expressions = new ArrayList<LNode>();
		  expressions.add(new AtomNode(false));

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:168:2: ( ^( FOR_RANGE Id from= expr to= expr block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:168:4: ^( FOR_RANGE Id from= expr to= expr block ^( ATTRIBUTES ( attribute )* ) )
			{
			match(input,FOR_RANGE,FOLLOW_FOR_RANGE_in_for_range801); 
			match(input, Token.DOWN, null); 
			Id36=(CommonTree)match(input,Id,FOLLOW_Id_in_for_range803); 
			pushFollow(FOLLOW_expr_in_for_range807);
			from=expr();
			state._fsp--;

			pushFollow(FOLLOW_expr_in_for_range811);
			to=expr();
			state._fsp--;

			expressions.add(new AtomNode((Id36!=null?Id36.getText():null))); expressions.add(from); expressions.add(to);
			pushFollow(FOLLOW_block_in_for_range821);
			block37=block();
			state._fsp--;

			expressions.add(block37);
			match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_for_range856); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:170:20: ( attribute )*
				loop14:
				while (true) {
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==Id) ) {
						alt14=1;
					}

					switch (alt14) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:170:21: attribute
						{
						pushFollow(FOLLOW_attribute_in_for_range859);
						attribute38=attribute();
						state._fsp--;

						expressions.add(attribute38);
						}
						break;

					default :
						break loop14;
					}
				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("for", tags.get("for"), expressions.toArray(new LNode[expressions.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "for_range"


	public static class for_block_return extends TreeRuleReturnScope {
		public LNode node1;
		public LNode node2;
	};


	// $ANTLR start "for_block"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:175:1: for_block returns [LNode node1, LNode node2] : ^( FOR_BLOCK n1= block (n2= block )? ) ;
	public final LiquidWalker.for_block_return for_block() throws RecognitionException {
		LiquidWalker.for_block_return retval = new LiquidWalker.for_block_return();
		retval.start = input.LT(1);

		BlockNode n1 =null;
		BlockNode n2 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:176:2: ( ^( FOR_BLOCK n1= block (n2= block )? ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:176:4: ^( FOR_BLOCK n1= block (n2= block )? )
			{
			match(input,FOR_BLOCK,FOLLOW_FOR_BLOCK_in_for_block898); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_block_in_for_block902);
			n1=block();
			state._fsp--;

			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:176:27: (n2= block )?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==BLOCK) ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:176:27: n2= block
					{
					pushFollow(FOLLOW_block_in_for_block906);
					n2=block();
					state._fsp--;

					}
					break;

			}

			match(input, Token.UP, null); 


			      retval.node1 = n1;
			      retval.node2 = n2;
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "for_block"



	// $ANTLR start "attribute"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:183:1: attribute returns [LNode node] : ^( Id expr ) ;
	public final LNode attribute() throws RecognitionException {
		LNode node = null;


		CommonTree Id39=null;
		LNode expr40 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:184:2: ( ^( Id expr ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:184:4: ^( Id expr )
			{
			Id39=(CommonTree)match(input,Id,FOLLOW_Id_in_attribute930); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_expr_in_attribute932);
			expr40=expr();
			state._fsp--;

			match(input, Token.UP, null); 

			node = new AttributeNode(new AtomNode((Id39!=null?Id39.getText():null)), expr40);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "attribute"



	// $ANTLR start "table_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:187:1: table_tag returns [LNode node] : ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) ) ;
	public final LNode table_tag() throws RecognitionException {
		LNode node = null;


		CommonTree Id41=null;
		LookupNode lookup42 =null;
		BlockNode block43 =null;
		LNode attribute44 =null;


		  List<LNode> expressions = new ArrayList<LNode>();

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:191:2: ( ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:191:4: ^( TABLE Id lookup block ^( ATTRIBUTES ( attribute )* ) )
			{
			match(input,TABLE,FOLLOW_TABLE_in_table_tag955); 
			match(input, Token.DOWN, null); 
			Id41=(CommonTree)match(input,Id,FOLLOW_Id_in_table_tag963); 
			expressions.add(new AtomNode((Id41!=null?Id41.getText():null)));
			pushFollow(FOLLOW_lookup_in_table_tag994);
			lookup42=lookup();
			state._fsp--;

			expressions.add(lookup42);
			pushFollow(FOLLOW_block_in_table_tag1021);
			block43=block();
			state._fsp--;

			expressions.add(block43);
			match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_table_tag1050); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:195:20: ( attribute )*
				loop16:
				while (true) {
					int alt16=2;
					int LA16_0 = input.LA(1);
					if ( (LA16_0==Id) ) {
						alt16=1;
					}

					switch (alt16) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:195:21: attribute
						{
						pushFollow(FOLLOW_attribute_in_table_tag1053);
						attribute44=attribute();
						state._fsp--;

						expressions.add(attribute44);
						}
						break;

					default :
						break loop16;
					}
				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			node = new TagNode("tablerow", tags.get("tablerow"), expressions.toArray(new LNode[expressions.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "table_tag"



	// $ANTLR start "capture_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:200:1: capture_tag returns [LNode node] : ^( CAPTURE Id block ) ;
	public final LNode capture_tag() throws RecognitionException {
		LNode node = null;


		CommonTree Id45=null;
		BlockNode block46 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:201:2: ( ^( CAPTURE Id block ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:201:4: ^( CAPTURE Id block )
			{
			match(input,CAPTURE,FOLLOW_CAPTURE_in_capture_tag1086); 
			match(input, Token.DOWN, null); 
			Id45=(CommonTree)match(input,Id,FOLLOW_Id_in_capture_tag1088); 
			pushFollow(FOLLOW_block_in_capture_tag1090);
			block46=block();
			state._fsp--;

			match(input, Token.UP, null); 

			node = new TagNode("capture", tags.get("capture"), new AtomNode((Id45!=null?Id45.getText():null)), block46);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "capture_tag"



	// $ANTLR start "include_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:204:1: include_tag returns [LNode node] : ^( INCLUDE file= Str ^( WITH (with= Str )? ) ) ;
	public final LNode include_tag() throws RecognitionException {
		LNode node = null;


		CommonTree file=null;
		CommonTree with=null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:205:2: ( ^( INCLUDE file= Str ^( WITH (with= Str )? ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:205:4: ^( INCLUDE file= Str ^( WITH (with= Str )? ) )
			{
			match(input,INCLUDE,FOLLOW_INCLUDE_in_include_tag1109); 
			match(input, Token.DOWN, null); 
			file=(CommonTree)match(input,Str,FOLLOW_Str_in_include_tag1113); 
			match(input,WITH,FOLLOW_WITH_in_include_tag1116); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:205:30: (with= Str )?
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==Str) ) {
					alt17=1;
				}
				switch (alt17) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:205:31: with= Str
						{
						with=(CommonTree)match(input,Str,FOLLOW_Str_in_include_tag1121); 
						}
						break;

				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 


			      if((with!=null?with.getText():null) != null) {
			        node = new TagNode("include", tags.get("include"), flavor, new AtomNode((file!=null?file.getText():null)), new AtomNode((with!=null?with.getText():null)));
			      } else {
			        node = new TagNode("include", tags.get("include"), flavor, new AtomNode((file!=null?file.getText():null)));
			      }
			    
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "include_tag"



	// $ANTLR start "break_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:215:1: break_tag returns [LNode node] : Break ;
	public final LNode break_tag() throws RecognitionException {
		LNode node = null;


		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:216:2: ( Break )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:216:4: Break
			{
			match(input,Break,FOLLOW_Break_in_break_tag1146); 
			node = new AtomNode(Tag.Statement.BREAK);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "break_tag"



	// $ANTLR start "continue_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:219:1: continue_tag returns [LNode node] : Continue ;
	public final LNode continue_tag() throws RecognitionException {
		LNode node = null;


		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:220:2: ( Continue )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:220:4: Continue
			{
			match(input,Continue,FOLLOW_Continue_in_continue_tag1163); 
			node = new AtomNode(Tag.Statement.CONTINUE);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "continue_tag"



	// $ANTLR start "custom_tag"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:223:1: custom_tag returns [LNode node] : ^( CUSTOM_TAG Id ( Str )? ) ;
	public final LNode custom_tag() throws RecognitionException {
		LNode node = null;


		CommonTree Str47=null;
		CommonTree Id48=null;

		List<LNode> expressions = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:225:2: ( ^( CUSTOM_TAG Id ( Str )? ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:225:4: ^( CUSTOM_TAG Id ( Str )? )
			{
			match(input,CUSTOM_TAG,FOLLOW_CUSTOM_TAG_in_custom_tag1185); 
			match(input, Token.DOWN, null); 
			Id48=(CommonTree)match(input,Id,FOLLOW_Id_in_custom_tag1187); 
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:225:20: ( Str )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==Str) ) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:225:20: Str
					{
					Str47=(CommonTree)match(input,Str,FOLLOW_Str_in_custom_tag1189); 
					}
					break;

			}

			expressions.add(new AtomNode((Str47!=null?Str47.getText():null)));
			match(input, Token.UP, null); 

			node = new TagNode((Id48!=null?Id48.getText():null), tags.get((Id48!=null?Id48.getText():null)), expressions.toArray(new LNode[expressions.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "custom_tag"



	// $ANTLR start "custom_tag_block"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:229:1: custom_tag_block returns [LNode node] : ^( CUSTOM_TAG_BLOCK Id ( Str )? block ) ;
	public final LNode custom_tag_block() throws RecognitionException {
		LNode node = null;


		CommonTree Str49=null;
		CommonTree Id51=null;
		BlockNode block50 =null;

		List<LNode> expressions = new ArrayList<LNode>();
		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:231:2: ( ^( CUSTOM_TAG_BLOCK Id ( Str )? block ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:231:4: ^( CUSTOM_TAG_BLOCK Id ( Str )? block )
			{
			match(input,CUSTOM_TAG_BLOCK,FOLLOW_CUSTOM_TAG_BLOCK_in_custom_tag_block1219); 
			match(input, Token.DOWN, null); 
			Id51=(CommonTree)match(input,Id,FOLLOW_Id_in_custom_tag_block1221); 
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:231:26: ( Str )?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==Str) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:231:26: Str
					{
					Str49=(CommonTree)match(input,Str,FOLLOW_Str_in_custom_tag_block1223); 
					}
					break;

			}

			expressions.add(new AtomNode((Str49!=null?Str49.getText():null)));
			pushFollow(FOLLOW_block_in_custom_tag_block1228);
			block50=block();
			state._fsp--;

			expressions.add(block50);
			match(input, Token.UP, null); 

			node = new TagNode((Id51!=null?Id51.getText():null), tags.get((Id51!=null?Id51.getText():null)), expressions.toArray(new LNode[expressions.size()]));
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "custom_tag_block"



	// $ANTLR start "output"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:235:1: output returns [OutputNode node] : ^( OUTPUT expr ^( FILTERS ( filter )* ) ) ;
	public final OutputNode output() throws RecognitionException {
		OutputNode node = null;


		LNode expr52 =null;
		FilterNode filter53 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:236:2: ( ^( OUTPUT expr ^( FILTERS ( filter )* ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:236:4: ^( OUTPUT expr ^( FILTERS ( filter )* ) )
			{
			match(input,OUTPUT,FOLLOW_OUTPUT_in_output1253); 
			match(input, Token.DOWN, null); 
			pushFollow(FOLLOW_expr_in_output1255);
			expr52=expr();
			state._fsp--;

			node = new OutputNode(expr52);
			match(input,FILTERS,FOLLOW_FILTERS_in_output1260); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:236:66: ( filter )*
				loop20:
				while (true) {
					int alt20=2;
					int LA20_0 = input.LA(1);
					if ( (LA20_0==FILTER) ) {
						alt20=1;
					}

					switch (alt20) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:236:67: filter
						{
						pushFollow(FOLLOW_filter_in_output1263);
						filter53=filter();
						state._fsp--;

						node.addFilter(filter53);
						}
						break;

					default :
						break loop20;
					}
				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "output"



	// $ANTLR start "filter"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:239:1: filter returns [FilterNode node] : ^( FILTER Id ^( PARAMS ( params[$node] )? ) ) ;
	public final FilterNode filter() throws RecognitionException {
		FilterNode node = null;


		CommonTree Id54=null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:240:2: ( ^( FILTER Id ^( PARAMS ( params[$node] )? ) ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:240:4: ^( FILTER Id ^( PARAMS ( params[$node] )? ) )
			{
			match(input,FILTER,FOLLOW_FILTER_in_filter1285); 
			match(input, Token.DOWN, null); 
			Id54=(CommonTree)match(input,Id,FOLLOW_Id_in_filter1287); 
			node = new FilterNode((Id54!=null?Id54.getText():null), filters.get((Id54!=null?Id54.getText():null)));
			match(input,PARAMS,FOLLOW_PARAMS_in_filter1292); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:240:84: ( params[$node] )?
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==And||LA21_0==Contains||LA21_0==DoubleNum||LA21_0==Empty||LA21_0==Eq||LA21_0==False||(LA21_0 >= Gt && LA21_0 <= GtEq)||LA21_0==LOOKUP||(LA21_0 >= LongNum && LA21_0 <= LtEq)||(LA21_0 >= NEq && LA21_0 <= Nil)||LA21_0==Or||LA21_0==Str||LA21_0==True) ) {
					alt21=1;
				}
				switch (alt21) {
					case 1 :
						// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:240:84: params[$node]
						{
						pushFollow(FOLLOW_params_in_filter1294);
						params(node);
						state._fsp--;

						}
						break;

				}

				match(input, Token.UP, null); 
			}

			match(input, Token.UP, null); 

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "filter"



	// $ANTLR start "params"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:243:1: params[FilterNode node] : ( expr )+ ;
	public final void params(FilterNode node) throws RecognitionException {
		LNode expr55 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:244:2: ( ( expr )+ )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:244:4: ( expr )+
			{
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:244:4: ( expr )+
			int cnt22=0;
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( (LA22_0==And||LA22_0==Contains||LA22_0==DoubleNum||LA22_0==Empty||LA22_0==Eq||LA22_0==False||(LA22_0 >= Gt && LA22_0 <= GtEq)||LA22_0==LOOKUP||(LA22_0 >= LongNum && LA22_0 <= LtEq)||(LA22_0 >= NEq && LA22_0 <= Nil)||LA22_0==Or||LA22_0==Str||LA22_0==True) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:244:5: expr
					{
					pushFollow(FOLLOW_expr_in_params1311);
					expr55=expr();
					state._fsp--;

					node.add(expr55);
					}
					break;

				default :
					if ( cnt22 >= 1 ) break loop22;
					EarlyExitException eee = new EarlyExitException(22, input);
					throw eee;
				}
				cnt22++;
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "params"



	// $ANTLR start "assignment"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:247:1: assignment returns [TagNode node] : ^( ASSIGNMENT Id ( filter )? expr ) ;
	public final TagNode assignment() throws RecognitionException {
		TagNode node = null;


		CommonTree Id56=null;
		FilterNode filter57 =null;
		LNode expr58 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:248:2: ( ^( ASSIGNMENT Id ( filter )? expr ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:248:4: ^( ASSIGNMENT Id ( filter )? expr )
			{
			match(input,ASSIGNMENT,FOLLOW_ASSIGNMENT_in_assignment1331); 
			match(input, Token.DOWN, null); 
			Id56=(CommonTree)match(input,Id,FOLLOW_Id_in_assignment1333); 
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:248:20: ( filter )?
			int alt23=2;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==FILTER) ) {
				alt23=1;
			}
			switch (alt23) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:248:20: filter
					{
					pushFollow(FOLLOW_filter_in_assignment1335);
					filter57=filter();
					state._fsp--;

					}
					break;

			}

			pushFollow(FOLLOW_expr_in_assignment1338);
			expr58=expr();
			state._fsp--;

			match(input, Token.UP, null); 

			node = new TagNode("assign", tags.get("assign"), new AtomNode((Id56!=null?Id56.getText():null)), filter57, expr58);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "assignment"



	// $ANTLR start "expr"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:251:1: expr returns [LNode node] : ( ^( Or a= expr b= expr ) | ^( And a= expr b= expr ) | ^( Eq a= expr b= expr ) | ^( NEq a= expr b= expr ) | ^( LtEq a= expr b= expr ) | ^( Lt a= expr b= expr ) | ^( GtEq a= expr b= expr ) | ^( Gt a= expr b= expr ) | ^( Contains a= expr b= expr ) | LongNum | DoubleNum | Str | True | False | Nil | NO_SPACE | lookup | Empty );
	public final LNode expr() throws RecognitionException {
		LNode node = null;


		CommonTree LongNum59=null;
		CommonTree DoubleNum60=null;
		CommonTree Str61=null;
		CommonTree NO_SPACE62=null;
		LNode a =null;
		LNode b =null;
		LookupNode lookup63 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:252:2: ( ^( Or a= expr b= expr ) | ^( And a= expr b= expr ) | ^( Eq a= expr b= expr ) | ^( NEq a= expr b= expr ) | ^( LtEq a= expr b= expr ) | ^( Lt a= expr b= expr ) | ^( GtEq a= expr b= expr ) | ^( Gt a= expr b= expr ) | ^( Contains a= expr b= expr ) | LongNum | DoubleNum | Str | True | False | Nil | NO_SPACE | lookup | Empty )
			int alt24=18;
			switch ( input.LA(1) ) {
			case Or:
				{
				alt24=1;
				}
				break;
			case And:
				{
				alt24=2;
				}
				break;
			case Eq:
				{
				alt24=3;
				}
				break;
			case NEq:
				{
				alt24=4;
				}
				break;
			case LtEq:
				{
				alt24=5;
				}
				break;
			case Lt:
				{
				alt24=6;
				}
				break;
			case GtEq:
				{
				alt24=7;
				}
				break;
			case Gt:
				{
				alt24=8;
				}
				break;
			case Contains:
				{
				alt24=9;
				}
				break;
			case LongNum:
				{
				alt24=10;
				}
				break;
			case DoubleNum:
				{
				alt24=11;
				}
				break;
			case Str:
				{
				alt24=12;
				}
				break;
			case True:
				{
				alt24=13;
				}
				break;
			case False:
				{
				alt24=14;
				}
				break;
			case Nil:
				{
				alt24=15;
				}
				break;
			case NO_SPACE:
				{
				alt24=16;
				}
				break;
			case LOOKUP:
				{
				alt24=17;
				}
				break;
			case Empty:
				{
				alt24=18;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 24, 0, input);
				throw nvae;
			}
			switch (alt24) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:252:4: ^( Or a= expr b= expr )
					{
					match(input,Or,FOLLOW_Or_in_expr1357); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1361);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1365);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new OrNode(a, b);
					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:253:4: ^( And a= expr b= expr )
					{
					match(input,And,FOLLOW_And_in_expr1380); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1384);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1388);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new AndNode(a, b);
					}
					break;
				case 3 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:254:4: ^( Eq a= expr b= expr )
					{
					match(input,Eq,FOLLOW_Eq_in_expr1402); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1406);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1410);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new EqNode(a, b);
					}
					break;
				case 4 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:255:4: ^( NEq a= expr b= expr )
					{
					match(input,NEq,FOLLOW_NEq_in_expr1425); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1429);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1433);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new NEqNode(a, b);
					}
					break;
				case 5 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:256:4: ^( LtEq a= expr b= expr )
					{
					match(input,LtEq,FOLLOW_LtEq_in_expr1447); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1451);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1455);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new LtEqNode(a, b);
					}
					break;
				case 6 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:257:4: ^( Lt a= expr b= expr )
					{
					match(input,Lt,FOLLOW_Lt_in_expr1468); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1472);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1476);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new LtNode(a, b);
					}
					break;
				case 7 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:258:4: ^( GtEq a= expr b= expr )
					{
					match(input,GtEq,FOLLOW_GtEq_in_expr1491); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1495);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1499);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new GtEqNode(a, b);
					}
					break;
				case 8 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:259:4: ^( Gt a= expr b= expr )
					{
					match(input,Gt,FOLLOW_Gt_in_expr1512); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1516);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1520);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new GtNode(a, b);
					}
					break;
				case 9 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:260:4: ^( Contains a= expr b= expr )
					{
					match(input,Contains,FOLLOW_Contains_in_expr1535); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_expr1539);
					a=expr();
					state._fsp--;

					pushFollow(FOLLOW_expr_in_expr1543);
					b=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					node = new ContainsNode(a, b);
					}
					break;
				case 10 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:261:4: LongNum
					{
					LongNum59=(CommonTree)match(input,LongNum,FOLLOW_LongNum_in_expr1551); 
					node = new AtomNode(new Long((LongNum59!=null?LongNum59.getText():null)));
					}
					break;
				case 11 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:262:4: DoubleNum
					{
					DoubleNum60=(CommonTree)match(input,DoubleNum,FOLLOW_DoubleNum_in_expr1576); 
					node = new AtomNode(new Double((DoubleNum60!=null?DoubleNum60.getText():null)));
					}
					break;
				case 12 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:263:4: Str
					{
					Str61=(CommonTree)match(input,Str,FOLLOW_Str_in_expr1599); 
					node = new AtomNode((Str61!=null?Str61.getText():null));
					}
					break;
				case 13 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:264:4: True
					{
					match(input,True,FOLLOW_True_in_expr1628); 
					node = new AtomNode(true);
					}
					break;
				case 14 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:265:4: False
					{
					match(input,False,FOLLOW_False_in_expr1656); 
					node = new AtomNode(false);
					}
					break;
				case 15 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:266:4: Nil
					{
					match(input,Nil,FOLLOW_Nil_in_expr1683); 
					node = new AtomNode(null);
					}
					break;
				case 16 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:267:4: NO_SPACE
					{
					NO_SPACE62=(CommonTree)match(input,NO_SPACE,FOLLOW_NO_SPACE_in_expr1712); 
					node = new AtomNode((NO_SPACE62!=null?NO_SPACE62.getText():null));
					}
					break;
				case 17 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:268:4: lookup
					{
					pushFollow(FOLLOW_lookup_in_expr1736);
					lookup63=lookup();
					state._fsp--;

					node = lookup63;
					}
					break;
				case 18 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:269:4: Empty
					{
					match(input,Empty,FOLLOW_Empty_in_expr1762); 
					node = AtomNode.EMPTY;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "expr"



	// $ANTLR start "lookup"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:272:1: lookup returns [LookupNode node] : ^( LOOKUP Id ( index )* ( QMark )? ) ;
	public final LookupNode lookup() throws RecognitionException {
		LookupNode node = null;


		CommonTree Id64=null;
		LookupNode.Indexable index65 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:273:2: ( ^( LOOKUP Id ( index )* ( QMark )? ) )
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:273:4: ^( LOOKUP Id ( index )* ( QMark )? )
			{
			match(input,LOOKUP,FOLLOW_LOOKUP_in_lookup1800); 
			match(input, Token.DOWN, null); 
			Id64=(CommonTree)match(input,Id,FOLLOW_Id_in_lookup1808); 
			node = new LookupNode((Id64!=null?Id64.getText():null));
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:275:7: ( index )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==HASH||LA25_0==INDEX) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:275:9: index
					{
					pushFollow(FOLLOW_index_in_lookup1825);
					index65=index();
					state._fsp--;

					node.add(index65);
					}
					break;

				default :
					break loop25;
				}
			}

			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:277:7: ( QMark )?
			int alt26=2;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==QMark) ) {
				alt26=1;
			}
			switch (alt26) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:277:7: QMark
					{
					match(input,QMark,FOLLOW_QMark_in_lookup1844); 
					}
					break;

			}

			match(input, Token.UP, null); 

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return node;
	}
	// $ANTLR end "lookup"



	// $ANTLR start "index"
	// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:281:1: index returns [LookupNode.Indexable indexable] : ( ^( HASH Id ) | ^( INDEX expr ) );
	public final LookupNode.Indexable index() throws RecognitionException {
		LookupNode.Indexable indexable = null;


		CommonTree Id66=null;
		LNode expr67 =null;

		try {
			// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:282:2: ( ^( HASH Id ) | ^( INDEX expr ) )
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==HASH) ) {
				alt27=1;
			}
			else if ( (LA27_0==INDEX) ) {
				alt27=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:282:4: ^( HASH Id )
					{
					match(input,HASH,FOLLOW_HASH_in_index1867); 
					match(input, Token.DOWN, null); 
					Id66=(CommonTree)match(input,Id,FOLLOW_Id_in_index1869); 
					match(input, Token.UP, null); 

					indexable = new LookupNode.Hash((Id66!=null?Id66.getText():null));
					}
					break;
				case 2 :
					// org.eclipse.embedcdt.core.liqp/nodes/LiquidWalker.g:283:4: ^( INDEX expr )
					{
					match(input,INDEX,FOLLOW_INDEX_in_index1881); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_expr_in_index1883);
					expr67=expr();
					state._fsp--;

					match(input, Token.UP, null); 

					indexable = new LookupNode.Index(expr67);
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return indexable;
	}
	// $ANTLR end "index"

	// Delegated rules



	public static final BitSet FOLLOW_block_in_walk50 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BLOCK_in_block72 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_atom_in_block75 = new BitSet(new long[]{0x00C050000803AE18L,0x0000004010900400L});
	public static final BitSet FOLLOW_tag_in_atom95 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_output_in_atom109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_assignment_in_atom120 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLAIN_in_atom127 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_raw_tag_in_tag149 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comment_tag_in_tag165 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_if_tag_in_tag177 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unless_tag_in_tag194 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_case_tag_in_tag207 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cycle_tag_in_tag222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_for_tag_in_tag236 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_table_tag_in_tag252 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_capture_tag_in_tag266 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_include_tag_in_tag278 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_custom_tag_in_tag290 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_custom_tag_block_in_tag303 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_break_tag_in_tag310 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_continue_tag_in_tag324 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RAW_in_raw_tag345 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COMMENT_in_comment_tag362 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IF_in_if_tag384 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_if_tag395 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_if_tag399 = new BitSet(new long[]{0x0000000C00000000L});
	public static final BitSet FOLLOW_ELSIF_in_if_tag411 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_if_tag415 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_if_tag419 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ELSE_in_if_tag435 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_if_tag448 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_UNLESS_in_unless_tag486 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_unless_tag488 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_unless_tag492 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_ELSE_in_unless_tag500 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_unless_tag505 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CASE_in_case_tag543 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_case_tag545 = new BitSet(new long[]{0x0000000000000000L,0x0000020000000000L});
	public static final BitSet FOLLOW_when_tag_in_case_tag556 = new BitSet(new long[]{0x0000000400000000L,0x0000020000000000L});
	public static final BitSet FOLLOW_ELSE_in_case_tag570 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_case_tag573 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WHEN_in_when_tag599 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_when_tag602 = new BitSet(new long[]{0x4018814204000140L,0x0000002008000877L});
	public static final BitSet FOLLOW_block_in_when_tag608 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CYCLE_in_cycle_tag631 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_cycle_group_in_cycle_tag633 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_cycle_tag640 = new BitSet(new long[]{0x4018814204000048L,0x0000002008000877L});
	public static final BitSet FOLLOW_GROUP_in_cycle_group667 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_cycle_group669 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_for_array_in_for_tag688 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_for_range_in_for_tag695 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FOR_ARRAY_in_for_array717 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_for_array719 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_lookup_in_for_array721 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_for_block_in_for_array736 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ATTRIBUTES_in_for_array761 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_attribute_in_for_array764 = new BitSet(new long[]{0x0200000000000008L});
	public static final BitSet FOLLOW_FOR_RANGE_in_for_range801 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_for_range803 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_for_range807 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_for_range811 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_for_range821 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ATTRIBUTES_in_for_range856 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_attribute_in_for_range859 = new BitSet(new long[]{0x0200000000000008L});
	public static final BitSet FOLLOW_FOR_BLOCK_in_for_block898 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_for_block902 = new BitSet(new long[]{0x0000000000000108L});
	public static final BitSet FOLLOW_block_in_for_block906 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Id_in_attribute930 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_attribute932 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_TABLE_in_table_tag955 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_table_tag963 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_lookup_in_table_tag994 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_table_tag1021 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_ATTRIBUTES_in_table_tag1050 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_attribute_in_table_tag1053 = new BitSet(new long[]{0x0200000000000008L});
	public static final BitSet FOLLOW_CAPTURE_in_capture_tag1086 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_capture_tag1088 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_capture_tag1090 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_INCLUDE_in_include_tag1109 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Str_in_include_tag1113 = new BitSet(new long[]{0x0000000000000000L,0x0000040000000000L});
	public static final BitSet FOLLOW_WITH_in_include_tag1116 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Str_in_include_tag1121 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Break_in_break_tag1146 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Continue_in_continue_tag1163 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CUSTOM_TAG_in_custom_tag1185 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_custom_tag1187 = new BitSet(new long[]{0x0000000000000008L,0x0000000008000000L});
	public static final BitSet FOLLOW_Str_in_custom_tag1189 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CUSTOM_TAG_BLOCK_in_custom_tag_block1219 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_custom_tag_block1221 = new BitSet(new long[]{0x0000000000000100L,0x0000000008000000L});
	public static final BitSet FOLLOW_Str_in_custom_tag_block1223 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_block_in_custom_tag_block1228 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_OUTPUT_in_output1253 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_output1255 = new BitSet(new long[]{0x0000080000000000L});
	public static final BitSet FOLLOW_FILTERS_in_output1260 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_filter_in_output1263 = new BitSet(new long[]{0x0000040000000008L});
	public static final BitSet FOLLOW_FILTER_in_filter1285 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_filter1287 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
	public static final BitSet FOLLOW_PARAMS_in_filter1292 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_params_in_filter1294 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_expr_in_params1311 = new BitSet(new long[]{0x4018814204000042L,0x0000002008000877L});
	public static final BitSet FOLLOW_ASSIGNMENT_in_assignment1331 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_assignment1333 = new BitSet(new long[]{0x4018854204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_filter_in_assignment1335 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_assignment1338 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Or_in_expr1357 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1361 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1365 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_And_in_expr1380 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1384 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1388 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Eq_in_expr1402 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1406 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1410 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_NEq_in_expr1425 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1429 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1433 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LtEq_in_expr1447 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1451 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1455 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Lt_in_expr1468 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1472 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1476 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_GtEq_in_expr1491 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1495 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1499 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Gt_in_expr1512 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1516 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1520 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_Contains_in_expr1535 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_expr1539 = new BitSet(new long[]{0x4018814204000040L,0x0000002008000877L});
	public static final BitSet FOLLOW_expr_in_expr1543 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LongNum_in_expr1551 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DoubleNum_in_expr1576 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Str_in_expr1599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_True_in_expr1628 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_False_in_expr1656 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Nil_in_expr1683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NO_SPACE_in_expr1712 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lookup_in_expr1736 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_Empty_in_expr1762 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LOOKUP_in_lookup1800 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_lookup1808 = new BitSet(new long[]{0x0120000000000008L,0x0000000000400000L});
	public static final BitSet FOLLOW_index_in_lookup1825 = new BitSet(new long[]{0x0120000000000008L,0x0000000000400000L});
	public static final BitSet FOLLOW_QMark_in_lookup1844 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_HASH_in_index1867 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_Id_in_index1869 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_INDEX_in_index1881 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_expr_in_index1883 = new BitSet(new long[]{0x0000000000000008L});
}
