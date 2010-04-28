/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
 *   THIS SOFTWARE IS PROVIDED BY THE JOMC PROJECT AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JOMC PROJECT OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $Id$
 *
 */
package org.jomc.mojo;

import java.io.File;

/**
 * Attaches a project's test module artifact.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-test-resources
 * @goal attach-test-module
 */
public final class TestModuleAttachMojo extends AbstractAttachMojo
{

    /**
     * File of the attached module artifact.
     *
     * @parameter expression="${jomc.testModuleArtifactFile}"
     *            default-value="${project.build.testOutputDirectory}/META-INF/jomc.xml"
     */
    private File testModuleArtifactFile;

    /**
     * Classifier of the attached module artifact.
     *
     * @parameter expression="${jomc.testModuleArtifactClassifier}" default-value="jomc-test-module"
     */
    private String testModuleArtifactClassifier;

    /**
     * Type of the attached module artifact.
     *
     * @parameter expression="${jomc.testModuleArtifactType}" default-value="xml"
     */
    private String testModuleArtifactType;

    protected File getArtifactFile()
    {
        return this.testModuleArtifactFile;
    }

    protected String getArtifactClassifier()
    {
        return this.testModuleArtifactClassifier;
    }

    protected String getArtifactType()
    {
        return this.testModuleArtifactType;
    }

}
