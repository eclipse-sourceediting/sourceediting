/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.templates;

import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.wst.xml.ui.templates.TemplateContextTypeIds;

/**
 * Templates of this context type apply to any attributes within JSP content type.
 */
public class TemplateContextTypeJSPAttribute extends TemplateContextTypeJSP {

	public TemplateContextTypeJSPAttribute() {
		super(generateContextTypeId(TemplateContextTypeIds.ATTRIBUTE), JSPUIPlugin.getResourceString("%TemplateContextTypeJSPAttribute.0")); //$NON-NLS-1$
	}
}
