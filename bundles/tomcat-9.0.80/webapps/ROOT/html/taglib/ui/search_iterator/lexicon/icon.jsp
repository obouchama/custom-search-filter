<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/search_iterator/init.jsp" %>

<%@ include file="/html/taglib/ui/search_iterator/lexicon/top.jspf" %>

<%
if (searchResultCssClass == null) {
	searchResultCssClass = "display-style-icon list-unstyled row";
}

request.setAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW_CHECKER, rowChecker);

boolean allRowsIsChecked = true;

List<ResultRowSplitterEntry> resultRowSplitterEntries = new ArrayList<ResultRowSplitterEntry>();

if (resultRowSplitter != null) {
	resultRowSplitterEntries = resultRowSplitter.split(searchContainer.getResultRows());
}
else {
	resultRowSplitterEntries.add(new ResultRowSplitterEntry(StringPool.BLANK, resultRows));
}

for (int i = 0; i < resultRowSplitterEntries.size(); i++) {
	ResultRowSplitterEntry resultRowSplitterEntry = resultRowSplitterEntries.get(i);

	List<com.liferay.portal.kernel.dao.search.ResultRow> curResultRows = resultRowSplitterEntry.getResultRows();
%>

	<dl class="<%= searchResultCssClass %>" data-qa-id="rows<%= i %>">
		<c:choose>
			<c:when test="<%= Validator.isNotNull(resultRowSplitterEntry.getTitle()) %>">
				<dt class="container-fluid splitter splitter-spaced">
					<liferay-ui:message key="<%= resultRowSplitterEntry.getTitle() %>" />
				</dt>
			</c:when>
			<c:otherwise>
				<dt class="sr-only">
					<%= PortalUtil.getPortletTitle(portletRequest) %>
				</dt>
			</c:otherwise>
		</c:choose>

		<%
		for (int j = 0; j < curResultRows.size(); j++) {
			com.liferay.portal.kernel.dao.search.ResultRow row = curResultRows.get(j);

			primaryKeysJSONArray.put(row.getPrimaryKey());

			request.setAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW, row);

			List entries = row.getEntries();

			boolean rowIsChecked = false;
			boolean rowIsDisabled = false;

			if (rowChecker != null) {
				rowIsChecked = rowChecker.isChecked(row.getObject());
				rowIsDisabled = rowChecker.isDisabled(row.getObject());

				if (!rowIsChecked) {
					allRowsIsChecked = false;
				}

				String rowSelector = rowChecker.getRowSelector();

				if (Validator.isNull(rowSelector)) {
					Map<String, Object> rowData = row.getData();

					if (rowData == null) {
						rowData = new HashMap<String, Object>();
					}

					rowData.put("selectable", !rowIsDisabled);

					row.setData(rowData);
				}
			}

			request.setAttribute("liferay-ui:search-container-row:rowId", id.concat(StringPool.UNDERLINE.concat(row.getRowId())));

			Map<String, Object> data = row.getData();

			if (data == null) {
				data = new HashMap<String, Object>();
			}
		%>

			<dd class="<%= GetterUtil.getString(row.getClassName()) %> <%= row.getCssClass() %> <%= rowIsChecked ? "active" : StringPool.BLANK %>" data-qa-id="row" <%= AUIUtil.buildData(data) %>>

				<%
				for (int k = 0; k < entries.size(); k++) {
					com.liferay.portal.kernel.dao.search.SearchEntry entry = (com.liferay.portal.kernel.dao.search.SearchEntry)entries.get(k);

					entry.setIndex(k);

					request.setAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW_ENTRY, entry);

					entry.print(pageContext.getOut(), request, response);
				}
				%>

			</dd>

		<%
			request.removeAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
			request.removeAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW_ENTRY);

			request.removeAttribute("liferay-ui:search-container-row:rowId");
		}
		%>

		<c:if test="<%= i == (resultRowSplitterEntries.size() - 1) %>">
			<dd></dd>
		</c:if>
	</dl>

<%
}

String rowHtmlTag = "dd";
%>

<%@ include file="/html/taglib/ui/search_iterator/lexicon/bottom.jspf" %>