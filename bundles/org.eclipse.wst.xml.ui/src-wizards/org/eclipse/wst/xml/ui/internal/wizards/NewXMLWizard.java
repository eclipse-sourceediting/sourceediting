/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.wizards;

import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.util.DOMContentBuilder;
import org.eclipse.wst.common.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;
import org.eclipse.wst.xml.ui.dialogs.NamespaceInfoErrorHelper;
import org.eclipse.wst.xml.ui.dialogs.SelectFileOrXMLCatalogIdPanel;
import org.eclipse.wst.xml.ui.dialogs.UpdateListener;
import org.eclipse.wst.xml.ui.nsedit.CommonEditNamespacesDialog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;

public class NewXMLWizard extends NewModelWizard
{                                  
  protected static final int CREATE_FROM_DTD     = 0;
  protected static final int CREATE_FROM_XSD     = 1;
  protected static final int CREATE_FROM_SCRATCH = 2;

  protected static final String[] createFromRadioButtonLabel
    = {XMLWizard.getString("_UI_RADIO_XML_FROM_DTD"),
       XMLWizard.getString("_UI_RADIO_XML_FROM_SCHEMA"),
       XMLWizard.getString("_UI_RADIO_XML_FROM_SCRATCH")};

  protected static final String[] filePageFilterExtensions = {".xml"};
  protected static final String[] browseXSDFilterExtensions = {".xsd"};
  protected static final String[] browseDTDFilterExtensions = {".dtd"};

  protected NewFilePage newFilePage;
  protected StartPage startPage;
  protected SelectGrammarFilePage selectGrammarFilePage; 
  protected SelectRootElementPage selectRootElementPage;
                 
  protected String cmDocumentErrorMessage;

  protected NewXMLGenerator generator;


