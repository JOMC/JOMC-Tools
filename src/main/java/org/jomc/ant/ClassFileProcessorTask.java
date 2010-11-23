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
import org.jomc.tools.ClassFileProcessor;

/**
 * Base class for executing class file processor based tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ClassFileProcessorTask extends JomcToolTask
{

    /** Controls processing of class files. */
    private boolean classProcessingEnabled = true;

    /** Creates a new {@code ClassFileProcessorTask} instance. */
    public ClassFileProcessorTask()
    {
        super();
    }

    /**
     * Gets a flag indicating the processing of classes is enabled.
     *
     * @return {@code true} if processing of classes is enabled; {@code false} else.
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
     * @param value {@code true} to enable processing of classes; {@code false} to disable processing of classes.
     *
     * @see #isClassProcessingEnabled()
     */
    public final void setClassProcessingEnabled( final boolean value )
    {
        this.classProcessingEnabled = value;
    }

    /**
     * Creates a new {@code ClassFileProcessor} instance setup using the properties of the instance.
     *
     * @return A new {@code ClassFileProcessor} instance.
     *
     * @see #configureJomcTool(org.jomc.tools.JomcTool)
     */
    public ClassFileProcessor newClassFileProcessor()
    {
        final ClassFileProcessor tool = new ClassFileProcessor();
        this.configureJomcTool( tool );
        return tool;
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
            this.log( getMessage( "classProcessingSuccess" ) );
        }
        else
        {
            this.log( getMessage( "classProcessingDisabled" ) );
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
        this.log( getMessage( "unimplementedTask", this.getClass().getName() ), Project.MSG_ERR );
    }

    /** {@inheritDoc} */
    @Override
    public ClassFileProcessorTask clone()
    {
        return (ClassFileProcessorTask) super.clone();
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ClassFileProcessorTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
