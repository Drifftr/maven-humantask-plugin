/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.maven.humantask.artifact;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.wso2.maven.humantask.artifact.util.FileUtils;

@Mojo(name = "buildHumanTask")
public class HumanTaskMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.basedir}")
	private File path;

	@Parameter(defaultValue = "zip")
	private String type;

	@Parameter(defaultValue = "false")
	private boolean enableArchive;

	@Parameter(defaultValue = "${project}")
	private MavenProject mavenProject;

	/**
	 * Maven ProjectHelper.
	 * 
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	private static final String BPEL_CONTENT_DIR = "htcontent";

	public void execute() throws MojoExecutionException, MojoFailureException {
		File project = path;
		File bpelContentDir = new File(project, BPEL_CONTENT_DIR);
		createZip(bpelContentDir);
	}

	public void createZip(File project) throws MojoExecutionException {
		try {
			String artifactType = getType();
			String artifactName = mavenProject.getArtifactId() + "-" + mavenProject.getVersion() + "." + artifactType;
			getLog().info("Project Name : " + project.getName());
			getLog().info("Artifact Name : " + artifactName);
			getLog().info("path : " + path);
			File archive = FileUtils.createArchive(getLog(), path, project, artifactName);
			if (archive != null && archive.exists()) {
				mavenProject.getArtifact().setFile(archive);
			} else {
				throw new MojoExecutionException(archive + " is null or doesn't exist");
			}
		} catch (Exception e) {
			throw new MojoExecutionException("Error while creating bpel archive", e);
		}

	}

	public String getBPELProjectName(File project) {
		List<File> fileList = FileUtils.getAllFilesPresentInFolder(project);
		String bpelProjectName = project.getName();
		for (File file : fileList) {
			if (!file.isDirectory()) {
				if (file.getName().toLowerCase().endsWith(".ht")) {
					bpelProjectName = file.getParent();
					return bpelProjectName;
				}

			}
		}
		return bpelProjectName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setEnableArchive(boolean enableArchive) {
		this.enableArchive = enableArchive;
	}

	public boolean isEnableArchive() {
		return enableArchive;
	}
}
