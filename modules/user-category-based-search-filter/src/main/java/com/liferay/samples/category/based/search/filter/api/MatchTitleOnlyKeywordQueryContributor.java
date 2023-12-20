package com.liferay.samples.category.based.search.filter.api;

import aQute.bnd.annotation.metatype.Configurable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.util.Map;
import java.util.Optional;

import static com.liferay.samples.category.based.search.filter.constant.SearchConstant.LOCALIZED_TITLE;


@Component(
        immediate = true,
        service = {KeywordQueryContributor.class},
        configurationPid = "com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration"
)
public class MatchTitleOnlyKeywordQueryContributor implements KeywordQueryContributor {

    private CustomSearchFilterConfiguration _configuration;

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        _configuration = Configurable.createConfigurable(CustomSearchFilterConfiguration.class, properties);
    }

    @Override
    public void contribute(String keywords, BooleanQuery searchQuery, KeywordQueryContributorHelper keywordQueryContributorHelper) {

        if (_log.isDebugEnabled()) {
            _log.debug("Match Title Only KeywordQueryContributor invoked.");
        }

        SearchContext searchContext = keywordQueryContributorHelper.getSearchContext();
        Boolean preFilter = Optional.ofNullable((Boolean) searchContext.getAttribute(_configuration.preFilterParam())).orElse(Boolean.FALSE);

        if (preFilter && Validator.isNotNull(keywords)) {
            MatchQuery matchQuery = new MatchQuery(LOCALIZED_TITLE, StringUtil.toLowerCase(searchContext.getKeywords()));
            matchQuery.setType(MatchQuery.Type.PHRASE_PREFIX);
            try {
                searchQuery.add(matchQuery, BooleanClauseOccur.MUST);
            } catch (ParseException e) {
                if (_log.isErrorEnabled()) {
                    _log.error(e.getLocalizedMessage());
                }
            }
        }
    }

    private static final Log _log = LogFactoryUtil.getLog(MatchTitleOnlyKeywordQueryContributor.class);
}
