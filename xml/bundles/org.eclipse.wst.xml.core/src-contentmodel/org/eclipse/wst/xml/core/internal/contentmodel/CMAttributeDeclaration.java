/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel;

import java.util.Enumeration;

/**
 * AttributeDeclaration interface
 */
public interface CMAttributeDeclaration extends CMNode {
  public static final int OPTIONAL   = 1;
  public static final int REQUIRED   = 2;
  public static final int FIXED      = 3;
  public static final int PROHIBITED = 4;
/**
 * getAttrName method
 * @return java.lang.String
 */
String getAttrName();
/**
 * getAttrType method
 * @return CMDataType
 */
CMDataType getAttrType();
/**
 * getDefaultValue method
 * @return java.lang.String
 * @deprecated -- to be replaced in future with additional CMDataType methods (currently found on CMDataTypeHelper)
 */
String getDefaultValue();
/**
 * getEnumAttr method
 * @return java.util.Enumeration
 * @deprecated -- to be replaced in future with additional CMDataType methods (currently found on CMDataTypeHelper)
 */
Enumeration getEnumAttr();
/**
 * getUsage method
 * @return int
 */
int getUsage();
}
