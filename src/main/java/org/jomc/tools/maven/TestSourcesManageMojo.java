/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
package org.jomc.tools.maven;

import java.io.File;
import java.util.logging.Level;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Manages a projects' test source files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
@Mojo( name = "manage-test-sources",
       defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES,
       requiresDependencyResolution = ResolutionScope.TEST,
       threadSafe = true )
public final class TestSourcesManageMojo extends AbstractSourcesManageMojo
{

    /**
     * Execution strategy of the goal ({@code always} or {@code once-per-session}).
     *
     * @since 1.1
     */
    @Parameter( name = "manageTestSourcesExecutionStrategy",
                property = "jomc.manageTestSourcesExecutionStrategy",
                defaultValue = "once-per-session" )
    private String manageTestSourcesExecutionStrategy;

    /**
     * Creates a new {@code TestSourcesManageMojo} instance.
     */
    public TestSourcesManageMojo()
    {
        super();
    }

    @Override
    protected String getSourcesModuleName() throws MojoExecutionException
    {
        return this.getTestModuleName();
    }

    @Override
    protected ClassLoader getSourcesClassLoader() throws MojoExecutionException
    {
        return this.getTestClassLoader();
    }

    @Override
    protected File getSourcesDirectory() throws MojoExecutionException
    {
        final File sourcesDirectory = this.getTestSourceDirectory();
        boolean testCompileSourceRoot = false;

        for ( int i = 0, l0 = this.getMavenProject().getTestCompileSourceRoots().size(); i < l0; i++ )
        {
            final String element = (String) this.getMavenProject().getTestCompileSourceRoots().get( i );

            if ( sourcesDirectory.equals( this.getAbsoluteFile( element ) ) )
            {
                testCompileSourceRoot = true;
                break;
            }
        }

        if ( !testCompileSourceRoot )
        {
            if ( !sourcesDirectory.exists() && !sourcesDirectory.mkdirs() )
            {
                throw new MojoExecutionException( Messages.getMessage(
                    "failedCreatingDirectory", sourcesDirectory.getAbsolutePath() ) );

            }

            this.getMavenProject().addTestCompileSourceRoot( sourcesDirectory.getAbsolutePath() );
            this.log( Level.INFO, Messages.getMessage(
                      "addedTestCompileSourceRoot", sourcesDirectory.getAbsolutePath() ), null );

        }

        return sourcesDirectory;
    }

    @Override
    protected String getGoal() throws MojoExecutionException
    {
        return "manage-test-sources";
    }

    @Override
    protected String getExecutionStrategy() throws MojoExecutionException
    {
        return this.manageTestSourcesExecutionStrategy;
    }

}
