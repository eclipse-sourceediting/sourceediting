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
package org.eclipse.wst.xsd.ui.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.tabletree.TreeContentHelper;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeContentProvider;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeViewer;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTreeExtension;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.properties.sections.XSDActionManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ExtensionsComponentTableTreeViewer extends XMLTableTreeViewer
{
  private CommandStack commandStack;
  
  public ExtensionsComponentTableTreeViewer(Composite parent)
  {
    super(parent);

    getTree().setLinesVisible(true);
    
    treeExtension.setCellModifier(null);
    getTree().removePaintListener(treeExtension);
    treeExtension = new MyXMLTreeExtension(getTree());
    
    AppInfoContentProvider provider = new AppInfoContentProvider();
    setContentProvider(provider);
    setLabelProvider(provider);
  }
  
  public void setCommandStack(CommandStack commandStack) {
    this.commandStack = commandStack;
  }
  
  Element asiElement;
  
  public void setASIElement(Element asiElement)
  {
    this.asiElement = asiElement;
  }

  class AppInfoContentProvider extends XMLTableTreeContentProvider
  {
    MyTreeContentHelper treeContentHelper;
    public AppInfoContentProvider()
    {
      treeContentHelper = new MyTreeContentHelper();
    }

    public Object[] getChildren(Object element)
    {
      if (element instanceof List)
      {
        return ((List) element).toArray();
      }
      else if (element instanceof Element)
      {

      }
      return treeContentHelper.getChildren(element);
      
      //return super.getChildren(element);
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      if (oldInput instanceof Element)
        oldInput = ((Element) oldInput).getOwnerDocument();

      if (newInput instanceof Element)
        newInput = ((Element) newInput).getOwnerDocument();

      super.inputChanged(viewer, oldInput, newInput);

    }
  }

  class ASIActionMenuListener implements IMenuListener
  {
    public void menuAboutToShow(IMenuManager menuManager)
    {
      // used to disable NodeSelection listening while running NodeAction
      // ASIActionManager nodeActionManager = new ASIActionManager(fModel,
      // ASITableTreeViewer.this);
      // nodeActionManager.setCommandStack(commandStack);
      IDOMModel model = null;
      try
      {
        if (asiElement instanceof ElementImpl)
        {
          model = ((ElementImpl)asiElement).getModel();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      
      // if the model is not the XML model, then we can't support the table viewer.
      if (model != null)
      {
        XSDActionManager actionManager = new XSDActionManager(model, ExtensionsComponentTableTreeViewer.this);
        actionManager.setCommandStack(commandStack);
        
        // add general actions
        ActionRegistry registry = (ActionRegistry) XSDEditorPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(ActionRegistry.class);

        if (registry != null) // an editor may not use the registry
        {
          // add undo, redo, revert
          IAction undo = registry.getAction(ActionFactory.UNDO.getId());
          if (undo != null)
          {
            menuManager.add(new Separator());
            menuManager.add(undo);
          }
          IAction redo = registry.getAction(ActionFactory.REDO.getId());
          if (redo != null)
          {
            menuManager.add(redo);
          }
          menuManager.add(new Separator());
        }
        actionManager.fillContextMenu(menuManager, getSelection());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.ibm.xmleditor.internal.tabletree.XMLTableTreeViewer#createContextMenu()
   */
  protected void createContextMenu()
  {
    MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
    contextMenu.add(new Separator("additions")); //$NON-NLS-1$
    contextMenu.setRemoveAllWhenShown(true);

    // This is the line we have to modify
    contextMenu.addMenuListener(new ASIActionMenuListener());
    Menu menu = contextMenu.createContextMenu(getControl());
    getControl().setMenu(menu);
  }

  
  
  
  
  static String EMPTY_STRING = "";  //$NON-NLS-1$
  static String XML_NS_STRING = "xmlns";  //$NON-NLS-1$
  static String APPINFO = "appinfo"; //$NON-NLS-1$
  
  public class MyTreeContentHelper extends TreeContentHelper {
    public void setNodeValue(Node node, String value) {
      String oldValue = getNodeValue(node);
      // The command stack was being populated with changes like an empty string to a null value
      // So we weed those out as well
//      if (value != null && !value.equals(oldValue) && (!(value.equals(EMPTY_STRING) && oldValue ==null))) {
//          ModifyNodeCommand command = new ModifyNodeCommand(node, value, this);
//          commandStack.execute(command);
//      }
    }
    
    // TODO - Remove this method when Bugzilla 6738 is fixed
    public List getElementTextContent(Element element) {
          List result = null;
          if (!element.hasAttributes()) {
             Node node = element.getFirstChild();

             // TODO - Hack to workaround problem that a text cell editor appears on the first
             // click and not the enumerated cell editor
             if (node == null) {
                result = new Vector();
                Text txt = element.getOwnerDocument().createTextNode("");
                element.appendChild(txt);
                result.add(txt);
             }
             // end of workaround

             for (; node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                   if (result == null) {
                      result = new Vector();
                   }
                   result.add(node);
                } else {
                   result = null;
                   break;
                }
             }
          }
          return result;
       }    
    
    public void executeSetNodeValue(Node node, String value) {
      super.setNodeValue(node, value);
    }
    
    public boolean isEditable(Node node) {
      int nodeType = node.getNodeType();
      boolean result = false;
      switch (nodeType) {
        case Node.ATTRIBUTE_NODE :
        {
          // Prevent the editing of the namespace attribute
          Attr attr = (Attr)node;
          result = !attr.getName().startsWith(XML_NS_STRING);
          break;
        }
        default:
        {
          result = super.isEditable(node);
        }
      }
      return result;
    } 
    
    // Need to override since I don't want to see the source attribute
    public Object[] getChildren(Object element) {
      Object[] result = null;

      if (element instanceof List) {
        result = ((List)element).toArray();
      } else if (element instanceof Node) {
        Node node = (Node) element;
        List list = new ArrayList();
        boolean textContentOnly = true;

        // Don't want to see any attributes for the input element
        if (!(element instanceof Element && (element == getInput()))) {
          NamedNodeMap map = node.getAttributes();
          if (map != null) {
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
              list.add(map.item(i));
              textContentOnly = false;
            }
          }
        }

        Node prevIncludedNode = null;
        for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
          int childNodeType = childNode.getNodeType();
          boolean includeNode = true;

          if (includeNode && hasStyleFlag(HIDE_WHITE_SPACE_TEXT_NODES)) {
            if (isIgnorableText(childNode)) {
              // filter out the ignorable text node
              includeNode = false;
            }
          }

          if (includeNode && hasStyleFlag(COMBINE_ADJACENT_TEXT_AND_ENTITY_REFERENCES)) {
            if (isTextOrEntityReferenceNode(childNode) && prevIncludedNode != null && isTextOrEntityReferenceNode(prevIncludedNode)) {
              // we only show the first of a list of adjacent text or entity reference node in the tree
              // so we filter out this subsequent one
              includeNode = false;
            }
          }

          if (hasStyleFlag(HIDE_ELEMENT_CHILD_TEXT_NODES)) {
            if (childNodeType != Node.TEXT_NODE && childNodeType != Node.ENTITY_REFERENCE_NODE) {
              textContentOnly = false;
            }
          }

          if (includeNode) {
            list.add(childNode);
            prevIncludedNode = childNode;
          }
        }

        if (hasStyleFlag(HIDE_ELEMENT_CHILD_TEXT_NODES) && textContentOnly) {
          result = new Object[0];
        }
        else {
          result = list.toArray();
        }
      }
      return result;
    }
    
  } 
  
  class MyXMLTreeExtension extends XMLTreeExtension {
    
    public MyXMLTreeExtension(Tree tree) {
      super(tree);
      this.treeContentHelper = new MyTreeContentHelper();
      this.columnPosition = 200;
//      this.setRefreshAll(true);
    }
    
    
    // Do not wish to display any of the helper text
    public String getElementValueHelper(Element element) {
      return EMPTY_STRING; 
    } 
        
      
  }
  

}
