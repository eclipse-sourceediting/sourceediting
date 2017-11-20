/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.List;

import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Text;

public class AddXSDModelGroupDefinitionCommand extends BaseCommand
{
  XSDConcreteComponent parent;
  boolean isReference;

  public AddXSDModelGroupDefinitionCommand(String label, XSDConcreteComponent parent, boolean isReference)
  {
    super(label);
    this.parent = parent;
    this.isReference = isReference;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.gef.commands.Command#execute()
   */
  public void execute()
  {
    if (parent instanceof XSDSchema)
    {
      ensureSchemaElement((XSDSchema)parent);
    }
    try
    {
      beginRecording(parent.getElement());
      if (!isReference)
      {
        XSDModelGroupDefinition def = createXSDModelGroupDefinition();
        addedXSDConcreteComponent = def;
      }
      else
      {
        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
        XSDModelGroupDefinition def = factory.createXSDModelGroupDefinition();
        XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
        particle.setContent(def);
        List list = parent.getSchema().getModelGroupDefinitions();
        int length = list.size();
        if (length > 1)
        {
          for (int i = 0; i < length; i++)
          {
            XSDModelGroupDefinition gr = (XSDModelGroupDefinition) list.get(i);
            if (gr.getModelGroup() != parent)
            {
              def.setResolvedModelGroupDefinition(gr);
            }
          }
        }
        else if (length <= 1)
        {
          XSDModelGroupDefinition newGroupDef = createXSDModelGroupDefinition();
          def.setResolvedModelGroupDefinition(newGroupDef);
        }

        if (parent instanceof XSDModelGroup)
        {
          ((XSDModelGroup) parent).getContents().add(particle);
        }
        formatChild(def.getElement());
        addedXSDConcreteComponent = def;
      }
    }
    finally
    {
      endRecording();
    }
  }

  protected XSDModelGroupDefinition createXSDModelGroupDefinition()
  {
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDModelGroupDefinition def = factory.createXSDModelGroupDefinition();
    List list = parent.getSchema().getModelGroupDefinitions();
    String newName = XSDCommonUIUtils.createUniqueElementName("NewGroupDefinition", list); //$NON-NLS-1$
    def.setName(newName);

    XSDModelGroup modelGroup = createModelGroup();
    def.setModelGroup(modelGroup);
    Text textNode = parent.getSchema().getDocument().createTextNode("\n"); //$NON-NLS-1$
    parent.getSchema().getElement().appendChild(textNode);
    parent.getSchema().getContents().add(def);
    formatChild(def.getElement());
    return def;
  }

  protected XSDModelGroup createModelGroup()
  {
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDParticle particle = factory.createXSDParticle();
    XSDModelGroup modelGroup = factory.createXSDModelGroup();
    modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
    particle.setContent(modelGroup);

    return modelGroup;
  }

}
