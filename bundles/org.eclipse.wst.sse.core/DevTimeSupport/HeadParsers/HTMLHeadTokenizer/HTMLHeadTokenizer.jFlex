/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*nlsXXX*/
package org.eclipse.wst.html.core.internal.contenttype;
import java.io.IOException;
import java.io.Reader;

import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;



%%

%{


	private boolean hasMore = true;
	private boolean hasCharsetAttr = false;
	private final static int MAX_TO_SCAN = 8000;
	StringBuffer string = new StringBuffer();
	// state stack for easier state handling
	private IntStack fStateStack = new IntStack();
	private String valueText = null;
	boolean foundContentTypeValue = false;
	private boolean isXHTML;



	public HTMLHeadTokenizer() {
		super();
	}

	  public void reset (Reader in) {
	  	/* the input device */
	  	yy_reader = in;

  		/* the current state of the DFA */
  		yy_state = 0;

  		/* the current lexical state */
  		yy_lexical_state = YYINITIAL;

  		/* this buffer contains the current text to be matched and is
  		 the source of the yytext() string */
  		java.util.Arrays.fill(yy_buffer, (char)0);

  		/* the textposition at the last accepting state */
  		yy_markedPos = 0;

  		/* the textposition at the last state to be included in yytext */
  		yy_pushbackPos = 0;

  		/* the current text position in the buffer */
  		yy_currentPos = 0;

  		/* startRead marks the beginning of the yytext() string in the buffer */
  		yy_startRead = 0;

  		/** 
  		 * endRead marks the last character in the buffer, that has been read
  		 * from input 
  		 */
  		yy_endRead = 0;

  		/* number of newlines encountered up to the start of the matched text */
  		// yyline = 0;

  		/* the number of characters up to the start of the matched text */
  		yychar = 0;

  		/**
  		 * the number of characters from the last newline up to the start
  		 * of the matched text
  		 */
  		// yycolumn = 0; 

  		/** 
  		 * yy_atBOL == true <=> the scanner is currently at the beginning 
  		 * of a line
  		 */
  		yy_atBOL = false;

  		/* yy_atEOF == true <=> the scanner has returned a value for EOF */
  		yy_atEOF = false;

  		/* denotes if the user-EOF-code has already been executed */
  		yy_eof_done = false;


  		fStateStack.clear();
  		
  		hasMore = true;
  		hasCharsetAttr = false;
  		isXHTML = false;

		// its a little wasteful to "throw away" first char array generated
		// by class init (via auto generated code), but we really do want
		// a small buffer for our head parsers.
		if (yy_buffer.length != MAX_TO_SCAN) {
			yy_buffer = new char[MAX_TO_SCAN];
		}
  		

  	}


	public final HeadParserToken getNextToken() throws IOException {
		String context = null;
		HeadParserToken result = null;
		try {
			context = primGetNextToken();
		}
		catch (IllegalStateException e) {
			hasMore = false;
			result = createToken(HTMLHeadTokenizerConstants.UNDEFINED, yychar, yytext());
			while(yy_advance() != YYEOF) {}
			return result;
		}
		if (valueText != null) {
			result = createToken(context, yychar, valueText);
			valueText = null;
		} else {
			result = createToken(context, yychar, yytext());
		}
		return result;
	}

	public final boolean hasMoreTokens() {
		return hasMore && yychar < MAX_TO_SCAN;
	}
	private void pushCurrentState() {
		fStateStack.push(yystate());

	}
	public final boolean hasCharsetAttr() {
		return hasCharsetAttr;
	}

	private void popState() {
		yybegin(fStateStack.pop());
	}
	private HeadParserToken createToken(String context, int start, String text) {
		return new HeadParserToken(context, start, text);
	}
	
    public boolean isXHTML() {
       return isXHTML;
    }
%}

%eof{
	hasMore=false;
%eof}

