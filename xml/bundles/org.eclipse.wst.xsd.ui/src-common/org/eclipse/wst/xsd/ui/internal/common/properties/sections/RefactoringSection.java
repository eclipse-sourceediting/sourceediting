/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.ISelectionMapper;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.handlers.RenameHandler;
import org.eclipse.xsd.XSDSchema;

public abstract class RefactoringSection extends AbstractSection implements IHyperlinkListener
{
  /**
   * Clicking on it invokes the refactor->rename action.
   */
  private ImageHyperlink renameHyperlink;
  
  protected boolean hideHyperLink;

  /**
   * Invokes the refactor->rename action on the current selection.
   */
  private void invokeRenameRefactoring()
  {
    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    XSDSchema schema = (XSDSchema) editor.getAdapter(XSDSchema.class);
    ISelection selection = new StructuredSelection(input);
    ISelectionMapper mapper = (ISelectionMapper) editor.getAdapter(ISelectionMapper.class);
    selection = mapper != null ? mapper.mapSelection(selection) : selection;
    RenameHandler renameHandler = new RenameHandler();
    renameHandler.execute(selection, schema);
  }

  protected void showLink(boolean isVisible)
  {
	  renameHyperlink.setVisible(isVisible);
  }
  
  /**
   * Creates the refactor/rename hyperlink shown beside a component name.
   * Clicking on the hyperlink invokes the refactor/rename action.
   * 
   * @param parent
   *          the parent composite. Must not be null.
   */
  protected void createRenameHyperlink(Composite parent)
  {
    renameHyperlink = getWidgetFactory().createImageHyperlink(parent, SWT.NONE);

    renameHyperlink.setImage(XSDEditorPlugin.getXSDImage("icons/quickassist.gif")); //$NON-NLS-1$
    renameHyperlink.setToolTipText(Messages._UI_TOOLTIP_RENAME_REFACTOR);
    renameHyperlink.addHyperlinkListener(this);
  }
  
  protected void setRenameHyperlinkEnabled(boolean isEnabled)
  {
    renameHyperlink.setEnabled(isEnabled);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
   */
  public void linkActivated(HyperlinkEvent e)
  {
    invokeRenameRefactoring();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
   */
  public void linkEntered(HyperlinkEvent e)
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
   */
  public void linkExited(HyperlinkEvent e)
  {
  }
}
