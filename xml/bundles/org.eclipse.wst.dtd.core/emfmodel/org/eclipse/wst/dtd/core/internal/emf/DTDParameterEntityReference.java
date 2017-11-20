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

import org.eclipse.emf.ecore.ENamedElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Parameter Entity Reference</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity <em>Entity</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDParameterEntityReference()
 * @model
 * @generated
 */
public interface DTDParameterEntityReference extends ENamedElement, DTDContent, DTDObject, DTDSourceOffset {
	/**
	 * Returns the value of the '<em><b>Entity</b></em>' reference. It is
	 * bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef <em>Parm Entity Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entity</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Entity</em>' reference.
	 * @see #setEntity(DTDEntity)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDParameterEntityReference_Entity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef
	 * @model opposite="parmEntityRef" required="true"
	 * @generated
	 */
	DTDEntity getEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity <em>Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Entity</em>' reference.
	 * @see #getEntity()
	 * @generated
	 */
	void setEntity(DTDEntity value);

} // DTDParameterEntityReference
