/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.provider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationWrapper;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDWildcard;


public class XSDParticleAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDParticleAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  // hack to notify outline and graph view of minOccurs and maxOccurs changes
  public void notifyChanged(Notification msg) 
  {
    XSDParticle xsdParticle = (XSDParticle)msg.getNotifier();
    XSDParticleContent xsdParticleContent = xsdParticle.getContent();
    if (xsdParticleContent != null)
    {
      if (xsdParticleContent instanceof XSDElementDeclaration)
      {
        fireNotifyChanged(new NotificationWrapper((XSDElementDeclaration)xsdParticleContent, msg));
        XSDModelAdapterFactory.getAdapter(xsdParticleContent).firePropertyChanged(xsdParticleContent, null);
      }
      else if (xsdParticleContent instanceof XSDModelGroup)
      {
        fireNotifyChanged(new NotificationWrapper((XSDModelGroup)xsdParticleContent, msg));
        XSDModelAdapterFactory.getAdapter(xsdParticleContent).firePropertyChanged(xsdParticleContent, null);
      }
      else if (xsdParticleContent instanceof XSDWildcard)
      {
        fireNotifyChanged(new NotificationWrapper((XSDWildcard)xsdParticleContent, msg));
        XSDModelAdapterFactory.getAdapter(xsdParticleContent).firePropertyChanged(xsdParticleContent, null);
      }
    }
    // super.notifyChanged(msg);
  }

}
