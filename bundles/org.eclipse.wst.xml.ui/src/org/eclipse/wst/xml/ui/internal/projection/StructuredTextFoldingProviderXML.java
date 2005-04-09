package org.eclipse.wst.xml.ui.internal.projection;

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
 * Updates the projection model of a structured model for XML.
 */
public class StructuredTextFoldingProviderXML implements IStructuredTextFoldingProvider, IProjectionListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.xml.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$

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
			ProjectionModelNodeAdapterXML adapter = (ProjectionModelNodeAdapterXML) notifier.getExistingAdapter(ProjectionModelNodeAdapterXML.class);
			if (adapter != null) {
				adapter.updateAdapter(node);
			}
			else {
				// just call getadapter so the adapter is created and
				// automatically initialized
				notifier.getAdapterFor(ProjectionModelNodeAdapterXML.class);
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
			System.out.println("StructuredTextFoldingProviderXML.addAllAdapters: " + (end - start));
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryXML to use with this provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryXML
	 */
	private ProjectionModelNodeAdapterFactoryXML getAdapterFactory(boolean createIfNeeded) {
		long start = System.currentTimeMillis();

		ProjectionModelNodeAdapterFactoryXML factory = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel != null) {
				FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

				// getting the projectionmodelnodeadapter for the first time
				// so do some initializing
				if (!factoryRegistry.contains(ProjectionModelNodeAdapterXML.class) && createIfNeeded) {
					ProjectionModelNodeAdapterFactoryXML newFactory = new ProjectionModelNodeAdapterFactoryXML();

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
				factory = (ProjectionModelNodeAdapterFactoryXML) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterXML.class);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		long end = System.currentTimeMillis();
		if (debugProjectionPerf)
			System.out.println("StructuredTextFoldingProviderXML.getAdapterFactory: " + (end - start));
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
		ProjectionModelNodeAdapterFactoryXML factory = getAdapterFactory(false);
		if (factory != null) {
			factory.setProjectionViewer(null);
		}
		// clear out all annotations
		if (fViewer.getProjectionAnnotationModel() != null)
			fViewer.getProjectionAnnotationModel().removeAllAnnotations();
		
		fDocument = fViewer.getDocument();

		if (fDocument != null) {
			// set projection viewer on new document's adapter factory
			factory = getAdapterFactory(true);
			if (factory != null) {
				factory.setProjectionViewer(fViewer);
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
		ProjectionModelNodeAdapterFactoryXML factory = getAdapterFactory(false);
		if (factory != null) {
			factory.setProjectionViewer(null);
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
