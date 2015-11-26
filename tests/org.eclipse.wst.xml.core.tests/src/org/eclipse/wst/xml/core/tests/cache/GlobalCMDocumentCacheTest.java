/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.core.tests.cache;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.CMDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;
import org.w3c.dom.Document;

public class GlobalCMDocumentCacheTest extends TestCase {
	
	private static final String ZIP_FILE_NAME = "CMDocumentCacheTestProject.zip"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "CMDocumentCacheTestProject"; //$NON-NLS-1$
	
	protected void setUp() throws Exception {
		super.setUp();
		Location platformLocation = Platform.getInstanceLocation();
		ProjectUnzipUtility unzipUtil = new ProjectUnzipUtility();
		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, ZIP_FILE_NAME, ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		unzipUtil.unzipAndImport(zipFile, platformLocation.getURL().getFile());
		unzipUtil.initJavaProject(PROJECT_NAME);
	}

	/*
	 * Test description:
	 *  - Enable global cache.
	 *  - Schema GlobalCMDocumentCacheTestSchema.xsd is contributed to the system catalog.
	 *  - Load documents "GlobalCMDocumentCacheTest1.xml" and "GlobalCMDocumentCacheTest1.xml".
	 *  - Verify that the associated CMDocuments are the same (cached).
	 *  - Load documents "document1.xml" and "document2.xml" (local schema, not in catalog).  
	 *  - Verify that the associated CMDocuments are different (not cached, as the schema is not in system catalog).
	 */
	public void testGlobalCMDocumentCacheEnabled() {
		
		// Ensure the global cache is disabled.
		setGlobalCacheEnabled(true);
		
		// Load "web1.xml" and "web2.xml"
		CMDocument globalCMDocumentCacheTest_1 = getCMDocumentFromXMLFile(PROJECT_NAME + "/GlobalCMDocumentCacheTest1.xml"); //$NON-NLS-1$
		CMDocument globalCMDocumentCacheTest_2 = getCMDocumentFromXMLFile(PROJECT_NAME + "/GlobalCMDocumentCacheTest2.xml"); //$NON-NLS-1$
		
		// Ensure CMDocuments are different.
		assertEquals(globalCMDocumentCacheTest_1, globalCMDocumentCacheTest_2);
		
		// Load "document1.xml" and "document2.xml"
		CMDocument localCMDocument_1 = getCMDocumentFromXMLFile(PROJECT_NAME + "/document1.xml"); //$NON-NLS-1$
		CMDocument localCMDocument_2 = getCMDocumentFromXMLFile(PROJECT_NAME + "/document2.xml"); //$NON-NLS-1$
		
		// Ensure CMDocuments are different.
		assertNotSame(localCMDocument_1, localCMDocument_2);
	}

	/*
	 * Test description:
	 *  - Disable global cache.
	 *  - Schema GlobalCMDocumentCacheTestSchema.xsd is contributed to the system catalog.
	 *  - Load documents "GlobalCMDocumentCacheTest1.xml" and "GlobalCMDocumentCacheTest1.xml".
	 *  - Verify that the associated CMDocuments are different (not cached).
	 *  - Load documents "document1.xml" and "document2.xml".  (local schema, not in catalog).  
	 *  - Verify that the associated CMDocuments are different (not cached, as the schema is not in system catalog).
	 */
	public void testGlobalCMDocumentCacheDisabled() {
		
		// Ensure the global cache is disabled.
		setGlobalCacheEnabled(false);
		
		// Load "web1.xml" and "web2.xml"
		CMDocument globalCMDocumentCacheTest_1 = getCMDocumentFromXMLFile(PROJECT_NAME + "/GlobalCMDocumentCacheTest1.xml"); //$NON-NLS-1$
		CMDocument globalCMDocumentCacheTest_2 = getCMDocumentFromXMLFile(PROJECT_NAME + "/GlobalCMDocumentCacheTest2.xml"); //$NON-NLS-1$
		
		// Ensure CMDocuments are different.
		assertNotSame(globalCMDocumentCacheTest_1, globalCMDocumentCacheTest_2);
		
		// Load "document1.xml" and "document2.xml"
		CMDocument localCMDocument_1 = getCMDocumentFromXMLFile(PROJECT_NAME + "/document1.xml"); //$NON-NLS-1$
		CMDocument localCMDocument_2 = getCMDocumentFromXMLFile(PROJECT_NAME + "/document2.xml"); //$NON-NLS-1$
		
		// Ensure CMDocuments are different.
		assertNotSame(localCMDocument_1, localCMDocument_2);
		
	}

	private CMDocument getCMDocumentFromXMLFile(String documentPath) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(documentPath));
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel structuredModel = null;
		CMDocument cmDocument = null;
	    try {
	    	structuredModel = modelManager.getModelForRead(file);
			Document document = ((IDOMModel) structuredModel).getDocument();
			ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
			CMDocumentManager cmDocumentManager = modelQuery.getCMDocumentManager();
			CMDocumentLoader loader = new CMDocumentLoader(document, cmDocumentManager);
			loader.loadCMDocuments();
			cmDocument = modelQuery.getCorrespondingCMDocument(document.getDocumentElement());
	    } catch (Exception exception) {exception.printStackTrace();}
	    finally {
	    	if (structuredModel != null) {
				structuredModel.releaseFromRead();
	    	}
	    }
	    return cmDocument;
	}
	
	private void setGlobalCacheEnabled(boolean value) {
		Preferences pluginPreferences = XMLCorePlugin.getDefault().getPluginPreferences();
		pluginPreferences.setDefault(XMLCorePreferenceNames.CMDOCUMENT_GLOBAL_CACHE_ENABLED, value);
	}

}