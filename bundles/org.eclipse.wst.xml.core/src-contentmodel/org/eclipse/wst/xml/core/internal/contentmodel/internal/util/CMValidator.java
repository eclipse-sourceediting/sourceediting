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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;



public class CMValidator
{
  protected static StringElementContentComparator stringContentComparitor = new StringElementContentComparator();
  protected Hashtable graphNodeTable = new Hashtable();

  /**
   * GraphNode
   */
  protected static class GraphNode
  {
    public String name;
    public boolean isTerminal;
    public Vector arcList = new Vector();

    public GraphNode(String name)
    {
      this.name = name;
    }

    public void addArc(Arc arc)
    {
      arcList.addElement(arc);
    }

    public String toString()
    {
      return "[GraphNode " + name + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
  }


  /**
   * Arc
   */
  protected static class Arc
  {
    public static final int ELEMENT  = 1;
    public static final int REPEAT   = 2;
    public static final int OPTIONAL = 3;
    public static final int PREV_IN  = 4;
    public static final int OUT_NEXT = 5;
    public static final int LINK     = 6;

    public int kind;
    public String name;
    public GraphNode node;
    public CMNode cmNode;

    public Arc(int kind, GraphNode node, CMNode cmNode)
    {
      this(kind, "", node, cmNode); //$NON-NLS-1$
    }

    protected Arc(int kind, String name, GraphNode node, CMNode cmNode)
    {
      this.name = name;
      this.kind = kind;
      this.node = node;
      this.cmNode = cmNode;
    }
  }


  /**
   * GraphGenerator
   */
  protected static class GraphGenerator extends CMVisitor
  {
    public int indent;
    public int count;
    public GraphNode startGraphNode;
    public Context context;

    protected static class Context
    {
      GraphNode from;
      GraphNode to;

      Context(GraphNode from, GraphNode to)
      {
        this.from = from;
        this.to = to;
      }

      GraphNode getLastGraphNode()
      {
        return (to != null) ? to : from;
      }
    }


    protected GraphGenerator()
    {
      startGraphNode = new GraphNode(getGraphNodeName());
      context = new Context(startGraphNode, null);
    }


    protected void generateGraph(CMElementDeclaration ed)
    {
      int contentType = ed.getContentType();

      if (contentType == CMElementDeclaration.MIXED ||
          contentType == CMElementDeclaration.ELEMENT)
      {
        visitCMNode(ed.getContent());
      }
      // CMElementDeclaration.PCDATA... no graph required
      // CMElementDeclaration.ANY... no graph required
      context.getLastGraphNode().isTerminal = true;
    }


    protected String getGraphNodeName()
    {
      return "n" + count++; //$NON-NLS-1$
    }


    protected GraphNode getStartGraphNode()
    {
      return startGraphNode;
    }


    /**
     *                repeat
     *             +----#-----+
     *             |          |
     *             v          |
     *  prev --#-> in --'x'-> out --#-> next
     *  |                               ^
     *  |                               |
     *  +----------------#--------------+
     *                optional
     *
     */
    protected void createArcs(GraphNode in, GraphNode out, CMContent cmContent)
    {
      createArcs(in, out, cmContent, false);
    }

    protected void createArcs(GraphNode in, GraphNode out, CMContent cmContent, boolean isAllGroup)
    {
      //println("+createArcs() " + ed.getDescription() + " " + ed.getMinOccur());
      GraphNode prev = context.from;
      GraphNode next = new GraphNode(getGraphNodeName());

      prev.addArc(new Arc(Arc.PREV_IN, in, cmContent));
      out.addArc(new Arc(Arc.OUT_NEXT, next, cmContent));

      if (context.to != null)
      {
        next.addArc(new Arc(Arc.LINK, context.to, cmContent));
      }
      else
      {
        context.from = next;
      }

      if (cmContent.getMinOccur() == 0)
      {
        // todo... should we see if an optional arc has already been added?
        prev.addArc(new Arc(Arc.OPTIONAL, next, cmContent));
      }

      if (cmContent.getMaxOccur() == -1 || cmContent.getMaxOccur() > 1 || isAllGroup)
      {
        out.addArc(new Arc(Arc.REPEAT, in, cmContent));
      }
    }


