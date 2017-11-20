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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.xsd.XSDConcreteComponent;

// cs: This interface is intended to suppliment the NodeFilter class
// that can be specified via the customization extensions point.
// When the initial list of a category's objects is displayed 
// we'll use a NodeFilter to prune the list.  If the NodeFilter
// implements this interface we'll call this interface's isApplicableContext
// method instead of the NodeFilter's DOM node based method.  This provides 
// are a more convenient interface for clients who are only interested in 
// filter the list presented via the AddExtensionComponentDialog.
// See the class ExtensionsSection for more details.
//
public interface ExtensionItemFilter
{
  public boolean isApplicableContext(XSDConcreteComponent parent, XSDConcreteComponent candidate);
}
