/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.project.facet;

import java.util.Set;

import org.eclipse.core.internal.resources.ResourceStatus;
import org.eclipse.core.internal.utils.Messages;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonMessages;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;
import org.eclipse.wst.web.internal.ResourceHandler;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.util.StringTokenizer;

public class SimpleWebFacetInstallDataModelProvider extends FacetInstallDataModelProvider implements ISimpleWebFacetInstallDataModelProperties {

	public SimpleWebFacetInstallDataModelProvider() {
		super();
	}

	@Override
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(CONTENT_DIR);
		names.add(CONTEXT_ROOT);
		return names;
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(CONTENT_DIR)) {
			return "WebContent"; //$NON-NLS-1$
		} else if (propertyName.equals(CONTEXT_ROOT)) {
			return getStringProperty(FACET_PROJECT_NAME).replace(' ', '_');
		} else if (propertyName.equals(FACET_ID)) {
			return IModuleConstants.WST_WEB_MODULE;
		}
		return super.getDefaultProperty(propertyName);
	}
	
	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			model.notifyPropertyChange(CONTEXT_ROOT, IDataModel.VALID_VALUES_CHG);
		}
		return super.propertySet(propertyName, propertyValue);
	}
	
	@Override
	public IStatus validate(String name) {
		if (name.equals(CONTEXT_ROOT)) {
			return validateContextRoot(getStringProperty(CONTEXT_ROOT));
		} 
		else if (name.equals(CONTENT_DIR)) {
			String folderName = model.getStringProperty(CONTENT_DIR);
			if (folderName == null || folderName.length() == 0 || folderName.equals("/") || folderName.equals("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
				// all folders which meet the criteria of "CONFIG_FOLDER" are required
				String errorMessage = WTPCommonPlugin.getResourceString(WTPCommonMessages.WEBCONTENTFOLDER_EMPTY);
				return WTPCommonPlugin.createErrorStatus(errorMessage);
			}
			IStatus status = validateFolderName(folderName);
			if (status.isOK())
			{
				if (folderName.indexOf('#') != -1) { 
					String message = NLS.bind(Messages.resources_invalidCharInName, "#", folderName); //$NON-NLS-1$
					status = new ResourceStatus(IResourceStatus.INVALID_VALUE, null, message);
				}
			}
			return status;
		}
		
		return super.validate(name);
	}
	
	protected IStatus validateContextRoot(String contextRoot) {
		if (contextRoot == null || contextRoot.length() == 0) {
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, ResourceHandler.Context_Root_cannot_be_empty_2);
		} else if (contextRoot.trim().equals(contextRoot)) {
			StringTokenizer stok = new StringTokenizer(contextRoot, "."); //$NON-NLS-1$
			while (stok.hasMoreTokens()) {
				String token = stok.nextToken();
				int cp;
		        for (int i = 0; i < token.length(); i += UTF16.getCharCount(cp)) {
		            cp = UTF16.charAt(token, i);
					if(token.charAt(i) == ' ')
					{
						return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, ResourceHandler.Names_cannot_contain_whitespace);
					}
					else if (!(token.charAt(i) == '_') && !(token.charAt(i) == '-') && !(token.charAt(i) == '/') && Character.isLetterOrDigit(token.charAt(i)) == false) {
						String invalidCharString = null;
						if (UTF16.getCharCount(cp)>1)
						{
							invalidCharString = UTF16.valueOf(cp); 
						}
						else
						{
							invalidCharString = (new Character(token.charAt(i))).toString();
						}
						Object[] invalidChar = new Object[]{invalidCharString};
						String errorStatus = ResourceHandler.getString(ResourceHandler.The_character_is_invalid_in_a_context_root, invalidChar); 
						return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, errorStatus);
					}
				}
			}
		} else
		{
			return new ResourceStatus(IResourceStatus.INVALID_VALUE, null, ResourceHandler.Names_cannot_contain_whitespace);
		}
		return OK_STATUS;
	}
	
		protected IStatus validateFolderName(String folderName) {
		// the directory is not required, but if the name is entered ensure that it 
		// contains only valid characters.
		if (folderName == null || folderName.length() == 0) {
			return OK_STATUS;
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = new Path(folderName);
		for (int i = 0, max = path.segmentCount(); i < max; i++) {
			IStatus status = workspace.validateName(path.segment(i), IResource.FOLDER);
			if (! status.isOK())
				return status;
		}

		// all of the potential segments of the folder have been verified
		return OK_STATUS;
	}
}
