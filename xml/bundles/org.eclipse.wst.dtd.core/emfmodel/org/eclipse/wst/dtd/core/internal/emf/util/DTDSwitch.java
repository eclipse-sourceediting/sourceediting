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

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDAttribute;
import org.eclipse.wst.dtd.core.internal.emf.DTDBasicType;
import org.eclipse.wst.dtd.core.internal.emf.DTDContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEmptyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType;
import org.eclipse.wst.dtd.core.internal.emf.DTDExternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;
import org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDInternalEntity;
import org.eclipse.wst.dtd.core.internal.emf.DTDNotation;
import org.eclipse.wst.dtd.core.internal.emf.DTDPCDataContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;
import org.eclipse.wst.dtd.core.internal.emf.DTDParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.emf.DTDRepeatableContent;



public class DTDSwitch {
	/**
	 * The cached model package
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected static DTDPackage modelPackage;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public DTDSwitch() {
		if (modelPackage == null) {
			modelPackage = DTDPackage.eINSTANCE;
		}
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch((EClass)eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected Object doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case DTDPackage.DTD_ELEMENT_CONTENT: {
				DTDElementContent dtdElementContent = (DTDElementContent)theEObject;
				Object result = caseDTDElementContent(dtdElementContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_GROUP_CONTENT: {
				DTDGroupContent dtdGroupContent = (DTDGroupContent)theEObject;
				Object result = caseDTDGroupContent(dtdGroupContent);
				if (result == null) result = caseDTDRepeatableContent(dtdGroupContent);
				if (result == null) result = caseDTDElementContent(dtdGroupContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ATTRIBUTE: {
				DTDAttribute dtdAttribute = (DTDAttribute)theEObject;
				Object result = caseDTDAttribute(dtdAttribute);
				if (result == null) result = caseEAttribute(dtdAttribute);
				if (result == null) result = caseEStructuralFeature(dtdAttribute);
				if (result == null) result = caseETypedElement(dtdAttribute);
				if (result == null) result = caseENamedElement(dtdAttribute);
				if (result == null) result = caseEModelElement(dtdAttribute);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ELEMENT: {
				DTDElement dtdElement = (DTDElement)theEObject;
				Object result = caseDTDElement(dtdElement);
				if (result == null) result = caseEClass(dtdElement);
				if (result == null) result = caseDTDContent(dtdElement);
				if (result == null) result = caseEClassifier(dtdElement);
				if (result == null) result = caseENamedElement(dtdElement);
				if (result == null) result = caseEModelElement(dtdElement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_EMPTY_CONTENT: {
				DTDEmptyContent dtdEmptyContent = (DTDEmptyContent)theEObject;
				Object result = caseDTDEmptyContent(dtdEmptyContent);
				if (result == null) result = caseDTDElementContent(dtdEmptyContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ANY_CONTENT: {
				DTDAnyContent dtdAnyContent = (DTDAnyContent)theEObject;
				Object result = caseDTDAnyContent(dtdAnyContent);
				if (result == null) result = caseDTDElementContent(dtdAnyContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_PC_DATA_CONTENT: {
				DTDPCDataContent dtdpcDataContent = (DTDPCDataContent)theEObject;
				Object result = caseDTDPCDataContent(dtdpcDataContent);
				if (result == null) result = caseDTDElementContent(dtdpcDataContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ELEMENT_REFERENCE_CONTENT: {
				DTDElementReferenceContent dtdElementReferenceContent = (DTDElementReferenceContent)theEObject;
				Object result = caseDTDElementReferenceContent(dtdElementReferenceContent);
				if (result == null) result = caseDTDRepeatableContent(dtdElementReferenceContent);
				if (result == null) result = caseDTDElementContent(dtdElementReferenceContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_REPEATABLE_CONTENT: {
				DTDRepeatableContent dtdRepeatableContent = (DTDRepeatableContent)theEObject;
				Object result = caseDTDRepeatableContent(dtdRepeatableContent);
				if (result == null) result = caseDTDElementContent(dtdRepeatableContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_FILE: {
				DTDFile dtdFile = (DTDFile)theEObject;
				Object result = caseDTDFile(dtdFile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_BASIC_TYPE: {
				DTDBasicType dtdBasicType = (DTDBasicType)theEObject;
				Object result = caseDTDBasicType(dtdBasicType);
				if (result == null) result = caseEClass(dtdBasicType);
				if (result == null) result = caseEClassifier(dtdBasicType);
				if (result == null) result = caseENamedElement(dtdBasicType);
				if (result == null) result = caseEModelElement(dtdBasicType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ENUMERATION_TYPE: {
				DTDEnumerationType dtdEnumerationType = (DTDEnumerationType)theEObject;
				Object result = caseDTDEnumerationType(dtdEnumerationType);
				if (result == null) result = caseEEnum(dtdEnumerationType);
				if (result == null) result = caseEDataType(dtdEnumerationType);
				if (result == null) result = caseEClassifier(dtdEnumerationType);
				if (result == null) result = caseENamedElement(dtdEnumerationType);
				if (result == null) result = caseEModelElement(dtdEnumerationType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_NOTATION: {
				DTDNotation dtdNotation = (DTDNotation)theEObject;
				Object result = caseDTDNotation(dtdNotation);
				if (result == null) result = caseDTDContent(dtdNotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ENTITY: {
				DTDEntity dtdEntity = (DTDEntity)theEObject;
				Object result = caseDTDEntity(dtdEntity);
				if (result == null) result = caseDTDContent(dtdEntity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ENTITY_CONTENT: {
				DTDEntityContent dtdEntityContent = (DTDEntityContent)theEObject;
				Object result = caseDTDEntityContent(dtdEntityContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_EXTERNAL_ENTITY: {
				DTDExternalEntity dtdExternalEntity = (DTDExternalEntity)theEObject;
				Object result = caseDTDExternalEntity(dtdExternalEntity);
				if (result == null) result = caseDTDEntityContent(dtdExternalEntity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_INTERNAL_ENTITY: {
				DTDInternalEntity dtdInternalEntity = (DTDInternalEntity)theEObject;
				Object result = caseDTDInternalEntity(dtdInternalEntity);
				if (result == null) result = caseDTDEntityContent(dtdInternalEntity);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_PARAMETER_ENTITY_REFERENCE: {
				DTDParameterEntityReference dtdParameterEntityReference = (DTDParameterEntityReference)theEObject;
				Object result = caseDTDParameterEntityReference(dtdParameterEntityReference);
				if (result == null) result = caseDTDContent(dtdParameterEntityReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_ENTITY_REFERENCE_CONTENT: {
				DTDEntityReferenceContent dtdEntityReferenceContent = (DTDEntityReferenceContent)theEObject;
				Object result = caseDTDEntityReferenceContent(dtdEntityReferenceContent);
				if (result == null) result = caseDTDRepeatableContent(dtdEntityReferenceContent);
				if (result == null) result = caseDTDElementContent(dtdEntityReferenceContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case DTDPackage.DTD_CONTENT: {
				DTDContent dtdContent = (DTDContent)theEObject;
				Object result = caseDTDContent(dtdContent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDFile(DTDFile object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDNotation(DTDNotation object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDContent(DTDContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDEntity(DTDEntity object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDEntityContent(DTDEntityContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDExternalEntity(DTDExternalEntity object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDInternalEntity(DTDInternalEntity object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDParameterEntityReference(DTDParameterEntityReference object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDEntityReferenceContent(DTDEntityReferenceContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDRepeatableContent(DTDRepeatableContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDElementReferenceContent(DTDElementReferenceContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDElement(DTDElement object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDElementContent(DTDElementContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDEmptyContent(DTDEmptyContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDAnyContent(DTDAnyContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDPCDataContent(DTDPCDataContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDGroupContent(DTDGroupContent object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDAttribute(DTDAttribute object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDEnumerationType(DTDEnumerationType object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseDTDBasicType(DTDBasicType object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
	//
	// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
	// // public Object caseENamespace(ENamespace object) {
	// -------------------GENERICRULES.JSED-------------------
	// return null;
	// }
	// -------------------GENERICRULES.JSED-------------------
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEClass(EClass object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEAttribute(EAttribute object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEEnum(EEnum object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEModelElement(EModelElement object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseENamedElement(ENamedElement object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEStructuralFeature(EStructuralFeature object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEDataType(EDataType object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseETypedElement(ETypedElement object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object caseEClassifier(EClassifier object) {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
	//
	// +++++++++++++++++++GENERICRULES.JSED+++++++++++++++++++
	// // public Object caseInternalEClassifier(InternalEClassifier object) {
	// -------------------GENERICRULES.JSED-------------------
	// return null;
	// }
	// -------------------GENERICRULES.JSED-------------------
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Object defaultCase(EObject object) {
		return null;
	}

} // DTDSwitch
