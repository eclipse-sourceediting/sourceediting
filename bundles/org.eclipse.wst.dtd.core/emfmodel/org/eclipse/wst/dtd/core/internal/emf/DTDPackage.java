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
package org.eclipse.wst.dtd.core.internal.emf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFactory
 * @model kind="package"
 * @generated
 */
public interface DTDPackage extends EPackage {

	/**
	 * The package name.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "dtd"; //$NON-NLS-1$

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http:///com/ibm/etools/dtd.ecore"; //$NON-NLS-1$

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.dtd.core.internal.emf"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	DTDPackage eINSTANCE = org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementContentImpl <em>Element Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElementContent()
	 * @generated
	 */
	int DTD_ELEMENT_CONTENT = 0;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_CONTENT__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_CONTENT__ELEMENT = 1;

	/**
	 * The number of structural features of the '<em>Element Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_CONTENT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDRepeatableContentImpl <em>Repeatable Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDRepeatableContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDRepeatableContent()
	 * @generated
	 */
	int DTD_REPEATABLE_CONTENT = 8;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_REPEATABLE_CONTENT__GROUP = DTD_ELEMENT_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_REPEATABLE_CONTENT__ELEMENT = DTD_ELEMENT_CONTENT__ELEMENT;

	/**
	 * The feature id for the '<em><b>Occurrence</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_REPEATABLE_CONTENT__OCCURRENCE = DTD_ELEMENT_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Repeatable Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_REPEATABLE_CONTENT_FEATURE_COUNT = DTD_ELEMENT_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDGroupContentImpl <em>Group Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDGroupContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDGroupContent()
	 * @generated
	 */
	int DTD_GROUP_CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT__GROUP = DTD_REPEATABLE_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT__ELEMENT = DTD_REPEATABLE_CONTENT__ELEMENT;

	/**
	 * The feature id for the '<em><b>Occurrence</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT__OCCURRENCE = DTD_REPEATABLE_CONTENT__OCCURRENCE;

	/**
	 * The feature id for the '<em><b>Group Kind</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT__GROUP_KIND = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT__CONTENT = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Group Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_GROUP_CONTENT_FEATURE_COUNT = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl <em>Attribute</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDAttribute()
	 * @generated
	 */
	int DTD_ATTRIBUTE = 2;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__EANNOTATIONS = EcorePackage.EATTRIBUTE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__NAME = EcorePackage.EATTRIBUTE__NAME;

	/**
	 * The feature id for the '<em><b>Ordered</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ORDERED = EcorePackage.EATTRIBUTE__ORDERED;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__UNIQUE = EcorePackage.EATTRIBUTE__UNIQUE;

	/**
	 * The feature id for the '<em><b>Lower Bound</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__LOWER_BOUND = EcorePackage.EATTRIBUTE__LOWER_BOUND;

	/**
	 * The feature id for the '<em><b>Upper Bound</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__UPPER_BOUND = EcorePackage.EATTRIBUTE__UPPER_BOUND;

	/**
	 * The feature id for the '<em><b>Many</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__MANY = EcorePackage.EATTRIBUTE__MANY;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__REQUIRED = EcorePackage.EATTRIBUTE__REQUIRED;

	/**
	 * The feature id for the '<em><b>EType</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ETYPE = EcorePackage.EATTRIBUTE__ETYPE;

	/**
	 * The feature id for the '<em><b>EGeneric Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__EGENERIC_TYPE = EcorePackage.EATTRIBUTE__EGENERIC_TYPE;

	/**
	 * The feature id for the '<em><b>Changeable</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__CHANGEABLE = EcorePackage.EATTRIBUTE__CHANGEABLE;

	/**
	 * The feature id for the '<em><b>Volatile</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__VOLATILE = EcorePackage.EATTRIBUTE__VOLATILE;

	/**
	 * The feature id for the '<em><b>Transient</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__TRANSIENT = EcorePackage.EATTRIBUTE__TRANSIENT;

	/**
	 * The feature id for the '<em><b>Default Value Literal</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DEFAULT_VALUE_LITERAL = EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DEFAULT_VALUE = EcorePackage.EATTRIBUTE__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Unsettable</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__UNSETTABLE = EcorePackage.EATTRIBUTE__UNSETTABLE;

	/**
	 * The feature id for the '<em><b>Derived</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DERIVED = EcorePackage.EATTRIBUTE__DERIVED;

	/**
	 * The feature id for the '<em><b>EContaining Class</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ECONTAINING_CLASS = EcorePackage.EATTRIBUTE__ECONTAINING_CLASS;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ID = EcorePackage.EATTRIBUTE__ID;

	/**
	 * The feature id for the '<em><b>EAttribute Type</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__EATTRIBUTE_TYPE = EcorePackage.EATTRIBUTE__EATTRIBUTE_TYPE;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__COMMENT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Default Kind</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DEFAULT_KIND = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Default Value String</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DEFAULT_VALUE_STRING = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Attribute Name Referenced Entity</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Attribute Type Referenced Entity</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>DTD Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE__DTD_ELEMENT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Attribute</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ATTRIBUTE_FEATURE_COUNT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementImpl <em>Element</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElement()
	 * @generated
	 */
	int DTD_ELEMENT = 3;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EANNOTATIONS = EcorePackage.ECLASS__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__NAME = EcorePackage.ECLASS__NAME;

