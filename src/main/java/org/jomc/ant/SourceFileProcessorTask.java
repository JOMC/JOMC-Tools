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
import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.SourceFileProcessor;

/**
 * Base class for executing source file processor based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class SourceFileProcessorTask extends JomcToolTask
{

    /** Controls processing of source files. */
    private boolean sourceProcessingEnabled = true;

    /** Class of the {@code SourceFileProcessor} backing the task. */
    private Class<? extends SourceFileProcessor> sourceFileProcessorClass;

    /** Creates a new {@code SourceFileProcessorTask} instance. */
    public SourceFileProcessorTask()
    {
        super();
    }

    /**
     * Gets a flag indicating the processing of source files is enabled.
     *
     * @return {@code true} if processing of source files is enabled; {@code false} else.
     *
     * @see #setSourceProcessingEnabled(boolean)
     */
    public final boolean isSourceProcessingEnabled()
    {
        return this.sourceProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of source files is enabled.
     *
     * @param value {@code true} to enable processing of source files; {@code false} to disable processing of source
     * files.
     *
     * @see #isSourceProcessingEnabled()
     */
    public final void setSourceProcessingEnabled( final boolean value )
    {
        this.sourceProcessingEnabled = value;
    }

    /**
     * Gets the class of the {@code SourceFileProcessor} backing the task.
     *
     * @return The class of the {@code SourceFileProcessor} backing the task.
     *
     * @see #setSourceFileProcessorClass(java.lang.Class)
     */
    public final Class<? extends SourceFileProcessor> getSourceFileProcessorClass()
    {
        if ( this.sourceFileProcessorClass == null )
        {
            this.sourceFileProcessorClass = SourceFileProcessor.class;
        }

        return this.sourceFileProcessorClass;
    }

    /**
     * Sets the class of the {@code SourceFileProcessor} backing the task.
     *
     * @param value The new class of the {@code SourceFileProcessor} backing the task or {@code null}.
     *
     * @see #getSourceFileProcessorClass()
     */
    public final void setSourceFileProcessorClass( final Class<? extends SourceFileProcessor> value )
    {
        this.sourceFileProcessorClass = value;
    }

    /**
     * Creates a new {@code SourceFileProcessor} instance setup using the properties of the instance.
     *
     * @return A new {@code SourceFileProcessor} instance.
     *
     * @throws BuildException if creating a new {@code SourceFileProcessor} instance fails.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     * @see #getSourceFileProcessorClass()
     */
    public SourceFileProcessor newSourceFileProcessor() throws BuildException
    {
        try
        {
            final SourceFileProcessor tool = this.getSourceFileProcessorClass().newInstance();
            this.configureJomcTool( tool );
            return tool;
        }
        catch ( final InstantiationException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final IllegalAccessException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * Calls the {@code processSourceFiles} method if source processing is enabled.
     *
     * @throws BuildException if processing source files fails.
     *
     * @see #processSourceFiles()
     */
    @Override
    public final void executeTask() throws BuildException
    {
        if ( this.isSourceProcessingEnabled() )
        {
            this.processSourceFiles();
            this.log( getMessage( "sourceProcessingSuccess" ) );
        }
        else
        {
            this.log( getMessage( "sourceProcessingDisabled" ) );
        }
    }

    /**
     * Processes source files.
     *
     * @throws BuildException if processing source files fails.
     *
     * @see #executeTask()
     */
    public void processSourceFiles() throws BuildException
    {
        this.log( getMessage( "unimplementedTask", this.getClass().getName() ), Project.MSG_ERR );
    }

    /** {@inheritDoc} */
    @Override
    public SourceFileProcessorTask clone()
    {
        return (SourceFileProcessorTask) super.clone();
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            SourceFileProcessorTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
