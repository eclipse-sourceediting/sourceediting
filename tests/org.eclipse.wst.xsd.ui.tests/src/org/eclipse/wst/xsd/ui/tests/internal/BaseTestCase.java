/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.tests.internal;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.tests.Activator;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

public class BaseTestCase extends TestCase
{
  protected static final String PLUGIN_ABSOLUTE_PATH = Activator.getInstallURL();
  protected static final String RESOURCES_FOLDER = "testresources"; //$NON-NLS-1$
  protected static final String TC_ROOT_FOLDER = PLUGIN_ABSOLUTE_PATH + "/" + RESOURCES_FOLDER + "/XSD"; //$NON-NLS-1$
  protected XSDDirectivesManager importManager = new XSDDirectivesManager();

  public BaseTestCase()
  {
  }

  public BaseTestCase(String name)
  {
    super(name);
  }

  protected XSDSchema getXSDSchema(String path)
  {
    URI uri = URI.createFileURI(path);
    ResourceSet resourceSet = new ResourceSetImpl();
    XSDResourceImpl resource = (XSDResourceImpl) resourceSet.getResource(uri, true);
    XSDSchema schema = resource.getSchema();
    assertNotNull(schema);
    return schema;
  }
}
