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
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ResourceFileProcessor;

/**
 * Base class for writing resource files.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public abstract class AbstractResourcesWriteMojo extends AbstractJomcMojo
{

    /**
     * Constant for the name of the tool backing the class.
     */
    private static final String TOOLNAME = "ResourceFileProcessor";

    /**
     * The language of the default language properties file of generated resource bundle properties resources.
     *
     * @parameter expression="${jomc.resourceBundleDefaultLanguage}"
     */
    private String resourceBundleDefaultLanguage;

    /**
     * Creates a new {@code AbstractResourcesWriteMojo} instance.
     */
    public AbstractResourcesWriteMojo()
    {
        super();
    }

    @Override
    protected final void executeTool() throws Exception
    {
        this.logSeparator();

        if ( this.isResourceProcessingEnabled() )
        {
            this.logProcessingModule( TOOLNAME, this.getResourcesModuleName() );

            final ModelContext context = this.createModelContext( this.getResourcesClassLoader() );
            final ResourceFileProcessor tool = this.createResourceFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            final ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            if ( this.resourceBundleDefaultLanguage != null )
            {
                tool.setResourceBundleDefaultLocale(
                    new Locale( this.resourceBundleDefaultLanguage.toLowerCase( Locale.ENGLISH ) ) );

            }

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                final Module module =
                    tool.getModules() != null ? tool.getModules().getModule( this.getResourcesModuleName() ) : null;

                if ( module != null )
                {
                    if ( !this.getResourcesDirectory().exists() && !this.getResourcesDirectory().mkdirs() )
                    {
                        throw new MojoExecutionException( Messages.getMessage(
                            "failedCreatingDirectory", this.getResourcesDirectory().getAbsolutePath() ) );

                    }

                    tool.writeResourceBundleResourceFiles( module, this.getResourcesDirectory() );

                    if ( !this.getResourcesDirectory().equals( this.getResourcesOutputDirectory() ) )
                    {
                        FileUtils.copyDirectory( this.getResourcesDirectory(), this.getResourcesOutputDirectory() );
                    }

                    final Resource resource = new Resource();
                    resource.setDirectory( this.getResourcesDirectory().getAbsolutePath() );
                    resource.setFiltering( false );

                    this.addMavenResource( this.getMavenProject(), resource );

                    this.logToolSuccess( TOOLNAME );
                }
                else
                {
                    this.logMissingModule( this.getResourcesModuleName() );
                }
            }
            else
            {
                throw new MojoExecutionException( Messages.getMessage( "resourceProcessingFailure" ) );
            }
        }
        else if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, Messages.getMessage( "resourceProcessingDisabled" ), null );
        }
    }

    /**
     * Gets the name of the module to write resource files of.
     *
     * @return The name of the module to write resource files of.
     *
     * @throws MojoExecutionException if getting the name fails.
     */
    protected abstract String getResourcesModuleName() throws MojoExecutionException;

    /**
     * Gets the class loader to use for writing resource files.
     *
     * @return The class loader to use for writing resource files.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getResourcesClassLoader() throws MojoExecutionException;

    /**
     * Gets the directory to write the resource files to.
     *
     * @return The directory to write the resource files to.
     *
     * @throws MojoExecutionException if getting the directory fails.
     */
    protected abstract File getResourcesDirectory() throws MojoExecutionException;

    /**
     * Gets the directory to copy resource files to.
     *
     * @return The directory to copy resource files to.
     *
     * @throws MojoExecutionException if getting the directory fails.
     *
     * @since 1.2
     */
    protected abstract File getResourcesOutputDirectory() throws MojoExecutionException;

    /**
     * Adds a resource to a {@code MavenProjecŧ}.
     *
     * @param mavenProject The {@code MavenProject} to add a resource to.
     * @param resource The {@code Resource} to add.
     *
     * @throws MojoExecutionException if adding the resource fails.
     *
     * @since 1.2
     */
    protected abstract void addMavenResource( MavenProject mavenProject, Resource resource )
        throws MojoExecutionException;

}
