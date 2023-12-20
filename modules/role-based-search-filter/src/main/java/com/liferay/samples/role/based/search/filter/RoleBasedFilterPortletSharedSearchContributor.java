package com.liferay.samples.role.based.search.filter;

import aQute.bnd.annotation.metatype.Configurable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.util.Map;

import static com.liferay.samples.role.based.search.filter.RoleBasedFilterPortletKeys.PRE_FILTER_PARAM;

@Component(
        immediate = true,
        property = "javax.portlet.name=" + RoleBasedFilterPortletKeys.ROLE_BASED_FILTER,
        service = PortletSharedSearchContributor.class,
        configurationPid = "com.liferay.samples.category.based.search.filter.config.CustomSearchFilterConfiguration"
)
public class RoleBasedFilterPortletSharedSearchContributor implements PortletSharedSearchContributor {

    private CustomSearchFilterConfiguration _configuration;

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        _configuration = Configurable.createConfigurable(CustomSearchFilterConfiguration.class, properties);
    }

    @Override
    public void contribute(
            PortletSharedSearchSettings portletSharedSearchSettings) {

        _log.debug("Contribute role based filter");

        SearchContext searchContext = portletSharedSearchSettings.getSearchContext();

        searchContext.setAttribute(_configuration.preFilterParam(), true);
    }

    private static final Log _log = LogFactoryUtil.getLog(RoleBasedFilterPortletSharedSearchContributor.class);

}
