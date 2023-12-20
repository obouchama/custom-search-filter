package com.liferay.samples.category.based.search.filter.api;

import aQute.bnd.annotation.metatype.Configurable;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;
import com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.liferay.samples.category.based.search.filter.constant.SearchConstant.DDM_STRUCTURE_KEY_ATTR;
import static com.liferay.samples.category.based.search.filter.constant.SearchConstant.SEARCH_REQUEST_ATTR;

/**
 * @author obouchama
 */
@Component(
        immediate = true,
        property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
        service = ModelPreFilterContributor.class,
        configurationPid = "com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration"
)
public class UserCategoryPreFilterContributor implements ModelPreFilterContributor {

    private CustomSearchFilterConfiguration _configuration;

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        _configuration = Configurable.createConfigurable(CustomSearchFilterConfiguration.class, properties);
    }

    @Override
    public void contribute(BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings, SearchContext searchContext) {

        if (_log.isDebugEnabled()) {
            _log.debug("User Category PreFilterContributor invoked.");
        }

        try {
            long userId = searchContext.getUserId();
            User user = _userLocalService.getUser(userId);
            long groupId = user.getGroupId();

            SearchRequest searchRequest = (SearchRequest) searchContext.getAttribute(SEARCH_REQUEST_ATTR);
            List<ComplexQueryPart> complexQueryParts = searchRequest.getComplexQueryParts();

            List<DDMStructure> structures = DDMStructureLocalServiceUtil.getStructures(groupId);

            structures.forEach(struct -> struct.getName(Locale.FRANCE));

            Optional<ComplexQueryPart> complexQueryPartOptional = complexQueryParts.stream().filter(cqp -> DDM_STRUCTURE_KEY_ATTR.equals(cqp.getField())).findFirst();
            String ddmStructureKey = complexQueryPartOptional.map(ComplexQueryPart::getValue).orElse(null);

            Boolean preFilter = Optional.ofNullable((Boolean) searchContext.getAttribute(_configuration.preFilterParam())).orElse(Boolean.FALSE);
            if (preFilter) {

                // getRoles
                // get categories with the same titles and group them by Vocabulary
                // global query --> AND/MUST on Vocab and OR/SHOULD on map value(list of categ)

                List<Role> roles = user.getRoles();

                List<String> roleNames = roles.stream().map(RoleModel::getName).collect(Collectors.toList());

                List<AssetVocabulary> assetVocabularies = _assetVocabularyLocalService.getAssetVocabularies(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
                Function<AssetVocabulary, List<AssetCategory>> assetVocabularyListFunction = vocabulary -> {

                    AssetCategory vocabularyTousCategory = _assetCategoryLocalService.fetchCategory(groupId, 0, "TOUS", vocabulary.getVocabularyId());

                    List<AssetCategory> childCategories = _assetCategoryLocalService.getVocabularyRootCategories(vocabulary.getVocabularyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
                    List<AssetCategory> filteredCategories = childCategories.stream().filter(categ -> roleNames.contains(categ.getName())).collect(Collectors.toList());

                    if (Validator.isNotNull(vocabularyTousCategory)) {
                        filteredCategories.add(vocabularyTousCategory);
                        return filteredCategories;
                    }
                    return null;
                };
                List<List<AssetCategory>> categoriesByVocabulary = assetVocabularies.stream().map(assetVocabularyListFunction).filter(Objects::nonNull).collect(Collectors.toList());

                categoriesByVocabulary.forEach(categoryList -> {
                    BooleanFilter bf = new BooleanFilter();
                    List<Long> assetCategoryIds = categoryList.stream().map(AssetCategory::getCategoryId).collect(Collectors.toList());
                    TermsFilter filter = new TermsFilter(Field.ASSET_CATEGORY_IDS);
                    filter.addValues(ArrayUtil.toStringArray(assetCategoryIds));
                    bf.add(filter, BooleanClauseOccur.SHOULD);

                    if (Validator.isNull(ddmStructureKey)) {
                        bf.addTerm(DDM_STRUCTURE_KEY_ATTR, _configuration.allActuDDMStructureKey());
                    }
                    booleanFilter.add(bf, BooleanClauseOccur.MUST);
                });
            }
        } catch (PortalException e) {
            if (_log.isErrorEnabled()) {
                _log.error(e.getLocalizedMessage());
            }
        }
    }

    @Reference
    private UserLocalService _userLocalService;

    @Reference
    private AssetCategoryLocalService _assetCategoryLocalService;

    @Reference
    private AssetVocabularyLocalService _assetVocabularyLocalService;

    private static final Log _log = LogFactoryUtil.getLog(UserCategoryPreFilterContributor.class);
}