/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.openon;

import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.util.PathHelper;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.xml.ui.openon.DefaultOpenOnXML;

/**
 * This action class retrieves the link/file selected by the cursor and
 * attempts to open the link/file in the default editor or web browser
 */
public class DefaultOpenOnHTML extends DefaultOpenOnXML {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.xml.openon.DefaultOpenOnXML#resolveURI(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	protected String resolveURI(String baseLocation, String publicId, String systemId) {
		// currently using model's URI resolver until a better resolver is
		// extended to common extensible URI resolver
		// future_TODO: should use the new common extensible URI resolver when
		// clients start implementing it
		String resolvedURI = systemId;

		if (systemId != null) {
			IStructuredModel sModel = getModelManager().getExistingModelForRead(getDocument());
			try {
				if (sModel != null) {
					URIResolver resolver = sModel.getResolver();
					resolvedURI = resolver != null ? resolver.getLocationByURI(systemId, true) : systemId;
				}
			}
			catch (Exception e) {
				Logger.logException(e);
			}
			finally {
				if (sModel != null)
					sModel.releaseFromRead();
			}

			// special adjustment for file protocol
			if (systemId.startsWith(FILE_PROTOCOL)) {
				PathHelper.removeLeadingSeparator(resolvedURI);
			}
		}
		return resolvedURI;
	}
}