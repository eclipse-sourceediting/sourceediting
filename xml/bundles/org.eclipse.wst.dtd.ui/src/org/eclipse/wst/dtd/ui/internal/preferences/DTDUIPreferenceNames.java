/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.preferences;

/**
 * Preference keys for DTD UI
 */
public class DTDUIPreferenceNames {
	private DTDUIPreferenceNames() {
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
	 * The key to store the last template name used in new DTD file wizard.
	 * Template name is stored instead of template id because user-created
	 * templates do not have template ids.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String NEW_FILE_TEMPLATE_NAME = "newFileTemplateName"; //$NON-NLS-1$

	public static final String ACTIVATE_PROPERTIES = "activateProperties"; //$NON-NLS-1$
}
