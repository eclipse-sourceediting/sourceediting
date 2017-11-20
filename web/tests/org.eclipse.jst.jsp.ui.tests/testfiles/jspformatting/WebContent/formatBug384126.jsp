Try to create test.jsp and click Ctrl+Alt+F (Autoformat)

it wanishes "${pagingAndSor__________________" variable

Source:


  <c:if test="${pagingAndSort.pageCount > 1}">
    <div id="pager">
      <fmt:message key="pay.system.reward.paging">
        <fmt:param>
          <c:if test="${pagingAndSort.page != 1}">
            <a href="javascript:void(0);"
              onclick="doPaging('<c:out value="${pagingAndSort.page - 1}"/>'); return false;"> <img
              src="img/icons/arrow_left.gif" width="16" height="16" />
            </a>
          </c:if>
          <input size="1" value="<c:out value="${pagingAndSort.page}"/>" type="text"
            onchange="doPaging(this.value); return false;" />
          <c:if test="${pagingAndSort.page != pagingAndSort.pageCount}">
            <a href="javascript:void(0);"
              onclick="doPaging('<c:out value="${pagingAndSort.page + 1}"/>'); return false;"> <img
              src="img/icons/arrow_right.gif" width="16" height="16" />
            </a>
          </c:if>
        </fmt:param>
        <fmt:param>
          <c:out value="${pagingAndSort.pageCount}" />
        </fmt:param>
        <fmt:param>
          <form:select path="pagingAndSort.itemsPerPage" items="${viewPerPage}" id="itemsPerP"
            onchange="doChangeItemPerPage(this.value); return false;" />
        </fmt:param>
        <fmt:param>
          <strong><c:out value="${pagingAndSort.itemsCount}" /></strong>
        </fmt:param>
      </fmt:message>
    </div>
  </c:if>

<script type="text/javascript">
	function doSort(sortProperty) {
		var form = document.getElementById("sortForm");
		document.getElementById("sortProperty").value = sortProperty;
		var sortPropertyBase = '<c:out value="${pagingAndSort.sortProperty}"/>';
		var sortOrder = document.getElementById("sortOrder");
		if (sortProperty == sortPropertyBase) {
			if ("asc" == sortOrder.value) {
				sortOrder.value = "desc";
			} else {
				sortOrder.value = "asc";
			}
		} else {
			sortOrder.value = "desc";
		}
		form.submit();
	}
	function doPaging(page) {
		var form = document.getElementById("sortForm");
		document.getElementById("page").value = page;
		form.submit();
	}
	function doChangeItemPerPage(itemsPerPage) {
		var form = document.getElementById("sortForm");
		document.getElementById("itemsPerPage").value = itemsPerPage;
		form.submit();
	}
</script>