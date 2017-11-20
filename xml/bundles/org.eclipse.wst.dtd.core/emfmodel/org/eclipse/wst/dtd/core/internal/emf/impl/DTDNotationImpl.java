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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;


/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Notation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl#getSystemID <em>System ID</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl#getPublicID <em>Public ID</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl#getEntity <em>Entity</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DTDNotationImpl extends DTDContentImpl implements DTDNotation, DTDContent {

	public String getPathname() {
		return DTDPathnameUtil.makePath(null, "Nota", getName(), -1); //$NON-NLS-1$
	}


	public DTDObject findObject(String relativePath) {
		return null;
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
	 * The default value of the '{@link #getSystemID() <em>System ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getSystemID()
	 * @generated
	 * @ordered
	 */
	protected static final String SYSTEM_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSystemID() <em>System ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getSystemID()
	 * @generated
	 * @ordered
	 */
	protected String systemID = SYSTEM_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getPublicID() <em>Public ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPublicID()
	 * @generated
	 * @ordered
	 */
	protected static final String PUBLIC_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPublicID() <em>Public ID</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getPublicID()
	 * @generated
	 * @ordered
	 */
	protected String publicID = PUBLIC_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getEntity() <em>Entity</em>}' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEntity()
	 * @generated
	 * @ordered
	 */
	protected EList entity;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DTDNotationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_NOTATION;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_NOTATION__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setSystemID(String newSystemID) {
		String oldSystemID = systemID;
		systemID = newSystemID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_NOTATION__SYSTEM_ID, oldSystemID, systemID));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getPublicID() {
		return publicID;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setPublicID(String newPublicID) {
		String oldPublicID = publicID;
		publicID = newPublicID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_NOTATION__PUBLIC_ID, oldPublicID, publicID));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getEntity() {
		if (entity == null) {
			entity = new EObjectWithInverseResolvingEList(DTDExternalEntity.class, this, DTDPackage.DTD_NOTATION__ENTITY, DTDPackage.DTD_EXTERNAL_ENTITY__NOTATION);
		}
		return entity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_NOTATION__ENTITY:
				return ((InternalEList)getEntity()).basicAdd(otherEnd, msgs);
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
			case DTDPackage.DTD_NOTATION__ENTITY:
				return ((InternalEList)getEntity()).basicRemove(otherEnd, msgs);
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
			case DTDPackage.DTD_NOTATION__COMMENT:
				return getComment();
			case DTDPackage.DTD_NOTATION__SYSTEM_ID:
				return getSystemID();
			case DTDPackage.DTD_NOTATION__PUBLIC_ID:
				return getPublicID();
			case DTDPackage.DTD_NOTATION__ENTITY:
				return getEntity();
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
			case DTDPackage.DTD_NOTATION__COMMENT:
				setComment((String)newValue);
				return;
			case DTDPackage.DTD_NOTATION__SYSTEM_ID:
				setSystemID((String)newValue);
				return;
			case DTDPackage.DTD_NOTATION__PUBLIC_ID:
				setPublicID((String)newValue);
				return;
			case DTDPackage.DTD_NOTATION__ENTITY:
				getEntity().clear();
				getEntity().addAll((Collection)newValue);
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
			case DTDPackage.DTD_NOTATION__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_NOTATION__SYSTEM_ID:
				setSystemID(SYSTEM_ID_EDEFAULT);
				return;
			case DTDPackage.DTD_NOTATION__PUBLIC_ID:
				setPublicID(PUBLIC_ID_EDEFAULT);
				return;
			case DTDPackage.DTD_NOTATION__ENTITY:
				getEntity().clear();
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
			case DTDPackage.DTD_NOTATION__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_NOTATION__SYSTEM_ID:
				return SYSTEM_ID_EDEFAULT == null ? systemID != null : !SYSTEM_ID_EDEFAULT.equals(systemID);
			case DTDPackage.DTD_NOTATION__PUBLIC_ID:
				return PUBLIC_ID_EDEFAULT == null ? publicID != null : !PUBLIC_ID_EDEFAULT.equals(publicID);
			case DTDPackage.DTD_NOTATION__ENTITY:
				return entity != null && !entity.isEmpty();
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
		result.append(", systemID: ");
		result.append(systemID);
		result.append(", publicID: ");
		result.append(publicID);
		result.append(')');
		return result.toString();
	}

} // DTDNotationImpl

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
//

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
// //public class DTDNotationImpl extends ENamespaceImpl implements
// DTDNotation, ENamespace, DTDContent{
// -------------------GENERICRULES.JSED-------------------

