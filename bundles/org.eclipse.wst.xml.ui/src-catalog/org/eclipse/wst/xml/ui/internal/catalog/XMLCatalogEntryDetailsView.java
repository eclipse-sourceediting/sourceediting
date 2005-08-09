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
package org.eclipse.wst.xml.ui.internal.catalog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;



                       
public class XMLCatalogEntryDetailsView
{                      
  public static final String copyright = "(c) Copyright IBM Corporation 2002.";
  protected Text detailsText;
  protected ScrollBar verticalScroll, horizontalScroll;

  public XMLCatalogEntryDetailsView(Composite parent) 
  {
    Color color = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND); 
                
	detailsText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

	GridData data = new GridData(GridData.FILL_BOTH);	
	data.heightHint = 65;
	detailsText.setLayoutData(data);

    verticalScroll = detailsText.getVerticalBar();
    //verticalScroll.setVisible(false);
    horizontalScroll = detailsText.getHorizontalBar();
    detailsText.setEditable(false);
    detailsText.setBackground(color);
  }

  public void setCatalogElement(ICatalogEntry entry)
  {                       
    String value = getDisplayValue(entry != null ? entry.getURI() : "");
    String line1 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_COLON + "\t\t" + value;
    
    value = entry != null ? getKeyTypeValue(entry) : "";
    String line2 = XMLCatalogMessages.UI_KEY_TYPE_DETAILS_COLON + "\t" + value;

    value = getDisplayValue(entry != null ? entry.getKey() : "");
    String line3 = XMLCatalogMessages.UI_LABEL_DETAILS_KEY_COLON + "\t\t" + value;

    String entireString = "\n" + line1 + "\n" + line2 + "\n" + line3;
    detailsText.setText(entireString);
  }  
  
  public void setCatalogElement(INextCatalog nextCatalog)
  {                       
    String value = getDisplayValue(nextCatalog != null ? nextCatalog.getCatalogLocation() : "");
    String line1 = XMLCatalogMessages.UI_LABEL_DETAILS_URI_COLON + "\t\t" + value;
    
    String entireString = "\n" + line1;
    detailsText.setText(entireString);
  }  

  protected String getDisplayValue(String string)
  {
    return string != null ? string : "";
  }      

  protected String getKeyTypeValue(ICatalogEntry entry)
  {
    String result = null; 
    if (entry.getURI() != null && entry.getURI().endsWith("xsd"))    
    {             
      result = (entry.getEntryType() == ICatalogEntry.ENTRY_TYPE_URI) ?                       
               XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_PUBLIC :
               XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_XSD_SYSTEM;
    } 
    else
    {                                             
      switch (entry.getEntryType()) {
			case ICatalogEntry.ENTRY_TYPE_PUBLIC:
				result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_PUBLIC;
				break;
			case ICatalogEntry.ENTRY_TYPE_SYSTEM:
				result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_DTD_SYSTEM;
				break;
			default:
				result = XMLCatalogMessages.UI_KEY_TYPE_DESCRIPTION_URI;
				break;
			}

    }
    return result;
  }
}
