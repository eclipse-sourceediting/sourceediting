package org.eclipse.wst.sse.ui.tests.viewer;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.tests.Logger;

/**
 * @author pavery
 */
public class TestViewerConfiguration extends TestCase {
    
	private StructuredTextViewer fViewer = null;
	private StructuredTextViewerConfiguration fConfig = null;
	private IPreferenceStore fPreferenceStore = null;
	private boolean fDisplayExists = true;
	
    public TestViewerConfiguration() {
        super("TestViewerConfigurationXML");
    }
    protected void setUp() throws Exception {
		
    	super.setUp();
		setUpPreferences();
		setUpViewerConfiguration();
    }
	
    private void setUpPreferences() {
		fPreferenceStore = EditorPlugin.getDefault().getPreferenceStore();
		fPreferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
	}
	
	private void setUpViewerConfiguration() {
		if (Display.getCurrent() == null) {
			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
			Composite parent = shell.getParent();
			Control[] children = shell.getChildren();
			// kind of a hack just to satisfy the Composite
			// parent requirement for viewer
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof Composite) {
					parent = (Composite) children[i];
					break;
				}
			}
			// dummy viewer
			fViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
			fConfig = new StructuredTextViewerConfiguration(fPreferenceStore);
		}
		else {
			fDisplayExists = false;
			Logger.log(Logger.INFO, "TestViewerConfigurationHTML tests cannot run because there is no DISPLAY available");
		}
	}
    
	public void testGetAutoEditStrategies() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		Map strategies = fConfig.getAutoEditStrategies(fViewer);
		assertNotNull(strategies);
		assertTrue("there are no auto edit strategies", strategies.size()==0);
	}
	
	public void testGetConfiguredContentTypes() {
		
		// probably no display
		if(!fDisplayExists)
			return;
		
		String[] configuredContentTypes = fConfig.getConfiguredContentTypes(fViewer);
		assertNotNull(configuredContentTypes);
		assertTrue(configuredContentTypes.length == 1);
	}
	
	public void testGetContenistant() {
		
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
}
