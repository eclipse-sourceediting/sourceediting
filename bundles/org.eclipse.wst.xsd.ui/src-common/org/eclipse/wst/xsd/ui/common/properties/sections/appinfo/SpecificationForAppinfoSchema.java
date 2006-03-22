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
package org.eclipse.wst.xsd.ui.common.properties.sections.appinfo;

import org.eclipse.jface.viewers.ILabelProvider;

public class SpecificationForAppinfoSchema
{
  private String description;
  private String displayName;
  private String namespaceURI;
  private String location;
  private ILabelProvider labelProvider;

  public SpecificationForAppinfoSchema()
  {
    super();
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
}
