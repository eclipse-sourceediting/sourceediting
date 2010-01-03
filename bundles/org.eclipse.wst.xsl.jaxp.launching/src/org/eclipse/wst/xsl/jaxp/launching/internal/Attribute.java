/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xsl.jaxp.launching.IAttribute;

public class Attribute implements IAttribute, Comparable<Object> {
	private final String uri;
	private final String description;
	private final String type;

	public Attribute(String uri, String type, String description) {
		this.uri = uri;
		this.type = type;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getURI() {
		return uri;
	}

	public IStatus validateValue(String value) {
		IStatus status = null;
		if (TYPE_BOOLEAN.equals(type)) {
			boolean valid = "true".equals(value) || "false".equals(value); //$NON-NLS-1$ //$NON-NLS-2$
			if (!valid)
				status = new Status(IStatus.ERROR,
						JAXPLaunchingPlugin.PLUGIN_ID, 0, Messages.Attribute_2,
						null);
		} else if (TYPE_INT.equals(type)) {
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				status = new Status(IStatus.ERROR,
						JAXPLaunchingPlugin.PLUGIN_ID, 0, Messages.Attribute_1,
						null);
			}
		} else if (TYPE_DOUBLE.equals(type)) {
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				status = new Status(IStatus.ERROR,
						JAXPLaunchingPlugin.PLUGIN_ID, 0, Messages.Attribute_0,
						null);
			}
		} else if (TYPE_FLOAT.equals(type)) {
			try {
				Float.parseFloat(value);
			} catch (NumberFormatException e) {
				status = new Status(IStatus.ERROR,
						JAXPLaunchingPlugin.PLUGIN_ID, 0, Messages.Attribute_5,
						null);
			}
		} else if (TYPE_CLASS.equals(type) || TYPE_OBJECT.equals(type)) {
			// status = JavaConventions.validateJavaTypeName(value);
		}
		return status;
	}

	public int compareTo(Object o) {
		if (o instanceof IAttribute) {
			IAttribute f = (IAttribute) o;
			return f.getURI().compareTo(getURI());
		}
		return 0;
	}
}