// public static final String copyright = "(c) Copyright IBM Corporation
// 2002.";
//
// public DTDNotationImpl()
// {
// super();
// }
// public String getPathname()
// {
// return DTDPathnameUtil.makePath(null, "Nota", getName(), -1);
// }
//
// public DTDObject findObject(String relativePath)
// {
// return null;
// }
//
// // ugly hack for now since we don't have multiple inheritance.
// // Would rather have all this stuff in a base class but these
// // classes are inheriting from sometimes different mof classes
// DTDLexicalInfo lexInfo = new DTDLexicalInfo();
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
// protected String systemID = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected String publicID = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList entity = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setComment = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setSystemID = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setPublicID = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// private DTDContentImpl dtdContentDelegate = null;
// /**
// * Get the value of startOffset.
// * @return value of startOffset.
// */
// public int getStartOffset()
// {
// return lexInfo.getStartOffset();
// }
//  
// /**
// * Set the value of startOffset.
// * @param v Value to assign to startOffset.
// */
// public void setStartOffset(int v)
// {
// lexInfo.setStartOffset(v);
// }
//  
// /**
// * Get the value of endOffset.
// * @return value of endOffset.
// */
// public int getEndOffset()
// {
// return lexInfo.getEndOffset();
// }
//  
// /**
// * Set the value of endOffset.
// * @param v Value to assign to endOffset.
// */
// public void setEndOffset(int v)
// {
// lexInfo.setEndOffset(v);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EObject initInstance() {
// setEMetaObj(eClassDTDNotation());
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
// public EClass eClassDTDNotation() {
// return
// ((DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI)).getDTDNotation();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public String getComment() {
// if (this.setComment) return this.comment;
// else return
// (String)DTDPackage.eINSTANCE.getDTDNotation_Comment().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setComment(String value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDNotation_Comment(),this.comment,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetComment() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDNotation_Comment()));
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
// public String getSystemID() {
// if (this.setSystemID) return this.systemID;
// else return
// (String)DTDPackage.eINSTANCE.getDTDNotation_SystemID().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setSystemID(String value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDNotation_SystemID(),this.systemID,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetSystemID() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDNotation_SystemID()));
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetSystemID() {
// return setSystemID;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public String getPublicID() {
// if (this.setPublicID) return this.publicID;
// else return
// (String)DTDPackage.eINSTANCE.getDTDNotation_PublicID().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setPublicID(String value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDNotation_PublicID(),this.publicID,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetPublicID() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDNotation_PublicID()));
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetPublicID() {
// return setPublicID;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getEntity() {
// if (this.entity==null) {
// this.entity=newCollection(this,DTDPackage.eINSTANCE.getDTDNotation_Entity(),
// true);
// }
// return this.entity;
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: return getComment();
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: return getSystemID();
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: return getPublicID();
// case DTDPackage.DTD_NOTATION__ENTITY: return getEntity();
// case DTDPackage.DTD_NOTATION__DTD_FILE: return getDTDFile();
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT:
// return this.setComment? this.comment : null;
// case DTDPackage.DTD_NOTATION__SYSTEM_ID:
// return this.setSystemID? this.systemID : null;
// case DTDPackage.DTD_NOTATION__PUBLIC_ID:
// return this.setPublicID? this.publicID : null;
// case DTDPackage.DTD_NOTATION__ENTITY:
// return this.entity;
// case DTDPackage.DTD_NOTATION__DTD_FILE: return
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: return isSetComment();
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: return isSetSystemID();
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: return isSetPublicID();
// case DTDPackage.DTD_NOTATION__DTD_FILE: return isSetDTDFile();
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: {
// setComment((String)newValue);
// return;
// }
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: {
// setSystemID((String)newValue);
// return;
// }
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: {
// setPublicID((String)newValue);
// return;
// }
// case DTDPackage.DTD_NOTATION__DTD_FILE: {
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: {
// Object oldValue = this.comment;
// this.comment = (String)newValue;
// this.setComment = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_Comment(),oldValue,newValue);
// }
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: {
// Object oldValue = this.systemID;
// this.systemID = (String)newValue;
// this.setSystemID = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_SystemID(),oldValue,newValue);
// }
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: {
// Object oldValue = this.publicID;
// this.publicID = (String)newValue;
// this.setPublicID = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_PublicID(),oldValue,newValue);
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: {
// unsetComment();
// return;
// }
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: {
// unsetSystemID();
// return;
// }
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: {
// unsetPublicID();
// return;
// }
// case DTDPackage.DTD_NOTATION__DTD_FILE: {
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
// switch (eClassDTDNotation().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_NOTATION__COMMENT: {
// Object oldValue = this.comment;
// this.comment = null;
// this.setComment = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_Comment(),oldValue,getComment());
// }
// case DTDPackage.DTD_NOTATION__SYSTEM_ID: {
// Object oldValue = this.systemID;
// this.systemID = null;
// this.setSystemID = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_SystemID(),oldValue,getSystemID());
// }
// case DTDPackage.DTD_NOTATION__PUBLIC_ID: {
// Object oldValue = this.publicID;
// this.publicID = null;
// this.setPublicID = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDNotation_PublicID(),oldValue,getPublicID());
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

