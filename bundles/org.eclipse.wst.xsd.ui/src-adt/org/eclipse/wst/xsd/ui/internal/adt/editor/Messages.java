/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
  private static final String BUNDLE_NAME = "org.eclipse.wst.xsd.ui.internal.adt.editor.messages"; //$NON-NLS-1$

  private Messages()
  {
  }

  static
  {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
  public static String _UI_ACTION_SHOW_PROPERTIES;
  public static String _UI_ACTION_SET_AS_FOCUS;
  public static String _UI_ACTION_DELETE;
  public static String _UI_ACTION_ADD_FIELD;
  public static String _UI_ACTION_BROWSE;
  public static String _UI_ACTION_NEW;
  public static String _UI_ACTION_UPDATE_NAME;
  public static String _UI_ACTION_UPDATE_TYPE;
  public static String _UI_ACTION_UPDATE_ELEMENT_REFERENCE;
  public static String _UI_ACTION_SELECT_ALL;
  public static String _UI_LABEL_DESIGN;
  public static String _UI_LABEL_SOURCE;
  public static String _UI_LABEL_VIEW;
  public static String _UI_HOVER_VIEW_MODE_DESCRIPTION;
  public static String _UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION_QUESTION;
  public static String _UI_ACTION_CAPTURE_SCREEN_OVERWRITE_CONFIRMATION;
  public static String _UI_ACTION_CAPTURE_SCREEN_ERROR_TITLE;
  public static String _UI_ACTION_CAPTURE_SCREEN_ERROR_DESCRIPTION;
  public static String _UI_ACTION_CAPTURE_SCREEN_FORMAT_NOT_SUPPORTED;
  public static String _UI_CAPTURE_SCREEN_ACTION_TEXT;
  public static String _UI_CAPTURE_SCREEN_ACTION_TOOLTIPTEXT;
  public static String _UI_ACTION_CAPTURE_SCREEN_DEFAULT_FILE_NAME;
  public static String _UI_ACTION_CAPTURE_SCREEN_FILE_SAVE_DIALOG_TITLE;
  public static String _UI_HOVER_BACK_TO_SCHEMA;
  public static String _UI_REFACTOR_CONTEXT_MENU;
  public static String _UI_HYPERLINK_TEXT;  
}
