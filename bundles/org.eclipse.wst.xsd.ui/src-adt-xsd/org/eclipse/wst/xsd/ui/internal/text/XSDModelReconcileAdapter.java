/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;

import org.eclipse.wst.xsd.ui.internal.util.ModelReconcileAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDModelReconcileAdapter extends ModelReconcileAdapter
{
  protected XSDSchema schema;
  
  public XSDModelReconcileAdapter(Document document, XSDSchema schema)
  {
    super(document);
    this.schema = schema;
  }
  
  protected void handleNodeChanged(Node node)
  {
    XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);    
    concreteComponent.elementChanged((Element)node);    
  }  
}