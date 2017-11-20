/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*nlsXXX*/
package org.eclipse.jst.jsp.core.internal.contenttype;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;

%%

%{




        private boolean hasMore = true;
        private final static int MAX_TO_SCAN = 8000;
        StringBuffer string = new StringBuffer();
        // state stack for easier state handling
        private IntStack fStateStack = new IntStack();
        private String valueText = null;
        private boolean isXHTML;
        private boolean isWML;


        public JSPHeadTokenizer() {
                super();
        }

          public void reset (Reader in) {
                /* the input device */
                zzReader = in;

                /* the current state of the DFA */
                zzState = 0;

                /* the current lexical state */
                zzLexicalState = YYINITIAL;

                /* this buffer contains the current text to be matched and is
                 the source of the yytext() string */
                Arrays.fill(zzBuffer, (char)0);

                /* the textposition at the last accepting state */
                zzMarkedPos = 0;

                /* the textposition at the last state to be included in yytext */
                //zzPushbackPos = 0;

                /* the current text position in the buffer */
                zzCurrentPos = 0;

                /* startRead marks the beginning of the yytext() string in the buffer */
                zzStartRead = 0;

                /**
                 * endRead marks the last character in the buffer, that has been read
                 * from input
                 */
                zzEndRead = 0;

                /* number of newlines encountered up to the start of the matched text */
                //yyline = 0;

                /* the number of characters up to the start of the matched text */
                yychar = 0;

                /**
                 * the number of characters from the last newline up to the start
                 * of the matched text
                 */
                //yycolumn = 0;

                /**
                 * yy_atBOL == true <=> the scanner is currently at the beginning
                 * of a line
                 */
                zzAtBOL = true;

                /* yy_atEOF == true <=> the scanner has returned a value for EOF */
                zzAtEOF = false;

                /* denotes if the user-EOF-code has already been executed */
                zzEOFDone = false;


                fStateStack.clear();

                hasMore = true;
                isXHTML=false;
                isWML=false;


        }


        public final HeadParserToken getNextToken() throws IOException, Exception {
                String context = null;
                context = primGetNextToken();
                HeadParserToken result = null;
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
 
        private void popState() {
                yybegin(fStateStack.pop());
        }
        private HeadParserToken createToken(String context, int start, String text) {
                return new HeadParserToken(context, start, text);
        }
        
        public boolean isXHTML() {
            return isXHTML;
        }
        public boolean isWML() {
            return isWML;
        }        

%}

%eof{
        hasMore=false;
%eof}

%public
%class JSPHeadTokenizer
%function primGetNextToken
%type String
%char
%unicode
%ignorecase
//%debug
%switch
%buffer 8192
%scanerror java.lang.Exception


UTF16BE = \xFE\xFF
UTF16LE = \xFF\xFE
UTF83ByteBOM = \xEF\xBB\xBF

// SpaceChar = [\x20\x09]


// [3] S ::= (0x20 | 0x9 | 0xD | 0xA)+
S = [\x20\x09\x0D\x0A]

//BeginAttributeeValue = {S}* \= {S}*

LineTerminator = \r|\n


Z = (\x00)?
S_UTF = {Z}{S}{Z}
BeginAttributeValueUTF = {S_UTF}* \= {S_UTF}*

%state ST_XMLDecl
%state ST_PAGE_DIRECTIVE
%state QuotedAttributeValue
%state DQ_STRING
%state SQ_STRING
%state UnDelimitedString

%%


<YYINITIAL>
{
         // force to start at beginning of line (^) and at beginning of file (yychar == 0)
        ^ {UTF16BE}           {if (yychar == 0 ) {hasMore = false; return EncodingParserConstants.UTF16BE;}}
        ^ {UTF16LE}           {if (yychar == 0 ) {hasMore = false; return EncodingParserConstants.UTF16LE;}}
        ^ {UTF83ByteBOM}       {if (yychar == 0 ) {hasMore = false; return EncodingParserConstants.UTF83ByteBOM;}}
        
        // force to be started on first line, but we do allow preceeding spaces
		^ {S_UTF}* ({Z}<{Z}\?{Z}x{Z}m{Z}l{Z}){S_UTF}+ {if (yychar == 0 ) {yybegin(ST_XMLDecl); return XMLHeadTokenizerConstants.XMLDeclStart;}}


        // following are some simply rules to identify JSP content as "XHTML"
        // see http://www.rfc-editor.org/rfc/rfc3236.txt
		{Z}<{Z}\!{Z}D{Z}O{Z}C{Z}T{Z}Y{Z}P{Z}E{Z} {S_UTF}* {Z}h{Z}t{Z}m{Z}l{Z} {S_UTF}* {Z}P{Z}U{Z}B{Z}L{Z}I{Z}C{Z} .* {Z}\/{Z}\/{Z}D{Z}T{Z}D{Z}{S_UTF}{Z}X{Z}H{Z}T{Z}M{Z}L{Z}                {isXHTML = true;}
		{Z}<{Z}h{Z}t{Z}m{Z}l{Z} {S_UTF}* {Z}x{Z}m{Z}l{Z}n{Z}s{Z} {S_UTF}* {Z}\={Z} {S_UTF}* (({Z}\"{Z}) | ({Z}\'{Z})) {Z}h{Z}t{Z}t{Z}p{Z}:{Z}\/{Z}\/{Z}w{Z}w{Z}w{Z}\.{Z}w{Z}3{Z}\.{Z}o{Z}r{Z}g{Z}\/{Z}1{Z}9{Z}9{Z}9{Z}\/{Z}x{Z}h{Z}t{Z}m{Z}l{Z}     {isXHTML = true;}
         // another case that's part of the "HTML family" is WML 1.0 (WML 2.0 is part of XHTML)
		{Z}<{Z}\!{Z}D{Z}O{Z}C{Z}T{Z}Y{Z}P{Z}E{Z} {S_UTF}* {Z}w{Z}m{Z}l{Z} {S_UTF}* {Z}P{Z}U{Z}B{Z}L{Z}I{Z}C{Z} .* {Z}\/{Z}\/{Z}D{Z}T{Z}D {S}{Z}W{Z}M{Z}L{Z}                   {isWML = true;}

		{Z}<{Z}%{Z} {S_UTF}* {Z}@{Z} {S_UTF}* (({Z}p{Z}a{Z}g{Z}e{Z})|({Z}t{Z}a{Z}g{Z})) {S_UTF}+   {yybegin(ST_PAGE_DIRECTIVE); return JSPHeadTokenizerConstants.PageDirectiveStart;}
		(({Z}<{Z}j{Z}s{Z}p{Z}:{Z}d{Z}i{Z}r{Z}e{Z}c{Z}t{Z}i{Z}v{Z}e{Z}\.{Z}p{Z}a{Z}g{Z}e{Z})|({Z}<{Z}j{Z}s{Z}p{Z}:{Z}d{Z}i{Z}r{Z}e{Z}c{Z}t{Z}i{Z}v{Z}e{Z}\.{Z}t{Z}a{Z}g{Z})) {S_UTF}+           {yybegin(ST_PAGE_DIRECTIVE); return JSPHeadTokenizerConstants.PageDirectiveStart;}

}

<ST_XMLDecl>
{
        ({Z}v{Z}e{Z}r{Z}s{Z}i{Z}o{Z}n{Z}) {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); return XMLHeadTokenizerConstants.XMLDeclVersion;}
        ({Z}e{Z}n{Z}c{Z}o{Z}d{Z}i{Z}n{Z}g{Z}) {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); return XMLHeadTokenizerConstants.XMLDelEncoding;}
        // note the "forced end" (via 'hasMore=false') once the end of XML Declaration found
        // This is since non-ascii chars may follow and may cause IOExceptions which would not occur once stream is
        // read with incorrect encoding (such as if platform encoding is in effect until true encoding detected).
        // BUT, the hasMore=false was removed for this JSP case (probably still ok for pure XML) because
        // in a JSP, we must parse past xmlDecl to get at JSP page directive.
        // We'll assume all chars in this area are "readable" as is.
		{S_UTF}* {Z}\?{Z}>{Z}    {yybegin(YYINITIAL); return XMLHeadTokenizerConstants.XMLDeclEnd;}
}

<ST_PAGE_DIRECTIVE>
{
//  removed 'language' since it really can be handled seperately from encoding, but may add it back later for simple re-use.
		{Z}l{Z}a{Z}n{Z}g{Z}u{Z}a{Z}g{Z}e{Z}     {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); return JSPHeadTokenizerConstants.PageLanguage;}
        {Z}c{Z}o{Z}n{Z}t{Z}e{Z}n{Z}t{Z}T{Z}y{Z}p{Z}e{Z} {BeginAttributeValueUTF}  {pushCurrentState(); yybegin(QuotedAttributeValue); return JSPHeadTokenizerConstants.PageContentType;}
        {Z}p{Z}a{Z}g{Z}e{Z}E{Z}n{Z}c{Z}o{Z}d{Z}i{Z}n{Z}g{Z} {BeginAttributeValueUTF} {pushCurrentState(); yybegin(QuotedAttributeValue); return JSPHeadTokenizerConstants.PageEncoding;}
        // note the "forced end" (via 'hasMore=false') once the end of XML Declaration found
        // This is since non-ascii chars may follow and may cause IOExceptions which would not occur once stream is
        // read in correct encoding.

        // https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4205 demonstrates how we need to keep parsing,
        // even if come to end of one page directive, so hasMore=false was removed from these rules.
		{Z}%{Z}>{Z}    { yybegin(YYINITIAL);  return JSPHeadTokenizerConstants.PageDirectiveEnd;}
        {Z}\/{Z}>{Z}    { yybegin(YYINITIAL); return JSPHeadTokenizerConstants.PageDirectiveEnd;}
}


