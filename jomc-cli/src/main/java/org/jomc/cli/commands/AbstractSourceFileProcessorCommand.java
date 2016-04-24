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
import org.jomc.tools.SourceFileProcessor;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * {@code SourceFileProcessor} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractSourceFileProcessorCommand extends AbstractJomcToolCommand
{

    /**
     * Creates a new {@code AbstractSourceFileProcessorCommand} instance.
     */
    public AbstractSourceFileProcessorCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.SOURCE_FILE_PROCESSOR_CLASSNAME_OPTION );
        options.addOption( Options.NO_SOURCE_PROCESSING_OPTION );
        options.addOption( Options.SOURCE_DIRECTORY_OPTION );
        return options;
    }

    /**
     * Creates a new {@code SourceFileProcessor} instance taking a command line.
     *
     * @param commandLine The command line to process.
     *
     * @return A new {@code SourceFileProcessor} instance as specified by the given command line or {@code null}, if
     * creating a new instance fails.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if creating a new instance fails.
     */
    protected SourceFileProcessor createSourceFileProcessor( final CommandLine commandLine )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final String className =
            commandLine.hasOption( Options.SOURCE_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                ? commandLine.getOptionValue( Options.SOURCE_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                : SourceFileProcessor.class.getName();

        return this.createJomcTool( className, SourceFileProcessor.class, commandLine );
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

        if ( commandLine.hasOption( Options.NO_SOURCE_PROCESSING_OPTION.getOpt() ) )
        {
            this.log( Level.INFO, Messages.getMessage( "sourceProcessingDisabled" ), null );
        }
        else
        {
            this.processSourceFiles( commandLine );
        }
    }

    /**
     * Processes source files.
     *
     * @param commandLine The command line to execute.
     *
     * @throws CommandExecutionException if processing source files fails.
     */
    protected abstract void processSourceFiles( final CommandLine commandLine ) throws CommandExecutionException;

}
