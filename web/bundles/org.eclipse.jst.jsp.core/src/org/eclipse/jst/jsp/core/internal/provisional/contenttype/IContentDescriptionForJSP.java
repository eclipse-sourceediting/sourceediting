/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.provisional.contenttype;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.sse.core.internal.encoding.ICodedResourcePlugin;


public interface IContentDescriptionForJSP {
	/**
	 * Extra properties as part of ContentDescription, if the content is JSP.
	 */
	public final static QualifiedName CONTENT_TYPE_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "contentTypeAttribute"); //$NON-NLS-1$
	public final static QualifiedName LANGUAGE_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "languageAttribute"); //$NON-NLS-1$
	public final static QualifiedName CONTENT_FAMILY_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "contentFamilyAttribute"); //$NON-NLS-1$;

}
