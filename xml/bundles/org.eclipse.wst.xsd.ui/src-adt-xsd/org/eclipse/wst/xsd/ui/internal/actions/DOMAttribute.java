/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.actions;

// issue (cs) remove this
/**
 * @version 	1.0
 * @author
 */
public class DOMAttribute
{
  /**
   * Constructor for DOMAttribute.
   */
  public DOMAttribute()
  {
    super();
  }
  
  /**
   * Constructor for DOMAttribute.
   */
  public DOMAttribute(String name, String value)
  {
    super();
    this.name = name;
    this.value = value;
  }
  
  protected String name, value;
  /**
   * Gets the value.
   * @return Returns a String
   */
  public String getValue()
  {
    return value;
  }

  /**
   * Sets the value.
   * @param value The value to set
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * Gets the name.
   * @return Returns a String
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name.
   * @param name The name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }

}
