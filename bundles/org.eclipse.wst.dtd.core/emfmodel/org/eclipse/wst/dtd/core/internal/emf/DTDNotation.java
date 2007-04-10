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

package org.eclipse.wst.dtd.core.internal.emf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.ENamedElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Notation</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getSystemID <em>System ID</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getPublicID <em>Public ID</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getEntity <em>Entity</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDNotation()
 * @model
 * @generated
 */
public interface DTDNotation extends ENamedElement, DTDContent, DTDObject, DTDSourceOffset {

	/**
	 * Returns the value of the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Comment</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Comment</em>' attribute.
	 * @see #setComment(String)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDNotation_Comment()
	 * @model
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getComment <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>System ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>System ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>System ID</em>' attribute.
	 * @see #setSystemID(String)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDNotation_SystemID()
	 * @model
	 * @generated
	 */
	String getSystemID();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getSystemID <em>System ID</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>System ID</em>' attribute.
	 * @see #getSystemID()
	 * @generated
	 */
	void setSystemID(String value);

	/**
	 * Returns the value of the '<em><b>Public ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Public ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Public ID</em>' attribute.
	 * @see #setPublicID(String)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDNotation_PublicID()
	 * @model
	 * @generated
	 */
	String getPublicID();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getPublicID <em>Public ID</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Public ID</em>' attribute.
	 * @see #getPublicID()
	 * @generated
	 */
	void setPublicID(String value);

	/**
	 * Returns the value of the '<em><b>Entity</b></em>' reference list.
	 * The list contents are of type {@link DTDExternalEntity}. It is
	 * bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation <em>Notation</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entity</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Entity</em>' reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDNotation_Entity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation
	 * @model type="DTDExternalEntity" opposite="notation"
	 * @generated
	 */
	EList getEntity();

} // DTDNotation
