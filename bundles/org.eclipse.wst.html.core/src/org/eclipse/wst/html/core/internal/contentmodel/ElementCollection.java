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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations.
 */
class ElementCollection extends DeclCollection implements org.eclipse.wst.html.core.internal.provisional.HTML40Namespace.ElementName {


	// Element IDs
	protected static class Ids {
		public static final int ID_A = 0;
		public static final int ID_ABBR = 1;
		public static final int ID_ACRONYM = 2;
		public static final int ID_ADDRESS = 3;
		public static final int ID_APPLET = 4;
		public static final int ID_AREA = 5;
		public static final int ID_B = 6;
		public static final int ID_BASE = 7;
		public static final int ID_BASEFONT = 8;
		public static final int ID_BDO = 9;
		public static final int ID_BIG = 10;
		public static final int ID_BLINK = 11;
		public static final int ID_BLOCKQUOTE = 12;
		public static final int ID_BODY = 13;
		public static final int ID_BR = 14;
		public static final int ID_BUTTON = 15;
		public static final int ID_CAPTION = 16;
		public static final int ID_CENTER = 17;
		public static final int ID_CITE = 18;
		public static final int ID_CODE = 19;
		public static final int ID_COL = 20;
		public static final int ID_COLGROUP = 21;
		public static final int ID_DD = 22;
		public static final int ID_DEL = 23;
		public static final int ID_DFN = 24;
		public static final int ID_DIR = 25;
		public static final int ID_DIV = 26;
		public static final int ID_DL = 27;
		public static final int ID_DT = 28;
		public static final int ID_EM = 29;
		public static final int ID_EMBED = 30;
		public static final int ID_FIELDSET = 31;
		public static final int ID_FONT = 32;
		public static final int ID_FORM = 33;
		public static final int ID_FRAME = 34;
		public static final int ID_FRAMESET = 35;
		public static final int ID_H1 = 36;
		public static final int ID_H2 = 37;
		public static final int ID_H3 = 38;
		public static final int ID_H4 = 39;
		public static final int ID_H5 = 40;
		public static final int ID_H6 = 41;
		public static final int ID_HEAD = 42;
		public static final int ID_HR = 43;
		public static final int ID_HTML = 44;
		public static final int ID_I = 45;
		public static final int ID_IFRAME = 46;
		public static final int ID_IMG = 47;
		public static final int ID_INPUT = 48;
		public static final int ID_INS = 49;
		public static final int ID_ISINDEX = 50;
		public static final int ID_KBD = 51;
		public static final int ID_LABEL = 52;
		public static final int ID_LEGEND = 53;
		public static final int ID_LI = 54;
		public static final int ID_LINK = 55;
		public static final int ID_MAP = 56;
		public static final int ID_MENU = 57;
		public static final int ID_META = 58;
		public static final int ID_NOEMBED = 59;
		public static final int ID_NOFRAMES = 60;
		public static final int ID_NOSCRIPT = 61;
		public static final int ID_OBJECT = 62;
		public static final int ID_OL = 63;
		public static final int ID_OPTGROUP = 64;
		public static final int ID_OPTION = 65;
		public static final int ID_P = 66;
		public static final int ID_PARAM = 67;
		public static final int ID_PRE = 68;
		public static final int ID_Q = 69;
		public static final int ID_S = 70;
		public static final int ID_SAMP = 71;
		public static final int ID_SCRIPT = 72;
		public static final int ID_SELECT = 73;
		public static final int ID_SMALL = 74;
		public static final int ID_SPAN = 75;
		public static final int ID_STRIKE = 76;
		public static final int ID_STRONG = 77;
		public static final int ID_STYLE = 78;
		public static final int ID_SUB = 79;
		public static final int ID_SUP = 80;
		public static final int ID_TABLE = 81;
		public static final int ID_TBODY = 82;
		public static final int ID_TD = 83;
		public static final int ID_TEXTAREA = 84;
		public static final int ID_TFOOT = 85;
		public static final int ID_TH = 86;
		public static final int ID_THEAD = 87;
		public static final int ID_TITLE = 88;
		public static final int ID_TR = 89;
		public static final int ID_TT = 90;
		public static final int ID_U = 91;
		public static final int ID_UL = 92;
		public static final int ID_VAR = 93;
		public static final int ID_MARQUEE = 94;
		public static final int ID_SSI_CONFIG = 95;
		public static final int ID_SSI_ECHO = 96;
		public static final int ID_SSI_EXEC = 97;
		public static final int ID_SSI_FSIZE = 98;
		public static final int ID_SSI_FLASTMOD = 99;
		public static final int ID_SSI_INCLUDE = 100;
		public static final int ID_SSI_PRINTENV = 101;
		public static final int ID_SSI_SET = 102;
		// <<D205513
		public static final int ID_BGSOUND = 103;
		public static final int ID_NOBR = 104;
		public static final int ID_WBR = 105;

