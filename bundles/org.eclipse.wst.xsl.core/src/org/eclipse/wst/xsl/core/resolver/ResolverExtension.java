/*******************************************************************************
 * Copyright (c) 2008 Jesper Steen Moeller and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jesper Steen Moeller - XSL core plugin
 *******************************************************************************/

package org.eclipse.wst.xsl.core.resolver;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsl.core.Messages;
import org.eclipse.wst.xsl.core.XSLCorePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ResolverExtension implements URIResolverExtension {
	
	private static final Double DEFAULT_XSLT_VERSION = 1.0;

	private static final String SCHEMA_BASE_URI = "platform:/plugin/"  + XSLCorePlugin.PLUGIN_ID + "/xslt-schemas"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String XSLT_1_0_PATH = SCHEMA_BASE_URI + "/xslt-1.0.xsd"; //$NON-NLS-1$
	private static final String XSLT_2_0_PATH = SCHEMA_BASE_URI + "/xslt-2.0.xsd"; //$NON-NLS-1$

	private static final String XSLT_STYLESHEET = "stylesheet"; //$NON-NLS-1$
	private static final String XSLT_TEMPLATE = "template"; //$NON-NLS-1$
	private static final String XSLT_VERSION = "version"; //$NON-NLS-1$
	
	public ResolverExtension() {		
	}

	public String resolve(IFile file, String baseLocation, String publicId,
			String systemId) {

		// Is someone looking for "our" schema?
		if (! XSLCorePlugin.XSLT_NS.equals(publicId)) {
			// Not this time, return right away
			return null;
		}

		String version = null;
		
		version = peekVersionAttributeFromSSE(file);
		if (version == null)
			version = peekVersionFromFile(file, baseLocation);
		
		if (version == null)
			return null;
		
		Double versionNumber = null;
		try {
			versionNumber = Double.valueOf(version);
		} catch (Throwable t) {
			// Not interested
		}
		
		if (versionNumber == null) {
			versionNumber = DEFAULT_XSLT_VERSION;
		}
		
		// We carelessly ditch the fraction part
		int intVersion = versionNumber.intValue();
		if (intVersion == 1) {
			return XSLT_1_0_PATH;
		} else if (intVersion == 2) {
			return XSLT_2_0_PATH;
		}
		else return null;
	}

	private String peekVersionFromFile(IFile file, String baseLocation) {
		XSLVersionHandler handler = new XSLVersionHandler();
		try {
			handler.parseContents(file != null ? createInputSource(file) : createInputSource(baseLocation));
		} catch (SAXException se) {
			XSLCorePlugin.log(se);
			// drop through, since this is almost to be expected
		} catch (IOException ioe) {
			XSLCorePlugin.log(new CoreException(XSLCorePlugin.newErrorStatus("Can't parse XSL document", ioe))); //$NON-NLS-1$
			// drop through, since this is not really a show-stopper
		} catch (ParserConfigurationException pce) {
			// some bad thing happened - force this describer to be disabled
			String message = Messages.XSLCorePlugin_parserConfiguration;
			XSLCorePlugin.log(new Status(IStatus.ERROR, XSLCorePlugin.PLUGIN_ID, 0, message, pce));
			throw new RuntimeException(message);
			// drop through, since this is not really a show-stopper
		} catch (CoreException ce) {
			XSLCorePlugin.log(ce);
			// drop through, since this is not really a show-stopper
		}

		String versionX = handler.getVersionAttribute();
		return versionX;
	}

	private String peekVersionAttributeFromSSE(IFile file) {
		IModelManager manager = StructuredModelManager.getModelManager();

		if (manager != null) {
			String id = manager.calculateId(file);
			IStructuredModel model = manager.getExistingModelForRead(id);
			try {
				if (model instanceof IDOMModel) {
					Document doc = ((IDOMModel)model).getDocument();
					if (doc != null && doc.getDocumentElement() != null) {
						Element documentElement = doc.getDocumentElement();
						if (XSLT_STYLESHEET.equals(documentElement.getLocalName()) ||
							XSLT_TEMPLATE.equals(documentElement.getLocalName())) {
							return documentElement.getAttribute(XSLT_VERSION);
						} else return ""; //$NON-NLS-1$
					}
				}
			} finally {
				model.releaseFromRead();
			}
		}
		return null;
	}

	private InputSource createInputSource(String systemId) throws CoreException {
		return new InputSource(systemId);
	}

	private InputSource createInputSource(IFile file) throws CoreException {
		InputSource src = new InputSource(file.getContents());
		src.setSystemId(file.getLocationURI().toString());
		return src;
	}
}
