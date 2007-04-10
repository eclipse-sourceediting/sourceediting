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
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;


/**
 * 
 */
public interface CSSSourceFormatter {

	/**
	 * 
	 */
	StringBuffer cleanup(ICSSNode node);

	/**
	 * 
	 * @return java.lang.StringBuffer
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param region
	 *            org.eclipse.jface.text.IRegion
	 */
	StringBuffer cleanup(ICSSNode node, IRegion region);

	/**
	 * 
	 */
	StringBuffer format(ICSSNode node);

	/**
	 * 
	 */
	StringBuffer format(ICSSNode node, IRegion region);
}
