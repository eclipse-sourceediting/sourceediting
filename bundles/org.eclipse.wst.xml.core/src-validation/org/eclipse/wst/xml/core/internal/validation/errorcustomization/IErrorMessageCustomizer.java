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
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

/**
 * An error message customizer will be called by the XML validator to customize
 * XML validation errors for a given namespace. The customizer can be used to 
 * create clearer errors for specific application domains.
 */
public interface IErrorMessageCustomizer 
{
  /**
   * Return a customized error message for the given element. Null should be returned
   * if the message should not be customized.
   * 
   * @param elementInformation
   * 		The information about the element for which to customize the error.
   * @param errorKey
   * 		The key for the error for which to replace the message.
   * @param arguments
   * 		Arguments related the the error message.
   * @return
   * 		A customized error message or null if this customizer will not customize the message.
   */
  public String customizeMessage(ElementInformation elementInfo, String errorKey, Object[] arguments);
}
