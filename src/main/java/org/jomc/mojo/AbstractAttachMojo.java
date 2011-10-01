/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
 *   $JOMC$
 *
 */
package org.jomc.mojo;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * Base class for attaching artifacts to a project.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public abstract class AbstractAttachMojo extends AbstractMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "MavenProjectHelper";

    /** Prefix prepended to log messages. */
    private static final String LOG_PREFIX = "[JOMC] ";

    /**
     * The Maven project of the instance.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * Maven ProjectHelper.
     *
     * @component
     * @required
     * @readonly
     */
    private MavenProjectHelper mavenProjectHelper;

    /**
     * The Maven session of the instance.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     * @since 1.1
     */
    private MavenSession mavenSession;

    /**
     * Directory holding the session related files of the project.
     *
     * @parameter default-value="${project.build.directory}/jomc-sessions" expression="${jomc.sessionDirectory}"
     * @since 1.1
     */
    private String sessionDirectory;

    /**
     * Controls verbosity of the plugin.
     *
     * @parameter expression="${jomc.verbose}" default-value="false"
     * @since 1.1
     */
    private boolean verbose;

    /** Creates a new {@code AbstractAttachMojo} instance. */
    public AbstractAttachMojo()
    {
        super();
    }

    /**
     * Gets the Maven project of the instance.
     *
     * @return The Maven project of the instance.
     *
     * @throws MojoExecutionException if getting the Maven project of the instance fails.
     *
     * @since 1.1
     */
    protected MavenProject getMavenProject() throws MojoExecutionException
    {
        return this.mavenProject;
    }

    /**
     * Gets the Maven session of the instance.
     *
     * @return The Maven session of the instance.
     *
     * @throws MojoExecutionException if getting the Maven session of the instance fails.
     *
     * @since 1.1
     */
    protected MavenSession getMavenSession() throws MojoExecutionException
    {
        return this.mavenSession;
    }

    /**
     * Gets the Maven project helper of the instance.
     *
     * @return The Maven project helper of the instance.
     *
     * @throws MojoExecutionException if getting the Maven project helper of the instance fails.
     *
     * @since 1.1
     */
    protected MavenProjectHelper getMavenProjectHelper() throws MojoExecutionException
    {
        return this.mavenProjectHelper;
    }

    /**
     * Gets the directory holding the session related files of the project.
     *
     * @return The directory holding the session related files of the project.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.1
     */
    protected File getSessionDirectory() throws MojoExecutionException
    {
        File directory = new File( this.sessionDirectory );

        if ( !directory.isAbsolute() )
        {
            directory = new File( this.getMavenProject().getBasedir(), this.sessionDirectory );
        }

        return directory;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @return {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if getting the flag fails.
     *
     * @since 1.1
     */
    protected final boolean isVerbose() throws MojoExecutionException
    {
        return this.verbose;
    }

    /**
     * Gets a flag indicating verbose output is enabled.
     *
     * @param value {@code true} if verbose output is enabled; {@code false} if information messages are suppressed.
     *
     * @throws MojoExecutionException if setting the flag fails.
     *
     * @since 1.1
     */
    protected final void setVerbose( final boolean value ) throws MojoExecutionException
    {
        this.verbose = value;
    }

    /**
     * Gets the file of the artifact to attach.
     *
     * @return The file of the artifact to attach.
     */
    protected abstract File getArtifactFile();

    /**
     * Gets the classifier of the artifact to attach.
     *
     * @return The classifier of the artifact to attach.
     */
    protected abstract String getArtifactClassifier();

    /**
     * Gets the type of the artifact to attach.
     *
     * @return The type of the artifact to attach.
     */
    protected abstract String getArtifactType();

    /**
     * Gets the execution strategy of the instance.
     *
     * @return The execution strategy of the instance.
     *
     * @since 1.1
     */
    protected abstract String getExecutionStrategy();

    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        final File attachment =
            new File( this.getSessionDirectory(),
                      ArtifactUtils.versionlessKey( this.getMavenProject().getArtifact() ).hashCode()
                      + "-" + this.getArtifactClassifier()
                      + "-" + this.getMavenSession().getStartTime().getTime()
                      + "." + this.getArtifactType() );

        try
        {
            if ( this.isVerbose() && this.getLog().isInfoEnabled() )
            {
                this.getLog().info( LOG_PREFIX + Messages.getMessage( "separator" ) );
                this.getLog().info( LOG_PREFIX + Messages.getMessage( "title" ) );
            }

            if ( MojoDescriptor.MULTI_PASS_EXEC_STRATEGY.equals( this.getExecutionStrategy() )
                 || !attachment.exists() )
            {
                if ( this.isVerbose() && this.getLog().isInfoEnabled() )
                {
                    this.getLog().info( LOG_PREFIX + Messages.getMessage( "separator" ) );
                    this.getLog().info( LOG_PREFIX + Messages.getMessage(
                        "processingProject", TOOLNAME, this.getMavenProject().getName() == null
                                                       ? this.getMavenProject().getArtifactId()
                                                       : this.getMavenProject().getName() ) );

                }

                if ( this.getArtifactFile().isFile() )
                {
                    if ( attachment.exists() && !attachment.delete() )
                    {
                        this.getLog().warn( LOG_PREFIX + Messages.getMessage(
                            "failedDeletingFile", attachment.getAbsolutePath() ) );

                    }
                    if ( !attachment.getParentFile().exists() && !attachment.getParentFile().mkdirs() )
                    {
                        throw new MojoExecutionException( Messages.getMessage(
                            "failedCreatingDirectory", attachment.getParentFile().getAbsolutePath() ) );

                    }

                    FileUtils.copyFile( this.getArtifactFile(), attachment );
                    this.getMavenProjectHelper().attachArtifact( this.getMavenProject(), this.getArtifactType(),
                                                                 this.getArtifactClassifier(), attachment );

                    if ( this.isVerbose() && this.getLog().isInfoEnabled() )
                    {
                        this.getLog().info( LOG_PREFIX + Messages.getMessage(
                            "creatingAttachment", this.getArtifactFile().getAbsolutePath(),
                            this.getArtifactClassifier(), this.getArtifactType() ) );

                        this.getLog().info( LOG_PREFIX + Messages.getMessage( "toolSuccess", TOOLNAME ) );
                    }
                }
                else if ( this.getLog().isWarnEnabled() )
                {
                    this.getLog().warn( LOG_PREFIX + Messages.getMessage(
                        "artifactFileNotFound", this.getArtifactFile().getAbsolutePath() ) );

                }
            }
            else if ( this.isVerbose() && this.getLog().isInfoEnabled() )
            {
                this.getLog().info( LOG_PREFIX + Messages.getMessage( "executionSuppressed",
                                                                      this.getExecutionStrategy() ) );

            }
        }
        catch ( final IOException e )
        {
            final String message = Messages.getMessage( e );
            throw new MojoExecutionException( Messages.getMessage(
                "failedCopying", this.getArtifactFile().getAbsolutePath(), attachment.getAbsolutePath(),
                message != null ? message : "" ), e );

        }
        finally
        {
            if ( this.isVerbose() && this.getLog().isInfoEnabled() )
            {
                this.getLog().info( LOG_PREFIX + Messages.getMessage( "separator" ) );
            }
        }
    }

}
