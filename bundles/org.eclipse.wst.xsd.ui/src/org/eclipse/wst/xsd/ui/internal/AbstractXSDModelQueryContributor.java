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
package org.eclipse.wst.xsd.ui.internal;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.w3c.dom.Document;

public abstract class AbstractXSDModelQueryContributor
{ 
  protected AbstractXSDDataTypeValueExtension xsdDataTypeValueExtension;

  public void setModel(XMLModel model)
  {                    
    // remove our old DataTypeValueExtension
    //
    if (xsdDataTypeValueExtension != null)
    {
      xsdDataTypeValueExtension.dispose();
      xsdDataTypeValueExtension = null;
    }

    setImplicitGrammar(model.getDocument());

    // add a new DataTypeValueExtension
    //                                                          
    ModelQuery modelQuery = ModelQueryUtil.getModelQuery(model.getDocument());
    xsdDataTypeValueExtension = createXSDDataTypeValueExtension(modelQuery);
  }
  
  protected void setImplicitGrammar(Document document)
  {
//    DOMExtension domExtension = DOMExtensionProviderRegistry.getInstance().getDOMExtension(document);
//    if (domExtension != null)
//    {
      String uri = "platform:/plugin/org.eclipse.wst.xsd.ui/w3c/schemaForCodeAssist.xsd";
      uri = resolvePlatformUrl(uri);
      if (uri != null)
      {
        Vector list = new Vector();
        NamespaceInfo info = new NamespaceInfo("http://www.w3.org/2001/XMLSchema", "xsd", uri);
        info.setProperty("isImplied", "true");
        list.add(info);
//        domExtension.setImplictNamespaceInfoList(list);
      }
//    }
  } 

  protected static String resolvePlatformUrl(String urlspec)
  {
    String result = null;
    try
    {
      urlspec = urlspec.replace('\\', '/');
      URL url = new URL(urlspec);
      URL resolvedURL = Platform.resolve(url);
      result = resolvedURL.toString();
    }
    catch (Exception e)
    {
    }
    return result;
  }

  public abstract AbstractXSDDataTypeValueExtension createXSDDataTypeValueExtension(ModelQuery modelQuery);
}