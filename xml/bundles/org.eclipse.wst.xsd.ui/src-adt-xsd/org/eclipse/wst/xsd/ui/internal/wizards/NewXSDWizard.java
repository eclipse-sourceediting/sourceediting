/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.wizards;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;


public class NewXSDWizard extends Wizard implements INewWizard {
	private XSDNewFilePage newFilePage;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public NewXSDWizard() {
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		this.selection = aSelection;
		this.workbench = aWorkbench;

		this.setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/NewXSD.png"));
		this.setWindowTitle(XSDEditorPlugin.getXSDString("_UI_WIZARD_CREATE_XSD_MODEL_TITLE"));
	}

	public void addPages() {
		newFilePage = new XSDNewFilePage(selection);
		addPage(newFilePage);
	}

	public boolean performFinish() {
		IFile file = newFilePage.createNewFile();

		//
		// Get the xsd schema name from the full path name
		// e.g. f:/b2b/po.xsd => schema name = po
		//
		IPath iPath = file.getFullPath().removeFileExtension();
		// String schemaName = iPath.lastSegment();
		String schemaName = iPath.lastSegment();
		String schemaPrefix = "tns";
		String prefixForSchemaNamespace = "";
		String schemaNamespaceAttribute = "xmlns";
		if (XSDEditorPlugin.getPlugin().isQualifyXMLSchemaLanguage()) {
			// Added this if check before disallowing blank prefixes in the
			// preferences...
			// Can take this out. See also XSDEditor
			if (XSDEditorPlugin.getPlugin().getXMLSchemaPrefix().trim().length() > 0) {
				prefixForSchemaNamespace = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix() + ":";
				schemaNamespaceAttribute += ":" + XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
			}
		}

		Preferences preference = XMLCorePlugin.getDefault().getPluginPreferences();
		String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
		if (charSet == null || charSet.trim().equals("")) {
			charSet = "UTF-8";
		}

		String newContents = "<?xml version=\"1.0\" encoding=\"" + charSet + "\"?>\n";

		String defaultTargetURI = XSDEditorPlugin.getPlugin().getXMLSchemaTargetNamespace();
		newContents += "<" + prefixForSchemaNamespace + "schema " + schemaNamespaceAttribute + "=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"" + defaultTargetURI + schemaName + "\" xmlns:" + schemaPrefix + "=\"" + defaultTargetURI + schemaName + "\" elementFormDefault=\"qualified\">\n</" + prefixForSchemaNamespace + "schema>";

		try {
			byte[] bytes = newContents.getBytes(charSet);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

			file.setContents(inputStream, true, false, null);
			inputStream.close();
		}
		catch (Exception e) {
			// XSDEditorPlugin.getPlugin().getMsgLogger().write("Error writing
			// default content:\n" + newContents);
			// XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
		}

		if (file != null) {
			revealSelection(new StructuredSelection(file));
		}

		openEditor(file);

		return true;
	}

	private void revealSelection(final ISelection selection) {
		if (selection != null) {
			IWorkbench workbench2;
			if (workbench == null)
			{
			  workbench2 = XSDEditorPlugin.getPlugin().getWorkbench();
			}
			else
			{
			  workbench2 = workbench;
			}
			final IWorkbenchWindow workbenchWindow = workbench2.getActiveWorkbenchWindow();
			final IWorkbenchPart focusPart = workbenchWindow.getActivePage().getActivePart();
			if (focusPart instanceof ISetSelectionTarget) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						((ISetSelectionTarget) focusPart).selectReveal(selection);
					}
				});
			}
		}
	}

	public void openEditor(final IFile iFile) {
		if (iFile != null) {
			IWorkbench workbench2;
			if (workbench == null)
			{
			  workbench2 = XSDEditorPlugin.getPlugin().getWorkbench();
			}
			else
			{
			  workbench2 = workbench;
			}
			final IWorkbenchWindow workbenchWindow = workbench2.getActiveWorkbenchWindow();

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						String editorId = null;
						IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(iFile.getLocation().toOSString(), iFile.getContentDescription().getContentType());
						if (editor != null) {
							editorId = editor.getId();
						}
						workbenchWindow.getActivePage().openEditor(new FileEditorInput(iFile), editorId);
						
					}
					catch (PartInitException ex) {
					}
					catch (CoreException ex) {
					}
				}
			});
		}
	}

}
