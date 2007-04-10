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

package org.eclipse.wst.dtd.core.internal.emf.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.dtd.core.internal.emf.DTDFile;


/**
 * Load another DTD model - use when there is external entity references from
 * another model
 */
public class ExternalDTDModel {
	DTDFile dtdFile = null;

	public ExternalDTDModel() {
	}

	public DTDFile getExternalDTDFile() {
		return dtdFile;
	}

	/**
	 * Load the DTD model specified by the uri e.g.
	 * file:C:/eclipse/examples/purchaseorder.dtd
	 */
	public boolean loadModel(ResourceSet resources, String uri) {
		boolean rc = true;

		if (uri == null || uri.equals("")) { //$NON-NLS-1$
			return false;
		}

		try {
			// todo... Investigate why we can not create a new DTDUtil to load
			// the external
			// without breaking the bvt test bucket on the oagis cases.
			// Are depending on mof's built in caching via
			// resources.load(uri)?
			//
			URI uriObj = URI.createURI(uri);
			Resource r = resources.createResource(uriObj);
			Map options = new HashMap();
			r.load(options);

			for (Iterator i = resources.getResources().iterator(); i.hasNext();) {
				DTDFile currentFile = (DTDFile) ((Resource) i.next()).getContents().get(0);
				String currentURI = currentFile.eResource().getURI().toString();
				// note : mof stores a slightly different uri than the one we
				// passed in to resources.load(uri)
				// In the 'file:' protocol case, the call
				// resource.load("file:D:\x.dtd") will store a resource with
				// uri "D:\x.dtd".
				// This is why we use 'endswith()' instead of 'equals()' to
				// compare.
				if (uri.endsWith(currentURI)) {
					dtdFile = currentFile;
					break;
				}
			}
			if (dtdFile == null) {
				rc = false;
			}
		}
		catch (Exception ex) {
			rc = false;
		}
		return rc;
	}
}
