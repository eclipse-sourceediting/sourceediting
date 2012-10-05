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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFactory;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDType;
import org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType;

/**
 * @generated
 */
public class DTDFactoryImpl extends EFactoryImpl implements DTDFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DTDFactory init() {
		try {
			DTDFactory theDTDFactory = (DTDFactory)EPackage.Registry.INSTANCE.getEFactory("http:///org/eclipse/wst/dtd/core/dtd.ecore"); 
			if (theDTDFactory != null) {
				return theDTDFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new DTDFactoryImpl();
	}

	protected DTDBasicType dtdBasicType_NONE;
	protected DTDBasicType dtdBasicType_CDATA;
	protected DTDBasicType dtdBasicType_ID;
	protected DTDBasicType dtdBasicType_IDREF;
	protected DTDBasicType dtdBasicType_IDREFS;
	protected DTDBasicType dtdBasicType_ENTITY;
	protected DTDBasicType dtdBasicType_ENTITIES;
	protected DTDBasicType dtdBasicType_NMTOKEN;
	protected DTDBasicType dtdBasicType_NMTOKENS;



	public static DTDFactory instance() {
		return (DTDFactory) getPackage().getEFactoryInstance();
	}

	/**
	 * Gets the dtdBasicType_NONE.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_NONE() {
		if (dtdBasicType_NONE == null) {
			(dtdBasicType_NONE = createDTDBasicType()).setKind(DTDBasicTypeKind.CDATA_LITERAL);
		}
		return dtdBasicType_NONE;
	}

	/**
	 * Gets the dtdBasicType_CDATA.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_CDATA() {
		if (dtdBasicType_CDATA == null) {
			(dtdBasicType_CDATA = createDTDBasicType()).setKind(DTDBasicTypeKind.CDATA_LITERAL);
		}
		return dtdBasicType_CDATA;
	}

	/**
	 * Gets the dtdBasicType_ID.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_ID() {
		if (dtdBasicType_ID == null) {
			(dtdBasicType_ID = createDTDBasicType()).setKind(DTDBasicTypeKind.ID_LITERAL);
		}
		return dtdBasicType_ID;
	}

	/**
	 * Gets the dtdBasicType_IDREF.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_IDREF() {
		if (dtdBasicType_IDREF == null) {
			(dtdBasicType_IDREF = createDTDBasicType()).setKind(DTDBasicTypeKind.IDREF_LITERAL);
		}
		return dtdBasicType_IDREF;
	}

	/**
	 * Gets the dtdBasicType_IDREFS.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_IDREFS() {
		if (dtdBasicType_IDREFS == null) {
			(dtdBasicType_IDREFS = createDTDBasicType()).setKind(DTDBasicTypeKind.IDREFS_LITERAL);
		}
		return dtdBasicType_IDREFS;
	}

	/**
	 * Gets the dtdBasicType_ENTITY.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_ENTITY() {
		if (dtdBasicType_ENTITY == null) {
			(dtdBasicType_ENTITY = createDTDBasicType()).setKind(DTDBasicTypeKind.ENTITY_LITERAL);
		}
		return dtdBasicType_ENTITY;
	}

	/**
	 * Gets the dtdBasicType_ENTITIES.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_ENTITIES() {
		if (dtdBasicType_ENTITIES == null) {
			(dtdBasicType_ENTITIES = createDTDBasicType()).setKind(DTDBasicTypeKind.ENTITIES_LITERAL);
		}
		return dtdBasicType_ENTITIES;
	}

	/**
	 * Gets the dtdBasicType_NMTOKEN.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_NMTOKEN() {
		if (dtdBasicType_NMTOKEN == null) {
			(dtdBasicType_NMTOKEN = createDTDBasicType()).setKind(DTDBasicTypeKind.NMTOKEN_LITERAL);
		}
		return dtdBasicType_NMTOKEN;
	}

	/**
	 * Gets the dtdBasicType_NMTOKENS.
	 * 
	 * @return Returns a DTDType
	 */
	public DTDType getDTDBasicType_NMTOKENS() {
		if (dtdBasicType_NMTOKENS == null) {
			(dtdBasicType_NMTOKENS = createDTDBasicType()).setKind(DTDBasicTypeKind.NMTOKENS_LITERAL);
		}
		return dtdBasicType_NMTOKENS;
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public DTDFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case DTDPackage.DTD_GROUP_CONTENT: return createDTDGroupContent();
			case DTDPackage.DTD_ATTRIBUTE: return createDTDAttribute();
			case DTDPackage.DTD_ELEMENT: return createDTDElement();
			case DTDPackage.DTD_EMPTY_CONTENT: return createDTDEmptyContent();
			case DTDPackage.DTD_ANY_CONTENT: return createDTDAnyContent();
			case DTDPackage.DTD_PC_DATA_CONTENT: return createDTDPCDataContent();
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT: return createDTDElementReferenceContent();
			case DTDPackage.DTD_FILE: return createDTDFile();
			case DTDPackage.DTD_BASIC_TYPE: return createDTDBasicType();
			case DTDPackage.DTD_ENUMERATION_TYPE: return createDTDEnumerationType();
			case DTDPackage.DTD_NOTATION: return createDTDNotation();
			case DTDPackage.DTD_ENTITY: return createDTDEntity();
			case DTDPackage.DTD_EXTERNAL_ENTITY: return createDTDExternalEntity();
			case DTDPackage.DTD_INTERNAL_ENTITY: return createDTDInternalEntity();
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE: return createDTDParameterEntityReference();
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT: return createDTDEntityReferenceContent();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case DTDPackage.DTD_OCCURRENCE_TYPE:
				return createDTDOccurrenceTypeFromString(eDataType, initialValue);
			case DTDPackage.DTD_DEFAULT_KIND:
				return createDTDDefaultKindFromString(eDataType, initialValue);
			case DTDPackage.DTD_BASIC_TYPE_KIND:
				return createDTDBasicTypeKindFromString(eDataType, initialValue);
			case DTDPackage.DTD_ENUM_GROUP_KIND:
				return createDTDEnumGroupKindFromString(eDataType, initialValue);
			case DTDPackage.DTD_GROUP_KIND:
				return createDTDGroupKindFromString(eDataType, initialValue);
			case DTDPackage.XML_SCHEMA_DEFINED_TYPE:
				return createXMLSchemaDefinedTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case DTDPackage.DTD_OCCURRENCE_TYPE:
				return convertDTDOccurrenceTypeToString(eDataType, instanceValue);
			case DTDPackage.DTD_DEFAULT_KIND:
				return convertDTDDefaultKindToString(eDataType, instanceValue);
			case DTDPackage.DTD_BASIC_TYPE_KIND:
				return convertDTDBasicTypeKindToString(eDataType, instanceValue);
			case DTDPackage.DTD_ENUM_GROUP_KIND:
				return convertDTDEnumGroupKindToString(eDataType, instanceValue);
			case DTDPackage.DTD_GROUP_KIND:
				return convertDTDGroupKindToString(eDataType, instanceValue);
			case DTDPackage.XML_SCHEMA_DEFINED_TYPE:
				return convertXMLSchemaDefinedTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDGroupContent createDTDGroupContent() {
		DTDGroupContentImpl dtdGroupContent = new DTDGroupContentImpl();
		return dtdGroupContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDAttribute createDTDAttribute() {
		DTDAttributeImpl dtdAttribute = new DTDAttributeImpl();
		return dtdAttribute;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDElement createDTDElement() {
		DTDElementImpl dtdElement = new DTDElementImpl();
		return dtdElement;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEmptyContent createDTDEmptyContent() {
		DTDEmptyContentImpl dtdEmptyContent = new DTDEmptyContentImpl();
		return dtdEmptyContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDAnyContent createDTDAnyContent() {
		DTDAnyContentImpl dtdAnyContent = new DTDAnyContentImpl();
		return dtdAnyContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDPCDataContent createDTDPCDataContent() {
		DTDPCDataContentImpl dtdpcDataContent = new DTDPCDataContentImpl();
		return dtdpcDataContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDElementReferenceContent createDTDElementReferenceContent() {
		DTDElementReferenceContentImpl dtdElementReferenceContent = new DTDElementReferenceContentImpl();
		return dtdElementReferenceContent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDFile createDTDFile() {
		DTDFileImpl dtdFile = new DTDFileImpl();
		return dtdFile;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDBasicType createDTDBasicType() {
		DTDBasicTypeImpl dtdBasicType = new DTDBasicTypeImpl();
		return dtdBasicType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEnumerationType createDTDEnumerationType() {
		DTDEnumerationTypeImpl dtdEnumerationType = new DTDEnumerationTypeImpl();
		return dtdEnumerationType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDNotation createDTDNotation() {
		DTDNotationImpl dtdNotation = new DTDNotationImpl();
		return dtdNotation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntity createDTDEntity() {
		DTDEntityImpl dtdEntity = new DTDEntityImpl();
		return dtdEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDExternalEntity createDTDExternalEntity() {
		DTDExternalEntityImpl dtdExternalEntity = new DTDExternalEntityImpl();
		return dtdExternalEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDInternalEntity createDTDInternalEntity() {
		DTDInternalEntityImpl dtdInternalEntity = new DTDInternalEntityImpl();
		return dtdInternalEntity;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDParameterEntityReference createDTDParameterEntityReference() {
		DTDParameterEntityReferenceImpl dtdParameterEntityReference = new DTDParameterEntityReferenceImpl();
		return dtdParameterEntityReference;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEntityReferenceContent createDTDEntityReferenceContent() {
		DTDEntityReferenceContentImpl dtdEntityReferenceContent = new DTDEntityReferenceContentImpl();
		return dtdEntityReferenceContent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DTDOccurrenceType createDTDOccurrenceTypeFromString(EDataType eDataType, String initialValue) {
		DTDOccurrenceType result = DTDOccurrenceType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDTDOccurrenceTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DTDDefaultKind createDTDDefaultKindFromString(EDataType eDataType, String initialValue) {
		DTDDefaultKind result = DTDDefaultKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDTDDefaultKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DTDBasicTypeKind createDTDBasicTypeKindFromString(EDataType eDataType, String initialValue) {
		DTDBasicTypeKind result = DTDBasicTypeKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDTDBasicTypeKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DTDEnumGroupKind createDTDEnumGroupKindFromString(EDataType eDataType, String initialValue) {
		DTDEnumGroupKind result = DTDEnumGroupKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDTDEnumGroupKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DTDGroupKind createDTDGroupKindFromString(EDataType eDataType, String initialValue) {
		DTDGroupKind result = DTDGroupKind.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDTDGroupKindToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public XMLSchemaDefinedType createXMLSchemaDefinedTypeFromString(EDataType eDataType, String initialValue) {
		XMLSchemaDefinedType result = XMLSchemaDefinedType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertXMLSchemaDefinedTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDPackage getDTDPackage() {
		return (DTDPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	public static DTDPackage getPackage() {
		return DTDPackage.eINSTANCE;
	}
}
