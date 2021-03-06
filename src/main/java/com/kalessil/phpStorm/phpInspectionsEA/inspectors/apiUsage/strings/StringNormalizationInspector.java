package com.kalessil.phpStorm.phpInspectionsEA.inspectors.apiUsage.strings;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.kalessil.phpStorm.phpInspectionsEA.fixers.UseSuggestedReplacementFixer;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpInspection;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

public class StringNormalizationInspector extends BasePhpInspection {
    private static final String patternInvertedNesting  = "'%e%' should be used instead.";
    private static final String patternSenselessNesting = "'%i%(...)' makes no sense here.";

    @NotNull
    public String getShortName() {
        return "StringNormalizationInspection";
    }

    private static final Set<String> lengthManipulation    = new HashSet<>();
    private static final Set<String> caseManipulation      = new HashSet<>();
    private static final Set<String> innerCaseManipulation = new HashSet<>();
    static {
        innerCaseManipulation.add("strtolower");
        innerCaseManipulation.add("strtoupper");
        innerCaseManipulation.add("mb_convert_case");
        innerCaseManipulation.add("mb_strtolower");
        innerCaseManipulation.add("mb_strtoupper");

        caseManipulation.addAll(innerCaseManipulation);
        caseManipulation.add("ucfirst");
        caseManipulation.add("lcfirst");
        caseManipulation.add("ucwords");

        lengthManipulation.add("ltrim");
        lengthManipulation.add("rtrim");
        lengthManipulation.add("trim");
        lengthManipulation.add("substr");
        lengthManipulation.add("mb_substr");
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BasePhpElementVisitor() {
            @Override
            public void visitPhpFunctionCall(@NotNull FunctionReference reference) {
                final String functionName = reference.getName();
                if (functionName != null) {
                    final PsiElement[] arguments = reference.getParameters();
                    if (arguments.length > 0 && OpenapiTypesUtil.isFunctionReference(arguments[0])) {
                        final FunctionReference innerCall = (FunctionReference) arguments[0];
                        final String innerCallName        = innerCall.getName();
                        if (innerCallName != null) {
                            final PsiElement[] innerArguments = innerCall.getParameters();
                            if (innerArguments.length > 0) {
                                if (lengthManipulation.contains(functionName) && caseManipulation.contains(innerCallName)) {
                                    final String theString    = innerArguments[0].getText();
                                    final String newInnerCall = reference.getText().replace(arguments[0].getText(), theString);
                                    final String replacement  = innerCall.getText().replace(theString, newInnerCall);
                                    final String message      = patternInvertedNesting.replace("%e%", replacement);
                                    holder.registerProblem(reference, message, new NormalizationFix(replacement));
                                } else if (caseManipulation.contains(functionName) && caseManipulation.contains(innerCallName)) {
                                    if (!functionName.equals(innerCallName) && innerCaseManipulation.contains(innerCallName)) {
                                        return;
                                    }
                                    final String message = patternSenselessNesting.replace("%i%", innerCallName);
                                    holder.registerProblem(innerCall, message, new NormalizationFix(innerArguments[0].getText()));
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private class NormalizationFix extends UseSuggestedReplacementFixer {
        @NotNull
        @Override
        public String getName() {
            return "Fix the string normalization";
        }

        NormalizationFix(@NotNull String expression) {
            super(expression);
        }
    }
}
