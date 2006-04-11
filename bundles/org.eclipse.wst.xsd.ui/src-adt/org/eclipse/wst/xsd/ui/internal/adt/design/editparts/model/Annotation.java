/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;

public class Annotation
{
  Compartment compartment;
  public Annotation()
  {
    super();
  }
  
  public void setCompartment(Compartment compartment)
  {
    this.compartment = compartment;
  }
  
  public Compartment getCompartment()
  {
    return compartment;
  }
  
  public IStructure getOwner()
  {
    return compartment.getOwner();
  }
}
