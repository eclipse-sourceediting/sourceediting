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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;



/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Parameter Entity Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDParameterEntityReferenceImpl#getEntity <em>Entity</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DTDParameterEntityReferenceImpl extends DTDContentImpl implements DTDParameterEntityReference, ENamedElement, DTDContent {

	public String getName() {
		DTDEntity entity = getEntity();
		if (entity != null) {
			return getEntity().getName();
		} // end of if ()
		return ""; //$NON-NLS-1$
	}

	public String getPathname() {
		return DTDPathnameUtil.makePath(null, "PEnt", getName(), -1); //$NON-NLS-1$
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
	 * The cached value of the '{@link #getEntity() <em>Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getEntity()
	 * @generated
	 * @ordered
	 */
	protected DTDEntity entity = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected DTDParameterEntityReferenceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDParameterEntityReference();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity getEntity() {
		if (entity != null && entity.eIsProxy()) {
			DTDEntity oldEntity = entity;
			entity = (DTDEntity) EcoreUtil.resolve(entity, this);
			if (entity != oldEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, oldEntity, entity));
			}
		}
		return entity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity basicGetEntity() {
		return entity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetEntity(DTDEntity newEntity, NotificationChain msgs) {
		DTDEntity oldEntity = entity;
		entity = newEntity;
		if (eNotificationRequired()) {
			if (msgs == null)
				msgs = new NotificationChainImpl(4);
			msgs.add(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, oldEntity, newEntity));
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setEntity(DTDEntity newEntity) {
		if (newEntity != entity) {
			NotificationChain msgs = null;
			if (entity != null)
				msgs = ((InternalEObject) entity).eInverseRemove(this, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, DTDEntity.class, msgs);
			if (newEntity != null)
				msgs = ((InternalEObject) newEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, DTDEntity.class, msgs);
			msgs = basicSetEntity(newEntity, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY, newEntity, newEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE, msgs);
				case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
					if (entity != null)
						msgs = ((InternalEObject) entity).eInverseRemove(this, DTDPackage.DTD_ENTITY__PARM_ENTITY_REF, DTDEntity.class, msgs);
					return basicSetEntity((DTDEntity) otherEnd, msgs);
				default :
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (baseClass == null ? featureID : eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
					return eBasicSetContainer(null, DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE, msgs);
				case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
					return basicSetEntity(null, msgs);
				default :
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
		if (eContainerFeatureID() >= 0) {
			switch (eContainerFeatureID()) {
				case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_FILE__DTD_CONTENT, DTDFile.class, msgs);
				default :
					return eDynamicBasicRemoveFromContainer(msgs);
			}
		}
		return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID(), null, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
				return getDTDFile();
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
				if (resolve)
					return getEntity();
				return basicGetEntity();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
				setDTDFile((DTDFile) newValue);
				return;
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
				setEntity((DTDEntity) newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
				setDTDFile((DTDFile) null);
				return;
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
				setEntity((DTDEntity) null);
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature.getFeatureID(), eFeature.getContainerClass())) {
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE :
				return getDTDFile() != null;
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY :
				return entity != null;
		}
		return eDynamicIsSet(eFeature);
	}

} // DTDParameterEntityReferenceImpl

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
// //public class DTDParameterEntityReferenceImpl extends ENamespaceImpl
// implements DTDParameterEntityReference, ENamespace, DTDContent{
// -------------------GENERICRULES.JSED-------------------

// /**
// * @generated This field/method will be replaced during code generation.
// */
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected DTDEntity entity = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setEntity = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// private DTDContentImpl dtdContentDelegate = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EObject initInstance() {
// setEMetaObj(eClassDTDParameterEntityReference());
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
// public EClass eClassDTDParameterEntityReference() {
// return
// ((DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI)).getDTDParameterEntityReference();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public DTDEntity getEntity() {
// try {
// if (this.entity == null) return null;
// this.entity = (DTDEntity) ((InternalEObject)this.entity).resolve(this,
// DTDPackage.eINSTANCE.getDTDParameterEntityReference_Entity());
// if (this.entity==null) this.setEntity = false;
// return this.entity;
// } catch (Exception e) {
// return null;
// }
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setEntity(DTDEntity l) {
// refSetValueForSVReference(DTDPackage.eINSTANCE.getDTDParameterEntityReference_Entity(),this.entity,l);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetEntity() {
// refUnsetValueForSVReference(DTDPackage.eINSTANCE.getDTDParameterEntityReference_Entity(),this.entity);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetEntity() {
// return setEntity;
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: return getEntity();
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE: return
// getDTDFile();
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY:
// if (!this.setEntity||this.entity==null) return null;
// if (((InternalEObject)this.entity).refIsDeleted()) {this.entity=null;
// this.setEntity=false;}
// return this.entity;
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE: return
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: return
// isSetEntity();
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE: return
// isSetDTDFile();
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: {
// setEntity((DTDEntity)newValue);
// return;
// }
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE: {
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: {
// Object oldValue = this.entity;
// this.entity = (DTDEntity)newValue;
// this.setEntity = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDParameterEntityReference_Entity(),oldValue,newValue);
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: {
// unsetEntity();
// return;
// }
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE: {
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
// switch (eClassDTDParameterEntityReference().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE__ENTITY: {
// Object oldValue = this.entity;
// this.entity = null;
// this.setEntity = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDParameterEntityReference_Entity(),oldValue,null);
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
