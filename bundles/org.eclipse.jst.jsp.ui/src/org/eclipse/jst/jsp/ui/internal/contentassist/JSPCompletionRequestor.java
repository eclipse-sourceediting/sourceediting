package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.java.JSP2ServletNameUtil;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

/**
 * Most "accept" methods copied from JDT ResultCollector.
 * 
 * @author pavery
 */
public class JSPCompletionRequestor extends CompletionRequestor {

    // for debugging
    private static final boolean DEBUG;
    static {
        String value = Platform.getDebugOption("org.eclipse.jst.jsp.ui/debug/jspcontentassist"); //$NON-NLS-1$
        DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }

     private final static char[] METHOD_WITH_ARGUMENTS_TRIGGERS = new char[] {'(', '-', ' ' };
     private final static char[] METHOD_TRIGGERS = new char[] { ';', ',', '.','\t', '[', ' ' };
     private final static char[] TYPE_TRIGGERS = new char[] { '.', '\t', '[','(', ' ' };
     private final static char[] VAR_TRIGGER = new char[] { '\t', ' ', '=',';', '.' };

    // private IJavaProject fJavaProject;

    // set when imports can be added
    private ICompilationUnit fCompilationUnit;

    private int fCodeAssistOffset;

    // private int fContextOffset;

    protected ITextViewer fTextViewer;

    private List fFields = new ArrayList();

    private List fKeywords = new ArrayList();

    private List fLabels = new ArrayList();

    private List fMethods = new ArrayList();

    private List fModifiers = new ArrayList();

    private List fPackages = new ArrayList();

    private List fTypes = new ArrayList();

    private List fVariables = new ArrayList();

    private boolean fCursorInExpression = false;

    private int fJavaToJSPOffset;

    private String fJspName;

    private String fMangledName;

    public boolean isCursorInExpression() {
        return fCursorInExpression;
    }

    /**
     * @param doFilter
     *            The doFilter to set.
     */
    public void setCursorInExpression(boolean inExpression) {
        fCursorInExpression = inExpression;
    }

    /**
     * @return Returns the fJavaToJSPOffset.
     */
    public int getJavaToJSPOffset() {
        return fJavaToJSPOffset;
    }

    /**
     * @param javaToJSPOffset
     *            The fJavaToJSPOffset to set.
     */
    public void setJavaToJSPOffset(int javaToJSPOffset) {
        fJavaToJSPOffset = javaToJSPOffset;
    }

