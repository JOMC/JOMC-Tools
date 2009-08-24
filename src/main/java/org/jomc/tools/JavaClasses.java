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
package org.jomc.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Unknown;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Messages;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.xml.sax.SAXException;

/**
 * Manages Java classes.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #commitClasses(java.io.File) }</li>
 * <li>{@link #commitClasses(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #commitClasses(org.jomc.model.Specification, java.io.File) }</li>
 * <li>{@link #commitClasses(org.jomc.model.Implementation, java.io.File) }</li>
 * <li>{@link #validateClasses(java.io.File) }</li>
 * <li>{@link #validateClasses(java.lang.ClassLoader) }</li>
 * <li>{@link #validateClasses(org.jomc.model.Module, java.io.File) }</li>
 * <li>{@link #validateClasses(org.jomc.model.Module, java.lang.ClassLoader) }</li>
 * <li>{@link #validateClasses(org.jomc.model.Specification, org.apache.bcel.classfile.JavaClass) }</li>
 * <li>{@link #validateClasses(org.jomc.model.Implementation, org.apache.bcel.classfile.JavaClass) }</li>
 * <li>{@link #transformClasses(java.io.File, javax.xml.transform.Transformer) }</li>
 * <li>{@link #transformClasses(org.jomc.model.Module, java.io.File, javax.xml.transform.Transformer) }</li>
 * <li>{@link #transformClasses(org.jomc.model.Specification, org.apache.bcel.classfile.JavaClass, javax.xml.transform.Transformer) }</li>
 * <li>{@link #transformClasses(org.jomc.model.Implementation, org.apache.bcel.classfile.JavaClass, javax.xml.transform.Transformer) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class JavaClasses extends JomcTool
{

    /** Creates a new {@code JavaClasses} instance. */
    public JavaClasses()
    {
        super();
    }

    /**
     * Creates a new {@code JavaClasses} instance taking a {@code JavaClasses} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaClasses( final JavaClasses tool )
    {
        super( tool );
    }

    /**
     * Commits meta-data of the modules of the instance to compiled Java classes.
     *
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     *
     * @see #commitClasses(org.jomc.model.Module, java.io.File)
     */
    public void commitClasses( final File classesDirectory ) throws IOException
    {
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.commitClasses( m, classesDirectory );
        }
    }

    /**
     * Commits meta-data of a given module of the modules of the instance to compiled Java classes.
     *
     * @param module The module to process.
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code module} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     *
     * @see #commitClasses(org.jomc.model.Specification, java.io.File)
     * @see #commitClasses(org.jomc.model.Implementation, java.io.File)
     */
    public void commitClasses( final Module module, final File classesDirectory ) throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.commitClasses( s, classesDirectory );
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.commitClasses( i, classesDirectory );
            }
        }
    }

    /**
     * Commits meta-data of a given specification of the modules of the instance to compiled Java classes.
     *
     * @param specification The specification to process.
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code specification} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitClasses( final Specification specification, final File classesDirectory ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            final String classLocation = specification.getIdentifier().replace( '.', File.separatorChar ) + ".class";
            final File classFile = new File( classesDirectory, classLocation );
            final JavaClass javaClass = this.getJavaClass( classFile );
            this.setClassfileAttribute( javaClass, Specification.class.getName(),
                                        this.encodeSpecification( specification ) );

            javaClass.dump( classFile );
            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                {
                    classFile.getAbsolutePath()
                } ), null );

        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Commits meta-data of a given implementation of the modules of the instance to compiled Java classes.
     *
     * @param implementation The implementation to process.
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code implementation} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitClasses( final Implementation implementation, final File classesDirectory ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        try
        {
            if ( this.isJavaClassDeclaration( implementation ) )
            {
                final Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
                final Properties properties = this.getModules().getProperties( implementation.getIdentifier() );
                final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
                final Specifications specifications = new Specifications();
                final List<SpecificationReference> specificationReferences =
                    this.getModules().getSpecifications( implementation.getIdentifier() );

                if ( specificationReferences != null )
                {
                    for ( SpecificationReference r : specificationReferences )
                    {
                        final Specification s = this.getModules().getSpecification( r.getIdentifier() );
                        if ( s != null )
                        {
                            if ( specifications.getSpecification( s.getIdentifier() ) != null )
                            {
                                specifications.getSpecification().add( s );
                            }
                        }
                        else
                        {
                            this.log( Level.WARNING, this.getMessage( "unresolvedSpecification", new Object[]
                                {
                                    r.getIdentifier(), implementation.getIdentifier()
                                } ), null );

                        }
                    }
                }

                if ( dependencies != null )
                {
                    for ( Dependency d : dependencies.getDependency() )
                    {
                        final Specification s = this.getModules().getSpecification( d.getIdentifier() );
                        if ( s != null )
                        {
                            if ( specifications.getSpecification( s.getIdentifier() ) != null )
                            {
                                specifications.getSpecification().add( s );
                            }
                        }
                        else
                        {
                            this.log( Level.WARNING, this.getMessage( "unresolvedDependencySpecification", new Object[]
                                {
                                    d.getIdentifier(), d.getName(), implementation.getIdentifier()
                                } ), null );

                        }
                    }
                }

                final String classLocation = implementation.getClazz().replace( '.', File.separatorChar ) + ".class";
                final File classFile = new File( classesDirectory, classLocation );
                final JavaClass javaClass = this.getJavaClass( classFile );

                this.setClassfileAttribute( javaClass, Dependencies.class.getName(), this.encodeDependencies(
                    dependencies == null ? new Dependencies() : dependencies ) );

                this.setClassfileAttribute( javaClass, Properties.class.getName(), this.encodeProperties(
                    properties == null ? new Properties() : properties ) );

                this.setClassfileAttribute( javaClass, Messages.class.getName(), this.encodeMessages(
                    messages == null ? new Messages() : messages ) );

                this.setClassfileAttribute( javaClass, Specifications.class.getName(), this.encodeSpecifications(
                    specifications == null ? new Specifications() : specifications ) );

                javaClass.dump( classFile );
                this.log( Level.INFO, this.getMessage( "writing", new Object[]
                    {
                        classFile.getAbsolutePath()
                    } ), null );

            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Validates compiled Java classes against the modules of the instance.
     *
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code classesDirectory} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     *
     * @see #validateClasses(org.jomc.model.Module, java.io.File)
     */
    public void validateClasses( final File classesDirectory ) throws IOException, ModelException
    {
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();
        ModelException thrown = null;

        for ( Module m : this.getModules().getModule() )
        {
            try
            {
                this.validateClasses( m, classesDirectory );
            }
            catch ( ModelException e )
            {
                thrown = e;
                details.addAll( e.getDetails() );
            }
        }


        if ( !details.isEmpty() )
        {
            final ModelException modelException = new ModelException();
            modelException.getDetails().addAll( details );
            throw modelException;
        }
        if ( thrown != null )
        {
            throw thrown;
        }
    }

    /**
     * Validates compiled Java classes against the modules of the instance.
     *
     * @param classLoader The class loader to search for classes.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     *
     * @see #validateClasses(org.jomc.model.Module, java.lang.ClassLoader)
     */
    public void validateClasses( final ClassLoader classLoader ) throws IOException, ModelException
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();
        ModelException thrown = null;

        for ( Module m : this.getModules().getModule() )
        {
            try
            {
                this.validateClasses( m, classLoader );
            }
            catch ( ModelException e )
            {
                thrown = e;
                details.addAll( e.getDetails() );
            }
        }

        if ( !details.isEmpty() )
        {
            final ModelException modelException = new ModelException();
            modelException.getDetails().addAll( details );
            throw modelException;
        }
        if ( thrown != null )
        {
            throw thrown;
        }
    }

    /**
     * Validates compiled Java classes against a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param classesDirectory The directory holding the compiled class files.
     *
     * @throws NullPointerException if {@code module} or {@code classesDirectory} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     *
     * @see #validateClasses(org.jomc.model.Specification, org.apache.bcel.classfile.JavaClass)
     * @see #validateClasses(org.jomc.model.Implementation, org.apache.bcel.classfile.JavaClass)
     */
    public void validateClasses( final Module module, final File classesDirectory ) throws IOException, ModelException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();
        ModelException thrown = null;

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                final File classFile = new File( classesDirectory, classLocation );

                try
                {
                    this.validateClasses( s, this.getJavaClass( classFile ) );
                }
                catch ( ModelException e )
                {
                    thrown = e;
                    details.addAll( e.getDetails() );
                }
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( this.isJavaClassDeclaration( i ) )
                {
                    final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );
                    final JavaClass javaClass = this.getJavaClass( classFile );

                    try
                    {
                        this.validateClasses( i, javaClass );
                    }
                    catch ( ModelException e )
                    {
                        thrown = e;
                        details.addAll( e.getDetails() );
                    }
                }
            }
        }

        if ( !details.isEmpty() )
        {
            final ModelException modelException = new ModelException();
            modelException.getDetails().addAll( details );
            throw modelException;
        }
        if ( thrown != null )
        {
            throw thrown;
        }
    }

    /**
     * Validates compiled Java classes against a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param classLoader The class loader to search for classes.
     *
     * @throws NullPointerException if {@code module} or {@code classLoader} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     *
     * @see #validateClasses(org.jomc.model.Specification, org.apache.bcel.classfile.JavaClass)
     * @see #validateClasses(org.jomc.model.Implementation, org.apache.bcel.classfile.JavaClass)
     */
    public void validateClasses( final Module module, final ClassLoader classLoader ) throws IOException, ModelException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();
        ModelException thrown = null;

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                final URL classUrl = classLoader.getResource( classLocation );

                if ( classUrl != null )
                {
                    try
                    {
                        this.validateClasses( s, this.getJavaClass( classUrl, classLocation ) );
                    }
                    catch ( ModelException e )
                    {
                        thrown = e;
                        details.addAll( e.getDetails() );
                    }
                }
                else
                {
                    this.log( Level.WARNING, this.getMessage( "missingClassfile", new Object[]
                        {
                            s.getIdentifier()
                        } ), null );

                }
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( this.isJavaClassDeclaration( i ) )
                {
                    final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final URL classUrl = classLoader.getResource( classLocation );

                    if ( classUrl != null )
                    {
                        try
                        {
                            this.validateClasses( i, this.getJavaClass( classUrl, classLocation ) );
                        }
                        catch ( ModelException e )
                        {
                            thrown = e;
                            details.addAll( e.getDetails() );
                        }
                    }
                    else
                    {
                        this.log( Level.WARNING, this.getMessage( "missingClassfile", new Object[]
                            {
                                i.getClazz()
                            } ), null );

                    }
                }
            }
        }

        if ( !details.isEmpty() )
        {
            final ModelException modelException = new ModelException();
            modelException.getDetails().addAll( details );
            throw modelException;
        }
        if ( thrown != null )
        {
            throw thrown;
        }
    }

    /**
     * Validates compiled Java classes against a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param javaClass The class to validate.
     *
     * @throws NullPointerException if {@code specification} or {@code javaClass} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     */
    public void validateClasses( final Specification specification, final JavaClass javaClass )
        throws IOException, ModelException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        try
        {
            final Specification decoded = this.decodeSpecification( javaClass );

            if ( decoded != null )
            {
                final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();

                if ( decoded.getMultiplicity() != specification.getMultiplicity() )
                {
                    details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                        "illegalMultiplicity", new Object[]
                        {
                            specification.getIdentifier(), specification.getMultiplicity().value(),
                            decoded.getMultiplicity().value()
                        } ) ) );

                }

                if ( !details.isEmpty() )
                {
                    final ModelException modelException = new ModelException();
                    modelException.getDetails().addAll( details );
                    throw modelException;
                }
                else
                {
                    this.log( Level.INFO, this.getMessage( "validatedClass", new Object[]
                        {
                            specification.getIdentifier()
                        } ), null );

                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        specification.getIdentifier(), Specification.class.getName()
                    } ), null );

            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Validates compiled Java classes against a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param javaClass The class to validate.
     *
     * @throws NullPointerException if {@code implementation} or {@code javaClass} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if invalid classes are found.
     */
    public void validateClasses( final Implementation implementation, final JavaClass javaClass )
        throws IOException, ModelException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }

        try
        {
            final Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
            final Dependencies decodedDependencies = this.decodeDependencies( javaClass );
            final Properties properties = this.getModules().getProperties( implementation.getIdentifier() );
            final Properties decodedProperties = this.decodeProperties( javaClass );
            final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
            final Messages decodedMessages = this.decodeMessages( javaClass );
            final Specifications decodedSpecifications = this.decodeSpecifications( javaClass );
            final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();

            if ( decodedDependencies != null )
            {
                for ( Dependency decodedDependency : decodedDependencies.getDependency() )
                {
                    final Dependency dependency =
                        dependencies == null ? null : dependencies.getDependency( decodedDependency.getName() );

                    if ( dependency == null )
                    {
                        details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                            "missingDependency", new Object[]
                            {
                                implementation.getIdentifier(), decodedDependency.getName()
                            } ) ) );

                    }
                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        implementation.getClazz(), Dependencies.class.getName()
                    } ), null );

            }

            if ( decodedProperties != null )
            {
                for ( Property decodedProperty : decodedProperties.getProperty() )
                {
                    final Property property =
                        properties == null ? null : properties.getProperty( decodedProperty.getName() );

                    if ( property == null )
                    {
                        details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                            "missingProperty", new Object[]
                            {
                                implementation.getIdentifier(), decodedProperty.getName()
                            } ) ) );

                    }
                    else
                    {
                        if ( decodedProperty.getType() == null
                             ? property.getType() != null
                             : !decodedProperty.getType().equals( property.getType() ) )
                        {
                            details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                "illegalPropertyType", new Object[]
                                {
                                    implementation.getIdentifier(), decodedProperty.getName(),
                                    property.getType() == null ? "default" : property.getType(),
                                    decodedProperty.getType() == null ? "default" : decodedProperty.getType()
                                } ) ) );

                        }
                    }
                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        implementation.getClazz(), Properties.class.getName()
                    } ), null );

            }

            if ( decodedMessages != null )
            {
                for ( Message decodedMessage : decodedMessages.getMessage() )
                {
                    final Message message =
                        messages == null ? null : messages.getMessage( decodedMessage.getName() );

                    if ( message == null )
                    {
                        details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                            "missingMessage", new Object[]
                            {
                                implementation.getIdentifier(), decodedMessage.getName()
                            } ) ) );

                    }
                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        implementation.getClazz(), Messages.class.getName()
                    } ), null );

            }

            if ( decodedSpecifications != null )
            {
                for ( Specification decodedSpecification : decodedSpecifications.getSpecification() )
                {
                    final Specification specification = this.getModules().getSpecification(
                        decodedSpecification.getIdentifier() );

                    if ( specification == null )
                    {
                        details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                            "missingSpecification", new Object[]
                            {
                                implementation.getIdentifier(), decodedSpecification.getIdentifier()
                            } ) ) );

                    }
                    else
                    {
                        if ( decodedSpecification.getMultiplicity() != specification.getMultiplicity() )
                        {
                            details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                "illegalMultiplicity", new Object[]
                                {
                                    specification.getIdentifier(), specification.getMultiplicity().value(),
                                    decodedSpecification.getMultiplicity().value()
                                } ) ) );

                        }
                    }
                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        implementation.getClazz(), Specifications.class.getName()
                    } ), null );

            }

            if ( !details.isEmpty() )
            {
                final ModelException modelException = new ModelException();
                modelException.getDetails().addAll( details );
                throw modelException;
            }
            else
            {
                this.log( Level.INFO, this.getMessage( "validatedClass", new Object[]
                    {
                        implementation.getClazz()
                    } ), null );

            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Transforms committed meta-data of compiled Java classes of the modules of the instance.
     *
     * @param classesDirectory The directory holding the compiled class files.
     * @param transformer The transformer to use for transforming the classes.
     *
     * @throws NullPointerException if {@code classesDirectory} or {@code transformer} is {@code null}.
     * @throws IOException if accessing class files fails.
     * @throws TransformerException if transforming class files fails.
     *
     * @see #transformClasses(org.jomc.model.Module, java.io.File, javax.xml.transform.Transformer)
     */
    public void transformClasses( final File classesDirectory, final Transformer transformer )
        throws IOException, TransformerException
    {
        if ( transformer == null )
        {
            throw new NullPointerException( "transformer" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.transformClasses( m, classesDirectory, transformer );
        }
    }

    /**
     * Transforms committed meta-data of compiled Java classes of a given module of the modules of the instance.
     *
     * @param module The module to process.
     * @param classesDirectory The directory holding the compiled class files.
     * @param transformer The transformer to use for transforming the classes.
     *
     * @throws NullPointerException if {@code module}, {@code classesDirectory} or {@code transformer} is {@code null}.
     * @throws IOException if accessing class files fails.
     * @throws TransformerException if transforming class files fails.
     *
     * @see #transformClasses(org.jomc.model.Specification, org.apache.bcel.classfile.JavaClass, javax.xml.transform.Transformer)
     * @see #transformClasses(org.jomc.model.Implementation, org.apache.bcel.classfile.JavaClass, javax.xml.transform.Transformer)
     */
    public void transformClasses( final Module module, final File classesDirectory, final Transformer transformer )
        throws IOException, TransformerException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( transformer == null )
        {
            throw new NullPointerException( "transformer" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                final File classFile = new File( classesDirectory, classLocation );
                final JavaClass javaClass = this.getJavaClass( classFile );
                this.transformClasses( s, javaClass, transformer );
                javaClass.dump( classFile );
                this.log( Level.INFO, this.getMessage( "writing", new Object[]
                    {
                        classFile.getAbsolutePath()
                    } ), null );

            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( this.isJavaClassDeclaration( i ) )
                {
                    final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );
                    final JavaClass javaClass = this.getJavaClass( classFile );
                    this.transformClasses( i, javaClass, transformer );
                    javaClass.dump( classFile );
                    this.log( Level.INFO, this.getMessage( "writing", new Object[]
                        {
                            classFile.getAbsolutePath()
                        } ), null );

                }
            }
        }
    }

    /**
     * Transforms committed meta-data of compiled Java classes of a given specification of the modules of the instance.
     *
     * @param specification The specification to process.
     * @param javaClass The java class to process.
     * @param transformer The transformer to use for transforming the classes.
     *
     * @throws NullPointerException if {@code specification}, {@code javaClass} or {@code transformer} is {@code null}.
     * @throws IOException if accessing class files fails.
     * @throws TransformerException if transforming class files fails.
     */
    public void transformClasses( final Specification specification, final JavaClass javaClass,
                                  final Transformer transformer ) throws IOException, TransformerException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }
        if ( transformer == null )
        {
            throw new NullPointerException( "transformer" );
        }

        try
        {
            final Specification decodedSpecification = this.decodeSpecification( javaClass );

            if ( decodedSpecification != null )
            {
                this.setClassfileAttribute( javaClass, Specification.class.getName(), this.encodeSpecification(
                    this.getModelManager().transformModelObject( this.getModelManager().getObjectFactory().
                    createSpecification( specification ), transformer ) ) );

            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Transforms committed meta-data of compiled Java classes of a given implementation of the modules of the instance.
     *
     * @param implementation The implementation to process.
     * @param javaClass The java class to process.
     * @param transformer The transformer to use for transforming the classes.
     *
     * @throws NullPointerException if {@code specification}, {@code javaClass} or {@code transformer} is {@code null}.
     * @throws IOException if accessing class files fails.
     * @throws TransformerException if transforming class files fails.
     */
    public void transformClasses( final Implementation implementation, final JavaClass javaClass,
                                  final Transformer transformer ) throws TransformerException, IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( javaClass == null )
        {
            throw new NullPointerException( "javaClass" );
        }
        if ( transformer == null )
        {
            throw new NullPointerException( "transformer" );
        }

        try
        {
            final Dependencies decodedDependencies = this.decodeDependencies( javaClass );
            final Messages decodedMessages = this.decodeMessages( javaClass );
            final Properties decodedProperties = this.decodeProperties( javaClass );
            final Specifications decodedSpecifications = this.decodeSpecifications( javaClass );

            if ( decodedDependencies != null )
            {
                this.setClassfileAttribute( javaClass, Dependencies.class.getName(), this.encodeDependencies(
                    this.getModelManager().transformModelObject( this.getModelManager().getObjectFactory().
                    createDependencies( decodedDependencies ), transformer ) ) );

            }

            if ( decodedMessages != null )
            {
                this.setClassfileAttribute( javaClass, Messages.class.getName(), this.encodeMessages(
                    this.getModelManager().transformModelObject( this.getModelManager().getObjectFactory().
                    createMessages( decodedMessages ), transformer ) ) );

            }

            if ( decodedProperties != null )
            {
                this.setClassfileAttribute( javaClass, Properties.class.getName(), this.encodeProperties(
                    this.getModelManager().transformModelObject( this.getModelManager().getObjectFactory().
                    createProperties( decodedProperties ), transformer ) ) );

            }

            if ( decodedSpecifications != null )
            {
                this.setClassfileAttribute( javaClass, Specifications.class.getName(), this.encodeSpecifications(
                    this.getModelManager().transformModelObject( this.getModelManager().getObjectFactory().
                    createSpecifications( decodedSpecifications ), transformer ) ) );

            }
        }
        catch ( SAXException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( JAXBException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Parses a class file.
     *
     * @param classFile The class file to parse.
     *
     * @return The parsed class file.
     *
     * @throws IOException if parsing {@code classFile} fails.
     */
    public JavaClass getJavaClass( final File classFile ) throws IOException
    {
        return this.getJavaClass( classFile.toURI().toURL(), classFile.getName() );
    }

    /**
     * Parses a class file.
     *
     * @param url The URL of the class file to parse.
     * @param className The name of the class at {@code url}.
     *
     * @return The parsed class file.
     *
     * @throws IOException if parsing fails.
     */
    public JavaClass getJavaClass( final URL url, final String className ) throws IOException
    {
        return this.getJavaClass( url.openStream(), className );
    }

    /**
     * Parses a class file.
     *
     * @param stream The stream to read the class file from.
     * @param className The name of the class to read from {@code stream}.
     *
     * @return The parsed class file.
     *
     * @throws IOException if parsing fails.
     */
    public JavaClass getJavaClass( final InputStream stream, final String className ) throws IOException
    {
        final ClassParser parser = new ClassParser( stream, className );
        final JavaClass clazz = parser.parse();
        stream.close();
        return clazz;
    }

    /**
     * Gets an attribute from a java class.
     *
     * @param clazz The java class to get an attribute from.
     * @param attributeName The name of the attribute to get.
     *
     * @return The value of attribute {@code attributeName} of {@code clazz} or {@code null} if no such attribute
     * exists.
     *
     * @throws NullPointerException if {@code clazz} or {@code attributeName} is {@code null}.
     * @throws IOException if getting the attribute fails.
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

        Attribute[] attributes = clazz.getAttributes();

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
     * @param clazz The class to update.
     * @param attributeName The name of the attribute to update.
     * @param data The new data of the attribute to update the {@code classFile} with.
     *
     * @throws NullPointerException if {@code clazz} or {@code attributeName} is {@code null}.
     * @throws IOException if updating the class file fails.
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

        /*
        The JavaTM Virtual Machine Specification - Second Edition - Chapter 4.1

        A Java virtual machine implementation is required to silently ignore any
        or all attributes in the attributes table of a ClassFile structure that
        it does not recognize. Attributes not defined in this specification are
        not allowed to affect the semantics of the class file, but only to
        provide additional descriptive information (§4.7.1).
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

        final Unknown unknown = new Unknown( nameIndex, data.length, data, clazz.getConstantPool() );

        if ( attributeIndex == -1 )
        {
            final Attribute[] tmp = new Attribute[ attributes.length + 1 ];
            System.arraycopy( attributes, 0, tmp, 0, attributes.length );
            tmp[attributes.length] = unknown;
            attributeIndex = attributes.length;
            attributes = tmp;
        }
        else
        {
            attributes[attributeIndex] = unknown;
        }

        clazz.setAttributes( attributes );
    }

    /**
     * Encodes a specification into a byte array.
     *
     * @param specification The specification to encode.
     *
     * @return GZIP compressed XML document for {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    private byte[] encodeSpecification( final Specification specification )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createSpecification( specification ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Encodes specifications into a byte array.
     *
     * @param specifications The specifications to encode.
     *
     * @return GZIP compressed XML document for {@code specifications}.
     *
     * @throws NullPointerException if {@code specifications} is {@code null}.
     */
    private byte[] encodeSpecifications( final Specifications specifications )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createSpecifications( specifications ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Encodes dependencies into a byte array.
     *
     * @param dependencies The dependencies to encode.
     *
     * @return GZIP compressed XML document for {@code dependencies}.
     *
     * @throws NullPointerException if {@code dependencies} is {@code null}.
     */
    private byte[] encodeDependencies( final Dependencies dependencies )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createDependencies( dependencies ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Encodes properties into a byte array.
     *
     * @param properties The properties to encode.
     *
     * @return GZIP compressed XML document for {@code properties}.
     *
     * @throws NullPointerException if {@code properties} is {@code null}.
     */
    private byte[] encodeProperties( final Properties properties )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createProperties( properties ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Encodes messages into a byte array.
     *
     * @param messages The messages to encode.
     *
     * @return GZIP compressed XML document for {@code messages}.
     *
     * @throws NullPointerException if {@code messages} is {@code null}.
     */
    private byte[] encodeMessages( final Messages messages )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createMessages( messages ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Decodes a specification from a class file.
     *
     * @param clazz The class to decode a specification from.
     *
     * @return The {@code Specification} decoded from {@code clazz} or {@code null} if {@code clazz} does not
     * contain a corresponding attribute.
     *
     * @throws NullPointerException if {@code clazz} is {@code null}.
     */
    private Specification decodeSpecification( final JavaClass clazz )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        Specification decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( clazz, Specification.class.getName() );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = this.getModelManager().getUnmarshaller( false ).unmarshal(
                new StreamSource( in ), Specification.class ).getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Decodes specifications from a class file.
     *
     * @param clazz The class to decode specifications from.
     *
     * @return The {@code Specifications} decoded from {@code clazz} or {@code null} if {@code clazz} does not
     * contain a corresponding attribute.
     *
     * @throws NullPointerException if {@code clazz} is {@code null}.
     */
    private Specifications decodeSpecifications( final JavaClass clazz )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        Specifications decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( clazz, Specifications.class.getName() );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = this.getModelManager().getUnmarshaller( false ).unmarshal(
                new StreamSource( in ), Specifications.class ).getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Decodes dependencies from a class file.
     *
     * @param clazz The class to decode dependencies from.
     *
     * @return The {@code Dependencies} decoded from {@code clazz} or {@code null} if {@code clazz} does not
     * contain a corresponding attribute.
     *
     * @throws NullPointerException if {@code clazz} is {@code null}.
     */
    private Dependencies decodeDependencies( final JavaClass clazz )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        Dependencies decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( clazz, Dependencies.class.getName() );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = this.getModelManager().getUnmarshaller( false ).unmarshal(
                new StreamSource( in ), Dependencies.class ).getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Decodes properties from a class file.
     *
     * @param clazz The class to decode properties from.
     *
     * @return The {@code Properties} decoded from {@code clazz} or {@code null} if {@code clazz} does not contain a
     * corresponding attribute.
     *
     * @throws NullPointerException if {@code clazz} is {@code null}.
     */
    private Properties decodeProperties( final JavaClass clazz )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        Properties decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( clazz, Properties.class.getName() );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = this.getModelManager().getUnmarshaller( false ).unmarshal(
                new StreamSource( in ), Properties.class ).getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Decodes messages from a class file.
     *
     * @param clazz The class to decode messages from.
     *
     * @return The {@code Messages} decoded from {@code clazz} or {@code null} if {@code clazz} does not contain a
     * corresponding attribute.
     *
     * @throws NullPointerException if {@code clazz} is {@code null}.
     */
    private Messages decodeMessages( final JavaClass clazz )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        Messages decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( clazz, Properties.class.getName() );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = this.getModelManager().getUnmarshaller( false ).unmarshal(
                new StreamSource( in ), Messages.class ).getValue();

            in.close();
        }

        return decoded;
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( JavaClasses.class.getName().replace( '.', '/' ) );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
