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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc
 * -->
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage
 * @generated
 */
public interface DTDFactory extends EFactory {

	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	DTDFactory eINSTANCE = org.eclipse.wst.dtd.core.internal.emf.impl.DTDFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Group Content</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Group Content</em>'.
	 * @generated
	 */
	DTDGroupContent createDTDGroupContent();

	/**
	 * Returns a new object of class '<em>Attribute</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Attribute</em>'.
	 * @generated
	 */
	DTDAttribute createDTDAttribute();

	/**
	 * Returns a new object of class '<em>Element</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Element</em>'.
	 * @generated
	 */
	DTDElement createDTDElement();

	/**
	 * Returns a new object of class '<em>Empty Content</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Empty Content</em>'.
	 * @generated
	 */
	DTDEmptyContent createDTDEmptyContent();

	/**
	 * Returns a new object of class '<em>Any Content</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Any Content</em>'.
	 * @generated
	 */
	DTDAnyContent createDTDAnyContent();

	/**
	 * Returns a new object of class '<em>PC Data Content</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>PC Data Content</em>'.
	 * @generated
	 */
	DTDPCDataContent createDTDPCDataContent();

	/**
	 * Returns a new object of class '<em>Element Reference Content</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Element Reference Content</em>'.
	 * @generated
	 */
	DTDElementReferenceContent createDTDElementReferenceContent();

	/**
	 * Returns a new object of class '<em>File</em>'. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>File</em>'.
	 * @generated
	 */
	DTDFile createDTDFile();

	/**
	 * Returns a new object of class '<em>Basic Type</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Basic Type</em>'.
	 * @generated
	 */
	DTDBasicType createDTDBasicType();

	/**
	 * Returns a new object of class '<em>Enumeration Type</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Enumeration Type</em>'.
	 * @generated
	 */
	DTDEnumerationType createDTDEnumerationType();

	/**
	 * Returns a new object of class '<em>Notation</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Notation</em>'.
	 * @generated
	 */
	DTDNotation createDTDNotation();

	/**
	 * Returns a new object of class '<em>Entity</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Entity</em>'.
	 * @generated
	 */
	DTDEntity createDTDEntity();

	/**
	 * Returns a new object of class '<em>External Entity</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>External Entity</em>'.
	 * @generated
	 */
	DTDExternalEntity createDTDExternalEntity();

	/**
	 * Returns a new object of class '<em>Internal Entity</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Internal Entity</em>'.
	 * @generated
	 */
	DTDInternalEntity createDTDInternalEntity();

	/**
	 * Returns a new object of class '<em>Parameter Entity Reference</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Parameter Entity Reference</em>'.
	 * @generated
	 */
	DTDParameterEntityReference createDTDParameterEntityReference();

	/**
	 * Returns a new object of class '<em>Entity Reference Content</em>'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>Entity Reference Content</em>'.
	 * @generated
	 */
	DTDEntityReferenceContent createDTDEntityReferenceContent();

	/**
	 * Returns the package supported by this factory. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the package supported by this factory.
	 * @generated
	 */
	DTDPackage getDTDPackage();

} // DTDFactory
