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

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDInitParam;
import org.eclipse.wst.sse.core.utils.StringUtils;


public class TLDInitParamImpl implements TLDInitParam {
	private String description;
	private String name;
	private String value;

	public TLDInitParamImpl() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("\n\t name:" + StringUtils.escape(getName())); //$NON-NLS-1$
		buffer.append("\n\t description:" + StringUtils.escape(getDescription())); //$NON-NLS-1$
		buffer.append("\n\t value:" + StringUtils.escape(getValue())); //$NON-NLS-1$
		return buffer.toString();
	}
}
