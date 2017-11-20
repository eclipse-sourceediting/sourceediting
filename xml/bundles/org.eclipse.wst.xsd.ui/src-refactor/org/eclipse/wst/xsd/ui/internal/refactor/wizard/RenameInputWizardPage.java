/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.wizard;



import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.refactor.IReferenceUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;

/**
 * @author ebelisar
 *
 */
public class RenameInputWizardPage  extends UserInputWizardPage{
	private String fInitialValue;
	private Text fTextField;
	private Button fUpdateReferences;
	/**
	 * Creates a new text input page.
	 * @param isLastUserPage <code>true</code> if this page is the wizard's last
	 *  user input page. Otherwise <code>false</code>.
	 */
	public RenameInputWizardPage(String description, boolean isLastUserPage) {
		this(description, isLastUserPage, ""); //$NON-NLS-1$
	}
	
	/**
	 * Creates a new text input page.
	 * @param isLastUserPage <code>true</code> if this page is the wizard's last
	 *  user input page. Otherwise <code>false</code>
	 * @param initialValue the initial value
	 */
	public RenameInputWizardPage(String description, boolean isLastUserPage, String initialValue) {
	    super("RenameInputWizardPage");
		Assert.isNotNull(initialValue);
		setDescription(description);
		fInitialValue= initialValue;
	}
	
	/**
	 * Returns whether the initial input is valid. Typically it is not, because the 
	 * user is required to provide some information e.g. a new type name etc.
	 * 
	 * @return <code>true</code> iff the input provided at initialization is valid
	 */
	protected boolean isInitialInputValid(){
		return false;
	}
	
	/**
	 * Returns whether an empty string is a valid input. Typically it is not, because 
	 * the user is required to provide some information e.g. a new type name etc.
	 * 
	 * @return <code>true</code> iff an empty string is valid
	 */
	protected boolean isEmptyInputValid(){
		return false;
	}
	
	/**
	 * Returns the content of the text input field.
	 * 
	 * @return the content of the text input field. Returns <code>null</code> if
	 * not text input field has been created
	 */
	protected String getText() {
		if (fTextField == null)
			return null;
		return fTextField.getText();	
	}
	
	/**
	 * Sets the new text for the text field. Does nothing if the text field has not been created.
	 * @param text the new value
	 */
	protected void setText(String text) {
		if (fTextField == null)
			return;
		fTextField.setText(text);
	}
	
	/**
	 * Performs input validation. Returns a <code>RefactoringStatus</code> which
	 * describes the result of input validation. <code>Null<code> is interpreted
	 * as no error.
	 */
	protected RefactoringStatus validateTextField(String text){
		return null;
	}
	
	protected Text createTextInputField(Composite parent) {
		return createTextInputField(parent, SWT.BORDER);
	}
	
	protected Text createTextInputField(Composite parent, int style) {
		fTextField= new Text(parent, style);
		fTextField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textModified(getText());
			}
		});
    PlatformUI.getWorkbench().getHelpSystem().setHelp(fTextField, XSDEditorCSHelpIds.RENAME_NEW_NAME);
		fTextField.setText(fInitialValue);
		return fTextField;
	}
	
	/**
	 * Checks the page's state and issues a corresponding error message. The page validation
	 * is computed by calling <code>validatePage</code>.
	 */
	protected void textModified(String text) {	
		if (! isEmptyInputValid() && text.equals("")){ //$NON-NLS-1$
			setPageComplete(false);
			setErrorMessage(null);
			restoreMessage();
			return;
		}
		if ((! isInitialInputValid()) && text.equals(fInitialValue)){
			setPageComplete(false);
			setErrorMessage(null);
			restoreMessage();
			return;
		}
		
		setPageComplete(validateTextField(text));
		
//		 TODO: enable preview in M4
		getRefactoringWizard().setForcePreviewReview(false);
		getContainer().updateButtons();
	
	}
	
	/**
	 * Subclasses can override if they want to restore the message differently.
	 * This implementation calls <code>setMessage(null)</code>, which clears the message 
	 * thus exposing the description.
	 */
	protected void restoreMessage(){
		setMessage(null);
	}
	
	/* (non-Javadoc)
	 * Method declared in IDialogPage
	 */
	public void dispose() {
		fTextField= null;	
	}
	
	/* (non-Javadoc)
	 * Method declared in WizardPage
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			textModified(getText());
		}
		super.setVisible(visible);
		if (visible && fTextField != null) {
			fTextField.setFocus();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite superComposite= new Composite(parent, SWT.NONE);
		setControl(superComposite);
		initializeDialogUnits(superComposite);
		
		superComposite.setLayout(new GridLayout());
		Composite composite= new Composite(superComposite, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.verticalSpacing= 8;
		composite.setLayout(layout);
		
		
		Label label= new Label(composite, SWT.NONE);
		label.setText(getLabelText());
		
		Text text= createTextInputField(composite);
		text.selectAll();
		GridData gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= convertWidthInCharsToPixels(25);
		text.setLayoutData(gd);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(text, XSDEditorCSHelpIds.RENAME_NEW_NAME);
    
		addOptionalUpdateReferencesCheckbox(superComposite);
		gd= new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);
		
		getRefactoringWizard().setForcePreviewReview(false);
		
		Dialog.applyDialogFont(superComposite);
		//WorkbenchHelp.setHelp(getControl(), fHelpContextID);

	}
	
	private static Button createCheckbox(Composite parent, String title, boolean value) {
		Button checkBox= new Button(parent, SWT.CHECK);
		checkBox.setText(title);
		checkBox.setSelection(value);
		return checkBox;		
	}
	
	private void addOptionalUpdateReferencesCheckbox(Composite result) {

		final IReferenceUpdating ref= (IReferenceUpdating)getRefactoring().getAdapter(IReferenceUpdating.class);
		if (ref == null || !ref.canEnableUpdateReferences())	
			return;
		String title= RefactoringMessages.getString("RenameInputWizardPage.update_references"); //$NON-NLS-1$
		boolean defaultValue= true; 
		fUpdateReferences= createCheckbox(result, title, defaultValue);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(fUpdateReferences, XSDEditorCSHelpIds.RENAME_UPDATE_REFERENCES);
		ref.setUpdateReferences(fUpdateReferences.getSelection());
		fUpdateReferences.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
    		ref.setUpdateReferences(fUpdateReferences.getSelection());
			}
		});				
		fUpdateReferences.setEnabled(true);		
	}
	
	protected String getLabelText() {
		return RefactoringMessages.getString("RenameInputWizardPage.new_name"); //$NON-NLS-1$
	}
	

}
