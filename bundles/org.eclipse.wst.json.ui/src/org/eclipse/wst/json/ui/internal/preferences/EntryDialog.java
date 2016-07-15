/*************************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.eclipse.wst.json.ui.internal.preferences;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.json.core.internal.schema.catalog.UserEntries;
import org.eclipse.wst.json.core.internal.schema.catalog.UserEntry;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;

public class EntryDialog extends TitleAreaDialog {

	private Image dlgTitleImage;
	private UserEntry selectedEntry;
	private String fileMatch;
	private URI url;
	private Text fileMatchText;
	private Text urlText;
	private Button okButton;
	private UserEntries entries;

	protected EntryDialog(Shell parentShell, UserEntry entry, UserEntries entries) {
		super(parentShell);
		this.selectedEntry = entry;
		this.entries = entries;
	}

	@Override
	protected Control createContents(Composite parent) {
		
		Control contents = super.createContents(parent);
		if (selectedEntry == null) {
			setTitle(JSONUIMessages.Add_Catalog_Entry);
			setMessage(JSONUIMessages.Add_Catalog_Entry);
		} else {
			setTitle(JSONUIMessages.Edit_Catalog_Entry);
			setMessage(JSONUIMessages.Edit_Catalog_Entry);
		}
		ImageDescriptor descriptor = JSONUIPlugin
		.imageDescriptorFromPlugin(JSONUIPlugin.PLUGIN_ID,
				"icons/WizBan.png"); //$NON-NLS-1$
        if(descriptor != null) {
        	dlgTitleImage = descriptor.createImage();
        	setTitleImage(dlgTitleImage);
        }
        
		return contents;
	}

	@Override
	public boolean close() {
        if (dlgTitleImage != null) {
			dlgTitleImage.dispose();
		}
        return super.close();
    }
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);
        
		Composite container = new Composite(parentComposite, SWT.FILL);
		GridLayout layout = new GridLayout(3,false);
		layout.marginWidth = layout.marginHeight = 10;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);

		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText(JSONUIMessages.FileMatch);
		fileMatchText = new Text(container, SWT.SINGLE|SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan=2;
		fileMatchText.setLayoutData(gd);
		fileMatchText.addModifyListener(new ModifyListener(){
		
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		
		Label urlLabel = new Label(container, SWT.NONE);
		urlLabel.setText(JSONUIMessages.URL);
		urlText = new Text(container, SWT.SINGLE|SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		urlText.setLayoutData(gd);
		urlText.addModifyListener(new ModifyListener(){
			
			public void modifyText(ModifyEvent e) {
				validatePage();
			}

		});
		if (selectedEntry != null) {
			urlText.setText(selectedEntry.getUrl().toString());
			fileMatchText.setText(selectedEntry.getFileMatch());
		}
		Button browse = new Button(container,SWT.PUSH);
		browse.setText(JSONUIMessages.Browse);
		browse.addSelectionListener(new SelectionListener(){
		
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				String result = dialog.open();
				if (result == null || result.trim().length() == 0) {
					return;
				}
				try {
					String urlString = new File(result).toURI().toURL().toString();
					urlText.setText(urlString);
				} catch (MalformedURLException e1) {
					urlText.setText("file:///" + result); //$NON-NLS-1$
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		return parentComposite;
	}

	private boolean validatePage() {
		fileMatch = null;
		url = null;
		if (fileMatchText.getText().trim().length() <= 0) {
			setErrorMessage(JSONUIMessages.The_name_field_is_required);
			return updateButton(false);
		}
		Set<UserEntry> list = entries.getEntries();
		for(UserEntry entry:list) {
			if (entry != selectedEntry && fileMatchText.getText().equals(entry.getFileMatch())) {
				setErrorMessage(JSONUIMessages.The_entry_already_exists);
				return updateButton(false);
			}
		}
		if (urlText.getText().trim().length() <= 0) {
			setErrorMessage(JSONUIMessages.The_url_field_is_required);
			return updateButton(false);
		}
		try {
			@SuppressWarnings("unused")
			URL url = new URL(urlText.getText());
		} catch (MalformedURLException e) {
			setErrorMessage(JSONUIMessages.Invalid_URL);
			return updateButton(false);
		}
		setErrorMessage(null);
		fileMatch = fileMatchText.getText();
		try {
			url = new URL(urlText.getText()).toURI();
		} catch (MalformedURLException ignore) {
		} catch (URISyntaxException ignore) {
		}
		return updateButton(true);
	}

	private boolean updateButton(boolean enabled) {
		if (okButton != null) {
			okButton.setEnabled(enabled);
		}
		return false;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        okButton.setEnabled(selectedEntry != null);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	public String getFileMatch() {
		return fileMatch;
	}
	
	public URI getURL() {
		return url;
	}
	
}
