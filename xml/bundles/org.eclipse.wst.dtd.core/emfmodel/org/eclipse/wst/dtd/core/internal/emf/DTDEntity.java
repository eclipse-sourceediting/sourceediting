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
import org.eclipse.emf.ecore.ENamedElement;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#isParameterEntity <em>Parameter Entity</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent <em>Content</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef <em>Parm Entity Ref</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getEntityReference <em>Entity Reference</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeNameReference <em>Attribute Name Reference</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeTypeReference <em>Attribute Type Reference</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity()
 * @model
 * @generated
 */
public interface DTDEntity extends ENamedElement, DTDContent, DTDObject, DTDSourceOffset {
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
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_Comment()
	 * @model
	 * @generated
	 */
	String getComment();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getComment <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Comment</em>' attribute.
	 * @see #getComment()
	 * @generated
	 */
	void setComment(String value);

	/**
	 * Returns the value of the '<em><b>Parameter Entity</b></em>'
	 * attribute. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Entity</em>' attribute isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parameter Entity</em>' attribute.
	 * @see #setParameterEntity(boolean)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_ParameterEntity()
	 * @model
	 * @generated
	 */
	boolean isParameterEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#isParameterEntity <em>Parameter Entity</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parameter Entity</em>'
	 *            attribute.
	 * @see #isParameterEntity()
	 * @generated
	 */
	void setParameterEntity(boolean value);

	/**
	 * Returns the value of the '<em><b>Content</b></em>' containment
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity <em>DTD Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' containment reference
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Content</em>' containment reference.
	 * @see #setContent(DTDEntityContent)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_Content()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity
	 * @model opposite="DTDEntity" containment="true" required="true"
	 * @generated
	 */
	DTDEntityContent getContent();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent <em>Content</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Content</em>' containment
	 *            reference.
	 * @see #getContent()
	 * @generated
	 */
	void setContent(DTDEntityContent value);

	/**
	 * Returns the value of the '<em><b>Parm Entity Ref</b></em>'
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parm Entity Ref</em>' reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Parm Entity Ref</em>' reference.
	 * @see #setParmEntityRef(DTDParameterEntityReference)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_ParmEntityRef()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity
	 * @model opposite="entity" required="true"
	 * @generated
	 */
	DTDParameterEntityReference getParmEntityRef();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef <em>Parm Entity Ref</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Parm Entity Ref</em>'
	 *            reference.
	 * @see #getParmEntityRef()
	 * @generated
	 */
	void setParmEntityRef(DTDParameterEntityReference value);

	/**
	 * Returns the value of the '<em><b>Entity Reference</b></em>'
	 * reference list. The list contents are of type
	 * {@link DTDEntityReferenceContent}. It is bidirectional and its
	 * opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity <em>Element Referenced Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entity Reference</em>' reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Entity Reference</em>' reference
	 *         list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_EntityReference()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity
	 * @model type="DTDEntityReferenceContent"
	 *        opposite="elementReferencedEntity"
	 * @generated
	 */
	EList getEntityReference();

	/**
	 * Returns the value of the '<em><b>Attribute Name Reference</b></em>'
	 * reference list. The list contents are of type {@link DTDAttribute}. It
	 * is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Name Reference</em>' reference
	 * list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Attribute Name Reference</em>'
	 *         reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_AttributeNameReference()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity
	 * @model type="DTDAttribute" opposite="attributeNameReferencedEntity"
	 * @generated
	 */
	EList getAttributeNameReference();

	/**
	 * Returns the value of the '<em><b>Attribute Type Reference</b></em>'
	 * reference list. The list contents are of type {@link DTDAttribute}. It
	 * is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Type Reference</em>' reference
	 * list isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Attribute Type Reference</em>'
	 *         reference list.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntity_AttributeTypeReference()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity
	 * @model type="DTDAttribute" opposite="attributeTypeReferencedEntity"
	 * @generated
	 */
	EList getAttributeTypeReference();

} // DTDEntity