    /**
     * Copied logic from CompletionRequestorWrapper
     * 
     * @see org.eclipse.jdt.core.CompletionRequestor#accept(org.eclipse.jdt.core.CompletionProposal)
     */
    public void accept(CompletionProposal proposal) {

        switch (proposal.getKind()) {

        case CompletionProposal.KEYWORD:
            acceptKeyword(proposal.getName(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());
            break;
        case CompletionProposal.PACKAGE_REF:
            acceptPackage(proposal.getDeclarationSignature(), proposal.getCompletion(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.TYPE_REF:
            if ((proposal.getFlags() & Flags.AccEnum) != 0) {
                // does not exist for old requestor
            } else if ((proposal.getFlags() & Flags.AccInterface) != 0) {
                acceptInterface(proposal.getDeclarationSignature(), Signature.getSignatureSimpleName(proposal.getSignature()), proposal.getCompletion(), proposal.getFlags() & ~Flags.AccInterface,
                        proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            } else {
                acceptClass(proposal.getDeclarationSignature(), Signature.getSignatureSimpleName(proposal.getSignature()), proposal.getCompletion(), proposal.getFlags(), proposal.getReplaceStart(),
                        proposal.getReplaceEnd(), proposal.getRelevance());
            }
            break;
        case CompletionProposal.FIELD_REF:
            acceptField(Signature.getSignatureQualifier(proposal.getDeclarationSignature()), Signature.getSignatureSimpleName(proposal.getDeclarationSignature()), proposal.getName(), Signature
                    .getSignatureQualifier(proposal.getSignature()), Signature.getSignatureSimpleName(proposal.getSignature()), proposal.getCompletion(), proposal.getFlags(), proposal
                    .getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.METHOD_REF:
            acceptMethod(Signature.getSignatureQualifier(proposal.getDeclarationSignature()), Signature.getSignatureSimpleName(proposal.getDeclarationSignature()), proposal.getName(),
                    getParameterPackages(proposal.getSignature()), getParameterTypes(proposal.getSignature()), proposal.findParameterNames(null) == null ? CharOperation.NO_CHAR_CHAR : proposal
                            .findParameterNames(null), Signature.getSignatureQualifier(Signature.getReturnType(proposal.getSignature())), Signature.getSignatureSimpleName(Signature
                            .getReturnType(proposal.getSignature())), proposal.getCompletion(), proposal.getFlags(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.METHOD_DECLARATION:
            acceptMethodDeclaration(Signature.getSignatureQualifier(proposal.getDeclarationSignature()), Signature.getSignatureSimpleName(proposal.getDeclarationSignature()), proposal.getName(),
                    getParameterPackages(proposal.getSignature()), getParameterTypes(proposal.getSignature()), proposal.findParameterNames(null) == null ? CharOperation.NO_CHAR_CHAR : proposal
                            .findParameterNames(null), Signature.getSignatureQualifier(Signature.getReturnType(proposal.getSignature())), Signature.getSignatureSimpleName(Signature
                            .getReturnType(proposal.getSignature())), proposal.getCompletion(), proposal.getFlags(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.ANONYMOUS_CLASS_DECLARATION:
            acceptAnonymousType(Signature.getSignatureQualifier(proposal.getDeclarationSignature()), Signature.getSignatureSimpleName(proposal.getDeclarationSignature()),
                    getParameterPackages(proposal.getSignature()), getParameterTypes(proposal.getSignature()), proposal.findParameterNames(null) == null ? CharOperation.NO_CHAR_CHAR : proposal
                            .findParameterNames(null), proposal.getCompletion(), proposal.getFlags(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.LABEL_REF:
            acceptLabel(proposal.getCompletion(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());
            break;
        case CompletionProposal.LOCAL_VARIABLE_REF:
            acceptLocalVariable(proposal.getCompletion(), Signature.getSignatureQualifier(proposal.getSignature()), Signature.getSignatureSimpleName(proposal.getSignature()), proposal.getFlags(),
                    proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.VARIABLE_DECLARATION:
            acceptLocalVariable(proposal.getCompletion(), Signature.getSignatureQualifier(proposal.getSignature()), Signature.getSignatureSimpleName(proposal.getSignature()), proposal.getFlags(),
                    proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
            acceptPotentialMethodDeclaration(Signature.getSignatureQualifier(proposal.getDeclarationSignature()), Signature.getSignatureSimpleName(proposal.getDeclarationSignature()), proposal
                    .getName(), proposal.getReplaceStart(), proposal.getReplaceEnd(), proposal.getRelevance());

            break;
        }
    }

    private void acceptPotentialMethodDeclaration(char[] declaringTypePackageName, char[] declaringTypeName, char[] selector, int completionStart, int completionEnd, int relevance) {
        if (DEBUG)
            System.out.println("TODO: implement acceptPotentialMethodDeclaration");
        // if (fCompilationUnit == null) {
        // return;
        // }
        // String prefix= new String(selector);
        //    
        // try {
        // IJavaElement element=
        // getCompilationUnit().getElementAt(getCodeAssistOffset());
        // if (element != null) {
        // IType type= (IType) element.getAncestor(IJavaElement.TYPE);
        // if (type != null) {
        // GetterSetterCompletionProposal.evaluateProposals(type, prefix,
        // completionStart, completionEnd - completionStart, relevance + 100,
        // fSuggestedMethodNames, fMethods);
        // MethodCompletionProposal.evaluateProposals(type, prefix,
        // completionStart, completionEnd - completionStart, relevance + 99,
        // fSuggestedMethodNames, fMethods);
        // }
        // }
        // } catch (CoreException e) {
        //            
        // }
    }

    private void acceptLocalVariable(char[] name, char[] typePackageName, char[] typeName, int modifiers, int start, int end, int relevance) {

        String completionString = new String(name);
        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.LOCAL_VARIABLE_OBJ);

        StringBuffer buf = new StringBuffer();
        buf.append(name);
        if (typeName != null) {
            buf.append("    "); //$NON-NLS-1$
            buf.append(fixupTypeName(typeName));
        }
        String displayString = buf.toString();
        
        CustomCompletionProposal proposal = new CustomCompletionProposal(completionString, offset, length, completionString.length(), image, displayString, null, null, relevance);
       proposal.setTriggerCharacters(VAR_TRIGGER);
        fVariables.add(proposal);
    }

    private void acceptLabel(char[] completion, int replaceStart, int replaceEnd, int relevance) {
        if (DEBUG)
            System.out.println("implement acceptLabel");
    }

    private void acceptAnonymousType(char[] signatureQualifier, char[] signatureSimpleName, char[][] parameterPackages, char[][] parameterTypes, char[][] cs, char[] completion, int flags,
            int replaceStart, int replaceEnd, int relevance) {
        if (DEBUG)
            System.out.println("implement acceptAnonymousType");
    }

    private void acceptMethodDeclaration(char[] declaringTypePackageName, char[] declaringTypeName, char[] name, char[][] parameterPackageNames, char[][] parameterTypeNames, char[][] parameterNames,
            char[] returnTypePackageName, char[] returnTypeName, char[] completionName, int modifiers, int start, int end, int relevance) {

        if (!isCursorInExpression())
            return;
        
        StringBuffer displayString = getMethodDisplayString(null, name, parameterTypeNames, parameterNames, returnTypeName);
        displayString.append(" - "); //$NON-NLS-1$
        displayString.append("Override method in '" + new String(declaringTypeName) + "'");

        StringBuffer typeName = new StringBuffer();
        if (declaringTypePackageName.length > 0) {
            typeName.append(declaringTypePackageName);
            typeName.append('.');
        }
        typeName.append(declaringTypeName);

        String[] paramTypes = new String[parameterTypeNames.length];
        for (int i = 0; i < parameterTypeNames.length; i++) {
            paramTypes[i] = Signature.createTypeSignature(parameterTypeNames[i], true);
        }

        // JavaCompletionProposal proposal= new
        // OverrideCompletionProposal(fJavaProject, fCompilationUnit,
        // typeName.toString(), new String(name), paramTypes, start,
        // getLength(start, end), displayString.toString(), new
        // String(completionName));
        // proposal.setImage(getImage(getMemberDescriptor(modifiers)));
        // proposal.setProposalInfo(new ProposalInfo(fJavaProject,
        // declaringTypePackageName, declaringTypeName, name,
        // parameterPackageNames, parameterTypeNames, returnTypeName.length ==
        // 0));

        String completionString = new String(completionName);
        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = calculateMethodImage(modifiers);

        CustomCompletionProposal proposal = new CustomCompletionProposal(completionString, offset, length, completionString.length(), image, displayString.toString(), null, null, relevance + 100);
        // proposal.setRelevance(relevance + 100);
        fMethods.add(proposal);
        // fSuggestedMethodNames.add(new String(name));
    }

    private void acceptMethod(char[] declaringTypePackageName, char[] declaringTypeName, char[] name, char[][] parameterPackageNames, char[][] parameterTypeNames, char[][] parameterNames,
            char[] returnTypePackageName, char[] returnTypeName, char[] completionName, int modifiers, int start, int end, int relevance) {

        if (completionName == null)
            return;

        if (!isCursorInExpression())
            return;

        // if (TypeFilter.isFiltered(declaringTypePackageName,
        // declaringTypeName)) {
        // return;
        // }

        String completionString = new String(completionName);
        String displayString = getMethodDisplayString(declaringTypeName, name, parameterTypeNames, parameterNames, returnTypeName).toString();
        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = calculateMethodImage(modifiers);

        boolean hasOpeningBracket = completionName.length == 0 || (completionName.length > 0 && completionName[completionName.length - 1] == ')');
        ContextInformation contextInformation = null;
        if (hasOpeningBracket && parameterTypeNames.length > 0) {
           contextInformation = new ContextInformation(image, displayString, getParameterSignature(parameterTypeNames, parameterNames));
       
//           int position = (completionName.length == 0) ? fContextOffset : -1;
           //contextInformation.setContextInformationPosition(position);
       }
        boolean userMustCompleteParameters = (contextInformation != null && completionName.length > 0);
        char[] triggers = userMustCompleteParameters ? METHOD_WITH_ARGUMENTS_TRIGGERS : METHOD_TRIGGERS;
        int cursorPosition = completionString.length();
        if (userMustCompleteParameters) {
            // set the cursor before the closing bracket
            //proposal.setCursorPosition(completionName.length - 1);
            cursorPosition--;
        }
        
        CustomCompletionProposal proposal = new CustomCompletionProposal(completionString, offset, length, cursorPosition, image, displayString, contextInformation, null, relevance);
        proposal.setTriggerCharacters(triggers);
        

        
//         boolean isConstructor= returnTypeName == null ? true :
//         returnTypeName.length == 0;
//         proposal.setProposalInfo(new ProposalInfo(fJavaProject,
//         declaringTypePackageName, declaringTypeName, name,
//         parameterPackageNames, parameterTypeNames, isConstructor));

        fMethods.add(proposal);
    }

    private StringBuffer getMethodDisplayString(char[] declaringTypeName, char[] name, char[][] parameterTypeNames, char[][] parameterNames, char[] returnTypeName) {
        StringBuffer nameBuffer = new StringBuffer();
        nameBuffer.append(name);
        nameBuffer.append('(');
        if (parameterTypeNames != null && parameterTypeNames.length > 0) {
            nameBuffer.append(getParameterSignature(parameterTypeNames, parameterNames));
        }
        nameBuffer.append(')');
        if (returnTypeName != null && returnTypeName.length > 0) {
            nameBuffer.append("  "); //$NON-NLS-1$
            nameBuffer.append(returnTypeName);
        }
        // if declaring type is mangled, unmangle
        if (declaringTypeName != null && declaringTypeName.length > 0) {
            nameBuffer.append(" - "); //$NON-NLS-1$
            nameBuffer.append(fixupTypeName(declaringTypeName));
        }
        return nameBuffer;
    }

    protected String getParameterSignature(char[][] parameterTypeNames, char[][] parameterNames) {
        StringBuffer buf = new StringBuffer();
        if (parameterTypeNames != null) {
            for (int i = 0; i < parameterTypeNames.length; i++) {
                if (i > 0) {
                    buf.append(',');
                    buf.append(' ');
                }
                buf.append(parameterTypeNames[i]);
                if (parameterNames != null && parameterNames[i] != null) {
                    buf.append(' ');
                    buf.append(parameterNames[i]);
                }
            }
        }
        return buf.toString();
    }

    /**
     * @param flags
     * @return
     */
    private Image calculateMethodImage(int flags) {
        ImageDescriptor imageDescriptor = null;
        if ((flags & Flags.AccDefault) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.DEFAULT_CO);
        } else if ((flags & Flags.AccPrivate) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.PRIVATE_CO);
        } else if ((flags & Flags.AccProtected) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.PROTECTED_CO);
        } else if ((flags & Flags.AccPublic) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.PUBLIC_CO);
        } else {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.PUBLIC_CO);
        }

        Image image = decorateImage(imageDescriptor, flags).createImage();

        return image;
    }

    private ImageDescriptor decorateImage(ImageDescriptor base, int modifiers) {

        int adornments = 0;
        if (Flags.isDeprecated(modifiers))
            adornments = adornments | JavaElementImageDescriptor.DEPRECATED;
        if (Flags.isStatic(modifiers))
            adornments = adornments | JavaElementImageDescriptor.STATIC;

        JavaElementImageDescriptor javaDescriptor = new JavaElementImageDescriptor(base, adornments, new Point(16, 16));

        return javaDescriptor;
    }

    private void acceptField(char[] declaringTypePackageName, char[] declaringTypeName, char[] name, char[] typePackageName, char[] typeName, char[] completionName, int modifiers, int start, int end,
            int relevance) {

        // if (TypeFilter.isFiltered(declaringTypePackageName,
        // declaringTypeName)) {
        // return;
        // }

        String completionNameString = new String(completionName);
        int offset = start + getJavaToJSPOffset();
        int length = end - start;

        Image image = calculateFieldImage(modifiers);

        StringBuffer nameBuffer = new StringBuffer();
        nameBuffer.append(name);
        if (typeName.length > 0) {
            nameBuffer.append("   "); //$NON-NLS-1$
            nameBuffer.append(typeName);
        }
        // also fix up name if it's the servlet class
        // (since it's that ugly mangled string)
        if (declaringTypeName != null && declaringTypeName.length > 0) {
            nameBuffer.append(" - "); //$NON-NLS-1$
            nameBuffer.append(fixupTypeName(declaringTypeName));
        }

        // JavaCompletionProposal proposal= createCompletion(start, end, new
        // String(completionName), descriptor, nameBuffer.toString(),
        // relevance);
        CustomCompletionProposal proposal = new CustomCompletionProposal(completionNameString, offset, length, completionNameString.length(), image, nameBuffer.toString(), null, null, relevance);
        proposal.setTriggerCharacters(VAR_TRIGGER);
        fFields.add(proposal);
        // proposal.setProposalInfo(new ProposalInfo(fJavaProject,
        // declaringTypePackageName, declaringTypeName, name));
        
    }

    /**
     * @param flags
     * @return
     */
    private Image calculateFieldImage(int flags) {
        ImageDescriptor imageDescriptor = null;
        if ((flags & Flags.AccDefault) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.FIELD_DEFAULT_OBJ);
        } else if ((flags & Flags.AccPrivate) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.FIELD_PRIVATE_OBJ);
        } else if ((flags & Flags.AccProtected) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.FIELD_PROTECTED_OBJ);
        } else if ((flags & Flags.AccPublic) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.FIELD_PUBLIC_OBJ);
        } else {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.FIELD_DEFAULT_OBJ);
        }

        Image image = decorateImage(imageDescriptor, flags).createImage();

        return image;
    }

    protected void acceptClass(char[] packageName, char[] typeName, char[] completionName, int modifiers, int start, int end, int relevance) {

        // if (TypeFilter.isFiltered(packageName, typeName)) {
        // return;
        // }

        // ProposalInfo info= new ProposalInfo(fJavaProject, packageName,
        // typeName);

        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = calculateClassImage(modifiers);

        String completionNameString = new String(completionName);
        
        String containerNameString = new String(packageName);
        String typeNameString = fixupTypeName(typeName);
        if (typeNameString == getJspName())
            return;
        // containername can be null
        String fullName = concatenateName(containerNameString, typeNameString);

        StringBuffer buf = new StringBuffer(Signature.getSimpleName(fullName));
        String typeQualifier = Signature.getQualifier(fullName);
        if (typeQualifier.length() > 0) {
            buf.append(" - "); //$NON-NLS-1$
            buf.append(fixupTypeName(typeQualifier));
        }
        String name = buf.toString();

        // ICompilationUnit cu= null;
        // if (containerNameString != null && fCompilationUnit != null) {
        // if (completionNameString.equals(fullName)) {
        // cu= fCompilationUnit;
        // }
        // }

        CustomCompletionProposal proposal = new CustomCompletionProposal(completionNameString, offset, length, completionNameString.length(), image, name, null, null, relevance);
        proposal.setTriggerCharacters(TYPE_TRIGGERS);
        fTypes.add(proposal);

        // JavaCompletionProposal proposal= new
        // JavaTypeCompletionProposal(completion, cu, start, getLength(start,
        // end), getImage(descriptor), name, relevance, typeName,
        // containerName);
        // proposal.setProposalInfo(proposalInfo);     
        // fTypes.add(createTypeCompletion(start, end, new
        // String(completionName), descriptor, new String(typeName), new
        // String(packageName), info, relevance));
    }

    /**
     * @param modifiers
     */
    private Image calculateClassImage(int modifiers) {

        ImageDescriptor descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.IMG_OBJ_CLASS_OBJ);
        if ((modifiers & Flags.AccDefault) != 0) {
            descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERCLASS_DEFAULT_OBJ);
        } else if ((modifiers & Flags.AccPrivate) != 0) {
            descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERCLASS_PRIVATE_OBJ);
        } else if ((modifiers & Flags.AccProtected) != 0) {
            descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERCLASS_PROTECTED_OBJ);
        } else if ((modifiers & Flags.AccPublic) != 0) {
            descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.IMG_OBJ_CLASS_OBJ);
        } else {
            descriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERCLASS_DEFAULT_OBJ);
        }
        Image image = decorateImage(descriptor, modifiers).createImage();
        return image;
    }

    /**
     * Concatenates two names. Uses a dot for separation. Both strings can be
     * empty or <code>null</code>.
     */
    public String concatenateName(String name1, String name2) {
        StringBuffer buf = new StringBuffer();
        if (name1 != null && name1.length() > 0) {
            buf.append(name1);
        }
        if (name2 != null && name2.length() > 0) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(name2);
        }
        return buf.toString();
    }

    protected void acceptInterface(char[] packageName, char[] typeName, char[] completionName, int modifiers, int start, int end, int relevance) {
        // if (TypeFilter.isFiltered(packageName, typeName)) {
        // return;
        // }

        String completionNameString = new String(completionName);

        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = calculateInterfaceImage(modifiers);

        String containerNameString = new String(packageName);
        String typeNameString = fixupTypeName(new String(typeName));
        if (typeNameString == getJspName())
            return;

        // container name can be null
        String fullName = concatenateName(containerNameString, typeNameString);

        StringBuffer buf = new StringBuffer(Signature.getSimpleName(fullName));
        String typeQualifier = Signature.getQualifier(fullName);
        if (typeQualifier.length() > 0) {
            buf.append(" - "); //$NON-NLS-1$
            buf.append(fixupTypeName(typeQualifier));
        }
        String name = buf.toString();

        CustomCompletionProposal proposal = new CustomCompletionProposal(completionNameString, offset, length, completionNameString.length(), image, name, null, null, relevance);
        fTypes.add(proposal);
        // ProposalInfo info= new ProposalInfo(fJavaProject, packageName,
        // typeName);
        // fTypes.add(createTypeCompletion(start, end, new
        // String(completionName), descriptor, new String(typeName), new
        // String(packageName), info, relevance));
    }

    /**
     * @param modifiers
     * @return
     */
    private Image calculateInterfaceImage(int modifiers) {
        ImageDescriptor imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_PUBLIC_OBJ);
        if ((modifiers & Flags.AccDefault) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_DEFAULT_OBJ);
        } else if ((modifiers & Flags.AccPrivate) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_PRIVATE_OBJ);
        } else if ((modifiers & Flags.AccProtected) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_PROTECTED_OBJ);
        } else if ((modifiers & Flags.AccPublic) != 0) {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_PUBLIC_OBJ);
        } else {
            imageDescriptor = JSPEditorPluginImageHelper.getInstance().getImageDescriptor(JSPEditorPluginImages.INNERINTERFACE_DEFAULT_OBJ);
        }
        Image image = decorateImage(imageDescriptor, modifiers).createImage();
        return image;
    }

    private void acceptPackage(char[] packageName, char[] completionName, int start, int end, int relevance) {
//        if (TypeFilter.isFiltered(new String(packageName))) {
//            return;
//        }
        
        String packageNameString = new String(packageName);
        String completionNameString = new String(completionName);
        
        int offset = start + getJavaToJSPOffset();
        int length = end - start;
        Image image = JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.PACKAGE_OBJ);
        
        CustomCompletionProposal proposal = new CustomCompletionProposal(packageNameString, offset, length, packageNameString.length(), image, completionNameString, null, null, relevance);
       
        fPackages.add(proposal);
    }

    private void acceptKeyword(char[] name, int replaceStart, int replaceEnd, int relevance) {
        if(DEBUG)
            System.out.println("TODO: implement accept keyword");
    }

    private char[][] getParameterPackages(char[] methodSignature) {
        char[][] parameterQualifiedTypes = Signature.getParameterTypes(methodSignature);
        int length = parameterQualifiedTypes == null ? 0 : parameterQualifiedTypes.length;
        char[][] parameterPackages = new char[length][];
        for (int i = 0; i < length; i++) {
            parameterPackages[i] = Signature.getSignatureQualifier(parameterQualifiedTypes[i]);
        }

        return parameterPackages;
    }

    private char[][] getParameterTypes(char[] methodSignature) {
        char[][] parameterQualifiedTypes = Signature.getParameterTypes(methodSignature);
        int length = parameterQualifiedTypes == null ? 0 : parameterQualifiedTypes.length;
        char[][] parameterPackages = new char[length][];
        for (int i = 0; i < length; i++) {
            parameterPackages[i] = Signature.getSignatureSimpleName(parameterQualifiedTypes[i]);
        }

        return parameterPackages;
    }

    public ICompletionProposal[] getResults() {

        // TODO: eventually do better custom sorting here..
        List results = new ArrayList();
        results.addAll(fFields);
        results.addAll(fKeywords);
        results.addAll(fLabels);
        results.addAll(fMethods);
        results.addAll(fModifiers);
        results.addAll(fPackages);
        results.addAll(fTypes);
        results.addAll(fVariables);
        return (ICompletionProposal[]) results.toArray(new ICompletionProposal[results.size()]);
    }

    /**
     * @param compilationUnit
     *            The fCompilationUnit to set.
     */
    public void setCompilationUnit(ICompilationUnit compilationUnit) {
        fCompilationUnit = compilationUnit;

        // set some names for fixing up mangled name
        // in proposals later
        String cuName = getCompilationUnit().getPath().lastSegment();
        setMangledName(cuName.substring(0, cuName.lastIndexOf('.')));

        String unmangled = JSP2ServletNameUtil.unmangle(cuName);
        setJspName(unmangled.substring(unmangled.lastIndexOf('/') + 1));
    }

    /**
     * @return Returns the fCompilationUnit.
     */
    public ICompilationUnit getCompilationUnit() {
        return fCompilationUnit;
    }

    private String fixupTypeName(char[] typeNameChars) {
        String typeName = new String(typeNameChars);
        return fixupTypeName(typeName);
    }

    /**
     * Changes mangled type name to the name of the jsp file Or if it's an inner
     * type, returns the name of the inner type (minus the mangled parent type
     * name)
     * 
     * @param typeNameChars
     * @return
     */
    private String fixupTypeName(String typeName) {
        String mangledName = getMangledName();
        if (typeName.equals(mangledName))
            return getJspName();
        else if (typeName.startsWith(mangledName))
            return typeName.substring(typeName.lastIndexOf('.')+1);
        return typeName;
    }

    private String getMangledName() {
        return fMangledName;
    }

    private void setMangledName(String mangledName) {
        fMangledName = mangledName;
    }

    private String getJspName() {
        return fJspName;
    }

    private void setJspName(String jspName) {
        fJspName = jspName;
    }

    public int getCodeAssistOffset() {
        return fCodeAssistOffset;
    }

    public void setCodeAssistOffset(int offset) {
        fCodeAssistOffset = offset;
    }
}