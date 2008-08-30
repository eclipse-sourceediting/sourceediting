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
 * $Id: Argument.java,v 1.4 2008/08/30 03:41:05 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function;

import org.eclipse.emf.common.util.EList;

import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Argument</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getDataType <em>Data Type</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getComponetList <em>Componet</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage#getArgument()
 * @model
 * @generated
 */
public interface Argument extends XpathCommon {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * Returns the value of the '<em><b>Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Data Type</em>' attribute.
	 * @see #setDataType(String)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage#getArgument_DataType()
	 * @model required="true"
	 * @generated
	 */
	String getDataType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getDataType <em>Data Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Type</em>' attribute.
	 * @see #getDataType()
	 * @generated
	 */
	void setDataType(String value);

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence
	 * @see #setRequired(Occurrence)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage#getArgument_Required()
	 * @model required="true"
	 * @generated
	 */
	Occurrence getRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Argument#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.Occurrence
	 * @see #getRequired()
	 * @generated
	 */
	void setRequired(Occurrence value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Component[] getComponet();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Component getComponet(int index);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	int getComponetLength();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	void setComponet(Component[] newComponet);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	void setComponet(int index, Component element);

	/**
	 * Returns the value of the '<em><b>Componet</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Componet</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Componet</em>' reference list.
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.Function.FunctionPackage#getArgument_Componet()
	 * @model
	 * @generated
	 */
	EList<Component> getComponetList();

} // Argument
