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
package org.eclipse.wst.css.core.internal.util.declaration;



import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.w3c.dom.css.CSS2Properties;


/**
 * 
 */
public interface ICSS2Properties extends CSS2Properties {

	/**
	 * 
	 */
	void applyFull(ICSSStyleDeclaration decl);

	/**
	 * 
	 */
	void applyModified(ICSSStyleDeclaration decl);

	/**
	 * 
	 */
	String get(PropCMProperty prop);

	/**
	 * 
	 */
	String getBackgroundPositionX();

	/**
	 * 
	 */
	String getBackgroundPositionY();

	/**
	 * 
	 */
	String getClipBottom();

	/**
	 * 
	 */
	String getClipLeft();

	/**
	 * 
	 */
	String getClipRight();

	/**
	 * 
	 */
	String getClipTop();

	/**
	 * 
	 */
	boolean isModified();

	/**
	 * 
	 */
	Enumeration properties();

	/**
	 * 
	 */
	Iterator propertiesModified();

	/**
	 * 
	 */
	void resetModified();

	/**
	 * 
	 */
	void set(PropCMProperty prop, String value) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setBackgroundPositionX(String backgroundPosition) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setBackgroundPositionY(String backgroundPosition) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setClipBottom(String backgroundPosition) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setClipLeft(String backgroundPosition) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setClipRight(String backgroundPosition) throws org.w3c.dom.DOMException;

	/**
	 * 
	 */
	void setClipTop(String backgroundPosition) throws org.w3c.dom.DOMException;
}
