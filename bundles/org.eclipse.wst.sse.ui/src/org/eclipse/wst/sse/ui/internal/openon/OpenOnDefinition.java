/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.openon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.extensions.openon.IOpenOn;


/**
 * Open on definition object
 * @author amywu
 */
public class OpenOnDefinition {
	private String fId = null;
	private String fClassName = null;

	// a hash map of content type Ids (String) that points to lists of parition types (List of Strings)
	// contentTypeId -> List(paritionType, paritionType, partitionType, ...)
	// contentTypeId2 -> List(partitionType, partitionType, ...)
	// ...
	private HashMap fContentTypes = null;

	private IConfigurationElement fConfigurationElement = null;

	/**
	 * @param id
	 * @param class1
	 * @param configurationElement
	 */
	public OpenOnDefinition(String id, String class1, IConfigurationElement configurationElement) {
		super();
		fId = id;
		fClassName = class1;
		fConfigurationElement = configurationElement;
		fContentTypes = new HashMap();
	}

	public void addContentTypeId(String contentTypeId) {
		if (!fContentTypes.containsKey(contentTypeId))
			fContentTypes.put(contentTypeId, new ArrayList());
	}

	public void addPartitionType(String contentTypeId, String partitionType) {
		if (!fContentTypes.containsKey(contentTypeId))
			fContentTypes.put(contentTypeId, new ArrayList());

		List partitionList = (List) fContentTypes.get(contentTypeId);
		partitionList.add(partitionType);
	}

	/**
	 * @return Returns the fClass.
	 */
	public String getClassName() {
		return fClassName;
	}

	/**
	 * @return Returns the fConfigurationElement.
	 */
	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}

	/**
	 * @return Returns the fContentTypes.
	 */
	public HashMap getContentTypes() {
		return fContentTypes;
	}

	/**
	 * @return Returns the fId.
	 */
	public String getId() {
		return fId;
	}

	/**
	 * @return IOpenOn for this definition
	 */
	public IOpenOn createOpenOn() {
		IOpenOn openOn = null;

		if (getClassName() != null) {
			openOn = (IOpenOn) createExtension(OpenOnBuilder.ATT_CLASS);
		}

		return openOn;
	}

	/**
	 * Creates an extension.  If the extension plugin has not
	 * been loaded a busy cursor will be activated during the duration of
	 * the load.
	 * @param propertyName
	 * @return Object
	 */
	private Object createExtension(String propertyName) {
		// If plugin has been loaded create extension.
		// Otherwise, show busy cursor then create extension.
		final IConfigurationElement element = getConfigurationElement();
		final String name = propertyName;

		final Object[] result = new Object[1];
		IPluginDescriptor plugin = element.getDeclaringExtension().getDeclaringPluginDescriptor();
		if (plugin.isPluginActivated()) {
			try {
				return element.createExecutableExtension(name);
			}
			catch (CoreException e) {
				handleCreateExecutableException(result, e);
			}
		}
		else {
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					try {
						result[0] = element.createExecutableExtension(name);
					}
					catch (Throwable e) {
						handleCreateExecutableException(result, e);
					}
				}
			});
		}
		return result[0];
	}

	/**
	 * @param result
	 * @param e
	 */
	private void handleCreateExecutableException(Object[] result, Throwable e) {
		Logger.logException("Unable to create open on: " + getId(), e); //$NON-NLS-1$
		e.printStackTrace();
		result[0] = null;
	}
}
