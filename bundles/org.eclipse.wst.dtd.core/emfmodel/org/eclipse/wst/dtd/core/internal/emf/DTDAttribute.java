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

import org.eclipse.emf.ecore.EAttribute;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultKind <em>Default Kind</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultValueString <em>Default Value String</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement <em>DTD Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute()
 * @model
 * @generated
 */
public interface DTDAttribute extends EAttribute, DTDObject, DTDSourceOffset {
	// NON-GEN interfaces DTDObject, DTDSourceOffset

	public DTDType getDTDType();

	public void setDTDType(DTDType type);

	public void setDTDBasicType(int value);

	public DTDEnumerationType createDTDEnumeration(String[] enumValues, int enumKind);

	public Collection getEnumeratedValues();

	public String unparse();

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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_Comment()
	 * @model
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getComment <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Default Kind</b></em>' attribute.
	 * The literals are from the enumeration
	 * {@link org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Kind</em>' attribute isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Default Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind
	 * @see #setDefaultKind(DTDDefaultKind)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_DefaultKind()
	 * @model
	 * @generated
	 */
	DTDDefaultKind getDefaultKind();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultKind <em>Default Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind
	 * @see #getDefaultKind()
	 * @generated
	 */
	void setDefaultKind(DTDDefaultKind value);

	/**
	 * Returns the value of the '<em><b>Default Value String</b></em>'
	 * attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Value String</em>' attribute
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Default Value String</em>' attribute.
	 * @see #setDefaultValueString(String)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_DefaultValueString()
	 * @model
	 * @generated
	 */
	String getDefaultValueString();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultValueString <em>Default Value String</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Value String</em>'
	 *            attribute.
	 * @see #getDefaultValueString()
	 * @generated
	 */
	void setDefaultValueString(String value);

	/**
	 * Returns the value of the '<em><b>Attribute Name Referenced Entity</b></em>'
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeNameReference <em>Attribute Name Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Name Referenced Entity</em>'
	 * reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Attribute Name Referenced Entity</em>'
	 *         reference.
	 * @see #setAttributeNameReferencedEntity(DTDEntity)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_AttributeNameReferencedEntity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeNameReference
	 * @model opposite="attributeNameReference"
	 * @generated
	 */
	DTDEntity getAttributeNameReferencedEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Attribute Name Referenced Entity</em>'
	 *            reference.
	 * @see #getAttributeNameReferencedEntity()
	 * @generated
	 */
	void setAttributeNameReferencedEntity(DTDEntity value);

	/**
	 * Returns the value of the '<em><b>Attribute Type Referenced Entity</b></em>'
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeTypeReference <em>Attribute Type Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Type Referenced Entity</em>'
	 * reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Attribute Type Referenced Entity</em>'
	 *         reference.
	 * @see #setAttributeTypeReferencedEntity(DTDEntity)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_AttributeTypeReferencedEntity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeTypeReference
	 * @model opposite="attributeTypeReference"
	 * @generated
	 */
	DTDEntity getAttributeTypeReferencedEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Attribute Type Referenced Entity</em>'
	 *            reference.
	 * @see #getAttributeTypeReferencedEntity()
	 * @generated
	 */
	void setAttributeTypeReferencedEntity(DTDEntity value);

	/**
	 * Returns the value of the '<em><b>DTD Element</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getDTDAttribute <em>DTD Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD Element</em>' container reference
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD Element</em>' container
	 *         reference.
	 * @see #setDTDElement(DTDElement)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDAttribute_DTDElement()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement#getDTDAttribute
	 * @model opposite="DTDAttribute"
	 * @generated
	 */
	DTDElement getDTDElement();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement <em>DTD Element</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>DTD Element</em>' container
	 *            reference.
	 * @see #getDTDElement()
	 * @generated
	 */
	void setDTDElement(DTDElement value);

} // DTDAttribute
