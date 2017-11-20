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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Entity Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity <em>DTD Entity</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntityContent()
 * @model abstract="true"
 * @generated
 */
public interface DTDEntityContent extends EObject, DTDObject {

	public String unparse();

	/**
	 * Returns the value of the '<em><b>DTD Entity</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD Entity</em>' container reference
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD Entity</em>' container reference.
	 * @see #setDTDEntity(DTDEntity)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntityContent_DTDEntity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent
	 * @model opposite="content"
	 * @generated
	 */
	DTDEntity getDTDEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity <em>DTD Entity</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>DTD Entity</em>' container
	 *            reference.
	 * @see #getDTDEntity()
	 * @generated
	 */
	void setDTDEntity(DTDEntity value);

} // DTDEntityContent
