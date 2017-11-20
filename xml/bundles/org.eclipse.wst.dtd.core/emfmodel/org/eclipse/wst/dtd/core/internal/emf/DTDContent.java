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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile <em>DTD File</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDContent()
 * @model abstract="true"
 * @generated
 */
public interface DTDContent extends EObject {
	/**
	 * Returns the value of the '<em><b>DTD File</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDContent <em>DTD Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD File</em>' container reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD File</em>' container reference.
	 * @see #setDTDFile(DTDFile)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDContent_DTDFile()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDContent
	 * @model opposite="DTDContent"
	 * @generated
	 */
	DTDFile getDTDFile();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile <em>DTD File</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>DTD File</em>' container
	 *            reference.
	 * @see #getDTDFile()
	 * @generated
	 */
	void setDTDFile(DTDFile value);

} // DTDContent
