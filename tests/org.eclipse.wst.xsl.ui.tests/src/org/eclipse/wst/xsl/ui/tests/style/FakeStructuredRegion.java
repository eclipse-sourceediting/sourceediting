package org.eclipse.wst.xsl.ui.tests.style;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;

public class FakeStructuredRegion extends BasicStructuredDocumentRegion {

	@Override
	public ITextRegion getFirstRegion() {
		return null;
	}
}
