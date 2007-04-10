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



/**
 * Factory of ComplexTypeDefinition.
 */
final class ComplexTypeDefinitionFactory {

	private static ComplexTypeDefinitionFactory instance = null;
	private java.util.Hashtable definitions = null;
	// constants for complex type name
	/** for ADDRESS. */
	public final static String CTYPE_ADDRESS = "CTYPE_ADDRESS";//$NON-NLS-1$
	/** CDATA content. No ComplexTypeDefinition instance shuld be created. */
	public final static String CTYPE_CDATA = "CTYPE_CDATA";//$NON-NLS-1$
	/** col group content. COL* */
	public final static String CTYPE_COLUMN_GROUP = "CTYPE_COLUMN_GROUP";//$NON-NLS-1$
	/** for DL. */
	public final static String CTYPE_DEFINITION_LIST = "CTYPE_DEFINITION_LIST";//$NON-NLS-1$
	/** for EMBED. */
	public final static String CTYPE_EMBED = "CTYPE_EMBED";//$NON-NLS-1$
	/** empty content. No ComplexTypeDefinition instance should be created. */
	public final static String CTYPE_EMPTY = "CTYPE_EMPTY";//$NON-NLS-1$
	/** for FIELDSET. */
	public final static String CTYPE_FIELDSET = "CTYPE_FIELDSET";//$NON-NLS-1$
	/** for FRAMESET. */
	public final static String CTYPE_FRAMESET = "CTYPE_FRAMESET";//$NON-NLS-1$
	/** flow content. (%flow;)* */
	public final static String CTYPE_FLOW_CONTAINER = "CTYPE_FLOW_CONTAINER";//$NON-NLS-1$
	/** html content. HEAD, (FRAMESET|BODY) */
	public final static String CTYPE_HTML = "CTYPE_HTML";//$NON-NLS-1$
	/** head content. TITLE & ISINDEX? & BASE? */
	public final static String CTYPE_HEAD = "CTYPE_HEAD";//$NON-NLS-1$
	/** inline content. (%inline;)* */
	public final static String CTYPE_INLINE_CONTAINER = "CTYPE_INLINE_CONTAINER";//$NON-NLS-1$
	/** list item container. (LI)+ */
	public final static String CTYPE_LI_CONTAINER = "CTYPE_LI_CONTAINER";//$NON-NLS-1$
	/** for MAP. */
	public final static String CTYPE_MAP = "CTYPE_MAP";//$NON-NLS-1$
	/** noframes content. */
	public final static String CTYPE_NOFRAMES_CONTENT = "CTYPE_NOFRAMES_CONTENT";//$NON-NLS-1$
	/** for OPTGROUP. */
	public final static String CTYPE_OPTION_CONTAINER = "CTYPE_OPTION_CONTAINER";//$NON-NLS-1$
	/** param container. For OBJECT/APPLET. */
	public final static String CTYPE_PARAM_CONTAINER = "CTYPE_PARAM_CONTAINER";//$NON-NLS-1$
	/** PCDATA content. No ComplexTypeDefinition instance shuld be created. */
	public final static String CTYPE_PCDATA = "CTYPE_PCDATA";//$NON-NLS-1$
	/** for SELECT. */
	public final static String CTYPE_SELECT = "CTYPE_SELECT";//$NON-NLS-1$
	/** table content. CAPTION?, (COL*|COLGROUP*), THEAD?, TFOOT?, TBODY+ */
	public final static String CTYPE_TABLE = "CTYPE_TABLE";//$NON-NLS-1$
	/** table cell contaier. (TH|TD)+ */
	public final static String CTYPE_TCELL_CONTAINER = "CTYPE_TCELL_CONTAINER";//$NON-NLS-1$
	/** table record container. (TR)+ */
	public final static String CTYPE_TR_CONTAINER = "CTYPE_TR_CONTAINER";//$NON-NLS-1$

	/**
	 * ComplexTypeDefinitionFactory constructor comment.
	 */
	private ComplexTypeDefinitionFactory() {
		super();
		definitions = new java.util.Hashtable();
	}

	/**
	 * Factory method for ComplexTypeDefinition.
	 * Each instance created in this method must be registered into
	 * the map with its name.
	 * @param definitionName java.lang.String
	 * @param elementCollection ElementCollection
	 */
	public ComplexTypeDefinition createTypeDefinition(String definitionName, ElementCollection elementCollection) {
		if (definitions.containsKey(definitionName)) {
			return (ComplexTypeDefinition) definitions.get(definitionName);
		}

		ComplexTypeDefinition def = null;
		if (definitionName == CTYPE_ADDRESS) {
			def = new CtdAddress(elementCollection);

		}
		else if (definitionName == CTYPE_DEFINITION_LIST) {
			def = new CtdDl(elementCollection);

		}
		else if (definitionName == CTYPE_FLOW_CONTAINER) {
			def = new CtdFlowContainer(elementCollection);

		}
		else if (definitionName == CTYPE_HEAD) {
			def = new CtdHead(elementCollection);

		}
		else if (definitionName == CTYPE_HTML) {
			def = new CtdHtml(elementCollection);

		}
		else if (definitionName == CTYPE_INLINE_CONTAINER) {
			def = new CtdInlineContainer(elementCollection);

		}
		else if (definitionName == CTYPE_LI_CONTAINER) {
			def = new CtdLiContainer(elementCollection);


		}
		else if (definitionName == CTYPE_OPTION_CONTAINER) {
			def = new CtdOptionContainer(elementCollection);

		}
		else if (definitionName == CTYPE_SELECT) {
			def = new CtdSelect(elementCollection);

		}
		else {
			def = null;
		}
		if (def == null)
			return null; // fail to create.
		definitions.put(definitionName, def);
		return def;
	}

	/**
	 * For singleton.<br>
	 */
	public synchronized static ComplexTypeDefinitionFactory getInstance() {
		if (instance != null)
			return instance;
		instance = new ComplexTypeDefinitionFactory();
		return instance;
	}
}
