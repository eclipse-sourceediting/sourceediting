/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.provisional.contenttype;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.sse.core.internal.encoding.ICodedResourcePlugin;
/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public interface IContentDescriptionForJSP {
	public final static QualifiedName CONTENT_FAMILY_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "contentFamilyAttribute"); //$NON-NLS-1$;
	/**
	 * Extra properties as part of ContentDescription, if the content is JSP.
	 */
	public final static QualifiedName CONTENT_TYPE_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "contentTypeAttribute"); //$NON-NLS-1$
	public final static QualifiedName LANGUAGE_ATTRIBUTE = new QualifiedName(ICodedResourcePlugin.ID, "languageAttribute"); //$NON-NLS-1$
}
