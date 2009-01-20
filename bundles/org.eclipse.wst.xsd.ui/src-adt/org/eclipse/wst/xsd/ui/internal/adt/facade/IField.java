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
package org.eclipse.wst.xsd.ui.internal.adt.facade;

import org.eclipse.gef.commands.Command;

public interface IField extends IADTObject
{
  String getKind();
  String getName();
  String getTypeName();
  String getTypeNameQualifier();
  IModel getModel();
  IType getType();
  IComplexType getContainerType();
  int getMinOccurs();
  int getMaxOccurs();
  boolean isGlobal();
  boolean isReference();
  boolean isAbstract();
  
  Command getUpdateMinOccursCommand(int minOccurs);
  Command getUpdateMaxOccursCommand(int maxOccurs);
  Command getUpdateTypeNameCommand(String typeName, String quailifier);
  Command getUpdateNameCommand(String name); 
  Command getDeleteCommand();  
}
