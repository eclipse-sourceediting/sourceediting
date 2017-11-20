/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.provisional.document;

public interface IDOMImplementation {

	/**
	 * NOT IMPLEMENTED. This is defined here in preparation of DOM 3.
	 * 
	 * This method returns a specialized object which implements the
	 * specialized APIs of the specified feature and version, as specified in .
	 * The specialized object may also be obtained by using binding-specific
	 * casting methods but is not necessarily expected to, as discussed in .
	 * This method also allow the implementation to provide specialized
	 * objects which do not support the <code>DOMImplementation</code>
	 * interface.
	 * 
	 * @param feature
	 *            The name of the feature requested. Note that any plus sign
	 *            "+" prepended to the name of the feature will be ignored
	 *            since it is not significant in the context of this method.
	 * @param version
	 *            This is the version number of the feature to test.
	 * @return Returns an object which implements the specialized APIs of the
	 *         specified feature and version, if any, or <code>null</code>
	 *         if there is no object which implements interfaces associated
	 *         with that feature. If the <code>DOMObject</code> returned by
	 *         this method implements the <code>DOMImplementation</code>
	 *         interface, it must delegate to the primary core
	 *         <code>DOMImplementation</code> and not return results
	 *         inconsistent with the primary core
	 *         <code>DOMImplementation</code> such as
	 *         <code>hasFeature</code>, <code>getFeature</code>, etc.
	 * @see DOM Level 3
	 */
	public Object getFeature(String feature, String version);

}
