/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDAttributeDeclaration;

public class UpdateAttributeReferenceCommand extends BaseCommand
{
  XSDAttributeDeclaration attribute, ref;

  public UpdateAttributeReferenceCommand(String label, XSDAttributeDeclaration attribute, XSDAttributeDeclaration ref)
  {
    super(label);
    this.attribute = attribute;
    this.ref = ref;
  }

  public void execute()
  {
    try
    {
      beginRecording(attribute.getElement());
      attribute.setResolvedAttributeDeclaration(ref);
    }
    finally
    {
      endRecording();
    }
  }
}
