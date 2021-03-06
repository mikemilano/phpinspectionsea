package com.kalessil.phpStorm.phpInspectionsEA.codeStyle;

import com.kalessil.phpStorm.phpInspectionsEA.PhpCodeInsightFixtureTestCase;
import com.kalessil.phpStorm.phpInspectionsEA.inspectors.codeStyle.SenselessCommaInArrayDefinitionInspector;

public final class SenselessCommaInArrayDefinitionInspectorTest extends PhpCodeInsightFixtureTestCase {
    public void testIfFindsAllPatterns() {
        myFixture.enableInspections(new SenselessCommaInArrayDefinitionInspector());
        myFixture.configureByFile("fixtures/codeStyle/last-comma-in-array.php");
        myFixture.testHighlighting(true, false, true);

        myFixture.getAllQuickFixes().forEach(fix -> myFixture.launchAction(fix));
        myFixture.setTestDataPath(".");
        myFixture.checkResultByFile("fixtures/codeStyle/last-comma-in-array.fixed.php");
    }
}

