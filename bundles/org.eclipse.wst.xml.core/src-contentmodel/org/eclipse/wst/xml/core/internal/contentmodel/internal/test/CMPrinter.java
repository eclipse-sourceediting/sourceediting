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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.test;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMEntityDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;


public class CMPrinter extends CMVisitor
{
  protected StringBuffer sb = new StringBuffer();
  protected Vector visitedElements = new Vector(); 
    
  /******   formatting    ******/
  protected String indent = "";

  protected void incrementIndent()
  {
    indent = indent + "  ";
  }

  protected void decrementIndent()
  {
    indent = indent.substring(0, indent.length()-2);
  }


  public void printCMNode(String filename, CMNode cmnode)
  {
    visitCMNode(cmnode);
    try 
    {
      FileOutputStream fileStream = new FileOutputStream(filename);
      OutputStreamWriter writer = new OutputStreamWriter(fileStream);
      writer.write(sb.toString());
      writer.flush();
      fileStream.close();
    }
    catch (Exception e) {}  
  }

  public void visitCMAnyElement(CMAnyElement anyElement)
  {
    sb.append(indent + "<CMAnyElement");
    printAttributes(sb, anyElement);
    sb.append(">/n");
    incrementIndent();
    printProperties(sb, anyElement);
    decrementIndent();
    sb.append(indent + "</CMAnyElement>/n");
  }

  public void visitCMAttributeDeclaration(CMAttributeDeclaration ad)
  {
    sb.append(indent + "<CMAttributeDeclaration");
    printAttributes(sb, ad);
    sb.append(">\n");
    incrementIndent();
    printProperties(sb, ad);
    decrementIndent();
    visitCMNode(ad.getAttrType());
    sb.append(indent + "</CMAttributeDeclaration>\n");
  }

  public void visitCMDataType(CMDataType dataType)
  {
    sb.append(indent + "<CMDataType");
    printAttributes(sb, dataType);
    sb.append(">\n");
    incrementIndent();
    sb.append(indent + "<ImpliedValue kind=\"" + dataType.getImpliedValueKind() + "\">");
    sb.append(dataType.getImpliedValue() + "</ImpliedValue>\n");
    printEnumeration(sb, dataType.getEnumeratedValues());
    decrementIndent();
    sb.append(indent + "</CMDataType>\n");
  }

  public void visitCMDocument(CMDocument document)
  {
    sb.append(indent + "<CMDocument");
    printAttributes(sb, document);
    sb.append(">\n");
    incrementIndent();

    //    printEntities(sb, document.getEntities()); 
    printProperties(sb, document);

    CMNamedNodeMap map = document.getElements();
    int size = map.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(map.item(i));
    }

