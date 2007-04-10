/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Factory for element declarations.
 */
final class ElementCollection extends DeclCollection implements org.eclipse.wst.html.core.internal.provisional.HTML40Namespace.ElementName {


	// Element IDs
	private static class Ids {
		public static final int ID_A = 0;
		public static final int ID_ADDRESS = 1;
		public static final int ID_BASE = 2;
		public static final int ID_BLOCKQUOTE = 3;
		public static final int ID_BODY = 4;
		public static final int ID_BR = 5;
		public static final int ID_CENTER = 6;
		public static final int ID_DD = 7;
		public static final int ID_DIR = 8;
		public static final int ID_DIV = 9;
		public static final int ID_DL = 10;
		public static final int ID_DT = 11;
		public static final int ID_FORM = 12;
		public static final int ID_H1 = 13;
		public static final int ID_H2 = 14;
		public static final int ID_H3 = 15;
		public static final int ID_H4 = 16;
		public static final int ID_H5 = 17;
		public static final int ID_H6 = 18;
		public static final int ID_HEAD = 19;
		public static final int ID_HR = 20;
		public static final int ID_HTML = 21;
		public static final int ID_IMG = 22;
		public static final int ID_INPUT = 23;
		public static final int ID_LI = 24;
		public static final int ID_MENU = 25;
		public static final int ID_META = 26;
		public static final int ID_OL = 27;
		public static final int ID_OPTION = 28;
		public static final int ID_P = 29;
		public static final int ID_PRE = 30;
		public static final int ID_SELECT = 31;
		public static final int ID_TEXTAREA = 32;
		public static final int ID_TITLE = 33;
		public static final int ID_UL = 34;
		public static final int ID_SSI_CONFIG = 35;
		public static final int ID_SSI_ECHO = 36;
		public static final int ID_SSI_EXEC = 37;
		public static final int ID_SSI_FSIZE = 38;
		public static final int ID_SSI_FLASTMOD = 39;
		public static final int ID_SSI_INCLUDE = 40;
		public static final int ID_SSI_PRINTENV = 41;
		public static final int ID_SSI_SET = 42;

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

	/** %formctl;. INPUT | SELECT | TEXTAREA */
	private static final String[] FORMCTL = {INPUT, SELECT, TEXTAREA};
	/** %phrase;.
	 * DFN
	 */
	private static final String[] PHRASE = {DFN};
	/** %special;.
	 * A | IMG | BR
	 */
	private static final String[] SPECIAL = {A, IMG, BR};
	/** %heading;. H[1-6] */
	private static final String[] HEADING = {H1, H2, H3, H4, H5, H6};
	/** %list;. UL | OL | DIR | MENU */
	private static final String[] LIST = {UL, OL, DIR, MENU};
	/** %preformatted;. PRE */
	private static final String[] PREFORMATTED = {PRE};
	private AttributeCollection attributeCollection = null;
	private static String[] names = null;

	static {
		names = new String[Ids.getNumOfIds()];
		names[Ids.ID_A] = A;
		names[Ids.ID_ADDRESS] = ADDRESS;
		names[Ids.ID_BASE] = BASE;
		names[Ids.ID_BLOCKQUOTE] = BLOCKQUOTE;
		names[Ids.ID_BODY] = BODY;
		names[Ids.ID_BR] = BR;
		names[Ids.ID_CENTER] = CENTER;
		names[Ids.ID_DD] = DD;
		names[Ids.ID_DIR] = DIR;
		names[Ids.ID_DIV] = DIV;
		names[Ids.ID_DL] = DL;
		names[Ids.ID_DT] = DT;
		names[Ids.ID_FORM] = FORM;
		names[Ids.ID_H1] = H1;
		names[Ids.ID_H2] = H2;
		names[Ids.ID_H3] = H3;
		names[Ids.ID_H4] = H4;
		names[Ids.ID_H5] = H5;
		names[Ids.ID_H6] = H6;
		names[Ids.ID_HEAD] = HEAD;
		names[Ids.ID_HR] = HR;
		names[Ids.ID_HTML] = HTML;
		names[Ids.ID_IMG] = IMG;
		names[Ids.ID_INPUT] = INPUT;
		names[Ids.ID_LI] = LI;
		names[Ids.ID_MENU] = MENU;
		names[Ids.ID_META] = META;
		names[Ids.ID_OL] = OL;
		names[Ids.ID_OPTION] = OPTION;
		names[Ids.ID_P] = P;
		names[Ids.ID_PRE] = PRE;
		names[Ids.ID_SELECT] = SELECT;
		names[Ids.ID_TEXTAREA] = TEXTAREA;
		names[Ids.ID_TITLE] = TITLE;
		names[Ids.ID_UL] = UL;
		names[Ids.ID_SSI_CONFIG] = SSI_CONFIG;
		names[Ids.ID_SSI_ECHO] = SSI_ECHO;
		names[Ids.ID_SSI_EXEC] = SSI_EXEC;
		names[Ids.ID_SSI_FSIZE] = SSI_FSIZE;
		names[Ids.ID_SSI_FLASTMOD] = SSI_FLASTMOD;
		names[Ids.ID_SSI_INCLUDE] = SSI_INCLUDE;
		names[Ids.ID_SSI_PRINTENV] = SSI_PRINTENV;
		names[Ids.ID_SSI_SET] = SSI_SET;
	}

