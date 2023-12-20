package com.liferay.samples.user.category.based.search.query.filter.api;

import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.query.contributor.QueryPreFilterContributor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author obouchama
 */
@Component(immediate = true, service = QueryPreFilterContributor.class)
public class UserCategoryQueryPreFilterContributor implements QueryPreFilterContributor {

    @Override
    public void contribute(BooleanFilter booleanFilter, SearchContext searchContext) {

        long userId = searchContext.getUserId();
        AssetEntry entry = _assetEntryLocalService.fetchEntry(User.class.getName(), userId);
        long[] assetCategoryIds = _assetEntryAssetCategoryRelLocalService.getAssetCategoryPrimaryKeys(entry.getEntryId());

        if(ArrayUtil.isNotEmpty(assetCategoryIds)) {
            TermsFilter filter = new TermsFilter(Field.ASSET_CATEGORY_IDS);
            filter.addValues(ArrayUtil.toStringArray(assetCategoryIds));
            booleanFilter.add(filter, BooleanClauseOccur.MUST);
        }
    }

    @Reference
    private AssetEntryLocalService _assetEntryLocalService;

    @Reference
    private AssetEntryAssetCategoryRelLocalService _assetEntryAssetCategoryRelLocalService;
}