	/**
	 * The feature id for the '<em><b>Instance Class Name</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__INSTANCE_CLASS_NAME = EcorePackage.ECLASS__INSTANCE_CLASS_NAME;

	/**
	 * The feature id for the '<em><b>Instance Class</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__INSTANCE_CLASS = EcorePackage.ECLASS__INSTANCE_CLASS;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__DEFAULT_VALUE = EcorePackage.ECLASS__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Instance Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__INSTANCE_TYPE_NAME = EcorePackage.ECLASS__INSTANCE_TYPE_NAME;

	/**
	 * The feature id for the '<em><b>EPackage</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EPACKAGE = EcorePackage.ECLASS__EPACKAGE;

	/**
	 * The feature id for the '<em><b>EType Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__ETYPE_PARAMETERS = EcorePackage.ECLASS__ETYPE_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Abstract</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__ABSTRACT = EcorePackage.ECLASS__ABSTRACT;

	/**
	 * The feature id for the '<em><b>Interface</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__INTERFACE = EcorePackage.ECLASS__INTERFACE;

	/**
	 * The feature id for the '<em><b>ESuper Types</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__ESUPER_TYPES = EcorePackage.ECLASS__ESUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EOperations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EOPERATIONS = EcorePackage.ECLASS__EOPERATIONS;

	/**
	 * The feature id for the '<em><b>EAll Attributes</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_ATTRIBUTES = EcorePackage.ECLASS__EALL_ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EAll References</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_REFERENCES = EcorePackage.ECLASS__EALL_REFERENCES;

	/**
	 * The feature id for the '<em><b>EReferences</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EREFERENCES = EcorePackage.ECLASS__EREFERENCES;

	/**
	 * The feature id for the '<em><b>EAttributes</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EATTRIBUTES = EcorePackage.ECLASS__EATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EAll Containments</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_CONTAINMENTS = EcorePackage.ECLASS__EALL_CONTAINMENTS;

	/**
	 * The feature id for the '<em><b>EAll Operations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_OPERATIONS = EcorePackage.ECLASS__EALL_OPERATIONS;

	/**
	 * The feature id for the '<em><b>EAll Structural Features</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_STRUCTURAL_FEATURES = EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES;

	/**
	 * The feature id for the '<em><b>EAll Super Types</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_SUPER_TYPES = EcorePackage.ECLASS__EALL_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EID Attribute</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EID_ATTRIBUTE = EcorePackage.ECLASS__EID_ATTRIBUTE;

	/**
	 * The feature id for the '<em><b>EStructural Features</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__ESTRUCTURAL_FEATURES = EcorePackage.ECLASS__ESTRUCTURAL_FEATURES;

	/**
	 * The feature id for the '<em><b>EGeneric Super Types</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EGENERIC_SUPER_TYPES = EcorePackage.ECLASS__EGENERIC_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EAll Generic Super Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__EALL_GENERIC_SUPER_TYPES = EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__DTD_FILE = EcorePackage.ECLASS_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__COMMENT = EcorePackage.ECLASS_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__CONTENT = EcorePackage.ECLASS_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>DTD Attribute</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT__DTD_ATTRIBUTE = EcorePackage.ECLASS_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Element</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_FEATURE_COUNT = EcorePackage.ECLASS_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEmptyContentImpl <em>Empty Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEmptyContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEmptyContent()
	 * @generated
	 */
	int DTD_EMPTY_CONTENT = 4;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EMPTY_CONTENT__GROUP = DTD_ELEMENT_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EMPTY_CONTENT__ELEMENT = DTD_ELEMENT_CONTENT__ELEMENT;

	/**
	 * The number of structural features of the '<em>Empty Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EMPTY_CONTENT_FEATURE_COUNT = DTD_ELEMENT_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAnyContentImpl <em>Any Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDAnyContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDAnyContent()
	 * @generated
	 */
	int DTD_ANY_CONTENT = 5;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ANY_CONTENT__GROUP = DTD_ELEMENT_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ANY_CONTENT__ELEMENT = DTD_ELEMENT_CONTENT__ELEMENT;

	/**
	 * The number of structural features of the '<em>Any Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ANY_CONTENT_FEATURE_COUNT = DTD_ELEMENT_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDPCDataContentImpl <em>PC Data Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPCDataContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDPCDataContent()
	 * @generated
	 */
	int DTD_PC_DATA_CONTENT = 6;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_PC_DATA_CONTENT__GROUP = DTD_ELEMENT_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_PC_DATA_CONTENT__ELEMENT = DTD_ELEMENT_CONTENT__ELEMENT;

	/**
	 * The number of structural features of the '<em>PC Data Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_PC_DATA_CONTENT_FEATURE_COUNT = DTD_ELEMENT_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementReferenceContentImpl <em>Element Reference Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementReferenceContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElementReferenceContent()
	 * @generated
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT = 7;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT__GROUP = DTD_REPEATABLE_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT__ELEMENT = DTD_REPEATABLE_CONTENT__ELEMENT;

	/**
	 * The feature id for the '<em><b>Occurrence</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT__OCCURRENCE = DTD_REPEATABLE_CONTENT__OCCURRENCE;

	/**
	 * The feature id for the '<em><b>Referenced Element</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Element Reference Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ELEMENT_REFERENCE_CONTENT_FEATURE_COUNT = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl <em>File</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDFile()
	 * @generated
	 */
	int DTD_FILE = 9;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_FILE__COMMENT = 0;

