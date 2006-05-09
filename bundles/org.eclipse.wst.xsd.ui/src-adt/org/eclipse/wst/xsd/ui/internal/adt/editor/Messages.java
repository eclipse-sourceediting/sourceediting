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
  public static String _UI_LABEL_DESIGN;
  public static String _UI_LABEL_SOURCE;
}
