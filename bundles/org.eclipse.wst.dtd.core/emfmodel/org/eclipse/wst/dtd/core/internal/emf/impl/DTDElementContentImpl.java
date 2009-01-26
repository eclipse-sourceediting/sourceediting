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


import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;


/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Element Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDElementContentImpl#getGroup <em>Group</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDElementContentImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class DTDElementContentImpl extends EObjectImpl implements DTDElementContent {

	public DTDElementContentImpl() {
		super();
	}

	public DTDElement getDTDElement() {
		DTDElementContent c = getDTDGroupContent();
		if (c != null)
			return c.getElement();
		else
			return getElement();
	}

	// get the most outer DTDGroupContent
	public DTDGroupContent getDTDGroupContent() {
		DTDElementContent c = this;
		while (c.getGroup() != null) {
			c = c.getGroup();
		}
		return ((c instanceof DTDGroupContent) ? (DTDGroupContent) c : null);
	}

	public String getPathname() {
		int cnt = 0;
		DTDObject parent = getGroup();
		if (parent == null) {
			parent = getElement();
		}
		else {
			DTDGroupContent group = (DTDGroupContent) parent;
			Iterator i = group.getContent().iterator();
			while (i.hasNext()) {
				DTDElementContent content = (DTDElementContent) i.next();
				if (content == this) {
					break;
				}
				if ((content instanceof DTDElementReferenceContent) || (content instanceof DTDEntityReferenceContent)) {
					continue;
				}
				cnt++;
			}
		}
		return DTDPathnameUtil.makePath(((parent == null) ? "NULL_PARENT" : parent.getPathname()), "Content", null, cnt); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public DTDObject findObject(String relativePath) {
		return null;
	}

	public boolean isFirstElementContent() {
		DTDElement element = getElement();

		if (element != null) {
			// This means this is the only element content in the element
			return true;
		} // end of if ()

		DTDGroupContent group = getGroup();
		int pos = group.getContentPosition(this);
		if (pos == 0) {
			// now recurse and ensure this group is the first group
			return group.isFirstElementContent();
		} // end of if ()
		else {
			return false;
		}
	}

	public String getContentDetail() {
		// most times, content name is fine. However for groups,
		// we didn't want a long name to show up in the tree,
		// so we created a get contentdetail to get to the long
		// name (getContentName will get the short one.
		return getContentName();
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

	/*
	 * @see DTDElementContent#getContentName()
	 */
	public String getContentName() {
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDElementContent();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDGroupContent getGroup() {
		if (eContainerFeatureID() != DTDPackage.DTD_ELEMENT_CONTENT__GROUP)
			return null;
		return (DTDGroupContent) eContainer;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setGroup(DTDGroupContent newGroup) {
		if (newGroup != eContainer || (eContainerFeatureID() != DTDPackage.DTD_ELEMENT_CONTENT__GROUP && newGroup != null)) {
			if (EcoreUtil.isAncestor(this, newGroup))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newGroup != null)
				msgs = ((InternalEObject) newGroup).eInverseAdd(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
			msgs = eBasicSetContainer((InternalEObject) newGroup, DTDPackage.DTD_ELEMENT_CONTENT__GROUP, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT_CONTENT__GROUP, newGroup, newGroup));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDElement getElement() {
		if (eContainerFeatureID() != DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT)
			return null;
		return (DTDElement) eContainer;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setElement(DTDElement newElement) {
		if (newElement != eContainer || (eContainerFeatureID() != DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT && newElement != null)) {
			if (EcoreUtil.isAncestor(this, newElement))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newElement != null)
				msgs = ((InternalEObject) newElement).eInverseAdd(this, DTDPackage.DTD_ELEMENT__CONTENT, DTDElement.class, msgs);
			msgs = eBasicSetContainer((InternalEObject) newElement, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, newElement, newElement));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ELEMENT_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
					return eBasicSetContainer(null, DTDPackage.DTD_ELEMENT_CONTENT__GROUP, msgs);
				case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, msgs);
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
				case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_GROUP_CONTENT__CONTENT, DTDGroupContent.class, msgs);
				case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_ELEMENT__CONTENT, DTDElement.class, msgs);
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
			case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
				return getGroup();
			case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
				return getElement();
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
			case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
				setGroup((DTDGroupContent) newValue);
				return;
			case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
				setElement((DTDElement) newValue);
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
			case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
				setGroup((DTDGroupContent) null);
				return;
			case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
				setElement((DTDElement) null);
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
			case DTDPackage.DTD_ELEMENT_CONTENT__GROUP :
				return getGroup() != null;
			case DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT :
				return getElement() != null;
		}
		return eDynamicIsSet(eFeature);
	}

} // DTDElementContentImpl
