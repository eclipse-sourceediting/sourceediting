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
package org.eclipse.wst.xsd.ui.internal.refactor.structure;

import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTypeDefinition;

public final class MakeLocalElementGlobalCommand extends AbstractCommand
{
	
  public MakeLocalElementGlobalCommand
    (XSDConcreteComponent element)
  {
    super(element.getContainer());
    setModelObject(element);
  }
  
  public void run()
  {
    
   if(getModelObject() instanceof XSDElementDeclaration){
   
	   XSDElementDeclaration element = (XSDElementDeclaration)getModelObject();
 	XSDConcreteComponent parent = getParent();
 	XSDConcreteComponent container = parent.getContainer();
 	
 	// clone element with it's content and set it global
	XSDConcreteComponent  elementDecl = ((XSDElementDeclaration)getModelObject()).cloneConcreteComponent(true, true);
 	container.getSchema().getContents().add(elementDecl);
 	
 	// create local element and set it's reference to the global one
 	XSDElementDeclaration elementRef = 
	      XSDFactory.eINSTANCE.createXSDElementDeclaration();
	elementRef.setValue(element.getValue());
    elementRef.setResolvedElementDeclaration((XSDElementDeclaration)elementDecl); 
    
    // now set content models
 	if(parent instanceof XSDComplexTypeContent){
 		if(container instanceof XSDModelGroup){
 			XSDModelGroup modelGroup = (XSDModelGroup)container;
 			// disconnect parent from its container
 			int index = modelGroup.getContents().indexOf(parent);
 			 XSDParticle particle = 
 			      XSDFactory.eINSTANCE.createXSDParticle();
 		    particle.setContent(elementRef);
 		    modelGroup.getContents().add(index, particle); 
            // Copy over the max/minOccurs from the old local to the element ref
            if (parent instanceof XSDParticle) {
              XSDParticle parentParticle = (XSDParticle)parent;
              
              if (parentParticle.isSetMinOccurs()) {
                particle.setMinOccurs(parentParticle.getMinOccurs());
                parentParticle.unsetMinOccurs();
              }
              
              if (parentParticle.isSetMaxOccurs()) {
                particle.setMaxOccurs(parentParticle.getMaxOccurs());
                parentParticle.unsetMaxOccurs();
              }
            }          
            element.unsetForm();
 		   
 			modelGroup.getContents().remove(parent);
 		    modelGroup.updateElement(true);
  		    formatChild(modelGroup.getElement());
 		}
 	}
 	else if(parent instanceof XSDTypeDefinition){
		 		
 	}
 	
 	container.getSchema().updateElement(true);
    formatChild(elementDecl.getElement());
  
   }

  }
	/* (non-Javadoc)
	 * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#adopt(org.eclipse.xsd.XSDConcreteComponent)
	 */
	protected boolean adopt(XSDConcreteComponent model) {
		// TODO Auto-generated method stub
		return true;
	}
}
