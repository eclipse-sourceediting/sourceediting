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
package org.eclipse.wst.xsd.core.internal.validation.eclipse;

/**
 * A wrapper class for the XSDMessageInfoHelper class for testing.
 */
public class XSDMessageInfoHelperWrapper extends XSDMessageInfoHelper 
{
  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.core.internal.validation.eclipse.XSDMessageInfoHelper#getFirstStringBetweenSingleQuotes(java.lang.String)
   */
  public String getFirstStringBetweenSingleQuotes(String s) 
  {
	return super.getFirstStringBetweenSingleQuotes(s);
  }
}
