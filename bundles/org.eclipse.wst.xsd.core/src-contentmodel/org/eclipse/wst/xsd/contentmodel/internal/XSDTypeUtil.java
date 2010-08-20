/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.contentmodel.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * Helper class to generate valid values for built-in simple types.
 */

public class XSDTypeUtil
{
  protected static Map defaultValue = new HashMap();

  public static void initialize()
  {
    defaultValue.put("anySimpleType", null);
    defaultValue.put("anyType", null);
    defaultValue.put("anyURI", "http://tempuri.org");
    defaultValue.put("base64Binary", "MA==");
    defaultValue.put("boolean", "true");
    defaultValue.put("byte", "0");
    defaultValue.put("date", "2001-01-01");
    defaultValue.put("dateTime", "2001-12-31T12:00:00");
    defaultValue.put("decimal", "0.0");
    defaultValue.put("double", "0.0");
    defaultValue.put("duration", "P1D");
    defaultValue.put("ENTITY", "entity");
    defaultValue.put("ENTITIES", "entities");
    defaultValue.put("float", "0.0");
    defaultValue.put("gDay", "---01");
    defaultValue.put("gMonth", "--01--");
    defaultValue.put("gMonthDay", "--01-01");
    defaultValue.put("gYear", "2001");
    defaultValue.put("gYearMonth", "2001-01");
    defaultValue.put("hexBinary", "0F00");
    defaultValue.put("ID", null);
    defaultValue.put("IDREF", null);
    defaultValue.put("IDREFS", null);
    defaultValue.put("int", "0");
    defaultValue.put("integer", "0");
    defaultValue.put("language", "EN");
    defaultValue.put("long", "0");
    defaultValue.put("Name", "Name");
    defaultValue.put("NCName", "NCName");
    defaultValue.put("negativeInteger", "-1");
    defaultValue.put("NMTOKEN", "NMTOKEN");
    defaultValue.put("NMTOKENS", "NMTOKENS");
    defaultValue.put("nonNegativeInteger", "0");
    defaultValue.put("nonPositiveInteger", "0");
    defaultValue.put("normalizedString", null);
    defaultValue.put("NOTATION", "NOTATION");
    defaultValue.put("positiveInteger", "1");
    defaultValue.put("QName", "QName");
    defaultValue.put("short", "0");
    defaultValue.put("string", null);
    defaultValue.put("time", "12:00:00");
    defaultValue.put("token", "token");
    defaultValue.put("unsignedByte", "0");
    defaultValue.put("unsignedInt", "0");
    defaultValue.put("unsignedLong", "0");
    defaultValue.put("unsignedShort", "0");
  }


  /*
   * Returns true if the type is built-in.
   * @param type - an XSDTypeDefinition object.
   * @return true if the type is built-in.
   */
  public static boolean isBuiltIn(XSDTypeDefinition type)
  { 
    boolean result = false;
    if (type instanceof XSDSimpleTypeDefinition)
    {
      String name = type.getName();
      if (name != null)
      {
        return  defaultValue.containsKey(name); 
      }
    }
    return result;
  }


  /**
   * Returns a valid default value for the simple type.
   * @param type - a simple built-in type.
   * @return a valid default value for the simple type.
   */
  public static String getInstanceValue(XSDTypeDefinition type)
  {
    if (type != null)
    {
      if (isBuiltIn(type))
      {
        String nameID = type.getName();
        return (String)defaultValue.get(nameID);
      }
      else
      {
        XSDTypeDefinition basetype = type.getBaseType();
        if (basetype != type) return getInstanceValue(basetype);
      }
    }
    return null;
  }
}

