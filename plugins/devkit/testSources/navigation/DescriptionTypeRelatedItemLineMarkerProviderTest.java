/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.devkit.navigation;

import com.intellij.codeInsight.daemon.GutterMark;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.util.PathUtil;

import java.util.Collection;
import java.util.Iterator;

@TestDataPath("$CONTENT_ROOT/testData/navigation/descriptionType")
public class DescriptionTypeRelatedItemLineMarkerProviderTest extends JavaCodeInsightFixtureTestCase {

  @Override
  protected String getBasePath() {
    return PluginPathManager.getPluginHomePathRelative("devkit") + "/testData/navigation/descriptionType";
  }

  @Override
  protected void tuneFixture(JavaModuleFixtureBuilder moduleBuilder) throws Exception {
    String pathForClass = PathUtil.getJarPathForClass(LocalInspectionEP.class);
    moduleBuilder.addLibrary("lang-api", pathForClass);
  }

  public void testInspectionDescription() {
    myFixture.copyDirectoryToProject("inspectionDescriptions", "inspectionDescriptions");

    final GutterMark gutter = myFixture.findGutter("MyWithDescriptionInspection.java");
    DevKitGutterTargetsChecker.checkGutterTargets(gutter, "Description", AllIcons.FileTypes.Html, "MyWithDescription.html");
  }

  public void testIntentionDescription() {
    myFixture.copyDirectoryToProject("intentionDescriptions", "intentionDescriptions");

    final Collection<GutterMark> gutters = myFixture.findAllGutters("MyIntentionActionWithDescription.java");
    assertSize(2, gutters);
    final Iterator<GutterMark> it = gutters.iterator();
    DevKitGutterTargetsChecker.checkGutterTargets(it.next(), "Description", AllIcons.FileTypes.Html, "description.html");
    DevKitGutterTargetsChecker.checkGutterTargets(it.next(), "Before/After Templates", AllIcons.Actions.Diff,
                                                  "after.java.template", "before.java.template");
  }
}