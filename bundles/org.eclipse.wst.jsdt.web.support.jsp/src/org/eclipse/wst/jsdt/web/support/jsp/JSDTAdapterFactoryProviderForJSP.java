package org.eclipse.wst.jsdt.web.support.jsp;

import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.wst.jsdt.web.ui.internal.registry.AdapterFactoryProviderForJSDT;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;

public class JSDTAdapterFactoryProviderForJSP extends
		AdapterFactoryProviderForJSDT {

	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForJSP);
	}
}
