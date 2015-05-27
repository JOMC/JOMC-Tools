/*
 *   Copyright (C) Christian Schulte <cs@schulte.it>, 2005-206
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *     o Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     o Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.mojo;

import java.io.File;

/**
 * Attaches a project's main module artifact.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @phase process-resources
 * @goal attach-main-module
 */
public final class MainModuleAttachMojo extends AbstractAttachMojo
{

    /**
     * File of the attached module artifact.
     *
     * @parameter expression="${jomc.mainModuleArtifactFile}"
     * default-value="${project.build.outputDirectory}/META-INF/jomc.xml"
     */
    private File mainModuleArtifactFile;

    /**
     * Classifier of the attached module artifact.
     *
     * @parameter expression="${jomc.mainModuleArtifactClassifier}" default-value="jomc-module"
     */
    private String mainModuleArtifactClassifier;

    /**
     * Type of the attached module artifact.
     *
     * @parameter expression="${jomc.mainModuleArtifactType}" default-value="xml"
     */
    private String mainModuleArtifactType;

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @parameter default-value="once-per-session" expression="${jomc.attachMainModuleExecutionStrategy}"
     * @since 1.1
     */
    private String attachMainModuleExecutionStrategy;

    /**
     * Creates a new {@code MainModuleAttachMojo} instance.
     */
    public MainModuleAttachMojo()
    {
        super();
    }

    @Override
    protected File getArtifactFile()
    {
        return this.mainModuleArtifactFile;
    }

    @Override
    protected String getArtifactClassifier()
    {
        return this.mainModuleArtifactClassifier;
    }

    @Override
    protected String getArtifactType()
    {
        return this.mainModuleArtifactType;
    }

    @Override
    protected String getExecutionStrategy()
    {
        return this.attachMainModuleExecutionStrategy;
    }

}
