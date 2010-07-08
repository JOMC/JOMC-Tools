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
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import org.apache.maven.plugin.MojoExecutionException;
import org.jomc.model.Module;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;
import org.jomc.tools.ClassFileProcessor;

/**
 * Base class for validating class file model objects.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class AbstractClassesValidateMojo extends AbstractJomcMojo
{

    /** Constant for the name of the tool backing the mojo. */
    private static final String TOOLNAME = "ClassFileProcessor";

    /** Creates a new {@code AbstractClassesValidateMojo} instance. */
    public AbstractClassesValidateMojo()
    {
        super();
    }

    @Override
    protected final void executeTool() throws Exception
    {
        this.logSeparator();

        if ( this.isClassProcessingEnabled() )
        {
            this.logProcessingModule( TOOLNAME, this.getClassesModuleName() );

            final ModelContext context = this.createModelContext( this.getClassesClassLoader() );
            final ClassFileProcessor tool = this.createClassFileProcessor( context );
            final JAXBContext jaxbContext = context.createContext( this.getModel() );
            final Source source = new JAXBSource( jaxbContext, new ObjectFactory().createModel( tool.getModel() ) );
            ModelValidationReport validationReport = context.validateModel( this.getModel(), source );

            this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

            if ( validationReport.isModelValid() )
            {
                final Module module = tool.getModules().getModule( this.getClassesModuleName() );

                if ( module != null )
                {
                    validationReport = tool.validateModelObjects( module, context, this.getClassesDirectory() );
                    this.log( context, validationReport.isModelValid() ? Level.INFO : Level.SEVERE, validationReport );

                    if ( !validationReport.isModelValid() )
                    {
                        throw new MojoExecutionException( getMessage( "failed" ) );
                    }

                    this.logToolSuccess( TOOLNAME );
                }
                else
                {
                    this.logMissingModule( this.getClassesModuleName() );
                }
            }
            else
            {
                throw new MojoExecutionException( getMessage( "failed" ) );
            }
        }
        else
        {
            this.log( Level.INFO, getMessage( "disabled" ), null );
        }
    }

    /**
     * Gets the name of the module to validate class file model objects of.
     *
     * @return The name of the module to validate class file model objects of.
     *
     * @throws MojoExecutionException if getting the name fails.
     */
    protected abstract String getClassesModuleName() throws MojoExecutionException;

    /**
     * Gets the class loader to use for validating class file model objects.
     *
     * @return The class loader to use for validating class file model objects.
     *
     * @throws MojoExecutionException if getting the class loader fails.
     */
    protected abstract ClassLoader getClassesClassLoader() throws MojoExecutionException;

    /**
     * Gets the directory holding the class files to validate model objects of.
     *
     * @return The directory holding the class files to validate model objects of.
     *
     * @throws MojoExecutionException if getting the directory fails.
     */
    protected abstract File getClassesDirectory() throws MojoExecutionException;

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            AbstractClassesValidateMojo.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