    decrementIndent();
    sb.append(indent + "</CMDocument>\n");
  }

  public void visitCMGroup(CMGroup group)
  {
    sb.append(indent + "<CMGroup");
    printAttributes(sb, group);
    sb.append(">\n");
    incrementIndent();

    CMNodeList nodeList = group.getChildNodes();
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(nodeList.item(i));
    }

    decrementIndent();
    sb.append(indent + "</CMGroup>\n");
  }

  public void visitCMElementDeclaration(CMElementDeclaration ed)
  {
    if (!visitedElements.contains(ed))
    {
      visitedElements.add(ed);
      sb.append(indent + "<CMElementDeclaration");
      printAttributes(sb, ed);
      sb.append(">\n");
      incrementIndent();
      printProperties(sb, ed);

      CMNamedNodeMap nodeMap = ed.getAttributes();
      int size = nodeMap.getLength();
      for (int i = 0; i < size; i++)
      {
        visitCMNode(nodeMap.item(i));
      }

      visitCMNode(ed.getContent());

      CMDataType dataType = ed.getDataType();
      if (dataType != null)
        visitCMNode(dataType);

      decrementIndent();
      sb.append(indent + "</CMElementDeclaration>\n");
    }
  }


  public void printEnumeration(StringBuffer sb, String[] values)
  {
    if ((values != null) && (values.length > 0))
    {
      sb.append(indent + "<Enumeration>\n");
      incrementIndent();

      for (int i=0; i<values.length; i++)
      {
        sb.append(indent + "<Value>" + values[i] + "</Values>\n");
      }

      decrementIndent();
      sb.append(indent + "</Enumeration>\n");
    }
  }


  public void printAttributes(StringBuffer sb, CMNode cmnode)
  {
    if (cmnode != null)
    {
      sb.append(" nodeName=\"" + cmnode.getNodeName() + "\"");
      sb.append(" nodeType=\"" + cmnode.getNodeType() + "\"");

      if (cmnode instanceof CMContent)
      {
        sb.append(" minOccur=\"" + ((CMContent)cmnode).getMinOccur() + "\"");
        sb.append(" maxOccur=\"" + ((CMContent)cmnode).getMaxOccur() + "\"");
      }
      if (cmnode instanceof CMAnyElement)
      {
        sb.append(" namespaceURI=\"" + ((CMAnyElement)cmnode).getNamespaceURI() + "\"");
      }
      if (cmnode instanceof CMAttributeDeclaration)
      {
        sb.append(" uasage=\"" + ((CMAttributeDeclaration)cmnode).getUsage() + "\"");
        sb.append(" defaultValue=\"" + ((CMAttributeDeclaration)cmnode).getDefaultValue() + "\"");
      }
      if (cmnode instanceof CMDataType)
      { 
        sb.append(" instanceValue=\"" + ((CMDataType)cmnode).generateInstanceValue() + "\"");
      }
      if (cmnode instanceof CMEntityDeclaration)
      {
        String value = ((CMEntityDeclaration)cmnode).getValue();
        if (value.indexOf("\"") == -1) sb.append(" value=\"" + value + "\"");
        else sb.append(" value=\"" + value + "\"");
      }
      if (cmnode instanceof CMGroup)
      {
        sb.append(" operator=\"" + ((CMGroup)cmnode).getOperator() + "\"");
      }
      if (cmnode instanceof CMElementDeclaration)
      {
        sb.append(" contentType=\"" + ((CMElementDeclaration)cmnode).getContentType() + "\"");
      }
    }
  }


  public void printEntities(StringBuffer sb, CMNamedNodeMap entities)
  {
    if ((entities != null) && (entities.getLength()>0))
    {
      sb.append(indent + "<Entities>\n");
      incrementIndent();

      for (Iterator i = entities.iterator(); i.hasNext();)
      {
        CMEntityDeclaration entity = (CMEntityDeclaration)i.next();
        sb.append(indent + "<Entity");
        printAttributes(sb, entity);
        sb.append("/>\n");
      }

      decrementIndent();
      sb.append(indent + "</Entities>\n");
    }
  }

  public void printProperties(StringBuffer sb, CMNode cmnode)
  {
    List properties = getProperties(cmnode);
    sb.append(indent + "<Properties>\n");
    incrementIndent();

    for (int i=0; i<properties.size(); i++)
    {
      String property = (String)properties.get(i);
      sb.append(indent + "<Property name=\"" + property + "\"");

      if (cmnode.supports(property))
      {
        sb.append(" supports=\"true\"");
      }
      else
      {
        sb.append(" supports=\"false\"");
      }
      Object value = cmnode.getProperty(property);
      if (value instanceof String) sb.append(" value=\"" + (String)value + "\"");
      else if (value instanceof Boolean) sb.append(" value=\"" + value + "\"");
      else if (value != null) sb.append(" value=\"" + value.getClass() + "\"");
      else sb.append(" value=\"null\"");
      sb.append("/>\n");
    }

    decrementIndent();
    sb.append(indent + "</Properties>\n");
  }

  public List getProperties(CMNode cmnode)
  {
    List properties = new Vector();
      
    if (cmnode != null)
    {
      properties.add("CMDocument");
      properties.add("documentation");
      properties.add("http://org.eclipse.wst/cm/properties/usesLocalElementDeclarations");
      properties.add("http://org.eclipse.wst/cm/properties/isNameSpaceAware");
      properties.add("http://org.eclipse.wst/cm/properties/nsPrefixQualification");
      properties.add("http://org.eclipse.wst/cm/properties/nillable");
      properties.add("http://org.eclipse.wst/cm/properties/mofNotifier");
      properties.add("spec");

      if (cmnode instanceof CMElementDeclaration)
      {
        properties.add("http://org.eclipse.wst/cm/properties/definitionInfo");
        properties.add("http://org.eclipse.wst/cm/properties/definition");
        properties.add("XSITypes");
        properties.add("DerivedElementDeclaration");
        // properties.add("SubstitutionGroup"); Currrently broken for nexted xsd files
        properties.add("Abstract");
      }
      if (cmnode instanceof CMDocument)
      {
        properties.add("http://org.eclipse.wst/cm/properties/targetNamespaceURI");
        properties.add("http://org.eclipse.wst/cm/properties/importedNamespaceInfo");
        properties.add("http://org.eclipse.wst/cm/properties/namespaceInfo");
        properties.add("http://org.eclipse.wst/cm/properties/elementFormDefault");
        properties.add("annotationMap");
        properties.add("encodingInfo");
      }
      // properties defines but not used
      // properties.add("documentationSource");
      // properties.add("documentationLanguage");
      // properties.add("isValidEmptyValue");
      // properties.add("http://org.eclipse.wst/cm/properties/defaultRootName");
    }
    return properties;
  }
  
}

 
