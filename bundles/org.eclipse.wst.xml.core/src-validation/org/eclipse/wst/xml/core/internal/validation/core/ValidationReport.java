/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.core;

import java.util.HashMap;

/**
 * An interface for a validation report.
 * 
 * @author Lawrence Mandel, IBM
 */
public interface ValidationReport
{
  /**
   * Returns the URI for the file the report refers to.
   * 
   * @return The URI for the file the report refers to.
   */
  public String getFileURI();
  
  /**
   * Returns whether the file is valid. The file may have warnings associated with it.
   * 
   * @return True if the file is valid, false otherwise.
   */
  public boolean isValid();

  /**
   * Returns an array of validation messages.
   * 
   * @return An array of validation messages.
   */
  public ValidationMessage[] getValidationMessages();
  
  public HashMap getNestedMessages();

}
