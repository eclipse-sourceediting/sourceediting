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
package org.eclipse.wst.xsd.ui.internal.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.xsd.util.XSDSchemaLocationResolver;

public class XSDSchemaLocationResolverAdapterFactory extends AdapterFactoryImpl
{
    protected XSDSchemaLocationResolverImpl schemaLocator = new XSDSchemaLocationResolverImpl();

    public boolean isFactoryForType(Object type)
    {
      return type == XSDSchemaLocationResolver.class;
    }

    public Adapter adaptNew(Notifier target, Object type)
    {
      return schemaLocator;
    }
}
