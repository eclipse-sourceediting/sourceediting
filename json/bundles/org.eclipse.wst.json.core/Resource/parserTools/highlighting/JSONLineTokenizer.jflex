/*nlsXXX*/
package org.eclipse.wst.json.core.internal.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Stack;

import org.eclipse.wst.json.core.internal.parser.regions.JSONTextRegionFactory;
import  org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

%%

%public
%class JSONLineTokenizer
%extends AbstractJSONTokenizer
%function primGetNextToken
%type String
%char
%line
%unicode
%caseless
//%debug
%pack

%{
	private final static String UNDEFINED = "undefined";
	private String fBufferedContext = null;
	private int fBufferedStart;
//	private int fBufferedTextLength;
	private int fBufferedLength;
//	private StringBuilder fBufferedText = null;
	private JSONTextRegionFactory fRegionFactory = JSONTextRegionFactory.getInstance();
	private int fInitialState = YYINITIAL;
	public final static int BUFFER_SIZE_NORMAL = 16384;
	public final static int BUFFER_SIZE_SMALL = 256;
	private int fInitialBufferSize = BUFFER_SIZE_NORMAL;

	public void setInitialState(int state) {
		fInitialState = state;
	}
	
	public void setInitialBufferSize(int size) {
		fInitialBufferSize = size;
	}

	@Override
	protected void setJSONArrayState() {
		yybegin(ST_JSON_ARRAY);
	}

	@Override
	protected void setJSONObjectState() {
		yybegin(ST_JSON_OBJECT);
	}

	@Override
	protected void setJSONValueState() {
		yybegin(ST_JSON_VALUE);
	}
	
	/* user method */
  	public final ITextRegion getNextToken() throws IOException {
		String context;
		String nextTokenType;
		boolean spaceFollows;
//		StringBuilder text;
		int start;
		int textLength;
		int length;
		if (fBufferedContext != null) {
			context = fBufferedContext;
//			text = fBufferedText;
			start = fBufferedStart;
			textLength = length = fBufferedLength;

			fBufferedContext = null;
		} else {
			context = primGetNextToken();
//			text = new StringBuilder(yytext());
			start = yychar;
			textLength = length = yylength();
		}

		if (context != null) {
			if (context == UNDEFINED) {
				// undef -> concatenate undef's
				nextTokenType = primGetNextToken();
				while (nextTokenType == UNDEFINED) {
//					text.append(yytext());
					textLength += yylength();
					length = textLength;
					nextTokenType = primGetNextToken();
				}
				fBufferedContext = nextTokenType;
//				fBufferedText = new StringBuilder(yytext());
				fBufferedStart = yychar;
				fBufferedLength = yylength();
			} else {
				nextTokenType = null;
				spaceFollows = false;
				/*if (JSONRegionUtil.isDeclarationValueType(context)) { // declaration value can contain VALUE_S
					nextTokenType = primGetNextToken();
					spaceFollows = (nextTokenType == JSON_DECLARATION_VALUE_S);
				} else 
				*/
				//if (canContainSpace(context)) {
					nextTokenType = primGetNextToken();
					//spaceFollows = (nextTokenType == JSON_S);
				//}
				if (nextTokenType != null) { // nextToken is retrieved
					if (spaceFollows && (context != JSON_COMMENT)) {
						// next is space -> append
//						text.append(yytext());
						length += yylength();
					} else {
						// next is NOT space -> push this for next time, return itself
						fBufferedContext = nextTokenType;
//						fBufferedText = new StringBuilder(yytext());
						fBufferedStart = yychar;
						fBufferedLength = yylength();
					}
				}
			}
		}

		if (context != null) {
			if (context == UNDEFINED) {
				context = JSON_UNKNOWN;
			}
			return fRegionFactory.createRegion(context, start, textLength, length);
		} else {
			return null;
		}
  	}

	/* user method */
	/* for standalone use */
  	/*public final List parseText() throws IOException {
  		List tokens = new ArrayList();

  		JSONTextToken token;
		for (String kind = primGetNextToken(); kind != null; kind = primGetNextToken()) {
			token = new JSONTextToken();
			token.kind = kind;  				
			token.start = yychar;
			token.length = yylength();
			token.image = yytext();
			tokens.add(token);
		}

  		return tokens;
  	}*/
  	
  	/* user method */
  	/*private boolean canContainSpace(String type) {
  		if (type == JSON_DELIMITER || type == JSON_RBRACE || type == JSON_DECLARATION_DELIMITER) {
  			return false;
  		} else {
  			return true;
  		}
  	}*/

	/* user method */
	public final int getOffset() {
		return yychar;
	}
	
	/* user method */
	public final int getLine() {
		return yyline;
	}
	
	/* user method */
	public final boolean isEOF() {
		return zzAtEOF;
	}

	/* user method */
	public void reset(char[] charArray) {
		reset(new CharArrayReader(charArray), 0);
	}

	/* user method */
	public final void reset(java.io.Reader in, int newOffset) {
		/** the input device */
		zzReader = in;

		/** the current state of the DFA */
		zzState = 0;

		/** the current lexical state */
		zzLexicalState = fInitialState; //YYINITIAL;

		/** this buffer contains the current text to be matched and is
			the source of the yytext() string */
		if (zzBuffer.length != fInitialBufferSize) {
			zzBuffer = new char[fInitialBufferSize];
		}
		java.util.Arrays.fill(zzBuffer, (char)0);

		/** the textposition at the last accepting state */
		zzMarkedPos = 0;

		/** the textposition at the last state to be included in yytext */
//		yy_pushbackPos = 0;

		/** the current text position in the buffer */
		zzCurrentPos = 0;

		/** startRead marks the beginning of the yytext() string in the buffer */
		zzStartRead = 0;

		/** endRead marks the last character in the buffer, that has been read
			from input */
		zzEndRead = 0;

		/** number of newlines encountered up to the start of the matched text */
		yyline = 0;

		/** the number of characters up to the start of the matched text */
		yychar = 0;

		/**
		 * the number of characters from the last newline up to the start of the 
		 * matched text
		 */
//		yycolumn = 0; 

		/** 
		 * yy_atBOL == true <=> the scanner is currently at the beginning of a line
		 */
//		yy_atBOL = false;
		
		/** zzAtEOF == true <=> the scanner has returned a value for EOF */
		zzAtEOF = false;

		/* user variables */
		//		fUndefined.delete(0, fUndefined.length());
		jsonContextStack.clear();
	}
	
	/* user method */
	public JSONLineTokenizer() {
		super();
	}

	/**
	 * Added to workaround stricter compilation options without creating
	 * an alternate skeleton file
	 */
	void _usePrivates() {
		System.out.print(yycolumn);
		System.out.print(yyline);
		System.out.print(Boolean.toString(zzAtBOL));
	}
%}

