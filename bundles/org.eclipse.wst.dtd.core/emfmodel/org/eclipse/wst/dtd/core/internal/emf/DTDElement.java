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

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getDTDAttribute <em>DTD Attribute</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElement()
 * @model
 * @generated
 */
public interface DTDElement extends EClass, DTDContent, DTDObject, DTDSourceOffset {

	public void addDTDAttribute(DTDAttribute attribute);

	// returns all references in the same DTDFile that reference this element
	public Collection getReferences();

	// return a brief string listing the attributes
	public String getAttributeDetail();

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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElement_Comment()
	 * @model
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getComment <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' containment
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' containment reference
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Content</em>' containment reference.
	 * @see #setContent(DTDElementContent)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElement_Content()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement
	 * @model opposite="element" containment="true" required="true"
	 * @generated
	 */
	DTDElementContent getContent();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent <em>Content</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Content</em>' containment
	 *            reference.
	 * @see #getContent()
	 * @generated
	 */
	void setContent(DTDElementContent value);

	/**
	 * Returns the value of the '<em><b>DTD Attribute</b></em>'
	 * containment reference list. The list contents are of type
	 * {@link DTDAttribute}. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement <em>DTD Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD Attribute</em>' containment
	 * reference list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD Attribute</em>' containment
	 *         reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElement_DTDAttribute()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement
	 * @model type="DTDAttribute" opposite="DTDElement" containment="true"
	 * @generated
	 */
	EList getDTDAttribute();

} // DTDElement