%public
%class HTMLHeadTokenizer
%function primGetNextToken
%type String
%char
%unicode
%ignorecase 
//%debug
%switch


UTF16BE = \xFE\xFF
UTF16LE = \xFF\xFE
UTF83ByteBOM = \xEF\xBB\xBF

SpaceChar = [\x20\x09]



// [3] S ::= (0x20 | 0x9 | 0xD | 0xA)+
S = [\x20\x09\x0D\x0A]

// BeginAttribeValue = {S}* \= {S}*

LineTerminator = \r|\n

Z = (\x00)?
S_UTF = {Z}{S}{Z}
BeginAttributeValueUTF = {S_UTF}* \= {S_UTF}*

%state ST_XMLDecl
%state ST_META_TAG
%state QuotedAttributeValue
%state DQ_STRING
%state SQ_STRING
%state UnDelimitedString
%state UnDelimitedCharset

%%


<YYINITIAL>  
{
	{UTF16BE}   		{hasMore = false; return EncodingParserConstants.UTF16BE;}
	{UTF16LE}   		{hasMore = false; return EncodingParserConstants.UTF16LE;}
	{UTF83ByteBOM}   	{hasMore = false; return EncodingParserConstants.UTF83ByteBOM;}

	// force to be started on first line, but we do allow preceeding spaces
	^ {S_UTF}* ({Z}<{Z}\?{Z}x{Z}m{Z}l{Z}){S_UTF}+ {if (yychar == 0 ) {yybegin(ST_XMLDecl); return XMLHeadTokenizerConstants.XMLDeclStart;}}
	({Z}<{Z}M{Z}E{Z}T{Z}A{Z})       {yybegin(ST_META_TAG); return HTMLHeadTokenizerConstants.MetaTagStart;}
	
	// To determine if content is XHTML.Taken from JSPHeadTokenizer.
    {Z}<{Z}\!{Z}D{Z}O{Z}C{Z}T{Z}Y{Z}P{Z}E{Z} {S_UTF}* {Z}h{Z}t{Z}m{Z}l{Z} {S_UTF}* {Z}P{Z}U{Z}B{Z}L{Z}I{Z}C{Z} .* {Z}\/{Z}\/{Z}D{Z}T{Z}D{Z}{S_UTF}{Z}X{Z}H{Z}T{Z}M{Z}L{Z}                {isXHTML = true;}
	{Z}<{Z}h{Z}t{Z}m{Z}l{Z} {S_UTF}* {Z}x{Z}m{Z}l{Z}n{Z}s{Z} {S_UTF}* {Z}\={Z} {S_UTF}* (({Z}\"{Z}) | ({Z}\'{Z})) {Z}h{Z}t{Z}t{Z}p{Z}:{Z}\/{Z}\/{Z}w{Z}w{Z}w{Z}\.{Z}w{Z}3{Z}\.{Z}o{Z}r{Z}g{Z}\/{Z}1{Z}9{Z}9{Z}9{Z}\/{Z}x{Z}h{Z}t{Z}m{Z}l{Z}     {isXHTML = true;}

}	
	
<ST_XMLDecl> 
{
	//"version" {BeginAttribeValue} {pushCurrentState(); yybegin(QuotedAttributeValue); return XMLHeadTokenizerConstants.XMLDeclVersion;}
	({Z}e{Z}n{Z}c{Z}o{Z}d{Z}i{Z}n{Z}g{Z}) {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); return XMLHeadTokenizerConstants.XMLDelEncoding;}
	// note this "forced end" once end of XML Declaration found
	({Z}\?{Z}>{Z})    {yybegin(YYINITIAL);  return XMLHeadTokenizerConstants.XMLDeclEnd;}
}	

<ST_META_TAG> 
{

//	"http-equiv" {S}* \= {S}* \"? "Content-Type" \"? {S}+ "content" {BeginAttribeValue}  {pushCurrentState(); yybegin(QuotedAttributeValue); foundContentTypeValue=true; return HTMLHeadTokenizerConstants.MetaTagContentType;}
	{Z}h{Z}t{Z}t{Z}p{Z}-{Z}e{Z}q{Z}u{Z}i{Z}v{Z} {S_UTF}* \= {S_UTF}* {Z}\"?{Z} ({Z}C{Z}o{Z}n{Z}t{Z}e{Z}n{Z}t{Z}-{Z}T{Z}y{Z}p{Z}e{Z}) \"?{Z} ({S_UTF})+ ({Z}c{Z}o{Z}n{Z}t{Z}e{Z}n{Z}t{Z}) {BeginAttributeValueUTF}  {pushCurrentState(); yybegin(QuotedAttributeValue); foundContentTypeValue=true; return HTMLHeadTokenizerConstants.MetaTagContentType;}
	{Z}c{Z}h{Z}a{Z}r{Z}s{Z}e{Z}t{Z} {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); foundContentTypeValue=true; hasCharsetAttr=true; return HTMLHeadTokenizerConstants.MetaTagContentType;}
	{Z}>{Z}    { yybegin(YYINITIAL);  if (foundContentTypeValue) hasMore = false; return HTMLHeadTokenizerConstants.MetaTagEnd;}
	{Z}\/{Z}>{Z}    { yybegin(YYINITIAL); if (foundContentTypeValue) hasMore = false; return HTMLHeadTokenizerConstants.MetaTagEnd;}
}
	

