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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Group Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getGroupKind <em>Group Kind</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getContent <em>Content</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDGroupContent()
 * @model
 * @generated
 */
public interface DTDGroupContent extends DTDRepeatableContent {

	public int getContentPosition(DTDElementContent content);

	/**
	 * Returns the value of the '<em><b>Group Kind</b></em>' attribute.
	 * The literals are from the enumeration
	 * {@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind}. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Group Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Group Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind
	 * @see #setGroupKind(DTDGroupKind)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDGroupContent_GroupKind()
	 * @model
	 * @generated
	 */
	DTDGroupKind getGroupKind();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getGroupKind <em>Group Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Group Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind
	 * @see #getGroupKind()
	 * @generated
	 */
	void setGroupKind(DTDGroupKind value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' containment
	 * reference list. The list contents are of type {@link DTDElementContent}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Content</em>' containment reference
	 *         list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDGroupContent_Content()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup
	 * @model type="DTDElementContent" opposite="group" containment="true"
	 *        required="true"
	 * @generated
	 */
	EList getContent();

} // DTDGroupContent
