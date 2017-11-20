/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;

public class DTDValidationTest extends TestCase {

	public void testBug378613() throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI uri = URI.createPlatformPluginURI("/org.eclipse.wst.dtd.core.tests/resources/invoice.dtd", true);
		Resource resource = resourceSet.createResource(uri);

		resource.load(null);

		EObject eObject = (EObject) resource.getContents().get(0);

		Diagnostician.INSTANCE.validate(eObject);
		// Validation should produce a valid Ecore model
	}
}