<QuotedAttributeValue>
{
	{Z}\"{Z}            { yybegin(DQ_STRING); string.setLength(0); }
	{Z}\'{Z}			{ yybegin(SQ_STRING); string.setLength(0); }
	// in this state, anything other than a space character can start an undelimited string
	{S_UTF}*.           { yypushback(1); yybegin(UnDelimitedString); string.setLength(0);}

}	


<DQ_STRING>
{

	{Z}\"{Z}                      { popState(); valueText = string.toString(); return EncodingParserConstants.StringValue; }
  	{Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}\?{Z}>{Z}			{ yypushback(yylength()); popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}<{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	
	{Z}>{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}\/{Z}>{Z}			{ yypushback(yylength()); popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	[^\x00]			{ string.append( yytext() ); }


}

<SQ_STRING>
{

	{Z}\'{Z}                      { popState(); valueText = string.toString(); return EncodingParserConstants.StringValue;}
  	{Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}%{Z}>{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}<{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}>{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}\/{Z}>{Z}			{ yypushback(yylength()); popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	[^\x00]			{ string.append( yytext() ); }


}

<UnDelimitedString>
{


	// note this initial special case for HTTP contenttype values
	// Look ahead and see if there are spaces, but don't append the spaces as they may be double-byte
	// Let the next state handle removal of the \x00 and properly append spaces
	";"/{S_UTF}*			{ pushCurrentState(); yybegin(UnDelimitedCharset); string.append( yytext() ); }
	{S_UTF}                     { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.UnDelimitedStringValue; }
  	{Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}\?{Z}>{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}<{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	// these are a bit special, since we started an undelimit string, but found a quote ... probably indicates a missing beginning quote
	{Z}\'{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue;}
	{Z}\"{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue;}
	
	{Z}>{Z}			{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	{Z}\/{Z}>{Z}			{ yypushback(yylength()); popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
	[^\x00]			{ string.append( yytext() ); }

}

<UnDelimitedCharset>
{
	{S} { string.append( yytext() ); }
	// For non \x00 characters, let the previous state handle it
	[^\x00] {yypushback(1); popState(); }
}

// The "match anything" rule should always be in effect except for when looking for end of string
// (That is, remember to update state list if/when new states added)
.|\n	{if(yychar > MAX_TO_SCAN) {hasMore=false; return EncodingParserConstants.MAX_CHARS_REACHED;}}

// this rule always in effect
<<EOF>>         {hasMore = false; return EncodingParserConstants.EOF;}
