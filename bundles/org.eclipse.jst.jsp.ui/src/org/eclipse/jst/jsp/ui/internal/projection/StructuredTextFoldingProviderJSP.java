package org.eclipse.jst.jsp.ui.internal.projection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Updates the projection model of a structured model for JSP.
 */
public class StructuredTextFoldingProviderJSP implements IStructuredTextFoldingProvider, IProjectionListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$
	
	private IDocument fDocument;
	private ProjectionViewer fViewer;

	/**
	 * Adds an adapter to node and its children
	 * 
	 * @param node
	 * @param level
	 */
	private void addAdapterToNodeAndChildren(Node node, int level) {
		if (node instanceof INodeNotifier) {
			INodeNotifier notifier = (INodeNotifier) node;

			// try and get the adapter for the current node and update the
			// adapter with projection information
			ProjectionModelNodeAdapterJSP adapter = (ProjectionModelNodeAdapterJSP) notifier.getExistingAdapter(ProjectionModelNodeAdapterJSP.class);
			if (adapter != null) {
				adapter.updateAdapter(node);
			}
			else {
				// just call getadapter so the adapter is created and
				// automatically initialized
				notifier.getAdapterFor(ProjectionModelNodeAdapterJSP.class);
			}
			ProjectionModelNodeAdapterHTML adapter2 = (ProjectionModelNodeAdapterHTML) notifier.getExistingAdapter(ProjectionModelNodeAdapterHTML.class);
			if (adapter2 != null) {
				adapter2.updateAdapter(node);
			}
			else {
				// just call getadapter so the adapter is created and
				// automatically initialized
				notifier.getAdapterFor(ProjectionModelNodeAdapterHTML.class);
			}

			Node nextChild = node.getFirstChild();
			while (nextChild != null) {
				Node childNode = nextChild;
				nextChild = childNode.getNextSibling();

				addAdapterToNodeAndChildren(childNode, level + 1);
			}
		}
	}

	/**
	 * Goes through every node and adds an adapter onto each for tracking
	 * purposes
	 */
	private void addAllAdapters() {
		long start = System.currentTimeMillis();
		
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel != null) {
				int startOffset = 0;
				IndexedRegion startNode = sModel.getIndexedRegion(startOffset);
				if (startNode instanceof Node) {
					int level = 0;
					Node nextSibling = (Node) startNode;
					while (nextSibling != null) {
						Node currentNode = nextSibling;
						nextSibling = currentNode.getNextSibling();

						addAdapterToNodeAndChildren(currentNode, level);
					}
				}
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderJSP.addAllAdapters: " + (end - start)); //$NON-NLS-1$
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryHTML to use with this
	 * provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryHTML
	 */
	private ProjectionModelNodeAdapterFactoryHTML getAdapterFactoryHTML(boolean createIfNeeded) {
		long start = System.currentTimeMillis();
		
		ProjectionModelNodeAdapterFactoryHTML factory = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel != null) {
				FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

				// getting the projectionmodelnodeadapter for the first time
				// so do some initializing
				if (!factoryRegistry.contains(ProjectionModelNodeAdapterHTML.class) && createIfNeeded) {
					ProjectionModelNodeAdapterFactoryHTML newFactory = new ProjectionModelNodeAdapterFactoryHTML();

					// add factory to factory registry
					factoryRegistry.addFactory(newFactory);

					// add factory to propogating adapter
					IDOMModel domModel = (IDOMModel) sModel;
					Document document = domModel.getDocument();
					PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) document).getAdapterFor(PropagatingAdapter.class);
					if (propagatingAdapter != null) {
						propagatingAdapter.addAdaptOnCreateFactory(newFactory);
					}
				}

				// try and get the factory
				factory = (ProjectionModelNodeAdapterFactoryHTML) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterHTML.class);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		
		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderJSP.getAdapterFactoryHTML: " + (end - start)); //$NON-NLS-1$
		return factory;
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryJSP to use with this provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryJSP
	 */
	private ProjectionModelNodeAdapterFactoryJSP getAdapterFactoryJSP(boolean createIfNeeded) {
		long start = System.currentTimeMillis();
		
		ProjectionModelNodeAdapterFactoryJSP factory = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel != null) {
				FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

				// getting the projectionmodelnodeadapter for the first time
				// so do some initializing
				if (!factoryRegistry.contains(ProjectionModelNodeAdapterJSP.class) && createIfNeeded) {
					ProjectionModelNodeAdapterFactoryJSP newFactory = new ProjectionModelNodeAdapterFactoryJSP();

					// add factory to factory registry
					factoryRegistry.addFactory(newFactory);

					// add factory to propogating adapter
					IDOMModel domModel = (IDOMModel) sModel;
					Document document = domModel.getDocument();
					PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) document).getAdapterFor(PropagatingAdapter.class);
					if (propagatingAdapter != null) {
						propagatingAdapter.addAdaptOnCreateFactory(newFactory);
					}
				}

				// try and get the factory
				factory = (ProjectionModelNodeAdapterFactoryJSP) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterJSP.class);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		
		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderJSP.getAdapterFactoryJSP: " + (end - start)); //$NON-NLS-1$
		return factory;
	}

	/**
	 * Initialize this provider with the correct document. Assumes projection
	 * is enabled. (otherwise, only install would have been called)
	 */
	public void initialize() {
		if (!isInstalled())
			return;

		// set projection viewer to null on old document's adapter factory
		projectionDisabled();

		// clear out all annotations
		if (fViewer.getProjectionAnnotationModel() != null)
			fViewer.getProjectionAnnotationModel().removeAllAnnotations();
		fDocument = fViewer.getDocument();

		if (fDocument != null) {
			// set projection viewer on new document's adapter factory
			ProjectionModelNodeAdapterFactoryJSP factory = getAdapterFactoryJSP(true);
			if (factory != null) {
				factory.setProjectionViewer(fViewer);
			}
			ProjectionModelNodeAdapterFactoryHTML factory2 = getAdapterFactoryHTML(true);
			if (factory2 != null) {
				factory2.setProjectionViewer(fViewer);
			}

			addAllAdapters();
		}
	}

	/**
	 * Associate a ProjectionViewer with this IStructuredTextFoldingProvider
	 * 
	 * @param viewer
	 */
	public void install(ProjectionViewer viewer) {
		// uninstall before trying to install new viewer
		if (isInstalled()) {
			uninstall();
		}
		fViewer = viewer;
		fViewer.addProjectionListener(this);
	}

	private boolean isInstalled() {
		return fViewer != null;
	}

	public void projectionDisabled() {
		ProjectionModelNodeAdapterFactoryJSP factory = getAdapterFactoryJSP(false);
		if (factory != null) {
			factory.setProjectionViewer(null);
		}
		ProjectionModelNodeAdapterFactoryHTML factory2 = getAdapterFactoryHTML(false);
		if (factory2 != null) {
			factory2.setProjectionViewer(null);
		}

		fDocument = null;
	}

	public void projectionEnabled() {
		initialize();
	}

	/**
	 * Disconnect this IStructuredTextFoldingProvider from projection viewer
	 */
	public void uninstall() {
		if (isInstalled()) {
			projectionDisabled();

			fViewer.removeProjectionListener(this);
			fViewer = null;
		}
	}
}
