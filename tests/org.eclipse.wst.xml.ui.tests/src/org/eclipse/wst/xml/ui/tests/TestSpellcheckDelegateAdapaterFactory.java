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
package org.eclipse.wst.xml.ui.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.spelling.ISpellcheckDelegate;
import org.eclipse.wst.xml.ui.internal.spelling.SpellcheckDelegateAdapterFactory;

/**
 * Tests for the <code>SpellcheckDelegateAdapaterFactory</code>
 */
public class TestSpellcheckDelegateAdapaterFactory extends TestCase {
	/**
	 * Default constructor
	 */
	public TestSpellcheckDelegateAdapaterFactory() {
		super("Test Spellcheck Delegate AdapaterFactory");
	}
	
	/**
	 * Constructor
	 * 
	 * @param name the name to give to this test run
	 */
	public TestSpellcheckDelegateAdapaterFactory(String name) {
		super(name);
	}
	
	/**
	 * Test the <code>SpellcheckDelegateAdapaterFactory</code>
	 * @throws CoreException 
	 * @throws IOException 
	 */
	public void testSpellcheckDelegateAdapaterFactory() throws IOException, CoreException  {
		String projectName = "TestSpellcheckDelegateAdapaterFactory";
		IProject project = ProjectUtil.createProject(projectName, null, null);
		
		IStructuredModel structuredModel = null;
		
		try {
			//get test file
			ProjectUtil.copyBundleEntriesIntoWorkspace("testresources/spellcheck", projectName);
			IFile testFile = project.getFile("spellcheck_comment_element.xml");
			assertTrue("Test file " + testFile + " does not exist", testFile.exists());
		
			//get delegate
			structuredModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(testFile);
			SpellcheckDelegateAdapterFactory factory = new SpellcheckDelegateAdapterFactory();
			ISpellcheckDelegate delegate = (ISpellcheckDelegate)factory.getAdapter(structuredModel, ISpellcheckDelegate.class);
			assertNotNull("Could not get spellcheck delegate", delegate);
			
			//run tests
			boolean shouldSpellcheckCommentElement = delegate.shouldSpellcheck(54, structuredModel);
			assertFalse("Should not be spellchecking comment elements", shouldSpellcheckCommentElement);
			
			boolean shouldSpellcheckComment = delegate.shouldSpellcheck(80, structuredModel);
			assertTrue("Should be spellchecking comment regions", shouldSpellcheckComment);
		} finally {
			project.delete(true, null);
		}
	}
}