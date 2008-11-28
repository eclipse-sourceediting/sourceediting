/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddDocumentationCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AnnotationSection extends AbstractSection
{
  Text simpleText;

  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    simpleText = getWidgetFactory().createText(composite, "", SWT.V_SCROLL | SWT.H_SCROLL); //$NON-NLS-1$
    simpleText.addListener(SWT.Modify, this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(simpleText,
    		XSDEditorCSHelpIds.DOCUMENTATION_TAB__NO_LABEL); 
    

    FormData data = new FormData();
    data.left = new FormAttachment(0, 1);
    data.right = new FormAttachment(100, -1);
    data.top = new FormAttachment(0, 1);
    data.bottom = new FormAttachment(100, -1);
    simpleText.setLayoutData(data);
  }

  public AnnotationSection()
  {
    super();
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();
    
    if (simpleText.isFocusControl()) return;
    setListenerEnabled(false);
    if (input instanceof XSDConcreteComponent)
    {
      XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation((XSDConcreteComponent) input, false);
      setInitialText(xsdAnnotation);
    }
    setListenerEnabled(true);
  }

  public void doHandleEvent(Event event)
  {
    if (input instanceof XSDConcreteComponent)
    {
      if (event.widget == simpleText)
      {
        AddDocumentationCommand command = new AddDocumentationCommand(Messages._UI_ACTION_ADD_DOCUMENTATION, null, (XSDConcreteComponent) input, simpleText.getText(), ""); //$NON-NLS-1$
        getCommandStack().execute(command);
      }
    }

  }

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  public void dispose()
  {

  }

  private void setInitialText(XSDAnnotation an)
  {
    if (an != null)
    {
      try
      {
        List documentationList = an.getUserInformation();
        if (documentationList.size() > 0)
        {
          Element docElement = (Element) documentationList.get(0);
          if (docElement != null)
          {
            simpleText.setText(doSerialize(docElement));
          }
        }
        else
        {
            simpleText.setText("");
        }
      }
      catch (Exception e)
      {

      }
    }
    else
    {
      simpleText.setText(""); //$NON-NLS-1$
    }
  }

  private String doSerialize(Element element) throws IOException
  {
    String source = ""; //$NON-NLS-1$

    Node firstChild = element.getFirstChild();
    Node lastChild = element.getLastChild();
    int start = 0;
    int end = 0;

    if (element instanceof IDOMElement)
    {
      IDOMElement domElement = (IDOMElement) element;
      IDOMModel model = domElement.getModel();
      IDOMDocument doc = model.getDocument();

      if (firstChild instanceof IDOMNode)
      {
        IDOMNode first = (IDOMNode) firstChild;
        start = first.getStartOffset();
      }
      if (lastChild instanceof IDOMNode)
      {
        IDOMNode last = (IDOMNode) lastChild;
        end = last.getEndOffset();
      }
      source = doc.getSource().substring(start, end);
    }

    return source;
  }

}
