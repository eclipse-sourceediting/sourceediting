/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. 
 *******************************************************************************/
package org.eclipse.wst.jsdt.internal.ui.wizards.dojo;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.wst.jsdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;


/**
 * Wizard page to create a new class.
 * <p>
 * Note: This class is not intended to be subclassed, but clients can
 * instantiate. To implement a different kind of a new class wizard page, extend
 * <code>NewTypeWizardPage</code>.
 * </p>
 * 
 * @since 2.0
 */
public class NewClassWizardPage extends NewTypeWizardPage {

	private final static String PAGE_NAME = "NewClassWizardPage"; //$NON-NLS-1$

	private final static String SETTINGS_CREATEMAIN = "create_main"; //$NON-NLS-1$
	private final static String SETTINGS_CREATECONSTR = "create_constructor"; //$NON-NLS-1$
	private final static String SETTINGS_CREATEUNIMPLEMENTED = "create_unimplemented"; //$NON-NLS-1$

	private SelectionButtonDialogFieldGroup fMethodStubsButtons;

	/**
	 * Creates a new <code>NewClassWizardPage</code>
	 */
	public NewClassWizardPage() {
		super(true, PAGE_NAME);

		setTitle("Dojo Class");
		setDescription("Create a new Dojo Class");

		// String[] buttonNames3= new String[] {
		// NewWizardMessages.NewClassWizardPage_methods_main,
		// NewWizardMessages.NewClassWizardPage_methods_constructors,
		// NewWizardMessages.NewClassWizardPage_methods_inherited
		// };
		String[] buttonNames3 = new String[] { "initializer", "statics" };
		fMethodStubsButtons = new SelectionButtonDialogFieldGroup(SWT.CHECK,
				buttonNames3, 1);
		fMethodStubsButtons
				.setLabelText(NewWizardMessages.NewClassWizardPage_methods_label);
	}

	// -------- Initialization ---------

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		getInitialJavaElement(selection);
		// initContainerPage(jelem.getJavaProject());
		initContainerPage(selection);
		initTypePage(selection);
		doStatusUpdate();

