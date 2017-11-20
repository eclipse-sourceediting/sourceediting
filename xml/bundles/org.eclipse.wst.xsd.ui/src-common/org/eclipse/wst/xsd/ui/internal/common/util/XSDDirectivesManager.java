/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class performs cleanup/removal of unused XSD imports and includes from XML Schemas
 * and xmlns entries in the namespace table
 */
public class XSDDirectivesManager
{
  protected static final String XMLNS = "xmlns"; //$NON-NLS-1$
  // List of all the unused directives.  These will be removed
  protected List unusedDirectives = new ArrayList();
  // List of all the included XSDSchema's
  protected List usedIncludeSchemas = new ArrayList();
  // List of all the XSDInclude's that are indirectly used
  protected List usedIndirectIncludes = new ArrayList();
  // List of the XSDInclude's that are directly used
  protected List usedDirectIncludes = new ArrayList();
  // Used to track the included schemas that were analyzed to avoid circular loop
  protected List analyzedIncludeSchemas = new ArrayList();
  // List of all the imported used schemas
  protected List usedSchemas = new ArrayList();
  // List of all the indirectly used includes  
  protected Map usedIndirectIncludesMap = new HashMap();
  // Keep track of all the unused prefixes
  protected Set unusedPrefixes;
  // Keep track of all the used prefixes
  protected Set usedPrefixes;
  // Map of each schema's unused prefixes
  protected Map schemaToPrefixMap;

  public static void removeUnusedXSDImports(XSDSchema schema)
  {
    // Only do the removal if the preference is turned on
    if (XSDEditorPlugin.getDefault().getRemoveImportSetting())
    {
      XSDDirectivesManager mgr = new XSDDirectivesManager();
      mgr.performRemoval(schema);
      mgr.cleanup();
    }
  }
  
  /**
   * Main method to do the cleanup
   * @param schema
   */
  public void performRemoval(XSDSchema schema)
  {
    // Compute unused imports and unused prefixes
    computeUnusedImports(schema);
    // Remove the imports
    removeUnusedImports();
    // Remove the prefixes
    removeUnusedPrefixes();
  }
  
  /**
   * Clients can manually clean the lists
   */
  public void cleanup()
  {
    clearMaps();
  }

  /**
   * After performing the cleanup, return the list of unused XSD directives
   *  
   * @return list of unused XSD directives
   */
  public List getUnusedXSDDirectives()
  {
    return unusedDirectives;
  }
  
  /**
   * After performing the cleanup, return the map of each schemas unused prefixes
   * 
   * @return map of each schemas unused prefixes
   */
  public Map getSchemaToPrefixMap()
  {
    return schemaToPrefixMap;
  }

  /**
   * Returns the set of unused prefixes from the XML namespace table
   * 
   * @return set of unused prefixes
   */
  public Set getUnusedPrefixes()
  {
    return unusedPrefixes;
  }

  /**
   * Returns the set of used prefixes from the XML namespace table
   * 
   * @return set of used prefixes
   */
  public Set getUsedPrefixes()
  {
    return usedPrefixes;
  }

  /**
   * Perform any cleanup after computing and removing the unused imports.
   */
  protected void clearMaps()
  {
    if (schemaToPrefixMap != null)
    {
      schemaToPrefixMap.clear();
    }
  }

  /**
   * Remove the list of all unused imports.  Imports is used in the generic term here.
   */
  protected void removeUnusedImports()
  {
    Iterator iter = unusedDirectives.iterator();
    while (iter.hasNext())
    {
      XSDSchemaDirective xsdDirective = (XSDSchemaDirective) iter.next();
      removeXSDDirective(xsdDirective);
    }
  }

  /**
   * Removes the directive from the model
   * @param xsdImport
   */
  protected void removeXSDDirective(XSDSchemaDirective xsdImport)
  {
    XSDSchema schema = xsdImport.getSchema();
    
    Element element = xsdImport.getElement();
    
    Document doc = element.getOwnerDocument();

    if (doc instanceof IDOMNode)
      ((IDOMNode)doc).getModel().aboutToChangeModel();
    
    try
    {
      if (!removeTextNodesBetweenNextElement(element))
      {
        removeTextNodeBetweenPreviousElement(element);
      }
      element.getParentNode().removeChild(element);
    }
    finally
    {
      if (doc instanceof IDOMNode)
      {
        ((IDOMNode)doc).getModel().changedModel();
      }
      schema.update(true);
    }

  }

