/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class UpdateComplexTypeDerivationBy extends BaseCommand
{
  private XSDComplexTypeDefinition complexType;
  private String derivation;

  public UpdateComplexTypeDerivationBy(XSDComplexTypeDefinition complexType, String derivation)
  {
    this.complexType = complexType;
    this.derivation = derivation;
    // TODO: use new Message bundle mechanism
    setLabel(XSDEditorPlugin.getXSDString("_UI_DERIVEDBY_CHANGE"));
  }

  public void execute()
  {
    super.execute();
    try
    {
      beginRecording(complexType.getElement());
      XSDTypeDefinition originalBaseType = complexType.getBaseType();
      if (derivation.equals(XSDConstants.EXTENSION_ELEMENT_TAG))
      {
        complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
      }
      else if (derivation.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
      {
        complexType.setDerivationMethod(XSDDerivationMethod.RESTRICTION_LITERAL);
      }
      complexType.setBaseTypeDefinition(originalBaseType);
    }
    finally
    {
      endRecording();
    }
  }
}
