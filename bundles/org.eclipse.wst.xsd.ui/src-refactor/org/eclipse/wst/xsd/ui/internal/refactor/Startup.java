package org.eclipse.wst.xsd.ui.internal.refactor;


import org.eclipse.ui.IStartup;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;

public class Startup implements IStartup {

	public void earlyStartup() {
		XSDEditorPlugin.getPlugin();

	}

}
