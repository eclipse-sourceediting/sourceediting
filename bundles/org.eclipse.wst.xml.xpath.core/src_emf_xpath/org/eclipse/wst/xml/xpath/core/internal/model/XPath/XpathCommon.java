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
 * $Id: XpathCommon.java,v 1.3 2008/08/30 03:41:07 dacarver Exp $
 */
package org.eclipse.wst.xml.xpath.core.internal.model.XPath;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xpath Common</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginColumn <em>Begin Column</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndColumn <em>End Column</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginLineNumber <em>Begin Line Number</em>}</li>
 *   <li>{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndLineNumber <em>End Line Number</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#getXpathCommon()
 * @model abstract="true"
 * @generated
 */
public interface XpathCommon extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = " Copyright (c) 2008 Standards for Technology in Automotive Retail and others.\n All rights reserved. This program and the accompanying materials\n are made available under the terms of the Eclipse Public License v1.0\n which accompanies this distribution, and is available at\n http://www.eclipse.org/legal/epl-v10.html\n\n Contributors:\n     David Carver - initial API and implementation";

	/**
	 * Returns the value of the '<em><b>Begin Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Begin Column</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Begin Column</em>' attribute.
	 * @see #setBeginColumn(int)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#getXpathCommon_BeginColumn()
	 * @model required="true"
	 * @generated
	 */
	int getBeginColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginColumn <em>Begin Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Begin Column</em>' attribute.
	 * @see #getBeginColumn()
	 * @generated
	 */
	void setBeginColumn(int value);

	/**
	 * Returns the value of the '<em><b>End Column</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>End Column</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Column</em>' attribute.
	 * @see #setEndColumn(int)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#getXpathCommon_EndColumn()
	 * @model required="true"
	 * @generated
	 */
	int getEndColumn();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndColumn <em>End Column</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Column</em>' attribute.
	 * @see #getEndColumn()
	 * @generated
	 */
	void setEndColumn(int value);

	/**
	 * Returns the value of the '<em><b>Begin Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Begin Line Number</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Begin Line Number</em>' attribute.
	 * @see #setBeginLineNumber(int)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#getXpathCommon_BeginLineNumber()
	 * @model required="true"
	 * @generated
	 */
	int getBeginLineNumber();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getBeginLineNumber <em>Begin Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Begin Line Number</em>' attribute.
	 * @see #getBeginLineNumber()
	 * @generated
	 */
	void setBeginLineNumber(int value);

	/**
	 * Returns the value of the '<em><b>End Line Number</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>End Line Number</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Line Number</em>' attribute.
	 * @see #setEndLineNumber(int)
	 * @see org.eclipse.wst.xml.xpath.core.internal.model.XPath.xpathPackage#getXpathCommon_EndLineNumber()
	 * @model required="true"
	 * @generated
	 */
	int getEndLineNumber();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.xml.xpath.core.internal.model.XPath.XpathCommon#getEndLineNumber <em>End Line Number</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Line Number</em>' attribute.
	 * @see #getEndLineNumber()
	 * @generated
	 */
	void setEndLineNumber(int value);

} // XpathCommon
