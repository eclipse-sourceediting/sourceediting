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

package org.eclipse.wst.dtd.core.internal.emf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENamedElementImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPathnameUtil;
import org.eclipse.wst.dtd.core.internal.emf.util.DTDPrinter;



/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl#isParseError <em>Parse Error</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl#getDTDContent <em>DTD Content</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl#getDTDEnumerationType <em>DTD Enumeration Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DTDFileImpl extends ENamedElementImpl implements DTDFile, ENamedElement {

	public EList getDTDObject() {
		return getDTDContent();
	}


	public List listDTDElement() {
		return getContentListOf(DTDElement.class, null);
	}


	public List listDTDNotation() {
		return getContentListOf(DTDNotation.class, null);
	}

	public List listDTDEntity() {
		return getContentListOf(DTDEntity.class, null);
	}

	public List listDTDParameterEntityReference() {
		return getContentListOf(DTDParameterEntityReference.class, null);
	}

	public List listDTDElementAndDTDParameterEntityReference() {
		return getContentListOf(DTDElement.class, DTDParameterEntityReference.class);
	}

	private List getContentListOf(Class class1, Class class2) {
		List v = new ArrayList();
		Iterator i = getDTDContent().iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (class1.isInstance(obj)) {
				v.add(obj);
			}
			else if ((class2 != null) && (class2.isInstance(obj))) {
				v.add(obj);
			}
		}
		return v;
	}

	public DTDElement findElement(String name) {
		for (Iterator i = listDTDElement().iterator(); i.hasNext();) {
			DTDElement e = (DTDElement) i.next();
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public DTDEntity findEntity(String name) {
		for (Iterator i = listDTDEntity().iterator(); i.hasNext();) {
			DTDEntity e = (DTDEntity) i.next();
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public DTDNotation findNotation(String name) {
		for (Iterator i = listDTDNotation().iterator(); i.hasNext();) {
			DTDNotation e = (DTDNotation) i.next();
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public DTDParameterEntityReference findParameterEntityReference(String name) {
		for (Iterator i = listDTDParameterEntityReference().iterator(); i.hasNext();) {
			DTDParameterEntityReference e = (DTDParameterEntityReference) i.next();
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	public String unparse(boolean include) {
		DTDPrinter printer = new DTDPrinter(true);
		printer.visitDTDFile(this);
		return printer.getBuffer().toString();
	}


	public String getPathname() {
		return ""; //$NON-NLS-1$
	}


	public DTDObject findObject(String relativePath) {
		Object[] result = DTDPathnameUtil.parsePathComponent(relativePath);


		String type = (String) result[0];


		if (type == null)
			return null;


		String name = (String) result[1];

		DTDObject obj = null;
		if (type.equals("Elem")) { //$NON-NLS-1$
			obj = findElement(name);
		}
		else if (type.equals("Ent")) { //$NON-NLS-1$
			obj = findEntity(name);
		}
		else if (type.equals("PEnt")) { //$NON-NLS-1$
			obj = findParameterEntityReference(name);
		}
		else if (type.equals("Nota")) { //$NON-NLS-1$
			obj = findNotation(name);
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
	 * The default value of the '{@link #isParseError() <em>Parse Error</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isParseError()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PARSE_ERROR_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isParseError() <em>Parse Error</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isParseError()
	 * @generated
	 * @ordered
	 */
	protected boolean parseError = PARSE_ERROR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDTDContent() <em>DTD Content</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getDTDContent()
	 * @generated
	 * @ordered
	 */
	protected EList dtdContent;

	/**
	 * The cached value of the '{@link #getDTDEnumerationType() <em>DTD Enumeration Type</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see #getDTDEnumerationType()
	 * @generated
	 * @ordered
	 */
	protected EList dtdEnumerationType;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DTDFileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_FILE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_FILE__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isParseError() {
		return parseError;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setParseError(boolean newParseError) {
		boolean oldParseError = parseError;
		parseError = newParseError;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_FILE__PARSE_ERROR, oldParseError, parseError));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getDTDContent() {
		if (dtdContent == null) {
			dtdContent = new EObjectContainmentWithInverseEList(DTDContent.class, this, DTDPackage.DTD_FILE__DTD_CONTENT, DTDPackage.DTD_CONTENT__DTD_FILE);
		}
		return dtdContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EList getDTDEnumerationType() {
		if (dtdEnumerationType == null) {
			dtdEnumerationType = new EObjectContainmentWithInverseEList(DTDEnumerationType.class, this, DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE, DTDPackage.DTD_ENUMERATION_TYPE__DTD_FILE);
		}
		return dtdEnumerationType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				return ((InternalEList)getDTDContent()).basicAdd(otherEnd, msgs);
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				return ((InternalEList)getDTDEnumerationType()).basicAdd(otherEnd, msgs);
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
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				return ((InternalEList)getDTDContent()).basicRemove(otherEnd, msgs);
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				return ((InternalEList)getDTDEnumerationType()).basicRemove(otherEnd, msgs);
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
			case DTDPackage.DTD_FILE__COMMENT:
				return getComment();
			case DTDPackage.DTD_FILE__PARSE_ERROR:
				return isParseError() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				return getDTDContent();
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				return getDTDEnumerationType();
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
			case DTDPackage.DTD_FILE__COMMENT:
				setComment((String)newValue);
				return;
			case DTDPackage.DTD_FILE__PARSE_ERROR:
				setParseError(((Boolean)newValue).booleanValue());
				return;
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				getDTDContent().clear();
				getDTDContent().addAll((Collection)newValue);
				return;
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				getDTDEnumerationType().clear();
				getDTDEnumerationType().addAll((Collection)newValue);
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
			case DTDPackage.DTD_FILE__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_FILE__PARSE_ERROR:
				setParseError(PARSE_ERROR_EDEFAULT);
				return;
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				getDTDContent().clear();
				return;
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				getDTDEnumerationType().clear();
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
			case DTDPackage.DTD_FILE__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_FILE__PARSE_ERROR:
				return parseError != PARSE_ERROR_EDEFAULT;
			case DTDPackage.DTD_FILE__DTD_CONTENT:
				return dtdContent != null && !dtdContent.isEmpty();
			case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
				return dtdEnumerationType != null && !dtdEnumerationType.isEmpty();
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
		result.append(", parseError: ");
		result.append(parseError);
		result.append(')');
		return result.toString();
	}

} // DTDFileImpl

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
//

// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
// //public class DTDFileImpl extends ENamespaceImpl implements DTDFile,
// ENamespace{
// -------------------GENERICRULES.JSED-------------------

// public static final String copyright = "(c) Copyright IBM Corporation
// 2002.";
//
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected String comment = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected Boolean parseError = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList dtdContent = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected EList dtdEnumerationType = null;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setComment = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// protected boolean setParseError = false;
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public EObject initInstance() {
// setEMetaObj(eClassDTDFile());
// initInstanceDelegates();
// return this;
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
// public EClass eClassDTDFile() {
// return
// ((DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI)).getDTDFile();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public String getComment() {
// if (this.setComment) return this.comment;
// else return
// (String)DTDPackage.eINSTANCE.getDTDFile_Comment().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setComment(String value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDFile_Comment(),this.comment,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetComment() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDFile_Comment()));
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
// public Boolean getParseError() {
// if (this.setParseError) return this.parseError;
// else return
// (Boolean)DTDPackage.eINSTANCE.getDTDFile_ParseError().getDefaultValue();
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public boolean isParseError() {
// Boolean result = getParseError();
// return result != null ? result.booleanValue() : false;
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setParseError(Boolean value) {
// refSetValueForSimpleSF(DTDPackage.eINSTANCE.getDTDFile_ParseError(),this.parseError,value);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void setParseError(boolean value) {
// setParseError(value?Boolean.TRUE:Boolean.FALSE);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public void unsetParseError() {
// eNotify(refBasicUnsetValue(DTDPackage.eINSTANCE.getDTDFile_ParseError()));
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public boolean isSetParseError() {
// return setParseError;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getDTDContent() {
// if (this.dtdContent==null) {
// this.dtdContent=newCollection(this,DTDPackage.eINSTANCE.getDTDFile_DTDContent(),
// true);
// }
// return this.dtdContent;
// }
// /**
// * @generated This field/method will be replaced during code generation
// */
// public EList getDTDEnumerationType() {
// if (this.dtdEnumerationType==null) {
// this.dtdEnumerationType=newCollection(this,DTDPackage.eINSTANCE.getDTDFile_DTDEnumerationType(),
// true);
// }
// return this.dtdEnumerationType;
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: return getComment();
// case DTDPackage.DTD_FILE__PARSE_ERROR: return getParseError();
// case DTDPackage.DTD_FILE__DTD_CONTENT: return getDTDContent();
// case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE: return
// getDTDEnumerationType();
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT:
// return this.setComment? this.comment : null;
// case DTDPackage.DTD_FILE__PARSE_ERROR:
// return this.setParseError? this.parseError : null;
// case DTDPackage.DTD_FILE__DTD_CONTENT:
// return this.dtdContent;
// case DTDPackage.DTD_FILE__DTD_ENUMERATION_TYPE:
// return this.dtdEnumerationType;
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: return isSetComment();
// case DTDPackage.DTD_FILE__PARSE_ERROR: return isSetParseError();
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: {
// setComment((String)newValue);
// return;
// }
// case DTDPackage.DTD_FILE__PARSE_ERROR: {
// setParseError((newValue instanceof
// String)?Boolean.valueOf((String)newValue):(Boolean)newValue);
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: {
// Object oldValue = this.comment;
// this.comment = (String)newValue;
// this.setComment = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDFile_Comment(),oldValue,newValue);
// }
// case DTDPackage.DTD_FILE__PARSE_ERROR: {
// Object oldValue = this.parseError;
// this.parseError = (Boolean)newValue;
// this.setParseError = true;
// return new
// ENotificationImpl((InternalEObject)this,Notification.SET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDFile_ParseError(),oldValue,newValue);
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: {
// unsetComment();
// return;
// }
// case DTDPackage.DTD_FILE__PARSE_ERROR: {
// unsetParseError();
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
// switch (eClassDTDFile().getEFeatureId(eFeature)) {
// case DTDPackage.DTD_FILE__COMMENT: {
// Object oldValue = this.comment;
// this.comment = null;
// this.setComment = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDFile_Comment(),oldValue,getComment());
// }
// case DTDPackage.DTD_FILE__PARSE_ERROR: {
// Object oldValue = this.parseError;
// this.parseError = null;
// this.setParseError = false;
// return new
// ENotificationImpl((InternalEObject)this,Notification.UNSET,(EStructuralFeature)DTDPackage.eINSTANCE.getDTDFile_ParseError(),oldValue,getParseError());
// }
//
// }
// return super.refBasicUnsetValue(feature);
// }
// /**
// * @generated This field/method will be replaced during code generation.
// */
// public String toString() {
// String out="("; //$NON-NLS-2$//$NON-NLS-1$
// boolean first=true;
// boolean emptyList=true;
// if(isSetComment()) {
// if(!emptyList) out+=", "; //$NON-NLS-2$//$NON-NLS-1$
// out+="comment: "+this.comment; //$NON-NLS-2$//$NON-NLS-1$
// first=false;
// emptyList=false;
// }
// if(isSetParseError()) {
// if(!emptyList) out+=", "; //$NON-NLS-2$//$NON-NLS-1$
// out+="parseError: "+this.parseError; //$NON-NLS-2$//$NON-NLS-1$
// first=false;
// emptyList=false;
// }
// out+=")";
// if (!first) return super.toString() + " " +out; //$NON-NLS-2$//$NON-NLS-1$
// return super.toString();
// }
// }
// -------------------GENERICRULES.JSED-------------------
