/*
 *   Copyright (C) Christian Schulte, 2005-206
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
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Writes a projects' main resource files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @phase process-resources
 * @goal write-main-resources
 * @threadSafe
 * @requiresDependencyResolution test
 */
public final class MainResourcesWriteMojo extends AbstractResourcesWriteMojo
{

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @parameter default-value="once-per-session" expression="${jomc.writeMainResourcesExecutionStrategy}"
     * @since 1.1
     */
    private String writeMainResourcesExecutionStrategy;

    /**
     * Directory to write resource files to.
     *
     * @parameter default-value="${project.build.directory}/generated-resources/jomc"
     *            expression="${jomc.mainResourcesOutputDirectory}"
     * @since 1.2
     */
    private File mainResourcesOutputDirectory;

    /** Creates a new {@code MainResourcesWriteMojo} instance. */
    public MainResourcesWriteMojo()
    {
        super();
    }

    @Override
    protected String getResourcesModuleName() throws MojoExecutionException
    {
        return this.getModuleName();
    }

    @Override
    protected ClassLoader getResourcesClassLoader() throws MojoExecutionException
    {
        return this.getMainClassLoader();
    }

    @Override
    protected File getResourcesDirectory() throws MojoExecutionException
    {
        return this.mainResourcesOutputDirectory;
    }

    @Override
    protected String getGoal() throws MojoExecutionException
    {
        return "write-main-resources";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.writeMainResourcesExecutionStrategy;
    }

    @Override
    protected File getResourcesOutputDirectory() throws MojoExecutionException
    {
        return this.getOutputDirectory();
    }

    @Override
    protected void addMavenResource( final MavenProject mavenProject, final Resource resource )
        throws MojoExecutionException
    {
        mavenProject.addResource( resource );
    }

}
