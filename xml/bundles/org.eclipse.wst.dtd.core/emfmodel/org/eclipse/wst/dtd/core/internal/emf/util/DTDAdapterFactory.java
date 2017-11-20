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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
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



public class DTDAdapterFactory extends AdapterFactoryImpl {
	/**
	 * @generated This field/method will be replaced during code generation.
	 */

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	protected static DTDPackage modelPackage;

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public DTDAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = DTDPackage.eINSTANCE;
		}
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch the delegates to the <code>createXXX</code> methods. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected DTDSwitch modelSwitch = new DTDSwitch() {
			public Object caseDTDElementContent(DTDElementContent object) {
				return createDTDElementContentAdapter();
			}
			public Object caseDTDGroupContent(DTDGroupContent object) {
				return createDTDGroupContentAdapter();
			}
			public Object caseDTDAttribute(DTDAttribute object) {
				return createDTDAttributeAdapter();
			}
			public Object caseDTDElement(DTDElement object) {
				return createDTDElementAdapter();
			}
			public Object caseDTDEmptyContent(DTDEmptyContent object) {
				return createDTDEmptyContentAdapter();
			}
			public Object caseDTDAnyContent(DTDAnyContent object) {
				return createDTDAnyContentAdapter();
			}
			public Object caseDTDPCDataContent(DTDPCDataContent object) {
				return createDTDPCDataContentAdapter();
			}
			public Object caseDTDElementReferenceContent(DTDElementReferenceContent object) {
				return createDTDElementReferenceContentAdapter();
			}
			public Object caseDTDRepeatableContent(DTDRepeatableContent object) {
				return createDTDRepeatableContentAdapter();
			}
			public Object caseDTDFile(DTDFile object) {
				return createDTDFileAdapter();
			}
			public Object caseDTDBasicType(DTDBasicType object) {
				return createDTDBasicTypeAdapter();
			}
			public Object caseDTDEnumerationType(DTDEnumerationType object) {
				return createDTDEnumerationTypeAdapter();
			}
			public Object caseDTDNotation(DTDNotation object) {
				return createDTDNotationAdapter();
			}
			public Object caseDTDEntity(DTDEntity object) {
				return createDTDEntityAdapter();
			}
			public Object caseDTDEntityContent(DTDEntityContent object) {
				return createDTDEntityContentAdapter();
			}
			public Object caseDTDExternalEntity(DTDExternalEntity object) {
				return createDTDExternalEntityAdapter();
			}
			public Object caseDTDInternalEntity(DTDInternalEntity object) {
				return createDTDInternalEntityAdapter();
			}
			public Object caseDTDParameterEntityReference(DTDParameterEntityReference object) {
				return createDTDParameterEntityReferenceAdapter();
			}
			public Object caseDTDEntityReferenceContent(DTDEntityReferenceContent object) {
				return createDTDEntityReferenceContentAdapter();
			}
			public Object caseDTDContent(DTDContent object) {
				return createDTDContentAdapter();
			}
			public Object caseEModelElement(EModelElement object) {
				return createEModelElementAdapter();
			}
			public Object caseENamedElement(ENamedElement object) {
				return createENamedElementAdapter();
			}
			public Object caseETypedElement(ETypedElement object) {
				return createETypedElementAdapter();
			}
			public Object caseEStructuralFeature(EStructuralFeature object) {
				return createEStructuralFeatureAdapter();
			}
			public Object caseEAttribute(EAttribute object) {
				return createEAttributeAdapter();
			}
			public Object caseEClassifier(EClassifier object) {
				return createEClassifierAdapter();
			}
			public Object caseEClass(EClass object) {
				return createEClassAdapter();
			}
			public Object caseEDataType(EDataType object) {
				return createEDataTypeAdapter();
			}
			public Object caseEEnum(EEnum object) {
				return createEEnumAdapter();
			}
			public Object defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createAdapter(Notifier target) {
		return (Adapter)modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * By default create methods return null so that we can easily ignore
	 * cases. It's useful to ignore a case when inheritance will catch all the
	 * cases anyway.
	 */
	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDFileAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDNotationAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDEntityAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDEntityContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDExternalEntityAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDInternalEntityAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDParameterEntityReferenceAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDEntityReferenceContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDRepeatableContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDElementReferenceContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDElementAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDElementContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDEmptyContentAdapter() {
		return null;
	}

	/**
	 * By default create methods return null so that we can easily ignore
	 * cases. It's useful to ignore a case when inheritance will catch all the
	 * cases anyway.
	 */

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDAnyContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDPCDataContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDGroupContentAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDAttributeAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDEnumerationTypeAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createDTDBasicTypeAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEClassAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEAttributeAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEEnumAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEModelElementAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createENamedElementAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEStructuralFeatureAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEDataTypeAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createETypedElementAdapter() {
		return null;
	}

	/**
	 * @generated This field/method will be replaced during code generation.
	 */
	public Adapter createEClassifierAdapter() {
		return null;
	}

} // DTDAdapterFactory
