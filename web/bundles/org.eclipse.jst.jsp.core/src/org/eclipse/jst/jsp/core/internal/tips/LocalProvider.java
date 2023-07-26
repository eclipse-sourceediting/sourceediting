package org.eclipse.jst.jsp.core.internal.tips;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.tips.core.TipImage;

public class LocalProvider extends org.eclipse.tips.core.TipProvider {

	@Override
	public void dispose() {
		
	}

	@Override
	public String getDescription() {
		return "Tips for working with JSPs";
	}

	@Override
	public String getID() {
		return "org.eclipse.jst.jsp.core.internal.tips.LocalProvider";
	}

	@Override
	public TipImage getImage() {
		return null;
	}

	@Override
	public IStatus loadNewTips(IProgressMonitor monitor) {
		return null;
	}

}
