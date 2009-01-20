//*******************************************************************************
// * Copyright (c) 2005, 2009 Andrea Bittau and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     Andrea Bittau - initial API and implementation
// *******************************************************************************/


package org.ucl.xpath;

import java_cup.runtime.*;

%%

%class XPathFlex
%unicode
%cup
%line
%column

Whitespace 	= [ \t\r\n]
StringLiteral 	= (\" ((\"\") | [^\"])* \") | (\' ((\'\') | [^\'])* \')
Digits		= [0-9]+
DoubleLiteral	= ((\. {Digits}) | ({Digits} (\. [0-9]*)?)) [eE] [\+\-]?  {Digits}
DecimalLiteral  = (\. {Digits}) | ({Digits} \. [0-9]*)

Letter		= [a-zA-Z]
NCNameChar      = {Letter} | [0-9] | \. | \- | "_"
NCName		= ( {Letter} | "_") ( {NCNameChar} )*


%{
	public int lineno() { return yyline + 1; }
	public int colno() { return yycolumn + 1; }

	private Symbol symbol(int type) {
		return new Symbol(type, lineno(), colno());
	}
	private Symbol symbol(int type, Object value) {
		return new Symbol(type, lineno(), colno(), value);
	}

	private int commentLevel = 0;
%}

%state COMMENT

%%

<YYINITIAL> {

"(:"			{ commentLevel++; // int overflow =P
			  yybegin(COMMENT); 
			}

"\["	{ return symbol(sym.LBRACKET); }
"\]"	{ return symbol(sym.RBRACKET); }
"\("	{ return symbol(sym.LPAR); }
"\)"	{ return symbol(sym.RPAR); }

"\+"	{ return symbol(sym.PLUS); }
"\-"	{ return symbol(sym.MINUS); }
"\*"	{ return symbol(sym.STAR); }
"\|"	{ return symbol(sym.PIPE); }

"="	{ return symbol(sym.EQUALS); }
"!="	{ return symbol(sym.NOTEQUALS); }
"<"	{ return symbol(sym.LESSTHAN); }
"<="	{ return symbol(sym.LESSEQUAL); }
">"	{ return symbol(sym.GREATER); }
">="	{ return symbol(sym.GREATEREQUAL); }

"<<"	{ return symbol(sym.LESS_LESS); }
">>"	{ return symbol(sym.GREATER_GREATER); }

"/"	{ return symbol(sym.FORWARD_SLASH); }
"//"	{ return symbol(sym.FORWARD_SLASHSLASH); }

"::"	{ return symbol(sym.COLONCOLON); }
"@"	{ return symbol(sym.AT_SYM); }
"\.\."	{ return symbol(sym.DOTDOT); }
":"	{ return symbol(sym.COLON); }
","	{ return symbol(sym.COMMA); }
"\$"	{ return symbol(sym.DOLLAR); }
"\."	{ return symbol(sym.DOT); }
"\?"	{ return symbol(sym.QUESTIONMARK); }


"child"				{ return symbol(sym.CHILD); }
"descendant"			{ return symbol(sym.DESCENDANT); }
"attribute"			{ return symbol(sym.ATTRIBUTE); }
"self"				{ return symbol(sym.SELF); }
"descendant\-or\-self"		{ return symbol(sym.DESCENDANT_OR_SELF); }

"following\-sibling"		{ return symbol(sym.FOLLOWING_SIBLING); }
"following"			{ return symbol(sym.FOLLOWING); }
"namespace"			{ return symbol(sym.NAMESPACE); }
"parent"			{ return symbol(sym.PARENT); }

"ancestor"			{ return symbol(sym.ANCESTOR); }
"preceding\-sibling"		{ return symbol(sym.PRECEDING_SIBLING); }
"preceding"			{ return symbol(sym.PRECEDING); }
"ancestor\-or\-self"		{ return symbol(sym.ANCESTOR_OR_SELF); }

"eq"				{ return symbol(sym.EQ); }
"ne"				{ return symbol(sym.NE); }
"lt"				{ return symbol(sym.LT); }
"le"				{ return symbol(sym.LE); }
"gt"				{ return symbol(sym.GT); }
"ge"				{ return symbol(sym.GE); }

"idiv"				{ return symbol(sym.IDIV); }
"div"				{ return symbol(sym.DIV); }
"mod"				{ return symbol(sym.MOD); }

"union"				{ return symbol(sym.UNION); }
"intersect"			{ return symbol(sym.INTERSECT); }
"except"			{ return symbol(sym.EXCEPT); }

"instance"			{ return symbol(sym.INSTANCE); }
"treat"				{ return symbol(sym.TREAT); }
"castable"			{ return symbol(sym.CASTABLE); }
"cast"				{ return symbol(sym.CAST); }
"as"				{ return symbol(sym.AS); }
"of"				{ return symbol(sym.OF); }
"is"				{ return symbol(sym.IS); }

"for"				{ return symbol(sym.FOR); }
"in"				{ return symbol(sym.IN); }
"return"			{ return symbol(sym.RETURN); }
"satisfies"			{ return symbol(sym.SATISFIES); }
"to"				{ return symbol(sym.TO); }
"some"				{ return symbol(sym.SOME); }
"every"				{ return symbol(sym.EVERY); }
"if"				{ return symbol(sym.IF); }
"then"				{ return symbol(sym.THEN); }
"else"				{ return symbol(sym.ELSE); }
"and"				{ return symbol(sym.AND); }
"or"				{ return symbol(sym.OR); }

"empty"				{ return symbol(sym.EMPTY); }
"item"				{ return symbol(sym.ITEM); }
"node"				{ return symbol(sym.NODE); }
"document\-node"		{ return symbol(sym.DOCUMENT_NODE); }
"text"				{ return symbol(sym.TEXT); }
"comment"			{ return symbol(sym.COMMENT); }
"processing\-instruction"	{ return symbol(sym.PROCESSING_INSTRUCTION); }
"schema\-attribute"		{ return symbol(sym.SCHEMA_ATTRIBUTE); }
"element"			{ return symbol(sym.ELEMENT); }
"schema\-element"		{ return symbol(sym.SCHEMA_ELEMENT); }


{StringLiteral}		{
				// get rid of quotes
				String str = yytext();
				assert str.length() >= 2;
				str = str.substring(1,str.length()-1);
				return symbol(sym.STRING, str); 
			}
{Digits}		{ return symbol(sym.INTEGER, new Integer(yytext())); }
{DoubleLiteral}		{ return symbol(sym.DOUBLE, new Double(yytext())); }
{DecimalLiteral}	{ return symbol(sym.DECIMAL, new Double(yytext())); }
{NCName}		{ return symbol(sym.NCNAME, yytext()); }




{Whitespace} { /* ignore */ }


.	{ 
		String err = "Unknown character at line " + lineno(); 
		err += " col " + colno();
		err += ": " + yytext(); 
			     
		throw new JFlexError(err); 
	}

}

<COMMENT> {
	"(:"		{ commentLevel++; }
	":)"		{ commentLevel--; 
			  if(commentLevel == 0)
		          	yybegin(YYINITIAL);
			}
	.|\n		{ /* ignore */ }
}