%state ST_JSON_OBJECT
%state ST_JSON_ARRAY
%state ST_JSON_OBJECT_COLON
%state ST_JSON_VALUE
%state ST_JSON_COMMENT

WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
NUMBER_TEXT=-?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?

S = [\x20\x09\x0D\x0A]+

h = [0-9a-f]
nonascii = [\u0080-\uffff]
unicode = \\{h}{1,6}[ \t\r\n\f]?
escape = {unicode}|\\[ -~\u0080-\uffff]

nmstart = [_a-zA-Z]|{nonascii}|{escape}
nmchar = [_a-zA-Z0-9-]|{nonascii}|{escape}
string1 = \"([\t !#$%&(-~]|\\{nl}|\'|{nonascii}|{escape})*\"
string2 = \'([\t !#$%&(-~]|\\{nl}|\"|{nonascii}|{escape})*\'

ident = -?{nmstart}{nmchar}*
value_ident = -?{nmstart}"."?({nmchar}+"."?)*

name = {nmchar}+
num = [+-]?([0-9]+|[0-9]*"."[0-9]*)
string = {string1}|{string2}
nl = \n|\r\n|\r|\f

Key = {string1}

startObject = \{
endObject = \}
startArray = \[
endArray = \]
comma = \,
%%

\/\*[^*]*\*+([^/*][^*]*\*+)*\/ { return JSON_COMMENT; }
\/\/.* { return JSON_COMMENT; }

{endObject} {return endElement(false); }

/* white space within a tag */
<ST_JSON_OBJECT, ST_JSON_OBJECT_COLON, ST_JSON_ARRAY, ST_JSON_VALUE> {S}* {
 return WHITE_SPACE;
}

<YYINITIAL> {
 {startObject} {return startElement(false);}
 {startArray} {return startElement(true);}
}

<ST_JSON_OBJECT>  {
 {endObject} {return endElement(false); }
 {Key} {yybegin(ST_JSON_OBJECT_COLON);	return JSON_OBJECT_KEY;}
}

<ST_JSON_OBJECT_COLON> {
 ":" {  yybegin(ST_JSON_VALUE); return JSON_COLON; }
}

<ST_JSON_ARRAY> {
 {endArray} {return endElement(true);}
}

<ST_JSON_VALUE, ST_JSON_ARRAY> {
 {startObject} {return startElement(false);}
 {startArray} {return startElement(true);}
 "true" { yybegin(ST_JSON_VALUE); return JSON_VALUE_BOOLEAN; }
 "false" { yybegin(ST_JSON_VALUE); return JSON_VALUE_BOOLEAN; }
 "null" { yybegin(ST_JSON_VALUE); return JSON_VALUE_NULL; }
 {string} { yybegin(ST_JSON_VALUE); return JSON_VALUE_STRING; }
 {NUMBER_TEXT} { yybegin(ST_JSON_VALUE); return JSON_VALUE_NUMBER; }
}

<ST_JSON_VALUE> {endObject} {
 return endElement(false);
}

<ST_JSON_VALUE> {comma} {
 if (!isArrayParsing()) {
   yybegin(ST_JSON_OBJECT);
 }
 return JSON_COMMA;
}

<ST_JSON_VALUE> {endArray} {
 return endElement(true);
}

. {
	return UNDEFINED;
}

\040 {
	return WHITE_SPACE;
}
\011 {
	return WHITE_SPACE;
}
\015 
{
	return WHITE_SPACE;
}
\012 {
	return WHITE_SPACE;
}