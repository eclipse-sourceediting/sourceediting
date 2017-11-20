package org.eclipse.jst.jsp.ui.tests.document;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.ui.tests.JSPUITestsPlugin;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;

/**
 * @author pavery
 */
public class UnzippedProjectTester extends TestCase {

	protected static IProject fTestProject;
	private static boolean fTestProjectInitialized;
	private static int nSetups = 0;
	private static final String TEST_PROJECT_NAME = "org.eclipse.wst.sse.core.internal.encoding.newtests";

	protected void setUp() throws Exception {
		super.setUp();
		nSetups++;
		if (!fTestProjectInitialized) {
			getAndCreateProject();
			// unzip files to the root of workspace directory
			String destinationProjectString = fTestProject.getLocation().toOSString();
			String destinationFolder = destinationProjectString + "/";
			// this zip file is sitting in the "root" of test plugin
			File zipFile = JSPUITestsPlugin.getTestFile("testfiles.zip");
			ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
			projUtil.unzipAndImport(zipFile, destinationFolder);
			projUtil.initJavaProject(TEST_PROJECT_NAME);
			fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			fTestProjectInitialized = true;
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		nSetups--;
		if (nSetups == 0) {
			if (!DEBUG) {
				//				Display display = PlatformUI.getWorkbench().getDisplay();
				//				display.asyncExec(new Runnable() {
				//					public void run() {
				//						ProjectUnzipUtility projUtil = new ProjectUnzipUtility();
				//						IProject proj = fTestProject;
				//						fTestProject = null;
				//						try {
				//							projUtil.deleteProject(proj);
				//						} catch (Exception e) {
				//							e.printStackTrace();
				//						}
				//					}
				//				});
			}
		}
	}

	private static final boolean DEBUG = false;

	private static void getAndCreateProject() throws CoreException {
		//TestsPlugin testsPlugin = (TestsPlugin)
		// Platform.getPlugin("org.eclipse.wst.sse.core.internal.encoding.tests");
		IWorkspace workspace = getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		fTestProject = root.getProject(TEST_PROJECT_NAME);
		// this form creates project as "linked" back to 'fileRoot'
		//createProject(testProject, new Path(fileRoot), null);
		createProject(fTestProject, null, null);
		fTestProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		assertTrue(fTestProject.exists());
		//		IContainer dotestFiles = testProject.getFolder("dotestFiles");
		//		assertTrue(dotestFiles.exists());
		//		IResource[] allFolders = dotestFiles.members();
		//		assertNotNull(allFolders);
	}
 
	private static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("creating test project", 10);
		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
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
		}
		finally {
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
