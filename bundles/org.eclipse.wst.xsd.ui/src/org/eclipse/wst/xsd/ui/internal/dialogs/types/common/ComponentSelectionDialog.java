/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;

public class ComponentSelectionDialog extends Dialog {
    private Display display = Display.getCurrent();
    private String dialogTitle;

    protected IComponentSelectionProvider provider;
    private List componentTreeViewerInput;
    private List masterComponentList;
    
    // widgets
    protected Composite topComposite;
    protected Composite bottomComposite; 
    private String filterTextLabel = "";
    private String componentListLabel = XSDEditorPlugin.getXSDString("_UI_LABEL_COMPONENTS");
    private Text textFilter;
    protected TreeViewer componentTreeViewer;
    private org.eclipse.swt.widgets.List qualifierList;
    
    protected Object componentSelection;
    protected Object qualifierTextSelection;

    public ComponentSelectionDialog(Shell shell, String dialogTitle, IComponentSelectionProvider provider) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.dialogTitle = dialogTitle;
        this.provider = provider;
        
        componentTreeViewerInput = new ArrayList();
        masterComponentList = new ArrayList();
    }

    /*
     * This method should be called before createDialogArea(Composite)
     */
    public void setComponentTreeLabel(String string) {
        componentListLabel = string;
    }

    /*
     * This method should be called before createDialogArea(Composite)
     */
    public void setFilterLabel(String string) {
        filterTextLabel = string;
    }
    
    public void create() {
        super.create();
        setTextFilterFocus();
    }
    
    protected void setTextFilterFocus() {
        textFilter.setFocus();        
    }

    public Control createDialogArea(Composite parent) {
        getShell().setText(dialogTitle);

        Composite mainComposite = (Composite) super.createDialogArea(parent);
        GridData gData = (GridData) mainComposite.getLayoutData();
        gData.heightHint = 500;
        gData.widthHint = 400;

        // Subclasses may use this Composite to add desired widgets
        //topComposite = new Composite(mainComposite, SWT.NONE);
        //topComposite.setLayoutData(new GridData());
        //topComposite.setLayout(new GridLayout());
        
        // do we need to introduce a method here to contain this
        // so we can add different parent other than 'topComposite'
        Composite filterLabelAndTree = new Composite(mainComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        filterLabelAndTree.setLayoutData(new GridData(GridData.FILL_BOTH));
        filterLabelAndTree.setLayout(layout);

        // Create Text textFilter
        Label filterLabel = new Label(filterLabelAndTree, SWT.NONE);
        filterLabel.setText(filterTextLabel);// + "(? = any character, * = any string):"); // TODO: Externalize String

        textFilter = new Text(filterLabelAndTree, SWT.SINGLE | SWT.BORDER);
        textFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        textFilter.addModifyListener(new TextFilterModifyAdapter());
        GridData textFilterData = new GridData();
        textFilterData.horizontalAlignment = GridData.FILL;
        textFilterData.grabExcessHorizontalSpace = true;
        textFilter.setLayoutData(textFilterData);

        //textFilter.setSelection(0);
        //textFilter.setf
        
        
        // Create Component TreeViewer
        createComponentTreeViewer(filterLabelAndTree);

        // Create Qualifier List widget
        Label qualifierLabel = new Label(mainComposite, SWT.NONE);
        qualifierLabel.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_QUALIFIER"));

        qualifierList = new org.eclipse.swt.widgets.List(mainComposite, SWT.BORDER | SWT.SINGLE);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.heightHint = 45;
        qualifierList.setLayoutData(data);
                
        bottomComposite = new Composite(mainComposite, SWT.NONE);
        bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bottomComposite.setLayout(new GridLayout());

        // Populate the Component TreeViewer via the provider
        // TODO: Is this the right way to set/get the ContentProvider?
        componentTreeViewer.setContentProvider(new ComponentTreeContentProvider());
        componentTreeViewer.setLabelProvider(provider.getLabelProvider());
        componentTreeViewer.setSorter(new ViewerSorter());
        componentTreeViewer.setInput(componentTreeViewerInput);
        
        populateMasterComponentList();
        refreshTreeViewer("");

        return mainComposite;
    }

    protected TreeViewer createTreeViewer(Composite comp, String title) {
        Label label = new Label(comp, SWT.NONE);
        label.setText(title);

        TreeViewer treeViewer = new TreeViewer(new Tree(comp, SWT.SINGLE | SWT.BORDER));
        Control treeWidget = treeViewer.getTree();
        GridData gd = new GridData(GridData.FILL_BOTH);
        treeWidget.setLayoutData(gd);

        return treeViewer;
    }
    
    /*
     * Creates the Component TreeViewer.
     */
    private void createComponentTreeViewer(Composite base) {
        componentTreeViewer = createTreeViewer(base, componentListLabel);    
        
        componentTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection structuredSelection = (IStructuredSelection) event.getSelection();
                List qualifiers = provider.getQualifiers(structuredSelection.getFirstElement());
                updateQualifierList(qualifiers);
                updateCanFinish();
            }
        });
    }
    
    private void updateQualifierList(List qualifiers) {
        qualifierList.removeAll();
        Iterator it = qualifiers.iterator();
        while (it.hasNext()) {
            qualifierList.add(it.next().toString());
        }
    }
    
    
    /*
     * Returns the processed filter text for the Text field.  Inserts a "."
     * before each supported meta-character.
     */
    protected String getProcessedFilterString() {
        return processFilterString(textFilter.getText());
    }

    /*
     * If supported metacharacters are used in the filter string, we need to
     * insert a "." before each metacharacter.
     */
    private String processFilterString(String inputString) {
        if (!(inputString.equals(""))) {
            inputString = insertString("*", ".", inputString);
            inputString = insertString("?", ".", inputString);
            inputString = inputString + ".*";
        } else {
            inputString = ".*";
        }

        return inputString.toLowerCase();
    }
    
    /*
     * Helper method to insert a "." before each metacharacter in the
     * search/filter string.
     */
    private String insertString(String target, String newString, String string) {
        StringBuffer stringBuffer = new StringBuffer(string);

        int index = stringBuffer.indexOf(target);
        while (index != -1) {
            stringBuffer = stringBuffer.insert(index, newString);
            index = stringBuffer.indexOf(target, index + newString.length() + target.length());
        }

        return stringBuffer.toString();
    }

    /*
     * Listens to changes made in the text filter widget
     */
    private class TextFilterModifyAdapter implements ModifyListener {
        public void modifyText(ModifyEvent e) {
            if (e.widget == textFilter) {
                if (delayedEvent != null) {
                    delayedEvent.CANCEL = true;
                }

                delayedEvent = new DelayedEvent();
                Display.getCurrent().timerExec(400, delayedEvent);
            }
        }
    }

    //TODO... do we really need one instance?
    private DelayedEvent delayedEvent;

    /*
     * Update the component TreeViewer when the text filter is modified.
     * Use a DelayedEvent so we don't update on every keystroke.
     */
    private class DelayedEvent implements Runnable {
        public boolean CANCEL = false;

        public void run() {
            if (!CANCEL) {
                refreshTreeViewer(getProcessedFilterString());
                
                // Select first match
                if (componentTreeViewer.getTree().getItemCount() > 0) {
                    TreeItem item = componentTreeViewer.getTree().getItems()[0];
                    TreeItem items[] = new TreeItem[1];
                    items[0] = item;
                    componentTreeViewer.getTree().setSelection(items);
                }
                
                // Update qualifierList
                IStructuredSelection structuredSelection = (IStructuredSelection) componentTreeViewer.getSelection();
                List qualifiers = provider.getQualifiers(structuredSelection.getFirstElement());
                updateQualifierList(qualifiers);
                
                updateCanFinish();
            }
        }
    }

    class ComponentList implements IComponentList {
        private Vector objectVector = new Vector();
        private long currentChangeCounter = 0;
        
        public void addComponent(Object o) {
            objectVector.add(o);
            currentChangeCounter++;
            doViewerUpdate();
        }
        
        private void doViewerUpdate() {
            // TODO: Investigate if we should also add a timer condition??
            if (currentChangeCounter == 10) {
                currentChangeCounter = 0;
                fireUpdateList(this);
            }
        }

        public int size() {
            return objectVector.size();
        }
        
        public List subList(int startIndex, int endIndex) {
            return objectVector.subList(startIndex, endIndex);
        }
        
        public Iterator iterator() {
            return objectVector.iterator();
        }
    }

    
    // this method gets called from a non-ui thread so needs to call
    // asyncExec to ensure the UI updates happen on the UI thread
    //
    protected void fireUpdateList(final ComponentList list) {    
        Runnable runnable = new Runnable() {
            public void run(){
                // add new objects
                int growingListSize = list.size();
                int currentSize = masterComponentList.size();
                if (growingListSize > currentSize) {
                    masterComponentList.addAll(list.subList(currentSize, growingListSize));
                }

                refreshTreeViewer(getProcessedFilterString());
            }
        };
        display.asyncExec(runnable);        
    }

    
    /*
     * Populate the Component TreeViewer with items.
     */
    protected void populateMasterComponentList() {
        masterComponentList.clear();
        
        final ComponentList componentList = new ComponentList();
        provider.getComponents(componentList, true);
        
        Job job = new Job("read components") {
          protected IStatus run(IProgressMonitor monitor) {
            try {
              // this stuff gets executed on a non-UI thread
              //
              provider.getComponents(componentList, false);
              
              // Do a final update of our Input for the component tree viewer.
              fireUpdateList(componentList);
            }
            catch (Exception e) {
            }
            return Status.OK_STATUS;
          }          
        };
        job.schedule();
    }
    
    protected void refreshTreeViewer(String filterText) {
        componentTreeViewerInput.clear();
        ILabelProvider labelProvider = provider.getLabelProvider();
        Pattern regex = Pattern.compile(filterText);
        Iterator it = masterComponentList.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            String itemString = labelProvider.getText(item);           
            Matcher m = regex.matcher(itemString.toLowerCase());
            if (itemString.toLowerCase().startsWith(filterText) || m.matches()) {
                componentTreeViewerInput.add(item);
            }
        }
        
        componentTreeViewer.refresh();
    }
    
    /*
     * If there is a selection in the ComponentTreeViewer, enable OK
     */
    protected void updateCanFinish() {
        IStructuredSelection selection = (IStructuredSelection) componentTreeViewer.getSelection();
        if (selection.getFirstElement() != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }
    
    protected void okPressed() {
        IStructuredSelection selection = (IStructuredSelection) componentTreeViewer.getSelection();
        componentSelection = selection.getFirstElement();
        int qualifierIndex = qualifierList.getSelectionIndex();
        if (qualifierIndex < 0) {
            qualifierIndex = 0;
        }
        qualifierTextSelection = qualifierList.getItem(qualifierIndex);
        
        super.okPressed();
    }
    
    private class ComponentTreeContentProvider implements ITreeContentProvider {
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof List) {
                return ((List) parentElement).toArray();
            }
            return new Object[0];
        }
        
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }
        
        public Object getParent(Object element) {
            return null;
        }
        
        public boolean hasChildren(Object element) {
            if (getChildren(element).length > 0) {
                return true;
            }
            return false;
        }
        
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
        
        public void dispose() {
        }
    }
}