  /**
   * This computes the list of unused imports for a schema
   * @param schema
   */
  protected void computeUnusedImports(XSDSchema schema)
  {
    unusedDirectives = new ArrayList();
    usedSchemas = new ArrayList();
    usedPrefixes = new HashSet();
    schemaToPrefixMap = new HashMap();
    
    try
    {
      // Step One.  Find unused imports using cross referencer
      Map xsdNamedComponentUsage = TopLevelComponentCrossReferencer.find(schema);
      
      doCrossReferencer(schema, usedSchemas, xsdNamedComponentUsage);

      // Step Two.  Update the unusedImport list given the list of used schemas obtained from cross referencing
      addToUnusedImports(schema, usedSchemas);
      
      // Step Three.  Compute unused prefixes to be removed
      computeUnusedPrefixes(schema);
    }
    catch (Exception e)
    {
      unusedDirectives.clear();
      schemaToPrefixMap.clear();
    }
  }
  
  /**
   * Computes the list of unused prefixes from the XML namespace table given a schema
   * @param schema
   */
  protected void computeUnusedPrefixes(XSDSchema schema)
  {
    Map prefixMap = schema.getQNamePrefixToNamespaceMap();
    Set definedPrefixes = prefixMap.keySet();
    Set actualSet = new HashSet();
    NamedNodeMap attributes = schema.getElement().getAttributes();
    Iterator iter = definedPrefixes.iterator();
    while (iter.hasNext())
    {
      String pref = (String)iter.next();
      if (pref == null)
      {
        if (attributes.getNamedItem(XMLNS) != null)
           actualSet.add(null);
      }
      else
      {
        if (attributes.getNamedItem(XMLNS + ":" + pref) != null) //$NON-NLS-1$
          actualSet.add(pref);
      }
    }

    unusedPrefixes = new HashSet(actualSet);
    
    usedPrefixes.add(schema.getSchemaForSchemaQNamePrefix());
    
    Element element = schema.getElement();
    
    NodeList childElements = element.getChildNodes();
    int length = childElements.getLength();
    for (int i = 0; i < length; i++)
    {
      Node node = childElements.item(i);
      if (node instanceof Element)
      {
        traverseDOMElement((Element)node, schema);
      }
    }
    
    // compute the used prefixes
    computeUsedXSDPrefixes(schema);
    
    // remove the used prefixes from the unused to get the list of unused prefixes
    unusedPrefixes.removeAll(usedPrefixes);
    
    // perform additional process from extenders
    doAdditionalProcessing(schema);
    
    schemaToPrefixMap.put(schema, unusedPrefixes);
  }
  
  /**
   * Remove unused prefixes from the XML namespace table
   */
  protected void removeUnusedPrefixes()
  {
    Set schemaSet = schemaToPrefixMap.keySet();
    Iterator iter = schemaSet.iterator();
    while (iter.hasNext())
    {
      XSDSchema schema = (XSDSchema)iter.next();
      Map prefixMap = schema.getQNamePrefixToNamespaceMap();
      Set prefixesToRemove = (Set)schemaToPrefixMap.get(schema);
      Iterator iter2 = prefixesToRemove.iterator();
      while (iter2.hasNext())
      {
        String string = (String)iter2.next();
        if (prefixMap.containsKey(string))
          prefixMap.remove(string);
      }
    }
  }
  
  /**
   * Extenders can customize
   * @param schema
   */
  protected void doAdditionalProcessing(XSDSchema schema)
  {
    // Do nothing for XSD
  }
  
  /**
   * 
   * @param schema
   */
  private void computeUsedXSDPrefixes(XSDSchema schema)
  {
    Map prefixMap = schema.getQNamePrefixToNamespaceMap();
    Set definedPrefixes = prefixMap.keySet();
    
    boolean foundEntryForTargetNamespace = false;
    String targetNamespace = schema.getTargetNamespace();
    for (Iterator iter = usedPrefixes.iterator(); iter.hasNext(); )
    {
      String key = (String) iter.next();
      String value = (String) prefixMap.get(key);
      if (targetNamespace == null && value == null)
      {
        foundEntryForTargetNamespace = true;
        break;
      }
      else if (targetNamespace != null && value != null)
      {
        if (targetNamespace.equals(value))
        {
          foundEntryForTargetNamespace = true;
          break;
        }
      }
    }
    
    if (!foundEntryForTargetNamespace)
    {
      for (Iterator iter = definedPrefixes.iterator(); iter.hasNext();)
      {
        String key = (String) iter.next();
        String value = (String) prefixMap.get(key);
        if (targetNamespace == null && value == null)
        {
          usedPrefixes.add(null);
          break;
        }
        else if (targetNamespace != null && value != null)
        {
          if (targetNamespace.equals(value))
          {
            usedPrefixes.add(key);
            break;
          }
        }
      }
    }
  }
  
