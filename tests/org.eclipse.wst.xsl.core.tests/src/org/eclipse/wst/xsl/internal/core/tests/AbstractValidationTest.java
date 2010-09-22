/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.internal.core.tests;

import java.io.IOException;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationReport;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.tests.XSLCoreTestsPlugin;
import org.junit.Before;
import org.osgi.framework.Bundle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An abstract class to help with the testing. It reads an XSL file, runs the
 * validation on it, and then parses the document looking for comment nodes
 * where the comment starts with ERROR or WARN. For each comment node found, it
 * determines whether the validation found a corresponding error or warning at
 * the given comment node parent's line number. It also checks to make sure that
 * no unexpected errors/warnings are found (ones that don't have comment nodes).
 * 
 * @author Doug Satchwell
 */
public abstract class AbstractValidationTest {
	protected static IProject fTestProject;
	private static boolean fTestProjectInitialized;
	private static final String TEST_PROJECT_NAME = "testproject";

	@Before
	public void setUp() throws Exception {
		if (!fTestProjectInitialized) {
			getAndCreateProject();

			Bundle coreBundle = Platform
					.getBundle(XSLCoreTestsPlugin.PLUGIN_ID);
			Enumeration<String> e = coreBundle.getEntryPaths("/projectfiles");
			while (e.hasMoreElements()) {
				String path = e.nextElement();
				URL url = coreBundle.getEntry(path);
				if (!url.getFile().endsWith("/")) {
					String relativePath = path;
					url = FileLocator.resolve(url);
					path = path.substring("projectfiles".length());
					IFile destFile = fTestProject.getFile(path);
					if (url.toExternalForm().startsWith("jar:file")) {
						InputStream source = FileLocator.openStream(coreBundle,
								new Path(relativePath), false);
						if (destFile.exists()) {
							destFile.delete(true, new NullProgressMonitor());
						}
						destFile.create(source, true, new NullProgressMonitor());
					} else {
						// if resource is not compressed, link
						destFile.createLink(url.toURI(), IResource.REPLACE,
								new NullProgressMonitor());
					}
				}
			}
		}

		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		fTestProjectInitialized = true;
	}

	protected IFile getFile(String path) {
		return fTestProject.getFile(new Path(path));
	}

	/**
	 * Validate the file
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 * @throws XPathExpressionException
	 * @throws IOException
	 */
	protected XSLValidationReport validate(IFile file) throws CoreException,
			XPathExpressionException, IOException {
		XSLValidationReport report = new XSLValidationReport(file
				.getLocationURI().toString());
		XSLValidator.getInstance().validate(file, report, true);
		StylesheetModel model = XSLCore.getInstance().getStylesheet(file);
		assertFalse("Stylesheet model is null", model == null);
		Map<Integer, String> expectedErrors = calculateErrorsAndWarnings(file);
		validateErrors(model, report, expectedErrors);
		return report;
	}

	private void validateErrors(StylesheetModel model,
			XSLValidationReport report, Map<Integer, String> expectedErrors) {
		expectedErrors = new HashMap<Integer, String>(expectedErrors);
		for (ValidationMessage msg : report.getValidationMessages()) {
			XSLValidationMessage error = (XSLValidationMessage) msg;
			assertTrue("Error report must be for the current stylesheet only",
					error.getNode().getStylesheet() == model.getStylesheet());
			String comment = expectedErrors.remove(error.getLineNumber());
			assertNotNull("Unxpected error at line " + error.getLineNumber()
					+ ": " + error, comment);
			assertFalse(
					"Incorrect error level for error at line "
							+ error.getLineNumber() + ": " + error,
					comment.startsWith("ERROR")
							&& msg.getSeverity() != ValidationMessage.SEV_HIGH);
			assertFalse(
					"Incorrect error level for error at line "
							+ error.getLineNumber() + ": " + error,
					comment.startsWith("WARN")
							&& msg.getSeverity() == ValidationMessage.SEV_HIGH);
		}
		for (Map.Entry<Integer, String> entry : expectedErrors.entrySet()) {
			assertTrue("Expected error " + entry.getValue() + " at line "
					+ entry.getKey(), false);
		}
	}

	private Map<Integer, String> calculateErrorsAndWarnings(IFile file)
			throws XPathExpressionException, IOException, CoreException {
		Map<Integer, String> expectedErrors = new HashMap<Integer, String>();
		IStructuredModel smodel = null;
		try {
			smodel = StructuredModelManager.getModelManager().getModelForRead(
					file);
			if (smodel != null && smodel instanceof IDOMModel) {
				IDOMModel model = (IDOMModel) smodel;
				XPathExpression xp = XPathFactory.newInstance().newXPath()
						.compile("//comment()");
				NodeList nodeSet = (NodeList) xp.evaluate(model.getDocument(),
						XPathConstants.NODESET);
				for (int i = 0; i < nodeSet.getLength(); i++) {
					Node commentNode = nodeSet.item(i);
					String comment = commentNode.getNodeValue().trim();
					if (comment.startsWith("ERROR")
							|| comment.startsWith("WARN")) {
						IDOMNode parent = (IDOMNode) commentNode
								.getParentNode();
						int line = model.getStructuredDocument()
								.getLineOfOffset(parent.getStartOffset()) + 1;
						expectedErrors.put(line, comment);
					}
				}
			}
		} finally {
			if (smodel != null)
				smodel.releaseFromRead();
		}
		return expectedErrors;
	}

	private static void getAndCreateProject() throws CoreException {
		IWorkspace workspace = getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);

		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
	}

	private static void createProject(IProject project, IPath locationPath,
			IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc = project.getWorkspace()
						.newProjectDescription(project.getName());
				if (Platform.getLocation().equals(locationPath)) {
					locationPath = null;
				}
				desc.setLocation(locationPath);
				project.create(desc, monitor);
				monitor = null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor = null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