    public void visitCMGroup(CMGroup group)
    {
      Context prevContext = context;
      GraphNode in = new GraphNode("(" + getGraphNodeName()); //$NON-NLS-1$
      GraphNode out = new GraphNode(")" + getGraphNodeName()); //$NON-NLS-1$

      int groupOperator = group.getOperator();
      if (groupOperator == CMGroup.SEQUENCE)
      {
        context = new Context(in, null);
        super.visitCMGroup(group);
        context.from.addArc(new Arc(Arc.LINK, out, group));
      }
      else if (groupOperator == CMGroup.CHOICE ||
               groupOperator == CMGroup.ALL)
      {
        context = new Context(in, out);
        super.visitCMGroup(group);
      }

      context = prevContext;
      createArcs(in, out, group, groupOperator == CMGroup.ALL);
    }


    public void visitCMElementDeclaration(CMElementDeclaration ed)
    {
      GraphNode in = new GraphNode(getGraphNodeName());
      GraphNode out = new GraphNode(getGraphNodeName());
      createArcs(in, out, ed);
      in.addArc(new Arc(Arc.ELEMENT, ed.getElementName(), out, ed));
    }
                            

    public void visitCMAnyElement(CMAnyElement anyElement)
    {
      GraphNode in = new GraphNode(getGraphNodeName());
      GraphNode out = new GraphNode(getGraphNodeName());
      createArcs(in, out, anyElement);
      in.addArc(new Arc(Arc.ELEMENT, "any", out, anyElement)); //$NON-NLS-1$
    }
  }

  // todo.. implement cache strategy hook, handle name spaces, locals etc.
  //
  public GraphNode lookupOrCreateGraph(CMElementDeclaration element)
  {
    Object key = element;
    GraphNode node = (GraphNode)graphNodeTable.get(key);
    if (node == null)
    {
      node = createGraph(element);
      graphNodeTable.put(key, node);
    }
    return node;
  }

  public GraphNode createGraph(CMElementDeclaration element)
  {
    GraphGenerator generator = new GraphGenerator();
    generator.generateGraph(element);
    return generator.getStartGraphNode();
  }


