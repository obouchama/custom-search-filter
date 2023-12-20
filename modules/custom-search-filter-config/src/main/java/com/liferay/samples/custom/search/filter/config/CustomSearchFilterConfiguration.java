package com.liferay.samples.custom.search.filter.config;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
        scope = ExtendedObjectClassDefinition.Scope.SYSTEM, category = "search"
)
@Meta.OCD(
        id = "com.liferay.samples.custom.search.filter.config.CustomSearchFilterConfiguration"
)
public interface CustomSearchFilterConfiguration {

    @Meta.AD(
            deflt = "pre_filter", required = false, description = "Nom du paramètre Pre Filter à renseigner dans le Custom Filter"
    )
    String preFilterParam();

    @Meta.AD(
            deflt = "38257", required = false, description = "Le ddmStructureKey pour toutes les actualités"
    )
    String allActuDDMStructureKey();
}

