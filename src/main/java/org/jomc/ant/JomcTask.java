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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.jomc.ant.types.KeyValueType;
import org.jomc.ant.types.NameType;
import org.jomc.model.ModelObject;
import org.jomc.modlet.DefaultModelContext;
import org.jomc.modlet.DefaultModletProvider;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

/**
 * Base class for executing tasks.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcTask extends Task
{

    /** The class path to process. */
    private Path classpath;

    /** The identifier of the model to process. */
    private String model;

    /** The name of the {@code ModelContext} implementation class backing the task. */
    private String modelContextClassName;

    /** Controls processing of models. */
    private boolean modelProcessingEnabled = true;

    /** The location to search for modlets. */
    private String modletLocation;

    /** The {@code http://jomc.org/modlet} namespace schema system id of the context backing the task. */
    private String modletSchemaSystemId;

    /** The location to search for providers. */
    private String providerLocation;

    /** The location to search for platform providers. */
    private String platformProviderLocation;

    /** Property controlling the execution of the task. */
    private Object _if;

    /** Property controlling the execution of the task. */
    private Object unless;

    /** Creates a new {@code JomcTask} instance. */
    public JomcTask()
    {
        super();
    }

    /**
     * Gets an object controlling the execution of the task.
     *
     * @return An object controlling the execution of the task or {@code null}.
     *
     * @see #setIf(java.lang.Object)
     */
    public final Object getIf()
    {
        return this._if;
    }

    /**
     * Sets an object controlling the execution of the task.
     *
     * @param value The new object controlling the execution of the task or {@code null}.
     *
     * @see #getIf()
     */
    public final void setIf( final Object value )
    {
        this._if = value;
    }

    /**
     * Gets an object controlling the execution of the task.
     *
     * @return An object controlling the execution of the task or {@code null}.
     *
     * @see #setUnless(java.lang.Object)
     */
    public final Object getUnless()
    {
        if ( this.unless == null )
        {
            this.unless = Boolean.TRUE;
        }

        return this.unless;
    }

    /**
     * Sets an object controlling the execution of the task.
     *
     * @param value The new object controlling the execution of the task or {@code null}.
     *
     * @see #getUnless()
     */
    public final void setUnless( final Object value )
    {
        this.unless = value;
    }

    /**
     * Creates a new {@code Path} instance.
     *
     * @return A new {@code Path} instance.
     */
    public final Path createClasspath()
    {
        return this.getClasspath().createPath();
    }

    /**
     * Gets the class path to process.
     *
     * @return The class path to process.
     */
    public final Path getClasspath()
    {
        if ( this.classpath == null )
        {
            this.classpath = new Path( this.getProject() );
        }

        return this.classpath;
    }

    /**
     * Adds to the class path to process.
     *
     * @param value The path to add to the list of class path elements.
     */
    public final void setClasspath( final Path value )
    {
        this.getClasspath().add( value );
    }

    /**
     * Adds a reference to a class path defined elsewhere.
     *
     * @param value A reference to a class path.
     */
    public final void setClasspathRef( final Reference value )
    {
        this.getClasspath().setRefid( value );
    }

    /**
     * Gets the identifier of the model to process.
     *
     * @return The identifier of the model to process.
     */
    public final String getModel()
    {
        if ( this.model == null )
        {
            this.model = ModelObject.MODEL_PUBLIC_ID;
        }

        return this.model;
    }

    /**
     * Sets the identifier of the model to process.
     *
     * @param value The new identifier of the model to process or {@code null}.
     */
    public final void setModel( final String value )
    {
        this.model = value;
    }

    /**
     * Gets the name of the {@code ModelContext} implementation class backing the task.
     *
     * @return The name of the {@code ModelContext} implementation class backing the task or {@code null}.
     *
     * @see #setModelContextClassName(java.lang.String)
     */
    public final String getModelContextClassName()
    {
        return this.modelContextClassName;
    }

    /**
     * Sets the name of the {@code ModelContext} implementation class backing the task.
     *
     * @param value The new name of the {@code ModelContext} implementation class backing the task or {@code null}.
     *
     * @see #getModelContextClassName()
     */
    public final void setModelContextClassName( final String value )
    {
        this.modelContextClassName = value;
    }

    /**
     * Gets a flag indicating the processing of models is enabled.
     *
     * @return {@code true} if processing of models is enabled; {@code false} else.
     */
    public final boolean isModelProcessingEnabled()
    {
        return this.modelProcessingEnabled;
    }

    /**
     * Sets the flag indicating the processing of models is enabled.
     *
     * @param value {@code true} to enable processing of models; {@code false} to disable processing of models.
     */
    public final void setModelProcessingEnabled( final boolean value )
    {
        this.modelProcessingEnabled = value;
    }

    /**
     * Gets the location searched for modlets.
     *
     * @return The location searched for modlets or {@code null}.
     */
    public final String getModletLocation()
    {
        return this.modletLocation;
    }

    /**
     * Sets the location to search for modlets.
     *
     * @param value The new location to search for modlets or {@code null}.
     */
    public final void setModletLocation( final String value )
    {
        this.modletLocation = value;
    }

    /**
     * Gets the {@code http://jomc.org/modlet} namespace schema system id of the context backing the task.
     *
     * @return The {@code http://jomc.org/modlet} namespace schema system id of the context backing the task or
     * {@code null}.
     *
     * @see #setModletSchemaSystemId(java.lang.String)
     */
    public final String getModletSchemaSystemId()
    {
        return this.modletSchemaSystemId;
    }

    /**
     * Sets the {@code http://jomc.org/modlet} namespace schema system id of the context backing the task.
     *
     * @param value The new {@code http://jomc.org/modlet} namespace schema system id of the context backing the task or
     * {@code null}.
     *
     * @see #getModletSchemaSystemId()
     */
    public final void setModletSchemaSystemId( final String value )
    {
        this.modletSchemaSystemId = value;
    }

    /**
     * Gets the location searched for providers.
     *
     * @return The location searched for providers or {@code null}.
     */
    public final String getProviderLocation()
    {
        return this.providerLocation;
    }

    /**
     * Sets the location to search for providers.
     *
     * @param value The new location to search for providers or {@code null}.
     */
    public final void setProviderLocation( final String value )
    {
        this.providerLocation = value;
    }

    /**
     * Gets the location searched for platform provider resources.
     *
     * @return The location searched for platform provider resources or {@code null}.
     */
    public final String getPlatformProviderLocation()
    {
        return this.platformProviderLocation;
    }

    /**
     * Sets the location to search for platform provider resources.
     *
     * @param value The new location to search for platform provider resources or {@code null}.
     */
    public final void setPlatformProviderLocation( final String value )
    {
        this.platformProviderLocation = value;
    }

    /**
     * Called by the project to let the task do its work.
     *
     * @throws BuildException if something goes wrong with the build.
     *
     * @see #getIf()
     * @see #getUnless()
     * @see #preExecuteTask()
     * @see #executeTask()
     * @see #postExecuteTask()
     */
    @Override
    public final void execute() throws BuildException
    {
        final PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper( this.getProject() );

        if ( propertyHelper.testIfCondition( this.getIf() ) && !propertyHelper.testUnlessCondition( this.getUnless() ) )
        {
            try
            {
                this.preExecuteTask();
                this.executeTask();
            }
            finally
            {
                this.postExecuteTask();
            }
        }
    }

    /**
     * Called by the {@code execute} method prior to the {@code executeTask} method.
     *
     * @throws BuildException if building fails.
     */
    public void preExecuteTask() throws BuildException
    {
        this.logSeparator();
        this.log( getMessage( "title" ) );
        this.logSeparator();

        ModelContext.setModelContextClassName( this.getModelContextClassName() );
        ModelContext.setDefaultModletSchemaSystemId( this.getModletSchemaSystemId() );
        DefaultModelContext.setDefaultProviderLocation( this.getProviderLocation() );
        DefaultModelContext.setDefaultPlatformProviderLocation( this.getPlatformProviderLocation() );
        DefaultModletProvider.setDefaultModletLocation( this.getModletLocation() );

        this.assertNotNull( "model", this.getModel() );
    }

    /**
     * Called by the {@code execute} method prior to the {@code postExecuteTask} method.
     *
     * @throws BuildException if building fails.
     */
    public void executeTask() throws BuildException
    {
        this.getProject().log( getMessage( "unimplementedTask", this.getClass().getName() ), Project.MSG_WARN );
    }

    /**
     * Called by the {@code execute} method after the {@code preExecuteTask}/{@code executeTask} methods even if those
     * methods threw an exception.
     *
     * @throws BuildException if building fails.
     */
    public void postExecuteTask() throws BuildException
    {
        ModelContext.setModelContextClassName( null );
        ModelContext.setDefaultModletSchemaSystemId( null );
        DefaultModelContext.setDefaultProviderLocation( null );
        DefaultModelContext.setDefaultPlatformProviderLocation( null );
        DefaultModletProvider.setDefaultModletLocation( null );

        this.logSeparator();
    }

    /**
     * Gets a {@code Model} from a given {@code ModelContext}.
     *
     * @param context The context to get a {@code Model} from.
     *
     * @return The {@code Model} from {@code context}.
     *
     * @throws NullPointerException if {@code contexŧ} is {@code null}.
     * @throws ModelException if getting the model fails.
     *
     * @see #getModel()
     * @see #isModelProcessingEnabled()
     */
    public Model getModel( final ModelContext context ) throws ModelException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        Model foundModel = context.findModel( this.getModel() );

        if ( foundModel != null && this.isModelProcessingEnabled() )
        {
            foundModel = context.processModel( foundModel );
        }

        return foundModel;
    }

    /**
     * Creates a new {@code ProjectClassLoader} instance.
     *
     * @return A new {@code ProjectClassLoader} instance.
     *
     * @throws IOException if creating a new class loader instance fails.
     */
    public ProjectClassLoader newProjectClassLoader() throws IOException
    {
        final ProjectClassLoader classLoader = new ProjectClassLoader( this.getProject(), this.getClasspath() );
        classLoader.getModletExcludes().addAll( ProjectClassLoader.getDefaultModletExcludes() );
        classLoader.getProviderExcludes().addAll( ProjectClassLoader.getDefaultProviderExcludes() );
        classLoader.getSchemaExcludes().addAll( ProjectClassLoader.getDefaultSchemaExcludes() );
        classLoader.getServiceExcludes().addAll( ProjectClassLoader.getDefaultServiceExcludes() );
        return classLoader;
    }

    /**
     * Creates a new {@code ModelContext} instance using a given class loader.
     *
     * @param classLoader The class loader to create a new {@code ModelContext} instance with.
     *
     * @return A new {@code ModelContext} instance backed by {@code classLoader}.
     *
     * @throws ModelException if creating a new {@code ModelContext} instance fails.
     */
    public ModelContext newModelContext( final ClassLoader classLoader ) throws ModelException
    {
        final ModelContext modelContext = ModelContext.createModelContext( classLoader );
        modelContext.setLogLevel( Level.ALL );
        modelContext.getListeners().add( new ModelContext.Listener()
        {

            @Override
            public void onLog( final Level level, final String message, final Throwable t )
            {
                logMessage( level, message, t );
            }

        } );

        return modelContext;
    }

    /**
     * Creates a new {@code TransformerFactory} using an {@code ErrorListener} logging messages to the project.
     *
     * @return A new {@code TransformerFactory} using an {@code ErrorListener} logging messages to the project.
     *
     * @see ProjectErrorListener
     */
    public TransformerFactory newTransformerFactory()
    {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setErrorListener( new ProjectErrorListener( this.getProject() ) );
        return transformerFactory;
    }

    /**
     * Creates a new {@code Transformer} by loading a style sheet from a given location.
     *
     * @param location The location of the style sheet to load.
     * @param classLoader The class loader to use for loading the style sheet.
     *
     * @return A new {@code Transformer} for {@code location}.
     *
     * @throws NullPointerException if {@code location} or {@code classLoader} is {@code null}.
     * @throws URISyntaxException if {@code location} holds an unsupported value.
     * @throws TransformerConfigurationException if creating a new {@code Transformer} fails.
     *
     * @see ProjectErrorListener
     */
    public Transformer newTransformer( final String location, final ClassLoader classLoader )
        throws URISyntaxException, TransformerConfigurationException
    {
        if ( location == null )
        {
            throw new NullPointerException( "location" );
        }
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        Source source = null;
        File file = new File( location );
        if ( !file.isAbsolute() )
        {
            file = new File( this.getProject().getBaseDir(), location );
        }

        if ( file.exists() )
        {
            source = new StreamSource( file );
        }
        else
        {
            final URL resource = classLoader.getResource( location );

            if ( resource != null )
            {
                source = new StreamSource( resource.toURI().toASCIIString() );
            }
        }

        if ( source == null )
        {
            throw new BuildException( getMessage( "stylesheetNotFound", location ), this.getLocation() );
        }

        final Transformer transformer = this.newTransformerFactory().newTransformer( source );
        transformer.setErrorListener( new ProjectErrorListener( this.getProject() ) );

        for ( Map.Entry<Object, Object> e : System.getProperties().entrySet() )
        {
            transformer.setParameter( e.getKey().toString(), e.getValue() );
        }

        return transformer;
    }

    /**
     * Throws a {@code BuildException} on a given {@code null} value.
     *
     * @param attributeName The name of a mandatory attribute.
     * @param value The value of that attribute.
     *
     * @throws NullPointerException if {@code attributeName} is {@code null}.
     * @throws BuildException if {@code value} is {@code null}.
     */
    public final void assertNotNull( final String attributeName, final Object value ) throws BuildException
    {
        if ( attributeName == null )
        {
            throw new NullPointerException( "attributeName" );
        }

        if ( value == null )
        {
            throw new BuildException( getMessage( "mandatoryAttribute", attributeName ), this.getLocation() );
        }
    }

    /**
     * Throws a {@code BuildException} on a {@code null} value of a {@code name} property of a given {@code NameType}
     * collection.
     *
     * @param names The collection holding the  {@code NameType} instances to test.
     *
     * @throws NullPointerException if {@code names} is {@code null}.
     * @throws BuildException if a {@code name} property of a given {@code NameType} from the {@code names} collection
     * holds a {@code null} value.
     */
    public final void assertNamesNotNull( final Collection<? extends NameType> names ) throws BuildException
    {
        if ( names == null )
        {
            throw new NullPointerException( "names" );
        }

        for ( NameType n : names )
        {
            this.assertNotNull( "name", n.getName() );
        }
    }

    /**
     * Throws a {@code BuildException} on a {@code null} value of a {@code key} property of a given {@code KeyValueType}
     * collection.
     *
     * @param keys The collection holding the  {@code KeyValueType} instances to test.
     *
     * @throws NullPointerException if {@code keys} is {@code null}.
     * @throws BuildException if a {@code key} property of a given {@code KeyValueType} from the {@code keys} collection
     * holds a {@code null} value.
     */
    public final void assertKeysNotNull( final Collection<? extends KeyValueType<?, ?>> keys ) throws BuildException
    {
        if ( keys == null )
        {
            throw new NullPointerException( "keys" );
        }

        for ( KeyValueType<?, ?> k : keys )
        {
            this.assertNotNull( "key", k.getKey() );
        }
    }

    /**
     * Throws a {@code BuildException} if a given {@code File} is not an existing directory.
     *
     * @param file The file to test.
     *
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws BuildException if {@code file} is not an existing directory.
     */
    public final void assertDirectory( final File file ) throws BuildException
    {
        if ( file == null )
        {
            throw new NullPointerException( "file" );
        }

        if ( !file.isDirectory() )
        {
            throw new BuildException( getMessage( "directoryNotFound", file.getAbsolutePath() ), this.getLocation() );
        }
    }

    /** Logs a separator string. */
    public final void logSeparator()
    {
        this.log( getMessage( "separator" ) );
    }

    /**
     * Logs a message at a given level.
     *
     * @param level The level to log at.
     * @param message The message to log.
     *
     * @throws BuildException if logging fails.
     */
    public final void logMessage( final Level level, final String message ) throws BuildException
    {
        try
        {
            final BufferedReader reader = new BufferedReader( new StringReader( message ) );

            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                if ( level.intValue() >= Level.SEVERE.intValue() )
                {
                    log( line, Project.MSG_ERR );
                }
                else if ( level.intValue() >= Level.WARNING.intValue() )
                {
                    log( line, Project.MSG_WARN );
                }
                else if ( level.intValue() >= Level.INFO.intValue() )
                {
                    log( line, Project.MSG_INFO );
                }
                else
                {
                    log( line, Project.MSG_DEBUG );
                }
            }

        }
        catch ( final IOException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
    }

    /**
     * Logs a message at a given level.
     *
     * @param level The level to log at.
     * @param message The message to log.
     * @param throwable The throwable to log.
     *
     * @throws BuildException if logging fails.
     */
    public final void logMessage( final Level level, final String message, final Throwable throwable )
        throws BuildException
    {
        this.logMessage( level, message );

        if ( level.intValue() >= Level.SEVERE.intValue() )
        {
            log( throwable, Project.MSG_ERR );
        }
        else if ( level.intValue() >= Level.WARNING.intValue() )
        {
            log( throwable, Project.MSG_WARN );
        }
        else if ( level.intValue() >= Level.INFO.intValue() )
        {
            log( throwable, Project.MSG_INFO );
        }
        else
        {
            log( throwable, Project.MSG_DEBUG );
        }
    }

    /**
     * Logs a validation report.
     *
     * @param context The context to use for logging the report.
     * @param report The report to log.
     *
     * @throws NullPointerException if {@code context} or {@code report} is {@code null}.
     * @throws BuildException if logging fails.
     */
    public final void logValidationReport( final ModelContext context, final ModelValidationReport report )
    {
        try
        {
            if ( !report.getDetails().isEmpty() )
            {
                this.logSeparator();
                Marshaller marshaller = null;

                for ( ModelValidationReport.Detail detail : report.getDetails() )
                {
                    this.logMessage( detail.getLevel(), "o " + detail.getMessage() );

                    if ( detail.getElement() != null )
                    {
                        if ( marshaller == null )
                        {
                            marshaller = context.createMarshaller( this.getModel() );
                            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        }

                        final StringWriter stringWriter = new StringWriter();
                        marshaller.marshal( detail.getElement(), stringWriter );
                        this.logMessage( Level.FINEST, stringWriter.toString() );
                    }
                }
            }
        }
        catch ( final ModelException e )
        {
            throw new BuildException( getMessage( e ), e, this.getLocation() );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new BuildException( message, e, this.getLocation() );
        }
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public JomcTask clone()
    {
        try
        {
            final JomcTask clone = (JomcTask) super.clone();
            clone.classpath = (Path) ( this.classpath != null ? this.classpath.clone() : null );
            return clone;
        }
        catch ( final CloneNotSupportedException e )
        {
            throw new AssertionError( e );
        }
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            JomcTask.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
