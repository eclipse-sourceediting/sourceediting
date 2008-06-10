/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. 
 *******************************************************************************/
package org.eclipse.wst.jsdt.internal.ui.wizards.dojo;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.wst.jsdt.ui.wizards.NewElementWizardPage;

/**
 * Wizard page that acts as a base class for wizard pages that create new Java
 * elements. The class provides a input field for source folders (called
 * container in this class) and API to validate the enter source folder name.
 * 
 * <p>
 * Clients may subclass.
 * </p>
 * 
 * @since 2.0
 */
public abstract class NewContainerWizardPage extends NewElementWizardPage {

	/** Id of the container field */
	protected static final String CONTAINER = "NewContainerWizardPage.container"; //$NON-NLS-1$

	/** The status of the last validation. */
	protected IStatus fContainerStatus;

	private StringDialogField fNewFileName;

	private IJavaScriptProject fJavaProject;

	private IWorkspaceRoot fWorkspaceRoot;

	/**
	 * Create a new <code>NewContainerWizardPage</code>
	 * 
	 * @param name
	 *            the wizard page's name
	 */
	public NewContainerWizardPage(String name) {
		super(name);
		fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		fNewFileName = new StringDialogField();// new
		// StringButtonDialogField(adapter);
		fNewFileName.setLabelText("New File Name");
		fNewFileName.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				doStatusUpdate();

			}

		});
		// fContainerDialogField.setButtonLabel("Br&owse");

		// fContainerStatus= new StatusInfo();
		// fCurrRoot= null;
	}

	protected void doStatusUpdate() {

	}

	protected void shouldDisableFolderSelection(boolean enabled) {
		fNewFileName.setEnabled(enabled);
	}

	/**
	 * Initializes the source folder field with a valid package fragment root.
	 * The package fragment root is computed from the given Java element.
	 * 
	 * @param elem
	 *            the Java element used to compute the initial package fragment
	 *            root used as the source folder
	 */

	protected void initContainerPage(IStructuredSelection selection) {
		IJavaScriptElement initialElement = getInitialJavaElement(selection);

		fJavaProject = initialElement != null ? initialElement.getJavaScriptProject()
				: null;
		fNewFileName.setEnabled(true);
		fNewFileName.setFocus();
	}

	/**
	 * Utility method to inspect a selection to find a Java element.
	 * 
	 * @param selection
	 *            the selection to be inspected
	 * @return a Java element to be used as the initial selection, or
	 *         <code>null</code>, if no Java element exists in the given
	 *         selection
	 */
	protected IJavaScriptElement getInitialJavaElement(IStructuredSelection selection) {
		IJavaScriptElement jelem = null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement = selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) selectedElement;

				jelem = (IJavaScriptElement) adaptable.getAdapter(IJavaScriptElement.class);
				if (jelem == null) {
					IResource resource = (IResource) adaptable
							.getAdapter(IResource.class);
					if (resource != null
							&& resource.getType() != IResource.ROOT) {
						while (jelem == null
								&& resource.getType() != IResource.PROJECT) {
							resource = resource.getParent();
							jelem = (IJavaScriptElement) resource
									.getAdapter(IJavaScriptElement.class);
						}
						if (jelem == null) {
							jelem = JavaScriptCore.create(resource); // java project
						}
					}
				}
			}
		}
		if (jelem == null) {
			IWorkbenchPart part = JavaScriptPlugin.getActivePage().getActivePart();
			if (part instanceof ContentOutline) {
				part = JavaScriptPlugin.getActivePage().getActiveEditor();
			}

			if (part instanceof IViewPartInputProvider) {
				Object elem = ((IViewPartInputProvider) part)
						.getViewPartInput();
				if (elem instanceof IJavaScriptElement) {
					jelem = (IJavaScriptElement) elem;
				}
			}
		}

		if (jelem == null || jelem.getElementType() == IJavaScriptElement.JAVASCRIPT_MODEL) {
			try {
				IJavaScriptProject[] projects = JavaScriptCore.create(getWorkspaceRoot())
						.getJavaScriptProjects();
				if (projects.length == 1) {
					jelem = projects[0];
				}
			} catch (JavaScriptModelException e) {
				JavaScriptPlugin.log(e);
			}
		}
		return jelem;
	}

	/**
	 * Returns the recommended maximum width for text fields (in pixels). This
	 * method requires that createContent has been called before this method is
	 * call. Subclasses may override to change the maximum width for text
	 * fields.
	 * 
	 * @return the recommended maximum width for text fields.
	 */
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}

	/**
	 * Creates the necessary controls (label, text field and browse button) to
	 * edit the source folder location. The method expects that the parent
	 * composite uses a <code>GridLayout</code> as its layout manager and that
	 * the grid layout has at least 3 columns.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param nColumns
	 *            the number of columns to span. This number must be greater or
	 *            equal three
	 */
	protected void createContainerControls(Composite parent, int nColumns) {
		fNewFileName.doFillIntoGrid(parent, nColumns);
		LayoutUtil.setWidthHint(fNewFileName.getTextControl(null),
				getMaxFieldWidth());

	}

	/**
	 * Sets the focus to the source folder's text field.
	 */
	protected void setFocusOnContainer() {
		fNewFileName.setFocus();
	}

	/**
	 * Returns the workspace root.
	 * 
	 * @return the workspace root
	 */
	protected IWorkspaceRoot getWorkspaceRoot() {
		return fWorkspaceRoot;
	}

	/**
	 * Returns the Java project of the currently selected package fragment root
	 * or <code>null</code> if no package fragment root is configured.
	 * 
	 * @return The current Java project or <code>null</code>.
	 * @since 3.3
	 */
	public IJavaScriptProject getJavaProject() {
		return fJavaProject;
	}

	public void setJavaProject(IJavaScriptProject project) {
		fJavaProject = project;
	}

	protected String getNewFileName() {
		return fNewFileName.getText();
	}

}
