/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.ui.internal.contentassist.BeanInfoProvider;
import org.eclipse.jst.jsp.ui.internal.contentassist.IJavaPropertyDescriptor;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.dialogs.IOverwriteQuery;

/**
 * This class tests the BeanInfoProvider. Currently test workspace MUST point
 * to the specific one for this test.
 * 
 * @author pavery
 */
public class BeanInfoProviderTest extends TestCase {
	class OverwriteNone implements IOverwriteQuery {
		public String queryOverwrite(String pathString) {
			return IOverwriteQuery.ALL;
		}
	}

	private IResource fResource;
	private BeanInfoProvider fProvider;
	private HashSet fAlreadyOpen = new HashSet();
	private ProjectUnzipUtility fProjUtil = null;
	private boolean fIsSetup = false;

	public BeanInfoProviderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		if (!this.fIsSetup) {
			initializeResource();
			this.fIsSetup = true;
		}
	}

	private void initializeResource() throws Exception {
		fProjUtil = new ProjectUnzipUtility();
		// root of workspace directory
		Location platformLocation = Platform.getInstanceLocation();
		// platform location may be null -- depends on "mode" of platform
		if (platformLocation != null) {
			File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, "beaninfo_tests.zip", ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
			fProjUtil.unzipAndImport(zipFile, platformLocation.getURL().getPath());
			fProjUtil.initJavaProject("BEANINFO");
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			String workspaceRelativeLocation = "/BEANINFO/BEAN_TESTS/beanInfo_test.jsp";
			fResource = root.getFile(new Path(workspaceRelativeLocation));
			IJavaProject javaProj = JavaCore.create(fResource.getProject());
			openPath(javaProj);
		}
	}

	protected File getSourceDirectory(String absoluteSourceDirectoryPath) {
		File sourceDirectory = new File(absoluteSourceDirectoryPath);
		if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
			return null;
		}
		return sourceDirectory;
	}

	private void openPath(IJavaProject javaProj) {
		try {
			if (javaProj.exists() && !javaProj.isOpen()) {
				javaProj.open(null);
			}
			IPackageFragmentRoot root = javaProj.getPackageFragmentRoot(fResource.getProject());
			if (!root.isOpen())
				root.open(null);
			IPackageFragment frag = getPackageFragment(root, "BEAN_TESTS");
			openAll(frag);
			frag = getPackageFragment(root, "org");
			if (frag != null && !frag.isOpen())
				openAll(frag);
			frag = getPackageFragment(root, "org.eclipse");
			if (frag != null && !frag.isOpen())
				openAll(frag);
			frag = getPackageFragment(root, "org.eclipse.jst");
			if (frag != null && !frag.isOpen())
				openAll(frag);
			frag = getPackageFragment(root, "org.eclipse.jst.jsp");
			if (frag != null && !frag.isOpen())
				openAll(frag);
			frag = getPackageFragment(root, "org.eclipse.jst.jsp.ui");
			if (frag != null && !frag.isOpen())
				openAll(frag);
			frag = getPackageFragment(root, "org.eclipse.jst.jsp.ui.tests");
			if (frag != null && !frag.isOpen())
				openAll(frag);
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private IPackageFragment getPackageFragment(IPackageFragmentRoot root, String fragmentName) throws JavaModelException {
		IPackageFragment frag = null;
		IJavaElement[] children = root.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getElementName().equals(fragmentName)) {
				frag = (IPackageFragment) children[i];
				break;
			}
		}
		return frag;
	}

	private void openAll(IJavaElement javaElem) throws JavaModelException {
		if (javaElem instanceof IOpenable) {
			if (!((IOpenable) javaElem).isOpen())
				((IOpenable) javaElem).open(null);
		}
		if (javaElem instanceof IParent && ((IParent) javaElem).hasChildren()) {
			IJavaElement[] children = ((IParent) javaElem).getChildren();
			for (int i = 0; i < children.length; i++) {
				if (!fAlreadyOpen.contains(children[i].getElementName())) {
					fAlreadyOpen.add(children[i].getElementName());
					openAll(children[i]);
				}
			}
		}
	}

	public void testAll() {
		beanOnClasspath();
		beanInProject();
	}

	private void beanOnClasspath() {
		// pa_TODO don't use JButton, properties change
		// use something more static
		IJavaPropertyDescriptor[] descriptors = getProvider().getRuntimeProperties(fResource, "javax.swing.JButton");
		//		assertEquals("Number of properties for JButton:", 122, descriptors.length);
		assertNotNull("descriptors shouldn't be null", descriptors);

		//		List getOnly = new ArrayList();
		//		List setOnly = new ArrayList();
		//		List both = new ArrayList();
		//		IJavaPropertyDescriptor jpd = null;
		//		for (int i = 0; i < descriptors.length; i++) {
		//			jpd = descriptors[i];
		//			if (jpd.getReadable() && jpd.getWriteable())
		//				both.add(jpd);
		//			else if (jpd.getReadable() && !jpd.getWriteable())
		//				getOnly.add(jpd);
		//			else
		//				setOnly.add(jpd);
		//		}
		//		assertEquals("Number of getOnly properties:", 62, getOnly.size());
		//assertEquals("Number of setOnly properties:", 0, setOnly.size());
		//assertEquals("Number of get & set properties that:", 59,
		// both.size());
	}

	private void beanInProject() {
		IJavaPropertyDescriptor[] descriptors = getProvider().getRuntimeProperties(fResource, "org.eclipse.jst.jsp.ui.tests.BaseAlbumCollectionBean");
		assertEquals("Number of properties for BaseAlbumCollectionBean:", 4, descriptors.length);
		List getOnly = new ArrayList();
		List setOnly = new ArrayList();
		List both = new ArrayList();
		IJavaPropertyDescriptor jpd = null;
		for (int i = 0; i < descriptors.length; i++) {
			jpd = descriptors[i];
			if (jpd.getReadable() && jpd.getWriteable())
				both.add(jpd);
			else if (jpd.getReadable() && !jpd.getWriteable())
				getOnly.add(jpd);
			else
				setOnly.add(jpd);
		}
		assertEquals("Number of getOnly properties:", 1, getOnly.size());
		assertEquals("Number of setOnly properties:", 1, setOnly.size());
		assertEquals("Number of get & set properties that:", 2, both.size());
		// get only
		jpd = (IJavaPropertyDescriptor) getOnly.get(0);
		assertEquals("get readable for get only property: ", true, jpd.getReadable());
		assertEquals("get writable for get only property:", false, jpd.getWriteable());
		assertEquals("name for get only property", "numCds", jpd.getDisplayName());
		assertEquals("type for get only property", "int", jpd.getDeclaredType());
		// set only
		jpd = (IJavaPropertyDescriptor) setOnly.get(0);
		assertEquals("get readable for set only property: ", false, jpd.getReadable());
		assertEquals("get writable for set only property:", true, jpd.getWriteable());
		assertEquals("name for set only property", "cds", jpd.getDisplayName());
		assertEquals("type for set only property", "String[]", jpd.getDeclaredType());
		// both
		jpd = (IJavaPropertyDescriptor) both.get(0);
		assertEquals("get readable for both property: ", true, jpd.getReadable());
		assertEquals("get writable for both property:", true, jpd.getWriteable());
	}

	private BeanInfoProvider getProvider() {
		if (fProvider == null)
			fProvider = new BeanInfoProvider();
		return fProvider;
	}
}
