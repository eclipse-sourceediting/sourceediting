/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.validation.tests.internal;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;


/**
 * Tests the "honour all schema locations" feature. 
 */
public class HonourAllSchemaLocationsTest extends BaseTestCase
{
  public void testHonoursAllSchemaLocations()
  {
    try
    {
      configuration.setFeature(XMLValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, true);
    }
    catch (Exception e)
    {
      fail();
    }

    String testFile = getTestFile();
    List keys = new ArrayList();
    int numErrors = 0;
    int numWarnings = 0;

    runTest(testFile, keys, numErrors, numWarnings);
  }

  public void testCanTurnOffHonourAllSchemaLocations()
  {
    try
    {
      configuration.setFeature(XMLValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, false);
    }
    catch (Exception e)
    {
      fail();
    }

    String testFile = getTestFile();
    List keys = new ArrayList();
    keys.add(null);
    keys.add("cvc-complex-type.2.4.d"); //$NON-NLS-1$
    int numErrors = 2;
    int numWarnings = 0;

    runTest(testFile, keys, numErrors, numWarnings);
  }

	public void testCanTurnOffReferencedFileErrors() {
		String qualifier = XMLCorePlugin.getDefault().getBundle().getSymbolicName();
		InstanceScope.INSTANCE.getNode(qualifier).putInt(XMLCorePreferenceNames.INDICATE_REFERENCED_FILE_CONTAINS_ERRORS, 0);
		try {
			try {
				configuration.setFeature(XMLValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, false);
			} catch (Exception e) {
				fail();
			}

			String testFile = getTestFile();
			List keys = new ArrayList();
			keys.add(null);
			keys.add("cvc-complex-type.2.4.d"); //$NON-NLS-1$
			int numErrors = 1;
			int numWarnings = 0;

			runTest(testFile, keys, numErrors, numWarnings);
		} finally {
			InstanceScope.INSTANCE.getNode(qualifier).remove(XMLCorePreferenceNames.INDICATE_REFERENCED_FILE_CONTAINS_ERRORS);
		}
	}

  private String getTestFile()
  {
    return FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + "XMLExamples/HonourAllSchemaLocations/A-instance.xml"; //$NON-NLS-1$
  }
}