		// D205513

		public static int getNumOfIds() {
			if (numofids != -1)
				return numofids;

			// NOTE: If the reflection is too slow, this method should
			// just return the literal value, like 105.
			// -- 5/25/2001
			Class clazz = Ids.class;
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
	private static final String[] FORMCTL = {INPUT, SELECT, TEXTAREA, LABEL, BUTTON};
	/** %phrase;.
	 * EM | STRONG | DFN | CODE | SAMP | KBD | VAR | CITE | ABBR | ACRONYM
	 */
	private static final String[] PHRASE = {EM, STRONG, DFN, CODE, SAMP, KBD, VAR, CITE, ABBR, ACRONYM};
	/** %special;.
	 * A | IMG | APPLET | OBJECT | FONT | BASEFONT | BR | SCRIPT |
	 * MAP | Q | SUB | SUP | SPAN | BDO | IFRAME | EMBED | MARQUEE |
	 * D2W | SUBMIT
	 * WBR | NOBR | BGSOUND
	 */
	private static final String[] SPECIAL = {A, IMG, APPLET, OBJECT, FONT, BASEFONT, BR, WBR, // D205513
				SCRIPT, MAP, Q, SUB, SUP, SPAN, BDO, IFRAME, EMBED, BGSOUND, // D205513
				MARQUEE, NOBR // D205513
	};
	/** %heading;. H[1-6] */
	private static final String[] HEADING = {H1, H2, H3, H4, H5, H6};
	/** %list;. UL | OL | DIR | MENU */
	private static final String[] LIST = {UL, OL, DIR, MENU};
	/** %preformatted;. PRE */
	private static final String[] PREFORMATTED = {PRE};
	protected AttributeCollection attributeCollection = null;
	private static String[] fNames = null;

	/**
	 */
	public ElementCollection(AttributeCollection collection) {
		super(getNames(), TOLERANT_CASE);
		attributeCollection = collection;
	}

	public ElementCollection(String[] names, AttributeCollection collection) {
		super(names, TOLERANT_CASE);
		attributeCollection = collection;
	}

	/**
	 * Actually creates HTMLElementDeclaration instance.
	 * @return HTMLElementDeclaration
	 */
	protected CMNode create(String elementName) {
		HTMLElemDeclImpl edec = null;

		if (elementName.equalsIgnoreCase(A)) {
			edec = new HedA(this);
		}
		else if (elementName.equalsIgnoreCase(ABBR)) {
			edec = new HedPhrase(ABBR, this);

		}
		else if (elementName.equalsIgnoreCase(ACRONYM)) {
			edec = new HedPhrase(ACRONYM, this);

		}
		else if (elementName.equalsIgnoreCase(ADDRESS)) {
			edec = new HedADDRESS(this);

		}
		else if (elementName.equalsIgnoreCase(APPLET)) {
			edec = new HedAPPLET(this);

		}
		else if (elementName.equalsIgnoreCase(AREA)) {
			edec = new HedAREA(this);

		}
		else if (elementName.equalsIgnoreCase(B)) {
			edec = new HedFontStyle(B, this);

		}
		else if (elementName.equalsIgnoreCase(BASE)) {
			edec = new HedBASE(this);

		}
		else if (elementName.equalsIgnoreCase(BASEFONT)) {
			edec = new HedBASEFONT(this);

		}
		else if (elementName.equalsIgnoreCase(BDO)) {
			edec = new HedBDO(this);

		}
		else if (elementName.equalsIgnoreCase(BIG)) {
			edec = new HedFontStyle(BIG, this);

		}
		else if (elementName.equalsIgnoreCase(BLINK)) {
			edec = new HedFontStyle(BLINK, this);

		}
		else if (elementName.equalsIgnoreCase(BLOCKQUOTE)) {
			edec = new HedBLOCKQUOTE(this);

		}
		else if (elementName.equalsIgnoreCase(BODY)) {
			edec = new HedBODY(this);

		}
		else if (elementName.equalsIgnoreCase(BR)) {
			edec = new HedBR(this);

		}
		else if (elementName.equalsIgnoreCase(BUTTON)) {
			edec = new HedBUTTON(this);

		}
		else if (elementName.equalsIgnoreCase(CAPTION)) {
			edec = new HedCAPTION(this);

		}
		else if (elementName.equalsIgnoreCase(CENTER)) {
			edec = new HedCENTER(this);

		}
		else if (elementName.equalsIgnoreCase(CITE)) {
			edec = new HedPhrase(CITE, this);

		}
		else if (elementName.equalsIgnoreCase(CODE)) {
			edec = new HedPhrase(CODE, this);

		}
		else if (elementName.equalsIgnoreCase(COL)) {
			edec = new HedCOL(this);

		}
		else if (elementName.equalsIgnoreCase(COLGROUP)) {
			edec = new HedCOLGROUP(this);

		}
		else if (elementName.equalsIgnoreCase(DD)) {
			edec = new HedDD(this);

		}
		else if (elementName.equalsIgnoreCase(DEL)) {
			edec = new HedMarkChanges(DEL, this);

		}
		else if (elementName.equalsIgnoreCase(DFN)) {
			edec = new HedPhrase(DFN, this);

		}
		else if (elementName.equalsIgnoreCase(DIR)) {
			edec = new HedMENU(DIR, this);

		}
		else if (elementName.equalsIgnoreCase(DIV)) {
			edec = new HedDIV(this);

		}
		else if (elementName.equalsIgnoreCase(DL)) {
			edec = new HedDL(this);

		}
		else if (elementName.equalsIgnoreCase(DT)) {
			edec = new HedDT(this);

		}
		else if (elementName.equalsIgnoreCase(EM)) {
			edec = new HedPhrase(EM, this);

		}
		else if (elementName.equalsIgnoreCase(EMBED)) {
			edec = new HedEMBED(this);

		}
		else if (elementName.equalsIgnoreCase(FIELDSET)) {
			edec = new HedFIELDSET(this);

		}
		else if (elementName.equalsIgnoreCase(FONT)) {
			edec = new HedFONT(this);

		}
		else if (elementName.equalsIgnoreCase(FORM)) {
			edec = new HedFORM(this);

		}
		else if (elementName.equalsIgnoreCase(FRAME)) {
			edec = new HedFRAME(this);

		}
		else if (elementName.equalsIgnoreCase(FRAMESET)) {
			edec = new HedFRAMESET(this);

		}
		else if (elementName.equalsIgnoreCase(H1)) {
			edec = new HedHeading(H1, this);

		}
		else if (elementName.equalsIgnoreCase(H2)) {
			edec = new HedHeading(H2, this);

		}
		else if (elementName.equalsIgnoreCase(H3)) {
			edec = new HedHeading(H3, this);

		}
		else if (elementName.equalsIgnoreCase(H4)) {
			edec = new HedHeading(H4, this);

		}
		else if (elementName.equalsIgnoreCase(H5)) {
			edec = new HedHeading(H5, this);

		}
		else if (elementName.equalsIgnoreCase(H6)) {
			edec = new HedHeading(H6, this);

		}
		else if (elementName.equalsIgnoreCase(HEAD)) {
			edec = new HedHEAD(this);

		}
		else if (elementName.equalsIgnoreCase(HR)) {
			edec = new HedHR(this);

		}
		else if (elementName.equalsIgnoreCase(HTML)) {
			edec = new HedHTML(this);

		}
		else if (elementName.equalsIgnoreCase(I)) {
			edec = new HedFontStyle(I, this);

		}
		else if (elementName.equalsIgnoreCase(IFRAME)) {
			edec = new HedIFRAME(this);

		}
		else if (elementName.equalsIgnoreCase(IMG)) {
			edec = new HedIMG(this);

		}
		else if (elementName.equalsIgnoreCase(INPUT)) {
			edec = new HedINPUT(this);

		}
		else if (elementName.equalsIgnoreCase(INS)) {
			edec = new HedMarkChanges(INS, this);

		}
		else if (elementName.equalsIgnoreCase(ISINDEX)) {
			edec = new HedISINDEX(this);

		}
		else if (elementName.equalsIgnoreCase(KBD)) {
			edec = new HedPhrase(KBD, this);

		}
		else if (elementName.equalsIgnoreCase(LABEL)) {
			edec = new HedLABEL(this);

		}
		else if (elementName.equalsIgnoreCase(LEGEND)) {
			edec = new HedLEGEND(this);

		}
		else if (elementName.equalsIgnoreCase(LI)) {
			edec = new HedLI(this);

		}
		else if (elementName.equalsIgnoreCase(LINK)) {
			edec = new HedLINK(this);

		}
		else if (elementName.equalsIgnoreCase(MAP)) {
			edec = new HedMAP(this);

		}
		else if (elementName.equalsIgnoreCase(MARQUEE)) {
			edec = new HedMARQUEE(this);

		}
		else if (elementName.equalsIgnoreCase(MENU)) {
			edec = new HedMENU(MENU, this);

		}
		else if (elementName.equalsIgnoreCase(META)) {
			edec = new HedMETA(this);

		}
		else if (elementName.equalsIgnoreCase(NOEMBED)) {
			edec = new HedNOEMBED(this);

		}
		else if (elementName.equalsIgnoreCase(NOFRAMES)) {
			edec = new HedNOFRAMES(this);

		}
		else if (elementName.equalsIgnoreCase(NOSCRIPT)) {
			edec = new HedNOSCRIPT(this);

		}
		else if (elementName.equalsIgnoreCase(OBJECT)) {
			edec = new HedOBJECT(this);

		}
		else if (elementName.equalsIgnoreCase(OL)) {
			edec = new HedOL(this);

		}
		else if (elementName.equalsIgnoreCase(OPTGROUP)) {
			edec = new HedOPTGROUP(this);

		}
		else if (elementName.equalsIgnoreCase(OPTION)) {
			edec = new HedOPTION(this);

		}
		else if (elementName.equalsIgnoreCase(P)) {
			edec = new HedP(this);

		}
		else if (elementName.equalsIgnoreCase(PARAM)) {
			edec = new HedPARAM(this);

		}
		else if (elementName.equalsIgnoreCase(PRE)) {
			edec = new HedPRE(this);

		}
		else if (elementName.equalsIgnoreCase(Q)) {
			edec = new HedQ(this);

		}
		else if (elementName.equalsIgnoreCase(S)) {
			edec = new HedFontStyle(S, this);

		}
		else if (elementName.equalsIgnoreCase(SAMP)) {
			edec = new HedPhrase(SAMP, this);

		}
		else if (elementName.equalsIgnoreCase(SCRIPT)) {
			edec = new HedSCRIPT(this);

		}
		else if (elementName.equalsIgnoreCase(SELECT)) {
			edec = new HedSELECT(this);

		}
		else if (elementName.equalsIgnoreCase(SMALL)) {
			edec = new HedFontStyle(SMALL, this);

		}
		else if (elementName.equalsIgnoreCase(SPAN)) {
			edec = new HedSPAN(this);

		}
		else if (elementName.equalsIgnoreCase(STRIKE)) {
			edec = new HedFontStyle(STRIKE, this);

		}
		else if (elementName.equalsIgnoreCase(STRONG)) {
			edec = new HedPhrase(STRONG, this);

		}
		else if (elementName.equalsIgnoreCase(STYLE)) {
			edec = new HedSTYLE(this);

		}
		else if (elementName.equalsIgnoreCase(SUB)) {
			edec = new HedScripts(SUB, this);

		}
		else if (elementName.equalsIgnoreCase(SUP)) {
			edec = new HedScripts(SUP, this);

		}
		else if (elementName.equalsIgnoreCase(TABLE)) {
			edec = new HedTABLE(this);

		}
		else if (elementName.equalsIgnoreCase(TBODY)) {
			edec = new HedTableBody(TBODY, this);

		}
		else if (elementName.equalsIgnoreCase(TD)) {
			edec = new HedTableCell(TD, this);

		}
		else if (elementName.equalsIgnoreCase(TEXTAREA)) {
			edec = new HedTEXTAREA(this);

		}
		else if (elementName.equalsIgnoreCase(TFOOT)) {
			edec = new HedTableBody(TFOOT, this);

		}
		else if (elementName.equalsIgnoreCase(TH)) {
			edec = new HedTableCell(TH, this);

		}
		else if (elementName.equalsIgnoreCase(THEAD)) {
			edec = new HedTableBody(THEAD, this);

		}
		else if (elementName.equalsIgnoreCase(TITLE)) {
			edec = new HedTITLE(this);

		}
		else if (elementName.equalsIgnoreCase(TR)) {
			edec = new HedTR(this);

		}
		else if (elementName.equalsIgnoreCase(TT)) {
			edec = new HedFontStyle(TT, this);

		}
		else if (elementName.equalsIgnoreCase(U)) {
			edec = new HedFontStyle(U, this);

		}
		else if (elementName.equalsIgnoreCase(UL)) {
			edec = new HedUL(this);

		}
		else if (elementName.equalsIgnoreCase(VAR)) {
			edec = new HedPhrase(VAR, this);

		}
		else if (elementName.equalsIgnoreCase(SSI_CONFIG)) {
			edec = new HedSSIConfig(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_ECHO)) {
			edec = new HedSSIEcho(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_EXEC)) {
			edec = new HedSSIExec(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_FSIZE)) {
			edec = new HedSSIFsize(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_FLASTMOD)) {
			edec = new HedSSIFlastmod(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_INCLUDE)) {
			edec = new HedSSIInclude(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_PRINTENV)) {
			edec = new HedSSIPrintenv(this);

		}
		else if (elementName.equalsIgnoreCase(SSI_SET)) {
			edec = new HedSSISet(this);

		}
		else if (elementName.equalsIgnoreCase(BGSOUND)) {
			edec = new HedBGSOUND(this);

		}
		else if (elementName.equalsIgnoreCase(NOBR)) {
			edec = new HedNOBR(this);

		}
		else if (elementName.equalsIgnoreCase(WBR)) {
			edec = new HedWBR(this);

		} // unknown
		else {
			// NOTE: We don't define the UNKNOWN element declaration.
			// <code>null</code> for a declaration is a sign of
			// the target element is unknown.
			// -- 3/9/2001
			edec = null;
		}
		return edec;
	}

	public AttributeCollection getAttributeCollection() {
		return attributeCollection;
	}

	public Collection getNamesOfBlock() {
		// P, DL, DIV, CENTER, NOSCRIPT, NOFRAMES, BLOCKQUOTE, FORM, ISINDEX, HR,
		// TABLE, FIELDSET, ADDRESS
		String[] blockMisc = {P, DL, DIV, CENTER, NOSCRIPT, NOFRAMES, BLOCKQUOTE, FORM, ISINDEX, HR, TABLE, FIELDSET, ADDRESS};
		Vector names = new Vector(Arrays.asList(blockMisc));
		// %heading;
		names.addAll(Arrays.asList(HEADING));
		// %list;
		names.addAll(Arrays.asList(LIST));
		// %preformatted;
		names.addAll(Arrays.asList(PREFORMATTED));

		return names;
	}

	/**
	 * %block;.
	 * %block; is:
	 * P | %heading; | %list; | %preformatted; | DL | DIV | CENTER |
	 * NOSCRIPT | NOFRAMES | BLOCKQUOTE | FORM | ISINDEX | HR |
	 * TABLE | FIELDSET | ADDRESS.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public final void getBlock(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, getNamesOfBlock().iterator());
	}

	/**
	 * Create element declarations and store them
	 * into a <code>CMGroupImpl</code> instance.
	 * @param group CMGroupImpl Return values.
	 */
	public void getFlow(CMGroupImpl group) {
		if (group == null)
			return;
		getBlock(group);
		getInline(group);
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public void getFontstyle(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(FONTSTYLE).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public void getFormctrl(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(FORMCTL).iterator());
	}

	/**
	 * %heading;.
	 * @param group CMGroupImpl Return values.
	 */
	public void getHeading(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(HEADING).iterator());
	}

	/**
	 * Create element declarations and store them
	 * into a <code>CMGroupImpl</code> instance.
	 * @param group CMGroupImpl Return values.
	 */
	public void getInline(CMGroupImpl group) {
		if (group == null)
			return;
		getFontstyle(group);
		getPhrase(group);
		getSpecial(group);
		getFormctrl(group);
	}

	/**
	 * %list;.
	 * @param group CMGroupImpl Return values.
	 */
	public void getList(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(LIST).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public void getPhrase(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(PHRASE).iterator());
	}

	/**
	 * %preformatted;
	 * @param group CMGroupImpl Return values.
	 */
	public void getPreformatted(CMGroupImpl group) {
		if (group == null)
			return;

		getDeclarations(group, Arrays.asList(PREFORMATTED).iterator());
	}

	/**
	 * Create element declarations and store them into a <code>CMGroupImpl</code>
	 * instance.<br>
	 * @param group CMGroupImpl Return values.
	 */
	public void getSpecial(CMGroupImpl group) {
		if (group == null)
			return;
		getDeclarations(group, Arrays.asList(SPECIAL).iterator());
	}

	private static String[] getNames() {
		if (fNames == null) {
			fNames = new String[Ids.getNumOfIds()];
			fNames[Ids.ID_A] = A;
			fNames[Ids.ID_ABBR] = ABBR;
			fNames[Ids.ID_ACRONYM] = ACRONYM;
			fNames[Ids.ID_ADDRESS] = ADDRESS;
			fNames[Ids.ID_APPLET] = APPLET;
			fNames[Ids.ID_AREA] = AREA;
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
			fNames[Ids.ID_CENTER] = CENTER;
			fNames[Ids.ID_CITE] = CITE;
			fNames[Ids.ID_CODE] = CODE;
			fNames[Ids.ID_COL] = COL;
			fNames[Ids.ID_COLGROUP] = COLGROUP;
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
			fNames[Ids.ID_FONT] = FONT;
			fNames[Ids.ID_FORM] = FORM;
			fNames[Ids.ID_FRAME] = FRAME;
			fNames[Ids.ID_FRAMESET] = FRAMESET;
			fNames[Ids.ID_H1] = H1;
			fNames[Ids.ID_H2] = H2;
			fNames[Ids.ID_H3] = H3;
			fNames[Ids.ID_H4] = H4;
			fNames[Ids.ID_H5] = H5;
			fNames[Ids.ID_H6] = H6;
			fNames[Ids.ID_HEAD] = HEAD;
			fNames[Ids.ID_HR] = HR;
			fNames[Ids.ID_HTML] = HTML;
			fNames[Ids.ID_I] = I;
			fNames[Ids.ID_IFRAME] = IFRAME;
			fNames[Ids.ID_IMG] = IMG;
			fNames[Ids.ID_INPUT] = INPUT;
			fNames[Ids.ID_INS] = INS;
			fNames[Ids.ID_ISINDEX] = ISINDEX;
			fNames[Ids.ID_KBD] = KBD;
			fNames[Ids.ID_LABEL] = LABEL;
			fNames[Ids.ID_LEGEND] = LEGEND;
			fNames[Ids.ID_LI] = LI;
			fNames[Ids.ID_LINK] = LINK;
			fNames[Ids.ID_MAP] = MAP;
			fNames[Ids.ID_MENU] = MENU;
			fNames[Ids.ID_META] = META;
			fNames[Ids.ID_NOEMBED] = NOEMBED;
			fNames[Ids.ID_NOFRAMES] = NOFRAMES;
			fNames[Ids.ID_NOSCRIPT] = NOSCRIPT;
			fNames[Ids.ID_OBJECT] = OBJECT;
			fNames[Ids.ID_OL] = OL;
			fNames[Ids.ID_OPTGROUP] = OPTGROUP;
			fNames[Ids.ID_OPTION] = OPTION;
			fNames[Ids.ID_P] = P;
			fNames[Ids.ID_PARAM] = PARAM;
			fNames[Ids.ID_PRE] = PRE;
			fNames[Ids.ID_Q] = Q;
			fNames[Ids.ID_S] = S;
			fNames[Ids.ID_SAMP] = SAMP;
			fNames[Ids.ID_SCRIPT] = SCRIPT;
			fNames[Ids.ID_SELECT] = SELECT;
			fNames[Ids.ID_SMALL] = SMALL;
			fNames[Ids.ID_SPAN] = SPAN;
			fNames[Ids.ID_STRIKE] = STRIKE;
			fNames[Ids.ID_STRONG] = STRONG;
			fNames[Ids.ID_STYLE] = STYLE;
			fNames[Ids.ID_SUB] = SUB;
			fNames[Ids.ID_SUP] = SUP;
			fNames[Ids.ID_TABLE] = TABLE;
			fNames[Ids.ID_TBODY] = TBODY;
			fNames[Ids.ID_TD] = TD;
			fNames[Ids.ID_TEXTAREA] = TEXTAREA;
			fNames[Ids.ID_TFOOT] = TFOOT;
			fNames[Ids.ID_TH] = TH;
			fNames[Ids.ID_THEAD] = THEAD;
			fNames[Ids.ID_TITLE] = TITLE;
			fNames[Ids.ID_TR] = TR;
			fNames[Ids.ID_TT] = TT;
			fNames[Ids.ID_U] = U;
			fNames[Ids.ID_UL] = UL;
			fNames[Ids.ID_VAR] = VAR;
			fNames[Ids.ID_MARQUEE] = MARQUEE;
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
