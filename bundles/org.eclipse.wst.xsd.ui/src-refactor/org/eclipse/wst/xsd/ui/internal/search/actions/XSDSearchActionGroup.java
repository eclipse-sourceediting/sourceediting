package org.eclipse.wst.xsd.ui.internal.search.actions;

import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionGroup;

public class XSDSearchActionGroup extends ActionGroup
{
  private ReferencesSearchGroup fReferencesGroup;
  private DeclarationsSearchGroup fDeclarationsGroup;
  private ImplementorsSearchGroup fImplementorsGroup;
  private OccurrencesSearchGroup fOccurrencesGroup;
  private IEditorPart fEditor;

  public XSDSearchActionGroup(IEditorPart editor)
  {
    Assert.isNotNull(editor);
    fEditor = editor;
    fReferencesGroup = new ReferencesSearchGroup(editor);
    fDeclarationsGroup = new DeclarationsSearchGroup();
    fImplementorsGroup = new ImplementorsSearchGroup();
    fOccurrencesGroup = new OccurrencesSearchGroup();
  }
}