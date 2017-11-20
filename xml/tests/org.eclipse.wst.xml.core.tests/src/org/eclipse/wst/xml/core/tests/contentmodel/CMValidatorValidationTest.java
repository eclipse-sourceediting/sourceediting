/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAction;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.tests.util.FileUtil;
import org.eclipse.wst.xml.core.tests.util.ProjectUnzipUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CMValidatorValidationTest extends TestCase {
	
	private final String PROJECT_NAME ="CMValidatorTestProject";
	private final String ZIP_FILE_NAME = "CMValidatorTestProject.zip";
	
	protected void setUp() throws Exception {
		Location platformLocation = Platform.getInstanceLocation();
		ProjectUnzipUtility unzipUtil = new ProjectUnzipUtility();
		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, ZIP_FILE_NAME, ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		unzipUtil.unzipAndImport(zipFile, platformLocation.getURL().getFile());
		unzipUtil.initJavaProject(PROJECT_NAME);
	}

	/*
	 * This test makes use of the following XML schema structure:
	 * 
	 * root (element)
	 *   |
	 *   +- sequence
	 *         |
	 *         +- a (element)
	 *         |  |
	 *         |  +- sequence (0 to n)
	 *         |        |
	 *         |        +- a1 (element)
	 *         |
	 *         +- b (element)
	 *         |  |
	 *         |  +- sequence
	 *         |        |
	 *         |        +- b1 (element with occurrences 0 to 2)
	 *         |
	 *         +- choice
	 *               |
	 *               +- c (element)
	 *               |
	 *               +- d (element)
	 *
	 * Test document contains only the root element.
	 * At the root element level, element a is initially available.
	 * Adding a makes b available. Adding b makes c or d available.
	 * Adding either c or d makes no nodes available at the root.
	 * Any arbitrary number of elements a1 can be added to element a.
	 * Only 2 elements b2 may be added to element b. 
	 */
	
	public void testGetInsertActions() {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(PROJECT_NAME + "/CMValidatorTestSchema.xml"));
		IModelManager modelManager = StructuredModelManager.getModelManager();
	    try {
			IStructuredModel model = modelManager.getModelForRead(file);
			Document document = ((IDOMModel) model).getDocument();
			Element documentElement = document.getDocumentElement();
			
			// root element has no children, element a should be available
			verify(documentElement, new String[]{"a"});

			// add element a
			Element aElement = document.createElement("a");
			documentElement.appendChild(aElement);
			
			// root element contains a; b should be available
			verify(documentElement, new String[]{"b"});
			
			// add element b
			Element bElement = document.createElement("b");
			documentElement.appendChild(bElement);
			
			// root element contains a and b; c and d should be available
			verify(documentElement, new String[]{"d", "c"});
			
			// add element c
			Element cElement = document.createElement("c");
			documentElement.appendChild(cElement);
			
			// root element contains a, b and c; no more elements should be available
			verify(documentElement, new String[]{});
			
			// remove c element
			documentElement.removeChild(cElement);
			
			// root element contains a and b; c and d should be available
			verify(documentElement, new String[]{"d", "c"});
			
			// add element d
			Element dElement = document.createElement("d");
			documentElement.appendChild(dElement);
			
			// root element contains a, b and d; no more elements should be available
			verify(documentElement, new String[]{});
			
			// element a contains no children; element a1 should be available
			verify(aElement, new String[]{"a1"});
			
			// add element a1 to a
			aElement.appendChild(document.createElement("a1"));
			
			// element a1 is in an unbounded sequence, it should still be available
			verify(aElement, new String[]{"a1"});
			
			// element b contains no children; element b1 should be available
			verify(bElement, new String[]{"b1"});
			
			// add element b1 to b
			bElement.appendChild(document.createElement("b1"));
			
			// element b contains 1 b1 child; another instance of b1 should be available
			verify(bElement, new String[]{"b1"});

			// add another element b1 to b
			bElement.appendChild(document.createElement("b1"));
			
			// b1 can only appear twice, no elements should be available
			verify(bElement, new String[]{});
			

		} catch (Exception exception) {exception.printStackTrace();}
	}
	
	
	private void verify(Element parentElement, String[] childElementsAvailable) {
		List list = new ArrayList();
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(parentElement.getOwnerDocument());
		CMElementDeclaration cmElementDeclaration = modelQuery.getCMElementDeclaration(parentElement);
		modelQuery.getInsertActions(parentElement, cmElementDeclaration, -1, ModelQuery.INCLUDE_CHILD_NODES, ModelQuery.VALIDITY_STRICT, list);
		assertEquals(childElementsAvailable.length, list.size());
		List availableNodeNameList = new ArrayList();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()) {
			availableNodeNameList.add(((ModelQueryAction)iterator.next()).getCMNode().getNodeName());
		}
		for (int i = 0; i < childElementsAvailable.length; i++) {
			if(availableNodeNameList.indexOf(childElementsAvailable[i]) == -1) {
				fail();
			}
		}
	}
	
}