  /**
   * Find prefixes that are in the document.
   * @param element
   * @param schema
   */
  private void traverseDOMElement(Element element, XSDSchema schema)
  {
    String prefix = element.getPrefix();
    usedPrefixes.add(prefix);
    
    NamedNodeMap attrs = element.getAttributes();
    int numOfAttrs = attrs.getLength();
    for (int i = 0; i < numOfAttrs; i++)
    {
      Node node = attrs.item(i);
      String attrPrefix = node.getPrefix();
      if (attrPrefix != null)
      {
        usedPrefixes.add(attrPrefix);
      }
      
      String attr = node.getLocalName();
      if (attr != null)
      {
        String value = node.getNodeValue();
        if (value == null) continue;
        if (attr.equals(XSDConstants.REF_ATTRIBUTE) ||
            attr.equals(XSDConstants.REFER_ATTRIBUTE) ||
            attr.equals(XSDConstants.TYPE_ATTRIBUTE) ||
            attr.equals(XSDConstants.BASE_ATTRIBUTE) ||
            attr.equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE) ||
            attr.equals(XSDConstants.ITEMTYPE_ATTRIBUTE))
        {
          try
          {
            usedPrefixes.add(extractPrefix(value));
          }
          catch (IndexOutOfBoundsException e)
          {
          }
        }
        else if (attr.equals(XSDConstants.MEMBERTYPES_ATTRIBUTE))
        {
          StringTokenizer tokenizer = new StringTokenizer(value);
          while (tokenizer.hasMoreTokens())
          {
            try
            {
              String token = tokenizer.nextToken();
              usedPrefixes.add(extractPrefix(token));
            }
            catch (IndexOutOfBoundsException e)
            {
            }
          }
        }
      }
    }
    
