<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page 
import="DefaultSubPerson0ViewBean" 
contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" 
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE></TITLE>

<!--Styles-->
<STYLE type="text/css">
<!--
H1 {
	text-align: center !IMPORTANT;
}

TH {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TD {
	text-align: left !IMPORTANT;
	vertical-align: top !IMPORTANT;
}

TH.result {
	background-color: #999999 !IMPORTANT;
}

TD.result {
	background-color: #cccccc;
	vertical-align: top !IMPORTANT;
}
-->
</STYLE>


</HEAD>
<BODY>

<jsp:useBean id="defaultSubPerson0Bean" scope="request"
	class="DefaultSubPerson0ViewBean" type="DefaultSubPerson0ViewBean" />
<%
// Instantiate the bean properties
if (defaultSubPerson0Bean.getAddress() == null)
	defaultSubPerson0Bean.setAddress(new webtools.bvt.Address());
%>
<jsp:setProperty name="defaultSubPerson0Bean" property="company"
	value='<%=new java.lang.String(request.getParameter("company"))%>' />
<jsp:setProperty name="defaultSubPerson0Bean" property="name"
	value='<%=new java.lang.String(request.getParameter("name"))%>' />
<% 
// Initialize the bean properties from the request parameters
defaultSubPerson0Bean.getAddress().setCity(new java.lang.String(request.getParameter("setCity_city")));
defaultSubPerson0Bean.getAddress().setZip(Integer.valueOf(request.getParameter("setZip_zip")).intValue());
defaultSubPerson0Bean.getAddress().setState(new java.lang.String(request.getParameter("setState_state")));
defaultSubPerson0Bean.getAddress().setStreetNumber(Integer.valueOf(request.getParameter("setStreetNumber_streetNumber")).intValue());
defaultSubPerson0Bean.getAddress().setStreet(new java.lang.String(request.getParameter("setStreet_street")));
defaultSubPerson0Bean.getAddress();

%>
<jsp:setProperty name="defaultSubPerson0Bean" property="age"
	value='<%=Integer.valueOf(request.getParameter("age")).intValue()%>' />

<!--Banner-->
<H1></H1>

<BR>
<BR>

<!-- Result Table -->
<TABLE border="0">
	<TBODY>
		<TR>
			<TH>company</TH>
			<TD><%=defaultSubPerson0Bean.getCompany()%></TD>
		</TR>
		<TR>
			<TH>name</TH>
			<TD><%=defaultSubPerson0Bean.getName()%></TD>
		</TR>
		<TR>
			<TH>friends</TH>
			<TD>
			<TABLE>
				<TBODY>
					<TR>
						<TH class="result">name</TH>
						<TH class="result">friends</TH>
						<TH class="result">address</TH>
						<TH class="result">age</TH>
						<TH class="result">class</TH>
					</TR>
					<%
	if (defaultSubPerson0Bean.getFriends()!= null) {
		for (int i0=0; i0<defaultSubPerson0Bean.getFriends().length; i0++) {
%>
					<TR>
						<TD class="result">
						<TABLE>
							<TBODY>

								<TR>
									<TD><INPUT name='name' type='text'
										value='<%=defaultSubPerson0Bean.getFriends()[i1].getName()%>'>
									</TD>
								</TR>
							</TBODY>
						</TABLE>
						</TD>
						<TD class="result">
						<TABLE>
							<TBODY>

								<TR>
									<TH class="result">friends</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getFriends()[i1].getFriends()!= null) {
		for (int i2=0; i2<defaultSubPerson0Bean.getFriends()[i1].getFriends().length; i2++) {
%>
								<TR>
									<TD class="result"><INPUT name='friends' type='text'
										value='<%=defaultSubPerson0Bean.getFriends()[i1].getFriends()[i2]== null ? "NULL" : defaultSubPerson0Bean.getFriends()[i1].getFriends()[i2].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
							</TBODY>
						</TABLE>
						</TD>
						<TD class="result">
						<TABLE>
							<TBODY>

								<TR>
									<TD><INPUT name='address' type='text'
										value='<%=defaultSubPerson0Bean.getFriends()[i1].getAddress()== null ? "NULL" : defaultSubPerson0Bean.getFriends()[i1].getAddress().toString()%>'>
									</TD>
								</TR>
							</TBODY>
						</TABLE>
						</TD>
						<TD class="result">
						<TABLE>
							<TBODY>

								<TR>
									<TD><INPUT name='age' type='text'
										value='<%=defaultSubPerson0Bean.getFriends()[i1].getAge()%>'>
									</TD>
								</TR>
							</TBODY>
						</TABLE>
						</TD>
						<TD class="result">
						<TABLE>
							<TBODY>

								<TR>
									<TD><INPUT name='class' type='text'
										value='<%=defaultSubPerson0Bean.getFriends()[i1].getClass()== null ? "NULL" : defaultSubPerson0Bean.getFriends()[i1].getClass().toString()%>'>
									</TD>
								</TR>
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<%
		}
	}
%>
					<!-- end of outer table -->
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TH>address</TH>
			<TD>
			<TABLE>
				<TBODY>
					<TR>
						<TH>city</TH>
						<TD><INPUT name='city' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getCity()%>'></TD>
					</TR>
					<TR>
						<TH>zip</TH>
						<TD><INPUT name='zip' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getZip()%>'></TD>
					</TR>
					<TR>
						<TH>state</TH>
						<TD><INPUT name='state' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getState()%>'></TD>
					</TR>
					<TR>
						<TH>streetNumber</TH>
						<TD><INPUT name='streetNumber' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getStreetNumber()%>'>
						</TD>
					</TR>
					<TR>
						<TH>street</TH>
						<TD><INPUT name='street' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getStreet()%>'></TD>
					</TR>
					<TR>
						<TH>class</TH>
						<TD><INPUT name='class' type='text'
							value='<%=defaultSubPerson0Bean.getAddress().getClass()== null ? "NULL" : defaultSubPerson0Bean.getAddress().getClass().toString()%>'>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<TR>
			<TH>age</TH>
			<TD><%=defaultSubPerson0Bean.getAge()%></TD>
		</TR>
		<TR>
			<TH>class</TH>
			<TD>
			<TABLE>
				<TBODY>
					<TR>
						<TH>package</TH>
						<TD><INPUT name='package' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getPackage()== null ? "NULL" : defaultSubPerson0Bean.getClass().getPackage().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>name</TH>
						<TD><INPUT name='name' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getName()%>'></TD>
					</TR>
					<TR>
						<TH>declaredClasses</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">declaredClasses</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getDeclaredClasses()!= null) {
		for (int i3=0; i3<defaultSubPerson0Bean.getClass().getDeclaredClasses().length; i3++) {
%>
								<TR>
									<TD class="result"><INPUT name='declaredClasses' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getDeclaredClasses()[i3]== null ? "NULL" : defaultSubPerson0Bean.getClass().getDeclaredClasses()[i3].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>classes</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">classes</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getClasses()!= null) {
		for (int i4=0; i4<defaultSubPerson0Bean.getClass().getClasses().length; i4++) {
%>
								<TR>
									<TD class="result"><INPUT name='classes' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getClasses()[i4]== null ? "NULL" : defaultSubPerson0Bean.getClass().getClasses()[i4].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>methods</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">methods</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getMethods()!= null) {
		for (int i5=0; i5<defaultSubPerson0Bean.getClass().getMethods().length; i5++) {
%>
								<TR>
									<TD class="result"><INPUT name='methods' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getMethods()[i5]== null ? "NULL" : defaultSubPerson0Bean.getClass().getMethods()[i5].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>declaringClass</TH>
						<TD><INPUT name='declaringClass' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getDeclaringClass()== null ? "NULL" : defaultSubPerson0Bean.getClass().getDeclaringClass().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>declaredFields</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">declaredFields</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getDeclaredFields()!= null) {
		for (int i6=0; i6<defaultSubPerson0Bean.getClass().getDeclaredFields().length; i6++) {
%>
								<TR>
									<TD class="result"><INPUT name='declaredFields' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getDeclaredFields()[i6]== null ? "NULL" : defaultSubPerson0Bean.getClass().getDeclaredFields()[i6].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>signers</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">signers</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getSigners()!= null) {
		for (int i7=0; i7<defaultSubPerson0Bean.getClass().getSigners().length; i7++) {
%>
								<TR>
									<TD class="result"><INPUT name='signers' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getSigners()[i7]== null ? "NULL" : defaultSubPerson0Bean.getClass().getSigners()[i7].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>interfaces</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">interfaces</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getInterfaces()!= null) {
		for (int i8=0; i8<defaultSubPerson0Bean.getClass().getInterfaces().length; i8++) {
%>
								<TR>
									<TD class="result"><INPUT name='interfaces' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getInterfaces()[i8]== null ? "NULL" : defaultSubPerson0Bean.getClass().getInterfaces()[i8].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>interface</TH>
						<TD><INPUT name='interface' type='text'
							value='<%=defaultSubPerson0Bean.getClass().isInterface()%>'></TD>
					</TR>
					<TR>
						<TH>constructors</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">constructors</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getConstructors()!= null) {
		for (int i9=0; i9<defaultSubPerson0Bean.getClass().getConstructors().length; i9++) {
%>
								<TR>
									<TD class="result"><INPUT name='constructors' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getConstructors()[i9]== null ? "NULL" : defaultSubPerson0Bean.getClass().getConstructors()[i9].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>superclass</TH>
						<TD><INPUT name='superclass' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getSuperclass()== null ? "NULL" : defaultSubPerson0Bean.getClass().getSuperclass().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>componentType</TH>
						<TD><INPUT name='componentType' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getComponentType()== null ? "NULL" : defaultSubPerson0Bean.getClass().getComponentType().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>array</TH>
						<TD><INPUT name='array' type='text'
							value='<%=defaultSubPerson0Bean.getClass().isArray()%>'></TD>
					</TR>
					<TR>
						<TH>protectionDomain</TH>
						<TD><INPUT name='protectionDomain' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getProtectionDomain()== null ? "NULL" : defaultSubPerson0Bean.getClass().getProtectionDomain().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>fields</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">fields</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getFields()!= null) {
		for (int i10=0; i10<defaultSubPerson0Bean.getClass().getFields().length; i10++) {
%>
								<TR>
									<TD class="result"><INPUT name='fields' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getFields()[i10]== null ? "NULL" : defaultSubPerson0Bean.getClass().getFields()[i10].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>classLoader</TH>
						<TD><INPUT name='classLoader' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getClassLoader()== null ? "NULL" : defaultSubPerson0Bean.getClass().getClassLoader().toString()%>'>
						</TD>
					</TR>
					<TR>
						<TH>primitive</TH>
						<TD><INPUT name='primitive' type='text'
							value='<%=defaultSubPerson0Bean.getClass().isPrimitive()%>'></TD>
					</TR>
					<TR>
						<TH>declaredConstructors</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">declaredConstructors</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getDeclaredConstructors()!= null) {
		for (int i11=0; i11<defaultSubPerson0Bean.getClass().getDeclaredConstructors().length; i11++) {
%>
								<TR>
									<TD class="result"><INPUT name='declaredConstructors'
										type='text'
										value='<%=defaultSubPerson0Bean.getClass().getDeclaredConstructors()[i11]== null ? "NULL" : defaultSubPerson0Bean.getClass().getDeclaredConstructors()[i11].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>modifiers</TH>
						<TD><INPUT name='modifiers' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getModifiers()%>'></TD>
					</TR>
					<TR>
						<TH>declaredMethods</TH>
						<TD>
						<TABLE>
							<TBODY>
								<TR>
									<TH class="result">declaredMethods</TH>
								</TR>
								<%
	if (defaultSubPerson0Bean.getClass().getDeclaredMethods()!= null) {
		for (int i12=0; i12<defaultSubPerson0Bean.getClass().getDeclaredMethods().length; i12++) {
%>
								<TR>
									<TD class="result"><INPUT name='declaredMethods' type='text'
										value='<%=defaultSubPerson0Bean.getClass().getDeclaredMethods()[i12]== null ? "NULL" : defaultSubPerson0Bean.getClass().getDeclaredMethods()[i12].toString()%>'>
									</TD>
								</TR>
								<%
		}
	}
%>
								<!-- end of outer table -->
							</TBODY>
						</TABLE>
						</TD>
					</TR>
					<TR>
						<TH>class</TH>
						<TD><INPUT name='class' type='text'
							value='<%=defaultSubPerson0Bean.getClass().getClass()== null ? "NULL" : defaultSubPerson0Bean.getClass().getClass().toString()%>'>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			</TD>
		</TR>
		<%
defaultSubPerson0Bean.wait();
%>
		<%
defaultSubPerson0Bean.wait(Long.valueOf(request.getParameter("wait_long_int__timeout")).longValue(), Integer.valueOf(request.getParameter("wait_long_int__nanos")).intValue());
%>
		<%
defaultSubPerson0Bean.wait(Long.valueOf(request.getParameter("wait_long__timeout")).longValue());
%>
		<%
defaultSubPerson0Bean.notifyAll();
%>
		<%
defaultSubPerson0Bean.notify();
%>
		<%
java.lang.String methodResult5120670 = defaultSubPerson0Bean.toString();
%>
		<TR>
			<TH>toString</TH>
			<TD><%=methodResult5120670%></TD>
		</TR>
		<%
boolean methodResult29548899 = defaultSubPerson0Bean.equals(new java.lang.Object(request.getParameter("equals_java_lang_Object__obj")));
%>
		<TR>
			<TH>equals</TH>
			<TD><%=methodResult29548899%></TD>
		</TR>
		<%
int methodResult11251596 = defaultSubPerson0Bean.hashCode();
%>
		<TR>
			<TH>hashCode</TH>
			<TD><%=methodResult11251596%></TD>
		</TR>
	</TBODY>
</TABLE>
</BODY>
</HTML>
