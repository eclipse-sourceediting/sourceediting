/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Sep 9, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDValidator;
import org.eclipse.wst.sse.core.utils.StringUtils;


public class TLDValidatorImpl implements TLDValidator {
	protected List initParams;
	protected String validatorClass;

	public List getInitParams() {
		if (initParams == null)
			initParams = new ArrayList();
		return initParams;
	}

	public String getValidatorClass() {
		return validatorClass;
	}

	public void setInitParams(List initParams) {
		this.initParams = initParams;
	}

	public void setValidatorClass(String validatorClass) {
		this.validatorClass = validatorClass;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("\n\t validator class:" + StringUtils.escape(getValidatorClass())); //$NON-NLS-1$
		buffer.append("\n\t init-parms:"); //$NON-NLS-1$
		for (int i = 0; i < getInitParams().size(); i++) {
			buffer.append("\n" + StringUtils.replace(getInitParams().get(i).toString(), "\n", "\n\t\t")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return buffer.toString();
	}
}
