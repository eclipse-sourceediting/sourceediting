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
package org.eclipse.wst.css.core.internal.metamodel;



import java.util.Iterator;

public interface CSSMetaModel extends CSSMMNode {

	// public interface CSSMetaModel {
	CSSMMStyleSheet getStyleSheet();

	/*
	 * Shortcut to access property node
	 */
	// Iterator getProperties();
	// Iterator getDescriptors();
	// CSSMMProperty getProperty(String propertyName);
	/*
	 * access to category information
	 */
	Iterator getCategories();

	/*
	 * Utilities
	 */
	// Iterator collectNodesByType(String type);
}
