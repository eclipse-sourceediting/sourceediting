package org.eclipse.wst.css.ui.internal.projection;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.ui.internal.projection.IStructuredTextFoldingProvider;

/**
 * Updates the projection model of a structured model for CSS.
 */
public class StructuredTextFoldingProviderCSS implements IStructuredTextFoldingProvider, IProjectionListener {
	private final static boolean debugProjectionPerf = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.css.ui/projectionperf")); //$NON-NLS-1$ //$NON-NLS-2$\

	private IDocument fDocument;
	private ProjectionViewer fViewer;

	/**
	 * Just add adapter to top stylesheet node. This adapter will track
	 * children addition/deletion.
	 */
	private void addAllAdapters() {
		long start = System.currentTimeMillis();

		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel instanceof ICSSModel) {
				ICSSModel cssModel = (ICSSModel) sModel;
				ICSSDocument cssDoc = cssModel.getDocument();
				if (cssDoc instanceof INodeNotifier) {
					INodeNotifier notifier = (INodeNotifier) cssDoc;
					ProjectionModelNodeAdapterCSS adapter = (ProjectionModelNodeAdapterCSS) notifier.getExistingAdapter(ProjectionModelNodeAdapterCSS.class);
					if (adapter != null) {
						adapter.updateAdapter(cssDoc);
					}
					else {
						// just call getadapter so the adapter is created and
						// automatically initialized
						notifier.getAdapterFor(ProjectionModelNodeAdapterCSS.class);
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
			System.out.println("StructuredTextFoldingProviderCSS.addAllAdapters: " + (end - start)); //$NON-NLS-1$
	}

	/**
	 * Get the ProjectionModelNodeAdapterFactoryCSS to use with this provider.
	 * 
	 * @return ProjectionModelNodeAdapterFactoryCSS
	 */
	private ProjectionModelNodeAdapterFactoryCSS getAdapterFactory(boolean createIfNeeded) {
		ProjectionModelNodeAdapterFactoryCSS factory = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
			if (sModel != null) {
				FactoryRegistry factoryRegistry = sModel.getFactoryRegistry();

				// getting the projectionmodelnodeadapter for the first time
				// so do some initializing
				if (!factoryRegistry.contains(ProjectionModelNodeAdapterCSS.class) && createIfNeeded) {
					ProjectionModelNodeAdapterFactoryCSS newFactory = new ProjectionModelNodeAdapterFactoryCSS();

					// add factory to factory registry
					factoryRegistry.addFactory(newFactory);
				}

				// try and get the factory
				factory = (ProjectionModelNodeAdapterFactoryCSS) factoryRegistry.getFactoryFor(ProjectionModelNodeAdapterCSS.class);
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
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
		ProjectionModelNodeAdapterFactoryCSS factory = getAdapterFactory(false);
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
		ProjectionModelNodeAdapterFactoryCSS factory = getAdapterFactory(false);
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
