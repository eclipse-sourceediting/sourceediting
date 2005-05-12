/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.xml.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;

public class CommonDirectivesSection extends AbstractSection
{
  Text schemaLocationText;
  Button wizardButton;
  StyledText errorText;
  Color red;

  // TODO: common up code with XSDSelectIncludeFileWizard
  public void doHandleEvent(Event event)
  {
    errorText.setText("");
    if (event.type == SWT.Modify)
    {
      if (event.widget == schemaLocationText)
      {
        String errorMessage = "";
        boolean isValidSchemaLocation = true;
        String xsdModelFile = schemaLocationText.getText();
        String namespace = "";
        XSDSchema externalSchema = null;
        
        if (xsdModelFile.length() == 0)
        {
          handleSchemaLocationChange(xsdModelFile, "", null);
          return;
        }

        try
        {
          IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();

          URI newURI = URI.createURI(xsdModelFile);
          String xsdFile = URIHelper.getRelativeURI(newURI.toString(), currentIFile.getFullPath().toString());
          final String normalizedXSDFile = URIHelper.normalize(xsdFile, currentIFile.getLocation().toString(), "");
          
          XSDParser parser = new XSDParser();
          parser.parse(normalizedXSDFile);
          
          externalSchema = parser.getSchema();

          if (externalSchema != null)
          {
            String extNamespace = externalSchema.getTargetNamespace();
            if (extNamespace == null) extNamespace = "";
            namespace = extNamespace;
            
            if (externalSchema.getDiagnostics() != null &&
                externalSchema.getDiagnostics().size() > 0)
            {
              isValidSchemaLocation = false;
              errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_INCORRECT_XML_SCHEMA", xsdModelFile);
            }  
            else
            {
              String currentNameSpace = getSchema().getTargetNamespace();
              if (input instanceof XSDInclude || input instanceof XSDRedefine)
              {  
                // Check the namespace to make sure they are the same as current file
                if (extNamespace != null)
                {
                  if (currentNameSpace != null && !extNamespace.equals(currentNameSpace))
                  {
                    errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_DIFFERENT_NAME_SPACE", xsdModelFile);
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
                    errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_SAME_NAME_SPACE", xsdModelFile);
                    isValidSchemaLocation = false;
                  }
                }
              }
            }
          }
          else
          {
            errorMessage = "Invalid file";
            isValidSchemaLocation = false;
          }
        }
        catch(Exception e)
        {
          errorMessage = "Invalid file";
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
