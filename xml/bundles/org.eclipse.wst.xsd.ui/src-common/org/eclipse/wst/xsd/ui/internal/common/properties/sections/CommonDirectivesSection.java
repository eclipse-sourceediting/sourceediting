/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;

public abstract class CommonDirectivesSection extends AbstractSection
{
  Text schemaLocationText;
  Button wizardButton;
  StyledText errorText;
  Color red;
  protected boolean isValidSchemaLocation = true;

  // TODO: common up code with XSDSelectIncludeFileWizard
  public void doHandleEvent(Event event)
  {
    errorText.setText(""); //$NON-NLS-1$

    if (event.widget == schemaLocationText)
    {
        String errorMessage = ""; //$NON-NLS-1$
        isValidSchemaLocation = true;
        String xsdModelFile = schemaLocationText.getText();
        String namespace = ""; //$NON-NLS-1$
        XSDSchema externalSchema = null;
        
        if (xsdModelFile.length() == 0)
        {
          handleSchemaLocationChange(xsdModelFile, "", null); //$NON-NLS-1$
          return;
        }

        try
        {
          IFile currentIFile = null;
          IEditorInput editorInput = getActiveEditor().getEditorInput();
          if (editorInput instanceof IFileEditorInput)
          {
            currentIFile = ((IFileEditorInput)editorInput).getFile();
          }
          
          URI newURI = URI.createURI(xsdModelFile);
          String xsdFile = URIHelper.getRelativeURI(newURI.toString(), currentIFile.getFullPath().toString());
          final String normalizedXSDFile = URIHelper.normalize(xsdFile, currentIFile.getLocation().toString(), ""); //$NON-NLS-1$
          final String normalizedURI = URI.encodeFragment(URIHelper.addImpliedFileProtocol(normalizedXSDFile), true).toString();
          XSDParser parser = new XSDParser(new HashMap());
          parser.parse(normalizedURI);
          externalSchema = parser.getSchema();

          if (externalSchema != null)
          {
            String extNamespace = externalSchema.getTargetNamespace();
            if (extNamespace == null) extNamespace = ""; //$NON-NLS-1$
            namespace = extNamespace;
            
            if (externalSchema.getDiagnostics() != null &&
                externalSchema.getDiagnostics().size() > 0)
            {
              isValidSchemaLocation = false;
              errorMessage = XSDEditorPlugin.getResourceString("_UI_INCORRECT_XML_SCHEMA", xsdModelFile); //$NON-NLS-1$
            }  
            else
            {
              String currentNameSpace = xsdSchema.getTargetNamespace();
              if (input instanceof XSDInclude || input instanceof XSDRedefine)
              {  
                // Check the namespace to make sure they are the same as current file
                if (extNamespace != null)
                {
                  if (currentNameSpace != null && !extNamespace.equals(currentNameSpace))
                  {
                    errorMessage = XSDEditorPlugin.getResourceString("_UI_DIFFERENT_NAME_SPACE", xsdModelFile); //$NON-NLS-1$
                    isValidSchemaLocation = false;
                  }
                }
              }
              else
              {  
                // Check the namespace to make sure they are different from the current file
                if (extNamespace != null)
                {
                  if (currentNameSpace != null && extNamespace.equals(currentNameSpace))
                  {
                    errorMessage = XSDEditorPlugin.getResourceString("_UI_SAME_NAME_SPACE", xsdModelFile); //$NON-NLS-1$
                    isValidSchemaLocation = false;
                  }
                }
              }
            }
          }
          else
          {
            errorMessage = Messages._UI_ERROR_INVALID_FILE;
            isValidSchemaLocation = false;
          }
        }
        catch(Exception e)
        {
          errorMessage = Messages._UI_ERROR_INVALID_FILE;
          isValidSchemaLocation = false;
        }
        finally
        {
          if (!isValidSchemaLocation)
          {
            errorText.setText(errorMessage);
            int length = errorText.getText().length();
            red = new Color(null, 255, 0, 0);
            StyleRange style = new StyleRange(0, length, red, schemaLocationText.getBackground());
            errorText.setStyleRange(style);
          }
          else
          {
            handleSchemaLocationChange(xsdModelFile, namespace, externalSchema);          
          }
        }
      }
  }
  
  protected void handleSchemaLocationChange(String schemaFileString, String namespace, XSDSchema externalSchema)
  {
    
  }

  
  public void dispose()
  {
    super.dispose();
    if (red != null)
    {
      red.dispose();
      red = null;
    }
  }

}
