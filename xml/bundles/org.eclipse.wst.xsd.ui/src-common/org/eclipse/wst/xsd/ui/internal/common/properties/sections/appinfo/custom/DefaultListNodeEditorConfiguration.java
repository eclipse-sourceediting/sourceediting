/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;


public class DefaultListNodeEditorConfiguration extends ListNodeEditorConfiguration
{
  private String[] values;
  
  public DefaultListNodeEditorConfiguration(String[] values)
  {
    this.values = values; 
  }
 
  public Object[] getValues(Object propertyObject)
  {
    return values;
  }
}