	/**
	 * The feature id for the '<em><b>Parse Error</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_FILE__PARSE_ERROR = 1;

	/**
	 * The feature id for the '<em><b>DTD Content</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_FILE__DTD_CONTENT = 2;

	/**
	 * The feature id for the '<em><b>DTD Enumeration Type</b></em>'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_FILE__DTD_ENUMERATION_TYPE = 3;

	/**
	 * The number of structural features of the '<em>File</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_FILE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl <em>Basic Type</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDBasicType()
	 * @generated
	 */
	int DTD_BASIC_TYPE = 10;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EANNOTATIONS = EcorePackage.ECLASS__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__NAME = EcorePackage.ECLASS__NAME;

	/**
	 * The feature id for the '<em><b>Instance Class Name</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__INSTANCE_CLASS_NAME = EcorePackage.ECLASS__INSTANCE_CLASS_NAME;

	/**
	 * The feature id for the '<em><b>Instance Class</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__INSTANCE_CLASS = EcorePackage.ECLASS__INSTANCE_CLASS;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__DEFAULT_VALUE = EcorePackage.ECLASS__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Instance Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__INSTANCE_TYPE_NAME = EcorePackage.ECLASS__INSTANCE_TYPE_NAME;

	/**
	 * The feature id for the '<em><b>EPackage</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EPACKAGE = EcorePackage.ECLASS__EPACKAGE;

	/**
	 * The feature id for the '<em><b>EType Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__ETYPE_PARAMETERS = EcorePackage.ECLASS__ETYPE_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Abstract</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__ABSTRACT = EcorePackage.ECLASS__ABSTRACT;

	/**
	 * The feature id for the '<em><b>Interface</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__INTERFACE = EcorePackage.ECLASS__INTERFACE;

	/**
	 * The feature id for the '<em><b>ESuper Types</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__ESUPER_TYPES = EcorePackage.ECLASS__ESUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EOperations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EOPERATIONS = EcorePackage.ECLASS__EOPERATIONS;

	/**
	 * The feature id for the '<em><b>EAll Attributes</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_ATTRIBUTES = EcorePackage.ECLASS__EALL_ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EAll References</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_REFERENCES = EcorePackage.ECLASS__EALL_REFERENCES;

	/**
	 * The feature id for the '<em><b>EReferences</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EREFERENCES = EcorePackage.ECLASS__EREFERENCES;

	/**
	 * The feature id for the '<em><b>EAttributes</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EATTRIBUTES = EcorePackage.ECLASS__EATTRIBUTES;

	/**
	 * The feature id for the '<em><b>EAll Containments</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_CONTAINMENTS = EcorePackage.ECLASS__EALL_CONTAINMENTS;

	/**
	 * The feature id for the '<em><b>EAll Operations</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_OPERATIONS = EcorePackage.ECLASS__EALL_OPERATIONS;

	/**
	 * The feature id for the '<em><b>EAll Structural Features</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_STRUCTURAL_FEATURES = EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES;

	/**
	 * The feature id for the '<em><b>EAll Super Types</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_SUPER_TYPES = EcorePackage.ECLASS__EALL_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EID Attribute</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EID_ATTRIBUTE = EcorePackage.ECLASS__EID_ATTRIBUTE;

	/**
	 * The feature id for the '<em><b>EStructural Features</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__ESTRUCTURAL_FEATURES = EcorePackage.ECLASS__ESTRUCTURAL_FEATURES;

	/**
	 * The feature id for the '<em><b>EGeneric Super Types</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EGENERIC_SUPER_TYPES = EcorePackage.ECLASS__EGENERIC_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>EAll Generic Super Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__EALL_GENERIC_SUPER_TYPES = EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE__KIND = EcorePackage.ECLASS_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Basic Type</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_BASIC_TYPE_FEATURE_COUNT = EcorePackage.ECLASS_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEnumerationTypeImpl <em>Enumeration Type</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEnumerationTypeImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEnumerationType()
	 * @generated
	 */
	int DTD_ENUMERATION_TYPE = 11;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__EANNOTATIONS = EcorePackage.EENUM__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__NAME = EcorePackage.EENUM__NAME;

	/**
	 * The feature id for the '<em><b>Instance Class Name</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__INSTANCE_CLASS_NAME = EcorePackage.EENUM__INSTANCE_CLASS_NAME;

	/**
	 * The feature id for the '<em><b>Instance Class</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__INSTANCE_CLASS = EcorePackage.EENUM__INSTANCE_CLASS;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__DEFAULT_VALUE = EcorePackage.EENUM__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Instance Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__INSTANCE_TYPE_NAME = EcorePackage.EENUM__INSTANCE_TYPE_NAME;

	/**
	 * The feature id for the '<em><b>EPackage</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__EPACKAGE = EcorePackage.EENUM__EPACKAGE;

	/**
	 * The feature id for the '<em><b>EType Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__ETYPE_PARAMETERS = EcorePackage.EENUM__ETYPE_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Serializable</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__SERIALIZABLE = EcorePackage.EENUM__SERIALIZABLE;

	/**
	 * The feature id for the '<em><b>ELiterals</b></em>' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__ELITERALS = EcorePackage.EENUM__ELITERALS;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__KIND = EcorePackage.EENUM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE__DTD_FILE = EcorePackage.EENUM_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Enumeration Type</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENUMERATION_TYPE_FEATURE_COUNT = EcorePackage.EENUM_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDContentImpl <em>Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDContent()
	 * @generated
	 */
	int DTD_CONTENT = 19;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_CONTENT__DTD_FILE = 0;

