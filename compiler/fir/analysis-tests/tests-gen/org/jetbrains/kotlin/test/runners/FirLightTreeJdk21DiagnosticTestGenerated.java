/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test.runners;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.test.generators.GenerateCompilerTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("compiler/testData/diagnostics/testsWithJdk21")
@TestDataPath("$PROJECT_ROOT")
public class FirLightTreeJdk21DiagnosticTestGenerated extends AbstractFirLightTreeJdk21DiagnosticTest {
    @Test
    public void testAllFilesPresentInTestsWithJdk21() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("compiler/testData/diagnostics/testsWithJdk21"), Pattern.compile("^(.+)\\.kt$"), Pattern.compile("^(.+)\\.(reversed|fir|ll)\\.kts?$"), true);
    }

    @Test
    @TestMetadata("newListMethods.kt")
    public void testNewListMethods() throws Exception {
        runTest("compiler/testData/diagnostics/testsWithJdk21/newListMethods.kt");
    }
}
