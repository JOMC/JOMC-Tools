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
package org.jomc.mojo;

import java.io.File;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.ModelContext;
import org.jomc.model.ModelValidationReport;
import org.jomc.model.Module;
import org.jomc.model.ObjectFactory;
import org.jomc.tools.SourceFileProcessor;

/**
 * Base class for managing source code files.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractSourcesMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the class. */
    private static final String TOOLNAME = "SourceFileProcessor";

    @Override
    protected final void executeTool() throws Exception
    {
        if ( this.isSourceProcessingEnabled() )
        {
            final ModelContext context = this.createModelContext( this.getSourcesClassLoader() );
            final SourceFileProcessor tool = this.createSourceFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext();
            final ModelValidationReport validationReport = context.validateModel( new JAXBSource(
                jaxbContext, new ObjectFactory().createModules( tool.getModules() ) ) );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                this.logSeparator( Level.INFO );
                final Module module = tool.getModules().getModule( this.getSourcesModuleName() );

                if ( module != null )
                {
                    this.logProcessingModule( TOOLNAME, module.getName() );
                    tool.manageSourceFiles( module, this.getSourcesDirectory() );
                    this.logToolSuccess( TOOLNAME );
                }
                else
                {
                    this.logMissingModule( this.getSourcesModuleName() );
                }

                this.logSeparator( Level.INFO );
            }
            else
            {
                throw new MojoExecutionException( getMessage( "failed" ) );
            }
        }
        else
        {
            this.logSeparator( Level.INFO );
            this.log( Level.INFO, getMessage( "disabled" ), null );
            this.logSeparator( Level.INFO );
        }
    }

    protected abstract String getSourcesModuleName() throws MojoExecutionException;

    protected abstract ClassLoader getSourcesClassLoader() throws MojoExecutionException;

    protected abstract File getSourcesDirectory() throws MojoExecutionException;

    private static String getMessage( final String key )
    {
        return ResourceBundle.getBundle( AbstractSourcesMojo.class.getName().replace( '.', '/' ) ).getString( key );
    }

}