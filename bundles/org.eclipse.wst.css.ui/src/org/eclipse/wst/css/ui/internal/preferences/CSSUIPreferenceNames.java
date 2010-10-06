/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.preferences;


/**
 * Preference keys for CSS UI
 */
public class CSSUIPreferenceNames {
	private CSSUIPreferenceNames() {
		// cannot create instance
	}
	/**
	 * The key to store customized templates.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String TEMPLATES_KEY = "org.eclipse.wst.sse.ui.custom_templates"; //$NON-NLS-1$
	
	/**
	 * The key to store the last template name used in new CSS file wizard.
	 * Template name is stored instead of template id because user-created
	 * templates do not have template ids.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String NEW_FILE_TEMPLATE_NAME = "newFileTemplateName"; //$NON-NLS-1$

	/**
	 * The initial template ID to be used in the new CSS file wizard. In the absence
	 * of {@link NEW_FILE_TEMPLATE_NAME}, this ID is used to find a template name
	 */
	public static final String NEW_FILE_TEMPLATE_ID = "newFileTemplateId"; //$NON-NLS-1$
}
