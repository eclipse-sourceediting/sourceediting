/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.contentmodel.internal.test;
                                      
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.wst.sse.core.internal.contentmodel.CMNode;
import org.eclipse.wst.sse.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.sse.core.internal.contentmodel.modelqueryimpl.XMLAssociationProvider;
import org.eclipse.wst.sse.core.internal.contentmodel.util.CMDocumentCache;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Test implements IPlatformRunnable
{ 
  public Object run(Object a) 
  {             
    String args[] = (String[])a;
    if (args.length > 0)
    {
      test(args[0]);      
    }                    
    else
    {
      System.out.println("xml file name argument required");
    }
    return null;
  }
      
  protected void test(String fileName)
  {               
    try
    {
      CMDocumentCache cache=new CMDocumentCache();
      XMLAssociationProvider provider = new XMLAssociationProvider(cache){};
                                                                                   
      ModelQuery mq = new ModelQueryImpl(provider);                              
    
      Thread.currentThread().setContextClassLoader(Test.class.getClassLoader()); 
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(fileName);

      /*
      ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader()); 
      Class theClass = Class.forName("org.apache.xerces.parsers.DOMParser");
      DOMParser parser = (DOMParser)theClass.newInstance();
      Thread.currentThread().setContextClassLoader(prevClassLoader); 
      parser.parse(new InputSource(fileName));       
      Document document = parser.getDocument();        
      */
      visitNode(document, mq, 0);
    }
    catch (Exception e)
    {    
      e.printStackTrace();
    }                                            
  }

  protected void visitNode(Node node, ModelQuery mq, int indent)
  {                                                                                                    
    CMNode cmnode = mq.getCMNode(node);     
    printlnIndented(indent, "node :" + node.getNodeName() + " cmnode : " + (cmnode != null ? cmnode.getNodeName() : "null"));
    NamedNodeMap map = node.getAttributes();
    if (map != null)
    {                                    
      indent += 2;
      int mapLength = map.getLength();
      for (int i = 0; i < mapLength; i++)
      {
        visitNode(map.item(i), mq, indent);
      } 
      indent -= 2;
    }
    indent += 4;
    NodeList list = node.getChildNodes();
    int listLength = list.getLength();
    for (int i = 0; i < listLength; i++)
    {
      visitNode(list.item(i), mq, indent);
    } 
    indent -= 4;
  }     

  public static void printlnIndented(int indent, String string)
  {    
    for (int i = 0; i < indent; i++)
    {
      System.out.print(" ");
    }
    System.out.println(string);
  }     
}
