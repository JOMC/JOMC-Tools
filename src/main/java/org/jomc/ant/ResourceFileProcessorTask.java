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
package org.jomc.ant;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.ResourceFileProcessor;

/**
 * Base class for executing resource file processor based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ResourceFileProcessorTask extends JomcToolTask
{

    /** The language of the default language properties file of generated resource bundle resources. */
    private String resourceBundleDefaultLanguage;

    /** Controls processing of resource files. */
    private boolean resourceProcessingEnabled = true;

    /** Creates a new {@code ResourceFileProcessorTask} instance. */
    public ResourceFileProcessorTask()
    {
        super();
    }

    /**
     * Gets a flag indicating the processing of resources is enabled.
     *
     * @return {@code true} if processing of resources is enabled; {@code false} else.
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
     * @param value {@code true} to enable processing of resources; {@code false} to disable processing of resources.
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
     * @param value The language of the default language properties file of generated resource bundle resource files or
     * {@code null}.
     *
     * @see #getResourceBundleDefaultLanguage()
     */
    public final void setResourceBundleDefaultLanguage( final String value )
    {
        this.resourceBundleDefaultLanguage = value;
    }

    /**
     * Creates a new {@code ResourceFileProcessor} instance setup using the properties of the instance.
     *
     * @return A new {@code ResourceFileProcessor} instance.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     */
    public ResourceFileProcessor newResourceFileProcessor()
    {
        final ResourceFileProcessor tool = new ResourceFileProcessor();
        this.configureJomcTool( tool );

        if ( this.getResourceBundleDefaultLanguage() != null )
        {
            tool.setResourceBundleDefaultLocale( new Locale( this.getResourceBundleDefaultLanguage() ) );
        }
        else
        {
            tool.setResourceBundleDefaultLocale( null );
        }

        return tool;
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
            this.log( getMessage( "resourceProcessingSuccess" ) );
        }
        else
        {
            this.log( getMessage( "resourceProcessingDisabled" ) );
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
        this.log( getMessage( "unimplementedTask", this.getClass().getName() ), Project.MSG_ERR );
    }

    /** {@inheritDoc} */
    @Override
    public ResourceFileProcessorTask clone()
    {
        return (ResourceFileProcessorTask) super.clone();
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ResourceFileProcessorTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
