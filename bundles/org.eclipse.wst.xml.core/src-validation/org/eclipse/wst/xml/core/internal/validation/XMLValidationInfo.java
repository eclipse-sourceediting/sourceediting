/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation;

import java.util.Stack;

import org.apache.xerces.xni.XMLLocator;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorCustomizationManager;


/**
 * A validation information object specific to XML validators.
 */
public class XMLValidationInfo extends ValidationInfo implements XMLValidationReport
{
  protected boolean grammarEncountered = false;
  protected boolean dtdEncountered = false;
  protected boolean namespaceEncountered = false;
  protected int elementDeclCount = 0;
  protected String currentErrorKey;
  protected Object messageArguments[] = null;
  protected XMLLocator locator = null;
  protected ErrorCustomizationManager errorCustomizationManager = null;
  
  /**
   * A stack of start tag locations, used to move errors
   * reported at the close tag to be reported at the start tag.
   */
  protected Stack startElementLocations = new Stack();
  
  /**
   * Constructor.
   * 
   * @param uri The URI of the file this report describes.
   */
  public XMLValidationInfo(String uri)
  {
    super(uri);
  }
  
  public boolean isGrammarEncountered()
  {
    return grammarEncountered;
  }
  
  /**
   * Set whether a grammar has been encountered or not.
   * 
   * @param grammarEncountered Set true if a grammar has been encountered, false otherwise.
   */
  public void setGrammarEncountered(boolean grammarEncountered)
  {
    this.grammarEncountered = grammarEncountered;
  }
  
  public boolean isDTDWithoutElementDeclarationEncountered()
  {
    return dtdEncountered && elementDeclCount == 0;
  }
  
  /**
   * Set whether a DTD without an element declaration was encountered.
   * 
   * @param dtdWithoutElementDeclarationEncountered Set true if a DTD without an
   *         element declaration was encountered, false otherwise.
   */
  public void setDTDEncountered(boolean dtdEncountered)
  {
    this.dtdEncountered = dtdEncountered;
  }
  
  public boolean isNamespaceEncountered()
  {
    return namespaceEncountered;
  }
  
  /**
   * Set whether a namespace was encountered.
   * 
   * @param namespaceEncountered Set true if a namespace was encountered, false otherwise.
   */
  public void setNamespaceEncountered(boolean namespaceEncountered)
  {
    this.namespaceEncountered = namespaceEncountered;
  }
  
  /**
   * Increase the element declaration count for DTD elements by one.
   */
  public void increaseElementDeclarationCount()
  {
    this.elementDeclCount++;
  }
  
  /**
   * Set the number of DTD elements encountered.
   * 
   * @param count The number of DTD elements encountered.
   */
  public void setElementDeclarationCount(int count)
  {
  	elementDeclCount = count;
  }
  
  /**
   * Get the XML locator if one has been specified.
   * 
   * @return The XML locator if one has been specified or null.
   */
  public XMLLocator getXMLLocator()
  {
    return locator;
  }
  
  /**
   * Set the XMLLocator.
   * 
   * @param locator The XMLLocator to set.
   */
  public void setXMLLocator(XMLLocator locator)
  {
    this.locator = locator;
  }
  
  /**
   * Get the current error key.
   * 
   * @return Returns the currentErrorKey.
   */
  public String getCurrentErrorKey()
  {
    return currentErrorKey;
  }
  
  /**
   * Set the current error key.
   * 
   * @param currentErrorKey The currentErrorKey to set.
   */
  public void setCurrentErrorKey(String currentErrorKey)
  {
    this.currentErrorKey = currentErrorKey;
  }

public Object[] getMessageArguments() {
	return messageArguments;
}


public void setMessageArguments(Object[] messageArguments) {
	this.messageArguments = messageArguments;
}

  /**
   * Get the start elements locations.
   * 
   * @return 
   * 		A stack containing the start element locations.
   */
  protected Stack getStartElementLocations()
  {
    return startElementLocations;
  }
  
  /**
   * Get the error customization manager for this validation run.
   * 
   * @return
   * 	The error customization manager for this validation run.
   */
  protected ErrorCustomizationManager getErrorCustomizationManager()
  {
	  if(errorCustomizationManager == null)
	  {
		  errorCustomizationManager = new ErrorCustomizationManager();
	  }
	  return errorCustomizationManager;
  }
  
  /**
   * @deprecated Use {@link XMLValidationConfiguration} instead.
   */
  public boolean isUseXInclude() {
	  return XMLCorePlugin.getDefault().getPluginPreferences().getBoolean(XMLCorePreferenceNames.USE_XINCLUDE);
  }
}