/**
 *  Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      David Carver - initial API and implementation
 *
 * $Id: Axis.java,v 1.4 2008/08/30 03:41:05 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis#getShortcut <em>Shortcut</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage#getAxis()
 * @model
 * @generated
 */
public interface Axis extends XpathCommon, Component {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage#getAxis_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Shortcut</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shortcut</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shortcut</em>' attribute.
	 * @see #setShortcut(String)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.AxisPackage#getAxis_Shortcut()
	 * @model
	 * @generated
	 */
	String getShortcut();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Axis.Axis#getShortcut <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shortcut</em>' attribute.
	 * @see #getShortcut()
	 * @generated
	 */
	void setShortcut(String value);

} // Axis
