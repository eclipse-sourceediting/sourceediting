/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.StringTokenizer;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleAddExtensionCommand;

public class SpecificationForExtensionsSchema
{
  private String description;
  private String displayName;
  private String namespaceURI;
  private String location;
  private ILabelProvider labelProvider;
  private boolean isDefaultSchema = false;
  private ExtensibleAddExtensionCommand addActionCommand;
  
  /**
   * Either the workspace-relative path of the xsd file or the namespace
   * of the xsd file (if it come from the Catalog) 
   */
  private String sourceHint;
  private boolean fromCatalog;

  public SpecificationForExtensionsSchema()
  {
    super();
  }

  public SpecificationForExtensionsSchema(String desc) {
	StringTokenizer tokenizer = new StringTokenizer(desc, "\t");
	
	// we must be sure that each 'desc' contains info in correct format
	// no error checking here
	description = tokenizer.nextToken();
	displayName = tokenizer.nextToken();
	namespaceURI = tokenizer.nextToken();
	location = tokenizer.nextToken();
	isDefaultSchema = tokenizer.nextToken().equals("true");
	sourceHint = tokenizer.nextToken();
	fromCatalog = tokenizer.nextToken().equals("true");
  }

/**
   * @return Returns the description.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @param description
   *          The description to set.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * @return Returns the displayName.
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * @param name
   *          The displayName to set.
   */
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  /**
   * @return Returns the namespaceURI.
   */
  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  /**
   * @param namespaceURI
   *          The namespaceURI to set.
   */
  public void setNamespaceURI(String namespaceURI)
  {
    this.namespaceURI = namespaceURI;
  }

  /**
   * @return Returns the location of the xsd file.
   */
  public String getLocation()
  {
    return location;
  }

  /**
   * @param location
   *          The location to be set
   */
  public void setLocation(String location)
  {
    this.location = location;
  }

  public ILabelProvider getLabelProvider()
  {
    return labelProvider;
  }
  
  public void setLabelProvider(ILabelProvider labelProvider)
  {
    this.labelProvider = labelProvider;
  }

  public ExtensibleAddExtensionCommand getExtensibleAddExtensionCommand()
  {
    return addActionCommand;
  }

  public void setExtensibleAddExtensionCommand(ExtensibleAddExtensionCommand addActionCommand)
  {
    this.addActionCommand = addActionCommand;
  }

  public boolean isDefautSchema(){
	  return isDefaultSchema ;
  }

  public void setDefautSchema(){
	  isDefaultSchema = true;
  }
  
  public void setSourceHint(String s){
	  sourceHint = s;
  }
  
  public String getSourceHint(){
	  return sourceHint;
  }

  public boolean isFromCatalog() {
	return fromCatalog;
  }

  public void setFromCatalog(boolean fromCatalog) {
	this.fromCatalog = fromCatalog;
  }
}
