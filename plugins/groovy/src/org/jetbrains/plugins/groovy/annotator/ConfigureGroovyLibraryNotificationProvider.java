/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package org.jetbrains.plugins.groovy.annotator;

import com.intellij.ProjectTopics;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.jetbrains.plugins.groovy.GroovyFileType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maxim.Medvedev
 */
public class ConfigureGroovyLibraryNotificationProvider implements EditorNotifications.Provider<EditorNotificationPanel> {
  private static final Key<EditorNotificationPanel> KEY = Key.create("configure.groovy.library");

  private final Project myProject;

  private final Set<FileType> supportedFileTypes;

  public ConfigureGroovyLibraryNotificationProvider(Project project, final EditorNotifications notifications) {
    myProject = project;
    project.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
      @Override
      public void beforeRootsChange(ModuleRootEvent event) {}

      @Override
      public void rootsChanged(ModuleRootEvent event) {
        notifications.updateAllNotifications();
      }
    });

    supportedFileTypes = new HashSet<FileType>();
    supportedFileTypes.add(GroovyFileType.GROOVY_FILE_TYPE);

    for (GroovyFrameworkConfigNotification configNotification : GroovyFrameworkConfigNotification.EP_NAME.getExtensions()) {
      Collections.addAll(supportedFileTypes, configNotification.getFrameworkFileTypes());
    }
  }

  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Override
  public EditorNotificationPanel createNotificationPanel(VirtualFile file) {
    if (!supportedFileTypes.contains(file.getFileType())) return null;

    final Module module = ModuleUtil.findModuleForFile(file, myProject);
    if (module == null) return null;

    for (GroovyFrameworkConfigNotification configNotification : GroovyFrameworkConfigNotification.EP_NAME.getExtensions()) {
      if (configNotification.hasFrameworkStructure(module)) {
        if (!configNotification.hasFrameworkLibrary(module)) {
          return configNotification.createConfigureNotificationPanel(module);
        }
        return null;
      }
    }

    return null;
  }

}
