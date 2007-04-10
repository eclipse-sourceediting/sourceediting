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

package org.eclipse.wst.xml.core.internal.validation;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;

/**
 * An interface represention a validation report for XML validation.
 * 
 * @author Lawrence Mandel, IBM
 */
public interface XMLValidationReport extends ValidationReport
{
  
  /**
   * Returns whether a grammar was encountered during the validation.
   * 
   * @return True if a grammar was encountered, false otherwise.
   */
  public boolean isGrammarEncountered();
  
  /**
   * Returns whether a namespace was encountered.
   * 
   * @return True if a namespace was encountered, false otherwise.
   */
  public boolean isNamespaceEncountered();
  
  /**
   * Returns whether a DTD without element declarations was encountered.
   * 
   * @return True if a DTD without element declarations was encountered.
   */
  public boolean isDTDWithoutElementDeclarationEncountered();
}
