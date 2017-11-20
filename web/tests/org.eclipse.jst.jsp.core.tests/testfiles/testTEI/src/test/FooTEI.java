package test;

public class FooTEI extends javax.servlet.jsp.tagext.TagExtraInfo {

	@Override
	public boolean isValid(javax.servlet.jsp.tagext.TagData data) {
		return data.getAttribute("attr") != null;
	}

}