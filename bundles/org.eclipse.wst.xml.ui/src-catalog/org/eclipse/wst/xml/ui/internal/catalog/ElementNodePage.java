
package org.eclipse.wst.xml.ui.internal.catalog;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;


public abstract class ElementNodePage {
	
	Control fControl;
	
	public ElementNodePage() {
		super();
		
	}
	
	public abstract Control createControl(Composite parent);
	
	public Control getControl(){
		return fControl;
	}
	
	public abstract void saveData();
	
	public abstract ICatalogElement getData();
}
