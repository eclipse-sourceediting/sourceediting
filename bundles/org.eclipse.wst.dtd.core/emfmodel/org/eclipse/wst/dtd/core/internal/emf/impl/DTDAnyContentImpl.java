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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.wst.dtd.core.internal.emf.DTDAnyContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDPackage;



/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Any Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DTDAnyContentImpl extends DTDElementContentImpl implements DTDAnyContent {
	public String getContentName() {
		return "ANY"; //$NON-NLS-1$
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DTDAnyContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DTDPackage.Literals.DTD_ANY_CONTENT;
	}
}
