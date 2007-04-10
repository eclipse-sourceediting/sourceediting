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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Repeatable Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent#getOccurrence <em>Occurrence</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDRepeatableContent()
 * @model abstract="true"
 * @generated
 */
public interface DTDRepeatableContent extends DTDElementContent {

	public abstract String unparseRepeatableContent();

	/**
	 * Returns the value of the '<em><b>Occurrence</b></em>' attribute.
	 * The literals are from the enumeration
	 * {@link org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Occurrence</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Occurrence</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType
	 * @see #setOccurrence(DTDOccurrenceType)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDRepeatableContent_Occurrence()
	 * @model
	 * @generated
	 */
	DTDOccurrenceType getOccurrence();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent#getOccurrence <em>Occurrence</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Occurrence</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType
	 * @see #getOccurrence()
	 * @generated
	 */
	void setOccurrence(DTDOccurrenceType value);

} // DTDRepeatableContent
