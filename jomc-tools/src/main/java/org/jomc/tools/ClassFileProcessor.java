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
package org.jomc.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Unknown;
import org.jomc.jls.JavaTypeName;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.ModelObject;
import org.jomc.model.ModelObjectException;
import org.jomc.model.Module;
import org.jomc.model.ObjectFactory;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.util.ParseException;
import org.jomc.util.TokenMgrError;
import org.jomc.util.VersionParser;

/**
 * Processes class files.
 *
 * <p>
 * <b>Use Cases:</b><br/><ul>
 * <li>{@link #commitModelObjects(org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #commitModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #commitModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #commitModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #validateModelObjects(org.jomc.modlet.ModelContext) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext) }</li>
 * <li>{@link #validateModelObjects(org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #validateModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext, java.io.File) }</li>
 * <li>{@link #transformModelObjects(org.jomc.modlet.ModelContext, java.io.File, java.util.List) }</li>
 * <li>{@link #transformModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File, java.util.List) }</li>
 * <li>{@link #transformModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File, java.util.List) }</li>
 * <li>{@link #transformModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File, java.util.List) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 *
 * @see #getModules()
 */
public class ClassFileProcessor extends JomcTool
{

    /**
     * Empty byte array.
     */
    private static final byte[] NO_BYTES =
    {
    };

    /**
     * Creates a new {@code ClassFileProcessor} instance.
     */
    public ClassFileProcessor()
    {
        super();
    }

    /**
     * Creates a new {@code ClassFileProcessor} instance taking a {@code ClassFileProcessor} instance to initialize the
     * instance with.
     *
     * @param tool The instance to initialize the new instance with.
     *
     * @throws NullPointerException if {@code tool} is {@code null}.
     * @throws IOException if copying {@code tool} fails.
     */
    public ClassFileProcessor( final ClassFileProcessor tool ) throws IOException
    {
        super( tool );
    }

