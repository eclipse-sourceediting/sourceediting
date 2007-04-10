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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>External Entity</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getSystemID <em>System ID</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getPublicID <em>Public ID</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation <em>Notation</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getEntityReferencedFromAnotherFile <em>Entity Referenced From Another File</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDExternalEntity()
 * @model
 * @generated
 */
public interface DTDExternalEntity extends DTDEntityContent {

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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDExternalEntity_SystemID()
	 * @model
	 * @generated
	 */
	String getSystemID();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getSystemID <em>System ID</em>}'
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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDExternalEntity_PublicID()
	 * @model
	 * @generated
	 */
	String getPublicID();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getPublicID <em>Public ID</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Public ID</em>' attribute.
	 * @see #getPublicID()
	 * @generated
	 */
	void setPublicID(String value);

	/**
	 * Returns the value of the '<em><b>Notation</b></em>' reference. It
	 * is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Notation</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Notation</em>' reference.
	 * @see #setNotation(DTDNotation)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDExternalEntity_Notation()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getEntity
	 * @model opposite="entity"
	 * @generated
	 */
	DTDNotation getNotation();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation <em>Notation</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Notation</em>' reference.
	 * @see #getNotation()
	 * @generated
	 */
	void setNotation(DTDNotation value);

	/**
	 * Returns the value of the '<em><b>Entity Referenced From Another File</b></em>'
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entity Referenced From Another File</em>'
	 * reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Entity Referenced From Another File</em>'
	 *         reference.
	 * @see #setEntityReferencedFromAnotherFile(DTDFile)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDExternalEntity_EntityReferencedFromAnotherFile()
	 * @model
	 * @generated
	 */
	DTDFile getEntityReferencedFromAnotherFile();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getEntityReferencedFromAnotherFile <em>Entity Referenced From Another File</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Entity Referenced From Another File</em>'
	 *            reference.
	 * @see #getEntityReferencedFromAnotherFile()
	 * @generated
	 */
	void setEntityReferencedFromAnotherFile(DTDFile value);

} // DTDExternalEntity