  public void printGraph(GraphNode node, Vector namedArcList, Vector unamedArcList, int indent)
  {
    //String decoration = node.isTerminal ? " *" : "";
    //printlnIndented(indent, "GraphNode:" + node.name + decoration);

    indent += 2;
    for (Enumeration e = node.arcList.elements() ; e.hasMoreElements() ;)
    {
      Arc arc = (Arc)e.nextElement();
      //boolean visit = false;
      printlnIndented(indent, "Arc:" + arc.name + " (" + arc.kind + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      if (arc.kind == Arc.ELEMENT)
      {
        //table.add(currentGrammarObject, arc.grammarObject);
        if (!namedArcList.contains(arc))
        {
          namedArcList.add(arc);
          unamedArcList = new Vector();
          printGraph(arc.node, namedArcList, unamedArcList, indent + 2);
        }
      }
      else
      {
        if (!unamedArcList.contains(arc))
        {
          unamedArcList.add(arc);
          printGraph(arc.node, namedArcList, unamedArcList, indent + 2);
        }
      }
    }
  }


  public void populateFTable(FTable table, GraphNode node, CMElementDeclaration prevED, Vector namedArcList, Vector unamedArcList, int indent)
  {
    //String decoration = node.isTerminal ? " *" : "";
    //printlnIndented(indent, "GraphNode:" + node.name + decoration);

    indent += 2;
    for (Enumeration e = node.arcList.elements() ; e.hasMoreElements() ;)
    {
      Arc arc = (Arc)e.nextElement();
     //boolean visit = false;
      //printlnIndented(indent, "Arc:" + arc.name + " (" + arc.kind + ")");
      if (arc.kind == Arc.ELEMENT)
      {
        CMElementDeclaration newED = (CMElementDeclaration)arc.cmNode;
        table.add(prevED, newED);
        if (!namedArcList.contains(arc))
        {
          namedArcList.add(arc);
          unamedArcList = new Vector();
          populateFTable(table, arc.node, newED, namedArcList, unamedArcList, indent + 2);
        }
      }
      else
      {
        if (!unamedArcList.contains(arc))
        {
          unamedArcList.add(arc);
          populateFTable(table, arc.node, prevED, namedArcList, unamedArcList, indent + 2);
        }
      }
    }
  }


  public FTable getFTable(CMElementDeclaration ed)
  {
    FTable table = new FTable();
    GraphNode node = lookupOrCreateGraph(ed);
    populateFTable(table, node, ed, new Vector(), new Vector(), 0);
    return table;
  }


  public void printGraph(GraphNode node)
  {
    printGraph(node, new Vector(), new Vector(), 0);
  }


  public void validateElementList(ElementList list, GraphNode node, ElementContentComparator comparator, Result result, boolean loopFlag)
  {
    //println("+validateElementList " + node + " " + list);
    if (list == null && node.isTerminal)
    {
      result.isValid = true;
    }
    else
    {
      for (Iterator i = node.arcList.iterator(); i.hasNext();)
      {
        Arc arc = (Arc)i.next();
        boolean traverseArc = false;
        if (arc.kind == Arc.ELEMENT)
        {
          if (list != null && comparator.matches(list.head, arc.cmNode))
          {
            loopFlag = false;
            traverseArc = true;
            list = list.tail; // increment our position in the list
          }
        }
        else if (arc.kind == Arc.REPEAT)
        {
          if (!loopFlag)
          {
            traverseArc = true;
          }
          loopFlag = true;
        }
        else
        {
          traverseArc = true;
        }

        if (traverseArc)
        {
          // see if this arc leads to a correct solution
          result.push(arc);
          validateElementList(list, arc.node, comparator, result, loopFlag);
          if (result.isValid)
          {
            break;
          }
          result.pop(arc);
        }
      }
    }
  }


  /**
   *
   */
  protected static ElementList createElementList(int contentType, List v, ElementContentComparator comparator, Result result)
  {
    ElementList first = null;
    ElementList prev = null;

    int size = v.size();
    for (int i = 0; i < size; i++)
    {
      Object o = v.get(i);
      if (o != null && !comparator.isIgnorable(o))
      {
        if (comparator.isElement(o))
        {
          ElementList list = new ElementList();
          list.head = o;

          if (prev != null)
          {
            prev.tail = list;
          }
          else
          {
            first = list;
          }
          prev = list;
        }
        else if (contentType == CMElementDeclaration.ELEMENT)
        {
          result.isValid = false;
          result.errorIndex = i;
          result.errorMessage = "Element can not include PCDATA content"; //$NON-NLS-1$
        }
      }
    }
    return first;
  }

  /**
   *
   */
  public void validate(CMElementDeclaration ed, List elementContent, ElementContentComparator comparator, Result result)
  {
    int contentType = ed.getContentType();

    if (contentType == CMElementDeclaration.MIXED ||
        contentType == CMElementDeclaration.ELEMENT)
    {
      ElementList elementList = createElementList(contentType, elementContent, comparator, result);
      if (result.isValid == true)
      {  
        // defect 226213 ... elements with a large number of children will cause the recursion based validation
        // algorithm to stack overflow ... as a quick fix assume any element with a large number of children is valid        
        if (elementContent.size() < 500)
        {
          boolean isGraphValidationNeeded = !(elementList == null && contentType == CMElementDeclaration.MIXED);
          if (isGraphValidationNeeded)
          {
            // validate the elementList using a graph
            //
            result.isValid = false;
            GraphNode node = lookupOrCreateGraph(ed);
            validateElementList(elementList, node, comparator, result, false);
          }
        }
      }
    }
    else if (contentType == CMElementDeclaration.PCDATA)
    {
      int size = elementContent.size();
      for (int i = 0; i < size; i++)
      {
        Object o = elementContent.get(i);
        if (comparator.isElement(o))
        {
          result.isValid = false;
          result.errorIndex = i;
          result.errorMessage = "Element may only include PCDATA content"; //$NON-NLS-1$
          break;
        }
      }
    }
    else if (contentType == CMElementDeclaration.EMPTY)
    {
      int size = elementContent.size();
      for (int i = 0; i < size; i++)
      {
        Object o = elementContent.get(i);
        if (!comparator.isIgnorable(o))
        {
          result.isValid = false;
          result.errorIndex = i;
          result.errorMessage = "Element may not contain PCDATA or Element content"; //$NON-NLS-1$
          break;
        }
      }
    }
    //else if (contentType == CMElementDeclaration.ANY)
    // {
    //   assume elementContent will always be valid for this content type
    // }
  }


  public void getOriginArray(CMElementDeclaration ed, List elementContent, ElementContentComparator comparator, ElementPathRecordingResult result)
  {
    CMNode[] cmNodeArray = null;
    validate(ed, elementContent, comparator, result);
    if (result.isValid)
    {
      CMDataType dataType = ed.getDataType();
      int size = elementContent.size();
      cmNodeArray = new CMNode[size];
      Vector originList = result.getElementOriginList();
      int originListSize = originList.size(); 
      int originListIndex = 0;
      for (int i = 0; i < size; i++)
      {
        Object o = elementContent.get(i);
        if (comparator.isElement(o))
        {     
          if (originListIndex < originListSize)
          {
            cmNodeArray[i] = (CMNode)originList.get(originListIndex);
            originListIndex++;
          }
        }
        else if (comparator.isPCData(o))
        {
          cmNodeArray[i] = dataType;
        }
        // else the CMNode at this index is null
      }
      result.setOriginArray(cmNodeArray);
    }
  }

  /**
   *
   */
  public static class Result
  {
    public boolean isValid = true;
    public int errorIndex = -1;
    public String errorMessage;
    public boolean isRepeatTraversed; // detects if a repeat has been traversed

    public void push(Arc arc)
    {
      // overide this method to record traversed nodes
    }
    public void pop(Arc arc)
    {
      // overide this method to record traversed nodes
    }
    public CMNode[] getOriginArray()
    {
      return null;
    }
  }

  /**
   *
   */
  public static class ElementPathRecordingResult extends Result
  {
    protected Vector elementOriginStack = new Vector();
    protected CMNode[] originArray = null;

    public void push(Arc arc)
    {
      if (arc.kind == Arc.ELEMENT)
      {
        elementOriginStack.add(arc.cmNode);
      }
    }

    public void pop(Arc arc)
    {
      if (arc.kind == Arc.ELEMENT)
      {
        int size = elementOriginStack.size();
        elementOriginStack.remove(size - 1);
      }
    }

    public Vector getElementOriginList()
    {
      return elementOriginStack;
    }

    public CMNode[] getOriginArray()
    {
      return originArray;
    }

    public void setOriginArray(CMNode[] originArray)
    {
      this.originArray = originArray;
    }
  }

  /**
   *
   */
  public static class PathRecordingResult extends Result
  {
    protected Vector arcList = new Vector();

    public void push(Arc arc)
    {
      arcList.add(arc);
    }

    public void pop(Arc arc)
    {
      int size = arcList.size();
      arcList.remove(size - 1);
    }

    public List getArcList()
    {
      List list = new Vector();
      for (Iterator iterator = arcList.iterator(); iterator.hasNext(); )
      {
        Arc arc = (Arc)iterator.next();
        if (arc.kind == Arc.ELEMENT)
        {
          list.add(arc);
        }
      }
      return list;
    }

    public MatchModelNode getMatchModel()
    {
      MatchModelNodeBuilder builder = new MatchModelNodeBuilder(arcList);
      builder.buildMatchModel();
      return builder.getRoot();
    }
  }

  /**
   *
   */
  public static class MatchModelNode
  {
    public CMNode cmNode;
    public List children = new Vector();
    public Object data;

    public MatchModelNode(MatchModelNode parent, CMNode cmNode)
    {
      this.cmNode = cmNode;
    }

    public void printModel(int indent)
    {
      //String cmNodeName = cmNode != null ? cmNode.getNodeName() : "null";
      //printlnIndented(indent, "MatchModelNode : " + cmNodeName);
      for (Iterator iterator = children.iterator(); iterator.hasNext(); )
      {
        MatchModelNode child = (MatchModelNode)iterator.next();
        child.printModel(indent + 2);
      }
    }
  }

  public static class MatchModelNodeBuilder
  {
    protected List arcList;
    protected List stack = new Vector();
    protected MatchModelNode root;
    protected MatchModelNode current;

    public MatchModelNodeBuilder(List arcList)
    {
      this.arcList = arcList;
      root = new MatchModelNode(null, null);
      push(root);
    }

    protected void push(MatchModelNode node)
    {
      current = node;
      stack.add(node);
    }

    protected void pop()
    {
      int size = stack.size();
      stack.remove(size - 1);
      current = (MatchModelNode)stack.get(size - 2);
    }

    public boolean isCMGroup(CMNode cmNode)
    {
      return cmNode != null && cmNode.getNodeType() == CMNode.GROUP;
    }

    public void buildMatchModel()
    {
      for (Iterator iterator = arcList.iterator(); iterator.hasNext(); )
      {
        Arc arc = (Arc)iterator.next();

        if (arc.kind == Arc.ELEMENT)
        {
          current.children.add(new MatchModelNode(current, arc.cmNode));
        }
        else if (arc.kind == Arc.PREV_IN)
        {
          if (isCMGroup(arc.cmNode))
          {
            MatchModelNode newModelNode = new MatchModelNode(current, arc.cmNode);
            current.children.add(newModelNode);
            push(newModelNode);
          }
        }
        else if (arc.kind == Arc.OUT_NEXT)
        {
          if (isCMGroup(arc.cmNode))
          {
            pop();
          }
        }
        else if (arc.kind == Arc.REPEAT)
        {
          if (isCMGroup(arc.cmNode))
          {
            pop();
            MatchModelNode newModelNode = new MatchModelNode(current, arc.cmNode);
            current.children.add(newModelNode);
            push(newModelNode);
          }
        }
      }
    }

    public MatchModelNode getRoot()
    {
      return root;
    }
  }


  /**
   *
   */
  public interface ElementContentComparator
  {
    public boolean isIgnorable(Object o);
    public boolean isPCData(Object o);
    public boolean isElement(Object o);
    public boolean matches(Object o, CMNode cmNode);
  }

  /**
   * A linked list
   */
  public static class ElementList
  {
    protected Object head;
    protected ElementList tail;

    public static ElementList create(List v)
    {
      ElementList first = null;
      ElementList prev = null;

      for (Iterator iterator = v.iterator(); iterator.hasNext(); )
      {
        Object o = iterator.next();
        if (o != null)
        {
          ElementList list = new ElementList();
          list.head = o;

          if (prev != null)
          {
            prev.tail = list;
          }
          else
          {
            first = list;
          }
          prev = list;
        }
      }
      return first;
    }


    public String toString()
    {
      String string = "[" + head + "],"; //$NON-NLS-1$ //$NON-NLS-2$

      if (tail != null)
      {
        string += tail.toString();
      }

      return string;
    }
  }

  /**
   * StringElementContentComparator
   */
  public static class StringElementContentComparator implements ElementContentComparator
  {
    public boolean isIgnorable(Object o)
    {
      String string = o.toString();
      return string.startsWith("!") || string.startsWith("?"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isPCData(Object o)
    {
      String string = o.toString();
      return string.startsWith("'") || string.startsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isElement(Object o)
    {
      return !isIgnorable(o) && !isPCData(o);
    }

    public boolean matches(Object o, CMNode cmNode)
    {
      boolean result = false;
      if (cmNode.getNodeType() == CMNode.ELEMENT_DECLARATION)
      { 
        CMElementDeclaration element = (CMElementDeclaration)cmNode;
        String name = o.toString();                              
        int index = name.indexOf("]"); //$NON-NLS-1$
        if (index != -1)
        {
          name = name.substring(index + 1);
        }
        result = name.equalsIgnoreCase(element.getElementName());        

        // TODO... here's we consider substitution groups... revisit to see if this should be moved into validator code
        if (!result)
        {
          CMNodeList cmNodeList = (CMNodeList)element.getProperty("SubstitutionGroup");   //$NON-NLS-1$
          if (cmNodeList != null)
          {
            int cmNodeListLength = cmNodeList.getLength();
            if (cmNodeListLength > 1)
            {                        
              for (int i = 0; i < cmNodeListLength; i++)
              {                                                               
                CMElementDeclaration alternativeCMElementDeclaration = (CMElementDeclaration)cmNodeList.item(i);
                String altName = alternativeCMElementDeclaration.getElementName();
                result = name.equalsIgnoreCase(altName);
                if (result)
                {
                  break;
                }
              }
            }
          }
        }         
      }   
      else if (cmNode.getNodeType() == CMNode.ANY_ELEMENT)
      {                                   
        String string = o.toString();
        if (string.equals("*")) //$NON-NLS-1$
        {
          result = true;
        }
        else
        {
          CMAnyElement anyElement = (CMAnyElement)cmNode;
          String anyElementURI = anyElement.getNamespaceURI();    
          if (anyElementURI != null)
          {           
            if (anyElementURI.equals("##any")) //$NON-NLS-1$
            {                               
              result = true;
            }
            else if (anyElementURI.equals("##other")) //$NON-NLS-1$
            {     
              result = true;    
              CMDocument cmDocument = (CMDocument)anyElement.getProperty("CMDocument");   //$NON-NLS-1$
              if (cmDocument != null)
              {
                String excludedURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI"); //$NON-NLS-1$
                if (excludedURI != null)
                { 
                  String specifiedURI = getURIForContentSpecification(string);
                  if (specifiedURI != null && excludedURI.equals(specifiedURI))
                  { 
                    result = false;
                  } 
                }
              }
            } 
            else if (anyElementURI.equals("##targetNamespace")) //$NON-NLS-1$
            {
              result = true;
              CMDocument cmDocument = (CMDocument)anyElement.getProperty("CMDocument");   //$NON-NLS-1$
              if (cmDocument != null)
              {     
                String targetNamespaceURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI"); //$NON-NLS-1$
                String specifiedURI = getURIForContentSpecification(string);
                if (specifiedURI != null && !targetNamespaceURI.equals(specifiedURI))
                { 
                  result = false;
                } 
              }
            }
            else  
            {        
              result = true;
              String specifiedURI = getURIForContentSpecification(string);
              if (specifiedURI != null && !anyElementURI.equals(specifiedURI))
              { 
                result = false;
              }      
            }
          }  
          else 
          {          
            result = true;
          }        
        }
      }
      return result;
    }     
    

    protected String getURIForContentSpecification(String specification)
    {           
      String result = null;
      int index = specification.indexOf("]"); //$NON-NLS-1$
      if (index != -1)
      {                
        result = specification.substring(1, index);
      } 
      return result;  
    }
  }      

  public static List createStringList(String arg[], int startIndex)
  {
    Vector v = new Vector();
    for (int i = startIndex; i < arg.length; i++)
    {
      v.add(arg[i]);
    }
    return v;
  }


  public static void main(String arg[])
  {
    if (arg.length > 1)
    {
      try
      {
        //CMDocumentFactoryRegistry.getInstance().registerCMDocumentBuilderWithClassName("org.eclipse.wst.xml.core.internal.contentmodel.mofimpl.CMDocumentBuilderImpl");

        String grammarFileName = arg[0];
        String elementName = arg[1];

        CMDocument cmDocument = ContentModelManager.getInstance().createCMDocument(grammarFileName, null);
        
        CMNamedNodeMap elementMap = cmDocument.getElements();
        CMElementDeclaration element = (CMElementDeclaration)elementMap.getNamedItem(elementName);
        if (element != null)
        { /*
          println("found element [" + elementName + "]  contentType = " + element.getContentType());
          GraphNode graphNode = createGraph(element);
          printGraph(graphNode);
          */
          println("-------------- begin validate ---------------"); //$NON-NLS-1$

          StringElementContentComparator comparator = new StringElementContentComparator();
          CMValidator validator = new CMValidator();
          ElementPathRecordingResult result = new ElementPathRecordingResult();
          validator.getOriginArray(element, createStringList(arg, 2), comparator, result);
          if (result.isValid)
          {
            CMNode[] nodeMapping = result.getOriginArray();
            println("Validation Success!"); //$NON-NLS-1$
            print("  "); //$NON-NLS-1$
            for (int i = 0; i < nodeMapping.length; i++)
            {
              String name = nodeMapping[i] != null ? nodeMapping[i].getNodeName() : "null"; //$NON-NLS-1$
              print("[" + name + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            println(""); //$NON-NLS-1$
          }
          else
          {
            println("Validation Failed! "); //$NON-NLS-1$
            if (result.errorMessage != null)
            {
              println("  " + result.errorMessage); //$NON-NLS-1$
            }
          }
          println("-------------- end validate ---------------"); //$NON-NLS-1$
        }
        else
        {
          println("element [" + elementName + "] can not be found"); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      catch (Exception e)
      {
        println("CMValidator error"); //$NON-NLS-1$
        e.printStackTrace();
      }
    }
    else
    {
      println("2 args required... only " + arg.length + " provided"); //$NON-NLS-1$ //$NON-NLS-2$
      println("usage java org.eclipse.wst.newxml.util.XMLUtil grammarFileName rootElementName pattern"); //$NON-NLS-1$
    }
  }

  public static void print(String string)
  {  
  }
	  
  public static void println(String string)
  {  
  }
  
  public static void printlnIndented(int indent, String string)
  {
  }
}
