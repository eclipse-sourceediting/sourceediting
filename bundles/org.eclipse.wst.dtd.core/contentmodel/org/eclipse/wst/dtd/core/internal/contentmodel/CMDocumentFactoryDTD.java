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

import org.eclipse.wst.dtd.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.factory.CMDocumentFactory;

/**
 * This builder handles building .dtd grammar files
 */
public class CMDocumentFactoryDTD implements CMDocumentFactory {
	public CMDocumentFactoryDTD() {
	}


	public CMDocument createCMDocument(String uri) {
		// work around a bug in our parsers
		// todo... revisit this
		//
//		https://bugs.eclipse.org/bugs/show_bug.cgi?id=100481
//		String fileProtocol = "file:"; //$NON-NLS-1$
//		if (uri.startsWith(fileProtocol)) {
//			uri = uri.substring(fileProtocol.length());
//		}

		CMDocument result = null;
		try {
			result = DTDImpl.buildCMDocument(uri);
		}
		catch (Exception e) {
			Logger.logException("could not create content model for URI: " + uri, e); //$NON-NLS-1$
		}
		return result;
	}
}
