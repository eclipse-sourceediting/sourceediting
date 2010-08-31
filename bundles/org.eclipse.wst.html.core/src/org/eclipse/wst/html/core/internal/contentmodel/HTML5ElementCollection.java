/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations.
 */
class HTML5ElementCollection extends ElementCollection implements org.eclipse.wst.html.core.internal.provisional.HTML50Namespace.ElementName {


	// Element IDs
	private static class Ids50 extends Ids {
		// <<D205513
		
		public static final int ID_ARTICLE =106;
		public static final int ID_ASIDE =107;
		public static final int ID_AUDIO =108;
		public static final int ID_CANVAS =109;
		public static final int ID_COMMAND =110;
		public static final int ID_DATALIST =111;
		public static final int ID_DETAILS = 112;
		public static final int ID_FIGURE =113;
		public static final int ID_FIGCAPTION = 114;
		public static final int ID_FOOTER =115;
		public static final int ID_HEADER = 116;
		public static final int ID_HGROUP =117;
		public static final int ID_KEYGEN =118;
		public static final int ID_MARK =119;
		public static final int ID_MATH =120;
		public static final int ID_METER =121;
		public static final int ID_NAV =122;
		public static final int ID_OUTPUT =123;
		public static final int ID_PROGRESS =124;
		public static final int ID_RP = 125;
		public static final int ID_RT = 126;
		public static final int ID_RUBY =127;
		public static final int ID_SECTION =128;
		public static final int ID_SOURCE = 129;
		public static final int ID_SUMMARY = 130;
		public static final int ID_SVG =131;
		public static final int ID_TIME =132;
		public static final int ID_VIDEO =133;

		// D205513

		public static int getNumOfIds() {
			if (numofids != -1)
				return numofids;

			// NOTE: If the reflection is too slow, this method should
			// just return the literal value, like 105.
			// -- 5/25/2001
			Class clazz = Ids50.class;
			Field[] fields = clazz.getFields();
			numofids = 0;
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName();
				if (name.startsWith("ID_"))//$NON-NLS-1$
					numofids++;
			}
			return numofids;
		}

