package org.eclipse.wst.html.ui.tests.viewer;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.style.IHighlighter;

/**
 * @author pavery
 */
public class TestViewerConfigurationHTML extends TestCase {
    
	private StructuredTextViewer fViewer = null;
	private StructuredTextViewerConfiguration fConfig = null;
	private IPreferenceStore fPreferenceStore = null;
	
    public TestViewerConfigurationHTML() {
        super("TestViewerConfigurationJSP");
    }
    protected void setUp() throws Exception {
		
    	super.setUp();
		setUpPreferences();
		setUpViewerConfiguration();
    }
	
    private void setUpPreferences() {
		fPreferenceStore = HTMLUIPlugin.getDefault().getPreferenceStore();
		fPreferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
	}
	
	private void setUpViewerConfiguration() {
		Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
		Composite parent = shell.getParent();
		Control[] children = shell.getChildren();
		// kind of a hack just to satisfy the Composite 
		// parent requirement for viewer
		for (int i = 0; i < children.length; i++) {
			if(children[i] instanceof Composite) {
				parent = (Composite)children[i];
				break;
			}
		}
		// dummy viewer
		fViewer = new StructuredTextViewer(parent, null, null, false, SWT.NONE);
		fConfig = new StructuredTextViewerConfigurationHTML(fPreferenceStore);
	}
    
	public void testGetAutoEditStrategies() {
		
		Map strategies = fConfig.getAutoEditStrategies(fViewer);
		assertNotNull(strategies);
		assertTrue("there are no auto edit strategies", strategies.size()>1);
	}
	
	public void testGetConfiguredContentTypes() {
		String[] configuredContentTypes = fConfig.getConfiguredContentTypes(fViewer);
		assertNotNull(configuredContentTypes);
		assertTrue("there are no configured content types", configuredContentTypes.length > 1);
	}
	
	public void testGetContentAssistant() {
		IContentAssistant ca = fConfig.getContentAssistant(fViewer);
		assertNotNull("there is no content assistant", ca);
	}
	
	public void testGetCorrectionAssistant() {
		IContentAssistant ca = fConfig.getCorrectionAssistant(fViewer);
		assertNotNull("there is no correction assistant", ca);
	}
	
	public void testGetContentFormatter() {
		IContentFormatter cf = fConfig.getContentFormatter(fViewer);
		assertNotNull("there is no content formatter", cf);
	}
	
	public void testGetDoubleClickStrategy() {
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
		IHighlighter highlighter = fConfig.getHighlighter(fViewer);
		assertNotNull("Highlighter is null", highlighter);
	}
	
	public void testGetInformationPresenter() {
		IInformationPresenter presenter = fConfig.getInformationPresenter(fViewer);
		assertNotNull("InformationPresenter is null", presenter);
	}
	
    public void testGetAnnotationHover() {
 
		IAnnotationHover hover = fConfig.getAnnotationHover(fViewer);
		assertNotNull("AnnotationHover is null", hover);
    }
	
	public void testUnconfigure() {
		
		fConfig.unConfigure(fViewer);
		// need a good test here to make sure thing are really unconfigured
		
		// need to re-set up since it's likely
		// more tests are called after this one
		setUpViewerConfiguration();
	}
	
	public void testGetReconciler() {
		IReconciler r = fConfig.getReconciler(fViewer);
		assertNotNull("Reconciler is null", r);
	}
	
	public void testGetHyperlinkDetectors() {
		IHyperlinkDetector[] detectors = fConfig.getHyperlinkDetectors(fViewer);
		assertNotNull(detectors);
		assertTrue("there are no hyperlink detectors", detectors.length > 1);
	}
}
