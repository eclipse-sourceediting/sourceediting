/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.TypeVizFigureFactory;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDEditPartFactory;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDGraphViewerDialog extends PopupDialog
{
  protected Object model;
  protected ScrollingGraphicalViewer viewer;
  protected IOpenInNewEditor openInNewEditorHelper;
  private OpenEditorLinkListener linkListener;
  private Label nsInfoLabel;
  private Hyperlink link;
  private String infoText;
  private Font infoFont;
  private String uniqueID;
  private PreviewControlListener moveListener;
  
  private static String X_ORIGIN = "DIALOG_X_ORIGIN"; //$NON-NLS-1$
  private static String Y_ORIGIN = "DIALOG_Y_ORIGIN"; //$NON-NLS-1$
  private boolean isHighContrast = false;

  public XSDGraphViewerDialog(Shell parentShell, String titleText, String infoText, Object model, String ID)
  {
    super(parentShell, HOVER_SHELLSTYLE, true, true, true, true, false, titleText, infoText);
    setModel(model);
    linkListener = new OpenEditorLinkListener();
    this.infoText = infoText;
    this.uniqueID = ID;
    Assert.isTrue(ID != null && ID.length() > 0);    
    moveListener = new PreviewControlListener();
    try 
    {
      isHighContrast = Display.getDefault().getHighContrast();
    }
    catch (Exception e) {
      // ignore 
    }
    create();
  }

  public void setOpenExternalEditor(IOpenInNewEditor helper)
  {
    this.openInNewEditorHelper = helper;
  }
  
  protected void fillDialogMenu(IMenuManager dialogMenu)
  {
    super.fillDialogMenu(dialogMenu);
    dialogMenu.add(new Separator());
    dialogMenu.add(new ClosePopup());
    dialogMenu.add(new Separator());
    dialogMenu.add(new SetOpenInEditor());
  }

  protected Control createDialogArea(Composite parent)
  {
    viewer = new ScrollingGraphicalViewer();
    Composite c = (Composite)super.createDialogArea(parent);

    if (isHighContrast)
    {
      c.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }
    else
    {
      c.setBackground(ColorConstants.white);
    }
    c.setLayout(new FillLayout());

    RootEditPart root = new RootEditPart();
    viewer.setRootEditPart(root);

    viewer.createControl(c);

    // The graphical viewer tool tip processing creates an extra shell which
    // interferes with the PopupDialog's deactivation logic. 
    removeMouseListeners(viewer.getControl());    

    if (isHighContrast)
    {
      viewer.getControl().setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    }
    else
    {
      viewer.getControl().setBackground(ColorConstants.white);
    }
    EditPartFactory editPartFactory = new XSDEditPartFactory(new TypeVizFigureFactory());
    viewer.setEditPartFactory(editPartFactory);

    RootContentEditPart rootContentEditPart = new RootContentEditPart();
    rootContentEditPart.setModel(model);
    viewer.setContents(rootContentEditPart);
    
    getShell().addControlListener(moveListener);
    return c;
  }

  private void removeMouseListeners(Control control)
  {
    Listener[] listeners = control.getListeners(SWT.MouseEnter);
    control.removeListener(SWT.MouseEnter, listeners[0]);
    
    listeners = control.getListeners(SWT.MouseExit);
    control.removeListener(SWT.MouseExit, listeners[0]);    

    listeners = control.getListeners(SWT.MouseHover);
    control.removeListener(SWT.MouseHover, listeners[0]);    

    listeners = control.getListeners(SWT.MouseMove);
    control.removeListener(SWT.MouseMove, listeners[0]);    

    listeners = control.getListeners(SWT.MouseDown);
    control.removeListener(SWT.MouseDown, listeners[0]);    

    listeners = control.getListeners(SWT.MouseUp);
    control.removeListener(SWT.MouseUp, listeners[0]);    
    
    listeners = control.getListeners(SWT.MouseDoubleClick);
    control.removeListener(SWT.MouseDoubleClick, listeners[0]);
  }
  
  protected Control createInfoTextArea(Composite parent)
  {
    Composite infoComposite = new Composite(parent, SWT.NONE);
    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    infoComposite.setLayout(gridLayout);
    GridData gd = new GridData(GridData.FILL_BOTH);
    infoComposite.setLayoutData(gd);

    nsInfoLabel = new Label(infoComposite, SWT.LEFT);
    nsInfoLabel.setText(infoText);

    Font font = nsInfoLabel.getFont();
    FontData[] fontDatas = font.getFontData();
    for (int i = 0; i < fontDatas.length; i++)
    {
      fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
    }
    infoFont = new Font(nsInfoLabel.getDisplay(), fontDatas);
    nsInfoLabel.setFont(infoFont);
    gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
    nsInfoLabel.setLayoutData(gd);
    nsInfoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

    link = new Hyperlink(infoComposite, SWT.RIGHT);
    link.setText(Messages._UI_ACTION_OPEN_IN_NEW_EDITOR);
    link.setFont(infoFont);
    link.addHyperlinkListener(linkListener);
    return infoComposite;
  }
  
  private void setModel(Object model)
  {
    Assert.isTrue(model instanceof XSDConcreteComponent);
    this.model = XSDAdapterFactory.getInstance().adapt((XSDConcreteComponent) model);
  }
    
  protected class SetOpenInEditor extends Action
  {
    public SetOpenInEditor()
    {
      super(Messages._UI_ACTION_OPEN_IN_NEW_EDITOR);
    }
    
    public void run()
    {
      if (openInNewEditorHelper != null)
      {
        try
        {
          openInNewEditorHelper.openXSDEditor();
        }
        catch (Exception e)
        {
          
        }
      }
    }
  }
  
  private class ClosePopup extends Action {
    public ClosePopup()
    {
      super(Messages._UI_ACTION_CLOSE_SCHEMA_PREVIEW_POPUP);
    }

    public void run()
    {
      close();
    }
  }
  
  protected IDialogSettings getDialogSettings()
  {
    IDialogSettings settings= XSDEditorPlugin.getDefault().getDialogSettings().getSection(uniqueID);
    if (settings == null)
      settings= XSDEditorPlugin.getDefault().getDialogSettings().addNewSection(uniqueID);

    return settings;
  }
  
  protected Point getInitialLocation(Point initialSize)
  {
    Point result = super.getInitialLocation(initialSize);

    IDialogSettings settings = getDialogSettings();
    if (settings != null)
    {
      try
      {

        String prefix = uniqueID == null ? getClass().getName() : uniqueID;
        int x = settings.getInt(prefix + X_ORIGIN);
        int y = settings.getInt(prefix + Y_ORIGIN);
        result = new Point(x, y);
        Shell parent = getParentShell();
        if (parent != null)
        {
          Point parentLocation = parent.getLocation();
          result.x += parentLocation.x;
          result.y += parentLocation.y;
        }
      }
      catch (NumberFormatException e)
      {
      }
    }
    return result;
  }

  protected void saveDialogBounds(Shell shell)
  {
    IDialogSettings settings = getDialogSettings();
    if (settings != null)
    {
      Point shellLocation = shell.getLocation();
      Shell parent = getParentShell();
      if (parent != null)
      {
        Point parentLocation = parent.getLocation();
        shellLocation.x -= parentLocation.x;
        shellLocation.y -= parentLocation.y;
      }
      String prefix = uniqueID == null ? getClass().getName() : uniqueID;
      settings.put(prefix + X_ORIGIN, shellLocation.x);
      settings.put(prefix + Y_ORIGIN, shellLocation.y);
    }
  }

  
  public boolean close()
  {
    if (getShell() == null || getShell().isDisposed()) 
    {
      return true;
    }
    
    getShell().removeControlListener(moveListener);
    if (link != null)
      link.removeHyperlinkListener(linkListener);
    if (infoFont != null)
      infoFont.dispose();
    infoFont = null;
    return super.close();
  }
  
  private final class OpenEditorLinkListener implements IHyperlinkListener
  {

    public void linkActivated(HyperlinkEvent e)
    {
      new SetOpenInEditor().run();
      close();
    }

    public void linkEntered(HyperlinkEvent e)
    {
      link.setForeground(ColorConstants.lightBlue);
    }

    public void linkExited(HyperlinkEvent e)
    {
      link.setForeground(link.getParent().getForeground());
    }
    
  }
  
  protected class PreviewControlListener implements ControlListener
  {

    public void controlMoved(ControlEvent e)
    {
      saveDialogBounds(getShell());
    }

    public void controlResized(ControlEvent e)
    {
    }
  }

  /**
   * @deprecated since 1.2.101
   * @param xsdComponent
   * @param schema
   * @param editorName
   */
  public static void openNonXSDResourceSchema(XSDConcreteComponent xsdComponent, XSDSchema schema, String editorName)
  {
    openInlineSchema(getCurrentEditorInput(), xsdComponent, schema, editorName);
  }
  
  public static void openInlineSchema(IEditorInput editorInput, XSDConcreteComponent xsdComponent, XSDSchema schema, String editorName)
  {
    OpenInNewEditor.openInlineSchema(editorInput, xsdComponent, schema, editorName);
  }

  /**
   * @deprecated
   */
  public static void openXSDEditor(XSDConcreteComponent xsdComponent)
  {
    openXSDEditor(getCurrentEditorInput(), xsdComponent.getSchema(), xsdComponent);
  }

  public static void openXSDEditor(IEditorInput editorInput, XSDSchema schema, XSDConcreteComponent xsdComponent)
  {
    OpenInNewEditor.openXSDEditor(editorInput, schema, xsdComponent);
  }
  
  private static IEditorInput getCurrentEditorInput()
  {
    try
    {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      IWorkbenchPage page = null;
      if (workbenchWindow != null)
      {
        page = workbenchWindow.getActivePage();
        if (page != null)
        {
          IEditorPart editorPart = page.getActiveEditor();
          if (editorPart != null)
          {
            return editorPart.getEditorInput();
          }
        }
      }
    }
    catch (Exception e)
    {
      
    }
    return null;
  }
  
  protected Control getFocusControl()
  {
    return link;
  }  
}
