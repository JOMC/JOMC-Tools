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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.SourceFileProcessor;

/**
 * Base class for executing source file processor based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 * @see #processSourceFiles()
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
     * @return {@code true}, if processing of source files is enabled; {@code false}, else.
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
     * @param value {@code true}, to enable processing of source files; {@code false}, to disable processing of source
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
     * @see #getSourceFileProcessorClass()
     * @see #configureSourceFileProcessor(org.jomc.tools.SourceFileProcessor)
     */
    public SourceFileProcessor newSourceFileProcessor() throws BuildException
    {
        try
        {
            final SourceFileProcessor sourceFileProcessor = this.getSourceFileProcessorClass().newInstance();
            this.configureSourceFileProcessor( sourceFileProcessor );
            return sourceFileProcessor;
        }
        catch ( final InstantiationException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getSourceFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
        catch ( final IllegalAccessException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getSourceFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
    }

    /**
     * Configures a given {@code SourceFileProcessor} instance using the properties of the instance.
     *
     * @param sourceFileProcessor The source file processor to configure.
     *
     * @throws NullPointerException if {@code sourceFileProcessor} is {@code null}.
     * @throws BuildException if configuring {@code sourceFileProcessor} fails.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     */
    public void configureSourceFileProcessor( final SourceFileProcessor sourceFileProcessor ) throws BuildException
    {
        if ( sourceFileProcessor == null )
        {
            throw new NullPointerException( "sourceFileProcessor" );
        }

        this.configureJomcTool( sourceFileProcessor );
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
            this.log( Messages.getMessage( "sourceProcessingSuccess" ) );
        }
        else
        {
            this.log( Messages.getMessage( "sourceProcessingDisabled" ) );
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
        this.log( Messages.getMessage( "unimplementedTask", this.getClass().getName(), "processSourceFiles" ),
                  Project.MSG_ERR );

    }

    /** {@inheritDoc} */
    @Override
    public SourceFileProcessorTask clone()
    {
        return (SourceFileProcessorTask) super.clone();
    }

}
