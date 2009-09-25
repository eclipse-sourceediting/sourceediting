/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.exslt.core.internal.resolver;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.xsl.exslt.core.internal.EXSLTCore;
import org.osgi.framework.Bundle;

public class EXSLTResolverExtension implements URIResolverExtension {
	Bundle bundle = null;
	
	public EXSLTResolverExtension() {
		bundle = EXSLTCore.getDefault().getBundle();
	}

	public String resolve(IFile file, String baseLocation, String publicId, String systemId)
	{
		if (EXSLTCore.EXSLT_COMMON_NAMESPACE.equals(publicId)) {
			return getURLPath("/schemas/common.xsd");
		}
		
		return null;
	}
	
	private String getURLPath(String grammarPath) {
		URL pluginURL = bundle.getEntry(grammarPath);
		return pluginURL.toExternalForm();
	}
}
