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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.JAXBException;
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
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @see #commitModuleClasses(java.io.File)
 * @see #validateModuleClasses(java.io.File)
 * @see #validateClasses(java.lang.ClassLoader)
 */
public class JavaClasses extends JomcTool
{

    /** Constant for the name of the properties file holding modification timestamps. */
    private static final String TIMESTAMPS_FILE_NAME = "java-classes.properties";

    /** Creates a new {@code JavaClasses} instance. */
    public JavaClasses()
    {
        super();
    }

    /**
     * Creates a new {@code JavaClasses} instance taking a {@code JomcTool} instance to initialize the instance with.
     *
     * @param tool The instance to initialize the new instance with,
     */
    public JavaClasses( final JomcTool tool )
    {
        super( tool );
    }

    /**
     * Commits meta-data of the module of the instance to compiled Java classes.
     *
     * @param classesDirectory The directory holding the compiled class files of the module of the instance.
     *
     * @throws NullPointerException if {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     *
     * @see #getModule()
     */
    public void commitModuleClasses( final File classesDirectory ) throws IOException
    {
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( this.getModule() != null )
        {
            this.log( Level.INFO, this.getMessage( "processingModule", new Object[]
                {
                    this.getModule().getName()
                } ), null );

            File timestampsFile = null;
            final java.util.Properties timestampProperties = new java.util.Properties();
            if ( this.getBuildDirectory() != null )
            {
                timestampsFile = new File( this.getBuildDirectory(), TIMESTAMPS_FILE_NAME );
                if ( !this.getBuildDirectory().exists() )
                {
                    this.getBuildDirectory().mkdirs();
                }
                if ( !timestampsFile.exists() )
                {
                    timestampsFile.createNewFile();
                }

                final InputStream in = new FileInputStream( timestampsFile );
                timestampProperties.load( in );
                in.close();

                this.log( Level.FINE, this.getMessage( "timestampsFile", new Object[]
                    {
                        timestampsFile.getAbsolutePath()
                    } ), null );

            }

            if ( this.getModule().getSpecifications() != null )
            {
                for ( Specification s : this.getModule().getSpecifications().getSpecification() )
                {
                    final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );

                    long lastModified = -1L;
                    if ( timestampProperties.getProperty( classFile.getAbsolutePath() ) != null )
                    {
                        lastModified = Long.valueOf( timestampProperties.getProperty( classFile.getAbsolutePath() ) );
                    }

                    if ( lastModified == -1L || lastModified != classFile.lastModified() )
                    {
                        final long oldLength = classFile.length();
                        final JavaClass clazz = this.getJavaClass( classFile );
                        this.commitSpecificationClass( s, clazz );
                        clazz.dump( classFile );
                        this.log( Level.INFO, this.getMessage( "writing", new Object[]
                            {
                                classFile.getAbsolutePath(), classFile.length() - oldLength
                            } ), null );

                    }

                    timestampProperties.setProperty(
                        classFile.getAbsolutePath(), Long.valueOf( classFile.lastModified() ).toString() );

                }
            }
            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( i.getIdentifier().equals( i.getClazz() ) )
                    {
                        final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                        final File classFile = new File( classesDirectory, classLocation );

                        long lastModified = -1L;
                        if ( timestampProperties.getProperty( classFile.getAbsolutePath() ) != null )
                        {
                            lastModified =
                                Long.valueOf( timestampProperties.getProperty( classFile.getAbsolutePath() ) );
                        }

                        if ( lastModified == -1L || lastModified != classFile.lastModified() )
                        {
                            final long oldLength = classFile.length();
                            final JavaClass clazz = this.getJavaClass( classFile );
                            this.commitImplementationClass( i, clazz );
                            clazz.dump( classFile );
                            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                                {
                                    classFile.getAbsolutePath(), classFile.length() - oldLength
                                } ), null );

                        }

