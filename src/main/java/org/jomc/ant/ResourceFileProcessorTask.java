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
package org.jomc.ant;

import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.ResourceFileProcessor;

/**
 * Base class for executing resource file processor based tasks.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @see #processResourceFiles()
 */
public class ResourceFileProcessorTask extends JomcToolTask
{

    /**
     * The language of the default language properties file of generated resource bundle resources.
     */
    private String resourceBundleDefaultLanguage;

    /**
     * Controls processing of resource files.
     */
    private boolean resourceProcessingEnabled = true;

    /**
     * Class of the {@code ResourceFileProcessor} backing the task.
     */
    private Class<? extends ResourceFileProcessor> resourceFileProcessorClass;

    /**
     * Creates a new {@code ResourceFileProcessorTask} instance.
     */
    public ResourceFileProcessorTask()
    {
        super();
    }

    /**
     * Gets a flag indicating the processing of resources is enabled.
     *
     * @return {@code true}, if processing of resources is enabled; {@code false}, else.
     *
     * @see #setResourceProcessingEnabled(boolean)
     */
    public final boolean isResourceProcessingEnabled()
    {
        return this.resourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of resources is enabled.
     *
     * @param value {@code true}, to enable processing of resources; {@code false}, to disable processing of resources.
     *
     * @see #isResourceProcessingEnabled()
     */
    public final void setResourceProcessingEnabled( final boolean value )
    {
        this.resourceProcessingEnabled = value;
    }

    /**
     * Gets the language of the default language properties file of generated resource bundle resource files.
     *
     * @return The language of the default language properties file of generated resource bundle resource files or
     * {@code null}.
     *
     * @see #setResourceBundleDefaultLanguage(java.lang.String)
     */
    public final String getResourceBundleDefaultLanguage()
    {
        return this.resourceBundleDefaultLanguage;
    }

    /**
     * Sets the language of the default language properties file of generated resource bundle resource files.
     *
     * @param value The new language of the default language properties file of generated resource bundle resource files
     * or {@code null}.
     *
     * @see #getResourceBundleDefaultLanguage()
     */
    public final void setResourceBundleDefaultLanguage( final String value )
    {
        this.resourceBundleDefaultLanguage = value;
    }

    /**
     * Gets the class of the {@code ResourceFileProcessor} backing the task.
     *
     * @return The class of the {@code ResourceFileProcessor} backing the task.
     *
     * @see #setResourceFileProcessorClass(java.lang.Class)
     */
    public final Class<? extends ResourceFileProcessor> getResourceFileProcessorClass()
    {
        if ( this.resourceFileProcessorClass == null )
        {
            this.resourceFileProcessorClass = ResourceFileProcessor.class;
        }

        return this.resourceFileProcessorClass;
    }

    /**
     * Sets the class of the {@code ResourceFileProcessor} backing the task.
     *
     * @param value The new class of the {@code ResourceFileProcessor} backing the task or {@code null}.
     *
     * @see #getResourceFileProcessorClass()
     */
    public final void setResourceFileProcessorClass( final Class<? extends ResourceFileProcessor> value )
    {
        this.resourceFileProcessorClass = value;
    }

    /**
     * Creates a new {@code ResourceFileProcessor} instance setup using the properties of the instance.
     *
     * @return A new {@code ResourceFileProcessor} instance.
     *
     * @throws BuildException if creating a new {@code ResourceFileProcessor} instance fails.
     *
     * @see #getResourceFileProcessorClass()
     * @see #configureResourceFileProcessor(org.jomc.tools.ResourceFileProcessor)
     */
    public ResourceFileProcessor newResourceFileProcessor() throws BuildException
    {
        try
        {
            final ResourceFileProcessor resourceFileProcessor = this.getResourceFileProcessorClass().newInstance();
            this.configureResourceFileProcessor( resourceFileProcessor );
            return resourceFileProcessor;
        }
        catch ( final InstantiationException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getResourceFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
        catch ( final IllegalAccessException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getResourceFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
    }

    /**
     * Configures a given {@code ResourceFileProcessor} instance using the properties of the instance.
     *
     * @param resourceFileProcessor The resource file processor to configure.
     *
     * @throws NullPointerException if {@code resourceFileProcessor} is {@code null}.
     * @throws BuildException if configuring {@code resourceFileProcessor} fails.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     */
    public void configureResourceFileProcessor( final ResourceFileProcessor resourceFileProcessor )
        throws BuildException
    {
        if ( resourceFileProcessor == null )
        {
            throw new NullPointerException( "resourceFileProcessor" );
        }

        this.configureJomcTool( resourceFileProcessor );

        if ( this.getResourceBundleDefaultLanguage() != null )
        {
            resourceFileProcessor.setResourceBundleDefaultLocale(
                new Locale( this.getResourceBundleDefaultLanguage() ) );

        }
    }

    /**
     * Calls the {@code processResourceFiles} method if resource processing is enabled.
     *
     * @throws BuildException if processing resource files fails.
     *
     * @see #processResourceFiles()
     */
    @Override
    public final void executeTask() throws BuildException
    {
        if ( this.isResourceProcessingEnabled() )
        {
            this.processResourceFiles();
            this.log( Messages.getMessage( "resourceProcessingSuccess" ) );
        }
        else
        {
            this.log( Messages.getMessage( "resourceProcessingDisabled" ) );
        }
    }

    /**
     * Processes resource files.
     *
     * @throws BuildException if processing resource files fails.
     *
     * @see #executeTask()
     */
    public void processResourceFiles() throws BuildException
    {
        this.log( Messages.getMessage( "unimplementedTask", this.getClass().getName(), "processResourceFiles" ),
                  Project.MSG_ERR );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceFileProcessorTask clone()
    {
        return (ResourceFileProcessorTask) super.clone();
    }

}
