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
package org.eclipse.wst.xsd.ui.internal.navigation;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextSelectionNavigationLocation;

/**
 * The platform's navigation history plumbing doesn't like multipage text
 * editors very much and tends to ignore text locations.  To fix this
 * problem we need to override the getEditPart() method of the super class
 * in order to return the actual TextEditor of our multi-page editor
 */
public class MultiPageEditorTextSelectionNavigationLocation extends TextSelectionNavigationLocation
{
  public MultiPageEditorTextSelectionNavigationLocation(ITextEditor part, boolean initialize)
  {
    super(part, initialize);
  }

  protected IEditorPart getEditorPart()
  {
    IEditorPart part = super.getEditorPart();
    if (part != null)
      return (ITextEditor) part.getAdapter(ITextEditor.class);
    return null;
  }

  public String getText()
  {
    // ISSUE: how to get title?
    // IEditorPart part = getEditorPart();
    // if (part instanceof WSDLTextEditor) {
    // return ((WSDLTextEditor) part).getWSDLEditor().getTitle();
    // }
    // else {
    // return super.getText();
    // }
    return super.getText();
  }
}
