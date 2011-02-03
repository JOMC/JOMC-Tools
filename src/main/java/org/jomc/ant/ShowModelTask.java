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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.model.Instance;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.modlet.ObjectFactory;

/**
 * Task for displaying model objects.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class ShowModelTask extends JomcModelTask
{

    /** The identifier of a specification to show. */
    private String specification;

    /** The identifier of an implementation to show. */
    private String implementation;

    /** The name of a module to show. */
    private String module;

    /** The encoding to use when writing the model. */
    private String modelEncoding;

    /** File to write the model to. */
    private File modelFile;

    /** Creates a new {@code ShowModelTask} instance. */
    public ShowModelTask()
    {
        super();
    }

    /**
     * Gets the encoding of the model resource.
     *
     * @return The encoding of the model resource.
     *
     * @see #setModelEncoding(java.lang.String)
     */
    public String getModelEncoding()
    {
        if ( this.modelEncoding == null )
        {
            this.modelEncoding = new OutputStreamWriter( new ByteArrayOutputStream() ).getEncoding();
        }

        return this.modelEncoding;
    }

    /**
     * Sets the encoding of the model resource.
     *
     * @param value The new encoding of the model resource or {@code null}.
     *
     * @see #getModelEncoding()
     */
    public void setModelEncoding( final String value )
    {
        this.modelEncoding = value;
    }

    /**
     * Gets the file to write the model to.
     *
     * @return The file to write the model to or {@code null}.
     *
     * @see #setModelFile(java.io.File)
     */
    public File getModelFile()
    {
        return this.modelFile;
    }

    /**
     * Sets the file to write the model to.
     *
     * @param value The new file to write the model to or {@code null}.
     *
     * @see #getModelFile()
     */
    public void setModelFile( final File value )
    {
        this.modelFile = value;
    }

    /**
     * Gets the identifier of a specification to show.
     *
     * @return The identifier of a specification to show or {@code null}.
     *
     * @see #setSpecification(java.lang.String)
     */
    public String getSpecification()
    {
        return this.specification;
    }

    /**
     * Sets the identifier of a specification to show.
     *
     * @param value The new identifier of a specification to show or {@code null}.
     *
     * @see #getSpecification()
     */
    public void setSpecification( final String value )
    {
        this.specification = value;
    }

    /**
     * Gets the specification to show from a given model.
     *
     * @param model The model to get the specification to show from.
     *
     * @return The specification to show or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getSpecification()
     */
    public Specification getSpecification( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Specification s = null;

        if ( this.getSpecification() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                s = modules.getSpecification( this.getSpecification() );
            }

            if ( s == null )
            {
                this.log( getMessage( "specificationNotFound", this.getSpecification() ), Project.MSG_WARN );
            }
        }

        return s;
    }

    /**
     * Gets the identifier of an implementation to show.
     *
     * @return The identifier of an implementation to show or {@code null}.
     *
     * @see #setImplementation(java.lang.String)
     */
    public String getImplementation()
    {
        return this.implementation;
    }

    /**
     * Sets the identifier of an implementation to show.
     *
     * @param value The new identifier of an implementation to show or {@code null}.
     *
     * @see #getImplementation()
     */
    public void setImplementation( final String value )
    {
        this.implementation = value;
    }

    /**
     * Gets the instance to show from a given model.
     *
     * @param model The model to get the instance to show from.
     *
     * @return The implementation to show or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getImplementation()
     */
    public Instance getInstance( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Instance i = null;

        if ( this.getImplementation() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                i = modules.getInstance( this.getImplementation() );
            }

            if ( i == null )
            {
                this.log( getMessage( "implementationNotFound", this.getImplementation() ), Project.MSG_WARN );
            }
        }

        return i;
    }

    /**
     * Gets the identifier of a module to show.
     *
     * @return The identifier of a module to show or {@code null}.
     *
     * @see #setModule(java.lang.String)
     */
    public String getModule()
    {
        return this.module;
    }

    /**
     * Sets the identifier of a module to show.
     *
     * @param value The new identifier of a module to show or {@code null}.
     *
     * @see #getModule()
     */
    public void setModule( final String value )
    {
        this.module = value;
    }

    /**
     * Gets the module to show from a given model.
     *
     * @param model The model to get the module to show from.
     *
     * @return The module to show or {@code null}.
     *
     * @throws NullPointerException if {@code model} is {@code null}.
     *
     * @see #getModule()
     */
    public Module getModule( final Model model )
    {
        if ( model == null )
        {
            throw new NullPointerException( "model" );
        }

        Module m = null;

        if ( this.getModule() != null )
        {
            final Modules modules = ModelHelper.getModules( model );

            if ( modules != null )
            {
                m = modules.getModule( this.getModule() );
            }

            if ( m == null )
            {
                this.log( getMessage( "moduleNotFound", this.getModule() ), Project.MSG_WARN );
            }
        }

        return m;
    }

    /** {@inheritDoc} */
    @Override
    public void executeTask() throws BuildException
    {
        try
        {
            final ProjectClassLoader classLoader = this.newProjectClassLoader();
            final ModelContext modelContext = this.newModelContext( classLoader );
            final Model model = this.getModel( modelContext );
            final Marshaller marshaller = modelContext.createMarshaller( this.getModel() );
            final ModelValidationReport validationReport = modelContext.validateModel(
                this.getModel(), new JAXBSource( marshaller, new ObjectFactory().createModel( model ) ) );

            this.logValidationReport( modelContext, validationReport );
            marshaller.setProperty( Marshaller.JAXB_ENCODING, this.getModelEncoding() );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            Model displayModel = new Model();
            displayModel.setIdentifier( this.getModel() );

            final Specification s = this.getSpecification( model );
            if ( s != null )
            {
                displayModel.getAny().add( s );
            }

            final Instance i = this.getInstance( model );
            if ( i != null )
            {
                displayModel.getAny().add( i );
            }

            final Module m = this.getModule( model );
            if ( m != null )
            {
                displayModel.getAny().add( m );
            }

            if ( displayModel.getAny().isEmpty() )
            {
                displayModel = model;
            }

            if ( this.getModelFile() != null )
            {
                this.log( getMessage( "writing", this.getModel(), this.getModelFile().getAbsolutePath() ),
                          Project.MSG_INFO );

                marshaller.marshal( new ObjectFactory().createModel( displayModel ), this.getModelFile() );
            }
            else
            {
                this.log( getMessage( "showing", this.getModel() ), Project.MSG_INFO );

                final StringWriter writer = new StringWriter();
                marshaller.marshal( new ObjectFactory().createModel( displayModel ), writer );

                final BufferedReader reader = new BufferedReader( new StringReader( writer.toString() ) );
                String line;

                while ( ( line = reader.readLine() ) != null )
                {
                    this.log( line, Project.MSG_INFO );
                }
            }
        }
        catch ( final IOException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new BuildException( message, e, this.getLocation() );
        }
        catch ( final ModelException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
    }

    /** {@inheritDoc} */
    @Override
    public ShowModelTask clone()
    {
        return (ShowModelTask) super.clone();
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ShowModelTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
