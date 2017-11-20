/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.basic;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMDataTypeImpl extends CMNodeImpl implements CMDataType
{
  protected String dataTypeName;
  protected String[] enumeratedValues;     
  protected String instanceValue;

  public CMDataTypeImpl(String dataTypeName, String instanceValue)
  {
    this.dataTypeName = dataTypeName;
    this.instanceValue = instanceValue;
    this.enumeratedValues = new String[0];
  }

  public CMDataTypeImpl(String dataTypeName, String[] enumeratedValues)
  {
    this.dataTypeName = dataTypeName;
    this.enumeratedValues = enumeratedValues;   
    this.instanceValue = enumeratedValues[0];
  }


  public int getNodeType()
  {
    return CMNode.DATA_TYPE;
  }

  public String getNodeName()
  {
    return getDataTypeName();
  }

  public String getDataTypeName()
  {
    return dataTypeName;
  }

  public int getImpliedValueKind()
  {
    return IMPLIED_VALUE_NONE;
  }

  public String getImpliedValue()
  {
    return null;
  }

  public String[] getEnumeratedValues()
  {
    return enumeratedValues;
  }

  public String generateInstanceValue()
  {
    return instanceValue;
  }
}
