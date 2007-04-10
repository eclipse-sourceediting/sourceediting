/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.provisional.document;



import org.eclipse.wst.css.core.internal.event.ICSSStyleNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.w3c.dom.Node;


/**
 * 
 */
public interface ICSSModel extends ICSSStyleNotifier, IStructuredModel {

	public static final String EXTERNAL = "externalCSS"; //$NON-NLS-1$
	public static final String EMBEDDED = "embeddedCSS"; //$NON-NLS-1$
	public static final String INLINE = "inlineCSS"; //$NON-NLS-1$

	ICSSDocument getDocument();

	/**
	 * @return org.w3c.dom.Node
	 */
	Node getOwnerDOMNode();

	/**
	 * 
	 * @return java.lang.Object
	 */
	Object getStyleSheetType();

	/**
	 * cleanup -> rebuild CSS Nodes This is pre-beta fix for 178176.
	 */
	void refreshNodes();
}