    NodeList childElements = element.getChildNodes();
    int length = childElements.getLength();
    for (int i = 0; i < length; i++)
    {
      Node node = childElements.item(i);
      if (node instanceof Element)
      {
        traverseDOMElement((Element)node, schema);
      }
    }
  }
  
  /**
   * Extract the prefix from the given string.  For example, pref:attr returns pref.
   * @param value
   * @return the prefix
   */
  protected String extractPrefix(String value)
  {
    int index = value.indexOf(':');
    if (index < 0) 
      return null;
    else
      return value.substring(0, index);
  }

  /**
   * This determines the list of referenced components and hence the used schemas from which
   * we can determine what are the unused directives
   * 
   * @param schema
   * @param unusedImportList
   * @param usedSchemas
   */
  protected void doCrossReferencer(XSDSchema schema, List usedSchemas, Map xsdNamedComponentUsage)
  {
    // Calculate additional unused imports that may have the same
    // namespace that did not get added in the initial pass
    Iterator iterator = xsdNamedComponentUsage.keySet().iterator();
    // First determine the used schemas from the cross referencer
    while (iterator.hasNext())
    {
      XSDNamedComponent namedComponent = (XSDNamedComponent) iterator.next();
      XSDSchema namedComponentSchema = namedComponent.getSchema();
      // If the named component belongs to the same schema, then continue...we
      // want to check the external references
      if (namedComponentSchema == schema)
      {
        continue;
      }
      Collection collection = (Collection) xsdNamedComponentUsage.get(namedComponent);
      Iterator iterator2 = collection.iterator();
      while (iterator2.hasNext())
      {
        Setting setting = (Setting) iterator2.next();
        Object obj = setting.getEObject();
        if (isComponentUsed(obj, schema, namedComponentSchema))
        {
          if (!usedSchemas.contains(namedComponentSchema))
            usedSchemas.add(namedComponentSchema);
        }
      }
    }
  }
  
  /**
   * Determines if the object to be analyzed is referenced by the schema 
   * @param obj
   * @param schema
   * @param targetSchema
   * @return true if the component is referenced by the schema, false if not referenced
   */
  protected boolean isComponentUsed(Object obj, XSDSchema schema, XSDSchema targetSchema)
  {
    if (obj instanceof XSDConcreteComponent)
    {
      XSDConcreteComponent component = (XSDConcreteComponent) obj;
      if (component == schema || component instanceof XSDSchema)
      {
        return false;
      }
      if (!usedIncludeSchemas.contains(targetSchema))
        usedIncludeSchemas.add(targetSchema);
      return true;
    }
    return false;
  }
  
  /**
   * From a list of used schemas, update the unusedImports list for the given schema
   * 
   * @param schema
   * @param unusedImports
   * @param usedSchemas
   */
  protected void addToUnusedImports(XSDSchema schema, List usedSchemas)
  {
    // now that we have the list of usedSchemas, get the list of unused
    // schemas by comparing this list to what is actually in the schema
    Iterator iter = schema.getContents().iterator();
    while(iter.hasNext())
    {
      Object o = iter.next();
      if (o instanceof XSDSchemaDirective)
      {
        XSDSchemaDirective directive = (XSDSchemaDirective) o;
        boolean isUsed = false;
        Iterator iter2 = usedSchemas.iterator();
        while (iter2.hasNext())
        {
          XSDSchema usedSchema = (XSDSchema) iter2.next();
          if (directive instanceof XSDImport && directive.getResolvedSchema() == usedSchema)
          {
            isUsed = true;
            break;
          }
          if (directive instanceof XSDInclude && ((XSDInclude)directive).getIncorporatedSchema() == usedSchema)
          {
            isUsed = true;
            usedDirectIncludes.add(usedSchema);
            break;
          }
          // blindly accept redefines as used
          if (directive instanceof XSDRedefine)
          {
            isUsed = true;
            break;
          }
        }
        
        // If it is an include, we need to check if it is used indirectly
        if (directive instanceof XSDInclude && !isUsed)
        {
          XSDInclude inc = (XSDInclude)directive;
          XSDSchema incSchema = inc.getIncorporatedSchema();
          if (incSchema != null)
          {
            XSDSchema usedSchema = getUsedIncludeSchema(incSchema, inc);
            if (usedSchema != null)
            {
              usedIndirectIncludes.add(directive);
              usedIndirectIncludesMap.put(directive, usedSchema);
              isUsed = true;
            }
            else
            {
              isUsed = false;
            }
          }
        }
        
        // If resolved directives are determined unused
        // If resolved directives are not already in the unused list
        // Also any redefines should be considered used
        if (!isUsed && !unusedDirectives.contains(directive) && !(directive instanceof XSDRedefine))
        {
          unusedDirectives.add(directive);
        }
      }
    }
    Iterator iter3 = usedIndirectIncludes.iterator();
    while (iter3.hasNext())
    {
      Object o = iter3.next();
      if (o instanceof XSDInclude)
      {
        XSDInclude inc = (XSDInclude)o;
        XSDSchema targetSchema = (XSDSchema)usedIndirectIncludesMap.get(inc);
        if (usedIncludeSchemas.contains(targetSchema) && usedDirectIncludes.contains(targetSchema))
        {
          unusedDirectives.add(inc);
        }
        else
        {
          usedDirectIncludes.add(targetSchema);
        }
      }
    }
  }

  /**
   * Includes can be used indirectly.   If the schema includes A which includes B, but the schema
   * references something in B, then A is indirectly used, and hence A cannot be removed.
   * 
   * @param schema
   * @param xsdInclude
   * @return the referenced schema if used, null if not used
   */
  private XSDSchema getUsedIncludeSchema(XSDSchema schema, XSDInclude xsdInclude)
  {
    XSDSchema refSchema = null;
    boolean isUsed = false;
    Iterator iter = schema.getContents().iterator();
    while (iter.hasNext())
    {
      Object o = iter.next();
      if (o instanceof XSDInclude)
      {
        XSDInclude inc = (XSDInclude)o;
        XSDSchema incSchema = inc.getIncorporatedSchema();
        if (incSchema != null)
        {
          Iterator iter2 = usedIncludeSchemas.iterator();
          while (iter2.hasNext())
          {
            XSDSchema xsdSch = (XSDSchema)iter2.next();
            if (incSchema == xsdSch)
            {
              isUsed = true;
              refSchema = incSchema;
              break;
            }
          }
          
          if (!isUsed)
          {
            if (!analyzedIncludeSchemas.contains(incSchema)) // To prevent infinite cycle
            {
              analyzedIncludeSchemas.add(incSchema);
              refSchema = getUsedIncludeSchema(incSchema, inc);
            }
          }
          if (isUsed || refSchema != null)
          {
            return refSchema;
          }
        }
      }
      else
      {
        break;
      }
    }
    return refSchema;
  }
  
  /**
   * See cross reference for more details. 
   */
  protected static class TopLevelComponentCrossReferencer extends XSDUtil.XSDNamedComponentCrossReferencer
  {
    private static final long serialVersionUID = 1L;
    
    XSDSchema schemaForSchema = XSDUtil.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
    XSDSchema schemaForXSI = XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);

    protected TopLevelComponentCrossReferencer(EObject arg0)
    {
      super(arg0);
    }

    /**
     * Returns a map of all XSDNamedComponent cross references in the content
     * tree.
     */
    public static Map find(EObject eObject)
    {
      TopLevelComponentCrossReferencer result = new TopLevelComponentCrossReferencer(eObject);
      result.crossReference();
      result.done();
      return result;
    }

    protected boolean crossReference(EObject eObject, EReference eReference, EObject crossReferencedEObject)
    {
      if (crossReferencedEObject instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent) crossReferencedEObject;

        if (namedComponent.getContainer() == schemaForSchema || 
            namedComponent.getContainer() == schemaForXSI || 
            crossReferencedEObject.eContainer() == eObject || 
            namedComponent.getName() == null)
        {
          return false;
        }

        if (namedComponent instanceof XSDTypeDefinition)
        {
          XSDTypeDefinition typeDefinition = (XSDTypeDefinition) namedComponent;
          if (!(typeDefinition.getContainer() instanceof XSDSchema))
          {
            return false;
          }
          if (typeDefinition.getName() == null)
          {
            return false;
          }
        }
        return true;
      }
      return false;
    }
  }

  /**
   * Helper method to remove Text nodes
   * @param element
   * @return
   */
  protected boolean removeTextNodesBetweenNextElement(Element element)
  {
    List nodesToRemove = new ArrayList();
    for (Node node = element.getNextSibling(); node != null; node = node.getNextSibling())
    {
      if (node.getNodeType() == Node.TEXT_NODE)
      {
        nodesToRemove.add(node);
      }
      else if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        for (Iterator j = nodesToRemove.iterator(); j.hasNext();)
        {
          Node nodeToRemove = (Node) j.next();
          nodeToRemove.getParentNode().removeChild(nodeToRemove);
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Helper method to remove Text nodes.
   * @param element
   * @return
   */
  protected boolean removeTextNodeBetweenPreviousElement(Element element)
  {
    List nodesToRemove = new ArrayList();
    for (Node node = element.getPreviousSibling(); node != null; node = node.getPreviousSibling())
    {
      if (node.getNodeType() == Node.TEXT_NODE)
      {
        nodesToRemove.add(node);
      }
      else if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        for (Iterator j = nodesToRemove.iterator(); j.hasNext();)
        {
          Node nodeToRemove = (Node) j.next();
          nodeToRemove.getParentNode().removeChild(nodeToRemove);
        }
        return true;
      }
    }
    return false;
  }
  
  /**
   * 
   * @param iFile
   * @param checkPreference - if false, ignore checking the preference setting 
   * @throws CoreException
   * @throws IOException
   */
  
  public static void removeUnusedXSDImports(IFile iFile, boolean checkPreference) throws CoreException, IOException
  {
    if (!checkPreference || XSDEditorPlugin.getDefault().getRemoveImportSetting())
    {
      IDOMModel model = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(iFile);
      if (model != null)
      {
        Document document = model.getDocument();
        if (document != null)
        {
          XSDSchema schema = XSDModelAdapter.lookupOrCreateSchema(document);
          XSDDirectivesManager mgr = new XSDDirectivesManager();
          mgr.performRemoval(schema);
          mgr.cleanup();
          model.save();
        }
      }
    }
  }
}
