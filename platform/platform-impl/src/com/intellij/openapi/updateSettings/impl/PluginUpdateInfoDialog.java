/*
 * Copyright 2000-2013 JetBrains s.r.o.
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
package com.intellij.openapi.updateSettings.impl;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.plugins.PluginManagerConfigurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.TableUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @author pti
 */
class PluginUpdateInfoDialog extends AbstractUpdateDialog {
  private final Collection<PluginDownloader> myUploadedPlugins;

  public PluginUpdateInfoDialog(Collection<PluginDownloader> uploadedPlugins, boolean enableLink) {
    super(enableLink);
    myUploadedPlugins = uploadedPlugins;
    init();
  }

  protected PluginUpdateInfoDialog(Component parent, @NotNull Collection<PluginDownloader> updatePlugins, boolean enableLink) {
    super(parent, enableLink);
    myUploadedPlugins = updatePlugins;
    init();
  }

  @Override
  protected JComponent createCenterPanel() {
    return new PluginUpdateInfoPanel().myPanel;
  }

  @Override
  protected String getOkButtonText() {
    return IdeBundle.message("update.plugins.update.action");
  }

  @NotNull
  @Override
  protected Action[] createActions() {
    return new Action[]{getOKAction(), getCancelAction()};
  }

  @Override
  protected void doOKAction() {
    if (downloadPlugins() && toRestart()) {
      restart();
    }

    super.doOKAction();
  }

  protected boolean toRestart() {
    return PluginManagerConfigurable.showRestartIDEADialog() == Messages.YES;
  }

  private boolean downloadPlugins() {
    UpdateChecker.saveDisabledToUpdatePlugins();
    return UpdateChecker.install(myUploadedPlugins);
  }

  private class PluginUpdateInfoPanel {
    private JPanel myPanel;
    private JLabel myPluginsToUpdateLabel;
    private JPanel myPluginsPanel;
    private JEditorPane myMessageArea;

    public PluginUpdateInfoPanel() {
      myPluginsToUpdateLabel.setVisible(true);
      myPluginsPanel.setVisible(true);

      final DetectedPluginsPanel foundPluginsPanel = new DetectedPluginsPanel();
      foundPluginsPanel.addAll(myUploadedPlugins);
      TableUtil.ensureSelectionExists(foundPluginsPanel.getEntryTable());
      foundPluginsPanel.addStateListener(new DetectedPluginsPanel.Listener() {
        @Override
        public void stateChanged() {
          boolean allSkipped = foundPluginsPanel.getSkippedPlugins().size() == myUploadedPlugins.size();
          getOKAction().setEnabled(!allSkipped);
        }
      });
      myPluginsPanel.add(foundPluginsPanel, BorderLayout.CENTER);

      configureMessageArea(myMessageArea);
    }
  }
}
