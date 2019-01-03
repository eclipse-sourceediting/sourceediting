/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.facade;

import org.eclipse.gef.commands.Command;

public interface IType extends IADTObject
{ 
  IType getSuperType();
  String getName();
  String getQualifier();
  boolean isComplexType();
  boolean isAnonymous();
  
  Command getUpdateNameCommand(String newName);
}
