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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getDefaultKind <em>Default Kind</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getDefaultValueString <em>Default Value String</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}</li>
 *   <li>{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl#getDTDElement <em>DTD Element</em>}</li>
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
	 * The default value of the '{@link #getDefaultKind() <em>Default Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getDefaultKind()
	 * @generated
	 * @ordered
	 */
	protected static final DTDDefaultKind DEFAULT_KIND_EDEFAULT = DTDDefaultKind.IMPLIED_LITERAL;

	/**
	 * The cached value of the '{@link #getDefaultKind() <em>Default Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getDefaultKind()
	 * @generated
	 * @ordered
	 */
	protected DTDDefaultKind defaultKind = DEFAULT_KIND_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefaultValueString() <em>Default Value String</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getDefaultValueString()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFAULT_VALUE_STRING_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefaultValueString() <em>Default Value String</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getDefaultValueString()
	 * @generated
	 * @ordered
	 */
	protected String defaultValueString = DEFAULT_VALUE_STRING_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAttributeNameReferencedEntity() <em>Attribute Name Referenced Entity</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getAttributeNameReferencedEntity()
	 * @generated
	 * @ordered
	 */
	protected DTDEntity attributeNameReferencedEntity;

	/**
	 * The cached value of the '{@link #getAttributeTypeReferencedEntity() <em>Attribute Type Referenced Entity</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getAttributeTypeReferencedEntity()
	 * @generated
	 * @ordered
	 */
	protected DTDEntity attributeTypeReferencedEntity;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ATTRIBUTE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDDefaultKind getDefaultKind() {
		return defaultKind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * @generated
	 */
	public String getDefaultValueString() {
		return defaultValueString;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * @generated
	 */
	public DTDEntity getAttributeNameReferencedEntity() {
		if (attributeNameReferencedEntity != null && attributeNameReferencedEntity.eIsProxy()) {
			InternalEObject oldAttributeNameReferencedEntity = (InternalEObject)attributeNameReferencedEntity;
			attributeNameReferencedEntity = (DTDEntity)eResolveProxy(oldAttributeNameReferencedEntity);
			if (attributeNameReferencedEntity != oldAttributeNameReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, oldAttributeNameReferencedEntity, attributeNameReferencedEntity));
			}
		}
		return attributeNameReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntity basicGetAttributeNameReferencedEntity() {
		return attributeNameReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAttributeNameReferencedEntity(DTDEntity newAttributeNameReferencedEntity, NotificationChain msgs) {
		DTDEntity oldAttributeNameReferencedEntity = attributeNameReferencedEntity;
		attributeNameReferencedEntity = newAttributeNameReferencedEntity;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, oldAttributeNameReferencedEntity, newAttributeNameReferencedEntity);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttributeNameReferencedEntity(DTDEntity newAttributeNameReferencedEntity) {
		if (newAttributeNameReferencedEntity != attributeNameReferencedEntity) {
			NotificationChain msgs = null;
			if (attributeNameReferencedEntity != null)
				msgs = ((InternalEObject)attributeNameReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
			if (newAttributeNameReferencedEntity != null)
				msgs = ((InternalEObject)newAttributeNameReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetAttributeNameReferencedEntity(newAttributeNameReferencedEntity, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY, newAttributeNameReferencedEntity, newAttributeNameReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntity getAttributeTypeReferencedEntity() {
		if (attributeTypeReferencedEntity != null && attributeTypeReferencedEntity.eIsProxy()) {
			InternalEObject oldAttributeTypeReferencedEntity = (InternalEObject)attributeTypeReferencedEntity;
			attributeTypeReferencedEntity = (DTDEntity)eResolveProxy(oldAttributeTypeReferencedEntity);
			if (attributeTypeReferencedEntity != oldAttributeTypeReferencedEntity) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, oldAttributeTypeReferencedEntity, attributeTypeReferencedEntity));
			}
		}
		return attributeTypeReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntity basicGetAttributeTypeReferencedEntity() {
		return attributeTypeReferencedEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAttributeTypeReferencedEntity(DTDEntity newAttributeTypeReferencedEntity, NotificationChain msgs) {
		DTDEntity oldAttributeTypeReferencedEntity = attributeTypeReferencedEntity;
		attributeTypeReferencedEntity = newAttributeTypeReferencedEntity;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, oldAttributeTypeReferencedEntity, newAttributeTypeReferencedEntity);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttributeTypeReferencedEntity(DTDEntity newAttributeTypeReferencedEntity) {
		if (newAttributeTypeReferencedEntity != attributeTypeReferencedEntity) {
			NotificationChain msgs = null;
			if (attributeTypeReferencedEntity != null)
				msgs = ((InternalEObject)attributeTypeReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
			if (newAttributeTypeReferencedEntity != null)
				msgs = ((InternalEObject)newAttributeTypeReferencedEntity).eInverseAdd(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
			msgs = basicSetAttributeTypeReferencedEntity(newAttributeTypeReferencedEntity, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY, newAttributeTypeReferencedEntity, newAttributeTypeReferencedEntity));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDElement getDTDElement() {
		if (eContainerFeatureID() != DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT) return null;
		return (DTDElement)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDTDElement(DTDElement newDTDElement, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newDTDElement, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setDTDElement(DTDElement newDTDElement) {
		if (newDTDElement != eInternalContainer() || (eContainerFeatureID() != DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT && newDTDElement != null)) {
			if (EcoreUtil.isAncestor(this, newDTDElement))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newDTDElement != null)
				msgs = ((InternalEObject)newDTDElement).eInverseAdd(this, DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE, DTDElement.class, msgs);
			msgs = basicSetDTDElement(newDTDElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT, newDTDElement, newDTDElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				if (attributeNameReferencedEntity != null)
					msgs = ((InternalEObject)attributeNameReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE, DTDEntity.class, msgs);
				return basicSetAttributeNameReferencedEntity((DTDEntity)otherEnd, msgs);
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				if (attributeTypeReferencedEntity != null)
					msgs = ((InternalEObject)attributeTypeReferencedEntity).eInverseRemove(this, DTDPackage.DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE, DTDEntity.class, msgs);
				return basicSetAttributeTypeReferencedEntity((DTDEntity)otherEnd, msgs);
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetDTDElement((DTDElement)otherEnd, msgs);
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
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				return basicSetAttributeNameReferencedEntity(null, msgs);
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				return basicSetAttributeTypeReferencedEntity(null, msgs);
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				return basicSetDTDElement(null, msgs);
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
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				return eInternalContainer().eInverseRemove(this, DTDPackage.DTD_ELEMENT__DTD_ATTRIBUTE, DTDElement.class, msgs);
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
			case DTDPackage.DTD_ATTRIBUTE__COMMENT:
				return getComment();
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND:
				return getDefaultKind();
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING:
				return getDefaultValueString();
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				if (resolve) return getAttributeNameReferencedEntity();
				return basicGetAttributeNameReferencedEntity();
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				if (resolve) return getAttributeTypeReferencedEntity();
				return basicGetAttributeTypeReferencedEntity();
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				return getDTDElement();
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
			case DTDPackage.DTD_ATTRIBUTE__COMMENT:
				setComment((String)newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND:
				setDefaultKind((DTDDefaultKind)newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING:
				setDefaultValueString((String)newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				setAttributeNameReferencedEntity((DTDEntity)newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				setAttributeTypeReferencedEntity((DTDEntity)newValue);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				setDTDElement((DTDElement)newValue);
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
			case DTDPackage.DTD_ATTRIBUTE__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND:
				setDefaultKind(DEFAULT_KIND_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING:
				setDefaultValueString(DEFAULT_VALUE_STRING_EDEFAULT);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				setAttributeNameReferencedEntity((DTDEntity)null);
				return;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				setAttributeTypeReferencedEntity((DTDEntity)null);
				return;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				setDTDElement((DTDElement)null);
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
			case DTDPackage.DTD_ATTRIBUTE__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_KIND:
				return defaultKind != DEFAULT_KIND_EDEFAULT;
			case DTDPackage.DTD_ATTRIBUTE__DEFAULT_VALUE_STRING:
				return DEFAULT_VALUE_STRING_EDEFAULT == null ? defaultValueString != null : !DEFAULT_VALUE_STRING_EDEFAULT.equals(defaultValueString);
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY:
				return attributeNameReferencedEntity != null;
			case DTDPackage.DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY:
				return attributeTypeReferencedEntity != null;
			case DTDPackage.DTD_ATTRIBUTE__DTD_ELEMENT:
				return getDTDElement() != null;
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
		result.append(", defaultKind: ");
		result.append(defaultKind);
		result.append(", defaultValueString: ");
		result.append(defaultValueString);
		result.append(')');
		return result.toString();
	}

} // DTDAttributeImpl