    /**
     * Commits model objects of the modules of the instance to class files.
     *
     * @param context The model context to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code context} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File)
     */
    public final void commitModelObjects( final ModelContext context, final File classesDirectory ) throws IOException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            if ( this.getModules() != null )
            {
                this.commitModelObjects( this.getModules().getSpecifications(), this.getModules().getImplementations(),
                                         classesDirectory, context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "modulesNotFound", this.getModel().getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Commits model objects of a given module of the modules of the instance to class files.
     *
     * @param module The module to process.
     * @param context The model context to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code module}, {@code context} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File)
     * @see #commitModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext, java.io.File)
     */
    public final void commitModelObjects( final Module module, final ModelContext context, final File classesDirectory )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            if ( this.getModules() != null && this.getModules().getModule( module.getName() ) != null )
            {
                this.commitModelObjects( module.getSpecifications(), module.getImplementations(), classesDirectory,
                                         context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "moduleNotFound", module.getName() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Commits model objects of a given specification of the modules of the instance to class files.
     *
     * @param specification The specification to process.
     * @param context The model context to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code specification}, {@code context} or {@code classesDirectory} is
     * {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Specification, javax.xml.bind.Marshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final void commitModelObjects( final Specification specification, final ModelContext context,
                                          final File classesDirectory ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getSpecification( specification.getIdentifier() ) != null )
            {
                if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                {
                    final File javaClassfile =
                        this.getJavaClassfile( specification.getJavaTypeName(), classesDirectory );

                    final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                    m.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    final JavaClass javaClass = this.readJavaClass( javaClassfile );
                    this.commitModelObjects( specification, m, javaClass );
                    this.writeJavaClass( javaClass, javaClassfile );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Commits model objects of a given implementation of the modules of the instance to class files.
     *
     * @param implementation The implementation to process.
     * @param context The model context to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code implementation}, {@code context} or {@code classesDirectory} is
     * {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Implementation, javax.xml.bind.Marshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final void commitModelObjects( final Implementation implementation, final ModelContext context,
                                          final File classesDirectory ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                {
                    final File javaClassfile =
                        this.getJavaClassfile( implementation.getJavaTypeName(), classesDirectory );

                    final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                    m.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    final JavaClass javaClass = this.readJavaClass( javaClassfile );
                    this.commitModelObjects( implementation, m, javaClass );
                    this.writeJavaClass( javaClass, javaClassfile );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Commits model objects of a given specification of the modules of the instance to a given class file.
     *
     * @param specification The specification to process.
     * @param marshaller The marshaller to use for committing the model objects.
     * @param javaClass The java class to commit to.
     *
     * @throws NullPointerException if {@code specification}, {@code marshaller} or {@code javaClass} is {@code null}.
     * @throws IOException if committing model objects fails.
     */
    public void commitModelObjects( final Specification specification, final Marshaller marshaller,
                                    final JavaClass javaClass ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "committingSpecification", specification.getIdentifier() ), null );
        }

        if ( this.getModules() != null
                 && this.getModules().getSpecification( specification.getIdentifier() ) != null )
        {
            this.setClassfileAttribute( javaClass, Specification.class.getName(), this.encodeModelObject(
                                        marshaller, new ObjectFactory().createSpecification( specification ) ) );

        }
        else if ( this.isLoggable( Level.WARNING ) )
        {
            this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
        }
    }

    /**
     * Commits model objects of a given implementation of the modules of the instance to a given class file.
     *
     * @param implementation The implementation to process.
     * @param marshaller The marshaller to use for committing the model objects.
     * @param javaClass The java class to commit to.
     *
     * @throws NullPointerException if {@code implementation}, {@code marshaller} or {@code javaClass} is {@code null}.
     * @throws IOException if committing model objects fails.
     */
    public void commitModelObjects( final Implementation implementation, final Marshaller marshaller,
                                    final JavaClass javaClass ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "committingImplementation", implementation.getIdentifier() ), null );
        }

        if ( this.getModules() != null
                 && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
        {
            final ObjectFactory of = new ObjectFactory();

            Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
            if ( dependencies == null )
            {
                dependencies = new Dependencies();
            }

            Properties properties = this.getModules().getProperties( implementation.getIdentifier() );
            if ( properties == null )
            {
                properties = new Properties();
            }

            Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
            if ( messages == null )
            {
                messages = new Messages();
            }

            Specifications specifications = this.getModules().getSpecifications( implementation.getIdentifier() );
            if ( specifications == null )
            {
                specifications = new Specifications();
            }

            for ( int i = 0, s0 = specifications.getReference().size(); i < s0; i++ )
            {
                final SpecificationReference r = specifications.getReference().get( i );

                if ( specifications.getSpecification( r.getIdentifier() ) == null && this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "unresolvedSpecification", r.getIdentifier(),
                                                         implementation.getIdentifier() ), null );

                }
            }

            for ( int i = 0, s0 = dependencies.getDependency().size(); i < s0; i++ )
            {
                final Dependency d = dependencies.getDependency().get( i );
                final Specification s = this.getModules().getSpecification( d.getIdentifier() );

                if ( s != null )
                {
                    if ( specifications.getSpecification( s.getIdentifier() ) == null )
                    {
                        specifications.getSpecification().add( s );
                    }
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "unresolvedDependencySpecification", d.getIdentifier(),
                                                         d.getName(), implementation.getIdentifier() ), null );

                }
            }

            this.setClassfileAttribute( javaClass, Dependencies.class.getName(), this.encodeModelObject(
                                        marshaller, of.createDependencies( dependencies ) ) );

            this.setClassfileAttribute( javaClass, Properties.class.getName(), this.encodeModelObject(
                                        marshaller, of.createProperties( properties ) ) );

            this.setClassfileAttribute( javaClass, Messages.class.getName(), this.encodeModelObject(
                                        marshaller, of.createMessages( messages ) ) );

            this.setClassfileAttribute( javaClass, Specifications.class.getName(), this.encodeModelObject(
                                        marshaller, of.createSpecifications( specifications ) ) );

        }
        else if ( this.isLoggable( Level.WARNING ) )
        {
            this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
        }
    }

    /**
     * Validates model objects of class files of the modules of the instance.
     *
     * @param context The model context to use for validating model objects.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code context} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext)
     */
    public final ModelValidationReport validateModelObjects( final ModelContext context ) throws IOException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null )
            {
                report = this.validateModelObjects( this.getModules().getSpecifications(),
                                                    this.getModules().getImplementations(),
                                                    context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "modulesNotFound", this.getModel().getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param context The model context to use for validating model objects.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code module} or {@code context} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext)
     * @see #validateModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext)
     */
    public final ModelValidationReport validateModelObjects( final Module module, final ModelContext context )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null && this.getModules().getModule( module.getName() ) != null )
            {
                report = this.validateModelObjects( module.getSpecifications(), module.getImplementations(),
                                                    context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "moduleNotFound", module.getName() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param context The model context to use for validating model objects.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code specification} or {@code context} is {@code null}.
     *
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final ModelValidationReport validateModelObjects( final Specification specification,
                                                             final ModelContext context ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null
                     && this.getModules().getSpecification( specification.getIdentifier() ) != null )
            {
                if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                {
                    final URL javaClassfile = this.getJavaClassfile( specification.getJavaTypeName(), context );
                    final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                    u.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    final JavaClass javaClass;
                    try ( final InputStream in = javaClassfile.openStream() )
                    {
                        javaClass = new ClassParser( in, javaClassfile.toExternalForm() ).parse();
                    }

                    report = this.validateModelObjects( specification, u, javaClass );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param context The model context to use for validating model objects.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code implementation} or {@code context} is {@code null}.
     *
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Implementation, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final ModelValidationReport validateModelObjects( final Implementation implementation,
                                                             final ModelContext context ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                {
                    final URL javaClassfile = this.getJavaClassfile( implementation.getJavaTypeName(), context );
                    final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                    u.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    final JavaClass javaClass;
                    try ( final InputStream in = javaClassfile.openStream() )
                    {
                        javaClass = new ClassParser( in, javaClassfile.toExternalForm() ).parse();
                    }

                    report = this.validateModelObjects( implementation, u, javaClass );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of the modules of the instance.
     *
     * @param context The model context to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code context} or {@code classesDirectory} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File)
     */
    public final ModelValidationReport validateModelObjects( final ModelContext context, final File classesDirectory )
        throws IOException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null )
            {
                report = this.validateModelObjects( this.getModules().getSpecifications(),
                                                    this.getModules().getImplementations(),
                                                    classesDirectory, context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "modulesNotFound", this.getModel().getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param context The model context to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code module}, {@code context} or {@code classesDirectory} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File)
     * @see #validateModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext, java.io.File)
     */
    public final ModelValidationReport validateModelObjects( final Module module, final ModelContext context,
                                                             final File classesDirectory ) throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null && this.getModules().getModule( module.getName() ) != null )
            {
                report = this.validateModelObjects( module.getSpecifications(), module.getImplementations(),
                                                    classesDirectory, context );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "moduleNotFound", module.getName() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param context The model context to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code specification}, {@code context} or {@code classesDirectory} is
     * {@code null}.
     *
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final ModelValidationReport validateModelObjects( final Specification specification,
                                                             final ModelContext context, final File classesDirectory )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null
                     && this.getModules().getSpecification( specification.getIdentifier() ) != null )
            {
                if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                {
                    final File javaClassfile =
                        this.getJavaClassfile( specification.getJavaTypeName(), classesDirectory );

                    final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                    u.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    report = this.validateModelObjects( specification, u, this.readJavaClass( javaClassfile ) );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of class files of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param context The model context to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code implementation}, {@code context} or {@code classesDirectory} is
     * {@code null}.
     *
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Implementation, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     */
    public final ModelValidationReport validateModelObjects( final Implementation implementation,
                                                             final ModelContext context, final File classesDirectory )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            ModelValidationReport report = null;

            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                {
                    final File javaClassfile =
                        this.getJavaClassfile( implementation.getJavaTypeName(), classesDirectory );

                    final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                    u.setSchema( context.createSchema( this.getModel().getIdentifier() ) );

                    report = this.validateModelObjects( implementation, u, this.readJavaClass( javaClassfile ) );
                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Validates model objects of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param javaClass The java class to validate.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code specification}, {@code unmarshaller} or {@code javaClass} is {@code null}.
     * @throws IOException if validating model objects fails.
     */
    public ModelValidationReport validateModelObjects( final Specification specification,
                                                       final Unmarshaller unmarshaller, final JavaClass javaClass )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        ModelValidationReport report = null;

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "validatingSpecification", specification.getIdentifier() ), null );
        }

        if ( this.getModules() != null && this.getModules().getSpecification( specification.getIdentifier() ) != null )
        {
            report = new ModelValidationReport();
            Specification decoded = null;
            final byte[] bytes = this.getClassfileAttribute( javaClass, Specification.class.getName() );
            if ( bytes != null )
            {
                decoded = this.decodeModelObject( unmarshaller, bytes, Specification.class );
            }

            if ( decoded != null )
            {
                if ( decoded.getMultiplicity() != specification.getMultiplicity() )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "CLASS_ILLEGAL_SPECIFICATION_MULTIPLICITY", Level.SEVERE, getMessage(
                            "illegalMultiplicity", specification.getIdentifier(),
                            specification.getMultiplicity().value(),
                            decoded.getMultiplicity().value() ),
                        new ObjectFactory().createSpecification( specification ) ) );

                }

                if ( decoded.getScope() == null
                         ? specification.getScope() != null
                         : !decoded.getScope().equals( specification.getScope() ) )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "CLASS_ILLEGAL_SPECIFICATION_SCOPE", Level.SEVERE, getMessage(
                            "illegalScope", specification.getIdentifier(),
                            specification.getScope() == null ? "Multiton" : specification.getScope(),
                            decoded.getScope() == null ? "Multiton" : decoded.getScope() ),
                        new ObjectFactory().createSpecification( specification ) ) );

                }

                if ( decoded.getClazz() == null
                         ? specification.getClazz() != null
                         : !decoded.getClazz().equals( specification.getClazz() ) )
                {
                    report.getDetails().add( new ModelValidationReport.Detail(
                        "CLASS_ILLEGAL_SPECIFICATION_CLASS", Level.SEVERE, getMessage(
                            "illegalSpecificationClass", decoded.getIdentifier(),
                            specification.getClazz(), decoded.getClazz() ),
                        new ObjectFactory().createSpecification( specification ) ) );

                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "cannotValidateSpecification", specification.getIdentifier(),
                                                     Specification.class.getName() ), null );

            }
        }
        else if ( this.isLoggable( Level.WARNING ) )
        {
            this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
        }

        return report;
    }

    /**
     * Validates model objects of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param javaClass The java class to validate.
     *
     * @return The report of the validation or {@code null}, if no model objects are found.
     *
     * @throws NullPointerException if {@code implementation}, {@code unmarshaller} or {@code javaClass} is {@code null}.
     * @throws IOException if validating model objects fails.
     */
    public ModelValidationReport validateModelObjects( final Implementation implementation,
                                                       final Unmarshaller unmarshaller, final JavaClass javaClass )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "validatingImplementation", implementation.getIdentifier() ), null );
        }

        try
        {
            ModelValidationReport report = null;
            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                report = new ModelValidationReport();
                Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
                if ( dependencies == null )
                {
                    dependencies = new Dependencies();
                }

                Properties properties = this.getModules().getProperties( implementation.getIdentifier() );
                if ( properties == null )
                {
                    properties = new Properties();
                }

                Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
                if ( messages == null )
                {
                    messages = new Messages();
                }

                Specifications specifications = this.getModules().getSpecifications( implementation.getIdentifier() );
                if ( specifications == null )
                {
                    specifications = new Specifications();
                }

                Dependencies decodedDependencies = null;
                byte[] bytes = this.getClassfileAttribute( javaClass, Dependencies.class.getName() );
                if ( bytes != null )
                {
                    decodedDependencies = this.decodeModelObject( unmarshaller, bytes, Dependencies.class );
                }

                Properties decodedProperties = null;
                bytes = this.getClassfileAttribute( javaClass, Properties.class.getName() );
                if ( bytes != null )
                {
                    decodedProperties = this.decodeModelObject( unmarshaller, bytes, Properties.class );
                }

                Messages decodedMessages = null;
                bytes = this.getClassfileAttribute( javaClass, Messages.class.getName() );
                if ( bytes != null )
                {
                    decodedMessages = this.decodeModelObject( unmarshaller, bytes, Messages.class );
                }

                Specifications decodedSpecifications = null;
                bytes = this.getClassfileAttribute( javaClass, Specifications.class.getName() );
                if ( bytes != null )
                {
                    decodedSpecifications = this.decodeModelObject( unmarshaller, bytes, Specifications.class );
                }

                if ( decodedDependencies != null )
                {
                    for ( int i = 0, s0 = decodedDependencies.getDependency().size(); i < s0; i++ )
                    {
                        final Dependency decodedDependency = decodedDependencies.getDependency().get( i );
                        final Dependency dependency = dependencies.getDependency( decodedDependency.getName() );
                        final Specification s = this.getModules().getSpecification( decodedDependency.getIdentifier() );

                        if ( dependency == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_IMPLEMENTATION_DEPENDENCY", Level.SEVERE, getMessage(
                                    "missingDependency", implementation.getIdentifier(), decodedDependency.getName() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                        else if ( decodedDependency.getImplementationName() != null
                                      && dependency.getImplementationName() == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_DEPENDENCY_IMPLEMENTATION_NAME", Level.SEVERE, getMessage(
                                    "missingDependencyImplementationName", implementation.getIdentifier(),
                                    decodedDependency.getName() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }

                        if ( s != null && s.getVersion() != null && decodedDependency.getVersion() != null
                                 && VersionParser.compare( decodedDependency.getVersion(), s.getVersion() ) > 0 )
                        {
                            final Module moduleOfSpecification =
                                this.getModules().getModuleOfSpecification( s.getIdentifier() );

                            final Module moduleOfImplementation =
                                this.getModules().getModuleOfImplementation( implementation.getIdentifier() );

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_INCOMPATIBLE_IMPLEMENTATION_DEPENDENCY", Level.SEVERE, getMessage(
                                    "incompatibleDependency", javaClass.getClassName(),
                                    moduleOfImplementation == null ? "<>" : moduleOfImplementation.getName(),
                                    s.getIdentifier(),
                                    moduleOfSpecification == null ? "<>" : moduleOfSpecification.getName(),
                                    decodedDependency.getVersion(), s.getVersion() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "cannotValidateImplementation", implementation.getIdentifier(),
                                                         Dependencies.class.getName() ), null );

                }

                if ( decodedProperties != null )
                {
                    for ( int i = 0, s0 = decodedProperties.getProperty().size(); i < s0; i++ )
                    {
                        final Property decodedProperty = decodedProperties.getProperty().get( i );
                        final Property property = properties.getProperty( decodedProperty.getName() );

                        if ( property == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_IMPLEMENTATION_PROPERTY", Level.SEVERE, getMessage(
                                    "missingProperty", implementation.getIdentifier(), decodedProperty.getName() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                        else if ( decodedProperty.getType() == null
                                      ? property.getType() != null
                                      : !decodedProperty.getType().equals( property.getType() ) )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_ILLEGAL_IMPLEMENTATION_PROPERTY", Level.SEVERE, getMessage(
                                    "illegalPropertyType", implementation.getIdentifier(), decodedProperty.getName(),
                                    property.getType() == null ? "<>" : property.getType(),
                                    decodedProperty.getType() == null ? "<>" : decodedProperty.getType() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "cannotValidateImplementation", implementation.getIdentifier(),
                                                         Properties.class.getName() ), null );

                }

                if ( decodedMessages != null )
                {
                    for ( int i = 0, s0 = decodedMessages.getMessage().size(); i < s0; i++ )
                    {
                        final Message decodedMessage = decodedMessages.getMessage().get( i );
                        final Message message = messages.getMessage( decodedMessage.getName() );

                        if ( message == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_IMPLEMENTATION_MESSAGE", Level.SEVERE, getMessage(
                                    "missingMessage", implementation.getIdentifier(), decodedMessage.getName() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "cannotValidateImplementation", implementation.getIdentifier(),
                                                         Messages.class.getName() ), null );

                }

                if ( decodedSpecifications != null )
                {
                    for ( int i = 0, s0 = decodedSpecifications.getSpecification().size(); i < s0; i++ )
                    {
                        final Specification decodedSpecification = decodedSpecifications.getSpecification().get( i );
                        final Specification specification =
                            this.getModules().getSpecification( decodedSpecification.getIdentifier() );

                        if ( specification == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_SPECIFICATION", Level.SEVERE, getMessage(
                                    "missingSpecification", implementation.getIdentifier(),
                                    decodedSpecification.getIdentifier() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                        else
                        {
                            if ( decodedSpecification.getMultiplicity() != specification.getMultiplicity() )
                            {
                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "CLASS_ILLEGAL_SPECIFICATION_MULTIPLICITY", Level.SEVERE, getMessage(
                                        "illegalMultiplicity", specification.getIdentifier(),
                                        specification.getMultiplicity().value(),
                                        decodedSpecification.getMultiplicity().value() ),
                                    new ObjectFactory().createImplementation( implementation ) ) );

                            }

                            if ( decodedSpecification.getScope() == null
                                     ? specification.getScope() != null
                                     : !decodedSpecification.getScope().equals( specification.getScope() ) )
                            {
                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "CLASS_ILLEGAL_SPECIFICATION_SCOPE", Level.SEVERE, getMessage(
                                        "illegalScope", decodedSpecification.getIdentifier(),
                                        specification.getScope() == null ? "Multiton" : specification.getScope(),
                                        decodedSpecification.getScope() == null
                                            ? "Multiton"
                                            : decodedSpecification.getScope() ),
                                    new ObjectFactory().createImplementation( implementation ) ) );

                            }

                            if ( decodedSpecification.getClazz() == null
                                     ? specification.getClazz() != null
                                     : !decodedSpecification.getClazz().equals( specification.getClazz() ) )
                            {
                                report.getDetails().add( new ModelValidationReport.Detail(
                                    "CLASS_ILLEGAL_SPECIFICATION_CLASS", Level.SEVERE, getMessage(
                                        "illegalSpecificationClass", decodedSpecification.getIdentifier(),
                                        specification.getClazz(), decodedSpecification.getClazz() ),
                                    new ObjectFactory().createImplementation( implementation ) ) );

                            }
                        }
                    }

                    for ( int i = 0, s0 = decodedSpecifications.getReference().size(); i < s0; i++ )
                    {
                        final SpecificationReference decodedReference = decodedSpecifications.getReference().get( i );
                        final Specification specification =
                            specifications.getSpecification( decodedReference.getIdentifier() );

                        if ( specification == null )
                        {
                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_MISSING_SPECIFICATION", Level.SEVERE, getMessage(
                                    "missingSpecification", implementation.getIdentifier(),
                                    decodedReference.getIdentifier() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                        else if ( decodedReference.getVersion() != null && specification.getVersion() != null
                                      && VersionParser.compare( decodedReference.getVersion(),
                                                                specification.getVersion() ) != 0 )
                        {
                            final Module moduleOfSpecification =
                                this.getModules().getModuleOfSpecification( decodedReference.getIdentifier() );

                            final Module moduleOfImplementation =
                                this.getModules().getModuleOfImplementation( implementation.getIdentifier() );

                            report.getDetails().add( new ModelValidationReport.Detail(
                                "CLASS_INCOMPATIBLE_IMPLEMENTATION", Level.SEVERE, getMessage(
                                    "incompatibleImplementation", javaClass.getClassName(),
                                    moduleOfImplementation == null ? "<>" : moduleOfImplementation.getName(),
                                    specification.getIdentifier(),
                                    moduleOfSpecification == null ? "<>" : moduleOfSpecification.getName(),
                                    decodedReference.getVersion(), specification.getVersion() ),
                                new ObjectFactory().createImplementation( implementation ) ) );

                        }
                    }
                }
                else if ( this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "cannotValidateImplementation", implementation.getIdentifier(),
                                                         Specifications.class.getName() ), null );

                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }

            return report;
        }
        catch ( final ParseException | TokenMgrError e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of class files of the modules of the instance.
     *
     * @param context The model context to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming model objects.
     *
     * @throws NullPointerException if {@code context}, {@code classesDirectory} or {@code transformers} is
     * {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Module, org.jomc.modlet.ModelContext, java.io.File, java.util.List)
     */
    public final void transformModelObjects( final ModelContext context, final File classesDirectory,
                                             final List<Transformer> transformers ) throws IOException
    {
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( !classesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
        }

        try
        {
            if ( this.getModules() != null )
            {
                final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                final Schema s = context.createSchema( this.getModel().getIdentifier() );
                u.setSchema( s );
                m.setSchema( s );

                this.transformModelObjects( this.getModules().getSpecifications(),
                                            this.getModules().getImplementations(),
                                            u, m, classesDirectory, transformers );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "modulesNotFound", this.getModel().getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of class files of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param context The model context to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code module}, {@code context}, {@code classesDirectory} or {@code transformers}
     * is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Specification, org.jomc.modlet.ModelContext, java.io.File, java.util.List)
     * @see #transformModelObjects(org.jomc.model.Implementation, org.jomc.modlet.ModelContext, java.io.File, java.util.List)
     */
    public final void transformModelObjects( final Module module, final ModelContext context,
                                             final File classesDirectory, final List<Transformer> transformers )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( !classesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
        }

        try
        {
            if ( this.getModules() != null && this.getModules().getModule( module.getName() ) != null )
            {
                final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                final Schema s = context.createSchema( this.getModel().getIdentifier() );
                u.setSchema( s );
                m.setSchema( s );

                this.transformModelObjects( module.getSpecifications(), module.getImplementations(), u, m,
                                            classesDirectory, transformers );

            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "moduleNotFound", module.getName() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of class files of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param context The model context to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code specification}, {@code context}, {@code classesDirectory} or
     * {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Specification, javax.xml.bind.Marshaller, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass, java.util.List)
     */
    public final void transformModelObjects( final Specification specification, final ModelContext context,
                                             final File classesDirectory, final List<Transformer> transformers )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( !classesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getSpecification( specification.getIdentifier() ) != null )
            {
                final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                final Schema s = context.createSchema( this.getModel().getIdentifier() );
                u.setSchema( s );
                m.setSchema( s );

                this.transformModelObjects( specification, m, u, classesDirectory, transformers );
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of class files of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param context The model context to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code implementation}, {@code context}, {@code classesDirectory} or
     * {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Implementation, javax.xml.bind.Marshaller, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass, java.util.List)
     */
    public final void transformModelObjects( final Implementation implementation, final ModelContext context,
                                             final File classesDirectory, final List<Transformer> transformers )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( context == null )
        {
            throw new NullPointerException( "context" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( !classesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                final Unmarshaller u = context.createUnmarshaller( this.getModel().getIdentifier() );
                final Marshaller m = context.createMarshaller( this.getModel().getIdentifier() );
                final Schema s = context.createSchema( this.getModel().getIdentifier() );
                u.setSchema( s );
                m.setSchema( s );

                this.transformModelObjects( implementation, m, u, classesDirectory, transformers );
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }
        }
        catch ( final ModelException e )
        {
            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param javaClass The java class to transform model objects of.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code specification}, {@code marshaller}, {@code unmarshaller},
     * {@code javaClass} or {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     */
    public void transformModelObjects( final Specification specification, final Marshaller marshaller,
                                       final Unmarshaller unmarshaller, final JavaClass javaClass,
                                       final List<Transformer> transformers ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getSpecification( specification.getIdentifier() ) != null )
            {
                Specification decodedSpecification = null;
                final ObjectFactory objectFactory = new ObjectFactory();
                final byte[] bytes = this.getClassfileAttribute( javaClass, Specification.class.getName() );
                if ( bytes != null )
                {
                    decodedSpecification = this.decodeModelObject( unmarshaller, bytes, Specification.class );
                }

                if ( decodedSpecification != null )
                {
                    for ( int i = 0, l = transformers.size(); i < l; i++ )
                    {
                        final JAXBSource source =
                            new JAXBSource( marshaller, objectFactory.createSpecification( decodedSpecification ) );

                        final JAXBResult result = new JAXBResult( unmarshaller );
                        transformers.get( i ).transform( source, result );

                        if ( result.getResult() instanceof JAXBElement<?>
                                 && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Specification )
                        {
                            decodedSpecification = (Specification) ( (JAXBElement<?>) result.getResult() ).getValue();
                        }
                        else
                        {
                            throw new IOException( getMessage(
                                "illegalSpecificationTransformationResult", specification.getIdentifier() ) );

                        }
                    }

                    this.setClassfileAttribute( javaClass, Specification.class.getName(), this.encodeModelObject(
                                                marshaller,
                                                objectFactory.createSpecification( decodedSpecification ) ) );

                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "specificationNotFound", specification.getIdentifier() ), null );
            }
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new IOException( getMessage( e ), e );
        }
        catch ( final TransformerException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getException() != null )
            {
                message = getMessage( e.getException() );
            }

            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Transforms model objects of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param javaClass The java class to transform model object of.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code implementation}, {@code marshaller}, {@code unmarshaller},
     * {@code javaClass} or {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     */
    public void transformModelObjects( final Implementation implementation, final Marshaller marshaller,
                                       final Unmarshaller unmarshaller, final JavaClass javaClass,
                                       final List<Transformer> transformers ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }

        try
        {
            if ( this.getModules() != null
                     && this.getModules().getImplementation( implementation.getIdentifier() ) != null )
            {
                Dependencies decodedDependencies = null;
                byte[] bytes = this.getClassfileAttribute( javaClass, Dependencies.class.getName() );
                if ( bytes != null )
                {
                    decodedDependencies = this.decodeModelObject( unmarshaller, bytes, Dependencies.class );
                }

                Messages decodedMessages = null;
                bytes = this.getClassfileAttribute( javaClass, Messages.class.getName() );
                if ( bytes != null )
                {
                    decodedMessages = this.decodeModelObject( unmarshaller, bytes, Messages.class );
                }

                Properties decodedProperties = null;
                bytes = this.getClassfileAttribute( javaClass, Properties.class.getName() );
                if ( bytes != null )
                {
                    decodedProperties = this.decodeModelObject( unmarshaller, bytes, Properties.class );
                }

                Specifications decodedSpecifications = null;
                bytes = this.getClassfileAttribute( javaClass, Specifications.class.getName() );
                if ( bytes != null )
                {
                    decodedSpecifications = this.decodeModelObject( unmarshaller, bytes, Specifications.class );
                }

                final ObjectFactory of = new ObjectFactory();
                for ( int i = 0, l = transformers.size(); i < l; i++ )
                {
                    final Transformer transformer = transformers.get( i );

                    if ( decodedDependencies != null )
                    {
                        final JAXBSource source =
                            new JAXBSource( marshaller, of.createDependencies( decodedDependencies ) );

                        final JAXBResult result = new JAXBResult( unmarshaller );
                        transformer.transform( source, result );

                        if ( result.getResult() instanceof JAXBElement<?>
                                 && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Dependencies )
                        {
                            decodedDependencies = (Dependencies) ( (JAXBElement<?>) result.getResult() ).getValue();
                        }
                        else
                        {
                            throw new IOException( getMessage(
                                "illegalImplementationTransformationResult", implementation.getIdentifier() ) );

                        }
                    }

                    if ( decodedMessages != null )
                    {
                        final JAXBSource source = new JAXBSource( marshaller, of.createMessages( decodedMessages ) );
                        final JAXBResult result = new JAXBResult( unmarshaller );
                        transformer.transform( source, result );

                        if ( result.getResult() instanceof JAXBElement<?>
                                 && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Messages )
                        {
                            decodedMessages = (Messages) ( (JAXBElement<?>) result.getResult() ).getValue();
                        }
                        else
                        {
                            throw new IOException( getMessage(
                                "illegalImplementationTransformationResult", implementation.getIdentifier() ) );

                        }
                    }

                    if ( decodedProperties != null )
                    {
                        final JAXBSource source =
                            new JAXBSource( marshaller, of.createProperties( decodedProperties ) );

                        final JAXBResult result = new JAXBResult( unmarshaller );
                        transformer.transform( source, result );

                        if ( result.getResult() instanceof JAXBElement<?>
                                 && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Properties )
                        {
                            decodedProperties = (Properties) ( (JAXBElement<?>) result.getResult() ).getValue();
                        }
                        else
                        {
                            throw new IOException( getMessage(
                                "illegalImplementationTransformationResult", implementation.getIdentifier() ) );

                        }
                    }

                    if ( decodedSpecifications != null )
                    {
                        final JAXBSource source =
                            new JAXBSource( marshaller, of.createSpecifications( decodedSpecifications ) );

                        final JAXBResult result = new JAXBResult( unmarshaller );
                        transformer.transform( source, result );

                        if ( result.getResult() instanceof JAXBElement<?>
                                 && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Specifications )
                        {
                            decodedSpecifications = (Specifications) ( (JAXBElement<?>) result.getResult() ).getValue();
                        }
                        else
                        {
                            throw new IOException( getMessage(
                                "illegalImplementationTransformationResult", implementation.getIdentifier() ) );

                        }
                    }
                }

                if ( decodedDependencies != null )
                {
                    this.setClassfileAttribute( javaClass, Dependencies.class.getName(), this.encodeModelObject(
                                                marshaller, of.createDependencies( decodedDependencies ) ) );

                }

                if ( decodedMessages != null )
                {
                    this.setClassfileAttribute( javaClass, Messages.class.getName(), this.encodeModelObject(
                                                marshaller, of.createMessages( decodedMessages ) ) );

                }

                if ( decodedProperties != null )
                {
                    this.setClassfileAttribute( javaClass, Properties.class.getName(), this.encodeModelObject(
                                                marshaller, of.createProperties( decodedProperties ) ) );

                }

                if ( decodedSpecifications != null )
                {
                    this.setClassfileAttribute( javaClass, Specifications.class.getName(), this.encodeModelObject(
                                                marshaller, of.createSpecifications( decodedSpecifications ) ) );

                }
            }
            else if ( this.isLoggable( Level.WARNING ) )
            {
                this.log( Level.WARNING, getMessage( "implementationNotFound", implementation.getIdentifier() ), null );
            }
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new IOException( getMessage( e ), e );
        }
        catch ( final TransformerException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getException() != null )
            {
                message = getMessage( e.getException() );
            }

            throw new IOException( getMessage( e ), e );
        }
    }

    /**
     * Gets an attribute from a java class.
     *
     * @param clazz The java class to get an attribute from.
     * @param attributeName The name of the attribute to get.
     *
     * @return The value of attribute {@code attributeName} of {@code clazz} or {@code null}, if no such attribute
     * exists.
     *
     * @throws NullPointerException if {@code clazz} or {@code attributeName} is {@code null}.
     * @throws IOException if getting the attribute fails.
     *
     * @see JavaClass#getAttributes()
     */
    public byte[] getClassfileAttribute( final JavaClass clazz, final String attributeName ) throws IOException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }
        if ( attributeName == null )
        {
            throw new NullPointerException( "attributeName" );
        }

        final Attribute[] attributes = clazz.getAttributes();

        for ( int i = attributes.length - 1; i >= 0; i-- )
        {
            final Constant constant = clazz.getConstantPool().getConstant( attributes[i].getNameIndex() );

            if ( constant instanceof ConstantUtf8 && attributeName.equals( ( (ConstantUtf8) constant ).getBytes() ) )
            {
                final Unknown unknown = (Unknown) attributes[i];
                return unknown.getBytes();
            }
        }

        return null;
    }

    /**
     * Adds or updates an attribute in a java class.
     *
     * @param clazz The class to update an attribute of.
     * @param attributeName The name of the attribute to update.
     * @param data The new data of the attribute to update the {@code clazz} with.
     *
     * @throws NullPointerException if {@code clazz} or {@code attributeName} is {@code null}.
     * @throws IOException if updating the class file fails.
     *
     * @see JavaClass#getAttributes()
     */
    public void setClassfileAttribute( final JavaClass clazz, final String attributeName, final byte[] data )
        throws IOException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }
        if ( attributeName == null )
        {
            throw new NullPointerException( "attributeName" );
        }

        final byte[] attributeData = data != null ? data : NO_BYTES;

        /*
         * The JavaTM Virtual Machine Specification - Second Edition - Chapter 4.1
         *
         * A Java virtual machine implementation is required to silently ignore any
         * or all attributes in the attributes table of a ClassFile structure that
         * it does not recognize. Attributes not defined in this specification are
         * not allowed to affect the semantics of the class file, but only to
         * provide additional descriptive information (§4.7.1).
         */
        Attribute[] attributes = clazz.getAttributes();

        int attributeIndex = -1;
        int nameIndex = -1;

        for ( int i = attributes.length - 1; i >= 0; i-- )
        {
            final Constant constant = clazz.getConstantPool().getConstant( attributes[i].getNameIndex() );

            if ( constant instanceof ConstantUtf8 && attributeName.equals( ( (ConstantUtf8) constant ).getBytes() ) )
            {
                attributeIndex = i;
                nameIndex = attributes[i].getNameIndex();
            }
        }

        if ( nameIndex == -1 )
        {
            final Constant[] pool = clazz.getConstantPool().getConstantPool();
            final Constant[] tmp = new Constant[ pool.length + 1 ];
            System.arraycopy( pool, 0, tmp, 0, pool.length );
            tmp[pool.length] = new ConstantUtf8( attributeName );
            nameIndex = pool.length;
            clazz.setConstantPool( new ConstantPool( tmp ) );
        }

        final Unknown unknown = new Unknown( nameIndex, attributeData.length, attributeData, clazz.getConstantPool() );

        if ( attributeIndex == -1 )
        {
            final Attribute[] tmp = new Attribute[ attributes.length + 1 ];
            System.arraycopy( attributes, 0, tmp, 0, attributes.length );
            tmp[attributes.length] = unknown;
            attributes = tmp;
        }
        else
        {
            attributes[attributeIndex] = unknown;
        }

        clazz.setAttributes( attributes );
    }

    /**
     * Encodes a model object to a byte array.
     *
     * @param marshaller The marshaller to use for encoding the object.
     * @param modelObject The model object to encode.
     *
     * @return GZIP compressed XML document of {@code modelObject}.
     *
     * @throws NullPointerException if {@code marshaller} or {@code modelObject} is {@code null}.
     * @throws IOException if encoding {@code modelObject} fails.
     *
     * @see #decodeModelObject(javax.xml.bind.Unmarshaller, byte[], java.lang.Class)
     */
    public byte[] encodeModelObject( final Marshaller marshaller, final JAXBElement<? extends ModelObject> modelObject )
        throws IOException
    {
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( modelObject == null )
        {
            throw new NullPointerException( "modelObject" );
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try ( final GZIPOutputStream out = new GZIPOutputStream( baos ) )
        {
            marshaller.marshal( modelObject, out );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new IOException( getMessage( e ), e );
        }

        return baos.toByteArray();
    }

    /**
     * Decodes a model object from a byte array.
     *
     * @param unmarshaller The unmarshaller to use for decoding the object.
     * @param bytes The encoded model object to decode.
     * @param type The class of the type of the encoded model object.
     * @param <T> The type of the encoded model object.
     *
     * @return Model object decoded from {@code bytes}.
     *
     * @throws NullPointerException if {@code unmarshaller}, {@code bytes} or {@code type} is {@code null}.
     * @throws IOException if decoding {@code bytes} fails.
     *
     * @see #encodeModelObject(javax.xml.bind.Marshaller, javax.xml.bind.JAXBElement)
     */
    public <T extends ModelObject> T decodeModelObject( final Unmarshaller unmarshaller, final byte[] bytes,
                                                        final Class<T> type ) throws IOException
    {
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( bytes == null )
        {
            throw new NullPointerException( "bytes" );
        }
        if ( type == null )
        {
            throw new NullPointerException( "type" );
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream( bytes );

        final JAXBElement<T> element;
        try ( final GZIPInputStream in = new GZIPInputStream( bais ) )
        {
            element = (JAXBElement<T>) unmarshaller.unmarshal( in );
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw new IOException( getMessage( e ), e );
        }

        return element.getValue();
    }

    private void commitModelObjects( final Specifications specifications, final Implementations implementations,
                                     final File classesDirectory, final ModelContext context )
        throws IOException, ModelException
    {
        try
        {
            final ThreadLocal<Marshaller> threadLocalMarshaller = new ThreadLocal<>();
            final Schema schema = context.createSchema( this.getModel().getIdentifier() );

            class CommitModelObjectsTask implements Callable<Void>
            {

                final File javaClassfile;

                Specification specification;

                Implementation implementation;

                CommitModelObjectsTask( final File javaClassfile )
                {
                    super();
                    this.javaClassfile = javaClassfile;
                }

                @Override
                public Void call() throws IOException, ModelException
                {
                    Marshaller marshaller = threadLocalMarshaller.get();
                    if ( marshaller == null )
                    {
                        marshaller = context.createMarshaller( getModel().getIdentifier() );
                        marshaller.setSchema( schema );
                        threadLocalMarshaller.set( marshaller );
                    }

                    final JavaClass javaClass = readJavaClass( this.javaClassfile );

                    if ( this.specification != null )
                    {
                        commitModelObjects( this.specification, marshaller, javaClass );
                    }
                    if ( this.implementation != null )
                    {
                        commitModelObjects( this.implementation, marshaller, javaClass );
                    }

                    writeJavaClass( javaClass, this.javaClassfile );
                    return null;
                }

            }

            final Map<String, CommitModelObjectsTask> tasks = new HashMap<>( 512 );

            if ( specifications != null )
            {
                for ( int i = specifications.getSpecification().size() - 1; i >= 0; i-- )
                {
                    final Specification specification = specifications.getSpecification().get( i );

                    if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                    {
                        final File javaClassfile =
                            this.getJavaClassfile( specification.getJavaTypeName(), classesDirectory );

                        CommitModelObjectsTask task = tasks.get( javaClassfile.getAbsolutePath() );

                        if ( task == null )
                        {
                            task = new CommitModelObjectsTask( javaClassfile );
                            tasks.put( javaClassfile.getAbsolutePath(), task );
                        }

                        task.specification = specification;
                    }
                }
            }

            if ( implementations != null )
            {
                for ( int i = implementations.getImplementation().size() - 1; i >= 0; i-- )
                {
                    final Implementation implementation = implementations.getImplementation().get( i );

                    if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                    {
                        final File javaClassfile =
                            this.getJavaClassfile( implementation.getJavaTypeName(), classesDirectory );

                        CommitModelObjectsTask task = tasks.get( javaClassfile.getAbsolutePath() );

                        if ( task == null )
                        {
                            task = new CommitModelObjectsTask( javaClassfile );
                            tasks.put( javaClassfile.getAbsolutePath(), task );
                        }

                        task.implementation = implementation;
                    }
                }
            }

            if ( this.getExecutorService() != null && tasks.size() > 1 )
            {
                for ( final Future<Void> task : this.getExecutorService().invokeAll( tasks.values() ) )
                {
                    task.get();
                }
            }
            else
            {
                for ( final CommitModelObjectsTask task : tasks.values() )
                {
                    task.call();
                }
            }
        }
        catch ( final CancellationException | InterruptedException e )
        {
            throw new IOException( getMessage( e.getCause() ), e.getCause() );
        }
        catch ( final ExecutionException e )
        {
            if ( e.getCause() instanceof ModelException )
            {
                throw (ModelException) e.getCause();
            }
            else if ( e.getCause() instanceof IOException )
            {
                throw (IOException) e.getCause();
            }
            else if ( e.getCause() instanceof RuntimeException )
            {
                // The fork-join framework breaks the exception handling contract of Callable by re-throwing any
                // exception caught using a runtime exception.
                if ( e.getCause().getCause() instanceof ModelException )
                {
                    throw (ModelException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof IOException )
                {
                    throw (IOException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof RuntimeException )
                {
                    throw (RuntimeException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Error )
                {
                    throw (Error) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Exception )
                {
                    // Checked exception not declared to be thrown by the Callable's 'call' method.
                    throw new UndeclaredThrowableException( e.getCause().getCause() );
                }
                else
                {
                    throw (RuntimeException) e.getCause();
                }
            }
            else if ( e.getCause() instanceof Error )
            {
                throw (Error) e.getCause();
            }
            else
            {
                // Checked exception not declared to be thrown by the Callable's 'call' method.
                throw new UndeclaredThrowableException( e.getCause() );
            }
        }
    }

    private ModelValidationReport validateModelObjects( final Specifications specifications,
                                                        final Implementations implementations,
                                                        final File classesDirectory,
                                                        final ModelContext context )
        throws IOException, ModelException
    {
        try
        {
            final ThreadLocal<Unmarshaller> threadLocalUnmarshaller = new ThreadLocal<>();
            final Schema schema = context.createSchema( this.getModel().getIdentifier() );

            class ValidateModelObjectsTask implements Callable<ModelValidationReport>
            {

                final File javaClassfile;

                Specification specification;

                Implementation implementation;

                ValidateModelObjectsTask( final File javaClassfile )
                {
                    super();
                    this.javaClassfile = javaClassfile;
                }

                @Override
                public ModelValidationReport call() throws IOException, ModelException
                {
                    Unmarshaller unmarshaller = threadLocalUnmarshaller.get();
                    if ( unmarshaller == null )
                    {
                        unmarshaller = context.createUnmarshaller( getModel().getIdentifier() );
                        unmarshaller.setSchema( schema );
                        threadLocalUnmarshaller.set( unmarshaller );
                    }

                    final ModelValidationReport report = new ModelValidationReport();
                    final JavaClass javaClass = readJavaClass( this.javaClassfile );

                    if ( this.specification != null )
                    {
                        report.getDetails().
                            addAll( validateModelObjects( this.specification, unmarshaller, javaClass ).getDetails() );

                    }
                    if ( this.implementation != null )
                    {
                        report.getDetails().
                            addAll( validateModelObjects( this.implementation, unmarshaller, javaClass ).getDetails() );

                    }

                    return report;
                }

            }

            final Map<String, ValidateModelObjectsTask> tasks = new HashMap<>();

            if ( specifications != null )
            {
                for ( int i = 0, s0 = specifications.getSpecification().size(); i < s0; i++ )
                {
                    final Specification specification = specifications.getSpecification().get( i );

                    if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                    {
                        final File javaClassfile =
                            this.getJavaClassfile( specification.getJavaTypeName(), classesDirectory );

                        ValidateModelObjectsTask task = tasks.get( javaClassfile.getAbsolutePath() );

                        if ( task == null )
                        {
                            task = new ValidateModelObjectsTask( javaClassfile );
                            tasks.put( javaClassfile.getAbsolutePath(), task );
                        }

                        task.specification = specification;
                    }
                }
            }

            if ( implementations != null )
            {
                for ( int i = 0, s0 = implementations.getImplementation().size(); i < s0; i++ )
                {
                    final Implementation implementation = implementations.getImplementation().get( i );

                    if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                    {
                        final File javaClassfile =
                            this.getJavaClassfile( implementation.getJavaTypeName(), classesDirectory );

                        ValidateModelObjectsTask task = tasks.get( javaClassfile.getAbsolutePath() );

                        if ( task == null )
                        {
                            task = new ValidateModelObjectsTask( javaClassfile );
                            tasks.put( javaClassfile.getAbsolutePath(), task );
                        }

                        task.implementation = implementation;
                    }
                }
            }

            final ModelValidationReport report = new ModelValidationReport();

            if ( this.getExecutorService() != null && tasks.size() > 1 )
            {
                for ( final Future<ModelValidationReport> task : this.getExecutorService().invokeAll( tasks.values() ) )
                {
                    report.getDetails().addAll( task.get().getDetails() );
                }
            }
            else
            {
                for ( final ValidateModelObjectsTask task : tasks.values() )
                {
                    report.getDetails().addAll( task.call().getDetails() );
                }
            }

            return report;
        }
        catch ( final CancellationException | InterruptedException e )
        {
            throw new IOException( getMessage( e.getCause() ), e.getCause() );
        }
        catch ( final ExecutionException e )
        {
            if ( e.getCause() instanceof ModelException )
            {
                throw (ModelException) e.getCause();
            }
            else if ( e.getCause() instanceof IOException )
            {
                throw (IOException) e.getCause();
            }
            else if ( e.getCause() instanceof RuntimeException )
            {
                // The fork-join framework breaks the exception handling contract of Callable by re-throwing any
                // exception caught using a runtime exception.
                if ( e.getCause().getCause() instanceof ModelException )
                {
                    throw (ModelException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof IOException )
                {
                    throw (IOException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof RuntimeException )
                {
                    throw (RuntimeException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Error )
                {
                    throw (Error) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Exception )
                {
                    // Checked exception not declared to be thrown by the Callable's 'call' method.
                    throw new UndeclaredThrowableException( e.getCause().getCause() );
                }
                else
                {
                    throw (RuntimeException) e.getCause();
                }
            }
            else if ( e.getCause() instanceof Error )
            {
                throw (Error) e.getCause();
            }
            else
            {
                // Checked exception not declared to be thrown by the Callable's 'call' method.
                throw new UndeclaredThrowableException( e.getCause() );
            }
        }
    }

    private ModelValidationReport validateModelObjects( final Specifications specifications,
                                                        final Implementations implementations,
                                                        final ModelContext context )
        throws IOException, ModelException
    {
        final ThreadLocal<Unmarshaller> threadLocalUnmarshaller = new ThreadLocal<>();
        final Schema schema = context.createSchema( this.getModel().getIdentifier() );

        class ValidateModelObjectsTask implements Callable<ModelValidationReport>
        {

            final URL javaClassfile;

            Specification specification;

            Implementation implementation;

            ValidateModelObjectsTask( final URL javaClassfile )
            {
                super();
                this.javaClassfile = javaClassfile;
            }

            @Override
            public ModelValidationReport call() throws IOException, ModelException
            {
                Unmarshaller unmarshaller = threadLocalUnmarshaller.get();
                if ( unmarshaller == null )
                {
                    unmarshaller = context.createUnmarshaller( getModel().getIdentifier() );
                    unmarshaller.setSchema( schema );
                    threadLocalUnmarshaller.set( unmarshaller );
                }

                final JavaClass javaClass;
                try ( final InputStream in = this.javaClassfile.openStream() )
                {
                    javaClass = new ClassParser( in, this.javaClassfile.toExternalForm() ).parse();
                }

                final ModelValidationReport report = new ModelValidationReport();

                if ( this.specification != null )
                {
                    report.getDetails().
                        addAll( validateModelObjects( this.specification, unmarshaller, javaClass ).getDetails() );

                }
                if ( this.implementation != null )
                {
                    report.getDetails().
                        addAll( validateModelObjects( this.implementation, unmarshaller, javaClass ).getDetails() );

                }

                return report;
            }

        }

        final Map<String, ValidateModelObjectsTask> tasks = new HashMap<>();

        if ( specifications != null )
        {
            for ( int i = 0, s0 = specifications.getSpecification().size(); i < s0; i++ )
            {
                final Specification specification = specifications.getSpecification().get( i );

                if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
                {
                    final URL javaClassfile =
                        this.getJavaClassfile( specification.getJavaTypeName(), context );

                    ValidateModelObjectsTask task = tasks.get( javaClassfile.toExternalForm() );

                    if ( task == null )
                    {
                        task = new ValidateModelObjectsTask( javaClassfile );
                        tasks.put( javaClassfile.toExternalForm(), task );
                    }

                    task.specification = specification;
                }
            }
        }

        if ( implementations != null )
        {
            for ( int i = 0, s0 = implementations.getImplementation().size(); i < s0; i++ )
            {
                final Implementation implementation = implementations.getImplementation().get( i );

                if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
                {
                    final URL javaClassfile =
                        this.getJavaClassfile( implementation.getJavaTypeName(), context );

                    ValidateModelObjectsTask task = tasks.get( javaClassfile.toExternalForm() );

                    if ( task == null )
                    {
                        task = new ValidateModelObjectsTask( javaClassfile );
                        tasks.put( javaClassfile.toExternalForm(), task );
                    }

                    task.implementation = implementation;
                }
            }
        }

        try
        {
            final ModelValidationReport report = new ModelValidationReport();

            if ( this.getExecutorService() != null && tasks.size() > 1 )
            {
                for ( final Future<ModelValidationReport> task
                          : this.getExecutorService().invokeAll( tasks.values() ) )
                {
                    report.getDetails().addAll( task.get().getDetails() );
                }
            }
            else
            {
                for ( final ValidateModelObjectsTask task : tasks.values() )
                {
                    report.getDetails().addAll( task.call().getDetails() );
                }
            }

            return report;
        }
        catch ( final CancellationException | InterruptedException e )
        {
            throw new IOException( getMessage( e.getCause() ), e.getCause() );
        }
        catch ( final ExecutionException e )
        {
            if ( e.getCause() instanceof ModelException )
            {
                throw (ModelException) e.getCause();
            }
            else if ( e.getCause() instanceof IOException )
            {
                throw (IOException) e.getCause();
            }
            else if ( e.getCause() instanceof RuntimeException )
            {
                // The fork-join framework breaks the exception handling contract of Callable by re-throwing any
                // exception caught using a runtime exception.
                if ( e.getCause().getCause() instanceof ModelException )
                {
                    throw (ModelException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof IOException )
                {
                    throw (IOException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof RuntimeException )
                {
                    throw (RuntimeException) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Error )
                {
                    throw (Error) e.getCause().getCause();
                }
                else if ( e.getCause().getCause() instanceof Exception )
                {
                    // Checked exception not declared to be thrown by the Callable's 'call' method.
                    throw new UndeclaredThrowableException( e.getCause().getCause() );
                }
                else
                {
                    throw (RuntimeException) e.getCause();
                }
            }
            else if ( e.getCause() instanceof Error )
            {
                throw (Error) e.getCause();
            }
            else
            {
                // Checked exception not declared to be thrown by the Callable's 'call' method.
                throw new UndeclaredThrowableException( e.getCause() );
            }
        }
    }

    private void transformModelObjects( final Specifications specifications, final Implementations implementations,
                                        final Unmarshaller unmarshaller, final Marshaller marshaller,
                                        final File classesDirectory, final List<Transformer> transformers )
        throws IOException, ModelObjectException
    {
        if ( specifications != null )
        {
            for ( int i = 0, s0 = specifications.getSpecification().size(); i < s0; i++ )
            {
                this.transformModelObjects( specifications.getSpecification().get( i ), marshaller, unmarshaller,
                                            classesDirectory, transformers );

            }
        }

        if ( implementations != null )
        {
            for ( int i = 0, s0 = implementations.getImplementation().size(); i < s0; i++ )
            {
                this.transformModelObjects( implementations.getImplementation().get( i ), marshaller, unmarshaller,
                                            classesDirectory, transformers );

            }
        }
    }

    private void transformModelObjects( final Specification specification, final Marshaller marshaller,
                                        final Unmarshaller unmarshaller, final File classesDirectory,
                                        final List<Transformer> transformers ) throws IOException, ModelObjectException
    {
        if ( specification.isClassDeclaration() && specification.getJavaTypeName() != null )
        {
            final String classLocation =
                specification.getJavaTypeName().getClassName().replace( '.', File.separatorChar ) + ".class";

            final File classFile = new File( classesDirectory, classLocation );

            if ( !classesDirectory.isDirectory() )
            {
                throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
            }
            if ( !classFile.isFile() )
            {
                throw new IOException( getMessage( "fileNotFound", classFile.getAbsolutePath() ) );
            }
            if ( !( classFile.canRead() && classFile.canWrite() ) )
            {
                throw new IOException( getMessage( "fileAccessDenied", classFile.getAbsolutePath() ) );
            }

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, getMessage( "transforming", classFile.getAbsolutePath() ), null );
            }

            final JavaClass javaClass = this.readJavaClass( classFile );
            this.transformModelObjects( specification, marshaller, unmarshaller, javaClass, transformers );
            this.writeJavaClass( javaClass, classFile );
        }
    }

    private void transformModelObjects( final Implementation implementation, final Marshaller marshaller,
                                        final Unmarshaller unmarshaller, final File classesDirectory,
                                        final List<Transformer> transformers ) throws IOException, ModelObjectException
    {
        if ( implementation.isClassDeclaration() && implementation.getJavaTypeName() != null )
        {
            final String classLocation =
                implementation.getJavaTypeName().getClassName().replace( '.', File.separatorChar ) + ".class";

            final File classFile = new File( classesDirectory, classLocation );

            if ( !classesDirectory.isDirectory() )
            {
                throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
            }
            if ( !classFile.isFile() )
            {
                throw new IOException( getMessage( "fileNotFound", classFile.getAbsolutePath() ) );
            }
            if ( !( classFile.canRead() && classFile.canWrite() ) )
            {
                throw new IOException( getMessage( "fileAccessDenied", classFile.getAbsolutePath() ) );
            }

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, getMessage( "transforming", classFile.getAbsolutePath() ), null );
            }

            final JavaClass javaClass = this.readJavaClass( classFile );
            this.transformModelObjects( implementation, marshaller, unmarshaller, javaClass, transformers );
            this.writeJavaClass( javaClass, classFile );
        }
    }

    private File getJavaClassfile( final JavaTypeName javaTypeName, final File classesDirectory ) throws IOException
    {
        final String classLocation = javaTypeName.getClassName().replace( '.', File.separatorChar ) + ".class";
        final File classFile = new File( classesDirectory, classLocation );

        if ( !classesDirectory.isDirectory() )
        {
            throw new IOException( getMessage( "directoryNotFound", classesDirectory.getAbsolutePath() ) );
        }
        if ( !classFile.isFile() )
        {
            throw new IOException( getMessage( "fileNotFound", classFile.getAbsolutePath() ) );
        }
        if ( !( classFile.canRead() && classFile.canWrite() ) )
        {
            throw new IOException( getMessage( "fileAccessDenied", classFile.getAbsolutePath() ) );
        }

        return classFile;
    }

    private URL getJavaClassfile( final JavaTypeName javaTypeName, final ModelContext context )
        throws IOException, ModelException
    {
        final String javaClassfileLocation = javaTypeName.getClassName().replace( '.', '/' ) + ".class";
        final URL javaClassfileUrl = context.findResource( javaClassfileLocation );

        if ( javaClassfileUrl == null )
        {
            throw new IOException( getMessage( "resourceNotFound", javaClassfileLocation ) );
        }

        return javaClassfileUrl;
    }

    private JavaClass readJavaClass( final File classFile ) throws IOException
    {
        try ( final FileInputStream in = new FileInputStream( classFile );
              final FileLock fileLock = in.getChannel().lock( 0, classFile.length(), true ) )
        {
            return new ClassParser( in, classFile.getAbsolutePath() ).parse();
        }
    }

    private void writeJavaClass( final JavaClass javaClass, final File classFile ) throws IOException
    {
        try ( final FileOutputStream out = new FileOutputStream( classFile );
              final FileLock fileLock = out.getChannel().lock() )
        {
            javaClass.dump( out );
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ClassFileProcessor.class.getName().replace( '.', '/' ) ).getString( key ), arguments );

    }

    private static String getMessage( final Throwable t )
    {
        return t != null
                   ? t.getMessage() != null && t.getMessage().trim().length() > 0
                         ? t.getMessage()
                         : getMessage( t.getCause() )
                   : null;

    }

}
