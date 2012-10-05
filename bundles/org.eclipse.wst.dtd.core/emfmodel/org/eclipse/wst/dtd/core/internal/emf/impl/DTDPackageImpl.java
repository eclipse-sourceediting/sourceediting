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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent;
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
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;
import org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType;


/**
 * @lastgen class DTDPackageImpl extends EPackageImpl implements DTDPackage,
 *          EPackage {}
 */
public class DTDPackageImpl extends EPackageImpl implements DTDPackage {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdElementContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdGroupContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdAttributeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdElementEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdEmptyContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdAnyContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdpcDataContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdElementReferenceContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdRepeatableContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdFileEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdBasicTypeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdEnumerationTypeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdNotationEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdEntityEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdEntityContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdExternalEntityEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdInternalEntityEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdParameterEntityReferenceEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdEntityReferenceContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dtdContentEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dtdOccurrenceTypeEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dtdDefaultKindEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dtdBasicTypeKindEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dtdEnumGroupKindEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dtdGroupKindEEnum = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum xmlSchemaDefinedTypeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by
	 * the package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory
	 * method {@link #init init()}, which also performs initialization of the
	 * package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DTDPackageImpl() {
		super(eNS_URI, DTDFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	// public DTDPackageImpl()
	// {
	// super(eNS_URI);
	// initializePackage(null);
	// }


	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link DTDPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DTDPackage init() {
		if (isInited) return (DTDPackage)EPackage.Registry.INSTANCE.getEPackage(DTDPackage.eNS_URI);

		// Obtain or create and register package
		DTDPackageImpl theDTDPackage = (DTDPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof DTDPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new DTDPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theDTDPackage.createPackageContents();

		// Initialize created meta-data
		theDTDPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theDTDPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DTDPackage.eNS_URI, theDTDPackage);
		return theDTDPackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDElementContent() {
		return dtdElementContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDElementContent_Group() {
		return (EReference)dtdElementContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDElementContent_Element() {
		return (EReference)dtdElementContentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDGroupContent() {
		return dtdGroupContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDGroupContent_GroupKind() {
		return (EAttribute)dtdGroupContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDGroupContent_Content() {
		return (EReference)dtdGroupContentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDAttribute() {
		return dtdAttributeEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDAttribute_Comment() {
		return (EAttribute)dtdAttributeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDAttribute_DefaultKind() {
		return (EAttribute)dtdAttributeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDAttribute_DefaultValueString() {
		return (EAttribute)dtdAttributeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDAttribute_AttributeNameReferencedEntity() {
		return (EReference)dtdAttributeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDAttribute_AttributeTypeReferencedEntity() {
		return (EReference)dtdAttributeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDAttribute_DTDElement() {
		return (EReference)dtdAttributeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDElement() {
		return dtdElementEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDElement_Comment() {
		return (EAttribute)dtdElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDElement_Content() {
		return (EReference)dtdElementEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDElement_DTDAttribute() {
		return (EReference)dtdElementEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDEmptyContent() {
		return dtdEmptyContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDAnyContent() {
		return dtdAnyContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDPCDataContent() {
		return dtdpcDataContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDElementReferenceContent() {
		return dtdElementReferenceContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDElementReferenceContent_ReferencedElement() {
		return (EReference)dtdElementReferenceContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDRepeatableContent() {
		return dtdRepeatableContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDRepeatableContent_Occurrence() {
		return (EAttribute)dtdRepeatableContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDFile() {
		return dtdFileEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDFile_Comment() {
		return (EAttribute)dtdFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDFile_ParseError() {
		return (EAttribute)dtdFileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDFile_DTDContent() {
		return (EReference)dtdFileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDFile_DTDEnumerationType() {
		return (EReference)dtdFileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDBasicType() {
		return dtdBasicTypeEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDBasicType_Kind() {
		return (EAttribute)dtdBasicTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDEnumerationType() {
		return dtdEnumerationTypeEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDEnumerationType_Kind() {
		return (EAttribute)dtdEnumerationTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEnumerationType_DTDFile() {
		return (EReference)dtdEnumerationTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDNotation() {
		return dtdNotationEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDNotation_Comment() {
		return (EAttribute)dtdNotationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDNotation_SystemID() {
		return (EAttribute)dtdNotationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDNotation_PublicID() {
		return (EAttribute)dtdNotationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDNotation_Entity() {
		return (EReference)dtdNotationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDEntity() {
		return dtdEntityEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDEntity_Comment() {
		return (EAttribute)dtdEntityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDEntity_ParameterEntity() {
		return (EAttribute)dtdEntityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntity_Content() {
		return (EReference)dtdEntityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntity_ParmEntityRef() {
		return (EReference)dtdEntityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntity_EntityReference() {
		return (EReference)dtdEntityEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntity_AttributeNameReference() {
		return (EReference)dtdEntityEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntity_AttributeTypeReference() {
		return (EReference)dtdEntityEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDEntityContent() {
		return dtdEntityContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntityContent_DTDEntity() {
		return (EReference)dtdEntityContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDExternalEntity() {
		return dtdExternalEntityEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDExternalEntity_SystemID() {
		return (EAttribute)dtdExternalEntityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDExternalEntity_PublicID() {
		return (EAttribute)dtdExternalEntityEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDExternalEntity_Notation() {
		return (EReference)dtdExternalEntityEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDExternalEntity_EntityReferencedFromAnotherFile() {
		return (EReference)dtdExternalEntityEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDInternalEntity() {
		return dtdInternalEntityEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDTDInternalEntity_Value() {
		return (EAttribute)dtdInternalEntityEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDParameterEntityReference() {
		return dtdParameterEntityReferenceEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDParameterEntityReference_Entity() {
		return (EReference)dtdParameterEntityReferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDEntityReferenceContent() {
		return dtdEntityReferenceContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDEntityReferenceContent_ElementReferencedEntity() {
		return (EReference)dtdEntityReferenceContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDTDContent() {
		return dtdContentEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDTDContent_DTDFile() {
		return (EReference)dtdContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDTDOccurrenceType() {
		return dtdOccurrenceTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDTDDefaultKind() {
		return dtdDefaultKindEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDTDBasicTypeKind() {
		return dtdBasicTypeKindEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDTDEnumGroupKind() {
		return dtdEnumGroupKindEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDTDGroupKind() {
		return dtdGroupKindEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getXMLSchemaDefinedType() {
		return xmlSchemaDefinedTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public DTDFactory getDTDFactory() {
		return (DTDFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		dtdElementContentEClass = createEClass(DTD_ELEMENT_CONTENT);
		createEReference(dtdElementContentEClass, DTD_ELEMENT_CONTENT__GROUP);
		createEReference(dtdElementContentEClass, DTD_ELEMENT_CONTENT__ELEMENT);

		dtdGroupContentEClass = createEClass(DTD_GROUP_CONTENT);
		createEAttribute(dtdGroupContentEClass, DTD_GROUP_CONTENT__GROUP_KIND);
		createEReference(dtdGroupContentEClass, DTD_GROUP_CONTENT__CONTENT);

		dtdAttributeEClass = createEClass(DTD_ATTRIBUTE);
		createEAttribute(dtdAttributeEClass, DTD_ATTRIBUTE__COMMENT);
		createEAttribute(dtdAttributeEClass, DTD_ATTRIBUTE__DEFAULT_KIND);
		createEAttribute(dtdAttributeEClass, DTD_ATTRIBUTE__DEFAULT_VALUE_STRING);
		createEReference(dtdAttributeEClass, DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY);
		createEReference(dtdAttributeEClass, DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY);
		createEReference(dtdAttributeEClass, DTD_ATTRIBUTE__DTD_ELEMENT);

		dtdElementEClass = createEClass(DTD_ELEMENT);
		createEAttribute(dtdElementEClass, DTD_ELEMENT__COMMENT);
		createEReference(dtdElementEClass, DTD_ELEMENT__CONTENT);
		createEReference(dtdElementEClass, DTD_ELEMENT__DTD_ATTRIBUTE);

		dtdEmptyContentEClass = createEClass(DTD_EMPTY_CONTENT);

		dtdAnyContentEClass = createEClass(DTD_ANY_CONTENT);

		dtdpcDataContentEClass = createEClass(DTD_PC_DATA_CONTENT);

		dtdElementReferenceContentEClass = createEClass(DTD_ELEMENT_REFERENCE_CONTENT);
		createEReference(dtdElementReferenceContentEClass, DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT);

		dtdRepeatableContentEClass = createEClass(DTD_REPEATABLE_CONTENT);
		createEAttribute(dtdRepeatableContentEClass, DTD_REPEATABLE_CONTENT__OCCURRENCE);

		dtdFileEClass = createEClass(DTD_FILE);
		createEAttribute(dtdFileEClass, DTD_FILE__COMMENT);
		createEAttribute(dtdFileEClass, DTD_FILE__PARSE_ERROR);
		createEReference(dtdFileEClass, DTD_FILE__DTD_CONTENT);
		createEReference(dtdFileEClass, DTD_FILE__DTD_ENUMERATION_TYPE);

		dtdBasicTypeEClass = createEClass(DTD_BASIC_TYPE);
		createEAttribute(dtdBasicTypeEClass, DTD_BASIC_TYPE__KIND);

		dtdEnumerationTypeEClass = createEClass(DTD_ENUMERATION_TYPE);
		createEAttribute(dtdEnumerationTypeEClass, DTD_ENUMERATION_TYPE__KIND);
		createEReference(dtdEnumerationTypeEClass, DTD_ENUMERATION_TYPE__DTD_FILE);

		dtdNotationEClass = createEClass(DTD_NOTATION);
		createEAttribute(dtdNotationEClass, DTD_NOTATION__COMMENT);
		createEAttribute(dtdNotationEClass, DTD_NOTATION__SYSTEM_ID);
		createEAttribute(dtdNotationEClass, DTD_NOTATION__PUBLIC_ID);
		createEReference(dtdNotationEClass, DTD_NOTATION__ENTITY);

		dtdEntityEClass = createEClass(DTD_ENTITY);
		createEAttribute(dtdEntityEClass, DTD_ENTITY__COMMENT);
		createEAttribute(dtdEntityEClass, DTD_ENTITY__PARAMETER_ENTITY);
		createEReference(dtdEntityEClass, DTD_ENTITY__CONTENT);
		createEReference(dtdEntityEClass, DTD_ENTITY__PARM_ENTITY_REF);
		createEReference(dtdEntityEClass, DTD_ENTITY__ENTITY_REFERENCE);
		createEReference(dtdEntityEClass, DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE);
		createEReference(dtdEntityEClass, DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE);

		dtdEntityContentEClass = createEClass(DTD_ENTITY_CONTENT);
		createEReference(dtdEntityContentEClass, DTD_ENTITY_CONTENT__DTD_ENTITY);

		dtdExternalEntityEClass = createEClass(DTD_EXTERNAL_ENTITY);
		createEAttribute(dtdExternalEntityEClass, DTD_EXTERNAL_ENTITY__SYSTEM_ID);
		createEAttribute(dtdExternalEntityEClass, DTD_EXTERNAL_ENTITY__PUBLIC_ID);
		createEReference(dtdExternalEntityEClass, DTD_EXTERNAL_ENTITY__NOTATION);
		createEReference(dtdExternalEntityEClass, DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE);

		dtdInternalEntityEClass = createEClass(DTD_INTERNAL_ENTITY);
		createEAttribute(dtdInternalEntityEClass, DTD_INTERNAL_ENTITY__VALUE);

		dtdParameterEntityReferenceEClass = createEClass(DTD_PARAMETER_ENTITY_REFERENCE);
		createEReference(dtdParameterEntityReferenceEClass, DTD_PARAMETER_ENTITY_REFERENCE__ENTITY);

		dtdEntityReferenceContentEClass = createEClass(DTD_ENTITY_REFERENCE_CONTENT);
		createEReference(dtdEntityReferenceContentEClass, DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY);

		dtdContentEClass = createEClass(DTD_CONTENT);
		createEReference(dtdContentEClass, DTD_CONTENT__DTD_FILE);

		// Create enums
		dtdOccurrenceTypeEEnum = createEEnum(DTD_OCCURRENCE_TYPE);
		dtdDefaultKindEEnum = createEEnum(DTD_DEFAULT_KIND);
		dtdBasicTypeKindEEnum = createEEnum(DTD_BASIC_TYPE_KIND);
		dtdEnumGroupKindEEnum = createEEnum(DTD_ENUM_GROUP_KIND);
		dtdGroupKindEEnum = createEEnum(DTD_GROUP_KIND);
		xmlSchemaDefinedTypeEEnum = createEEnum(XML_SCHEMA_DEFINED_TYPE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Add supertypes to classes
		dtdGroupContentEClass.getESuperTypes().add(this.getDTDRepeatableContent());
		dtdAttributeEClass.getESuperTypes().add(theEcorePackage.getEAttribute());
		dtdElementEClass.getESuperTypes().add(theEcorePackage.getEClass());
		dtdElementEClass.getESuperTypes().add(this.getDTDContent());
		dtdEmptyContentEClass.getESuperTypes().add(this.getDTDElementContent());
		dtdAnyContentEClass.getESuperTypes().add(this.getDTDElementContent());
		dtdpcDataContentEClass.getESuperTypes().add(this.getDTDElementContent());
		dtdElementReferenceContentEClass.getESuperTypes().add(this.getDTDRepeatableContent());
		dtdRepeatableContentEClass.getESuperTypes().add(this.getDTDElementContent());
		dtdBasicTypeEClass.getESuperTypes().add(theEcorePackage.getEClass());
		dtdEnumerationTypeEClass.getESuperTypes().add(theEcorePackage.getEEnum());
		dtdNotationEClass.getESuperTypes().add(this.getDTDContent());
		dtdEntityEClass.getESuperTypes().add(this.getDTDContent());
		dtdExternalEntityEClass.getESuperTypes().add(this.getDTDEntityContent());
		dtdInternalEntityEClass.getESuperTypes().add(this.getDTDEntityContent());
		dtdParameterEntityReferenceEClass.getESuperTypes().add(this.getDTDContent());
		dtdEntityReferenceContentEClass.getESuperTypes().add(this.getDTDRepeatableContent());

		// Initialize classes and features; add operations and parameters
		initEClass(dtdElementContentEClass, DTDElementContent.class, "DTDElementContent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDElementContent_Group(), this.getDTDGroupContent(), this.getDTDGroupContent_Content(), "group", null, 0, 1, DTDElementContent.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDElementContent_Element(), this.getDTDElement(), this.getDTDElement_Content(), "element", null, 0, 1, DTDElementContent.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdGroupContentEClass, DTDGroupContent.class, "DTDGroupContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDGroupContent_GroupKind(), this.getDTDGroupKind(), "groupKind", null, 0, 1, DTDGroupContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDGroupContent_Content(), this.getDTDElementContent(), this.getDTDElementContent_Group(), "content", null, 1, -1, DTDGroupContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdAttributeEClass, DTDAttribute.class, "DTDAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDAttribute_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, DTDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDAttribute_DefaultKind(), this.getDTDDefaultKind(), "defaultKind", null, 0, 1, DTDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDAttribute_DefaultValueString(), ecorePackage.getEString(), "defaultValueString", null, 0, 1, DTDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDAttribute_AttributeNameReferencedEntity(), this.getDTDEntity(), this.getDTDEntity_AttributeNameReference(), "attributeNameReferencedEntity", null, 0, 1, DTDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDAttribute_AttributeTypeReferencedEntity(), this.getDTDEntity(), this.getDTDEntity_AttributeTypeReference(), "attributeTypeReferencedEntity", null, 0, 1, DTDAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDAttribute_DTDElement(), this.getDTDElement(), this.getDTDElement_DTDAttribute(), "DTDElement", null, 0, 1, DTDAttribute.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdElementEClass, DTDElement.class, "DTDElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDElement_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, DTDElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDElement_Content(), this.getDTDElementContent(), this.getDTDElementContent_Element(), "content", null, 1, 1, DTDElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDElement_DTDAttribute(), this.getDTDAttribute(), this.getDTDAttribute_DTDElement(), "DTDAttribute", null, 0, -1, DTDElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdEmptyContentEClass, DTDEmptyContent.class, "DTDEmptyContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dtdAnyContentEClass, DTDAnyContent.class, "DTDAnyContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dtdpcDataContentEClass, DTDPCDataContent.class, "DTDPCDataContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dtdElementReferenceContentEClass, DTDElementReferenceContent.class, "DTDElementReferenceContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDElementReferenceContent_ReferencedElement(), this.getDTDElement(), null, "referencedElement", null, 1, 1, DTDElementReferenceContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdRepeatableContentEClass, DTDRepeatableContent.class, "DTDRepeatableContent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDRepeatableContent_Occurrence(), this.getDTDOccurrenceType(), "occurrence", null, 0, 1, DTDRepeatableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdFileEClass, DTDFile.class, "DTDFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDFile_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, DTDFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDFile_ParseError(), ecorePackage.getEBoolean(), "parseError", null, 0, 1, DTDFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDFile_DTDContent(), this.getDTDContent(), this.getDTDContent_DTDFile(), "DTDContent", null, 0, -1, DTDFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDFile_DTDEnumerationType(), this.getDTDEnumerationType(), this.getDTDEnumerationType_DTDFile(), "DTDEnumerationType", null, 0, -1, DTDFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdBasicTypeEClass, DTDBasicType.class, "DTDBasicType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDBasicType_Kind(), this.getDTDBasicTypeKind(), "kind", null, 0, 1, DTDBasicType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdEnumerationTypeEClass, DTDEnumerationType.class, "DTDEnumerationType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDEnumerationType_Kind(), this.getDTDEnumGroupKind(), "kind", null, 0, 1, DTDEnumerationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEnumerationType_DTDFile(), this.getDTDFile(), this.getDTDFile_DTDEnumerationType(), "DTDFile", null, 0, 1, DTDEnumerationType.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdNotationEClass, DTDNotation.class, "DTDNotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDNotation_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, DTDNotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDNotation_SystemID(), ecorePackage.getEString(), "systemID", null, 0, 1, DTDNotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDNotation_PublicID(), ecorePackage.getEString(), "publicID", null, 0, 1, DTDNotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDNotation_Entity(), this.getDTDExternalEntity(), this.getDTDExternalEntity_Notation(), "entity", null, 0, -1, DTDNotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdEntityEClass, DTDEntity.class, "DTDEntity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDEntity_Comment(), ecorePackage.getEString(), "comment", null, 0, 1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDEntity_ParameterEntity(), ecorePackage.getEBoolean(), "parameterEntity", null, 0, 1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEntity_Content(), this.getDTDEntityContent(), this.getDTDEntityContent_DTDEntity(), "content", null, 1, 1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEntity_ParmEntityRef(), this.getDTDParameterEntityReference(), this.getDTDParameterEntityReference_Entity(), "parmEntityRef", null, 1, 1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEntity_EntityReference(), this.getDTDEntityReferenceContent(), this.getDTDEntityReferenceContent_ElementReferencedEntity(), "entityReference", null, 0, -1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEntity_AttributeNameReference(), this.getDTDAttribute(), this.getDTDAttribute_AttributeNameReferencedEntity(), "attributeNameReference", null, 0, -1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDEntity_AttributeTypeReference(), this.getDTDAttribute(), this.getDTDAttribute_AttributeTypeReferencedEntity(), "attributeTypeReference", null, 0, -1, DTDEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdEntityContentEClass, DTDEntityContent.class, "DTDEntityContent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDEntityContent_DTDEntity(), this.getDTDEntity(), this.getDTDEntity_Content(), "DTDEntity", null, 0, 1, DTDEntityContent.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdExternalEntityEClass, DTDExternalEntity.class, "DTDExternalEntity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDExternalEntity_SystemID(), ecorePackage.getEString(), "systemID", null, 0, 1, DTDExternalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDTDExternalEntity_PublicID(), ecorePackage.getEString(), "publicID", null, 0, 1, DTDExternalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDExternalEntity_Notation(), this.getDTDNotation(), this.getDTDNotation_Entity(), "notation", null, 0, 1, DTDExternalEntity.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDTDExternalEntity_EntityReferencedFromAnotherFile(), this.getDTDFile(), null, "entityReferencedFromAnotherFile", null, 0, 1, DTDExternalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdInternalEntityEClass, DTDInternalEntity.class, "DTDInternalEntity", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDTDInternalEntity_Value(), ecorePackage.getEString(), "value", null, 0, 1, DTDInternalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdParameterEntityReferenceEClass, DTDParameterEntityReference.class, "DTDParameterEntityReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDParameterEntityReference_Entity(), this.getDTDEntity(), this.getDTDEntity_ParmEntityRef(), "entity", null, 1, 1, DTDParameterEntityReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdEntityReferenceContentEClass, DTDEntityReferenceContent.class, "DTDEntityReferenceContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDEntityReferenceContent_ElementReferencedEntity(), this.getDTDEntity(), this.getDTDEntity_EntityReference(), "elementReferencedEntity", null, 1, 1, DTDEntityReferenceContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dtdContentEClass, DTDContent.class, "DTDContent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDTDContent_DTDFile(), this.getDTDFile(), this.getDTDFile_DTDContent(), "DTDFile", null, 0, 1, DTDContent.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(dtdOccurrenceTypeEEnum, DTDOccurrenceType.class, "DTDOccurrenceType");
		addEEnumLiteral(dtdOccurrenceTypeEEnum, DTDOccurrenceType.ONE_LITERAL);
		addEEnumLiteral(dtdOccurrenceTypeEEnum, DTDOccurrenceType.OPTIONAL_LITERAL);
		addEEnumLiteral(dtdOccurrenceTypeEEnum, DTDOccurrenceType.ONE_OR_MORE_LITERAL);
		addEEnumLiteral(dtdOccurrenceTypeEEnum, DTDOccurrenceType.ZERO_OR_MORE_LITERAL);

		initEEnum(dtdDefaultKindEEnum, DTDDefaultKind.class, "DTDDefaultKind");
		addEEnumLiteral(dtdDefaultKindEEnum, DTDDefaultKind.IMPLIED_LITERAL);
		addEEnumLiteral(dtdDefaultKindEEnum, DTDDefaultKind.REQUIRED_LITERAL);
		addEEnumLiteral(dtdDefaultKindEEnum, DTDDefaultKind.FIXED_LITERAL);
		addEEnumLiteral(dtdDefaultKindEEnum, DTDDefaultKind.NOFIXED_LITERAL);

		initEEnum(dtdBasicTypeKindEEnum, DTDBasicTypeKind.class, "DTDBasicTypeKind");
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.NONE_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.CDATA_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.ID_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.IDREF_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.IDREFS_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.ENTITY_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.ENTITIES_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.NMTOKEN_LITERAL);
		addEEnumLiteral(dtdBasicTypeKindEEnum, DTDBasicTypeKind.NMTOKENS_LITERAL);

		initEEnum(dtdEnumGroupKindEEnum, DTDEnumGroupKind.class, "DTDEnumGroupKind");
		addEEnumLiteral(dtdEnumGroupKindEEnum, DTDEnumGroupKind.NAME_TOKEN_GROUP_LITERAL);
		addEEnumLiteral(dtdEnumGroupKindEEnum, DTDEnumGroupKind.NOTATION_GROUP_LITERAL);

		initEEnum(dtdGroupKindEEnum, DTDGroupKind.class, "DTDGroupKind");
		addEEnumLiteral(dtdGroupKindEEnum, DTDGroupKind.SEQUENCE_LITERAL);
		addEEnumLiteral(dtdGroupKindEEnum, DTDGroupKind.CHOICE_LITERAL);

		initEEnum(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.class, "XMLSchemaDefinedType");
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.NONE_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.STRING_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.BOOLEAN_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.FLOAT_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.DOUBLE_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.DECIMAL_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.TIMEINSTANT_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.TIMEDURATION_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.RECURRINGINSTANT_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.BINARY_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.URI_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.INTEGER_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.DATE_LITERAL);
		addEEnumLiteral(xmlSchemaDefinedTypeEEnum, XMLSchemaDefinedType.TIME_LITERAL);

		// Create resource
		createResource(eNS_URI);
	}


} // DTDPackageImpl