                        timestampProperties.setProperty(
                            classFile.getAbsolutePath(), Long.valueOf( classFile.lastModified() ).toString() );

                    }
                }
            }

            if ( timestampsFile != null )
            {
                final OutputStream out = new FileOutputStream( timestampsFile );
                timestampProperties.store( out, this.getClass().getName() );
                out.close();
            }

            this.log( Level.INFO, this.getMessage( "upToDate", null ), null );
        }
        else
        {
            this.log( Level.WARNING, this.getMessage( "missingModule", new Object[]
                {
                    this.getModuleName()
                } ), null );

        }
    }

    /**
     * Validates compiled Java classes of the module of the instance to comply with the module of the instance.
     *
     * @param classesDirectory The directory holding the compiled class files of the module of the instance.
     *
     * @throws NullPointerException if {@code classesDirectory} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if any of the compiled Java classes of the module of the instance does not comply with
     * the module of the instance.
     *
     * @see #getModule()
     */
    public void validateModuleClasses( final File classesDirectory ) throws IOException, ModelException
    {
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();

        if ( this.getModule() != null )
        {
            this.log( Level.INFO, this.getMessage( "processingModule", new Object[]
                {
                    this.getModule().getName()
                } ), null );


            if ( this.getModule().getSpecifications() != null )
            {
                for ( Specification s : this.getModule().getSpecifications().getSpecification() )
                {
                    final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );

                    try
                    {
                        this.validateSpecificationClass( s, this.getJavaClass( classFile ) );
                    }
                    catch ( ModelException e )
                    {
                        details.addAll( e.getDetails() );
                    }
                }
            }
            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( i.getIdentifier().equals( i.getClazz() ) )
                    {
                        final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                        final File classFile = new File( classesDirectory, classLocation );

                        try
                        {
                            this.validateImplementationClass( i, this.getJavaClass( classFile ) );
                        }
                        catch ( ModelException e )
                        {
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

            this.log( Level.INFO, this.getMessage( "validated", new Object[]
                {
                    this.getModule().getName()
                } ), null );

        }
        else
        {
            this.log( Level.WARNING, this.getMessage( "missingModule", new Object[]
                {
                    this.getModuleName()
                } ), null );

        }
    }

    /**
     * Validates compiled Java classes to comply with the modules of the instance.
     *
     * @param classLoader The class loader to search for classes.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws IOException if reading class files fails.
     * @throws ModelException if any of the found class files does not comply with the given modules.
     */
    public void validateClasses( final ClassLoader classLoader ) throws IOException, ModelException
    {
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();

        for ( Module m : this.getModules().getModule() )
        {
            if ( m.getSpecifications() != null )
            {
                for ( Specification s : m.getSpecifications().getSpecification() )
                {
                    final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                    final URL classUrl = classLoader.getResource( classLocation );

                    if ( classUrl != null )
                    {
                        try
                        {
                            this.validateSpecificationClass( s, this.getJavaClass( classUrl, classLocation ) );
                        }
                        catch ( ModelException e )
                        {
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
            if ( m.getImplementations() != null )
            {
                for ( Implementation i : m.getImplementations().getImplementation() )
                {
                    if ( i.getIdentifier().equals( i.getClazz() ) )
                    {
                        final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                        final URL classUrl = classLoader.getResource( classLocation );

                        if ( classUrl != null )
                        {
                            try
                            {
                                this.validateImplementationClass( i, this.getJavaClass( classUrl, classLocation ) );
                            }
                            catch ( ModelException e )
                            {
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
        }
    }

    /**
     * Commits specification meta-data to the class file of a given specification.
     *
     * @param specification The specification to commit.
     * @param clazz The class of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} or {@code clazz} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitSpecificationClass( final Specification specification, final JavaClass clazz )
        throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        try
        {
            this.setClassfileAttribute( clazz, Specification.class.getName(),
                                        this.encodeSpecification( specification ) );

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
     * Commits implementation meta-data to the class file of a given implementation.
     *
     * @param implementation The implementation to process.
     * @param clazz The class of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code classFile} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitImplementationClass( final Implementation implementation, final JavaClass clazz )
        throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        try
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

            this.setClassfileAttribute( clazz, Dependencies.class.getName(), this.encodeDependencies(
                dependencies == null ? new Dependencies() : dependencies ) );

            this.setClassfileAttribute( clazz, Properties.class.getName(), this.encodeProperties(
                properties == null ? new Properties() : properties ) );

            this.setClassfileAttribute( clazz, Messages.class.getName(), this.encodeMessages(
                messages == null ? new Messages() : messages ) );

            this.setClassfileAttribute( clazz, Specifications.class.getName(), this.encodeSpecifications(
                specifications == null ? new Specifications() : specifications ) );

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
     * Validates the compiled Java class of a given specification to comply with the specification of the module of the
     * instance.
     *
     * @param specification The specification to process.
     * @param clazz The class to validate.
     *
     * @throws NullPointerException if {@code specification} or {@code clazz} is {@code null}.
     * @throws ModelException if {@code classFile} does not comply with {@code specification}.
     */
    public void validateSpecificationClass( final Specification specification, final JavaClass clazz )
        throws ModelException, IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( clazz == null )
        {
            throw new NullPointerException( "classFile" );
        }

        try
        {
            final Specification decoded = this.decodeSpecification( clazz );

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
                    this.log( Level.FINE, this.getMessage( "validatedClass", new Object[]
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
     * Validates the compiled Java class of a given implementation to comply with the implementation of the module of
     * the instance.
     *
     * @param implementation The implementation to process.
     * @param clazz The class of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code clazz} is {@code null}.
     * @throws IOException if reading {@code clazz} fails.
     * @throws ModelException if {@code clazz} does not comply with {@code implementation}.
     */
    public void validateImplementationClass( final Implementation implementation, final JavaClass clazz )
        throws IOException, ModelException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }

        try
        {
            final Dependencies dependencies = this.getModules().getDependencies( implementation.getIdentifier() );
            final Dependencies decodedDependencies = this.decodeDependencies( clazz );
            final Properties properties = this.getModules().getProperties( implementation.getIdentifier() );
            final Properties decodedProperties = this.decodeProperties( clazz );
            final Messages messages = this.getModules().getMessages( implementation.getIdentifier() );
            final Messages decodedMessages = this.decodeMessages( clazz );
            final Specifications decodedSpecifications = this.decodeSpecifications( clazz );
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
                this.log( Level.FINE, this.getMessage( "validatedClass", new Object[]
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
     * Relocates a given Java class.
     *
     * @param clazz The class to relocate.
     * @param relocator The relocator to use for relocating.
     *
     * @return {@code true} if {@code clazz} got changed due to relocation; {@code false} else.
     *
     * @throws NullPointerException if {@code clazz} or {@code relocator} is {@code null}.
     */
    public boolean relocateJavaClass( final JavaClass clazz, final ModelObjectRelocator relocator )
        throws IOException, SAXException, JAXBException
    {
        if ( clazz == null )
        {
            throw new NullPointerException( "clazz" );
        }
        if ( relocator == null )
        {
            throw new NullPointerException( "relocator" );
        }

        boolean relocated = false;

        final Specification decodedSpecification = this.decodeSpecification( clazz );
        final Dependencies decodedDependencies = this.decodeDependencies( clazz );
        final Specifications decodedSpecifications = this.decodeSpecifications( clazz );

        if ( decodedSpecification != null )
        {
            this.setClassfileAttribute( clazz, Specification.class.getName(), this.encodeSpecification(
                relocator.relocateModelObject( decodedSpecification, Specification.class ) ) );

            relocated = true;
        }

        if ( decodedDependencies != null )
        {
            this.setClassfileAttribute( clazz, Dependencies.class.getName(), this.encodeDependencies(
                relocator.relocateModelObject( decodedDependencies, Dependencies.class ) ) );

            relocated = true;
        }

        if ( decodedSpecifications != null )
        {
            this.setClassfileAttribute( clazz, Specifications.class.getName(), this.encodeSpecifications(
                relocator.relocateModelObject( decodedSpecifications, Specifications.class ) ) );

            relocated = true;
        }

        return relocated;
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
    protected byte[] getClassfileAttribute( final JavaClass clazz, final String attributeName ) throws IOException
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
    protected void setClassfileAttribute( final JavaClass clazz, final String attributeName, final byte[] data )
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
