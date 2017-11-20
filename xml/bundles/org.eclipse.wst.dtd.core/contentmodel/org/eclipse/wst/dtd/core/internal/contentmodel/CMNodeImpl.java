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
package org.eclipse.wst.dtd.core.internal.contentmodel;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public abstract class CMNodeImpl extends AdapterImpl implements CMNode {
	protected static final String PROPERTY_DOCUMENTATION = "documentation"; //$NON-NLS-1$
	protected static final String PROPERTY_DOCUMENTATION_SOURCE = "documentationSource"; //$NON-NLS-1$
	protected static final String PROPERTY_DOCUMENTATION_LANGUAGE = "documentationLanguage"; //$NON-NLS-1$
	protected static final String PROPERTY_MOF_NOTIFIER = "key"; //$NON-NLS-1$
	protected static final String PROPERTY_DEFINITION_INFO = "http://org.eclipse.wst/cm/properties/definitionInfo"; //$NON-NLS-1$
	protected static final String PROPERTY_DEFINITION = "http://org.eclipse.wst/cm/properties/definition"; //$NON-NLS-1$

	public abstract Object getKey();

	public boolean supports(String propertyName) {
		return propertyName.equals(PROPERTY_MOF_NOTIFIER);
	}

	public Object getProperty(String propertyName) {
		return null;
	}

	public void setProperty(String propertyName, Object object) {
	}
}
