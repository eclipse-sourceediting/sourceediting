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
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDLexicalInfo;
import org.eclipse.wst.dtd.core.internal.emf.DTDObject;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDType;


/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getComment <em>Comment</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getDefaultKind <em>Default Kind</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getDefaultValueString <em>Default Value String</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.internal.impl.DTDAttributeImpl#getDTDElement <em>DTD Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class DTDAttributeImpl extends EAttributeImpl implements DTDAttribute {

	public DTDAttributeImpl() {
		super();
	}

	public DTDType getDTDType() {
		return (DTDType) getEType();
	}


	public void setDTDType(DTDType type) {
		setEType((EClassifier) type);
	}

	public void setDTDBasicType(int value) {
		DTDFactoryImpl factory = (DTDFactoryImpl) DTDFactoryImpl.instance();
		// set attribute type
		switch (value) {
			case 0 :
				setDTDType(factory.getDTDBasicType_NONE());
				break;
			case 1 :
				setDTDType(factory.getDTDBasicType_CDATA());
				break;
			case 2 :
				setDTDType(factory.getDTDBasicType_ID());
				break;
			case 3 :
				setDTDType(factory.getDTDBasicType_IDREF());
				break;
			case 4 :
				setDTDType(factory.getDTDBasicType_IDREFS());
				break;
			case 5 :
				setDTDType(factory.getDTDBasicType_ENTITY());
				break;
			case 6 :
				setDTDType(factory.getDTDBasicType_ENTITIES());
				break;
			case 7 :
				setDTDType(factory.getDTDBasicType_NMTOKEN());
				break;
			case 8 :
				setDTDType(factory.getDTDBasicType_NMTOKENS());
				break;
		}
	}

	public String unparse() {
		StringBuffer result = new StringBuffer(128);


		result.append(getName());


		StringBuffer value = new StringBuffer();
		switch (getDefaultKind().getValue()) {
			case DTDDefaultKind.IMPLIED :
				value.append("#IMPLIED"); //$NON-NLS-1$
				break;
			case DTDDefaultKind.REQUIRED :
				value.append("#REQUIRED"); //$NON-NLS-1$
				break;
			case DTDDefaultKind.FIXED :
				String type = getDTDType().toString();
				if (!(type.equals(DTDType.ID) || type.equals(DTDType.IDREF) || type.equals(DTDType.ENUM_NAME_TOKEN_GROUP) || type.equals(DTDType.IDREFS))) {
					value.append("#FIXED \"").append(getDefaultValueString()).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
				}
				break;
			case DTDDefaultKind.NOFIXED :
				String defaultValue = getDefaultValueString();
				if (defaultValue != null)
					value.append("\"").append(defaultValue).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
				break;
		}


		// Get the attribute type
		DTDEntity typeEnt = getAttributeTypeReferencedEntity();
		if (typeEnt != null) {
			result.append(" %" + typeEnt.getName() + "; ").append(value); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			DTDType dtdType = getDTDType();
			if (dtdType instanceof DTDBasicType) {
				switch (((DTDBasicType) dtdType).getKind().getValue()) {
					case DTDBasicTypeKind.CDATA :
						result.append(" CDATA ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.ID :
						result.append(" ID ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.IDREF :
						result.append(" IDREF ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.IDREFS :
						result.append(" IDREFS ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.ENTITY :
						result.append(" ENTITY ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.ENTITIES :
						result.append(" ENTITIES ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.NMTOKEN :
						result.append(" NMTOKEN ").append(value); //$NON-NLS-1$
						break;
					case DTDBasicTypeKind.NMTOKENS :
						result.append(" NMTOKENS ").append(value); //$NON-NLS-1$
						break;
				}
			}
			else if (dtdType instanceof DTDEnumerationType) {
				result.append(" ").append(buildEnumString((DTDEnumerationType) dtdType)).append(value); //$NON-NLS-1$
			}
		}


		return result.toString();
	}


	private String buildEnumString(DTDEnumerationType enumType) {
		String result = ""; //$NON-NLS-1$


		if (enumType.getKind().getValue() == DTDEnumGroupKind.NOTATION_GROUP)
			result += "NOTATION "; //$NON-NLS-1$


		Iterator i = enumType.getEnumLiterals().iterator();
		if (i.hasNext()) {
			result += "(" + ((EEnumLiteral) i.next()).toString(); //$NON-NLS-1$
			while (i.hasNext()) {
				result += " | " + ((EEnumLiteral) i.next()).toString(); //$NON-NLS-1$
			}
			result += ") "; //$NON-NLS-1$
		}


		return result;
	}

	public DTDEnumerationType createDTDEnumeration(String[] enumValues, int enumKind) {
		DTDEnumerationType enumeration = DTDFactoryImpl.instance().createDTDEnumerationType();
		// enum.setID("Enum_" + ((DTDElement)getMOFDomain()).getName() + "_" +
		// getName());
		DTDEnumGroupKind groupKind = DTDEnumGroupKind.get(enumKind);
		enumeration.setKind(groupKind);
		// Enumeration values
		if (enumValues != null) {
			for (int i = 0; i < enumValues.length; i++) {
				EcorePackage ePackage = (EcorePackage) EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
				EEnumLiteral enumLiteral = ((EcoreFactory) ePackage.getEFactoryInstance()).createEEnumLiteral();
				// MOF2EMF Port
				// enumLiteral.refSetLiteral(enumValues[i]);
				enumLiteral.setName(enumValues[i]);
				// MOF2EMF Port
				// enumLiteral.refSetLiteral(i);
				enumLiteral.setValue(i);

				enumeration.getEnumLiterals().add(enumLiteral);
			}
		}
		getDTDElement().getDTDFile().getDTDEnumerationType().add(enumeration);
		return enumeration;
	}

	public String getPathname() {
		return null;
		// TODO: finish port
		// return DTDPathnameUtil.makePath(getDTDElement().getPathname(),
		// "Attr", getName(), -1);
	}


	public DTDObject findObject(String relativePath) {
		return null;
		// TODO: finish port
		/*
		 * Object[] result = DTDPathnameUtil.parsePathComponent(relativePath);
		 * 
		 * 
		 * String type = (String)result[0];
		 * 
		 * 
		 * if (type == null) return null;
		 * 
		 * 
		 * DTDObject obj = null; if (type.equals("Type")) { obj =
		 * getDataType(); } else { return null; }
		 * 
		 * 
		 * String restPath = (String)result[3];
		 * 
		 * if ((restPath == null) || (obj == null)) { return obj; } else {
		 * return obj.findObject(restPath); }
		 */
	}

	public Collection getEnumeratedValues() {
		Collection result = new ArrayList();


		DTDType type = getDTDType();
		if (type instanceof DTDEnumerationType) {
			DTDEnumerationType enumType = (DTDEnumerationType) type;
			Iterator i = enumType.getEnumLiterals().iterator();
			while (i.hasNext()) {
				result.add(((EEnumLiteral) i.next()).toString());
			}
		}
		return result;
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
	 * The default value of the '{@link #getComment() <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getComment() <em>Comment</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected String comment = COMMENT_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultKind() <em>Default Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDefaultKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDDefaultKind DEFAULT_KIND_EDEFAULT = DTDDefaultKind.IMPLIED_LITERAL;

	/**
	 * The cached value of the '{@link #getDefaultKind() <em>Default Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDefaultKind()
	 * @generated
	 * @ordered
	 */
	protected DTDDefaultKind defaultKind = DEFAULT_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultValueString() <em>Default Value String</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDefaultValueString()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_STRING_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultValueString() <em>Default Value String</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDefaultValueString()
	 * @generated
	 * @ordered
	 */
	protected String defaultValueString = DEFAULT_VALUE_STRING_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAttributeNameReferencedEntity() <em>Attribute Name Referenced Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getAttributeNameReferencedEntity()
	 * @generated
	 * @ordered
	 */
	protected DTDEntity attributeNameReferencedEntity = null;

	/**
	 * The cached value of the '{@link #getAttributeTypeReferencedEntity() <em>Attribute Type Referenced Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getAttributeTypeReferencedEntity()
	 * @generated
	 * @ordered
	 */
	protected DTDEntity attributeTypeReferencedEntity = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.eINSTANCE.getDTDAttribute();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDDefaultKind getDefaultKind() {
		return defaultKind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDefaultKind(DTDDefaultKind newDefaultKind) {
		DTDDefaultKind oldDefaultKind = defaultKind;
		defaultKind = newDefaultKind == null ? DEFAULT_KIND_EDEFAULT : newDefaultKind;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND, oldDefaultKind, defaultKind));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getDefaultValueString() {
		return defaultValueString;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDefaultValueString(String newDefaultValueString) {
		String oldDefaultValueString = defaultValueString;
		defaultValueString = newDefaultValueString;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING, oldDefaultValueString, defaultValueString));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity getAttributeNameReferencedEntity() {
		if (attributeNameReferencedEntity != null && attributeNameReferencedEntity.eIsProxy()) {
			DTDEntity oldAttributeNameReferencedEntity = attributeNameReferencedEntity;
			attributeNameReferencedEntity = (DTDEntity) EcoreUtil.resolve(attributeNameReferencedEntity, this);
			if (attributeNameReferencedEntity != oldAttributeNameReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, oldAttributeNameReferencedEntity, attributeNameReferencedEntity));
			}
		}
		return attributeNameReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity basicGetAttributeNameReferencedEntity() {
		return attributeNameReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetAttributeNameReferencedEntity(DTDEntity newAttributeNameReferencedEntity, NotificationChain msgs) {
		DTDEntity oldAttributeNameReferencedEntity = attributeNameReferencedEntity;
		attributeNameReferencedEntity = newAttributeNameReferencedEntity;
		if (eNotificationRequired()) {
			if (msgs == null)
				msgs = new NotificationChainImpl(4);
			msgs.add(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, oldAttributeNameReferencedEntity, newAttributeNameReferencedEntity));
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setAttributeNameReferencedEntity(DTDEntity newAttributeNameReferencedEntity) {
		if (newAttributeNameReferencedEntity != attributeNameReferencedEntity) {
			NotificationChain msgs = null;
			if (attributeNameReferencedEntity != null)
				msgs = ((InternalEObject) attributeNameReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
			if (newAttributeNameReferencedEntity != null)
				msgs = ((InternalEObject) newAttributeNameReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetAttributeNameReferencedEntity(newAttributeNameReferencedEntity, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, newAttributeNameReferencedEntity, newAttributeNameReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity getAttributeTypeReferencedEntity() {
		if (attributeTypeReferencedEntity != null && attributeTypeReferencedEntity.eIsProxy()) {
			DTDEntity oldAttributeTypeReferencedEntity = attributeTypeReferencedEntity;
			attributeTypeReferencedEntity = (DTDEntity) EcoreUtil.resolve(attributeTypeReferencedEntity, this);
			if (attributeTypeReferencedEntity != oldAttributeTypeReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, oldAttributeTypeReferencedEntity, attributeTypeReferencedEntity));
			}
		}
		return attributeTypeReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDEntity basicGetAttributeTypeReferencedEntity() {
		return attributeTypeReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetAttributeTypeReferencedEntity(DTDEntity newAttributeTypeReferencedEntity, NotificationChain msgs) {
		DTDEntity oldAttributeTypeReferencedEntity = attributeTypeReferencedEntity;
		attributeTypeReferencedEntity = newAttributeTypeReferencedEntity;
		if (eNotificationRequired()) {
			if (msgs == null)
				msgs = new NotificationChainImpl(4);
			msgs.add(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, oldAttributeTypeReferencedEntity, newAttributeTypeReferencedEntity));
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setAttributeTypeReferencedEntity(DTDEntity newAttributeTypeReferencedEntity) {
		if (newAttributeTypeReferencedEntity != attributeTypeReferencedEntity) {
			NotificationChain msgs = null;
			if (attributeTypeReferencedEntity != null)
				msgs = ((InternalEObject) attributeTypeReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
			if (newAttributeTypeReferencedEntity != null)
				msgs = ((InternalEObject) newAttributeTypeReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetAttributeTypeReferencedEntity(newAttributeTypeReferencedEntity, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, newAttributeTypeReferencedEntity, newAttributeTypeReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DTDElement getDTDElement() {
		if (eContainerFeatureID() != DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT)
			return null;
		return (DTDElement) eContainer;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDTDElement(DTDElement newDTDElement) {
		if (newDTDElement != eContainer || (eContainerFeatureID() != DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT && newDTDElement != null)) {
			if (EcoreUtil.isAncestor(this, newDTDElement))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString() + "."); //$NON-NLS-1$ //$NON-NLS-2$
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDElement != null)
				msgs = ((InternalEObject) newDTDElement).eInverseAdd(this, DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE, DTDElement.class, msgs);
			msgs = eBasicSetContainer((InternalEObject) newDTDElement, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, newDTDElement, newDTDElement));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicAdd(otherEnd, msgs);
				case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
					if (attributeNameReferencedEntity != null)
						msgs = ((InternalEObject) attributeNameReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
					return basicSetAttributeNameReferencedEntity((DTDEntity) otherEnd, msgs);
				case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
					if (attributeTypeReferencedEntity != null)
						msgs = ((InternalEObject) attributeTypeReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
					return basicSetAttributeTypeReferencedEntity((DTDEntity) otherEnd, msgs);
				case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, msgs);
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
				case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
					return ((InternalEList) getEAnnotations()).basicRemove(otherEnd, msgs);
				case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
					return basicSetAttributeNameReferencedEntity(null, msgs);
				case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
					return basicSetAttributeTypeReferencedEntity(null, msgs);
				case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
					return eBasicSetContainer(null, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, msgs);
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
				case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
					return eContainer.eInverseRemove(this, DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE, DTDElement.class, msgs);
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
			case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
				return getEAnnotations();
			case DTDPackage.DTD_ATTRIBUTE__NAME :
				return getName();
			case DTDPackage.DTD_ATTRIBUTE__ETYPE :
				if (resolve)
					return getEType();
				return basicGetEType();
			case DTDPackage.DTD_ATTRIBUTE__CHANGEABLE :
				return isChangeable() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__VOLATILE :
				return isVolatile() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__TRANSIENT :
				return isTransient() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__UNIQUE :
				return isUnique() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_LITERAL :
				return getDefaultValueLiteral();
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE :
				return getDefaultValue();
			case DTDPackage.DTD_ATTRIBUTE__LOWER_BOUND :
				return new Integer(getLowerBound());
			case DTDPackage.DTD_ATTRIBUTE__UPPER_BOUND :
				return new Integer(getUpperBound());
			case DTDPackage.DTD_ATTRIBUTE__MANY :
				return isMany() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__REQUIRED :
				return isRequired() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__ECONTAINING_CLASS :
				return getEContainingClass();
			case DTDPackage.DTD_ATTRIBUTE__UNSETTABLE :
				return isUnsettable() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__ID :
				return isID() ? Boolean.TRUE : Boolean.FALSE;
			case DTDPackage.DTD_ATTRIBUTE__EATTRIBUTE_TYPE :
				if (resolve)
					return getEAttributeType();
				return basicGetEAttributeType();
			case DTDPackage.DTD_ATTRIBUTE__COMMENT :
				return getComment();
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND :
				return getDefaultKind();
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING :
				return getDefaultValueString();
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
				if (resolve)
					return getAttributeNameReferencedEntity();
				return basicGetAttributeNameReferencedEntity();
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
				if (resolve)
					return getAttributeTypeReferencedEntity();
				return basicGetAttributeTypeReferencedEntity();
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
				return getDTDElement();
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
			case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
				getEAnnotations().clear();
				getEAnnotations().addAll((Collection) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__NAME :
				setName((String) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ETYPE :
				setEType((EClassifier) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__CHANGEABLE :
				setChangeable(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__VOLATILE :
				setVolatile(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__TRANSIENT :
				setTransient(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__UNIQUE :
				setUnique(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_LITERAL :
				setDefaultValueLiteral((String) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__LOWER_BOUND :
				setLowerBound(((Integer) newValue).intValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__UPPER_BOUND :
				setUpperBound(((Integer) newValue).intValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__UNSETTABLE :
				setUnsettable(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__ID :
				setID(((Boolean) newValue).booleanValue());
				return;
			case DTDPackage.DTD_ATTRIBUTE__COMMENT :
				setComment((String) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND :
				setDefaultKind((DTDDefaultKind) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING :
				setDefaultValueString((String) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
				setAttributeNameReferencedEntity((DTDEntity) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
				setAttributeTypeReferencedEntity((DTDEntity) newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
				setDTDElement((DTDElement) newValue);
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
			case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
				getEAnnotations().clear();
				return;
			case DTDPackage.DTD_ATTRIBUTE__NAME :
				setName(NAME_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ETYPE :
				setEType((EClassifier) null);
				return;
			case DTDPackage.DTD_ATTRIBUTE__CHANGEABLE :
				setChangeable(CHANGEABLE_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__VOLATILE :
				setVolatile(VOLATILE_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__TRANSIENT :
				setTransient(TRANSIENT_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__UNIQUE :
				setUnique(UNIQUE_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_LITERAL :
				setDefaultValueLiteral(DEFAULT_VALUE_LITERAL_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__LOWER_BOUND :
				setLowerBound(LOWER_BOUND_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__UPPER_BOUND :
				setUpperBound(UPPER_BOUND_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__UNSETTABLE :
				setUnsettable(UNSETTABLE_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ID :
				setID(ID_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__COMMENT :
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND :
				setDefaultKind(DEFAULT_KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING :
				setDefaultValueString(DEFAULT_VALUE_STRING_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
				setAttributeNameReferencedEntity((DTDEntity) null);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
				setAttributeTypeReferencedEntity((DTDEntity) null);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
				setDTDElement((DTDElement) null);
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
			case DTDPackage.DTD_ATTRIBUTE__EANNOTATIONS :
				return eAnnotations != null && !getEAnnotations().isEmpty();
			case DTDPackage.DTD_ATTRIBUTE__NAME :
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DTDPackage.DTD_ATTRIBUTE__ETYPE :
				return eType != null;
			case DTDPackage.DTD_ATTRIBUTE__CHANGEABLE :
				return ((eFlags & CHANGEABLE_EFLAG) != 0) != CHANGEABLE_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__VOLATILE :
				return ((eFlags & VOLATILE_EFLAG) != 0) != VOLATILE_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__TRANSIENT :
				return ((eFlags & TRANSIENT_EFLAG) != 0) != TRANSIENT_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__UNIQUE :
				return ((eFlags & UNIQUE_EFLAG) != 0) != UNIQUE_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_LITERAL :
				return DEFAULT_VALUE_LITERAL_EDEFAULT == null ? defaultValueLiteral != null : !DEFAULT_VALUE_LITERAL_EDEFAULT.equals(defaultValueLiteral);
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE :
				return getDefaultValue() != null;
			case DTDPackage.DTD_ATTRIBUTE__LOWER_BOUND :
				return lowerBound != LOWER_BOUND_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__UPPER_BOUND :
				return upperBound != UPPER_BOUND_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__MANY :
				return isMany() != false;
			case DTDPackage.DTD_ATTRIBUTE__REQUIRED :
				return isRequired() != false;
			case DTDPackage.DTD_ATTRIBUTE__ECONTAINING_CLASS :
				return getEContainingClass() != null;
			case DTDPackage.DTD_ATTRIBUTE__UNSETTABLE :
				return ((eFlags & UNSETTABLE_EFLAG) != 0) != UNSETTABLE_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__ID :
				return ((eFlags & ID_EFLAG) != 0) != ID_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__EATTRIBUTE_TYPE :
				return basicGetEAttributeType() != null;
			case DTDPackage.DTD_ATTRIBUTE__COMMENT :
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND :
				return defaultKind != DEFAULT_KIND_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING :
				return DEFAULT_VALUE_STRING_EDEFAULT == null ? defaultValueString != null : !DEFAULT_VALUE_STRING_EDEFAULT.equals(defaultValueString);
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY :
				return attributeNameReferencedEntity != null;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY :
				return attributeTypeReferencedEntity != null;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT :
				return getDTDElement() != null;
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (comment: "); //$NON-NLS-1$
		result.append(comment);
		result.append(", defaultKind: "); //$NON-NLS-1$
		result.append(defaultKind);
		result.append(", defaultValueString: "); //$NON-NLS-1$
		result.append(defaultValueString);
		result.append(')');
		return result.toString();
	}

} // DTDAttributeImpl
