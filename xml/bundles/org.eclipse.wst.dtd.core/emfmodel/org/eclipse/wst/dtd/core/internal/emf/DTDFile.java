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

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.ENamedElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>File</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#isParseError <em>Parse Error</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDContent <em>DTD Content</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDEnumerationType <em>DTD Enumeration Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDFile()
 * @model
 * @generated
 */
public interface DTDFile extends ENamedElement, DTDObject {

	public EList getDTDObject();

	public List listDTDElement();

	public List listDTDNotation();

	public List listDTDEntity();

	public List listDTDParameterEntityReference();

	public List listDTDElementAndDTDParameterEntityReference();

	public DTDElement findElement(String name);

	public DTDEntity findEntity(String name);

	public DTDNotation findNotation(String name);

	public DTDParameterEntityReference findParameterEntityReference(String name);

	public String unparse(boolean includeError);

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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDFile_Comment()
	 * @model
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getComment <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Parse Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parse Error</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parse Error</em>' attribute.
	 * @see #setParseError(boolean)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDFile_ParseError()
	 * @model
	 * @generated
	 */
	boolean isParseError();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#isParseError <em>Parse Error</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parse Error</em>' attribute.
	 * @see #isParseError()
	 * @generated
	 */
	void setParseError(boolean value);

	/**
	 * Returns the value of the '<em><b>DTD Content</b></em>' containment
	 * reference list. The list contents are of type {@link DTDContent}. It
	 * is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile <em>DTD File</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD Content</em>' containment reference
	 * list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD Content</em>' containment
	 *         reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDFile_DTDContent()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile
	 * @model type="DTDContent" opposite="DTDFile" containment="true"
	 * @generated
	 */
	EList getDTDContent();

	/**
	 * Returns the value of the '<em><b>DTD Enumeration Type</b></em>'
	 * containment reference list. The list contents are of type
	 * {@link DTDEnumerationType}. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile <em>DTD File</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD Enumeration Type</em>' containment
	 * reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD Enumeration Type</em>'
	 *         containment reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDFile_DTDEnumerationType()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile
	 * @model type="DTDEnumerationType" opposite="DTDFile" containment="true"
	 * @generated
	 */
	EList getDTDEnumerationType();

} // DTDFile
