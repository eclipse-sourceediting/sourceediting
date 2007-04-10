/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMValidator;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMValidator.ElementPathRecordingResult;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMValidator.StringElementContentComparator;

public class CMValidatorTest
{
  public static void main(String arg[])
  {
    if (arg.length > 1)
    {
      try
      {
        //CMDocumentFactoryRegistry.getInstance().registerCMDocumentBuilderWithClassName("org.eclipse.wst.xml.core.internal.contentmodel.mofimpl.CMDocumentBuilderImpl");

        String grammarFileName = arg[0];
        String elementName = arg[1];

        CMDocument cmDocument = ContentModelManager.getInstance().createCMDocument(grammarFileName, null);
        
        CMNamedNodeMap elementMap = cmDocument.getElements();
        CMElementDeclaration element = (CMElementDeclaration)elementMap.getNamedItem(elementName);
        if (element != null)
        { /*
          println("found element [" + elementName + "]  contentType = " + element.getContentType());
          GraphNode graphNode = createGraph(element);
          printGraph(graphNode);
          */
          println("-------------- begin validate ---------------"); //$NON-NLS-1$

          StringElementContentComparator comparator = new StringElementContentComparator();
          CMValidator validator = new CMValidator();
          ElementPathRecordingResult result = new ElementPathRecordingResult();
          validator.getOriginArray(element, CMValidator.createStringList(arg, 2), comparator, result);
          if (result.isValid)
          {
            CMNode[] nodeMapping = result.getOriginArray();
            println("Validation Success!"); //$NON-NLS-1$
            print("  "); //$NON-NLS-1$
            for (int i = 0; i < nodeMapping.length; i++)
            {
              String name = nodeMapping[i] != null ? nodeMapping[i].getNodeName() : "null"; //$NON-NLS-1$
              print("[" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            println(""); //$NON-NLS-1$
          }
          else
          {
            println("Validation Failed! "); //$NON-NLS-1$
            if (result.errorMessage != null)
            {
              println("  " + result.errorMessage); //$NON-NLS-1$
            }
          }
          println("-------------- end validate ---------------"); //$NON-NLS-1$
        }
        else
        {
          println("element [" + elementName + "] can not be found"); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      catch (Exception e)
      {
        println("CMValidator error"); //$NON-NLS-1$
        e.printStackTrace();
      }
    }
    else
    {
      println("2 args required... only " + arg.length + " provided"); //$NON-NLS-1$ //$NON-NLS-2$
      println("usage java org.eclipse.wst.newxml.util.XMLUtil grammarFileName rootElementName pattern"); //$NON-NLS-1$
    }
  }
  
  public static void print(String string)
  {  
  }
    
  public static void println(String string)
  {  
  }
  
  public static void printlnIndented(int indent, String string)
  {
  }    
}
