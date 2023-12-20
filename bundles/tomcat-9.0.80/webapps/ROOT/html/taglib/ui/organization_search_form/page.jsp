<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/taglib/ui/organization_search_form/init.jsp" %>

<%
OrganizationSearch searchContainer = (OrganizationSearch)request.getAttribute("liferay-ui:search:searchContainer");

OrganizationDisplayTerms displayTerms = (OrganizationDisplayTerms)searchContainer.getDisplayTerms();

String type = displayTerms.getType();
%>

<liferay-ui:search-toggle
	buttonLabel="search"
	displayTerms="<%= displayTerms %>"
	id="toggle_id_organization_search"
>
	<aui:fieldset>
		<aui:input inlineField="<%= true %>" name="<%= OrganizationDisplayTerms.NAME %>" size="20" type="text" value="<%= displayTerms.getName() %>" />

		<aui:select inlineField="<%= true %>" name="<%= OrganizationDisplayTerms.TYPE %>">
			<aui:option value=""></aui:option>

			<%
			for (String curType : OrganizationLocalServiceUtil.getTypes()) {
			%>

				<aui:option label="<%= curType %>" selected="<%= type.equals(curType) %>" />

			<%
			}
			%>

		</aui:select>

		<aui:input inlineField="<%= true %>" name="<%= OrganizationDisplayTerms.STREET %>" size="20" type="text" value="<%= displayTerms.getStreet() %>" />
	</aui:fieldset>

	<aui:fieldset>
		<aui:select inlineField="<%= true %>" label="country" name="<%= OrganizationDisplayTerms.COUNTRY_ID %>" />

		<aui:input inlineField="<%= true %>" name="<%= OrganizationDisplayTerms.CITY %>" size="20" type="text" value="<%= displayTerms.getCity() %>" />

		<aui:select inlineField="<%= true %>" label="region" name="<%= OrganizationDisplayTerms.REGION_ID %>" />
	</aui:fieldset>

	<aui:fieldset>
		<aui:input inlineField="<%= true %>" label="postal-code" name="<%= OrganizationDisplayTerms.ZIP %>" size="20" type="text" value="<%= displayTerms.getZip() %>" />
	</aui:fieldset>
</liferay-ui:search-toggle>

<%
Organization parentOrganization = null;

if (displayTerms.getParentOrganizationId() > 0) {
	try {
		parentOrganization = OrganizationLocalServiceUtil.getOrganization(displayTerms.getParentOrganizationId());
	}
	catch (NoSuchOrganizationException nsoe) {
	}
}
%>

<c:if test="<%= parentOrganization != null %>">
	<aui:input name="<%= OrganizationDisplayTerms.PARENT_ORGANIZATION_ID %>" type="hidden" value="<%= parentOrganization.getOrganizationId() %>" />

	<br />

	<liferay-ui:message key="filter-by-organization" />: <%= HtmlUtil.escape(parentOrganization.getName()) %><br />
</c:if>

<script>
	new Liferay.DynamicSelect(
		[
			{
				select: '<portlet:namespace /><%= OrganizationDisplayTerms.COUNTRY_ID %>',
				selectData: Liferay.Address.getCountries,
				selectDesc: 'nameCurrentValue',
				selectId: 'countryId',
				selectSort: '<%= true %>',
				selectVal: '<%= displayTerms.getCountryId() %>'
			},
			{
				select: '<portlet:namespace /><%= OrganizationDisplayTerms.REGION_ID %>',
				selectData: Liferay.Address.getRegions,
				selectDesc: 'name',
				selectId: 'regionId',
				selectVal: '<%= displayTerms.getRegionId() %>'
			}
		]
	);
</script>