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
package org.jomc.tools.cli.commands;

import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.jomc.tools.ResourceFileProcessor;

/**
 * {@code ResourceFileProcessor} based command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public abstract class AbstractResourceFileProcessorCommand extends AbstractJomcToolCommand
{

    /**
     * Creates a new {@code AbstractResourceFileProcessorCommand} instance.
     */
    public AbstractResourceFileProcessorCommand()
    {
        super();
    }

    @Override
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = super.getOptions();
        options.addOption( Options.RESOURCE_FILE_PROCESSOR_CLASSNAME_OPTION );
        options.addOption( Options.NO_RESOURCE_PROCESSING_OPTION );
        options.addOption( Options.RESOURCE_DIRECTORY_OPTION );
        return options;
    }

    /**
     * Creates a new {@code ResourceFileProcessor} instance taking a command line.
     *
     * @param commandLine The command line to process.
     *
     * @return A new {@code ResourceFileProcessor} instance as specified by the given command line or {@code null}, if
     * creating a new instance fails.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if creating a new instance fails.
     */
    protected ResourceFileProcessor createResourceFileProcessor( final CommandLine commandLine )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final String className =
            commandLine.hasOption( Options.RESOURCE_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                ? commandLine.getOptionValue( Options.RESOURCE_FILE_PROCESSOR_CLASSNAME_OPTION.getOpt() )
                : ResourceFileProcessor.class.getName();

        final ResourceFileProcessor tool = this.createJomcTool( className, ResourceFileProcessor.class, commandLine );
        tool.setResourceBundleDefaultLocale( this.getLocale( commandLine ) );
        return tool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        if ( commandLine.hasOption( Options.NO_RESOURCE_PROCESSING_OPTION.getOpt() ) )
        {
            this.log( Level.INFO, Messages.getMessage( "resourceProcessingDisabled" ), null );
        }
        else
        {
            this.processResourceFiles( commandLine );
        }
    }

    /**
     * Processes resource files.
     *
     * @param commandLine The command line to execute.
     *
     * @throws CommandExecutionException if processing resource files fails.
     */
    protected abstract void processResourceFiles( final CommandLine commandLine ) throws CommandExecutionException;

}
