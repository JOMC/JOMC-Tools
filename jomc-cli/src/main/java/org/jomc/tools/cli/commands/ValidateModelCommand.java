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

import java.io.IOException;
import java.util.Locale;
import org.apache.commons.cli.CommandLine;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

/**
 * {@code validate-model} command implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public final class ValidateModelCommand extends AbstractModelCommand
{

    /**
     * Creates a new {@code ValidateModelCommand} instance.
     */
    public ValidateModelCommand()
    {
        super();
    }

    @Override
    public String getName()
    {
        return "validate-model";
    }

    @Override
    public String getAbbreviatedName()
    {
        return "vm";
    }

    @Override
    public String getShortDescription( final Locale locale )
    {
        return Messages.getMessage( "validateModelShortDescription" );
    }

    @Override
    public String getLongDescription( final Locale locale )
    {
        return null;
    }

    @Override
    protected void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        try ( final CommandLineClassLoader classLoader = new CommandLineClassLoader( commandLine ) )
        {
            final ModelContext context = this.createModelContext( commandLine, classLoader );
            final Model model = this.getModel( context, commandLine );
            final ModelValidationReport validationReport = context.validateModel( model );
            this.log( validationReport, context.createMarshaller( model.getIdentifier() ) );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( Messages.getMessage( "invalidModel",
                                                                          this.getModel( commandLine ) ) );

            }
        }
        catch ( final IOException | ModelException e )
        {
            throw new CommandExecutionException( Messages.getMessage( e ), e );
        }
    }

}
