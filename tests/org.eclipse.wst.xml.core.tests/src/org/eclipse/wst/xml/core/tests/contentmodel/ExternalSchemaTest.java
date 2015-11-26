/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.IExternalSchemaLocationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ExternalSchemaLocationProviderRegistry;

public class ExternalSchemaTest extends TestCase {

	private static final String EXTERNAL_SCHEMA_LOCATION = "file://external/schema/foo.xsd";

	public static class ExternalSchemaLocationProvider implements org.eclipse.wst.xml.core.contentmodel.modelquery.IExternalSchemaLocationProvider {
		private static URI INTERESTED_RESOURCE = null;
		public ExternalSchemaLocationProvider() {
			
			try {
				INTERESTED_RESOURCE = new URI("file://interested/path/file.xml");
			} catch (Exception e) {}
		}

		public Map getExternalSchemaLocation(URI fileURI) {
			Map map = new HashMap(1);
			if (fileURI != null && fileURI.equals(INTERESTED_RESOURCE)) {
				map.put(NO_NAMESPACE_SCHEMA_LOCATION, EXTERNAL_SCHEMA_LOCATION);
			}
			return map;
		}
	}

	public void testExternalLocations() throws Exception {
		final IExternalSchemaLocationProvider[] providers = ExternalSchemaLocationProviderRegistry.getInstance().getProviders();
		assertTrue(providers.length > 0);
		URI resource = new URI("file://uninterested/path/file.xml");
		for (int i = 0; i < providers.length; i++) {
			Map locations =  providers[i].getExternalSchemaLocation(resource);
			assertTrue("[" + resource + "] should have no external schemas.", locations == null || locations.isEmpty());
		}

		resource = new URI("file://interested/path/file.xml");
		boolean foundCorrectSchema = false;
		for (int i = 0; i < providers.length; i++) {
			Map locations =  providers[i].getExternalSchemaLocation(resource);
			if (locations != null) {
				String location = (String) locations.get(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION);
				if (EXTERNAL_SCHEMA_LOCATION.equals(location)) {
					foundCorrectSchema = true;
				}
			}
			assertTrue("[" + resource + "] should have an external schema.", foundCorrectSchema);
		}
	}

}