	/**
	 */
	public ElementCollection(AttributeCollection collection) {
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
		else if (elementName.equalsIgnoreCase(ADDRESS)) {
			edec = new HedADDRESS(this);

		}
		else if (elementName.equalsIgnoreCase(BASE)) {
			edec = new HedBASE(this);

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
		else if (elementName.equalsIgnoreCase(CENTER)) {
			edec = new HedCENTER(this);

		}
		else if (elementName.equalsIgnoreCase(DD)) {
			edec = new HedDD(this);

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
		else if (elementName.equalsIgnoreCase(FORM)) {
			edec = new HedFORM(this);

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
		else if (elementName.equalsIgnoreCase(IMG)) {
			edec = new HedIMG(this);

		}
		else if (elementName.equalsIgnoreCase(INPUT)) {
			edec = new HedINPUT(this);

		}
		else if (elementName.equalsIgnoreCase(LI)) {
			edec = new HedLI(this);

		}
		else if (elementName.equalsIgnoreCase(MENU)) {
			edec = new HedMENU(MENU, this);

		}
		else if (elementName.equalsIgnoreCase(META)) {
			edec = new HedMETA(this);

		}
		else if (elementName.equalsIgnoreCase(OL)) {
			edec = new HedOL(this);

		}
		else if (elementName.equalsIgnoreCase(OPTION)) {
			edec = new HedOPTION(this);

		}
		else if (elementName.equalsIgnoreCase(P)) {
			edec = new HedP(this);

		}
		else if (elementName.equalsIgnoreCase(PRE)) {
			edec = new HedPRE(this);

		}
		else if (elementName.equalsIgnoreCase(SELECT)) {
			edec = new HedSELECT(this);

		}
		else if (elementName.equalsIgnoreCase(TEXTAREA)) {
			edec = new HedTEXTAREA(this);

		}
		else if (elementName.equalsIgnoreCase(TITLE)) {
			edec = new HedTITLE(this);

		}
		else if (elementName.equalsIgnoreCase(UL)) {
			edec = new HedUL(this);

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

	/**
	 */
	public final Collection getNamesOfBlock() {
		// P | %list | %preformatted | DL | DIV | CENTER | BLOCKQUOTE | FORM | HR
		String[] blockMisc = {P, DL, DIV, CENTER, BLOCKQUOTE, FORM, HR,};
		Vector blockNames = new Vector(Arrays.asList(blockMisc));
		// %heading;
		blockNames.addAll(Arrays.asList(HEADING));
		// %list;
		blockNames.addAll(Arrays.asList(LIST));
		// %preformatted;
		blockNames.addAll(Arrays.asList(PREFORMATTED));

		return blockNames;
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
	public final void getFlow(CMGroupImpl group) {
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
	public final void getFontstyle(CMGroupImpl group) {
		return;
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
	 * Create element declarations and store them
	 * into a <code>CMGroupImpl</code> instance.
	 * @param group CMGroupImpl Return values.
	 */
	public final void getInline(CMGroupImpl group) {
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
}
