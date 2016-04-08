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
package org.jomc.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.ClassFileProcessor;

/**
 * Base class for executing class file processor based tasks.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @see #processClassFiles()
 */
public class ClassFileProcessorTask extends JomcToolTask
{

    /**
     * Controls processing of class files.
     */
    private boolean classProcessingEnabled = true;

    /**
     * Class of the {@code ClassFileProcessor} backing the task.
     */
    private Class<? extends ClassFileProcessor> classFileProcessorClass;

    /**
     * Creates a new {@code ClassFileProcessorTask} instance.
     */
    public ClassFileProcessorTask()
    {
        super();
    }

    /**
     * Gets a flag indicating the processing of classes is enabled.
     *
     * @return {@code true}, if processing of classes is enabled; {@code false}, else.
     *
     * @see #setClassProcessingEnabled(boolean)
     */
    public final boolean isClassProcessingEnabled()
    {
        return this.classProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of classes is enabled.
     *
     * @param value {@code true}, to enable processing of classes; {@code false}, to disable processing of classes.
     *
     * @see #isClassProcessingEnabled()
     */
    public final void setClassProcessingEnabled( final boolean value )
    {
        this.classProcessingEnabled = value;
    }

    /**
     * Gets the class of the {@code ClassFileProcessor} backing the task.
     *
     * @return The class of the {@code ClassFileProcessor} backing the task.
     *
     * @see #setClassFileProcessorClass(java.lang.Class)
     */
    public final Class<? extends ClassFileProcessor> getClassFileProcessorClass()
    {
        if ( this.classFileProcessorClass == null )
        {
            this.classFileProcessorClass = ClassFileProcessor.class;
        }

        return this.classFileProcessorClass;
    }

    /**
     * Sets the class of the {@code ClassFileProcessor} backing the task.
     *
     * @param value The new class of the {@code ClassFileProcessor} backing the task or {@code null}.
     *
     * @see #getClassFileProcessorClass()
     */
    public final void setClassFileProcessorClass( final Class<? extends ClassFileProcessor> value )
    {
        this.classFileProcessorClass = value;
    }

    /**
     * Creates a new {@code ClassFileProcessor} instance setup using the properties of the instance.
     *
     * @return A new {@code ClassFileProcessor} instance.
     *
     * @throws BuildException if creating a new {@code ClassFileProcessor} instance fails.
     *
     * @see #getClassFileProcessorClass()
     * @see #configureClassFileProcessor(org.jomc.tools.ClassFileProcessor)
     */
    public ClassFileProcessor newClassFileProcessor() throws BuildException
    {
        try
        {
            final ClassFileProcessor classFileProcessor = this.getClassFileProcessorClass().newInstance();
            this.configureClassFileProcessor( classFileProcessor );
            return classFileProcessor;
        }
        catch ( final InstantiationException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getClassFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
        catch ( final IllegalAccessException e )
        {
            throw new BuildException( Messages.getMessage( "failedCreatingObject",
                                                           this.getClassFileProcessorClass().getName() ),
                                      e, this.getLocation() );

        }
    }

    /**
     * Configures a given {@code ClassFileProcessor} instance using the properties of the instance.
     *
     * @param classFileProcessor The class file processor to configure.
     *
     * @throws NullPointerException if {@code classFileProcessor} is {@code null}.
     * @throws BuildException if configuring {@code classFileProcessor} fails.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     */
    public void configureClassFileProcessor( final ClassFileProcessor classFileProcessor ) throws BuildException
    {
        if ( classFileProcessor == null )
        {
            throw new NullPointerException( "classFileProcessor" );
        }

        this.configureJomcTool( classFileProcessor );
    }

    /**
     * Calls the {@code processClassFiles} method if class processing is enabled.
     *
     * @throws BuildException if processing class files fails.
     *
     * @see #processClassFiles()
     */
    @Override
    public final void executeTask() throws BuildException
    {
        if ( this.isClassProcessingEnabled() )
        {
            this.processClassFiles();
            this.log( Messages.getMessage( "classProcessingSuccess" ) );
        }
        else
        {
            this.log( Messages.getMessage( "classProcessingDisabled" ) );
        }
    }

    /**
     * Processes class files.
     *
     * @throws BuildException if processing class files fails.
     *
     * @see #executeTask()
     */
    public void processClassFiles() throws BuildException
    {
        this.log( Messages.getMessage( "unimplementedTask", this.getClass().getName(), "processClassFiles" ),
                  Project.MSG_ERR );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassFileProcessorTask clone()
    {
        return (ClassFileProcessorTask) super.clone();
    }

}
