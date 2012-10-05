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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDVisitor;


/**
 * @generated
 */
public class DTDElementImpl extends EClassImpl implements DTDElement {
	/**
	 * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;

	public DTDElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ELEMENT;
	}

	public void addDTDAttribute(DTDAttribute attribute) {
		getDTDAttribute().add(attribute);
	}

	public String getPathname() {
		return DTDPathnameUtil.makePath(null, "Elem", getName(), -1); //$NON-NLS-1$
	}

	public DTDObject findObject(String relativePath) {
		Object[] result = DTDPathnameUtil.parsePathComponent(relativePath);

		String type = (String) result[0];

		if (type == null)
			return null;

		DTDObject obj = null;
		if (type.equals("Attr")) { //$NON-NLS-1$
			// TODO: fix port
			// obj = findAttribute(name);
		}
		else if ((type.equals("Content")) || (type.equals("ElemRef")) || (type.equals("EntRef"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	// returns all references in the same DTDFile that reference this element
	public Collection getReferences() {
		DTDFile file = getDTDFile();
		final Collection result = new ArrayList();

		DTDVisitor visitRefs = new DTDVisitor() {
			public void visitDTDElementReferenceContent(DTDElementReferenceContent elementReferenceContent) {
				if (elementReferenceContent.getReferencedElement() == DTDElementImpl.this) {
					result.add(elementReferenceContent);
				} // end of if ()
			}
		};
		visitRefs.visitDTDFile(file);
		return result;
	}

	public String getAttributeDetail() {
		String attributeString = ""; //$NON-NLS-1$
		Collection attributes = getDTDAttribute();
		if (attributes != null) {
			boolean seenOne = false;
			for (java.util.Iterator i = attributes.iterator(); i.hasNext();) {
				if (seenOne) {
					attributeString = attributeString + ", "; //$NON-NLS-1$
				}
				else {
					seenOne = true;
				} // end of else

				attributeString = attributeString + ((DTDAttribute) i.next()).getName();
			}
		} // end of if ()
		return attributeString;
	}


	// ugly hack for now since we don't have multiple inheritance.
	// Would rather have all this stuff in a base class but these
	// classes are inheriting from sometimes different mof classes
	DTDLexicalInfo lexInfo = new DTDLexicalInfo();
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected String comment = COMMENT_EDEFAULT;
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected DTDElementContent content;
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected EList dtdAttribute;

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
	 * @generated This field/method will be replaced during code generation
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT__COMMENT, oldComment, comment));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public DTDElementContent getContent() {
		return content;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContent(DTDElementContent newContent, NotificationChain msgs) {
		DTDElementContent oldContent = content;
		content = newContent;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT__CONTENT, oldContent, newContent);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setContent(DTDElementContent newContent) {
		if (newContent != content) {
			NotificationChain msgs = null;
			if (content != null)
				msgs = ((InternalEObject)content).eInverseRemove(this, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, DTDElementContent.class, msgs);
			if (newContent != null)
				msgs = ((InternalEObject)newContent).eInverseAdd(this, DTDPackage.DTD_ELEMENT_CONTENT__ELEMENT, DTDElementContent.class, msgs);
			msgs = basicSetContent(newContent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT__CONTENT, newContent, newContent));
	}

	/**
	 * @generated This field/method will be replaced during code generation
	 */
	public EList getDTDAttribute() {
		if (dtdAttribute == null) {
			dtdAttribute = new EObjectContainmentWithInverseEList(DTDAttribute.class, this, DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT);
		}
		return dtdAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetDTDFile((DTDFile)otherEnd, msgs);
			case DTDPackage.DTD_ELEMENT__CONTENT:
				if (content != null)
					msgs = ((InternalEObject)content).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DTDPackage.DTD_ELEMENT__CONTENT, null, msgs);
				return basicSetContent((DTDElementContent)otherEnd, msgs);
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				return ((InternalEList)getDTDAttribute()).basicAdd(otherEnd, msgs);
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
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				return basicSetDTDFile(null, msgs);
			case DTDPackage.DTD_ELEMENT__CONTENT:
				return basicSetContent(null, msgs);
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				return ((InternalEList)getDTDAttribute()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				return eInternalContainer().eInverseRemove(this, DTDPackage.DTD_FILE__DTD_CONTENT, DTDFile.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				return getDTDFile();
			case DTDPackage.DTD_ELEMENT__COMMENT:
				return getComment();
			case DTDPackage.DTD_ELEMENT__CONTENT:
				return getContent();
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				return getDTDAttribute();
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
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				setDTDFile((DTDFile)newValue);
				return;
			case DTDPackage.DTD_ELEMENT__COMMENT:
				setComment((String)newValue);
				return;
			case DTDPackage.DTD_ELEMENT__CONTENT:
				setContent((DTDElementContent)newValue);
				return;
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				getDTDAttribute().clear();
				getDTDAttribute().addAll((Collection)newValue);
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
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				setDTDFile((DTDFile)null);
				return;
			case DTDPackage.DTD_ELEMENT__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_ELEMENT__CONTENT:
				setContent((DTDElementContent)null);
				return;
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				getDTDAttribute().clear();
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
			case DTDPackage.DTD_ELEMENT__DTD_FILE:
				return getDTDFile() != null;
			case DTDPackage.DTD_ELEMENT__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_ELEMENT__CONTENT:
				return content != null;
			case DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE:
				return dtdAttribute != null && !dtdAttribute.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
		if (baseClass == DTDContent.class) {
			switch (derivedFeatureID) {
				case DTDPackage.DTD_ELEMENT__DTD_FILE: return DTDPackage.DTD_CONTENT__DTD_FILE;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
		if (baseClass == DTDContent.class) {
			switch (baseFeatureID) {
				case DTDPackage.DTD_CONTENT__DTD_FILE: return DTDPackage.DTD_ELEMENT__DTD_FILE;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
		result.append(')');
		return result.toString();
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public DTDFile getDTDFile() {
		if (eContainerFeatureID() != DTDPackage.DTD_ELEMENT__DTD_FILE) return null;
		return (DTDFile)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDTDFile(DTDFile newDTDFile, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newDTDFile, DTDPackage.DTD_ELEMENT__DTD_FILE, msgs);
		return msgs;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public void setDTDFile(DTDFile newDTDFile) {
		if (newDTDFile != eInternalContainer() || (eContainerFeatureID() != DTDPackage.DTD_ELEMENT__DTD_FILE && newDTDFile != null)) {
			if (EcoreUtil.isAncestor(this, newDTDFile))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDFile != null)
				msgs = ((InternalEObject)newDTDFile).eInverseAdd(this, DTDPackage.DTD_FILE__DTD_CONTENT, DTDFile.class, msgs);
			msgs = basicSetDTDFile(newDTDFile, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ELEMENT__DTD_FILE, newDTDFile, newDTDFile));
	}

}
