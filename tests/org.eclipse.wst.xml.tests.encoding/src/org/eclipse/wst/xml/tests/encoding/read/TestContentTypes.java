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
package org.eclipse.wst.xml.tests.encoding.read;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;


public class TestContentTypes extends TestCase {
	private static final boolean DEBUG = false;

	public void testCreation() {
		IContentTypeManager registry = Platform.getContentTypeManager();
		assertTrue("content type identifer registry must exist", registry != null);
		IContentType[] allTypes = registry.getAllContentTypes();
		for (int i = 0; i < allTypes.length; i++) {
			IContentType contentType = allTypes[i];
			IContentType parentType = contentType.getBaseType();
			if (DEBUG) {
				System.out.print(contentType);

				if (parentType != null) {
					System.out.println(" (extends " + parentType + ")");
				}
				else {
					System.out.println();
				}
				System.out.println("   " + contentType.getName());
			}
			String[] filespecs = contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC | IContentType.FILE_NAME_SPEC);
			if (DEBUG) {
				for (int j = 0; j < filespecs.length; j++) {
					String filespec = filespecs[j];
					System.out.println("        " + filespec);
				}
			}
		}
	}
}