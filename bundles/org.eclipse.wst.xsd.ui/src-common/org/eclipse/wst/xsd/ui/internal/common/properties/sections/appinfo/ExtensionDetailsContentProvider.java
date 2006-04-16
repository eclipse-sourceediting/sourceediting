package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

public interface ExtensionDetailsContentProvider
{
  Object[] getItems(Object input);
  String getName(Object item);
  String getValue(Object item);
  String[] getPossibleValues(Object item);
}
