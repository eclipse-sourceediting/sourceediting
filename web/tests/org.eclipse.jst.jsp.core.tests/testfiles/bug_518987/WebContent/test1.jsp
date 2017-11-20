<%@taglib uri="http://eclipse.org/test_518987" prefix="test"%>
<select name="{{labelValue}}">
	<option value="one">one value</option>
	<option value="two"> <%= "two value" %> </option>
	<option value="three"> <test:insertattr insert="three value"></test:insertattr> </option>
</select>
