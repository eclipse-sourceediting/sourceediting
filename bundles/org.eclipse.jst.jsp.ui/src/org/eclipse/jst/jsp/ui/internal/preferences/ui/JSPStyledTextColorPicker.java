package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.jst.jsp.ui.internal.style.IStyleConstantsJSP;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.StyledTextColorPicker;

/**
* Overrides StyledTextColorPicker for special enablement behavior 
* for JSPContent (only background settable)
**/
public class JSPStyledTextColorPicker extends StyledTextColorPicker {
	
	public JSPStyledTextColorPicker(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Activate controls based on the given local color type.
	 * Overridden to disable foreground color, bold.
	 */
	protected void activate(String namedStyle) {
		if(namedStyle != null && namedStyle == IStyleConstantsJSP.JSP_CONTENT) {
			fForeground.setEnabled(false);
			fBackground.setEnabled(true);
			fClearStyle.setEnabled(true);
			fBold.setEnabled(false);
			if (showItalic)
				fItalic.setEnabled(false);
			fForegroundLabel.setEnabled(false);
			fBackgroundLabel.setEnabled(false);
		}
		else {
			super.activate(namedStyle);
		}
	}
}
