package org.eclipse.wst.sse.ui.tests.viewer;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.style.IHighlighter;
import org.eclipse.wst.sse.ui.tests.Logger;

public class TestViewerConfiguration extends TestCase {
    
	private StructuredTextViewer fViewer = null;
	private StructuredTextViewerConfiguration fConfig = null;
	private IPreferenceStore fPreferenceStore = null;
	private boolean fDisplayExists = true;
	private boolean isSetup = false;
    public TestViewerConfiguration() {
        super("TestViewerConfiguration");
    }
    protected void setUp() throws Exception {
		
    	super.setUp();
		if(!this.isSetup){
			setUpPreferences();
			setUpViewerConfiguration();
			this.isSetup = true;
		}
    }
	
    private void setUpPreferences() {
		fPreferenceStore = SSEUIPlugin.getDefault().getPreferenceStore();
		fPreferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
	}
	
	private void setUpViewerConfiguration() {
		if (Display.getCurrent() != null) {
			
			Shell shell = null;
			Composite parent = null;
			
			if(PlatformUI.isWorkbenchRunning()) {
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			else {	
				shell = new Shell(Display.getCurrent());
			}
			parent = new Composite(shell, SWT.NONE);
			
			// dummy viewer
			fViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
			fConfig = new StructuredTextViewerConfiguration(fPreferenceStore);
		}
		else {
			fDisplayExists = false;
			Logger.log(Logger.INFO, "TestViewerConfiguration tests cannot run because there is no DISPLAY available");
		}
	}
    
	public void testGetAutoEditStrategies() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IAutoEditStrategy[] strategies = fConfig.getAutoEditStrategies(fViewer, IStructuredPartitionTypes.DEFAULT_PARTITION);
		assertNotNull(strategies);
		assertTrue("there are no auto edit strategies", strategies.length > 0);
	}
	
	public void testGetConfiguredContentTypes() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] configuredContentTypes = fConfig.getConfiguredContentTypes(fViewer);
		assertNotNull(configuredContentTypes);
		assertTrue(configuredContentTypes.length == 1);
	}
	
	public void testGetContentAssistant() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IContentAssistant ca = fConfig.getContentAssistant(fViewer);
		assertNotNull("there is no content assistant", ca);
	}
	
	public void testGetCorrectionAssistant() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IContentAssistant ca = fConfig.getCorrectionAssistant(fViewer);
		assertNotNull("there is no correction assistant", ca);
	}
	
	public void testGetContentFormatter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IContentFormatter cf = fConfig.getContentFormatter(fViewer);
		assertNull("there is a content formatter", cf);
	}
	
	public void testGetDoubleClickStrategy() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] contentTypes = fConfig.getConfiguredContentTypes(fViewer);
		for (int i = 0; i < contentTypes.length; i++) {
			ITextDoubleClickStrategy strategy = fConfig.getDoubleClickStrategy(fViewer, contentTypes[i]);
			if(strategy != null) {
				return;
			}
		}
		assertTrue("there are no configured double click strategies", false);
	}
	
	public void testGetHighlighter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IHighlighter highlighter = fConfig.getHighlighter(fViewer);
		assertNotNull("Highlighter is null", highlighter);
	}
	
	public void testGetInformationPresenter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IInformationPresenter presenter = fConfig.getInformationPresenter(fViewer);
		assertNull("InformationPresenter is not null", presenter);
	}
	
    public void testGetAnnotationHover() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
 
		IAnnotationHover hover = fConfig.getAnnotationHover(fViewer);
		assertNotNull("AnnotationHover is null", hover);
    }
	
	public void testUnconfigure() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		
		fConfig.unConfigure(fViewer);
		// need a good test here to make sure thing are really unconfigured
		
		// need to re-set up since it's likely
		// more tests are called after this one
		setUpViewerConfiguration();
	}
	
	public void testGetReconciler() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IReconciler r = fConfig.getReconciler(fViewer);
		assertNull("Reconciler is not null", r);
	}
	
	public void testGetHyperlinkDetectors() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IHyperlinkDetector[] detectors = fConfig.getHyperlinkDetectors(fViewer);
		assertNotNull(detectors);
		assertTrue(detectors.length == 1);
	}
	
	
	public void testConfigureOn() {
		// TODO: get a resource
	}
	
	public void testGetConfiguredDocumentPartitioning() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String partitioning = fConfig.getConfiguredDocumentPartitioning(fViewer);
		assertEquals(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, partitioning);
	}
	
	public void testGetConfiguredTextHoverStateMasks() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		int[] masks = fConfig.getConfiguredTextHoverStateMasks(fViewer, IStructuredPartitionTypes.DEFAULT_PARTITION);
		assertEquals(2, masks.length);
	}
	
	public void testGetSetDeclaringID() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String origID = fConfig.getDeclaringID();
		
		fConfig.setDeclaringID("testID");
		String gottenID = fConfig.getDeclaringID();
		assertEquals("testID", gottenID);
		
		// to not mess up other tests
		fConfig.setDeclaringID(origID);
	}
	public void testGetSetEditorPart() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IEditorPart origPart = fConfig.getEditorPart();
		
		fConfig.setEditorPart(null);
		IEditorPart gottenPart = fConfig.getEditorPart();
		assertNull(gottenPart);
		
		// don't mess up other tests
		fConfig.setEditorPart(origPart);
	}

	public void testGetHyperlinkPresenter() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IHyperlinkPresenter presenter = fConfig.getHyperlinkPresenter(fViewer);
		assertNotNull("hyperlink presenter shouldn't be null", presenter);
	}
	
	public void testGetInformationControlCreator() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IInformationControlCreator infoControlCreator = fConfig.getInformationControlCreator(fViewer);
		assertNotNull("info control creator was null", infoControlCreator);
	}
	
	public void testGetOverviewRulerAnnotationHover() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IAnnotationHover annotationHover = fConfig.getOverviewRulerAnnotationHover(fViewer);
		assertNotNull("annotation hover was null", annotationHover);
	}
	public void testGetPresentationReconciler() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IPresentationReconciler presentationReconciler = fConfig.getPresentationReconciler(fViewer);
		// our default presentation reconciler is null
		assertNull("presentation reconciler was not null", presentationReconciler);
	}
	public void testGetUndoManager() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		IUndoManager undoManager = fConfig.getUndoManager(fViewer);
		assertNotNull("undo manager was null", undoManager);
	}
	public void testSetPreferenceStore() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		// not much to tests since there's no getter
		IPreferenceStore sseStore = SSEUIPlugin.getDefault().getPreferenceStore();
		fConfig.setPreferenceStore(sseStore);
	}
}