		// chache the result of the reflection.
		private static int numofids = -1;
	}

	/** %fontstyle;. TT | I | B | U | S | STRIKE | BIG | SMALL | BLINK */
	private static final String[] FONTSTYLE = {TT, I, B, U, S, STRIKE, BIG, SMALL, BLINK};
	/** %formctl;. INPUT | SELECT | TEXTAREA | LABEL | BUTTON */
	private static final String[] FORMCTL = {INPUT, SELECT, TEXTAREA, LABEL, BUTTON, DATALIST};
	/** %phrase;.
	 * EM | STRONG | DFN | CODE | SAMP | KBD | VAR | CITE | ABBR | ACRONYM | MARK
	 */	private static final String[] PHRASE = {KEYGEN, EM, STRONG, DFN, CODE, SAMP, KBD, VAR, CITE, ABBR, ACRONYM, MARK};
	/** %special;.
	 * A | IMG | APPLET | OBJECT | FONT | BASEFONT | BR | SCRIPT |
	 * MAP | Q | SUB | SUP | SPAN | BDO | IFRAME | EMBED | MARQUEE |
	 * D2W | SUBMIT
	 * WBR | NOBR | BGSOUND
	 */
	private static final String[] SPECIAL = {A, IMG, APPLET, OBJECT, FONT, BASEFONT, BR, WBR, // D205513
				SCRIPT, MAP, Q, SUB, SUP, SPAN, BDO, IFRAME, EMBED, BGSOUND, // D205513
				MARQUEE, NOBR, // D205513
				OUTPUT, TIME, METER, PROGRESS,
				COMMAND
	};
	/** %heading;. H[1-6] */
	private static final String[] HEADING = {H1, H2, H3, H4, H5, H6};
	/** %list;. UL | OL | DIR | MENU */
	private static final String[] LIST = {UL, OL, DIR, MENU};
	/** %preformatted;. PRE */
	private static final String[] PREFORMATTED = {PRE};
	/** %sectioning;. ARTICLE | ASIDE | NAV | SECTION */
	private static final String[] SECTIONING = { ARTICLE, ASIDE, NAV, SECTION };

	/** %embedded;. AUDIO|CANVAS|EMBED|IFRAME|IMG|MATH|OBJECT|SVG|VIDEO */
	private static final String[] EMBEDDED = { AUDIO, CANVAS, EMBED, IFRAME, IMG, MATH, OBJECT, SVG, VIDEO};

    private static String[] fNames = null;

	/**
	 */
	public HTML5ElementCollection(AttributeCollection collection) {
		super(getNames(), collection);
		attributeCollection = collection;
	}

	/**
	 * Actually creates HTMLElementDeclaration instance.
	 * @return HTMLElementDeclaration
	 */
	protected CMNode create(String elementName) {
		CMNode edec = null;

		if (elementName.equalsIgnoreCase(ACRONYM)) {
			edec = new HedPhrase(ACRONYM, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(APPLET)) {
			edec = new HedAPPLET(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(ARTICLE)) {
			edec = new HedSectioning(ARTICLE, this);

		}
		else if (elementName.equalsIgnoreCase(ASIDE)) {
			edec = new HedSectioning(ASIDE, this);

		}
		else if (elementName.equalsIgnoreCase(AUDIO)) {
			edec = new HedMediaElement(AUDIO, this);

		}
		else if (elementName.equalsIgnoreCase(BASEFONT)) {
			edec = new HedBASEFONT(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(BIG)) {
			edec = new HedFontStyle(BIG, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(CANVAS)) {
			edec = new HedCANVAS(this);

		}
		else if (elementName.equalsIgnoreCase(CENTER)) {
			edec = new HedCENTER(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(COMMAND)) {
			edec = new HedCOMMAND(this);

		}
		else if (elementName.equalsIgnoreCase(DATALIST)) {
			edec = new HedDATALIST(this);

		}
		else if (elementName.equalsIgnoreCase(DETAILS)) {
			edec = new HedDETAILS(this);

		}
		else if (elementName.equalsIgnoreCase(DIR)) {
			edec = new HedMENU(DIR, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(FIGCAPTION)) {
			edec = new HedFIGCAPTION(this);

		}
		else if (elementName.equalsIgnoreCase(FIGURE)) {
			edec = new HedFIGURE(this);

		}
		else if (elementName.equalsIgnoreCase(FOOTER)) {
			edec = new HedHEADER(HTML50Namespace.ElementName.FOOTER ,this);

		}
		else if (elementName.equalsIgnoreCase(FRAME)) {
			edec = new HedFRAME(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(FRAMESET)) {
			edec = new HedFRAMESET(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(HEADER)) {
			edec = new HedHEADER(HTML50Namespace.ElementName.HEADER ,this);
		}
		else if (elementName.equalsIgnoreCase(HGROUP)) {
			edec = new HedHGROUP(this);

		}
		else if (elementName.equalsIgnoreCase(ISINDEX)) {
			edec = new HedISINDEX(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(KEYGEN)) {
			edec = new HedKEYGEN(this);

		}
		else if (elementName.equalsIgnoreCase(MARK)) {
			edec = new HedPhrase(HTML50Namespace.ElementName.MARK,this);

		}
		else if (elementName.equalsIgnoreCase(MATH)) {
			edec = new HedMath(this);

		}
		else if (elementName.equalsIgnoreCase(METER)) {
			edec = new HedMETER(this);
		}
		else if (elementName.equalsIgnoreCase(NAV)) {
			edec = new HedSectioning(NAV, this);
		}
		else if (elementName.equalsIgnoreCase(NOFRAMES)) {
			edec = new HedNOFRAMES(this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(OUTPUT)) {
			edec = new HedOUTPUT(this);

		}
		else if (elementName.equalsIgnoreCase(PROGRESS)) {
			edec = new HedPROGRESS(this);

		}
		else if (elementName.equalsIgnoreCase(RP)) {
			edec = new HedRP(this);

		}
		else if (elementName.equalsIgnoreCase(RT)) {
			edec = new HedRT(this);

		}
		else if (elementName.equalsIgnoreCase(RUBY)) {
			edec = new HedRUBY(this);

		}
		else if (elementName.equalsIgnoreCase(S)) {
			edec = new HedFontStyle(S, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(SECTION)) {
			edec = new HedSectioning(SECTION, this);

		}
		else if (elementName.equalsIgnoreCase(SOURCE)) {
			edec = new HedSOURCE(this);

		}
		else if (elementName.equalsIgnoreCase(STRIKE)) {
			edec = new HedFontStyle(STRIKE, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(SUMMARY)) {
			edec = new HedSUMMARY(this);
		}
		else if (elementName.equalsIgnoreCase(SVG)) {
			edec = new HedSVG(this);
		}
		else if (elementName.equalsIgnoreCase(TIME)) {
			edec = new HedTIME(this);
		}
		else if (elementName.equalsIgnoreCase(TT)) {
			edec = new HedFontStyle(TT, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(U)) {
			edec = new HedFontStyle(U, this);
			((HTMLElemDeclImpl) edec).obsolete(true);
		}
		else if (elementName.equalsIgnoreCase(VIDEO)) {
			edec = new HedVIDEO(this);

		}
		// unknown
		else {
			// NOTE: We don't define the UNKNOWN element declaration.
			// <code>null</code> for a declaration is a sign of
			// the target element is unknown.
			// -- 3/9/2001
			edec = super.create(elementName);
		}
		return edec;
	}

	public AttributeCollection getAttributeCollection() {
		return attributeCollection;
	}

	public final Collection getNamesOfBlock() {
		// P, DL, DIV, CENTER, NOSCRIPT, NOFRAMES, BLOCKQUOTE, FORM, ISINDEX, HR,
		// TABLE, FIELDSET, ADDRESS, RUBY, FIGURE
		String[] blockMisc = {HEADER, FOOTER, HGROUP, P, DL, DIV, CENTER, NOSCRIPT, NOFRAMES, BLOCKQUOTE, FORM, ISINDEX, HR, TABLE, FIELDSET, ADDRESS, RUBY, FIGURE};
		Vector names = new Vector(Arrays.asList(blockMisc));
		// %heading;
		names.addAll(Arrays.asList(HEADING));
		// %list;
		names.addAll(Arrays.asList(LIST));
		// %preformatted;
		names.addAll(Arrays.asList(PREFORMATTED));

		
		return names;
	}

	
	public final void getSectioning(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(SECTIONING).iterator());
	}
	
	public void getEmbedded(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(EMBEDDED).iterator());
	}

	public void getFlow(CMGroupImpl group) {
		if (group == null)
			return;
		super.getFlow(group);
		getSectioning(group);
		CMNode node = getNamedItem(DETAILS);
		if (node != null) {
			group.appendChild(node);
		}
	}

	public void getInline(CMGroupImpl group) {
		if (group == null)
			return;
		super.getInline(group);
		getEmbedded(group);
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public final void getFontstyle(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(FONTSTYLE).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public final void getFormctrl(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(FORMCTL).iterator());
	}

	/**
	 * %heading;.
	 * @param group CMGroupImpl Return values.
	 */
	public final void getHeading(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(HEADING).iterator());
	}

	

	/**
	 * %list;.
	 * @param group CMGroupImpl Return values.
	 */
	public final void getList(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(LIST).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public final void getPhrase(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(PHRASE).iterator());
	}

	/**
	 * %preformatted;
	 * @param group CMGroupImpl Return values.
	 */
	public final void getPreformatted(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(PREFORMATTED).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public final void getSpecial(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(SPECIAL).iterator());
	}

	private static String[] getNames() {
		if (fNames == null) {
			fNames = new String[Ids50.getNumOfIds()];
			fNames[Ids.ID_A] = A;
			fNames[Ids.ID_ABBR] = ABBR;
			fNames[Ids.ID_ACRONYM] = ACRONYM;
			fNames[Ids.ID_ADDRESS] = ADDRESS;
			fNames[Ids.ID_APPLET] = APPLET;
			fNames[Ids.ID_AREA] = AREA;
			fNames[Ids50.ID_ARTICLE] = ARTICLE;
			fNames[Ids50.ID_ASIDE] = ASIDE;
			fNames[Ids50.ID_AUDIO] = AUDIO;
			fNames[Ids.ID_B] = B;
			fNames[Ids.ID_BASE] = BASE;
			fNames[Ids.ID_BASEFONT] = BASEFONT;
			fNames[Ids.ID_BDO] = BDO;
			fNames[Ids.ID_BIG] = BIG;
			fNames[Ids.ID_BLINK] = BLINK;
			fNames[Ids.ID_BLOCKQUOTE] = BLOCKQUOTE;
			fNames[Ids.ID_BODY] = BODY;
			fNames[Ids.ID_BR] = BR;
			fNames[Ids.ID_BUTTON] = BUTTON;
			fNames[Ids.ID_CAPTION] = CAPTION;
			fNames[Ids50.ID_CANVAS] = CANVAS;
			fNames[Ids.ID_CENTER] = CENTER;
			fNames[Ids.ID_CITE] = CITE;
			fNames[Ids.ID_CODE] = CODE;
			fNames[Ids.ID_COL] = COL;
			fNames[Ids.ID_COLGROUP] = COLGROUP;
			fNames[Ids50.ID_COMMAND] = COMMAND;
			fNames[Ids50.ID_DATALIST] = DATALIST;
			fNames[Ids50.ID_DETAILS] = DETAILS;
			fNames[Ids.ID_DD] = DD;
			fNames[Ids.ID_DEL] = DEL;
			fNames[Ids.ID_DFN] = DFN;
			fNames[Ids.ID_DIR] = DIR;
			fNames[Ids.ID_DIV] = DIV;
			fNames[Ids.ID_DL] = DL;
			fNames[Ids.ID_DT] = DT;
			fNames[Ids.ID_EM] = EM;
			fNames[Ids.ID_EMBED] = EMBED;
			fNames[Ids.ID_FIELDSET] = FIELDSET;
			fNames[Ids50.ID_FIGURE] = FIGURE;
			fNames[Ids50.ID_FIGCAPTION] = FIGCAPTION;
			fNames[Ids.ID_FONT] = FONT;
			fNames[Ids.ID_FORM] = FORM;
			fNames[Ids50.ID_FOOTER] = FOOTER;
			fNames[Ids.ID_FRAME] = FRAME;
			fNames[Ids.ID_FRAMESET] = FRAMESET;
			fNames[Ids.ID_H1] = H1;
			fNames[Ids.ID_H2] = H2;
			fNames[Ids.ID_H3] = H3;
			fNames[Ids.ID_H4] = H4;
			fNames[Ids.ID_H5] = H5;
			fNames[Ids.ID_H6] = H6;
			fNames[Ids.ID_HEAD] = HEAD;
			fNames[Ids50.ID_HEADER] = HEADER;
			fNames[Ids50.ID_HGROUP] = HGROUP;
			fNames[Ids.ID_HR] = HR;
			fNames[Ids.ID_HTML] = HTML;
			fNames[Ids.ID_I] = I;
			fNames[Ids.ID_IFRAME] = IFRAME;
			fNames[Ids.ID_IMG] = IMG;
			fNames[Ids.ID_INPUT] = INPUT;
			fNames[Ids.ID_INS] = INS;
			fNames[Ids.ID_ISINDEX] = ISINDEX;
			fNames[Ids.ID_KBD] = KBD;
			fNames[Ids50.ID_KEYGEN] = KEYGEN;
			fNames[Ids.ID_LABEL] = LABEL;
			fNames[Ids.ID_LEGEND] = LEGEND;
			fNames[Ids.ID_LI] = LI;
			fNames[Ids.ID_LINK] = LINK;
			fNames[Ids.ID_MAP] = MAP;
			fNames[Ids50.ID_MARK] = MARK;
			fNames[Ids.ID_MARQUEE] = MARQUEE;
			fNames[Ids50.ID_MATH] = MATH;
			fNames[Ids.ID_MENU] = MENU;
			fNames[Ids.ID_META] = META;
			fNames[Ids50.ID_METER] = METER;
			fNames[Ids50.ID_NAV] = NAV;
			fNames[Ids.ID_NOEMBED] = NOEMBED;
			fNames[Ids.ID_NOFRAMES] = NOFRAMES;
			fNames[Ids.ID_NOSCRIPT] = NOSCRIPT;
			fNames[Ids.ID_OBJECT] = OBJECT;
			fNames[Ids.ID_OL] = OL;
			fNames[Ids.ID_OPTGROUP] = OPTGROUP;
			fNames[Ids.ID_OPTION] = OPTION;
			fNames[Ids50.ID_OUTPUT] = OUTPUT;
			fNames[Ids.ID_P] = P;
			fNames[Ids.ID_PARAM] = PARAM;
			fNames[Ids.ID_PRE] = PRE;
			fNames[Ids50.ID_PROGRESS] = PROGRESS;
			fNames[Ids.ID_Q] = Q;
			fNames[Ids50.ID_RP] = RP;
			fNames[Ids50.ID_RT] = RT;
			fNames[Ids50.ID_RUBY] = RUBY;
			fNames[Ids.ID_S] = S;
			fNames[Ids.ID_SAMP] = SAMP;
			fNames[Ids.ID_SCRIPT] = SCRIPT;
			fNames[Ids50.ID_SECTION] = SECTION;
			fNames[Ids.ID_SELECT] = SELECT;
			fNames[Ids.ID_SMALL] = SMALL;
			fNames[Ids50.ID_SOURCE] = SOURCE;
			fNames[Ids.ID_SPAN] = SPAN;
			fNames[Ids.ID_STRIKE] = STRIKE;
			fNames[Ids.ID_STRONG] = STRONG;
			fNames[Ids.ID_STYLE] = STYLE;
			fNames[Ids50.ID_SUMMARY] = SUMMARY;
			fNames[Ids.ID_SUB] = SUB;
			fNames[Ids.ID_SUP] = SUP;
			fNames[Ids50.ID_SVG] = SVG;
			fNames[Ids.ID_TABLE] = TABLE;
			fNames[Ids.ID_TBODY] = TBODY;
			fNames[Ids.ID_TD] = TD;
			fNames[Ids.ID_TEXTAREA] = TEXTAREA;
			fNames[Ids.ID_TFOOT] = TFOOT;
			fNames[Ids.ID_TH] = TH;
			fNames[Ids.ID_THEAD] = THEAD;
			fNames[Ids50.ID_TIME] = TIME;
			fNames[Ids.ID_TITLE] = TITLE;
			fNames[Ids.ID_TR] = TR;
			fNames[Ids.ID_TT] = TT;
			fNames[Ids.ID_U] = U;
			fNames[Ids.ID_UL] = UL;
			fNames[Ids.ID_VAR] = VAR;
			fNames[Ids50.ID_VIDEO] = VIDEO;
			fNames[Ids.ID_WBR] = WBR;
			fNames[Ids.ID_SSI_CONFIG] = SSI_CONFIG;
			fNames[Ids.ID_SSI_ECHO] = SSI_ECHO;
			fNames[Ids.ID_SSI_EXEC] = SSI_EXEC;
			fNames[Ids.ID_SSI_FSIZE] = SSI_FSIZE;
			fNames[Ids.ID_SSI_FLASTMOD] = SSI_FLASTMOD;
			fNames[Ids.ID_SSI_INCLUDE] = SSI_INCLUDE;
			fNames[Ids.ID_SSI_PRINTENV] = SSI_PRINTENV;
			fNames[Ids.ID_SSI_SET] = SSI_SET;
			fNames[Ids.ID_BGSOUND] = BGSOUND;
			fNames[Ids.ID_NOBR] = NOBR;
			fNames[Ids.ID_WBR] = WBR;
		}
		return fNames;
	}
}
