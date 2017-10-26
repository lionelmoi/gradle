/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.dependencylock;

import org.gradle.BuildResult;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.internal.dependencylock.io.writer.DependencyLockWriter;
import org.gradle.internal.dependencylock.io.writer.JsonDependencyLockWriter;
import org.gradle.internal.dependencylock.model.DependencyLock;

import java.io.File;

public class DefaultDependencyLockManager implements DependencyLockManager {

    private final DependencyLockCreator dependencyLockCreator;

    public DefaultDependencyLockManager(DependencyLockCreator dependencyLockCreator) {
        this.dependencyLockCreator = dependencyLockCreator;
    }

    @Override
    public void initiate(Project rootProject) {
        final File lockFile = getLockFile(rootProject);
        final DependencyLock dependencyLock = dependencyLockCreator.create(rootProject.getAllprojects());

        rootProject.getGradle().buildFinished(new Action<BuildResult>() {
            @Override
            public void execute(BuildResult buildResult) {
                DependencyLockWriter dependencyLockWriter = new JsonDependencyLockWriter(lockFile);
                dependencyLockWriter.write(dependencyLock);
            }
        });
    }

    private File getLockFile(Project project) {
        File lockDir = project.file("gradle");
        return new File(lockDir, "dependencies.lock");
    }
}