  public NewXMLWizard()
  {
    setWindowTitle(XMLWizard.getString("_UI_WIZARD_CREATE_XML_HEADING"));
    setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(XMLWizard.class,"icons/generatexml_wiz.gif"));
    generator = new NewXMLGenerator();
  }
  

  public NewXMLWizard(IFile file, CMDocument cmDocument)
  {
    this();       

    generator.setGrammarURI(URIHelper.getPlatformURI(file));
    generator.setCMDocument(cmDocument);
  }


  public static void showDialog(Shell shell, IFile file, IStructuredSelection structuredSelection)
  {                        
    List errorList = new Vector();          
    String[] errorInfo = new String[2];
    CMDocument cmDocument = NewXMLGenerator.createCMDocument(file.getLocation().toOSString(), errorInfo);
    if (errorInfo[0] == null)
    {
      NewXMLWizard wizard = new NewXMLWizard(file, cmDocument);
      wizard.init(XMLEditorPlugin.getInstance().getWorkbench(), structuredSelection);
      wizard.setNeedsProgressMonitor(true);
      WizardDialog dialog = new WizardDialog(shell, wizard);
      dialog.create();          
      dialog.getShell().setText(XMLWizard.getString("_UI_DIALOG_NEW_TITLE"));     
      dialog.setBlockOnOpen(true);
      dialog.open();
    }         
    else
    {
      MessageDialog.openInformation(shell, errorInfo[0], errorInfo[1]);
    }
  }  


  public void addPages()
  {
    String grammarURI = generator.getGrammarURI();

    if (grammarURI == null)
    {
      // start page
      startPage = new StartPage("StartPage", createFromRadioButtonLabel)
      {
        public void createControl(Composite parent)
        {
          super.createControl(parent);
        }
                    
        public void setVisible(boolean visible)
        {
          super.setVisible(visible);
          getRadioButtonAtIndex(getCreateMode()).setSelection(true);
          getRadioButtonAtIndex(getCreateMode()).setFocus();

          // Set the help context for each button
          //WorkbenchHelp.setHelp(startPage.getRadioButtonAtIndex(0), XMLBuilderContextIds.XMLC_FROM_DTD_RADIO);
          //WorkbenchHelp.setHelp(startPage.getRadioButtonAtIndex(1), XMLBuilderContextIds.XMLC_XML_SCHEMA_RADIO);
          //WorkbenchHelp.setHelp(startPage.getRadioButtonAtIndex(2), XMLBuilderContextIds.XMLC_SCRATCH_RADIO);
        }
      };


      startPage.setTitle(XMLWizard.getString("_UI_WIZARD_CREATE_XML_HEADING"));
      startPage.setDescription(XMLWizard.getString("_UI_WIZARD_CREATE_XML_EXPL"));
      addPage(startPage);
    }
                                   
    // new file page                              
    newFilePage = new NewFilePage(selection);
    newFilePage.setTitle(XMLWizard.getString("_UI_WIZARD_CREATE_XML_FILE_HEADING"));
    newFilePage.setDescription(XMLWizard.getString("_UI_WIZARD_CREATE_XML_FILE_EXPL")); 
    newFilePage.defaultName = (grammarURI != null) ? URIHelper.removeFileExtension(URIHelper.getLastSegment(grammarURI)) : "NewFile";
    newFilePage.defaultFileExtension = ".xml";
    newFilePage.filterExtensions = filePageFilterExtensions;
    addPage(newFilePage);
     
    // selectGrammarFilePage
    selectGrammarFilePage = new SelectGrammarFilePage();   
    addPage(selectGrammarFilePage);                                                                     
            
    // select root element page
    selectRootElementPage = new SelectRootElementPage();
    selectRootElementPage.setTitle(XMLWizard.getString("_UI_WIZARD_SELECT_ROOT_HEADING"));
    selectRootElementPage.setDescription(XMLWizard.getString("_UI_WIZARD_SELECT_ROOT_EXPL"));
    addPage(selectRootElementPage);
  }
                  

  public IWizardPage getStartingPage()
  {
    WizardPage result = null;
    if (startPage != null)
    {
      result = startPage;
    }
    else
    {
      result = newFilePage;
    }
    return result;
  }


  public int getCreateMode()
  {
    String grammarURI = generator.getGrammarURI();

    int result = CREATE_FROM_SCRATCH;
    if (grammarURI != null)
    {              
      if (grammarURI.endsWith(".dtd"))
      {
        result = CREATE_FROM_DTD;
      }
      else if (grammarURI.endsWith(".xsd"))
      {
        result = CREATE_FROM_XSD;
      }
    }
    else if (startPage != null)
    {
      int selectedIndex = startPage.getSelectedRadioButtonIndex();
      if (selectedIndex != -1)
      {
        result = selectedIndex;
      }
    }
    return result;
  }
 

  public IWizardPage getNextPage(IWizardPage currentPage)
  {
    WizardPage nextPage = null;
    if (currentPage == startPage)
    {
      nextPage = newFilePage;
    }
    else if (currentPage == newFilePage)
    {                       
      if (getCreateMode() == CREATE_FROM_SCRATCH)
      {
        nextPage = null;
      }
      else if (generator.getGrammarURI() == null)
      {
        nextPage = selectGrammarFilePage;
      }
      else
      {
        nextPage = selectRootElementPage;
      }
    }
    else if (currentPage == selectGrammarFilePage)
    {
      nextPage = selectRootElementPage;
    }                           
    return nextPage;
  }


  public boolean canFinish()
  {
    boolean result = false;

    IWizardPage currentPage = getContainer().getCurrentPage();

    if ((startPage != null && startPage.getSelectedRadioButtonIndex() == CREATE_FROM_SCRATCH && currentPage == newFilePage) ||
        (currentPage == selectRootElementPage))
    {
      result = currentPage.isPageComplete();
    }
    return result;
  }      
  

  public boolean performFinish()
  {       
    boolean result = true;
    super.performFinish();
    try
    {                                                                                   

      String[] namespaceErrors = generator.getNamespaceInfoErrors();
      if (namespaceErrors !=  null) 
      {
        String title = namespaceErrors[0];
        String message = namespaceErrors[1];
        result = MessageDialog.openQuestion(getShell(), title, message);
      }

      if (result)
      {
        String fileName = newFilePage.getFileName();
        if ((new Path(fileName)).getFileExtension() == null)
        {
          newFilePage.setFileName(fileName.concat(".xml"));
        }

        IFile newFile = newFilePage.createNewFile();
        String xmlFileName = newFile.getLocation().toOSString();

        if (getContainer().getCurrentPage() == selectRootElementPage)
        {

          generator.createXMLDocument(newFile, xmlFileName);
        }           
        else
        {
          generator.createEmptyXMLDocument(newFile);
        }
        
		newFile.refreshLocal(1, null);
		
		IWorkbenchWindow workbenchWindow = XMLEditorPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		BasicNewResourceWizard.selectAndReveal(newFile, workbenchWindow); 
        openEditor(newFile);
      }
    }
    catch (Exception e)
    {
      //e.printStackTrace();
    }
    return result;
  }  
  
  public void openEditor(IFile file)
  {  
	// Open editor on new file.
  	String editorId = null;
  	IEditorDescriptor editor = XMLEditorPlugin.getInstance().getWorkbench().getEditorRegistry().getDefaultEditor(file.getLocation().toOSString());
  	if(editor != null){
  		editorId = editor.getId();
  	}
  	IWorkbenchWindow dw = XMLEditorPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
	try {
		if (dw != null) {
			IWorkbenchPage page = dw.getActivePage();
			if (page != null)
				page.openEditor(new FileEditorInput(file), editorId, true);
		}
	} catch (PartInitException e) {
	}
  }
  

  protected String getDefaultSystemId()
  {  
    String relativePath = "platform:/resource/" + newFilePage.getContainerFullPath().toString() + "/dummy"; 
    return URIHelper.getRelativeURI(generator.getGrammarURI(), relativePath);
  }                          
     

  /**
   * SelectGrammarFilePage
   */
  class SelectGrammarFilePage extends WizardPage
  {
    protected SelectFileOrXMLCatalogIdPanel panel;
  
    SelectGrammarFilePage()
    {
      super("SelectGrammarFilePage");
    }  

    public void createControl(Composite parent)
    {             
      Composite composite = new Composite(parent, SWT.NONE);
      //WorkbenchHelp.setHelp(composite, XMLBuilderContextIds.XMLC_GRAMMAR_PAGE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.FILL_BOTH));
      setControl(composite);

      panel = new SelectFileOrXMLCatalogIdPanel(composite);
      panel.setLayoutData(new GridData(GridData.FILL_BOTH));

      SelectFileOrXMLCatalogIdPanel.Listener listener = new SelectFileOrXMLCatalogIdPanel.Listener()
      {
        public void completionStateChanged()
        {          
          updateErrorMessage();
        }
      };
      panel.setListener(listener);
    }
   
    public void setVisible(boolean visible)
    {                              
      super.setVisible(visible);                              
      if (visible)
      {                      
        if (getCreateMode() == CREATE_FROM_DTD)
        {                               
          setTitle(XMLWizard.getString("_UI_WIZARD_SELECT_DTD_FILE_TITLE"));
          setDescription(XMLWizard.getString("_UI_WIZARD_SELECT_DTD_FILE_DESC"));
          panel.setFilterExtensions(browseDTDFilterExtensions);
        }
        else
        { 
          setTitle(XMLWizard.getString("_UI_WIZARD_SELECT_XSD_FILE_TITLE"));
          setDescription(XMLWizard.getString("_UI_WIZARD_SELECT_XSD_FILE_DESC"));
          panel.setFilterExtensions(browseXSDFilterExtensions);
        } 
        generator.setGrammarURI(null);
        generator.setCMDocument(null); 
        cmDocumentErrorMessage = null;        
      }                             
      panel.setVisibleHelper(visible);
    }  

    public String getURI()
    {                      
      String uri = panel.getXMLCatalogURI();
      if (uri == null)
      {
        IFile file = panel.getFile();
        if (file != null)
        {
          uri = URIHelper.getPlatformURI(file);
        }               
      }
      return uri;
    }  

    public boolean isPageComplete()
    {                            
      return getURI() != null && getErrorMessage() == null;
    }                        

    public String getXMLCatalogId()
    {
      return panel.getXMLCatalogId();
    }                            
    
    public XMLCatalogEntry getXMLCatalogEntry()
    {
      return panel.getXMLCatalogEntry();   
    }    

    public String computeErrorMessage()
    {           
      String errorMessage = null;
      String uri = getURI();               
      if (uri != null)
      {         
        if (!URIHelper.isReadableURI(uri,false))
        {
          errorMessage = XMLWizard.getString("_UI_LABEL_ERROR_CATALOG_ENTRY_INVALID");                  
        }
      }             
      return errorMessage;
    }


    public void updateErrorMessage()
    {
      String errorMessage = computeErrorMessage();  
      setErrorMessage(errorMessage);
      setPageComplete(isPageComplete());
    }
  }


  /**
   * SelectRootElementPage
   */
  class SelectRootElementPage extends WizardPage implements SelectionListener
  {
    protected Combo combo;
    protected Button[] radioButton;
    protected PageBook pageBook;
    protected XSDOptionsPanel xsdOptionsPanel;
    protected DTDOptionsPanel dtdOptionsPanel;


    SelectRootElementPage()
    {
      super("SelectRootElementPage");
    }

    public void createControl(Composite parent)
    {
      // container group
      Composite containerGroup = new Composite(parent,SWT.NONE);
      //WorkbenchHelp.setHelp(containerGroup, XMLBuilderContextIds.XMLC_ROOT_PAGE);
      containerGroup.setLayout(new GridLayout());
      containerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setControl(containerGroup);

      // select root element
      Label containerLabel = new Label(containerGroup, SWT.NONE);
      containerLabel.setText(XMLWizard.getString("_UI_LABEL_ROOT_ELEMENT"));
      combo = new Combo(containerGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
      combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      combo.addSelectionListener(this);
      //WorkbenchHelp.setHelp(combo, XMLBuilderContextIds.XMLC_ROOT_ROOT);

      // Options
      {
        Group group = new Group(containerGroup, SWT.NONE);
        group.setText(XMLWizard.getString("_UI_WIZARD_CONTENT_OPTIONS"));
        //WorkbenchHelp.setHelp(group, XMLBuilderContextIds.XMLC_CURRENT_GROUP);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = 0;
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        radioButton = new Button[4];
                
        radioButton[0] = new Button(group, SWT.CHECK);
        radioButton[0].setText(XMLWizard.getString("_UI_WIZARD_CREATE_OPTIONAL_ATTRIBUTES"));
        radioButton[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButton[0].setSelection(false);
        //WorkbenchHelp.setHelp(radioButton[0], XMLBuilderContextIds.XMLC_CREATE_OPTIONAL_ATTRIBUTES);
        
        radioButton[1] = new Button(group, SWT.CHECK);
        radioButton[1].setText(XMLWizard.getString("_UI_WIZARD_CREATE_OPTIONAL_ELEMENTS"));
        radioButton[1].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButton[1].setSelection(false);
        //WorkbenchHelp.setHelp(radioButton[1], XMLBuilderContextIds.XMLC_CREATE_OPTIONAL_ELEMENTS);
        
        radioButton[2] = new Button(group, SWT.CHECK);
        radioButton[2].setText(XMLWizard.getString("_UI_WIZARD_CREATE_FIRST_CHOICE"));
        radioButton[2].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButton[2].setSelection(true);
        //WorkbenchHelp.setHelp(radioButton[2], XMLBuilderContextIds.XMLC_CREATE_FIRST_CHOICE);
        
        radioButton[3] = new Button(group, SWT.CHECK);
        radioButton[3].setText(XMLWizard.getString("_UI_WIZARD_FILL_ELEMENTS_AND_ATTRIBUTES"));
        radioButton[3].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButton[3].setSelection(true);                        
        //WorkbenchHelp.setHelp(radioButton[3], XMLBuilderContextIds.XMLC_FILL_ELEMENTS_AND_ATTRIBUTES);
/*
        radioButton = new Button[2];

        radioButton[0] = new Button(group, SWT.RADIO);
        radioButton[0].setText(XMLWizard.getString("_UI_WIZARD_CREATE_REQUIRED"));
        radioButton[0].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButton[0].setSelection(true);
        WorkbenchHelp.setHelp(radioButton[0], XMLBuilderContextIds.XMLC_CREATE_REQUIRED_ONLY);

        radioButton[1] = new Button(group, SWT.RADIO);
        radioButton[1].setText(XMLWizard.getString("_UI_WIZARD_CREATE_OPTIONAL"));
        radioButton[1].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        WorkbenchHelp.setHelp(radioButton[1], XMLBuilderContextIds.XMLC_CREATE_REQUIRED_AND_OPTION);
*/
      }

      // add the grammar specific generation options
      //
      {
        pageBook = new PageBook(containerGroup, SWT.NONE);
        pageBook.setLayoutData(new GridData(GridData.FILL_BOTH));
        xsdOptionsPanel = new XSDOptionsPanel(this, pageBook);
        dtdOptionsPanel = new DTDOptionsPanel(this, pageBook);
        pageBook.showPage(xsdOptionsPanel); 
      }
    }

    public void widgetSelected(SelectionEvent event)
    {
      int index = combo.getSelectionIndex();
      String rootElementName = (index != -1) ? combo.getItem(index) : null;
      generator.setRootElementName(rootElementName);
    }

    public void widgetDefaultSelected(SelectionEvent event)
    {
    }

    public void setVisible(boolean visible)
    {
      super.setVisible(visible);

      if (visible)
      {
        try
        {
          if (generator.getGrammarURI() == null)
          {
            generator.setGrammarURI(selectGrammarFilePage.getURI());
            generator.setXMLCatalogEntry(selectGrammarFilePage.getXMLCatalogEntry());
          }
          Assert.isNotNull(generator.getGrammarURI());    
                
          if (generator.getCMDocument() == null)
          {
            final String[] errorInfo = new String[2];
            final CMDocument[] cmdocs = new CMDocument[1];
            Runnable r = new Runnable()
              {
                public void run()
                {
                  cmdocs[0] = NewXMLGenerator.createCMDocument(generator.getGrammarURI(), errorInfo);
                }
              };
            org.eclipse.swt.custom.BusyIndicator.showWhile(Display.getCurrent(),r);

            generator.setCMDocument(cmdocs[0]);
            cmDocumentErrorMessage = errorInfo[1];     
          }    

          if (generator.getCMDocument() != null && cmDocumentErrorMessage == null)
          {
            CMNamedNodeMap nameNodeMap = generator.getCMDocument().getElements();
            Vector nameNodeVector = new Vector();

            for (int i = 0; i < nameNodeMap.getLength(); i++)
            {
              CMElementDeclaration cmElementDeclaration = (CMElementDeclaration)nameNodeMap.item(i);
              Object value =  cmElementDeclaration.getProperty("Abstract");
              if  (value !=  Boolean.TRUE)
              {
                nameNodeVector.add(cmElementDeclaration.getElementName());
              }
            }

            Object[] nameNodeArray = nameNodeVector.toArray();
            if (nameNodeArray.length > 0 )
            {
              Arrays.sort(nameNodeArray, Collator.getInstance());
            }

            String defaultRootName = (String) (generator.getCMDocument()).getProperty("http://org.eclipse.wst/cm/properties/defaultRootName");
            int defaultRootIndex = -1;
            combo.removeAll();


            for (int i = 0; i < nameNodeArray.length; i++)
            {
              String elementName = (String)nameNodeArray[i];

              combo.add(elementName);
              if (defaultRootName != null && defaultRootName.equals(elementName))
              {
                defaultRootIndex = i;
              }
            }
         
            if (nameNodeArray.length > 0)
            {
              defaultRootIndex = defaultRootIndex != -1 ? defaultRootIndex : 0;
              combo.select(defaultRootIndex);
              generator.setRootElementName(combo.getItem(defaultRootIndex));
            }
          }

          if (generator.getGrammarURI().endsWith("xsd"))
          {                                       
            pageBook.showPage(xsdOptionsPanel); 
            generator.setDefaultSystemId(getDefaultSystemId());
            generator.createNamespaceInfoList();

            // Provide default namespace prefix if none
            for (int i=0; i<generator.namespaceInfoList.size(); i++)
            {
              NamespaceInfo nsinfo = 
                (NamespaceInfo)generator.namespaceInfoList.get(i);
              if ((nsinfo.prefix == null || nsinfo.prefix.trim().length() == 0)
                  && (nsinfo.uri != null && nsinfo.uri.trim().length() != 0))
              {
                nsinfo.prefix = getDefaultPrefix(generator.namespaceInfoList);
              }
            }
            xsdOptionsPanel.setNamespaceInfoList(generator.namespaceInfoList);
          }
          else if (generator.getGrammarURI().endsWith("dtd"))
          {
            pageBook.showPage(dtdOptionsPanel);
            dtdOptionsPanel.update();
          }
        }   
        catch(Exception e)
        {
           //XMLBuilderPlugin.getPlugin().getMsgLogger().writeCurrentThread();
        }

        /*
        String errorMessage = computeErrorMessage();
        if (errorMessage == null) 
          super.setVisible(visible);
        */
        
        updateErrorMessage();
      }
    }
                            
    private String getDefaultPrefix(List nsInfoList)
    {
      String defaultPrefix = "p";
      if (nsInfoList == null)
        return defaultPrefix;

      Vector v = new Vector();
      for (int i=0; i<nsInfoList.size(); i++)
      {
        NamespaceInfo nsinfo = (NamespaceInfo)nsInfoList.get(i);
        if (nsinfo.prefix != null)
          v.addElement(nsinfo.prefix);
      }

      if (v.contains(defaultPrefix))
      {
        String s = defaultPrefix;
        for (int j=0; v.contains(s); j++)
        {
          s = defaultPrefix + Integer.toString(j);
        }
        return s;
      }
      else
        return defaultPrefix;
    }

    public boolean isPageComplete()
    {
      boolean complete = (generator.getRootElementName() != null && generator.getRootElementName().length() > 0) && getErrorMessage() == null;
      
      if (complete) {
      	/*
        int buildPolicy = radioButton[0].getSelection() ?
                          DOMContentBuilder.BUILD_ONLY_REQUIRED_CONTENT :
                          DOMContentBuilder.BUILD_ALL_CONTENT;
        */
        int buildPolicy = 0;
        if (radioButton[0].getSelection())
          buildPolicy = buildPolicy | DOMContentBuilder.BUILD_OPTIONAL_ATTRIBUTES;
        if (radioButton[1].getSelection())
          buildPolicy = buildPolicy | DOMContentBuilder.BUILD_OPTIONAL_ELEMENTS;
        if (radioButton[2].getSelection())
          buildPolicy = buildPolicy | DOMContentBuilder.BUILD_FIRST_CHOICE | DOMContentBuilder.BUILD_FIRST_SUBSTITUTION;
        if (radioButton[3].getSelection())
          buildPolicy = buildPolicy | DOMContentBuilder.BUILD_TEXT_NODES;
                    
        generator.setBuildPolicy(buildPolicy);
      }

      return complete;
    }

    public String computeErrorMessage()
    {
      String errorMessage = null;
               
      if (cmDocumentErrorMessage != null)
      {
        errorMessage = cmDocumentErrorMessage;                  
      }
      else if (generator.getRootElementName() == null || generator.getRootElementName().length() == 0)
      {
        errorMessage = XMLWizard.getString("_ERROR_ROOT_ELEMENT_MUST_BE_SPECIFIED");
      }                  

      return errorMessage;
    }


    public void updateErrorMessage()
    {
      String errorMessage = computeErrorMessage();
      if (errorMessage == null)
      { 
        if (xsdOptionsPanel.isVisible())
        {
          
          errorMessage = xsdOptionsPanel.computeErrorMessage();
        }
        else if (dtdOptionsPanel.isVisible())
        {
          errorMessage = dtdOptionsPanel.computeErrorMessage();
        }
      }
      setErrorMessage(errorMessage);
      setPageComplete(isPageComplete());
    }
  }
  ////////////////End SelectRootElementPage
  


  public static GridLayout createOptionsPanelLayout()
  {
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginWidth = 0;
    gridLayout.horizontalSpacing = 0;
    return gridLayout;
  }
    

                    
  /**
   * 
   */
  class XSDOptionsPanel extends Composite
  {            
    protected String errorMessage = null;
    protected SelectRootElementPage parentPage;   
    protected CommonEditNamespacesDialog editNamespaces;

    public XSDOptionsPanel(SelectRootElementPage parentPage, Composite parent)
    {
      super(parent, SWT.NONE);
      this.parentPage = parentPage;
                                                                            
      setLayout(createOptionsPanelLayout());                                 
      setLayoutData(new GridData(GridData.FILL_BOTH));             
                                
      Composite co = new Composite(this, SWT.NONE);
      co.setLayout(new GridLayout());
	 
      if (newFilePage != null && newFilePage.getContainerFullPath() != null)
      {
        // todo... this is a nasty mess. I need to revist this code.
        //
        String resourceURI = "platform:/resource" + newFilePage.getContainerFullPath().toString() + "/dummy";
        String resolvedPath = URIHelper.normalize(resourceURI, null, null);
        if (resolvedPath.startsWith("file:/"))
        {
          resolvedPath = resolvedPath.substring(6);
        }
        // end nasty messs
        String tableTitle = XMLWizard.getString("_UI_LABEL_NAMESPACE_INFORMATION");
        editNamespaces = new CommonEditNamespacesDialog(co, new Path(resolvedPath), tableTitle, true, true);
      }

      UpdateListener updateListener = new UpdateListener()
      {
        public void updateOccured(Object object, Object arg)
        { 
          updateErrorMessage((List)arg);
        }
      };
    }
    
    public void setNamespaceInfoList(List list)
    {                   
      editNamespaces.setNamespaceInfoList(list);   
      editNamespaces.updateErrorMessage(list);                      
    }    
    
    public void updateErrorMessage(List namespaceInfoList)
    {        
      NamespaceInfoErrorHelper helper = new NamespaceInfoErrorHelper();
      errorMessage = helper.computeErrorMessage(namespaceInfoList, null);
      parentPage.updateErrorMessage();
    }
  
    
    public String computeErrorMessage()
    {             
      return errorMessage;
    }   
  }    


  /**
   * 
   */
  public class DTDOptionsPanel extends Composite implements ModifyListener
  {
    protected Group group;
    protected Text systemIdField;
    protected Text publicIdField;
    protected SelectRootElementPage parentPage;

    public DTDOptionsPanel(SelectRootElementPage parentPage, Composite parent)
    {
      super(parent, SWT.NONE);
      this.parentPage = parentPage;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      setLayout(createOptionsPanelLayout());
      Group group = new Group(this, SWT.NONE);
      group.setText(XMLWizard.getString("_UI_LABEL_DOCTYPE_INFORMATION"));
      //WorkbenchHelp.setHelp(group, XMLBuilderContextIds.XMLC_DOCUMENTATION_GROUP);
      
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      group.setLayout(layout);
      group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                  
      Label publicIdLabel = new Label(group, SWT.NONE);
      publicIdLabel.setText(XMLWizard.getString("_UI_LABEL_PUBLIC_ID"));
      publicIdField = new Text(group, SWT.SINGLE | SWT.BORDER);
      publicIdField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      publicIdField.addModifyListener(this);
      //WorkbenchHelp.setHelp(publicIdField, XMLBuilderContextIds.XMLC_PUBLIC);
      
      Label systemIdLabel = new Label(group, SWT.NONE);
      systemIdLabel.setText(XMLWizard.getString("_UI_LABEL_SYSTEM_ID"));
      systemIdField = new Text(group, SWT.SINGLE | SWT.BORDER);
      systemIdField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      systemIdField.addModifyListener(this);
      //WorkbenchHelp.setHelp(systemIdField, XMLBuilderContextIds.XMLC_SYSTEM);      
    }
    
    public void update()
    {                           
      String thePublicId = null;                              
      String theSystemId = null;
      XMLCatalogEntry xmlCatalogEntry = generator.getXMLCatalogEntry();

      if (xmlCatalogEntry != null)
      {
        if (xmlCatalogEntry.getType() == XMLCatalogEntry.PUBLIC)
        {
          thePublicId = xmlCatalogEntry.getKey();
          theSystemId = xmlCatalogEntry.getWebAddress();
          if (theSystemId == null)
          {
            theSystemId = generator.getGrammarURI().startsWith("http:") ? generator.getGrammarURI() : URIHelper.getLastSegment(generator.getGrammarURI());
          }  
        }                                          
        else
        {
          theSystemId = xmlCatalogEntry.getKey();
        }  
      }                                     
      else
      {
        theSystemId = getDefaultSystemId();
      }                                    
                                                                    
      publicIdField.setText(thePublicId != null ? thePublicId : "");
      systemIdField.setText(theSystemId != null ? theSystemId : "");
    }      

    public void modifyText(ModifyEvent e)
    {
      generator.setSystemId(systemIdField.getText());
      generator.setPublicId(publicIdField.getText());
      parentPage.updateErrorMessage();
    }

    public String computeErrorMessage()
    {
      return null;
    }
  }


}