	/**
	 * The number of structural features of the '<em>Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_CONTENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl <em>Notation</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDNotation()
	 * @generated
	 */
	int DTD_NOTATION = 12;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION__DTD_FILE = DTD_CONTENT__DTD_FILE;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION__COMMENT = DTD_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>System ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION__SYSTEM_ID = DTD_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Public ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION__PUBLIC_ID = DTD_CONTENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Entity</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION__ENTITY = DTD_CONTENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Notation</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_NOTATION_FEATURE_COUNT = DTD_CONTENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl <em>Entity</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntity()
	 * @generated
	 */
	int DTD_ENTITY = 13;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__DTD_FILE = DTD_CONTENT__DTD_FILE;

	/**
	 * The feature id for the '<em><b>Comment</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__COMMENT = DTD_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Parameter Entity</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__PARAMETER_ENTITY = DTD_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Content</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__CONTENT = DTD_CONTENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Parm Entity Ref</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__PARM_ENTITY_REF = DTD_CONTENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Entity Reference</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__ENTITY_REFERENCE = DTD_CONTENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Attribute Name Reference</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE = DTD_CONTENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Attribute Type Reference</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE = DTD_CONTENT_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Entity</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_FEATURE_COUNT = DTD_CONTENT_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityContentImpl <em>Entity Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntityContent()
	 * @generated
	 */
	int DTD_ENTITY_CONTENT = 14;

	/**
	 * The feature id for the '<em><b>DTD Entity</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_CONTENT__DTD_ENTITY = 0;

	/**
	 * The number of structural features of the '<em>Entity Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_CONTENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDExternalEntityImpl <em>External Entity</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDExternalEntityImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDExternalEntity()
	 * @generated
	 */
	int DTD_EXTERNAL_ENTITY = 15;

	/**
	 * The feature id for the '<em><b>DTD Entity</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY__DTD_ENTITY = DTD_ENTITY_CONTENT__DTD_ENTITY;

	/**
	 * The feature id for the '<em><b>System ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY__SYSTEM_ID = DTD_ENTITY_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Public ID</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY__PUBLIC_ID = DTD_ENTITY_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Notation</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY__NOTATION = DTD_ENTITY_CONTENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Entity Referenced From Another File</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE = DTD_ENTITY_CONTENT_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>External Entity</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_EXTERNAL_ENTITY_FEATURE_COUNT = DTD_ENTITY_CONTENT_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDInternalEntityImpl <em>Internal Entity</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDInternalEntityImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDInternalEntity()
	 * @generated
	 */
	int DTD_INTERNAL_ENTITY = 16;

	/**
	 * The feature id for the '<em><b>DTD Entity</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_INTERNAL_ENTITY__DTD_ENTITY = DTD_ENTITY_CONTENT__DTD_ENTITY;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_INTERNAL_ENTITY__VALUE = DTD_ENTITY_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Internal Entity</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_INTERNAL_ENTITY_FEATURE_COUNT = DTD_ENTITY_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDParameterEntityReferenceImpl <em>Parameter Entity Reference</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDParameterEntityReferenceImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDParameterEntityReference()
	 * @generated
	 */
	int DTD_PARAMETER_ENTITY_REFERENCE = 17;

	/**
	 * The feature id for the '<em><b>DTD File</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_PARAMETER_ENTITY_REFERENCE__DTD_FILE = DTD_CONTENT__DTD_FILE;

	/**
	 * The feature id for the '<em><b>Entity</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int DTD_PARAMETER_ENTITY_REFERENCE__ENTITY = DTD_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Parameter Entity Reference</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_PARAMETER_ENTITY_REFERENCE_FEATURE_COUNT = DTD_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityReferenceContentImpl <em>Entity Reference Content</em>}' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityReferenceContentImpl
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntityReferenceContent()
	 * @generated
	 */
	int DTD_ENTITY_REFERENCE_CONTENT = 18;