		boolean createMain = false;
		boolean createConstructors = false;
		boolean createUnimplemented = true;
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
			if (section != null) {
				createMain = section.getBoolean(SETTINGS_CREATEMAIN);
				createConstructors = section.getBoolean(SETTINGS_CREATECONSTR);
				createUnimplemented = section
						.getBoolean(SETTINGS_CREATEUNIMPLEMENTED);
			}
		}

		setMethodStubSelection(createMain, createConstructors,
				createUnimplemented, true);
	}

	// ------ validation --------
	protected void doStatusUpdate() {
		// status of all used components
		// IStatus[] status= new IStatus[] {
		// fContainerStatus,
		// isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
		// fTypeNameStatus,
		// fModifierStatus,
		// fSuperClassStatus,
		// fSuperInterfacesStatus
		// };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		String newFileName = getNewFileName();

		String existingFileName = getExistingFileName();
		getFolderName();
		String className = getTypeName();
		getJavaProject();
		getExtends();
		boolean shouldUseExisting = shouldUseExisting();
		isCreateInit();
		isCreateStatic();

		if (className == null || className.equals("")) {
			updateStatus(new Status(IStatus.ERROR, "com.ibm.jsdt.dojo.core",
					"Missing Class Name"));
		} else if ((!shouldUseExisting)
				&& (newFileName == null || newFileName.equals(""))) {
			updateStatus(new Status(IStatus.ERROR, "com.ibm.jsdt.dojo.core",
					"No File Specified for new Class"));

		} else if ((shouldUseExisting)
				&& (existingFileName == null || existingFileName.equals(""))) {
			updateStatus(new Status(IStatus.ERROR, "com.ibm.jsdt.dojo.core",
					"No File Specified for new Class"));
		} else {

			updateStatus(new Status(IStatus.OK, "com.ibm.jsdt.dojo.core", ""));
		}
	}

	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		doStatusUpdate();
	}

	// ------ UI --------

	/*
	 * @see WizardPage#createControl
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		// createModifierControls(composite, nColumns);

		// createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		createMethodStubSelectionControls(composite, nColumns);

		// createCommentControls(composite, nColumns);
		// enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
				IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);
	}

	/*
	 * @see WizardPage#becomesVisible
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		} else {
			IDialogSettings dialogSettings = getDialogSettings();
			if (dialogSettings != null) {
				IDialogSettings section = dialogSettings.getSection(PAGE_NAME);
				if (section == null) {
					section = dialogSettings.addNewSection(PAGE_NAME);
				}
				section.put(SETTINGS_CREATEMAIN, isCreateInit());
				section.put(SETTINGS_CREATECONSTR, isCreateStatic());

			}
		}
	}

	private void createMethodStubSelectionControls(Composite composite,
			int nColumns) {
		Control labelControl = fMethodStubsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);

		DialogField.createEmptySpace(composite);

		Control buttonGroup = fMethodStubsButtons
				.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}

	/**
	 * Returns the current selection state of the 'Create Main' checkbox.
	 * 
	 * @return the selection state of the 'Create Main' checkbox
	 */
	public boolean isCreateStatic() {
		return fMethodStubsButtons.isSelected(1);
	}

	/**
	 * Returns the current selection state of the 'Create Constructors'
	 * checkbox.
	 * 
	 * @return the selection state of the 'Create Constructors' checkbox
	 */
	public boolean isCreateInit() {
		return fMethodStubsButtons.isSelected(0);
	}

	/**
	 * Sets the selection state of the method stub checkboxes.
	 * 
	 * @param createMain
	 *            initial selection state of the 'Create Main' checkbox.
	 * @param createConstructors
	 *            initial selection state of the 'Create Constructors' checkbox.
	 * @param createInherited
	 *            initial selection state of the 'Create inherited abstract
	 *            methods' checkbox.
	 * @param canBeModified
	 *            if <code>true</code> the method stub checkboxes can be
	 *            changed by the user. If <code>false</code> the buttons are
	 *            "read-only"
	 */
	public void setMethodStubSelection(boolean createMain,
			boolean createConstructors, boolean createInherited,
			boolean canBeModified) {
		fMethodStubsButtons.setSelection(0, createMain);
		fMethodStubsButtons.setSelection(1, createConstructors);
		fMethodStubsButtons.setSelection(2, createInherited);

		fMethodStubsButtons.setEnabled(canBeModified);
	}

	// // ---- creation ----------------
	//	
	// /*
	// * @see NewTypeWizardPage#createTypeMembers
	// */
	// protected void createTypeMembers(IType type, ImportsManager imports,
	// IProgressMonitor monitor) throws CoreException {
	// boolean doMain= isCreateMain();
	// boolean doConstr= isCreateConstructors();
	// boolean doInherited= isCreateInherited();
	// createInheritedMethods(type, doConstr, doInherited, imports, new
	// SubProgressMonitor(monitor, 1));
	//
	// if (doMain) {
	// StringBuffer buf= new StringBuffer();
	// final String lineDelim= "\n"; // OK, since content is formatted afterwards
	// //$NON-NLS-1$
	// String comment= CodeGeneration.getMethodComment(type.getCompilationUnit(),
	// type.getTypeQualifiedName('.'), "main", new String[] {"args"}, new String[0],
	// Signature.createTypeSignature("void", true), null, lineDelim); //$NON-NLS-1$
	// //$NON-NLS-2$ //$NON-NLS-3$
	// if (comment != null) {
	// buf.append(comment);
	// buf.append(lineDelim);
	// }
	// buf.append("public static void main("); //$NON-NLS-1$
	// buf.append(imports.addImport("java.lang.String")); //$NON-NLS-1$
	// buf.append("[] args) {"); //$NON-NLS-1$
	// buf.append(lineDelim);
	// final String content=
	// CodeGeneration.getMethodBodyContent(type.getCompilationUnit(),
	// type.getTypeQualifiedName('.'), "main", false, "", lineDelim); //$NON-NLS-1$
	// //$NON-NLS-2$
	// if (content != null && content.length() != 0)
	// buf.append(content);
	// buf.append(lineDelim);
	// buf.append("}"); //$NON-NLS-1$
	// type.createMethod(buf.toString(), null, false, null);
	// }
	//		
	// if (monitor != null) {
	// monitor.done();
	// }
	// }
	public void createType(IProgressMonitor monitor) throws CoreException,
			InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask("Create Dojo Class...", 8);

		String newFileName = getNewFileName();
		String existingFileName = getExistingFileName();
		String newPath = getFolderName();
		String className = getTypeName();
		IJavaScriptProject project = getJavaProject();
		String[] extendedClasses = getExtends();
		boolean shouldUseExisting = shouldUseExisting();
		boolean shouldCreateInit = isCreateInit();
		boolean shouldCreateStatic = isCreateStatic();
		// boolean shouldGenerateComments = isAddComments();
		String typeText1 = "";
		String typeText2 = "";
		String typeText3 = "";
		String typeText4 = "";
		String typeText5 = "";

		typeText1 = "dojo.declare(\"" + className + "\"";

		/* Build the mixins in the typeText2 string */
		if (extendedClasses != null && extendedClasses.length > 0) {
			typeText1 += ",";
		}

		if (extendedClasses != null && extendedClasses.length > 1) {
			typeText2 += "[";
		}
		for (int i = 0; extendedClasses != null && i < extendedClasses.length; i++) {
			typeText2 += extendedClasses[i];
			if (i < extendedClasses.length - 1)
				typeText2 += ",";
		}
		if (extendedClasses != null && extendedClasses.length > 1) {
			typeText2 += "]";
		}

		/* Start the class body */

		typeText3 += ",{\n\t/* Class Body */\n\n";

		if (shouldCreateInit) {
			typeText4 += "\tinitializer: function() {\n\t/* Class Initializer */\n\n\t}";

		}

		if (shouldCreateInit && shouldCreateStatic) {
			typeText4 += ",";
		}

		if (shouldCreateStatic) {
			typeText4 += "\n\tstatics: {\n\t\t /* declare static variables and functions here */\n\n\t}\n";

		}

		typeText5 += "});";

		String typeText = typeText1 + typeText2 + typeText3 + typeText4
				+ typeText5;

		if (shouldUseExisting) {

			String fileExtension = (new Path(existingFileName))
					.getFileExtension();

			if (fileExtension != null && fileExtension.equalsIgnoreCase(".js")) {

				if (existingFileName.length() > 0
						&& existingFileName.charAt(0) == '/'
						|| existingFileName.charAt(0) == '\\') {
					existingFileName = existingFileName.substring(1);
				}
				/* Create type in an existing file */
				IJavaScriptUnit jElm = (IJavaScriptUnit) project
						.findElement(new Path(existingFileName));

				if (jElm == null)
					return;

				jElm.becomeWorkingCopy(monitor);
				jElm.getBuffer().append(typeText);
				jElm.commitWorkingCopy(true, monitor);
				jElm.discardWorkingCopy();
			} else if (fileExtension != null
					&& (fileExtension.equalsIgnoreCase("html")
							|| fileExtension.equalsIgnoreCase("htm") || fileExtension
							.equalsIgnoreCase("jsp"))) {
				/* handle dojo class creation in an html file */
				IFile toHtml = project.getProject().getFile(existingFileName);
				insertInThml(toHtml, typeText);

			}
		} else {
			/* Create the type in a new file */
			IPath newFilePath = new Path(newPath + "/" + newFileName);
			IFile theFile = project.getProject().getFile(newFilePath);

			String fileExtension = theFile.getFileExtension();

			if (fileExtension != null
					&& (fileExtension.equalsIgnoreCase("html")
							|| fileExtension.equalsIgnoreCase("htm") || fileExtension
							.equalsIgnoreCase("jsp"))) {
				/* a little special casing for HTML/JSP */
				typeText = "<html>\n<head>\n<script>\n" + typeText
						+ "\n</script>\n</head>\n</html>";

			}
			ByteArrayInputStream str = new ByteArrayInputStream(typeText
					.getBytes());
			theFile.create(str, true, monitor);

		}

	}

	private void insertInThml(IFile htmlFile, String text) {
		boolean existing = true;
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager()
					.getExistingModelForEdit(htmlFile);
			if (xmlModel == null) {
				try {
					existing = false;
					xmlModel = (IDOMModel) StructuredModelManager
							.getModelManager().getModelForEdit(htmlFile);
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} catch (CoreException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
			if (!existing)
				JsTranslationAdapterFactory.setupAdapterFactory(xmlModel);
			IDOMDocument xmlDoc = xmlModel.getDocument();

			JsTranslationAdapter fTranslationAdapter = (JsTranslationAdapter) xmlDoc
					.getAdapterFor(IJsTranslation.class);
			if (fTranslationAdapter != null) {
				IJsTranslation translation = fTranslationAdapter
						.getJSPTranslation(false);
				translation.insertInFirstScriptRegion(text);

			}
			if (!existing)
				removeAdapterFactory(xmlModel);
		} finally {
			if (xmlModel != null)
				xmlModel.releaseFromEdit();

		}

	}

	

	private void removeAdapterFactory(IStructuredModel sm) {
		if (sm.getFactoryRegistry().getFactoryFor(IJsTranslation.class) != null) {
			sm.getFactoryRegistry().removeFactoriesFor(IJsTranslation.class);
		}
	}
}
