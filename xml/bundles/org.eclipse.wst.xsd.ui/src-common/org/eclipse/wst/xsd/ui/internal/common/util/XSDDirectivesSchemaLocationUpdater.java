/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.viewers.ResourceFilter;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.wizards.XSDSelectIncludeFileWizard;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.w3c.dom.Element;

public class XSDDirectivesSchemaLocationUpdater
{
  /**
   * Modifies the schema location by opening the schema location dialog and
   * processing the results. This method refactors the code in
   * XSDImportSection$widgetSelected and SchemaLocationSection$widgetSelected
   * and the processing in handleSchemaLocationChange()
   */
  public static void updateSchemaLocation(XSDSchema xsdSchema, Object selection, boolean isInclude)
  {
    Shell shell = Display.getCurrent().getActiveShell();
    IFile currentIFile = null;
    IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
    ViewerFilter filter;

    if (editorInput instanceof IFileEditorInput)
    {
      currentIFile = ((IFileEditorInput) editorInput).getFile();
      filter = new ResourceFilter(new String[] { ".xsd" }, //$NON-NLS-1$ 
          new IFile[] { currentIFile }, null);
    }
    else
    {
      filter = new ResourceFilter(new String[] { ".xsd" }, //$NON-NLS-1$ 
          null, null);
    }

    XSDSelectIncludeFileWizard fileSelectWizard = new XSDSelectIncludeFileWizard(xsdSchema, isInclude, XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_SCHEMA"), //$NON-NLS-1$
        XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_DESC"), //$NON-NLS-1$
        filter, new StructuredSelection(selection));

    WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
    wizardDialog.create();
    wizardDialog.setBlockOnOpen(true);
    int result = wizardDialog.open();

    if (result == Window.OK)
    {
      IFile selectedIFile = fileSelectWizard.getResultFile();
      String schemaFileString;
      if (selectedIFile != null && currentIFile != null)
      {
        schemaFileString = URIHelper.getRelativeURI(selectedIFile.getLocation(), currentIFile.getLocation());
      }
      else if (selectedIFile != null && currentIFile == null)
      {
        schemaFileString = selectedIFile.getLocationURI().toString();
      }
      else
      {
        schemaFileString = fileSelectWizard.getURL();
      }

      String attributeSchemaLocation = "schemaLocation"; //$NON-NLS-1$
      if (selection instanceof XSDImport)
      {
        XSDImport xsdImport = (XSDImport) selection;
        xsdImport.getElement().setAttribute(attributeSchemaLocation, schemaFileString); //$NON-NLS-1$

        String namespace = fileSelectWizard.getNamespace();
        if (namespace == null)
          namespace = ""; //$NON-NLS-1$

        XSDSchema externalSchema = fileSelectWizard.getExternalSchema();
        java.util.Map map = xsdSchema.getQNamePrefixToNamespaceMap();
        Element schemaElement = xsdSchema.getElement();

        // update the xmlns in the schema element first, and then update the
        // import element next so that the last change will be in the import element. This keeps the
        // selection on the import element
        TypesHelper helper = new TypesHelper(externalSchema);
        String prefix = helper.getPrefix(namespace, false);

        if (map.containsKey(prefix))
        {
          prefix = null;
        }

        if (prefix == null || (prefix != null && prefix.length() == 0))
        {
          StringBuffer newPrefix = new StringBuffer("pref"); //$NON-NLS-1$
          int prefixExtension = 1;
          while (map.containsKey(newPrefix.toString()) && prefixExtension < 100)
          {
            newPrefix = new StringBuffer("pref" + String.valueOf(prefixExtension)); //$NON-NLS-1$
            prefixExtension++;
          }
          prefix = newPrefix.toString();
        }

        String attributeNamespace = "namespace"; //$NON-NLS-1$
        if (namespace.length() > 0)
        {
          // if ns already in map, use its corresponding prefix
          if (map.containsValue(namespace))
          {
            TypesHelper typesHelper = new TypesHelper(xsdSchema);
            prefix = typesHelper.getPrefix(namespace, false);
          }
          else
          // otherwise add to the map
          {
            schemaElement.setAttribute("xmlns:" + prefix, namespace); //$NON-NLS-1$
          }
          // prefixText.setText(prefix);
          xsdImport.getElement().setAttribute(attributeNamespace, namespace);
        }

        // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=155885
        // Need to import otherwise the external schema is never
        // resolved. One problem is that the schema location is still null,
        // so the set types dialog will show types that belong to that schema
        // with a null schema location. This should load resource
        // into the resource set
        if (selection instanceof XSDImportImpl) // redundant
        {
          XSDImportImpl xsdImportImpl = (XSDImportImpl) selection;
          xsdImportImpl.importSchema();
        }

      }
      else if (selection instanceof XSDInclude)
      {
        XSDInclude xsdInclude = (XSDInclude) selection;
        xsdInclude.getElement().setAttribute(attributeSchemaLocation, schemaFileString);
      }
      else if (selection instanceof XSDRedefine)
      {
        XSDRedefine xsdRedefine = (XSDRedefine) selection;
        xsdRedefine.getElement().setAttribute(attributeSchemaLocation, schemaFileString);
      }
    }
  }
}
