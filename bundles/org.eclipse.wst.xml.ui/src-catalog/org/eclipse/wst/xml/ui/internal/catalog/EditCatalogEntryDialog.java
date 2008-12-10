/*******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.catalog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.common.ui.internal.dialogs.SelectSingleFileDialog;
import org.eclipse.wst.common.uriresolver.internal.URI;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;

public class EditCatalogEntryDialog extends Dialog {
	protected static Image borwseImage = ImageFactory.INSTANCE.getImage("icons/obj16/file_expand.gif"); //$NON-NLS-1$
	protected static Image catalogEntryToolBarImage = ImageFactory.INSTANCE.getImage("icons/etool50/catalogEntry.gif"); //$NON-NLS-1$
	protected static Image nextCatalogToolBarImage = ImageFactory.INSTANCE.getImage("icons/etool50/nextCatalog.gif"); //$NON-NLS-1$

	protected class CatalogEntryPage extends CatalogElementPage {

		protected Button browseWorkspaceButton;
		
		protected Button browseFileSystemButton;

		protected ICatalogEntry catalogEntry;

		protected Button checkboxButton;

		protected Label errorMessageLabel;

		protected Text keyField;

		protected Combo keyTypeCombo;

		protected Text resourceLocationField;

		protected Combo resourceTypeCombo;

		protected Text webAddressField;
		
		protected String key;
		
		protected int type;

		protected void computeErrorMessage() {
			errorMessage = null;
			warningMessage = null;

			if (errorMessage == null) {
				String fileName = resourceLocationField.getText();
				if (fileName.trim().length() > 0) {
					if ((fileName.indexOf("..") != -1) || (fileName.indexOf("./") != -1) || (fileName.indexOf("/.") != -1) || (fileName.indexOf(".\\") != -1) || (fileName.indexOf("\\.") != -1)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						errorMessage = XMLCatalogMessages.UI_WARNING_URI_MUST_NOT_HAVE_DOTS;
					}

					String uri = fileName;
					if (!URIHelper.hasProtocol(uri)) {
						URIHelper.isAbsolute(uri);
						uri = (URIHelper.isAbsolute(uri)) ? URIHelper.prependFileProtocol(uri) : URIHelper.prependPlatformResourceProtocol(uri);
					}

					if ((errorMessage == null) && !URIHelper.isReadableURI(uri, false)) {
						errorMessage = XMLCatalogMessages.UI_WARNING_URI_NOT_FOUND_COLON + fileName;
					}
				}
				else {
					// this an error that is not actaully
					// reported ... OK is just disabled
					errorMessage = ""; //$NON-NLS-1$
				}

				// Make sure the key is a fully qualified URI in the cases
				// where the key type is "System ID" or "Schema location"
				if ((keyField.getText().length() > 0) && (getKeyType() == ICatalogEntry.ENTRY_TYPE_SYSTEM)) {
					URI uri = URI.createURI(keyField.getText());
					if (uri.scheme() == null) {
						warningMessage = XMLCatalogMessages.UI_WARNING_SHOULD_BE_FULLY_QUALIFIED_URI;
					}
				}
			}

			if ((errorMessage == null) && checkboxButton.getSelection() && (webAddressField.getText().trim().length() == 0)) {
				// this an error that is not actaully
				// reported ... OK is just disabled
				errorMessage = ""; //$NON-NLS-1$
			}

			if ((errorMessage == null) && (keyField.getText().trim().length() == 0)) {
				// this an error that is not actaully
				// reported ... OK is just disabled
				errorMessage = ""; //$NON-NLS-1$
			}
		}

		protected Control createCatalogEntryPanel(Composite parent) {

			ModifyListener modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (e.widget == resourceLocationField) {
						if (keyField.getText().length() == 0) {
							String uri = resourceLocationField.getText();
							if (uri.endsWith("xsd") && !URIHelper.hasProtocol(uri)) { //$NON-NLS-1$
								uri = URIHelper.isAbsolute(uri) ? URIHelper.prependFileProtocol(uri) : URIHelper.prependPlatformResourceProtocol(uri);
								String namespaceURI = XMLQuickScan.getTargetNamespaceURIForSchema(uri);
								if (namespaceURI != null) {
									keyField.setText(namespaceURI);
								}
							}
						}
					}
					updateWidgets(e.widget);
				}
			};


			Composite composite = new Composite(parent, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_BOTH);
			composite.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			composite.setLayout(layout);

			Composite group = new Composite(composite, SWT.NONE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);

			layout = new GridLayout(2, false);
			group.setLayout(layout);

			Label resourceLocationLabel = new Label(group, SWT.NONE);
			resourceLocationLabel.setText(XMLCatalogMessages.UI_LABEL_LOCATION_COLON);

			resourceLocationField = new Text(group, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessHorizontalSpace = true;
			resourceLocationField.setLayoutData(gd);

			resourceLocationField.setText(getDisplayValue(URIUtils.convertURIToLocation(getEntry().getURI())));

			// WorkbenchHelp.setHelp(resourceLocationField,
			// XMLBuilderContextIds.XMLP_ENTRY_URI);
			resourceLocationField.addModifyListener(modifyListener);

			// WorkbenchHelp.setHelp(browseButton,
			// XMLBuilderContextIds.XMLP_ENTRY_BROWSE);

			Composite browseButtonsComposite = new Composite(group, SWT.NONE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			gd.horizontalAlignment = GridData.END;
			browseButtonsComposite.setLayoutData(gd);
			
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.marginBottom = 5;
			browseButtonsComposite.setLayout(layout);
			
			browseWorkspaceButton = new Button(browseButtonsComposite, SWT.PUSH);
			browseWorkspaceButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_WORKSPACE);
			browseWorkspaceButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					String value = invokeWorkspaceFileSelectionDialog();
					if(value != null) {
						resourceLocationField.setText(value);
					}
				}
			});
			
			browseFileSystemButton = new Button(browseButtonsComposite, SWT.PUSH);
			browseFileSystemButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_FILE_SYSTEM);
			browseFileSystemButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					String value = invokeFileSelectionDialog();
					if(value != null) {
						resourceLocationField.setText(value);
					}
				}
			});
			
			// Key Type
			//
			Label keyTypeLabel = new Label(group, SWT.NONE);
			keyTypeLabel.setText(XMLCatalogMessages.UI_KEY_TYPE_COLON);

			keyTypeCombo = new Combo(group, SWT.NONE);
			gd = new GridData();
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessHorizontalSpace = true;
			keyTypeCombo.setLayoutData(gd);
			updateKeyTypeCombo(getEntry().getEntryType());
			keyTypeCombo.addModifyListener(modifyListener);
			// WorkbenchHelp.setHelp(keyTypeCombo,
			// XMLBuilderContextIds.XMLP_ENTRY_KEY_TYPE);

			// Key
			// 
			Label keyValueLabel = new Label(group, SWT.NONE);
			keyValueLabel.setText(XMLCatalogMessages.UI_LABEL_KEY_COLON);
			keyField = new Text(group, SWT.SINGLE | SWT.BORDER);
			// WorkbenchHelp.setHelp(keyField,
			// XMLBuilderContextIds.XMLP_ENTRY_KEY);
			keyField.setLayoutData(gd);
			keyField.setText(getDisplayValue(getEntry().getKey()));
			keyField.addModifyListener(modifyListener);

			Composite group2 = new Composite(composite, SWT.NONE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			group2.setLayoutData(gd);

			layout = new GridLayout();
			group2.setLayout(layout);

			// checkbox -- note parent is dialogArea
			//
			checkboxButton = new Button(group2, SWT.CHECK);
			// WorkbenchHelp.setHelp(checkboxButton,
			// XMLBuilderContextIds.XMLP_ENTRY_SPECIFY_ALTERNATIVE);
			checkboxButton.setText(XMLCatalogMessages.UI_LABEL_SPECIFY_ALTERNATIVE_WEB_URL);
			checkboxButton.setLayoutData(new GridData());
			checkboxButton.setSelection(getEntry().getAttributeValue(ICatalogEntry.ATTR_WEB_URL) != null);
			SelectionListener buttonListener = new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent event) {
					// no impl
				}

				public void widgetSelected(SelectionEvent event) {
					if (event.widget == checkboxButton) {
						updateWidgets(checkboxButton);
					}
				}
			};
			checkboxButton.addSelectionListener(buttonListener);

			// Web Address field
			//

			ModifyListener webAddressFieldListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					computeErrorMessage();
					updateErrorMessageLabel(errorMessageLabel);
					updateOKButtonState();
				}
			};

			webAddressField = new Text(group2, SWT.SINGLE | SWT.BORDER);
			// WorkbenchHelp.setHelp(webAddressField,
			// XMLBuilderContextIds.XMLP_ENTRY_WEB_ADDRESS);
			webAddressField.setLayoutData(gd);
			webAddressField.setText(getDisplayValue(getEntry().getAttributeValue(ICatalogEntry.ATTR_WEB_URL)));
			webAddressField.setEnabled(false);
			webAddressField.addModifyListener(webAddressFieldListener);


			errorMessageLabel = new Label(group2, SWT.NONE);
			errorMessageLabel.setForeground(color);
			errorMessageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			updateWidgets(null);
			
			key = getEntry().getKey();
			type = getEntry().getEntryType();

			return composite;
		}

		public Control createControl(Composite parent) {

			fControl = createCatalogEntryPanel(parent);

			return fControl;
		}


		public ICatalogElement getData() {
			return getEntry();
		}

		protected ICatalogEntry getEntry() {
			if (catalogEntry == null) {
				if ((fCatalogElement != null) && (fCatalogElement.getType() == ICatalogElement.TYPE_ENTRY)) {
					catalogEntry = (ICatalogEntry) fCatalogElement;
				}
				else {
					if (catalog != null) {
						catalogEntry = (ICatalogEntry) catalog.createCatalogElement(ICatalogElement.TYPE_ENTRY);
					}
				}
			}
			return catalogEntry;
		}

		protected int getKeyType() {
			switch (keyTypeCombo.getSelectionIndex()) {
				case 0 :
					if ("schema".equals(keyTypeCombo.getData("keyType"))) { //$NON-NLS-1$ //$NON-NLS-2$
						return ICatalogEntry.ENTRY_TYPE_URI; // xsd
																// namespace
																// is URI type
																// key
					}
					return ICatalogEntry.ENTRY_TYPE_PUBLIC;
				case 1 :
					return ICatalogEntry.ENTRY_TYPE_SYSTEM;
				case 2 :
					return ICatalogEntry.ENTRY_TYPE_URI;
				default :
					return ICatalogEntry.ENTRY_TYPE_PUBLIC;
			}
		}

		public void saveData() {
			if (validateData()) {
				getEntry().setURI(URIUtils.convertLocationToURI(resourceLocationField.getText()));
				getEntry().setKey(keyField.getText());
				getEntry().setEntryType(getKeyType());
				getEntry().setAttributeValue(ICatalogEntry.ATTR_WEB_URL, checkboxButton.getSelection() ? webAddressField.getText() : null);
				dataSaved = true;
			}
			else {
				errorMessage = XMLCatalogMessages.UI_WARNING_DUPLICATE_ENTRY;
				errorMessageLabel.setText(errorMessage);
				updateOKButtonState();
				dataSaved = false;
			}
		}
		
		/**
		 * Validates that the data entered does not conflict with an existing entry in either catalog.
		 * @return True if validated, false otherwise.
		 */
		protected boolean validateData() {
		
			String result = null;
			if (key == null || !key.equals(keyField.getText()) || type != getKeyType())
			{
				// get system catalog
				ICatalog systemCatalog = null;			
				ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
				INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
				for (int i = 0; i < nextCatalogs.length; i++) {
					INextCatalog catalog = nextCatalogs[i];
					ICatalog referencedCatalog = catalog.getReferencedCatalog();
					if (referencedCatalog != null) {
						if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId())) {
							systemCatalog = referencedCatalog;
						}
					}
				}
				
				try {
					switch( getKeyType() )
					{
					case ICatalogEntry.ENTRY_TYPE_PUBLIC:
						result = catalog.resolvePublic(keyField.getText(), null);		
						if (result == null) {
							result = systemCatalog.resolvePublic(keyField.getText(), null);
						}
						break;
					case ICatalogEntry.ENTRY_TYPE_SYSTEM:
						result = catalog.resolveSystem(keyField.getText());
						if (result == null) {
							result = systemCatalog.resolveSystem(keyField.getText());
						}
						break;
					case ICatalogEntry.ENTRY_TYPE_URI:
						result = catalog.resolveURI(keyField.getText());
						if (result == null) {
							result = systemCatalog.resolveURI(keyField.getText());
						}
						break;
					}
				}
				catch (Exception e) {
				}
			}
			
			return (result == null);
		}

		protected void updateKeyTypeCombo(int type) {
			keyTypeCombo.removeAll();
			for (Iterator i = CatalogFileTypeRegistryReader.getXMLCatalogFileTypes().iterator(); i.hasNext();) {
				XMLCatalogFileType theFileType = (XMLCatalogFileType) i.next();
				if (theFileType.extensions != null) {
					for (Iterator j = theFileType.extensions.iterator(); j.hasNext();) {
						String extension = (String) j.next();
						if (resourceLocationField.getText().endsWith(extension)) {
							if (theFileType.description.equals("XSD Files")) { //$NON-NLS-1$
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_PUBLIC);
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_SYSTEM);
								keyTypeCombo.setData("keyType", "schema"); //$NON-NLS-1$ //$NON-NLS-2$
							}
							else if (theFileType.description.equals("DTD Files")) { //$NON-NLS-1$
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_PUBLIC);
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_SYSTEM);
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_URI);
							}
							else {
								keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_URI);
							}
						}

					}

				}
			}
			if (keyTypeCombo.getItemCount() == 0) {
				keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_PUBLIC);
				keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_SYSTEM);
				keyTypeCombo.add(XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_URI);
			}

			switch (type) {
				case ICatalogEntry.ENTRY_TYPE_PUBLIC :
					keyTypeCombo.select(0);
					break;
				case ICatalogEntry.ENTRY_TYPE_SYSTEM :
					keyTypeCombo.select(1);
					break;
				case ICatalogEntry.ENTRY_TYPE_URI : // handle XML Schema,
													// where namespace name is
													// mapped to URI situation
					if ("schema".equals(keyTypeCombo.getData("keyType"))) { //$NON-NLS-1$ //$NON-NLS-2$
						keyTypeCombo.select(0); // namespace name as URI key
												// type
					}
					else {
						keyTypeCombo.select(2); // URI key type
					}
					break;
				default :
					if (keyTypeCombo.getItemCount() > 0) {
						keyTypeCombo.select(0);
					}
					break;
			}

		}

		protected void updateWebAddressWidgets(int keyType) {
			boolean isPublicKeyType = (keyType == ICatalogEntry.ENTRY_TYPE_PUBLIC);
			checkboxButton.setEnabled(isPublicKeyType);
			webAddressField.setEnabled(isPublicKeyType && checkboxButton.getSelection());
		}

		protected void updateWidgets(Widget widget) {
			if (widget != keyTypeCombo) {
				updateKeyTypeCombo(getKeyType());
			}
			updateWebAddressWidgets(getKeyType());
			computeErrorMessage();
			updateErrorMessageLabel(errorMessageLabel);
			updateOKButtonState();
		}

	}

	protected abstract class CatalogElementPage {

		Control fControl;

		public CatalogElementPage() {
			super();

		}

		public abstract Control createControl(Composite parent);

		public Control getControl() {
			return fControl;
		}

		public abstract ICatalogElement getData();

		public abstract void saveData();
	}

	protected class FilterableSelectSingleFileDialog extends SelectSingleFileDialog implements SelectionListener {
		protected Combo filterControl;

		public FilterableSelectSingleFileDialog(Shell parentShell) {
			super(parentShell, null, true);
			setFilters(null);
		}

		public void createAndOpen() {
			this.create();
			setBlockOnOpen(true);
			getShell().setText(XMLCatalogMessages.UI_LABEL_FILE_SELECTION);
			this.setTitle(XMLCatalogMessages.UI_LABEL_SELECT_FILE);
			this.setMessage(XMLCatalogMessages.UI_LABEL_CHOOSE_FILE_TO_ADD_TO_CATALOG);
			open();
		}

		public void createFilterControl(Composite composite) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(XMLCatalogMessages.UI_LABEL_SELECT_FILE_FILTER_CONTROL);

			filterControl = new Combo(composite, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			filterControl.setLayoutData(gd);

			filterControl.setText(XMLCatalogMessages.UI_TEXT_SELECT_FILE_FILTER_CONTROL);
			filterControl.add(XMLCatalogMessages.UI_TEXT_SELECT_FILE_FILTER_CONTROL);

			for (Iterator i = CatalogFileTypeRegistryReader.getXMLCatalogFileTypes().iterator(); i.hasNext();) {
				XMLCatalogFileType fileType = (XMLCatalogFileType) i.next();
				if (fileType.description != null) {
					filterControl.add(fileType.description);
				}
			}

			filterControl.addSelectionListener(this);
		}

		protected void setFilters(XMLCatalogFileType fileType) {
			if (fileType == null) {
				// compute all the supported file extensions
				List list = new ArrayList();
				for (Iterator i = CatalogFileTypeRegistryReader.getXMLCatalogFileTypes().iterator(); i.hasNext();) {
					XMLCatalogFileType theFileType = (XMLCatalogFileType) i.next();
					if (theFileType.extensions != null) {
						list.addAll(theFileType.extensions);
					}
				}
				// Any files are now supported with Resource URI
				// selectSingleFileView.setFilterExtensions(createStringArray(list));
			}
			else {
				if (fileType.extensions != null) {
					selectSingleFileView.setFilterExtensions(createStringArray(fileType.extensions));
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}

		public void widgetSelected(SelectionEvent e) {
			String text = filterControl.getText();
			XMLCatalogFileType fileType = getMatchingFileType(text);
			setFilters(fileType);
		}
	}

	protected class NextCatalogPage extends CatalogElementPage {
		
		protected Button browseWorkspaceButton;
		
		protected Button browseFileSystemButton;
		
		protected Text catalogLocationField;

		protected INextCatalog nextCatalog;

		protected Label errorMessageLabel;

		protected void computeErrorMessage() {
			errorMessage = null;

			if (errorMessage == null) {
				String fileName = catalogLocationField.getText();
				if (fileName.trim().length() > 0) {
					if ((fileName.indexOf("..") != -1) || (fileName.indexOf("./") != -1) || (fileName.indexOf("/.") != -1) || (fileName.indexOf(".\\") != -1) || (fileName.indexOf("\\.") != -1)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						errorMessage = XMLCatalogMessages.UI_WARNING_URI_MUST_NOT_HAVE_DOTS;
					}

					String uri = fileName;
					if (!URIHelper.hasProtocol(uri)) {
						uri = URIHelper.isAbsolute(uri) ? URIHelper.prependFileProtocol(uri) : URIHelper.prependPlatformResourceProtocol(uri);
					}

					if ((errorMessage == null) && !URIHelper.isReadableURI(uri, false)) {
						errorMessage = XMLCatalogMessages.UI_WARNING_URI_NOT_FOUND_COLON + fileName;
					}
				}
				else {
					// this an error that is not actually
					// reported ... OK is just disabled
					errorMessage = ""; //$NON-NLS-1$
				}
			}

		}

		public Control createControl(Composite parent) {
			fControl = createNextCatalogPanel(parent);
			return fControl;
		}

		protected Control createNextCatalogPanel(Composite parent) {
			ModifyListener modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					updateWidgets(e.widget);
				}
			};

			Composite composite = new Composite(parent, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_BOTH);
			composite.setLayoutData(gd);

			GridLayout layout = new GridLayout();
			composite.setLayout(layout);

			Composite group = new Composite(composite, SWT.NONE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);

			layout = new GridLayout();
			group.setLayout(layout);

			Label resourceLocationLabel = new Label(group, SWT.NONE);
			resourceLocationLabel.setText(XMLCatalogMessages.UI_LABEL_CATALOG_URI_COLON);

			catalogLocationField = new Text(group, SWT.SINGLE | SWT.BORDER);
			catalogLocationField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			catalogLocationField.setText(URIUtils.convertURIToLocation(getDisplayValue(getNextCatalog().getCatalogLocation())));
			// WorkbenchHelp.setHelp(resourceLocationField,
			// XMLBuilderContextIds.XMLP_ENTRY_URI);
			catalogLocationField.addModifyListener(modifyListener);
			
			Composite browseButtonsComposite = new Composite(group, SWT.FLAT);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			gd.horizontalAlignment = GridData.END;
			browseButtonsComposite.setLayoutData(gd);
			
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.marginBottom = 5;
			browseButtonsComposite.setLayout(layout);
			
			browseWorkspaceButton = new Button(browseButtonsComposite, SWT.PUSH);
			browseWorkspaceButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_WORKSPACE);
			browseWorkspaceButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					String value = invokeWorkspaceFileSelectionDialog();
					if(value != null) {
						catalogLocationField.setText(value);
					}
				}
			});
			
			browseFileSystemButton = new Button(browseButtonsComposite, SWT.PUSH);
			browseFileSystemButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_FILE_SYSTEM);
			browseFileSystemButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					String value = invokeFileSelectionDialog();
					if(value != null) {
						catalogLocationField.setText(value);
					}
				}
			});

			errorMessageLabel = new Label(group, SWT.NONE);
			errorMessageLabel.setForeground(color);
			errorMessageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			updateWidgets(null);
			return composite;
		}

		public ICatalogElement getData() {
			return getNextCatalog();
		}

		protected INextCatalog getNextCatalog() {
			if (nextCatalog == null) {
				if ((fCatalogElement != null) && (fCatalogElement.getType() == ICatalogElement.TYPE_NEXT_CATALOG)) {
					nextCatalog = (INextCatalog) fCatalogElement;
				}
				else {
					if (catalog != null) {
						nextCatalog = (INextCatalog) catalog.createCatalogElement(ICatalogElement.TYPE_NEXT_CATALOG);
					}
				}
			}
			return nextCatalog;
		}

		public void saveData() {
			getNextCatalog().setCatalogLocation(URIUtils.convertLocationToURI(catalogLocationField.getText()));
			dataSaved = true;
		}

		protected void updateWidgets(Widget widget) {
			computeErrorMessage();
			updateErrorMessageLabel(errorMessageLabel);
			updateOKButtonState();
		}
	}

	protected class ToolBarItemSelectionChangeListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}

		public void widgetSelected(SelectionEvent e) {
			Object selection = e.getSource();
			if (selection instanceof ToolItem) {
				if (!showPage((CatalogElementPage) ((ToolItem) selection).getData())) {
					// Page flipping wasn't successful
					// handleError();
				}
			}
		}

	}

	public static String[] createStringArray(List list) {
		String[] stringArray = new String[list.size()];
		for (int i = 0; i < stringArray.length; i++) {
			stringArray[i] = (String) list.get(i);
		}
		return stringArray;
	}

	public static String removeLeadingSlash(String uri) {
		// remove leading slash from the value to avoid the whole leading
		// slash
		// ambiguity problem
		//       
		if (uri != null) {
			while (uri.startsWith("/") || uri.startsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
				uri = uri.substring(1);
			}
		}
		return uri;
	}

	protected ICatalog catalog;

	protected ICatalogElement fCatalogElement;

	protected String errorMessage;

	protected String warningMessage;

	protected Button okButton;

	protected PageBook pageContainer;

	protected CatalogElementPage selectedPage;

	// protected TreeViewer treeViewer;

	protected ToolBar toolBar;

	protected Color color;
	protected boolean dataSaved;

	public EditCatalogEntryDialog(Shell parentShell, ICatalog aCatalog) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.catalog = aCatalog;
	}

	public EditCatalogEntryDialog(Shell parentShell, ICatalogElement catalogElement, ICatalog aCatalog) {
		this(parentShell, aCatalog);
		this.fCatalogElement = catalogElement;
		// TODO EB: fix his
		// entry.setURI(URIHelper.removePlatformResourceProtocol(entry.getURI()));
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			selectedPage.saveData();
			if (!dataSaved) {				
				// do not exit edit dialog
				return;
			}
		}
		super.buttonPressed(buttonId);
	}



	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		updateOKButtonState();
	}

	protected void createCatalogEntryButton() {
		CatalogElementPage page = new CatalogEntryPage();
		page.createControl(pageContainer);
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(catalogEntryToolBarImage);
		toolItem.setText(XMLCatalogMessages.EditCatalogEntryDialog_catalogEntryLabel);
		toolItem.setData(page);
		toolItem.addSelectionListener(new ToolBarItemSelectionChangeListener());
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
		color = new Color(dialogAreaComposite.getDisplay(), 200, 0, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		dialogAreaComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 500;
		gd.heightHint = 250;
		dialogAreaComposite.setLayoutData(gd);
		createMainComponent(dialogAreaComposite);
		return this.dialogArea;
	}

	public boolean close() {
		if (color != null) {
			color.dispose();
		}
		return super.close();
	}

	protected Composite createMainComponent(Composite composite) {
		if (fCatalogElement != null) // "edit" action
		{
			Composite composite1 = new Composite(composite, SWT.NONE);
			GridData data = new GridData(GridData.FILL_BOTH);
			composite1.setLayoutData(data);
			GridLayout layout = new GridLayout();
			composite1.setLayout(layout);

			pageContainer = new PageBook(composite1, SWT.NONE);
			pageContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

			if (fCatalogElement.getType() == ICatalogElement.TYPE_ENTRY) {
				CatalogElementPage entryPage = new CatalogEntryPage();
				entryPage.createControl(pageContainer);
				showPage(entryPage);
			}
			else if (fCatalogElement.getType() == ICatalogElement.TYPE_NEXT_CATALOG) {
				CatalogElementPage nextCatalogPage = new NextCatalogPage();
				nextCatalogPage.createControl(pageContainer);
				showPage(nextCatalogPage);
			}

			return composite1;
		}
		return createMainComponentWithToolbar(composite);

	}

	protected Composite createMainComponentWithToolbar(Composite composite) {

		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		composite.setLayout(formLayout);

		Label label = new Label(composite, SWT.NONE);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		label.setLayoutData(data);

		toolBar = new ToolBar(composite, SWT.BORDER | SWT.FLAT | SWT.VERTICAL);

		data = new FormData();
		data.top = new FormAttachment(label, 0);
		data.left = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		// data.height = 250;
		// data.width = 50;
		toolBar.setLayoutData(data);

		Composite composite1 = new Composite(composite, SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(label, 0);
		data.left = new FormAttachment(toolBar, 0, SWT.DEFAULT);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		composite1.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite1.setLayout(layout);

		// createPageBookPanel(composite1);
		pageContainer = new PageBook(composite1, SWT.NONE);
		pageContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

		// add pages for each type of catalog element
		createCatalogEntryButton();
		createNextCatalogButton();
		if (toolBar.getItemCount() > 0) {
			ToolItem item = toolBar.getItem(0);
			showPage((CatalogElementPage) (item.getData()));
		}
		return composite1;
	}

	protected void createNextCatalogButton() {
		CatalogElementPage page = new NextCatalogPage();
		page.createControl(pageContainer);
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(nextCatalogToolBarImage);
		toolItem.setText(XMLCatalogMessages.EditCatalogEntryDialog_nextCatalogLabel);
		toolItem.setData(page);
		toolItem.addSelectionListener(new ToolBarItemSelectionChangeListener());

	}

	protected ICatalogElement getCatalogElement() {
		return fCatalogElement;
	}

	protected String getDisplayValue(String string) {
		return string != null ? string : ""; //$NON-NLS-1$
	}

	protected XMLCatalogFileType getMatchingFileType(String description) {
		XMLCatalogFileType fileType = null;
		for (Iterator i = CatalogFileTypeRegistryReader.getXMLCatalogFileTypes().iterator(); i.hasNext();) {
			XMLCatalogFileType theFileType = (XMLCatalogFileType) i.next();
			if ((theFileType.description != null) && theFileType.description.equals(description)) {
				fileType = theFileType;
			}
		}
		return fileType;
	}

	protected boolean showPage(CatalogElementPage page) {
		if (pageContainer.isDisposed()) {
			return false;
		}
		selectedPage = page;
		pageContainer.setVisible(true);
		pageContainer.showPage(selectedPage.getControl());
		fCatalogElement = selectedPage.getData();
		return true;
	}

	protected void updateErrorMessageLabel(Label errorMessageLabel) {
		if (errorMessage != null) {
			errorMessageLabel.setText(errorMessage);
		}
		else if (warningMessage != null) {
			errorMessageLabel.setText(warningMessage);
		}
		else {
			errorMessageLabel.setText("");
		}
	}

	protected void updateOKButtonState() {
		if (okButton != null) {
			okButton.setEnabled(errorMessage == null);
		}
	}

	protected Button createBrowseButton(Composite composite) {
		Button browseButton = new Button(composite, SWT.PUSH);
		// browseButton.setText(XMLCatalogMessages.
		// UI_BUTTON_BROWSE"));
		browseButton.setImage(borwseImage);
		Rectangle r = borwseImage.getBounds();
		GridData gd = new GridData();
		int IMAGE_WIDTH_MARGIN = 6;
		int IMAGE_HEIGHT_MARGIN = 6;
		gd.heightHint = r.height + IMAGE_HEIGHT_MARGIN;
		gd.widthHint = r.width + IMAGE_WIDTH_MARGIN;
		browseButton.setLayoutData(gd);

		return browseButton;

	}
	
	protected Button createWorkspaceBrowseButton(Composite composite) {
		Button browseWorkspaceButton = new Button(composite, SWT.PUSH);
		browseWorkspaceButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_WORKSPACE);
		return browseWorkspaceButton;
	}
	
	protected Button createFileSystemBrowseButton(Composite composite) {
		Button browseFileSystemButton = new Button(composite, SWT.PUSH);
		browseFileSystemButton.setText(XMLCatalogMessages.UI_BUTTON_MENU_BROWSE_WORKSPACE);
		return browseFileSystemButton;
	}

	
	String invokeWorkspaceFileSelectionDialog() {
		FilterableSelectSingleFileDialog dialog = new FilterableSelectSingleFileDialog(getShell());
		dialog.createAndOpen();
		IFile file = dialog.getFile();
		String uri = null;
		if (file != null) {
			// remove leading slash from the value to avoid the
			// whole leading slash ambiguity problem
			//                    
			uri = file.getFullPath().toString();
			while (uri.startsWith("/") || uri.startsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
				uri = uri.substring(1);
			}
		}
		return uri;
	}

	String invokeFileSelectionDialog() {
		FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
		return dialog.open();
	}

}