<QuotedAttributeValue>
{
        {Z}\"{Z}                      { yybegin(DQ_STRING); string.setLength(0); }
        {Z}\'{Z}                      { yybegin(SQ_STRING); string.setLength(0); }
        // in this state, anything other than a space character can start an undelimited string
		{S_UTF}*.           { yypushback(1); yybegin(UnDelimitedString); string.setLength(0);}
}


<DQ_STRING>
{

        {Z}\"{Z}			{ popState(); valueText = string.toString(); return EncodingParserConstants.StringValue; }
        {Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}\?{Z}>{Z}	{ yypushback(yylength()); popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}<{Z}				{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
		[^\x00]			{ string.append( yytext() ); }
        {Z}%{Z}>{Z}		{ yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}


}

<SQ_STRING>
{

        {Z}\'{Z}                      { popState(); valueText = string.toString(); return EncodingParserConstants.StringValue;}
		{Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}%{Z}>{Z}               { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}<{Z}                     { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
		// Skip over the single-byte 0s
		[^\x00]			{ string.append( yytext() ); }
        {Z}%{Z}>{Z}                    { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}


}

<UnDelimitedString>
{


        {S_UTF}                     { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.UnDelimitedStringValue; }
        {Z}{LineTerminator}{Z}        { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}\?{Z}>{Z}                   { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        {Z}<{Z}                     { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}
        // these are a bit special, since we started an undelimit string, but found a quote ... probably indicates a missing beginning quote
        {Z}\'{Z}                      { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue;}
        {Z}\"{Z}                      { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue;}        

		[^\x00]			{ string.append( yytext() ); }
        {Z}%{Z}>{Z}                    { yypushback(yylength());popState(); valueText = string.toString(); return EncodingParserConstants.InvalidTerminatedStringValue;}

}

// The "match anything" rule should always be in effect except for when looking for end of string
// (That is, remember to update state list if/when new states added)
.|\n	{if(yychar > MAX_TO_SCAN) {hasMore=false; return EncodingParserConstants.MAX_CHARS_REACHED;}}

// this rule always in effect
<<EOF>>         {hasMore = false; return EncodingParserConstants.EOF;}