	/**
	 * The feature id for the '<em><b>Group</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_REFERENCE_CONTENT__GROUP = DTD_REPEATABLE_CONTENT__GROUP;

	/**
	 * The feature id for the '<em><b>Element</b></em>' container reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_REFERENCE_CONTENT__ELEMENT = DTD_REPEATABLE_CONTENT__ELEMENT;

	/**
	 * The feature id for the '<em><b>Occurrence</b></em>' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_REFERENCE_CONTENT__OCCURRENCE = DTD_REPEATABLE_CONTENT__OCCURRENCE;

	/**
	 * The feature id for the '<em><b>Element Referenced Entity</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Entity Reference Content</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DTD_ENTITY_REFERENCE_CONTENT_FEATURE_COUNT = DTD_REPEATABLE_CONTENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType <em>Occurrence Type</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDOccurrenceType()
	 * @generated
	 */
	int DTD_OCCURRENCE_TYPE = 20;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind <em>Default Kind</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDDefaultKind()
	 * @generated
	 */
	int DTD_DEFAULT_KIND = 21;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind <em>Basic Type Kind</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDBasicTypeKind()
	 * @generated
	 */
	int DTD_BASIC_TYPE_KIND = 22;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind <em>Enum Group Kind</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEnumGroupKind()
	 * @generated
	 */
	int DTD_ENUM_GROUP_KIND = 23;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind <em>Group Kind</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDGroupKind()
	 * @generated
	 */
	int DTD_GROUP_KIND = 24;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType <em>XML Schema Defined Type</em>}' enum.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType
	 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getXMLSchemaDefinedType()
	 * @generated
	 */
	int XML_SCHEMA_DEFINED_TYPE = 25;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent <em>Element Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementContent
	 * @generated
	 */
	EClass getDTDElementContent();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Group</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup()
	 * @see #getDTDElementContent()
	 * @generated
	 */
	EReference getDTDElementContent_Group();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement <em>Element</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Element</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement()
	 * @see #getDTDElementContent()
	 * @generated
	 */
	EReference getDTDElementContent_Element();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent <em>Group Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Group Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent
	 * @generated
	 */
	EClass getDTDGroupContent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getGroupKind <em>Group Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Group Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getGroupKind()
	 * @see #getDTDGroupContent()
	 * @generated
	 */
	EAttribute getDTDGroupContent_GroupKind();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getContent()
	 * @see #getDTDGroupContent()
	 * @generated
	 */
	EReference getDTDGroupContent_Content();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute <em>Attribute</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute
	 * @generated
	 */
	EClass getDTDAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getComment()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EAttribute getDTDAttribute_Comment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultKind <em>Default Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultKind()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EAttribute getDTDAttribute_DefaultKind();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultValueString <em>Default Value String</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default Value String</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDefaultValueString()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EAttribute getDTDAttribute_DefaultValueString();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity <em>Attribute Name Referenced Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Attribute Name Referenced Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeNameReferencedEntity()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EReference getDTDAttribute_AttributeNameReferencedEntity();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity <em>Attribute Type Referenced Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Attribute Type Referenced Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getAttributeTypeReferencedEntity()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EReference getDTDAttribute_AttributeTypeReferencedEntity();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement <em>DTD Element</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>DTD Element</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAttribute#getDTDElement()
	 * @see #getDTDAttribute()
	 * @generated
	 */
	EReference getDTDAttribute_DTDElement();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement <em>Element</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement
	 * @generated
	 */
	EClass getDTDElement();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement#getComment()
	 * @see #getDTDElement()
	 * @generated
	 */
	EAttribute getDTDElement_Comment();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent()
	 * @see #getDTDElement()
	 * @generated
	 */
	EReference getDTDElement_Content();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getDTDAttribute <em>DTD Attribute</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>DTD Attribute</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement#getDTDAttribute()
	 * @see #getDTDElement()
	 * @generated
	 */
	EReference getDTDElement_DTDAttribute();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent <em>Empty Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Empty Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent
	 * @generated
	 */
	EClass getDTDEmptyContent();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent <em>Any Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Any Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent
	 * @generated
	 */
	EClass getDTDAnyContent();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent <em>PC Data Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>PC Data Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent
	 * @generated
	 */
	EClass getDTDPCDataContent();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent <em>Element Reference Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Reference Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent
	 * @generated
	 */
	EClass getDTDElementReferenceContent();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent#getReferencedElement <em>Referenced Element</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Referenced Element</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent#getReferencedElement()
	 * @see #getDTDElementReferenceContent()
	 * @generated
	 */
	EReference getDTDElementReferenceContent_ReferencedElement();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent <em>Repeatable Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Repeatable Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent
	 * @generated
	 */
	EClass getDTDRepeatableContent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent#getOccurrence <em>Occurrence</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Occurrence</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent#getOccurrence()
	 * @see #getDTDRepeatableContent()
	 * @generated
	 */
	EAttribute getDTDRepeatableContent_Occurrence();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile <em>File</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>File</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile
	 * @generated
	 */
	EClass getDTDFile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#getComment()
	 * @see #getDTDFile()
	 * @generated
	 */
	EAttribute getDTDFile_Comment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#isParseError <em>Parse Error</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parse Error</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#isParseError()
	 * @see #getDTDFile()
	 * @generated
	 */
	EAttribute getDTDFile_ParseError();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDContent <em>DTD Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>DTD Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDContent()
	 * @see #getDTDFile()
	 * @generated
	 */
	EReference getDTDFile_DTDContent();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDEnumerationType <em>DTD Enumeration Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>DTD Enumeration Type</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDEnumerationType()
	 * @see #getDTDFile()
	 * @generated
	 */
	EReference getDTDFile_DTDEnumerationType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicType <em>Basic Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Basic Type</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicType
	 * @generated
	 */
	EClass getDTDBasicType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicType#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicType#getKind()
	 * @see #getDTDBasicType()
	 * @generated
	 */
	EAttribute getDTDBasicType_Kind();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType <em>Enumeration Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enumeration Type</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType
	 * @generated
	 */
	EClass getDTDEnumerationType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getKind()
	 * @see #getDTDEnumerationType()
	 * @generated
	 */
	EAttribute getDTDEnumerationType_Kind();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile <em>DTD File</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>DTD File</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile()
	 * @see #getDTDEnumerationType()
	 * @generated
	 */
	EReference getDTDEnumerationType_DTDFile();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation <em>Notation</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Notation</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation
	 * @generated
	 */
	EClass getDTDNotation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getComment()
	 * @see #getDTDNotation()
	 * @generated
	 */
	EAttribute getDTDNotation_Comment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getSystemID <em>System ID</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>System ID</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getSystemID()
	 * @see #getDTDNotation()
	 * @generated
	 */
	EAttribute getDTDNotation_SystemID();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getPublicID <em>Public ID</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Public ID</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getPublicID()
	 * @see #getDTDNotation()
	 * @generated
	 */
	EAttribute getDTDNotation_PublicID();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDNotation#getEntity()
	 * @see #getDTDNotation()
	 * @generated
	 */
	EReference getDTDNotation_Entity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity
	 * @generated
	 */
	EClass getDTDEntity();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getComment <em>Comment</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Comment</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getComment()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EAttribute getDTDEntity_Comment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#isParameterEntity <em>Parameter Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parameter Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#isParameterEntity()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EAttribute getDTDEntity_ParameterEntity();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getContent()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EReference getDTDEntity_Content();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef <em>Parm Entity Ref</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parm Entity Ref</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getParmEntityRef()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EReference getDTDEntity_ParmEntityRef();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getEntityReference <em>Entity Reference</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Entity Reference</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getEntityReference()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EReference getDTDEntity_EntityReference();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeNameReference <em>Attribute Name Reference</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Attribute Name Reference</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeNameReference()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EReference getDTDEntity_AttributeNameReference();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeTypeReference <em>Attribute Type Reference</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Attribute Type Reference</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getAttributeTypeReference()
	 * @see #getDTDEntity()
	 * @generated
	 */
	EReference getDTDEntity_AttributeTypeReference();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent <em>Entity Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent
	 * @generated
	 */
	EClass getDTDEntityContent();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity <em>DTD Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>DTD Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent#getDTDEntity()
	 * @see #getDTDEntityContent()
	 * @generated
	 */
	EReference getDTDEntityContent_DTDEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity <em>External Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>External Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity
	 * @generated
	 */
	EClass getDTDExternalEntity();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getSystemID <em>System ID</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>System ID</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getSystemID()
	 * @see #getDTDExternalEntity()
	 * @generated
	 */
	EAttribute getDTDExternalEntity_SystemID();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getPublicID <em>Public ID</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Public ID</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getPublicID()
	 * @see #getDTDExternalEntity()
	 * @generated
	 */
	EAttribute getDTDExternalEntity_PublicID();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation <em>Notation</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Notation</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getNotation()
	 * @see #getDTDExternalEntity()
	 * @generated
	 */
	EReference getDTDExternalEntity_Notation();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getEntityReferencedFromAnotherFile <em>Entity Referenced From Another File</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Entity Referenced From Another File</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity#getEntityReferencedFromAnotherFile()
	 * @see #getDTDExternalEntity()
	 * @generated
	 */
	EReference getDTDExternalEntity_EntityReferencedFromAnotherFile();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity <em>Internal Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Internal Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity
	 * @generated
	 */
	EClass getDTDInternalEntity();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity#getValue()
	 * @see #getDTDInternalEntity()
	 * @generated
	 */
	EAttribute getDTDInternalEntity_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference <em>Parameter Entity Reference</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Entity Reference</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference
	 * @generated
	 */
	EClass getDTDParameterEntityReference();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity <em>Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference#getEntity()
	 * @see #getDTDParameterEntityReference()
	 * @generated
	 */
	EReference getDTDParameterEntityReference_Entity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent <em>Entity Reference Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entity Reference Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent
	 * @generated
	 */
	EClass getDTDEntityReferenceContent();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity <em>Element Referenced Entity</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Element Referenced Entity</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity()
	 * @see #getDTDEntityReferenceContent()
	 * @generated
	 */
	EReference getDTDEntityReferenceContent_ElementReferencedEntity();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.dtd.core.internal.emf.DTDContent <em>Content</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for class '<em>Content</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDContent
	 * @generated
	 */
	EClass getDTDContent();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile <em>DTD File</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>DTD File</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDContent#getDTDFile()
	 * @see #getDTDContent()
	 * @generated
	 */
	EReference getDTDContent_DTDFile();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType <em>Occurrence Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Occurrence Type</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType
	 * @generated
	 */
	EEnum getDTDOccurrenceType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind <em>Default Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Default Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind
	 * @generated
	 */
	EEnum getDTDDefaultKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind <em>Basic Type Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Basic Type Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind
	 * @generated
	 */
	EEnum getDTDBasicTypeKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind <em>Enum Group Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Enum Group Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind
	 * @generated
	 */
	EEnum getDTDEnumGroupKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind <em>Group Kind</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Group Kind</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind
	 * @generated
	 */
	EEnum getDTDGroupKind();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType <em>XML Schema Defined Type</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return the meta object for enum '<em>XML Schema Defined Type</em>'.
	 * @see org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType
	 * @generated
	 */
	EEnum getXMLSchemaDefinedType();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DTDFactory getDTDFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementContentImpl <em>Element Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElementContent()
		 * @generated
		 */
		EClass DTD_ELEMENT_CONTENT = eINSTANCE.getDTDElementContent();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ELEMENT_CONTENT__GROUP = eINSTANCE.getDTDElementContent_Group();

		/**
		 * The meta object literal for the '<em><b>Element</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ELEMENT_CONTENT__ELEMENT = eINSTANCE.getDTDElementContent_Element();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDGroupContentImpl <em>Group Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDGroupContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDGroupContent()
		 * @generated
		 */
		EClass DTD_GROUP_CONTENT = eINSTANCE.getDTDGroupContent();

		/**
		 * The meta object literal for the '<em><b>Group Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_GROUP_CONTENT__GROUP_KIND = eINSTANCE.getDTDGroupContent_GroupKind();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_GROUP_CONTENT__CONTENT = eINSTANCE.getDTDGroupContent_Content();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl <em>Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDAttributeImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDAttribute()
		 * @generated
		 */
		EClass DTD_ATTRIBUTE = eINSTANCE.getDTDAttribute();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ATTRIBUTE__COMMENT = eINSTANCE.getDTDAttribute_Comment();

		/**
		 * The meta object literal for the '<em><b>Default Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ATTRIBUTE__DEFAULT_KIND = eINSTANCE.getDTDAttribute_DefaultKind();

		/**
		 * The meta object literal for the '<em><b>Default Value String</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ATTRIBUTE__DEFAULT_VALUE_STRING = eINSTANCE.getDTDAttribute_DefaultValueString();

		/**
		 * The meta object literal for the '<em><b>Attribute Name Referenced Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ATTRIBUTE__ATTRIBUTE_NAME_REFERENCED_ENTITY = eINSTANCE.getDTDAttribute_AttributeNameReferencedEntity();

		/**
		 * The meta object literal for the '<em><b>Attribute Type Referenced Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ATTRIBUTE__ATTRIBUTE_TYPE_REFERENCED_ENTITY = eINSTANCE.getDTDAttribute_AttributeTypeReferencedEntity();

		/**
		 * The meta object literal for the '<em><b>DTD Element</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ATTRIBUTE__DTD_ELEMENT = eINSTANCE.getDTDAttribute_DTDElement();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementImpl <em>Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElement()
		 * @generated
		 */
		EClass DTD_ELEMENT = eINSTANCE.getDTDElement();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ELEMENT__COMMENT = eINSTANCE.getDTDElement_Comment();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ELEMENT__CONTENT = eINSTANCE.getDTDElement_Content();

		/**
		 * The meta object literal for the '<em><b>DTD Attribute</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ELEMENT__DTD_ATTRIBUTE = eINSTANCE.getDTDElement_DTDAttribute();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEmptyContentImpl <em>Empty Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEmptyContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEmptyContent()
		 * @generated
		 */
		EClass DTD_EMPTY_CONTENT = eINSTANCE.getDTDEmptyContent();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDAnyContentImpl <em>Any Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDAnyContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDAnyContent()
		 * @generated
		 */
		EClass DTD_ANY_CONTENT = eINSTANCE.getDTDAnyContent();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDPCDataContentImpl <em>PC Data Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPCDataContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDPCDataContent()
		 * @generated
		 */
		EClass DTD_PC_DATA_CONTENT = eINSTANCE.getDTDPCDataContent();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementReferenceContentImpl <em>Element Reference Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDElementReferenceContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDElementReferenceContent()
		 * @generated
		 */
		EClass DTD_ELEMENT_REFERENCE_CONTENT = eINSTANCE.getDTDElementReferenceContent();

		/**
		 * The meta object literal for the '<em><b>Referenced Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ELEMENT_REFERENCE_CONTENT__REFERENCED_ELEMENT = eINSTANCE.getDTDElementReferenceContent_ReferencedElement();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDRepeatableContentImpl <em>Repeatable Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDRepeatableContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDRepeatableContent()
		 * @generated
		 */
		EClass DTD_REPEATABLE_CONTENT = eINSTANCE.getDTDRepeatableContent();

		/**
		 * The meta object literal for the '<em><b>Occurrence</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_REPEATABLE_CONTENT__OCCURRENCE = eINSTANCE.getDTDRepeatableContent_Occurrence();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl <em>File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDFileImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDFile()
		 * @generated
		 */
		EClass DTD_FILE = eINSTANCE.getDTDFile();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_FILE__COMMENT = eINSTANCE.getDTDFile_Comment();

		/**
		 * The meta object literal for the '<em><b>Parse Error</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_FILE__PARSE_ERROR = eINSTANCE.getDTDFile_ParseError();

		/**
		 * The meta object literal for the '<em><b>DTD Content</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_FILE__DTD_CONTENT = eINSTANCE.getDTDFile_DTDContent();

		/**
		 * The meta object literal for the '<em><b>DTD Enumeration Type</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_FILE__DTD_ENUMERATION_TYPE = eINSTANCE.getDTDFile_DTDEnumerationType();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl <em>Basic Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDBasicTypeImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDBasicType()
		 * @generated
		 */
		EClass DTD_BASIC_TYPE = eINSTANCE.getDTDBasicType();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_BASIC_TYPE__KIND = eINSTANCE.getDTDBasicType_Kind();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEnumerationTypeImpl <em>Enumeration Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEnumerationTypeImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEnumerationType()
		 * @generated
		 */
		EClass DTD_ENUMERATION_TYPE = eINSTANCE.getDTDEnumerationType();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ENUMERATION_TYPE__KIND = eINSTANCE.getDTDEnumerationType_Kind();

		/**
		 * The meta object literal for the '<em><b>DTD File</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENUMERATION_TYPE__DTD_FILE = eINSTANCE.getDTDEnumerationType_DTDFile();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl <em>Notation</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDNotationImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDNotation()
		 * @generated
		 */
		EClass DTD_NOTATION = eINSTANCE.getDTDNotation();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_NOTATION__COMMENT = eINSTANCE.getDTDNotation_Comment();

		/**
		 * The meta object literal for the '<em><b>System ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_NOTATION__SYSTEM_ID = eINSTANCE.getDTDNotation_SystemID();

		/**
		 * The meta object literal for the '<em><b>Public ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_NOTATION__PUBLIC_ID = eINSTANCE.getDTDNotation_PublicID();

		/**
		 * The meta object literal for the '<em><b>Entity</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_NOTATION__ENTITY = eINSTANCE.getDTDNotation_Entity();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl <em>Entity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntity()
		 * @generated
		 */
		EClass DTD_ENTITY = eINSTANCE.getDTDEntity();

		/**
		 * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ENTITY__COMMENT = eINSTANCE.getDTDEntity_Comment();

		/**
		 * The meta object literal for the '<em><b>Parameter Entity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_ENTITY__PARAMETER_ENTITY = eINSTANCE.getDTDEntity_ParameterEntity();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY__CONTENT = eINSTANCE.getDTDEntity_Content();

		/**
		 * The meta object literal for the '<em><b>Parm Entity Ref</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY__PARM_ENTITY_REF = eINSTANCE.getDTDEntity_ParmEntityRef();

		/**
		 * The meta object literal for the '<em><b>Entity Reference</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY__ENTITY_REFERENCE = eINSTANCE.getDTDEntity_EntityReference();

		/**
		 * The meta object literal for the '<em><b>Attribute Name Reference</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY__ATTRIBUTE_NAME_REFERENCE = eINSTANCE.getDTDEntity_AttributeNameReference();

		/**
		 * The meta object literal for the '<em><b>Attribute Type Reference</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY__ATTRIBUTE_TYPE_REFERENCE = eINSTANCE.getDTDEntity_AttributeTypeReference();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityContentImpl <em>Entity Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntityContent()
		 * @generated
		 */
		EClass DTD_ENTITY_CONTENT = eINSTANCE.getDTDEntityContent();

		/**
		 * The meta object literal for the '<em><b>DTD Entity</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY_CONTENT__DTD_ENTITY = eINSTANCE.getDTDEntityContent_DTDEntity();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDExternalEntityImpl <em>External Entity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDExternalEntityImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDExternalEntity()
		 * @generated
		 */
		EClass DTD_EXTERNAL_ENTITY = eINSTANCE.getDTDExternalEntity();

		/**
		 * The meta object literal for the '<em><b>System ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_EXTERNAL_ENTITY__SYSTEM_ID = eINSTANCE.getDTDExternalEntity_SystemID();

		/**
		 * The meta object literal for the '<em><b>Public ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_EXTERNAL_ENTITY__PUBLIC_ID = eINSTANCE.getDTDExternalEntity_PublicID();

		/**
		 * The meta object literal for the '<em><b>Notation</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_EXTERNAL_ENTITY__NOTATION = eINSTANCE.getDTDExternalEntity_Notation();

		/**
		 * The meta object literal for the '<em><b>Entity Referenced From Another File</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_EXTERNAL_ENTITY__ENTITY_REFERENCED_FROM_ANOTHER_FILE = eINSTANCE.getDTDExternalEntity_EntityReferencedFromAnotherFile();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDInternalEntityImpl <em>Internal Entity</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDInternalEntityImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDInternalEntity()
		 * @generated
		 */
		EClass DTD_INTERNAL_ENTITY = eINSTANCE.getDTDInternalEntity();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DTD_INTERNAL_ENTITY__VALUE = eINSTANCE.getDTDInternalEntity_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDParameterEntityReferenceImpl <em>Parameter Entity Reference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDParameterEntityReferenceImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDParameterEntityReference()
		 * @generated
		 */
		EClass DTD_PARAMETER_ENTITY_REFERENCE = eINSTANCE.getDTDParameterEntityReference();

		/**
		 * The meta object literal for the '<em><b>Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_PARAMETER_ENTITY_REFERENCE__ENTITY = eINSTANCE.getDTDParameterEntityReference_Entity();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityReferenceContentImpl <em>Entity Reference Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDEntityReferenceContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEntityReferenceContent()
		 * @generated
		 */
		EClass DTD_ENTITY_REFERENCE_CONTENT = eINSTANCE.getDTDEntityReferenceContent();

		/**
		 * The meta object literal for the '<em><b>Element Referenced Entity</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_ENTITY_REFERENCE_CONTENT__ELEMENT_REFERENCED_ENTITY = eINSTANCE.getDTDEntityReferenceContent_ElementReferencedEntity();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.impl.DTDContentImpl <em>Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDContentImpl
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDContent()
		 * @generated
		 */
		EClass DTD_CONTENT = eINSTANCE.getDTDContent();

		/**
		 * The meta object literal for the '<em><b>DTD File</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DTD_CONTENT__DTD_FILE = eINSTANCE.getDTDContent_DTDFile();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType <em>Occurrence Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDOccurrenceType()
		 * @generated
		 */
		EEnum DTD_OCCURRENCE_TYPE = eINSTANCE.getDTDOccurrenceType();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind <em>Default Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.DTDDefaultKind
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDDefaultKind()
		 * @generated
		 */
		EEnum DTD_DEFAULT_KIND = eINSTANCE.getDTDDefaultKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind <em>Basic Type Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDBasicTypeKind()
		 * @generated
		 */
		EEnum DTD_BASIC_TYPE_KIND = eINSTANCE.getDTDBasicTypeKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind <em>Enum Group Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDEnumGroupKind()
		 * @generated
		 */
		EEnum DTD_ENUM_GROUP_KIND = eINSTANCE.getDTDEnumGroupKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind <em>Group Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupKind
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getDTDGroupKind()
		 * @generated
		 */
		EEnum DTD_GROUP_KIND = eINSTANCE.getDTDGroupKind();

		/**
		 * The meta object literal for the '{@link org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType <em>XML Schema Defined Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.wst.dtd.core.internal.emf.XMLSchemaDefinedType
		 * @see org.eclipse.wst.dtd.core.internal.emf.impl.DTDPackageImpl#getXMLSchemaDefinedType()
		 * @generated
		 */
		EEnum XML_SCHEMA_DEFINED_TYPE = eINSTANCE.getXMLSchemaDefinedType();

	}

} // DTDPackage
