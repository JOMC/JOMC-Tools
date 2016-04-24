/*
 * Copyright (C) 2009 Christian Schulte <cs@schulte.it>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
package org.jomc.cli.commands;

import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.jomc.tools.ClassFileProcessor;

/**
 * {@code ClassFileProcessor} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractClassFileProcessorCommand extends AbstractJomcToolCommand
{

    /**
     * Creates a new {@code AbstractClassFileProcessorCommand} instance.
     */
    public AbstractClassFileProcessorCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.CLASS_FILE_PROCESSOR_CLASSNAME_OPTION );
        options.addOption( Options.NO_CLASS_PROCESSING_OPTION );
        return options;
    }

    /**
     * Creates a new {@code ClassFileProcessor} instance taking a command line.
     *
     * @param commandLine The command line to process.
     *
     * @return A new {@code ClassFileProcessor} instance as specified by the given command line.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if creating a new instance fails.
     */
    protected ClassFileProcessor createClassFileProcessor( final CommandLine commandLine )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final String className =
            commandLine.hasOption( Options.CLASS_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                ? commandLine.getOptionValue( Options.CLASS_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                : ClassFileProcessor.class.getName();

        final ClassFileProcessor tool = this.createJomcTool( className, ClassFileProcessor.class, commandLine );
        return tool;
    }

    /**
     * {@inheritDoc}
     */
    protected final void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        if ( commandLine.hasOption( Options.NO_CLASS_PROCESSING_OPTION.getOpt() ) )
        {
            this.log( Level.INFO, Messages.getMessage( "classProcessingDisabled" ), null );
        }
        else
        {
            this.processClassFiles( commandLine );
        }
    }

    /**
     * Processes class files.
     *
     * @param commandLine The command line to execute.
     *
     * @throws CommandExecutionException if processing class files fails.
     */
    protected abstract void processClassFiles( final CommandLine commandLine ) throws CommandExecutionException;

}
