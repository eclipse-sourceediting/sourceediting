/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function;

import org.eclipse.emf.common.util.EList;

import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Component;
import org.eclipse.wst.xsl.core.internal.model.xpath.XPath.XpathCommon;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Argument</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getComponet <em>Componet</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionPackage#getArgument()
 * @model
 * @generated
 */
public interface Argument extends XpathCommon {
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
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionPackage#getArgument_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Required</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required</em>' attribute.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence
	 * @see #setRequired(Occurrence)
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionPackage#getArgument_Required()
	 * @model required="true"
	 * @generated
	 */
	Occurrence getRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Argument#getRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.Occurrence
	 * @see #getRequired()
	 * @generated
	 */
	void setRequired(Occurrence value);

	/**
	 * Returns the value of the '<em><b>Componet</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Component}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Componet</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Componet</em>' reference list.
	 * @see org.eclipse.wst.xsl.core.internal.model.xpath.XPath.Function.FunctionPackage#getArgument_Componet()
	 * @model
	 * @generated
	 */
	EList<Component> getComponet();

} // Argument
