<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
	05/01/03 SP - Updated Code so that users can type single or double quotes whille entering comments
	02/28/03 SP - Added an external file for ReasonCode , saving comments when they cancel and saving reasonCode
	02/25/2003 JDE - Updated to retrieve loan check digit (Issue #170).
	02/20/2003 JDE - Updated payeeName variable to retrieve PayeeName data member from the container.
	02/05/2003 JDE - Updated String ClosingAgentName = closingAgentInfo.getAgent(); //Previous method: closingAgentInfo.getContactName();
	02/05/2003 JDE - Updated setvalue JavaScript function per Shirley Smajd (Issue#65)
	02/03/2003 JDE - Updated CheckAmount and AccountSUSPNumber fields.
	01/31/2003 JDE - Updated for changed user requirements per Charles Lee request.
-->
<%
/****************************************************************************/
/*                                                                          */
/* Program name: rdtemplate.jsp                                             */
/*                                                                          */
/* Description: Template file of Rapid Deployment Wizard                    */
/*                                                                          */
/*                                                                          */
/*  Statement:  Licensed Materials - Property of IBM                        */
/*                                                                          */
/*              MQSeries SupportPac WA83                                    */
/*              (C) Copyright IBM Corp. 2000                                */
/*                                                                          */
/****************************************************************************/
/*                                                                          */
/* Function:                                                                */
/*   This file is a Template of RDW(Rapid Deployment Wizard).               */
/*                                                                          */
/*   RDW will generate input fields between                                 */
/*   '<!-- METADATA name="JspWizard" startspan -->' and                     */
/*   '<!-- METADATA name="JspWizard" endspan -->'.                          */
/*   Don't change these lines.                                              */
/*                                                                          */
/*   In this template, you must define accessor method for container        */
/*   member. Default accessor are defined, such as getStringMember or       */
/*   getDoubleMember. If you want to change these method, you must change   */
/*   RapidDeployment.properties too.                                        */
/*                                                                          */
/****************************************************************************/
/*                                                                          */
/* Change history:                                                          */
/*                                                                          */
/* V1.0   15-December-2000  JWH  Initial release                            */
/* V1.1   10-December-2001  JWH  Error page information is appended         */
/*                                                                          */
/*==========================================================================*/
/* Module Name: rdtemplate.jsp                                              */
/*==========================================================================*/
%>
<%@ page language="java" info="JspWizard" %>
<%@ page errorPage="../forms/ViewError.jsp" %>
<%@ page import="com.ibm.workflow.api.*" %>
<%@ page import="com.ibm.workflow.api.ItemPackage.*" %>
<%@ page import="com.ibm.workflow.servlet.client.*" %>
<%@ page import="com.principal.residential.postclosing.utils.*" %>
<%@ page import="com.principal.residential.postclosing.beans.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.io.*" %>
<%@ include file="./includes/Header.jsp"%>
<%@ include file="./includes/Comments.jsp"%>
<%@ include file="./includes/GetCheckDigit.jsp"%>
<%@ include file="./includes/ReasonCodeAndValue.jsp"%>

<jsp:useBean id="context" scope="request" type="com.ibm.workflow.servlet.client.RequestContext" />
<jsp:useBean id="now"  scope="request" class="java.util.Date" />

<% 
	ReadOnlyContainer inData=context.getContainer();
	WorkItem workItem=context.getWorkItem();
	request.setAttribute("context", context); 
	request.getSession(true).setAttribute("context", context);	
	
	String CheckInWorkItem = context.getCommand("checkInWorkItem", workItem.persistentOid());
	String CancelWorkItem = context.getCommand("cancelWorkItem", workItem.persistentOid());   

	String PayTo = getStringMember(context, inData, "PayTo");
	if(PayTo != null)
	{
		PayTo = PayTo.trim();
	}
	String PayeeName = getStringMember(context, inData, "PayeeName");
	if(PayeeName != null)
	{
		PayeeName = PayeeName.trim();
	}
	String PayeeAddress = getStringMember(context, inData, "PayeeAddress");
	if(PayeeAddress != null)
	{
		PayeeAddress = PayeeAddress.trim();
	}
	String bLastName = getStringMember(context, inData, "BorrowerLastName");
	if(bLastName != null)
	{
		bLastName = bLastName.trim();
	}
	String bFirstName = getStringMember(context, inData, "BorrowerFirstName");
	if(bFirstName != null)
	{
		bFirstName = bFirstName.trim();
	}
	String Borrower = bLastName + ", " + bFirstName;
	String CheckRouting = getStringMember(context, inData, "CheckRouting");	
	if(CheckRouting != null)
	{
		CheckRouting = CheckRouting.trim();
	}
	String CheckNeeded = getStringMember(context, inData, "CheckNeeded");
	if(CheckNeeded != null)
	{
		CheckNeeded = CheckNeeded.trim();
	}
	String CheckTiming = getStringMember(context, inData, "CheckTiming");
	if(CheckTiming != null)
	{
		CheckTiming = CheckTiming.trim();
	}
	String histOfCommFromIp = getStringMember(context, inData, "HistoryOfComments");
	histOfCommFromIp = adjustComments(histOfCommFromIp); 	
	String Comments = getStringMember(context, inData, "Comments");
	Comments = adjustComments(Comments);   	
	String LoanNumber = getStringMember(context, inData, "LoanNumber");
	if(LoanNumber != null)
	{
		LoanNumber = LoanNumber.trim();
	}
	String WFCheckNumber = getStringMember(context, inData, "WFCheckNumber");
	if(WFCheckNumber != null)
	{
		WFCheckNumber = WFCheckNumber.trim();
	}
	String blockCheckNumber = getStringMember(context, inData, "BlockCheckNumber");
	if(blockCheckNumber != null)
	{
		blockCheckNumber = blockCheckNumber.trim();
	}
	String BranchOfficeCode = getStringMember(context, inData, "BranchOfficeCode");	
	if(BranchOfficeCode != null)
	{
		BranchOfficeCode = BranchOfficeCode.trim();
	}
	String SatelliteOfficeCode = getStringMember(context, inData, "SatelliteOfficeCode");	
	if(SatelliteOfficeCode != null)
	{
		SatelliteOfficeCode = SatelliteOfficeCode.trim();
	}
	String RepresentativeCode = getStringMember(context, inData, "RepresentatveCode");		
	if(RepresentativeCode != null)
	{
		RepresentativeCode = RepresentativeCode.trim();
	}	
	String CheckRequestReason = getStringMember(context, inData, "CheckRequestReason");	
	if(CheckRequestReason != null)
	{
		CheckRequestReason = CheckRequestReason.trim();
	}
	String UserIDOrig = getStringMember(context, inData, "UserID_Orig");
	if(UserIDOrig != null)
	{
		UserIDOrig = UserIDOrig.trim();
	}	
	String UserID = context.getUserID();
	String AccountSUSPNumber = getStringMember(context, inData, "AccountSUSPNumber");
	if(AccountSUSPNumber != null)
	{
		AccountSUSPNumber = AccountSUSPNumber.trim();
	}
	String ActivityName = getStringMember(context, inData, "ActivityName");
	if(ActivityName != null)
	{
		ActivityName = ActivityName.trim();
	}
	String CheckAmount = getStringMember(context, inData, "CheckAmount");
	if(CheckAmount != null)
	{
		CheckAmount = CheckAmount.trim();
	}
	String reasonCode = getStringMember(context, inData, "ReasonCode");
	
	// LoanInfoBean
	RMPCLoanDataBean loanData = new RMPCLoanDataBean();
	RMPCLoanInfo loanInfo = new RMPCLoanInfo();
	loanInfo = loanData.getCurrentLoanInfo(getLongMember(context, inData, "LoanNumber"));
	String BorrowerAddress = loanInfo.getBorrowerAddress();	
	String BorrowerFirstName = loanInfo.getBorrowerFirstName();
	String BorrowerLastName = loanInfo.getBorrowerLastName();
	
	// Closing Agent Details	
	ClosingAgentInfo closingAgentInfo = loanInfo.getClosingAgentInfo();
	String ClosingAgentName = closingAgentInfo.getAgent(); //closingAgentInfo.getContactName();
	String ClosingAgentAddress = closingAgentInfo.getAddress();
	
// HistoryOfChecks Info
	String HCDt1 = getStringMember(context, inData, "HistoryOfChecks.Date1");
	if(HCDt1 != null)
	{
		HCDt1 = HCDt1.trim();
	}
	String HCAmt1 = getStringMember(context, inData, "HistoryOfChecks.Amount1");
	if(HCAmt1 != null)
	{
		HCAmt1 = HCAmt1.trim();
	}
	String HCPayee1 = getStringMember(context, inData, "HistoryOfChecks.PayeeName1");
	if(HCPayee1 != null)
	{
		HCPayee1 = HCPayee1.trim();
	}
	String HCDt2 = getStringMember(context, inData, "HistoryOfChecks.Date2");
	if(HCDt2 != null)
	{
		HCDt2 = HCDt2.trim();
	}
	String HCAmt2 = getStringMember(context, inData, "HistoryOfChecks.Amount2");
	if(HCAmt2 != null)
	{
		HCAmt2 = HCAmt2.trim();
	}
	String HCPayee2 = getStringMember(context, inData, "HistoryOfChecks.PayeeName2");
	if(HCPayee2 != null)
	{
		HCPayee2 = HCPayee2.trim();
	}
	String HCDt3 = getStringMember(context, inData, "HistoryOfChecks.Date3");
	if(HCDt3 != null)
	{
		HCDt3 = HCDt3.trim();
	}
	String HCAmt3 = getStringMember(context, inData, "HistoryOfChecks.Amount3");
	if(HCAmt3 != null)
	{
		HCAmt3 = HCAmt3.trim();
	}
	String HCPayee3 = getStringMember(context, inData, "HistoryOfChecks.PayeeName3");
	if(HCPayee3 != null)
	{
		HCPayee3 = HCPayee3.trim();
	}
	String HCDt4 = getStringMember(context, inData, "HistoryOfChecks.Date4");
	if(HCDt4 != null)
	{
		HCDt4 = HCDt4.trim();
	}
	String HCAmt4 = getStringMember(context, inData, "HistoryOfChecks.Amount4");
	if(HCAmt4 != null)
	{
		HCAmt4 = HCAmt4.trim();
	}
	String HCPayee4 = getStringMember(context, inData, "HistoryOfChecks.PayeeName4");
	if(HCPayee4 != null)
	{
		HCPayee4 = HCPayee4.trim();
	}
	String HCDt5 = getStringMember(context, inData, "HistoryOfChecks.Date5");
	if(HCDt5 != null)
	{
		HCDt5 = HCDt5.trim();
	}
	String HCAmt5 = getStringMember(context, inData, "HistoryOfChecks.Amount5");
	if(HCAmt5 != null)
	{
		HCAmt5 = HCAmt5.trim();
	}
	String HCPayee5 = getStringMember(context, inData, "HistoryOfChecks.PayeeName5");
	if(HCPayee5 != null)
	{
		HCPayee5 = HCPayee5.trim();
	}
	
	// 02/25/2003 JDE - Update to retrieve loan check digit (Issue #170)
	String CheckDigit = getCheckDigit(LoanNumber);	
%>

<%!
  public String getStringMember(RequestContext context, ReadOnlyContainer input, String name) {
    String val = null;    
    try {
      val = context.getMemberValue(input, name, "");
    }
    catch (Exception e) {
    }    
    return val;
  }

  public double getDoubleMember(RequestContext context, ReadOnlyContainer input, String name) {
    String val = null;
    double rtn = 0.0;    
    try {
      val = context.getMemberValue(input, name, "0.0");
      rtn = Double.parseDouble(val);
    }
    catch (Exception e) {
    }    
    return rtn;
  }

  public int getLongMember(RequestContext context, ReadOnlyContainer input, String name) {
    String val = null;
    int rtn = 0;    
    try {
      val = context.getMemberValue(input, name, "0");
      rtn = Integer.parseInt(val);
    }
    catch (Exception e) {
    }    
    return rtn;
  }    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title></title>
<script language="JavaScript" src="../programs/includes/includeJavaScript.js"></script>
<SCRIPT language="JavaScript" src="../programs/includes/validateJSP.js"></SCRIPT>
<script language="javascript">
<!--
var payTo = "<%=PayTo%>";
var borrower = "<%=Borrower%>";
var checkRouting = "<%=CheckRouting%>";
var checkNeeded = "<%=CheckNeeded%>";
var checkTiming = "<%=CheckTiming%>";
var closingAgentName = "<%=ClosingAgentName%>";
var closingAgentAddress = "<%=ClosingAgentAddress%>";
var borrowerLastName = trim("<%=BorrowerLastName%>");
var borrowerFirstName = trim("<%=BorrowerFirstName%>");
// 02/20/2003 JDE - Update payeeName variable to pull the PayeeName member from the data container.
// var payeeName = trim(borrowerFirstName) + " " + trim(borrowerLastName);
var payeeName = "<%=PayeeName%>";
var payeeAddress = "<%=PayeeAddress%>";
var borrowerAddress = "<%=BorrowerAddress%>";
// End 02/20/2003 Update
var accountSUSPNumber = "<%=AccountSUSPNumber%>";
var checkRequestReason = "<%=CheckRequestReason%>";
var spacer = ", ";
var br = "<BR>";
var hcDt1 = "<%=HCDt1%>";
var hcAmt1 = "<%=HCAmt1%>";
var hcPayee1 = "<%=HCPayee1%>";
var hcDt2 = "<%=HCDt2%>";
var hcAmt2 = "<%=HCAmt2%>";
var hcPayee2 = "<%=HCPayee2%>";
var hcDt3 = "<%=HCDt3%>";
var hcAmt3 = "<%=HCAmt3%>";
var hcPayee3 = "<%=HCPayee3%>";
var hcDt4 = "<%=HCDt4%>";
var hcAmt4 = "<%=HCAmt4%>";
var hcPayee4 = "<%=HCPayee4%>";
var hcDt5 = "<%=HCDt5%>";
var hcAmt5 = "<%=HCAmt5%>";
var hcPayee5 = "<%=HCPayee5%>";
var hcString = "";
var curDt = "<%=now%>";
var tab = "\t";
var fieldName = "";
var varName = "";
var needCheck = "";
var activity = "<%=ActivityName%>";
var historyOfComments = '<%=histOfCommFromIp%>';
var checkAmount = "<%=CheckAmount%>";
var displayCheckAmt = formatCurrency("<%=CheckAmount%>");

function pendForSelf(){	
	preserveValues();	
	document.forms[0].returnCode.value=1;	
	document.forms[0].submit();	
}		

function showRadio(text) {
	if (text == "One"){
		if (payTo == "Borrower"){document.write('<INPUT name="PayTo" type="radio" value="Borrower" onClick="displayBorrowerInfo()" checked>');
		}else{document.write('<INPUT name="PayTo" type="radio" value="Borrower" onClick="displayBorrowerInfo()">');}
	} 		
	if (text == "Two"){
		if (payTo == "MICompany"){document.write('<INPUT name="PayTo" type="radio" value="MICompany" onClick="clearPayeeInfo()" checked>');
		}else{document.write('<INPUT name="PayTo" type="radio" value="MICompany" onClick="clearPayeeInfo()" >');}
	}
	if (text == "Three"){
		if (payTo == "Closing Agent"){document.write('<INPUT name="PayTo" type="radio" value="Closing Agent" onClick="displayClosingAgentInfo()" checked>');
		}else{document.write('<INPUT name="PayTo" type="radio" value="Closing Agent" onClick="displayClosingAgentInfo()">');}
	}	
	if (text == "Four"){
		if (payTo == "Broker"){document.write('<INPUT name="PayTo" type="radio" value="Broker" onClick="clearPayeeInfo()" checked >');
		}else{document.write('<INPUT name="PayTo" type="radio" value="Broker" onClick="clearPayeeInfo()" >');}
	}
	if (text == "Five"){
		if (payTo == "Other"){document.write('<INPUT name="PayTo" type="radio" value="Other" onClick="clearPayeeInfo()" checked >');
		}else{document.write('<INPUT name="PayTo" type="radio" value="Other" onClick="clearPayeeInfo()" >');}
	}
	if (text == "Six"){
		if (checkRouting == "Payee"){document.write('<INPUT name="CheckRouting" type="radio" value="Payee" checked >');
		}else{document.write('<INPUT name="CheckRouting" type="radio" value="Payee" >');}
	}
	if (text == "Seven"){
		if (checkRouting == "Requester"){document.write('<INPUT name="CheckRouting" type="radio" value="Requester" checked >');
		}else{document.write('<INPUT name="CheckRouting" type="radio" value="Requester">');}
	}			
	if (text == "Eight"){				
		if (checkTiming == "Normal"){document.write('<INPUT name="CheckTiming" type="radio" value="Normal" checked >');					
		}else{document.write('<INPUT name="CheckTiming" type="radio" value="Normal">');}
	}
	if (text == "Nine"){
		if (checkTiming == "Rush"){document.write('<INPUT name="CheckTiming" type="radio" value="Rush" checked >');
		}else{document.write('<INPUT name="CheckTiming" type="radio" value="Rush" >');}
	}	
}
	
function setFieldValue(fieldName, varName) {					
	switch (payTo) {
	case "Borrower":			
		document.write('<INPUT name=\"' + fieldName + '\" type="text" size="60" value=\"' + varName + '\">');		
		break;
	case "Closing Agent":
		//if (fieldName == "PayeeName") {varName = closingAgentName;}
		//if (fieldName == "PayeeAddress") {varName = closingAgentAddress;}		
		document.write('<INPUT name=\"' + fieldName + '\" type="text" size="60" value=\"' + varName + '\">');
		break;
	default:				
		if (varName != "") {
			document.write('<INPUT name=\"' + fieldName + '\" type="text" size="60" value=\"' + varName + '\">');
		} else {
			document.write('<INPUT name=\"' + fieldName + '\" type="text" size="60">');
		}		
	}			
}
	
function cancelRequest()	{
	// Called by: Disapprove - button.	
	var xxx = validateElements(document);
	if (!xxx)
	{
		return false;
	} else {
		var val = getFieldValue("CheckAmount");
		getFloatRetVal = getFloat(val);
		if (getFloatRetVal != "NaN") 
		{document.forms[0].CheckAmount.value = getFloatRetVal;}
		else
		{
			alert("Check Amount : " + val + " is not a number; please re-enter.");
			return false;
		}
		SaveCommentsToHistoricComments(checkCharacters());
		document.forms[0].CancelCheck.value = "True";		
		// 01/15/2003 JDE - Added per Charles Lee request
		// 01/30/2003 JDE - Commented out per Charles Lee request: document.forms[0].Resubmission.value = "False";		
		document.forms[0].returnCode.value = 0;
		// End 01/15/2003 update
		// 01/30/2003 JDE - Added per Charles Lee request		
		document.forms[0].ReturnForCorrection.value = "True";
		// End 01/30/2003 update
		document.forms[0].submit();
	}
}	
	

function setCheckNeeded(param1) {					
	needCheck = param1;
	//alert('needCheck: '+needCheck+'\n'+'param1: '+param1);		
}

function getFieldValue(fieldName) {
	var obj = document.getElementById(fieldName);
	//alert(obj.value);
	return obj.value;
}

function displayComments()
	{	
		temp = "<TEXTAREA name='commentText' rows='3' cols='68' wrap='virtual'>";
		temp = temp + '<%=Comments%>';
		temp = temp + "</TEXTAREA>";
		document.write(temp);
		temp = "";
	}

	
function displayBorrowerInfo() {		
	document.forms[0].PayeeName.value = trim(borrowerFirstName) + " " + trim(borrowerLastName); // payeeName;
	document.forms[0].PayeeAddress.value = payeeAddress;
}


function displayClosingAgentInfo() {
	document.forms[0].PayeeName.value = closingAgentName;
	document.forms[0].PayeeAddress.value = closingAgentAddress;	
}

function clearPayeeInfo() {
	document.forms[0].PayeeName.value = "";
	document.forms[0].PayeeAddress.value = "";	
}

function showElements()	{						
	for (var i=0; i <= document.document.forms[0].elements.length-1;i++) {
		alert(document.document.forms[0].elements[i].name + "  " + "element[" + i + "]: " + document.document.forms[0].elements[i].value);			
	}	
}
	
function displayHistoryOfChecks() {
	hcString = " ";
	if (hcDt1 != "") {hcString = hcDt1+tab+hcAmt1+tab+hcPayee1;}
	if (hcDt2 != "") {hcString += br+hcDt2+tab+hcAmt2+tab+hcPayee2;}
	if (hcDt3 != "") {hcString += br+hcDt3+tab+hcAmt3+tab+hcPayee3;}
	if (hcDt4 != "") {hcString += br+hcDt4+tab+hcAmt4+tab+hcPayee4;}
	if (hcDt5 != "") {hcString += br+hcDt5+tab+hcAmt5+tab+hcPayee5;}		
	document.write(hcString);
}

function preserveValues() {
	document.forms[0].PayeeName.value = getFieldValue("PayeeName");
	document.forms[0].PayeeAddress.value = getFieldValue("PayeeAddress");
	document.forms[0].CheckAmount.value = getFieldValue("CheckAmount");
	getFloatRetVal = getFloat(getFieldValue("CheckAmount"));
	if (getFloatRetVal != "NaN") {document.forms[0].CheckAmount.value = getFloatRetVal;}
	document.forms[0].AccountSUSPNumber.value = getFieldValue("AccountSUSPNumber");
	document.forms[0].ReasonCode.value = getFieldValue("ReasonCode");
	storeComments(checkCharacters());
	SaveHistoricComments();
}


function storeComments(com)
	{
		ChecksApprover.Comments.value = com;	
	}
	
function SaveCommentsToHistoricComments(com)
	{
		
		ChecksApprover.HistoryOfComments.value = com + '<%=" "+histOfCommFromIp%>';
		
	}
	
function SaveHistoricComments()
	{
		
		ChecksApprover.HistoryOfComments.value = '<%=" "+histOfCommFromIp%>';
		
	}

function displayHistoryOfComments() {	
	if (historyOfComments.length>0) {
		document.write('<TEXTAREA name="HistOfComm" rows="3" cols="68" READONLY>'+historyOfComments+'</TEXTAREA>');
	} else {
		document.write('<TEXTAREA name="HistOfComm" rows="3" cols="68" READONLY></TEXTAREA>');
	}	
}

function approve() {
	// Called by: Approve - button
	var xxx = validateElements(document);
	if (!xxx)
	{
		return false;
	} else
	{
		
			
		//setCheckDetails();		
		// 01/30/2003 JDE - Commented code per Charles Lee request
		// document.forms[0].Resubmission.value = "True";
		// End 01/30/2003 update
		var val = getFieldValue("CheckAmount");
		getFloatRetVal = getFloat(val);
		if (getFloatRetVal != "NaN") 
		{document.forms[0].CheckAmount.value = getFloatRetVal;}
		else
		{
			alert("Check Amount : " + val + " is not a number; please re-enter.");
			return false;
		}
		SaveCommentsToHistoricComments(checkCharacters());
		document.forms[0].returnCode.value = 0;
		document.forms[0].action = document.forms[0].ApproveURL.value;
		document.forms[0].submit();
	}
}

function checkCharacters() {	
	var checkReturnCarriage = new RegExp("\r", "g");
	var checkTab = new RegExp("\t", "g");
	var checkNewLine = new RegExp("\n", "g");
    
	checkComments = ChecksApprover.commentText.value.replace(checkReturnCarriage, "  ");
	checkComments = checkComments.replace(checkTab, " ");
	checkComments = checkComments.replace(checkNewLine, " ");
	return checkComments;
}

function trim(value) {
   var temp = value;
   var obj = /^(\s*)([\W\w]*)(\b\s*$)/;
   if (obj.test(temp)) { temp = temp.replace(obj, '$2'); }
   var obj = / +/g;
   temp = temp.replace(obj, " ");
   if (temp == " ") { temp = ""; }
   return temp;
}

function formatCurrency(num) {            
   num = num.toString().replace(/\$|\,/g,'');
   if(isNaN(num))
      num = "0";
      sign = (num == (num = Math.abs(num)));
      num = Math.floor(num*100+0.50000000001);
      cents = num%100;
      num = Math.floor(num/100).toString();
   if(cents<10)
      cents = "0" + cents;
   for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
      num = num.substring(0,num.length-(4*i+3))+','+ num.substring(num.length-(4*i+3));

   //alert(((sign)?'':'-') + '$' + num + '.' + cents);
   return (((sign)?'':'-') + '$' + num + '.' + cents);
}

function getFloat(val) {	
	var retVal = null;		
	var floatValue = val.toString().replace("$","");
	floatValue = floatValue.toString().replace(",","");	
	if (isNaN(parseFloat(floatValue))) {
		retVal = "NaN";
	} else {
		retVal = floatValue;
	}
	return retVal;
}

-->
</SCRIPT>

<LINK rel="stylesheet" href="../Styles/workflow_styles.css" type="text/css">
<STYLE type="text/css">
<!--
FORM {}
BODY {background-color: white}
TABLE {}
FONT {background-color: silver}
-->
</STYLE>
</head>
<body>
<form method="post" name="ChecksApprover" action="<%=CheckInWorkItem%>">
<TABLE cellpadding="1" cellspacing="1">
	<TBODY>
		<TR>
			<TD width="600"><B><FONT size="4">Checks Approver</FONT></B></TD>
		</TR>
	</TBODY>
</TABLE>
<BR>
<TABLE cellpadding="1">
	<TBODY>
		<TR>
			<TD height="46" width="600">
			<TABLE border="1" cellpadding="1" bgcolor="#d8d8d8">
				<TBODY>
					<TR>
						<TD width="558"><B><U>The following information is required to
						request a check:<BR>
						<BR>
						</U></B>						
						<TABLE cellpadding="1" cellspacing="1" width="492">
							<TBODY>
								<TR><TD>
								<A href="../programs/OtherDocs.jsp?loannumber=<%=LoanNumber%>" target="_blank">
								All Other docs</A></TD>
								</TR>
								<TR>
									<TD width="160"><B>Loan Number</B></TD>
									<TD width="329"><U style="font-style: normal; font-weight: bold; font-size: 10pt">

									<%=LoanNumber%>-<%=CheckDigit%>							
											
									</U></TD>
								</TR>
								<TR>
									<TD width="160"><B>Borrower's name</B></TD>
									<TD width="329"><U style="font-style: normal; font-weight: bold; font-size: 10pt">
									<%=Borrower%></U></TD>
								</TR>
								<TR>
									<TD width="160"><B>Branch office code</B></TD>
									<TD width="329"><U style="font-style: normal; font-weight: bold; font-size: 10pt">
									<%=BranchOfficeCode%></U></TD>
								</TR>
								<TR>
									<TD width="160"><B>Satellite office code</B></TD>
									<TD width="329"><U style="font-style: normal; font-weight: bold; font-size: 10pt">
									<%=SatelliteOfficeCode%></U></TD>
								</TR>
								<TR>
									<TD width="160"><B>Rep code</B></TD>
									<TD width="329"><U style="font-style: normal; font-weight: bold; font-size: 10pt">
									<%=RepresentativeCode%></U></TD>
								</TR>
							</TBODY>
						</TABLE>
						

						<BR>
						<B>Pay to:<BR>
						</B>
						<TABLE cellpadding="1" cellspacing="1" width="499">
							<TBODY>
								<TR>
									<TD width="23"></TD>
									<TD width="473"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("One"); 
										--> 
										</SCRIPT> Borrower</TD>
								</TR>
								<TR>
									<TD width="23"></TD>
									<TD width="473"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Two"); 
										--> 
										</SCRIPT> MI Company</TD>
								</TR>
								<TR>
									<TD width="23"></TD>
									<TD width="473"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Three"); 
										--> 
										</SCRIPT> Closing Agent</TD>
								</TR>
								<TR>
									<TD width="23"></TD>
									<TD width="473"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Four"); 
										--> 
										</SCRIPT> Broker</TD>
								</TR>
								<TR>
									<TD width="23"></TD>
									<TD width="473"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Five"); 
										--> 
										</SCRIPT> Other</TD>
								</TR>
							</TBODY>
						</TABLE>
						<TABLE cellpadding="1" cellspacing="1" width="498">
							<TBODY>
								<TR>
									<TD width="160"><B>Payee Name</B></TD>
									<TD width="335">
										<SCRIPT language="JavaScript">
											<!--
											setFieldValue("PayeeName",payeeName);
											-->
										</SCRIPT>
									</TD>
								</TR>
								<TR>
									<TD width="160"><B>Payee Address</B></TD>
									<TD width="335">
										<SCRIPT language="JavaScript">
											<!--
											setFieldValue("PayeeAddress",payeeAddress);
											-->
										</SCRIPT>
									</TD>
								</TR>
								<TR>
									<TD width="160"><B>Check amount</B></TD>
									<TD width="335">
										<SCRIPT language="JavaScript">
											<!--
											setFieldValue("CheckAmount", displayCheckAmt);
											-->
										</SCRIPT>
										<!--<INPUT name="CheckAmount" type="text" onBlur="getFieldValue('CheckAmount',document.forms[0].CheckAmount.value)">-->
									</TD>
								</TR>
								<TR>
									<TD width="160"><B>Account/SUSP number</B></TD>
									<TD width="335">
										<SCRIPT language="JavaScript">
											<!--
											setFieldValue("AccountSUSPNumber", accountSUSPNumber);
											-->
										</SCRIPT>
									</TD>
								</TR>
								<TR>
									<TD width="160" valign="top"><B>Check request reason:</B></TD>
									<TD width="335"><select name="ReasonCode">
										
                                                                        <%
										Hashtable ht = getReasonCodeAndTexts();
										for (Enumeration e = ht.keys() ; e.hasMoreElements() ;) 
										{
											String code = (String) e.nextElement();
											String text = (String) ht.get(code);%>
											<option value="<%=code%>"<%
											if(code.equals(reasonCode))
												out.write(" selected");
											%>><%=text%></option><%		
										}
									%></select>
									</TD>
								</TR>
								<TR>
									<BR>
									<TD width="160" valing="top"></TD>
									<TD width="395"><B>* A copy of the check must be obtained for our records</B></TD>
								</TR>
							</TBODY>
						</TABLE>
						<BR>
						<TABLE width="556" cellspacing="1" cellpadding="1" id="checkrouting">
							<TBODY>
								<TR>
									<TD width="160"><B>Check Routing:</B></TD>
									<TD width="191"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Six"); 
										--> 
										</SCRIPT> Mail original to payee</TD>
									<TD width="201"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Seven"); 
										-->
										</SCRIPT> Return to requestor</TD>
								</TR>
							</TBODY>
						</TABLE>

						<TABLE width="557" cellspacing="1" cellpadding="1"
							id="checkrouting">
							<TBODY>
								<TR>
									<TD width="160"><B>Check Timing:</B></TD>
									<TD width="191"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Eight"); 
										-->
										</SCRIPT> Normal</TD>
									<TD width="202"><SCRIPT language="JavaScript">
										<!-- 
											showRadio("Nine"); 
										-->
										</SCRIPT> Rush</TD>
								</TR>
								<TR>
									<TD width="160"><B>User ID of Requestor:</B></TD>
									<TD width="191"><%=UserIDOrig%></TD>
									<TD width="202">(for Approver)</TD>
								</TR>
								
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					
				</TBODY>
			</TABLE>
			<BR>
			<B><U>History Of Comments:</U></B>
			<BR>			
			<SCRIPT language="JavaScript">
			<!--
				displayHistoryOfComments();
			-->
			</SCRIPT>			
			<BR>			
			<BR>
			<B><U>Comments:</U></B>
			<BR>
			<script language="Javascript">
            <!--
				displayComments();
			-->
			</script>
			<BR>
			<B><U>History of checks requested for this loan within workflow:</U></B>
			<TABLE width="95%" border="1" cellpadding="0" cellspacing="0">
				<TBODY>
					<TR>
						<TD width="427"><SCRIPT language="JavaScript">
							<!-- 
							displayHistoryOfChecks(); 
							-->
							</SCRIPT>
						</TD>
					</TR>
				</TBODY>
			</TABLE>			
		<TR>
			<TD width="600"><BR>
			<INPUT type="button" name="checkinWorkitem" value="Approve" onclick="approve()">&nbsp; 
			<INPUT type="button" name="cancelWorkItem" value="Disapprove" onclick="cancelRequest()">&nbsp; 
			<INPUT type="button" name="PendforSelf" value="Pend For Self" onclick="pendForSelf()"> &nbsp; 
                        <!--<INPUT type="button" name="cancelWorkItem" value="Cancel" onClick="javascript:{location='<%=CancelWorkItem%>';}">&nbsp; -->
			<INPUT type="button" name="Help" value="Help" onclick="helpMsg()">			
		</TR>
	</TBODY>
</TABLE>
</TD>
</TR>
</TBODY>
</TABLE>
<INPUT type="hidden" name="returnCode">
<INPUT type="hidden" name="CancelCheck"> 
<INPUT type="hidden" name="Comments"> 
<INPUT type="hidden" name="AccountSUSPNumber">
<INPUT type="hidden" name="HistoryOfComments">
<INPUT type="hidden" name="ReasonCode">
<INPUT type="hidden" name="ReturnForCorrection">

<INPUT type="hidden" name="ActivityName" value='<%=ActivityName%>'>
<INPUT type="hidden" name="WFCheckNumber" value='<%=WFCheckNumber%>'>
<INPUT type="hidden" name="LoanNumber" value='<%=LoanNumber%>'>
<INPUT type="hidden" name="UserID" value='<%=UserID%>'>
<INPUT type="hidden" name="BlockCheckNumber" value="<%=blockCheckNumber%>"> 



<input type="hidden" name="ApproveURL" value="<%=context.getCommand("x-approveCheckRequest", workItem.persistentOid())%>">
</form>
</body>
</html>