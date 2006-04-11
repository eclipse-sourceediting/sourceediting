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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.actions.EditAttributeAction;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XSDActionManager extends XMLNodeActionManager {

  private CommandStack commandStack;
  
	public XSDActionManager(IStructuredModel model, Viewer viewer) {
		super(model, viewer);
	}
  
  public void setCommandStack(CommandStack commandStack) {
    this.commandStack = commandStack;
  }

  protected Action createAddCDataSectionAction(Node parent, int index)
  {
    return null;
  }
  
  protected Action createAddPCDataAction(Node parent, CMDataType dataType, int index) {
    return null;
  }
  
  
	protected void contributeAddDocumentChildActions(IMenuManager menu, Document document, int ic, int vc) {
	}
  
	protected void contributeEditGrammarInformationActions(IMenuManager menu, Node node) {
	}
	
	protected void contributePIAndCommentActions(IMenuManager menu, Document document, int index) {
	}
	
	protected void contributePIAndCommentActions(IMenuManager menu, Element parentElement, CMElementDeclaration parentEd, int index) {
	}
	
	protected void contributeTextNodeActions(IMenuManager menu, Element parentElement, CMElementDeclaration parentEd, int index) {
		super.contributeTextNodeActions(menu, parentElement, parentEd, index);
	}
  
  protected Action createAddAttributeAction(Element parent, CMAttributeDeclaration ad) {
    Action action = null;
    if (ad == null) {
      action = new EditAttributeAction(this, parent, null, XMLUIMessages._UI_MENU_NEW_ATTRIBUTE, XMLUIMessages._UI_MENU_NEW_ATTRIBUTE_TITLE); //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      action = new AddNodeAction(ad, parent, -1);
    }
    
    WrapperCommand command = new WrapperCommand(action, parent, ad);
    WrapperAction wrapperAction = new WrapperAction(command);
    return wrapperAction;
  }
  
  class WrapperAction extends Action
  {
    WrapperCommand command;
    
    public WrapperAction(WrapperCommand command)
    {
      super();
      this.command = command;
    }
    
    public String getText()
    {
      return command.getAction().getText();
    }

    public void run()
    {
      // Some editors may not use a command stack
      if (commandStack != null)
      {
        commandStack.execute(command);
      }
      else
      {
        command.execute();
      }
    }
  }

  class WrapperCommand extends Command
  {
    Action action;
    Element parent;
    CMAttributeDeclaration ad;
    public WrapperCommand(Action action, Element parent, CMAttributeDeclaration ad)
    {
      super();
      this.action = action;
      this.parent = parent;
      this.ad = ad;
    }
    
    public String getLabel()
    {
      return action.getText();
    }
    
    public Action getAction()
    {
      return action;
    }
    
    public void execute()
    {
      action.run();
    }
    
    public void undo() {
      
//      ((Element)parent).removeAttribute(ad.getAttrName());

      getModel().getUndoManager().undo();

    } 
        

  }

}
