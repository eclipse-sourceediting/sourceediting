/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentRegionProcessor;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorMetaData;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;

/**
 * Tests reconciler in an open editor. The location of this class is awkward
 * in that it refers to content types declared in plugins that aren't required
 * to be present.
 */
public class TestSourceValidationFramework extends TestCase {
	/**
	 * THIS SUBCLASS IS FOR TESTING ONLY
	 */
	public static class TestStructuredTextEditor extends StructuredTextEditor {
		public SourceViewerConfiguration textViewerConfiguration = null;

		protected void setSourceViewerConfiguration(SourceViewerConfiguration config) {
			super.setSourceViewerConfiguration(config);
			textViewerConfiguration = config;
		}
	}

	private static final String PROJECT_NAME = "TestSourceValidationFramework";

	private static final String SEPARATOR = String.valueOf(IPath.SEPARATOR);

	private boolean fPreviousReconcilerPref;

	public TestSourceValidationFramework() {
		super("TestSourceValidationFramework");
	}

	private IFile ensureFileIsAccessible(String filePath, byte[] contents) {
		IFile blankFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		if (blankFile != null && !blankFile.isAccessible()) {
			try {
				byte[] bytes = contents;
				if (bytes == null) {
					bytes = new byte[0];
				}
				blankFile.create(new ByteArrayInputStream(bytes), true, new NullProgressMonitor());
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return blankFile;
	}

	private IContentType[] detectContentTypes(String fileName) {
		IContentType[] types = null;
		IFile file = ensureFileIsAccessible(PROJECT_NAME + SEPARATOR + fileName, null);

		types = Platform.getContentTypeManager().findContentTypesFor(file.getName());
		if (types.length == 0) {
			IContentDescription d = null;
			try {
				// optimized description lookup, might not succeed
				d = file.getContentDescription();
				if (d != null) {
					types = new IContentType[]{d.getContentType()};
				}
			}
			catch (CoreException e) {
				/*
				 * should not be possible given the accessible and file type
				 * check above
				 */
			}
		}
		if (types == null) {
			types = Platform.getContentTypeManager().findContentTypesFor(file.getName());
		}
		return types;
	}

	private String[] detectContentTypeIDs(String fileName) {
		IContentType[] types = detectContentTypes(fileName);
		String[] ids = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			ids[i] = types[i].getId();
		}
		return ids;
	}

	private boolean identicalContents(Object o1[], Object o2[]) {
		if (o1.length == 0 && o2.length == 0)
			return true;

		Object[] array1 = new Object[o1.length];
		Object[] array2 = new Object[o2.length];
		System.arraycopy(o1, 0, array1, 0, o1.length);
		System.arraycopy(o2, 0, array2, 0, o2.length);
		Arrays.sort(array1);
		Arrays.sort(array2);
		return Arrays.equals(array1, array2);
	}


	private void ensureProjectIsAccessible(String projName) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(projName);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
		try {
			if (!project.exists())
				project.create(description, new NullProgressMonitor());
			if (!project.isAccessible())
				project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private List getSourceValidatorIDs(String fileName) throws Exception {
		List validatorIds = new ArrayList(1);
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		IFile file = ensureFileIsAccessible(PROJECT_NAME + SEPARATOR + fileName, null);
		IEditorPart editor = IDE.openEditor(page, file, TestStructuredTextEditor.class.getName(), true);

		ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
		TestStructuredTextEditor testTextEditor = (TestStructuredTextEditor) textEditor;
		IReconciler reconciler = testTextEditor.textViewerConfiguration.getReconciler(testTextEditor.getTextViewer());

		assertNotNull(reconciler);
		assertTrue("unexpected IReconciler implementation: " + reconciler.getClass(), reconciler instanceof DocumentRegionProcessor);

		Class reconcilerClass = reconciler.getClass();
		Method method = null;
		while (reconcilerClass != Object.class && method == null) {
			Method[] methods = reconcilerClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().equals("getValidatorStrategy")) {
					method = methods[i];
				}
			}
			reconcilerClass = reconcilerClass.getSuperclass();
		}

		assertNotNull("no getValidatorStrategy method found on " + reconciler.getClass(), method);
		method.setAccessible(true);
		ValidatorStrategy strategy = (ValidatorStrategy) method.invoke(reconciler, new Object[0]);
		assertNotNull(strategy);
		Field fMetaData = strategy.getClass().getDeclaredField("fMetaData");
		assertNotNull("validator metadata field \"fMetaData\" not found on strategy " + strategy.getClass(), fMetaData);
		fMetaData.setAccessible(true);
		List metadata = (List) fMetaData.get(strategy);
		assertNotNull(metadata);
		for (int i = 0; i < metadata.size(); i++) {
			validatorIds.add(((ValidatorMetaData) metadata.get(i)).getValidatorId());
		}

		page.closeEditor(editor, false);

		return validatorIds;
	}

