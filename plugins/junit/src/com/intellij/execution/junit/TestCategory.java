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
package com.intellij.execution.junit;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

/**
* User: anna
* Date: 4/21/11
*/
class TestCategory extends TestPackage {
  private final Project myProject;

  public TestCategory(Project project,
                      JUnitConfiguration configuration,
                      ExecutionEnvironment environment) {
    super(project, configuration, environment);
    myProject = project;
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    JavaParametersUtil.checkAlternativeJRE(myConfiguration);
    ProgramParametersUtil.checkWorkingDirectoryExist(myConfiguration, myConfiguration.getProject(), myConfiguration.getConfigurationModule().getModule());
    final String category = myConfiguration.getPersistentData().getCategory();
    if (category == null || category.isEmpty()) {
      throw new RuntimeConfigurationError("Category is not specified");
    }
    final JavaRunConfigurationModule configurationModule = myConfiguration.getConfigurationModule();
    if (getSourceScope() == null) {
      configurationModule.checkForWarning();
    }
    configurationModule.findNotNullClass(category);
  }

  @Override
  protected PsiPackage getPackage(JUnitConfiguration.Data data) throws CantRunException {
    return JavaPsiFacade.getInstance(myProject).findPackage("");
  }

  @Override
  public boolean isConfiguredByElement(JUnitConfiguration configuration,
                                       PsiClass testClass,
                                       PsiMethod testMethod,
                                       PsiPackage testPackage, 
                                       PsiDirectory testDir) {
    return false;
  }
}
