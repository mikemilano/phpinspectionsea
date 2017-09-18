package com.kalessil.phpStorm.phpInspectionsEA.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.PhpTypedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.kalessil.phpStorm.phpInspectionsEA.EASettings;
import com.kalessil.phpStorm.phpInspectionsEA.utils.analytics.AnalyticsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final public class OpenapiResolveUtil {
    @Nullable
    static public PsiElement resolveReference(@NotNull PsiReference reference) {
        try {
            return reference.resolve();
        } catch (Throwable error) {
            AnalyticsUtil.registerPreventedException(EASettings.getInstance().getUuid(), error);
            return null;
        }
    }

    @Nullable
    static public PhpType resolveType(@NotNull PhpTypedElement expression, @NotNull Project project) {
        try {
            return expression.getType().global(project);
        } catch (Throwable error) {
            AnalyticsUtil.registerPreventedException(EASettings.getInstance().getUuid(), error);
            return null;
        }

    }
}
