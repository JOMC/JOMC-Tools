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
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Commits model objects to a projects' test classes.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @phase process-test-classes
 * @goal commit-test-classes
 * @requiresDependencyResolution test
 */
public final class TestClassesCommitMojo extends AbstractClassesCommitMojo
{

    /** Creates a new {@code TestClassesCommitMojo} instance. */
    public TestClassesCommitMojo()
    {
        super();
    }

    @Override
    protected String getClassesModuleName() throws MojoExecutionException
    {
        return this.getJomcTestModuleName();
    }

    @Override
    protected ClassLoader getClassesClassLoader() throws MojoExecutionException
    {
        return this.getTestClassLoader();
    }

    @Override
    protected File getClassesDirectory() throws MojoExecutionException
    {
        File classesDirectory = new File( this.getMavenProject().getBuild().getTestOutputDirectory() );

        if ( !classesDirectory.isAbsolute() )
        {
            classesDirectory = new File( this.getMavenProject().getBasedir(),
                                         this.getMavenProject().getBuild().getTestOutputDirectory() );

        }

        return classesDirectory;
    }

}
