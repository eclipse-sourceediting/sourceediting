/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.validation.core.IValidator;
import org.osgi.framework.Bundle;


/**
 * Object that holds information relevant to the creation of a validator for
 * the reconciling framework.
 * 
 * @author pavery
 */
public class ValidatorMetaData {
	private String fClass = null;
	private IConfigurationElement fConfigurationElement = null;
	private String fId = null;

	// a hash map of content type Ids (String) that points to lists of
	// partition types (List of Strings)
	// contentTypeId -> List(paritionType, paritionType, partitionType, ...)
	// contentTypeId2 -> List(partitionType, partitionType, ...)
	// ...
	private HashMap fMatrix = null;

	public ValidatorMetaData(IConfigurationElement element, String vId, String vClass, String vScope) {
		fId = vId;
		fClass = vClass;
		fConfigurationElement = element;

		fMatrix = new HashMap();
	}

	public void addContentTypeId(String contentTypeId) {
		if (!fMatrix.containsKey(contentTypeId))
			fMatrix.put(contentTypeId, new ArrayList());
	}

	public void addParitionType(String contentTypeId, String partitionType) {
		if (!fMatrix.containsKey(contentTypeId))
			fMatrix.put(contentTypeId, new ArrayList());

		List partitionList = (List) fMatrix.get(contentTypeId);
		partitionList.add(partitionType);
	}

	public boolean canHandleContentType(String contentType) {
		return fMatrix.containsKey(contentType);
	}

	public boolean canHandleParitionType(String contentTypeIds[], String paritionType) {
        for(int i=0; i<contentTypeIds.length; i++) {
    		if (fMatrix.containsKey(contentTypeIds[i])) {
    			List partitions = (List) fMatrix.get(contentTypeIds[i]);
    			for (int j = 0; j < partitions.size(); j++) {
    				if (paritionType.equals(partitions.get(j)))
    					return true;
    			}
    		}
        }
		return false;
	}

	/**
	 * @param element
	 * @param classAttribute
	 * @return Object
	 * @throws CoreException
	 */
	Object createExecutableExtension(final IConfigurationElement element, final String classAttribute) throws CoreException {
		Object obj = null;
		obj = element.createExecutableExtension(classAttribute);
		return obj;
	}

	/**
	 * Creates an extension. If the extension plugin has not been loaded a
	 * busy cursor will be activated during the duration of the load.
	 * 
	 * @param element
	 * @param classAttribute
	 * @return Object
	 * @throws CoreException
	 */
	public Object createExtension() {
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		final IConfigurationElement element = getConfigurationElement();
		final Object[] result = new Object[1];
		String pluginId = element.getDeclaringExtension().getNamespace();
		Bundle bundle = Platform.getBundle(pluginId);
		if (bundle.getState() == Bundle.ACTIVE) {
			try {
				return createExecutableExtension(element, "class"); //$NON-NLS-1$
			} catch (CoreException e) {
				handleCreateExecutableException(result, e);
			}
		} else {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					try {
						result[0] = createExecutableExtension(element, "class"); //$NON-NLS-1$
					} catch (Exception e) {
						handleCreateExecutableException(result, e);
					}
				}
			});
		}
		return result[0];
	}

	/**
	 * @param element
	 * @return Transfer
	 */
	public IValidator createValidator() {
		Object obj = null;
		obj = createExtension();
		if (obj == null)
			return null;
		return (obj instanceof IValidator) ? (IValidator) obj : null;
	}

	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}

	public String getValidatorClass() {
		return fClass;
	}

	public String getValidatorId() {
		return fId;
	}

	/**
	 * @param result
	 * @param e
	 */
	void handleCreateExecutableException(Object[] result, Throwable e) {
		Logger.logException(e);
		e.printStackTrace();
		result[0] = null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer debugString = new StringBuffer("ValidatorMetaData:"); //$NON-NLS-1$
		if (fId != null)
			debugString.append(" [id:" + fId + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		return debugString.toString();
	}
}
