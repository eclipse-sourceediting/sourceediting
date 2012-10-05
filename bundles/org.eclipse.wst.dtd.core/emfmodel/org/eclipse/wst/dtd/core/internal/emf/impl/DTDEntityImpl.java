/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;



/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#isParameterEntity <em>Parameter Entity</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getParmEntityRef <em>Parm Entity Ref</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getEntityReference <em>Entity Reference</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getAttributeNameReference <em>Attribute Name Reference</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl#getAttributeTypeReference <em>Attribute Type Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DTDEntityImpl extends DTDContentImpl implements DTDEntity, ENamedElement, DTDContent {

	public String getPathname() {
		return DTDPathnameUtil.makePath(null, "Ent", getName(), -1); //$NON-NLS-1$
	}


	public DTDObject findObject(String relativePath) {
		Object[] result = DTDPathnameUtil.parsePathComponent(relativePath);


		String type = (String) result[0];


		if (type == null)
			return null;


		DTDObject obj = null;
		if (type.equals("Content")) { //$NON-NLS-1$
			obj = getContent();
		}
		else {
			return null;
		}


		String restPath = (String) result[3];

		if ((restPath == null) || (obj == null)) {
			return obj;
		}
		else {
			return obj.findObject(restPath);
		}
	}


	// ugly hack for now since we don't have multiple inheritance.
	// Would rather have all this stuff in a base class but these
	// classes are inheriting from sometimes different mof classes
	DTDLexicalInfo lexInfo = new DTDLexicalInfo();

	/**
	 * Get the value of startOffset.
	 * 
	 * @return value of startOffset.
	 */
	public int getStartOffset() {
		return lexInfo.getStartOffset();
	}

	/**
	 * Set the value of startOffset.
	 * 
	 * @param v
	 *            Value to assign to startOffset.
	 */
	public void setStartOffset(int v) {
		lexInfo.setStartOffset(v);
	}

	/**
	 * Get the value of endOffset.
	 * 
	 * @return value of endOffset.
	 */
	public int getEndOffset() {
		return lexInfo.getEndOffset();
	}

	/**
	 * Set the value of endOffset.
	 * 
	 * @param v
	 *            Value to assign to endOffset.
	 */
	public void setEndOffset(int v) {
		lexInfo.setEndOffset(v);
	}



	/**
	 * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected String comment = COMMENT_EDEFAULT;

	/**
	 * The default value of the '{@link #isParameterEntity() <em>Parameter Entity</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isParameterEntity()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PARAMETER_ENTITY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isParameterEntity() <em>Parameter Entity</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isParameterEntity()
	 * @generated
	 * @ordered
	 */
	protected boolean parameterEntity = PARAMETER_ENTITY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getContent()
	 * @generated
	 * @ordered
	 */
	protected DTDEntityContent content;

	/**
	 * The cached value of the '{@link #getParmEntityRef() <em>Parm Entity Ref</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getParmEntityRef()
	 * @generated
	 * @ordered
	 */
	protected DTDParameterEntityReference parmEntityRef;

	/**
	 * The cached value of the '{@link #getEntityReference() <em>Entity Reference</em>}' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEntityReference()
	 * @generated
	 * @ordered
	 */
	protected EList entityReference;

	/**
	 * The cached value of the '{@link #getAttributeNameReference() <em>Attribute Name Reference</em>}' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getAttributeNameReference()
	 * @generated
	 * @ordered
	 */
	protected EList attributeNameReference;

	/**
	 * The cached value of the '{@link #getAttributeTypeReference() <em>Attribute Type Reference</em>}' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getAttributeTypeReference()
	 * @generated
	 * @ordered
	 */
	protected EList attributeTypeReference;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DTDEntityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ENTITY;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isParameterEntity() {
		return parameterEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameterEntity(boolean newParameterEntity) {
		boolean oldParameterEntity = parameterEntity;
		parameterEntity = newParameterEntity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__PARAMETER_ENTITY, oldParameterEntity, parameterEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntityContent getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContent(DTDEntityContent newContent, NotificationChain msgs) {
		DTDEntityContent oldContent = content;
		content = newContent;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__CONTENT, oldContent, newContent);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setContent(DTDEntityContent newContent) {
		if (newContent != content) {
			NotificationChain msgs = null;
			if (content != null)
				msgs = ((InternalEObject)content).eInverseRemove(this, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, DTDEntityContent.class, msgs);
			if (newContent != null)
				msgs = ((InternalEObject)newContent).eInverseAdd(this, DTDPackage.DTD_ENTITY_CONTENT__DTD_ENTITY, DTDEntityContent.class, msgs);
			msgs = basicSetContent(newContent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__CONTENT, newContent, newContent));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDParameterEntityReference getParmEntityRef() {
		if (parmEntityRef != null && parmEntityRef.eIsProxy()) {
			InternalEObject oldParmEntityRef = (InternalEObject)parmEntityRef;
			parmEntityRef = (DTDParameterEntityReference)eResolveProxy(oldParmEntityRef);
			if (parmEntityRef != oldParmEntityRef) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, oldParmEntityRef, parmEntityRef));
			}
		}
		return parmEntityRef;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDParameterEntityReference basicGetParmEntityRef() {
		return parmEntityRef;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParmEntityRef(DTDParameterEntityReference newParmEntityRef, NotificationChain msgs) {
		DTDParameterEntityReference oldParmEntityRef = parmEntityRef;
		parmEntityRef = newParmEntityRef;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, oldParmEntityRef, newParmEntityRef);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setParmEntityRef(DTDParameterEntityReference newParmEntityRef) {
		if (newParmEntityRef != parmEntityRef) {
			NotificationChain msgs = null;
			if (parmEntityRef != null)
				msgs = ((InternalEObject)parmEntityRef).eInverseRemove(this, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, DTDParameterEntityReference.class, msgs);
			if (newParmEntityRef != null)
				msgs = ((InternalEObject)newParmEntityRef).eInverseAdd(this, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, DTDParameterEntityReference.class, msgs);
			msgs = basicSetParmEntityRef(newParmEntityRef, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, newParmEntityRef, newParmEntityRef));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getEntityReference() {
		if (entityReference == null) {
			entityReference = new EObjectWithInverseResolvingEList(DTDEntityReferenceContent.class, this, DTDPackage.DTD_ENTITY__ENTITY_REFERENCE, DTDPackage.DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY);
		}
		return entityReference;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getAttributeNameReference() {
		if (attributeNameReference == null) {
			attributeNameReference = new EObjectWithInverseResolvingEList(DTDAttribute.class, this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY);
		}
		return attributeNameReference;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getAttributeTypeReference() {
		if (attributeTypeReference == null) {
			attributeTypeReference = new EObjectWithInverseResolvingEList(DTDAttribute.class, this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY);
		}
		return attributeTypeReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__CONTENT:
				if (content != null)
					msgs = ((InternalEObject)content).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DTDPackage.DTD_ENTITY__CONTENT, null, msgs);
				return basicSetContent((DTDEntityContent)otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				if (parmEntityRef != null)
					msgs = ((InternalEObject)parmEntityRef).eInverseRemove(this, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, DTDParameterEntityReference.class, msgs);
				return basicSetParmEntityRef((DTDParameterEntityReference)otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				return ((InternalEList)getEntityReference()).basicAdd(otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				return ((InternalEList)getAttributeNameReference()).basicAdd(otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				return ((InternalEList)getAttributeTypeReference()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__CONTENT:
				return basicSetContent(null, msgs);
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				return basicSetParmEntityRef(null, msgs);
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				return ((InternalEList)getEntityReference()).basicRemove(otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				return ((InternalEList)getAttributeNameReference()).basicRemove(otherEnd, msgs);
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				return ((InternalEList)getAttributeTypeReference()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__COMMENT:
				return getComment();
			case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY:
				return isParameterEntity() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ENTITY__CONTENT:
				return getContent();
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				if (resolve) return getParmEntityRef();
				return basicGetParmEntityRef();
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				return getEntityReference();
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				return getAttributeNameReference();
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				return getAttributeTypeReference();
		}
		return super.eGet(featureID, resolve, coreType);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__COMMENT:
				setComment((String)newValue);
				return;
			case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY:
				setParameterEntity(((Boolean)newValue).booleanValue());
				return;
			case DTDPackage.DTD_ENTITY__CONTENT:
				setContent((DTDEntityContent)newValue);
				return;
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				setParmEntityRef((DTDParameterEntityReference)newValue);
				return;
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				getEntityReference().clear();
				getEntityReference().addAll((Collection)newValue);
				return;
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				getAttributeNameReference().clear();
				getAttributeNameReference().addAll((Collection)newValue);
				return;
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				getAttributeTypeReference().clear();
				getAttributeTypeReference().addAll((Collection)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY:
				setParameterEntity(PARAMETER_ENTITY_EDEFAULT);
				return;
			case DTDPackage.DTD_ENTITY__CONTENT:
				setContent((DTDEntityContent)null);
				return;
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				setParmEntityRef((DTDParameterEntityReference)null);
				return;
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				getEntityReference().clear();
				return;
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				getAttributeNameReference().clear();
				return;
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				getAttributeTypeReference().clear();
				return;
		}
		super.eUnset(featureID);
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DTDPackage.DTD_ENTITY__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY:
				return parameterEntity != PARAMETER_ENTITY_EDEFAULT;
			case DTDPackage.DTD_ENTITY__CONTENT:
				return content != null;
			case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
				return parmEntityRef != null;
			case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
				return entityReference != null && !entityReference.isEmpty();
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
				return attributeNameReference != null && !attributeNameReference.isEmpty();
			case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
				return attributeTypeReference != null && !attributeTypeReference.isEmpty();
		}
		return super.eIsSet(featureID);
	}


	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (comment: ");
		result.append(comment);
		result.append(", parameterEntity: ");
		result.append(parameterEntity);
		result.append(')');
		return result.toString();
	}

} // DTDEntityImpl

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
//

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
// //public class DTDEntityImpl extends ENamespaceImpl implements DTDEntity,
// ENamespace, DTDContent{
// -------------------GENERICRULES.JSED-------------------

// /**
// * @generated This field/method will be replaced during code generation.
// */
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected String comment = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected Boolean parameterEntity = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected DTDEntityContent content = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected DTDParameterEntityReference parmEntityRef = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList entityReference = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList attributeNameReference = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList attributeTypeReference = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setComment = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setParameterEntity = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setContent = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setParmEntityRef = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// private DTDContentImpl dtdContentDelegate = null;

// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EObject initInstance() {
// setEMetaObj(eClassDTDEntity());
// initInstanceDelegates();
// return this;
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected void initInstanceDelegates() {
// super.initInstanceDelegates();
//
// getDtdContentDelegate().refSetDelegateOwner(this);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public DTDPackage ePackageDTD() {
// return
// (DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EClass eClassDTDEntity() {
// return
// ((DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI)).getDTDEntity();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public String getComment() {
// if (this.setComment) return this.comment;
// else return
// (String)DTDPackage.eINSTANCE.getDTDEntity_Comment().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setComment(String value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDEntity_Comment(),this.comment,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetComment() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDEntity_Comment()));
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetComment() {
// return setComment;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public Boolean getParameterEntity() {
// if (this.setParameterEntity) return this.parameterEntity;
// else return
// (Boolean)DTDPackage.eINSTANCE.getDTDEntity_ParameterEntity().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public boolean isParameterEntity() {
// Boolean result = getParameterEntity();
// return result != null ? result.booleanValue() : false;
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setParameterEntity(Boolean value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDEntity_ParameterEntity(),this.parameterEntity,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setParameterEntity(boolean value) {
// setParameterEntity(value?Boolean.TRUE:Boolean.FALSE);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetParameterEntity() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDEntity_ParameterEntity()));
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetParameterEntity() {
// return setParameterEntity;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public DTDEntityContent getContent() {
// try {
// if (this.content == null) return null;
// this.content = (DTDEntityContent)
// ((InternalEObject)this.content).resolve(this);
// if (this.content==null) this.setContent = false;
// return this.content;
// } catch (Exception e) {
// return null;
// }
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setContent(DTDEntityContent l) {
// refSetValueForRefObjectSF(DTDPackage.eINSTANCE.getDTDEntity_Content(),this.content,l);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetContent() {
// refUnsetValueForRefObjectSF(DTDPackage.eINSTANCE.getDTDEntity_Content(),this.content);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetContent() {
// return setContent;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public DTDParameterEntityReference getParmEntityRef() {
// try {
// if (this.parmEntityRef == null) return null;
// this.parmEntityRef = (DTDParameterEntityReference)
// ((InternalEObject)this.parmEntityRef).resolve(this,
// DTDPackage.eINSTANCE.getDTDEntity_ParmEntityRef());
// if (this.parmEntityRef==null) this.setParmEntityRef = false;
// return this.parmEntityRef;
// } catch (Exception e) {
// return null;
// }
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setParmEntityRef(DTDParameterEntityReference l) {
// refSetValueForSVReference(DTDPackage.eINSTANCE.getDTDEntity_ParmEntityRef(),this.parmEntityRef,l);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetParmEntityRef() {
// refUnsetValueForSVReference(DTDPackage.eINSTANCE.getDTDEntity_ParmEntityRef(),this.parmEntityRef);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetParmEntityRef() {
// return setParmEntityRef;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getEntityReference() {
// if (this.entityReference==null) {
// this.entityReference=newCollection(this,DTDPackage.eINSTANCE.getDTDEntity_EntityReference(),
// true);
// }
// return this.entityReference;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getAttributeNameReference() {
// if (this.attributeNameReference==null) {
// this.attributeNameReference=newCollection(this,DTDPackage.eINSTANCE.getDTDEntity_AttributeNameReference(),
// true);
// }
// return this.attributeNameReference;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getAttributeTypeReference() {
// if (this.attributeTypeReference==null) {
// this.attributeTypeReference=newCollection(this,DTDPackage.eINSTANCE.getDTDEntity_AttributeTypeReference(),
// true);
// }
// return this.attributeTypeReference;
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public Object eGet(EStructuralFeature feature) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// return super.eGet(feature);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: return getComment();
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: return getParameterEntity();
// case DTDPackage.DTD_ENTITY__CONTENT: return getContent();
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: return getParmEntityRef();
// case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE: return getEntityReference();
// case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE: return
// getAttributeNameReference();
// case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE: return
// getAttributeTypeReference();
// case DTDPackage.DTD_ENTITY__DTD_FILE: return getDTDFile();
//
// }
// return super.eGet(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public Object refBasicValue(EStructuralFeature feature) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// return super.refBasicValue(feature);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT:
// return this.setComment? this.comment : null;
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY:
// return this.setParameterEntity? this.parameterEntity : null;
// case DTDPackage.DTD_ENTITY__CONTENT:
// if (!this.setContent||this.content==null) return null;
// if (((InternalEObject)this.content).refIsDeleted()) {this.content=null;
// this.setContent=false;}
// return this.content;
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF:
// if (!this.setParmEntityRef||this.parmEntityRef==null) return null;
// if (((InternalEObject)this.parmEntityRef).refIsDeleted())
// {this.parmEntityRef=null; this.setParmEntityRef=false;}
// return this.parmEntityRef;
// case DTDPackage.DTD_ENTITY__ENTITY_REFERENCE:
// return this.entityReference;
// case DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE:
// return this.attributeNameReference;
// case DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE:
// return this.attributeTypeReference;
// case DTDPackage.DTD_ENTITY__DTD_FILE: return
// ((InternalEObject)getDtdContentDelegate()).refBasicValue(feature);
//
// }
// return super.refBasicValue(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean eIsSet(EStructuralFeature feature) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// return super.eIsSet(feature);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: return isSetComment();
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: return
// isSetParameterEntity();
// case DTDPackage.DTD_ENTITY__CONTENT: return isSetContent();
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: return isSetParmEntityRef();
// case DTDPackage.DTD_ENTITY__DTD_FILE: return isSetDTDFile();
//
// }
// return super.eIsSet(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void eSet(EStructuralFeature feature, Object newValue) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// super.eSet(feature, newValue);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: {
// setComment((String)newValue);
// return;
// }
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: {
// setParameterEntity((newValue instanceof
// String)?Boolean.valueOf((String)newValue):(Boolean)newValue);
// return;
// }
// case DTDPackage.DTD_ENTITY__CONTENT: {
// setContent((DTDEntityContent)newValue);
// return;
// }
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: {
// setParmEntityRef((DTDParameterEntityReference)newValue);
// return;
// }
// case DTDPackage.DTD_ENTITY__DTD_FILE: {
// setDTDFile((DTDFile)newValue);
// return;
// }
//
// }
// super.eSet(feature, newValue);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public Notification eBasicSet(EStructuralFeature feature, Object newValue)
// {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// return super.eBasicSet(feature, newValue);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: {
// Object oldValue = this.comment;
// this.comment = (String)newValue;
// this.setComment = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_Comment(),oldValue,newValue);
// }
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: {
// Object oldValue = this.parameterEntity;
// this.parameterEntity = (Boolean)newValue;
// this.setParameterEntity = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_ParameterEntity(),oldValue,newValue);
// }
// case DTDPackage.DTD_ENTITY__CONTENT: {
// Object oldValue = this.content;
// this.content = (DTDEntityContent)newValue;
// this.setContent = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_Content(),oldValue,newValue);
// }
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: {
// Object oldValue = this.parmEntityRef;
// this.parmEntityRef = (DTDParameterEntityReference)newValue;
// this.setParmEntityRef = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_ParmEntityRef(),oldValue,newValue);
// }
//
// }
// return super.eBasicSet(feature, newValue);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void eUnset(EStructuralFeature feature) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// super.eUnset(feature);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: {
// unsetComment();
// return;
// }
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: {
// unsetParameterEntity();
// return;
// }
// case DTDPackage.DTD_ENTITY__CONTENT: {
// unsetContent();
// return;
// }
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: {
// unsetParmEntityRef();
// return;
// }
// case DTDPackage.DTD_ENTITY__DTD_FILE: {
// unsetDTDFile();
// return;
// }
//
// }
// super.eUnset(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public Notification refBasicUnsetValue(EStructuralFeature feature) {
// EStructuralFeature eFeature=null;
// try {
// eFeature=(EStructuralFeature)feature;
// } catch (ClassCastException e) {
// return super.refBasicUnsetValue(feature);
// }
// switch (eClassDTDEntity().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_ENTITY__COMMENT: {
// Object oldValue = this.comment;
// this.comment = null;
// this.setComment = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_Comment(),oldValue,getComment());
// }
// case DTDPackage.DTD_ENTITY__PARAMETER_ENTITY: {
// Object oldValue = this.parameterEntity;
// this.parameterEntity = null;
// this.setParameterEntity = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_ParameterEntity(),oldValue,getParameterEntity());
// }
// case DTDPackage.DTD_ENTITY__CONTENT: {
// Object oldValue = this.content;
// this.content = null;
// this.setContent = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_Content(),oldValue,null);
// }
// case DTDPackage.DTD_ENTITY__PARM_ENTITY_REF: {
// Object oldValue = this.parmEntityRef;
// this.parmEntityRef = null;
// this.setParmEntityRef = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDEntity_ParmEntityRef(),oldValue,null);
// }
//
// }
// return super.refBasicUnsetValue(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected DTDContentImpl getDtdContentDelegate() {
// if (dtdContentDelegate == null) {
// DTDPackage pkg =
// (DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI);
// dtdContentDelegate=(DTDContentImpl)pkg.eCreateInstance(pkg.DTD_CONTENT);
// dtdContentDelegate.initInstance();
// }
// return dtdContentDelegate;
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EClass eClassDTDContent() {
// return getDtdContentDelegate().eClass();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public DTDFile getDTDFile() {
// return getDtdContentDelegate().getDTDFile();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setDTDFile(DTDFile value) {
// getDtdContentDelegate().setDTDFile(value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetDTDFile() {
// getDtdContentDelegate().unsetDTDFile();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetDTDFile() {
// return getDtdContentDelegate().isSetDTDFile();
// }
// }
// -------------------GENERICRULES.JSED-------------------
