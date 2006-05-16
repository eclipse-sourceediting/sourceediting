package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;


public abstract class NodeEditorProvider
{
  public abstract NodeEditorConfiguration getNodeEditorConfiguration(String parentName, String nodeName); 
  //public abstract NodeEditorConfiguration getNodeEditorConfiguration(Node node);   
}
