/*******************************************************************************
 * Copyright (c) 2006 Oracle Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oracle Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.ui.preferences;

import org.eclipse.wst.xsl.internal.XSLUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

/**
 * @author Michal Chmielewski (michal.chmielewski@oracle.com)
 * @date Oct 27, 2006
 *
 */
public class PreferenceConstants {

	/**
	 * Matching brackets enabled
	 */
	static final public String EDITOR_MATCHING_BRACKETS = "org.eclipse.bpel.ui.xpath.editor.MatchingBrackets"; //$NON-NLS-1$
	
	/**
	 * The matching brackets color.
	 */
	static final public String EDITOR_MATCHING_BRACKETS_COLOR = "org.eclipse.bpel.ui.xpath.editor.MatchingBracketsColor"; //$NON-NLS-1$

	/**
	 * A named preference that controls whether the 'close strings' feature
	 *  is   enabled.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * 
	 */
	static final public String EDITOR_CLOSE_STRINGS = "closeString"; //$NON-NLS-1$
	
	/**
	 * A named preference that controls whether the 'close brackets' feature is
	 * enabled.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * @since 2.1
	 */
	public final static String EDITOR_CLOSE_BRACKETS= "closeBrackets"; //$NON-NLS-1$
	
	
	
	/**
	 * AXIS - Color for AXIS
	 */
	public final static RGB AXIS = new RGB(0, 136, 0);

	/**
	 * BRACKET
	 */
	public final static RGB BRACKET = new RGB(0, 0, 0);

	/**
	 * DEFAULT
	 */
	public final static RGB DEFAULT = new RGB(0, 0, 0);

	/**
	 * NUMBER
	 */
	public final static RGB NUMBER = new RGB(100, 100, 100);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB OPERAND = new RGB(155, 68, 208);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB PARENS = new RGB(255, 0, 0);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB PATH_SEPARATOR = new RGB(0, 0, 255);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB STRING = new RGB(0, 136, 0);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB TAG = new RGB(0, 0, 128);

	/**
	 * TODO: Add Java Doc
	 */
	public final static RGB FUNCTIONS_XPATH = new RGB(0,0,200);

	/**
	 * TODO: Add Java Doc
	 */
	public static final RGB VARIABLES = new RGB(10,10,10);
	
	/**
	 * TODO: Add Java Doc
	 */
	public static final RGB VARIABLE_PART = new RGB(0xe0,80,0);
	
	
	
	/**
	 * Initializes the given preference store with the default values.
	 * 
	 * @param store the preference store to be initialized
	 * 
	 * @since 2.1
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		
		// set the default values from AbstractDecoratedTextEditor
		AbstractDecoratedTextEditorPreferenceConstants.initializeDefaultValues(store);
		
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_STRINGS, true);
		store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
	}

	/**
	 * Returns the preference store.
	 * 
	 * @return the preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		return XSLUIPlugin.getInstance().getPreferenceStore();
	}	
	
}