	protected void setUp() throws Exception {
		ensureProjectIsAccessible(PROJECT_NAME);

		// turn on reconciling
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		fPreviousReconcilerPref = store.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		if (!fPreviousReconcilerPref) {
			store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
		}
	}

	protected void tearDown() throws Exception {
		// restore reconciling preference
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, fPreviousReconcilerPref);
	}

	public void testSourceValidationEnablementWithInheritedValidators() throws Exception {
		Object[] xmlValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.xml").toArray();
		assertTrue("No XML source validators found", xmlValidatorIDs.length > 0);
		Object[] xml99ValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.xml99").toArray();
		assertTrue("No XML99 source validators found", xml99ValidatorIDs.length > 0);

		Arrays.sort(xmlValidatorIDs);
		Arrays.sort(xml99ValidatorIDs);

		assertEquals("validator lists should be the same length", xmlValidatorIDs.length, xml99ValidatorIDs.length);
		for (int i = 0; i < xmlValidatorIDs.length; i++) {
			assertEquals("validator IDs should be the same [" + i + "]", xmlValidatorIDs[i], xml99ValidatorIDs[i]);
		}
	}

	public void testSourceValidationEnablementWithUniqueValidators() throws Exception {
		String[] xmlContentTypes = detectContentTypeIDs("testValidatorConfigurations.xml");
		String[] xsdContentTypes = detectContentTypeIDs("testValidatorConfigurations.xsd");

		/*
		 * If the current configuration does not include a distinct XSD
		 * content type, skip the rest
		 */
		if (!identicalContents(xmlContentTypes, xsdContentTypes)) {
			List xmlValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.xml");
			assertTrue("No XML source validators found", !xmlValidatorIDs.isEmpty());
			List xsdValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.xsd");
			assertTrue("No XSD source validators found", !xsdValidatorIDs.isEmpty());
			for (int i = 0; i < xmlValidatorIDs.size(); i++) {
				assertTrue("XML Validator found on XSD input", !xsdValidatorIDs.contains(xmlValidatorIDs.get(i)));
			}
		}
		else {
			String message = "No distinct XSD content type found while running " + getClass().getName();
			System.err.println(message);
			Logger.log(Logger.WARNING, message);
		}
	}

	public void testSourceValidationEnablementWithUnrelatedContentTypes() throws Exception {
		String[] dtdContentTypes = detectContentTypeIDs("testValidatorConfigurations.dtd");
		String[] jspContentTypes = detectContentTypeIDs("testValidatorConfigurations.jsp");
		if (dtdContentTypes.length == 0) {
			String message = "No DTD content type found while running " + getClass().getName();
			System.err.println(message);
			Logger.log(Logger.WARNING, message);
		}
		if (jspContentTypes.length == 0) {
			String message = "No JSP content type found while running " + getClass().getName();
			System.err.println(message);
			Logger.log(Logger.WARNING, message);
		}


		if (dtdContentTypes.length > 0 && jspContentTypes.length > 0 && !identicalContents(dtdContentTypes, jspContentTypes)) {
			List dtdValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.dtd");
			assertTrue("No DTD source validators found", !dtdValidatorIDs.isEmpty());
			List jspValidatorIDs = getSourceValidatorIDs("testValidatorConfigurations.jsp");
			assertTrue("No JSP source validators found", !jspValidatorIDs.isEmpty());
			int dtdValidatorCount = dtdValidatorIDs.size();
			dtdValidatorIDs.removeAll(jspValidatorIDs);
			assertEquals("validators found running on both CSS and DTD", dtdValidatorCount, dtdValidatorIDs.size());
		}
	}
}
