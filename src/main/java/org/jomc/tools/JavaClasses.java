/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@schulte.it>
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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Unknown;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Instance;
import org.jomc.model.Message;
import org.jomc.model.MessageReference;
import org.jomc.model.ModelException;
import org.jomc.model.Module;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.xml.sax.SAXException;

/**
 * Manages Java classes.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 * @see #commitModuleClasses(java.io.File)
 */
public class JavaClasses extends JomcTool
{

    /** Constant for the name of the classfile attribute holding specification data. */
    private static final String SPECIFICATION_ATTRIBUTE = Specification.class.getName();

    /** Constant for the name of the classfile attribute holding implementation data. */
    private static final String IMPLEMENTATION_ATTRIBUTE = Module.class.getName();

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


            if ( this.getModule().getSpecifications() != null )
            {
                for ( Specification s : this.getModule().getSpecifications().getSpecification() )
                {
                    this.commitSpecificationClass( s, classesDirectory );
                }
            }
            if ( this.getModule().getImplementations() != null )
            {
                for ( Implementation i : this.getModule().getImplementations().getImplementation() )
                {
                    if ( i.getIdentifier().equals( i.getClazz() ) )
                    {
                        this.commitImplementationClass( i, classesDirectory );
                    }
                }
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
     * Commits specification meta-data to the classfile of a given specification.
     *
     * @param specification The specification to process.
     * @param classesDirectory The directory holding the classfile of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitSpecificationClass( final Specification specification, final File classesDirectory )
        throws IOException
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
            this.setClassfileAttribute( classFile, SPECIFICATION_ATTRIBUTE, this.encodeSpecification( specification ) );
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
     * Commits implementation meta-data to the classfile of a given implementation.
     *
     * @param implementation The implementation to process.
     * @param classesDirectory The directory holding the classfile of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing meta-data fails.
     */
    public void commitImplementationClass( final Implementation implementation, final File classesDirectory )
        throws IOException
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
            final String classLocation = implementation.getClazz().replace( '.', File.separatorChar ) + ".class";
            final File classFile = new File( classesDirectory, classLocation );
            this.setClassfileAttribute( classFile, IMPLEMENTATION_ATTRIBUTE,
                                        this.encodeImplementation( implementation ) );

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
     * Validates compiled Java classes of the module of the instance to comply with the module of the instance.
     *
     * @param classesDirectory The directory holding the compiled class files of the module of the instance.
     *
     * @throws NullPointerException if {@code classesDirectory} is {@code null}.
     * @throws IOException if reading classfiles fails.
     * @throws ModelException if any of the compiled Java classes of the module of the instance does not comply with
     * the module of the instance.
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
                        this.validateSpecificationClass(
                            s, classFile.getName(), new FileInputStream( classFile ) );

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
                            this.validateImplementationClass(
                                i, classFile.getName(), new FileInputStream( classFile ) );

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
     * Validates the compiled Java class of a given specification to comply with that specification.
     *
     * @param specification The specification to process.
     * @param classFileName The name of the classfile of {@code specification}.
     * @param classFile The classfile of {@code specification}.
     *
     * @throws NullPointerException if {@code specification} or {@code classFile} is {@code null}.
     * @throws IOException if reading {@code classFile} fails.
     * @throws ModelException if {@code classFile} does not comply with {@code specification}.
     */
    public void validateSpecificationClass( final Specification specification, final String classFileName,
                                            final InputStream classFile ) throws IOException, ModelException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        try
        {
            final Specification decoded = this.decodeSpecification( classFileName, classFile );

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

                if ( decoded.getScope() != specification.getScope() )
                {
                    details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                        "illegalScope", new Object[]
                        {
                            specification.getIdentifier(), specification.getScope().value(),
                            decoded.getScope().value()
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
                        specification.getIdentifier(), SPECIFICATION_ATTRIBUTE
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
     * Validates the compiled Java class of a given implementation to comply with that implementation.
     *
     * @param implementation The implementation to process.
     * @param classFileName The name of the classfile of {@code implementation}.
     * @param classFile The classfile of {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} or {@code classesFile} is {@code null}.
     * @throws IOException if reading {@code classFile} fails.
     * @throws ModelException if {@code classFile} does not comply with {@code implementation}.
     */
    public void validateImplementationClass( final Implementation implementation, final String classFileName,
                                             final InputStream classFile ) throws IOException, ModelException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        try
        {
            final Module decoded = this.decodeImplementation( classFileName, classFile );

            if ( decoded != null )
            {
                final Instance instance = this.getModules().getInstance( implementation.getIdentifier() );
                final Implementation decodedImplementation =
                    decoded.getImplementations().getImplementation( implementation.getIdentifier() );

                if ( decodedImplementation != null )
                {
                    final List<ModelException.Detail> details = new LinkedList<ModelException.Detail>();

                    if ( decodedImplementation.getProperties() != null )
                    {
                        for ( Property decodedProperty : decodedImplementation.getProperties().getProperty() )
                        {
                            final Property instanceProperty =
                                instance.getProperties() == null ? null : instance.getProperties().getProperty(
                                decodedProperty.getName() );

                            if ( instanceProperty == null )
                            {
                                details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                    "missingProperty", new Object[]
                                    {
                                        implementation.getIdentifier(), decodedProperty.getName()
                                    } ) ) );

                            }
                        }
                    }

                    if ( decodedImplementation.getDependencies() != null )
                    {
                        for ( Dependency decodedDependency : decodedImplementation.getDependencies().getDependency() )
                        {
                            final Dependency instanceDependency =
                                instance.getDependencies() == null ? null : instance.getDependencies().getDependency(
                                decodedDependency.getName() );

                            if ( instanceDependency == null )
                            {
                                details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                    "missingDependency", new Object[]
                                    {
                                        implementation.getIdentifier(), decodedDependency.getName()
                                    } ) ) );

                            }
                        }
                    }

                    if ( decodedImplementation.getMessages() != null )
                    {
                        for ( Message decodedMessage : decodedImplementation.getMessages().getMessage() )
                        {
                            final Message instanceMessage =
                                instance.getMessages() == null ? null : instance.getMessages().getMessage(
                                decodedMessage.getName() );

                            if ( instanceMessage == null )
                            {
                                details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                    "missingMessage", new Object[]
                                    {
                                        implementation.getIdentifier(), decodedMessage.getName()
                                    } ) ) );

                            }
                        }


                        for ( MessageReference decodedMessageRef : decodedImplementation.getMessages().getReference() )
                        {
                            final Message instanceMessage =
                                instance.getMessages() == null ? null : instance.getMessages().getMessage(
                                decodedMessageRef.getName() );

                            if ( instanceMessage == null )
                            {
                                details.add( new ModelException.Detail( Level.SEVERE, this.getMessage(
                                    "missingMessage", new Object[]
                                    {
                                        implementation.getIdentifier(), decodedMessageRef.getName()
                                    } ) ) );

                            }
                        }
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
                else
                {
                    throw new IOException( this.getMessage( "illegalClassfile", new Object[]
                        {
                            implementation.getClazz()
                        } ) );

                }
            }
            else
            {
                this.log( Level.WARNING, this.getMessage( "cannotValidate", new Object[]
                    {
                        implementation.getClazz(), IMPLEMENTATION_ATTRIBUTE
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
     * Validates compiled Java classes to comply with the modules of the instance.
     *
     * @param classLoader The classloader to search for classes.
     *
     * @throws NullPointerException if {@code classLoader} is {@code null}.
     * @throws IOException if reading classfiles fails.
     * @throws ModelException if any of the found classfiles does not comply with the given modules.
     */
    public void validateModules( final ClassLoader classLoader ) throws IOException, ModelException
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
                            final InputStream in = classUrl.openStream();
                            this.validateSpecificationClass( s, classLocation, in );
                            in.close();
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
                                final InputStream in = classUrl.openStream();
                                this.validateImplementationClass( i, classLocation, in );
                                in.close();
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
     * Encodes a specification into a byte array.
     *
     * @param specification The specification to encode.
     *
     * @return GZIP compressed XML document for {@code specification}.
     *
     * @throws NullPointerException if {@code specification} is {@code null}.
     */
    public byte[] encodeSpecification( final Specification specification )
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
     * Decodes an encoded specification.
     *
     * @param classFileName The name of the classfile to decode.
     * @param classFile The classfile to decode.
     *
     * @return The {@code Specification} decoded from {@code classFile} or {@code null} if {@code classFile} does not
     * contain a specification attribute.
     *
     * @throws NullPointerException if {@code classFile} is {@code null}.
     */
    public Specification decodeSpecification( final String classFileName, final InputStream classFile )
        throws IOException, SAXException, JAXBException
    {
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        Specification decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( classFileName, classFile, SPECIFICATION_ATTRIBUTE );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded = ( (JAXBElement<Specification>) this.getModelManager().getUnmarshaller( false ).unmarshal( in ) ).
                getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Encodes an implementation into a byte array.
     *
     * @param implementation The implementation to encode.
     *
     * @return GZIP compressed XML document for {@code implementation}.
     *
     * @throws NullPointerException if {@code implementation} is {@code null}.
     */
    public byte[] encodeImplementation( final Implementation implementation )
        throws IOException, SAXException, JAXBException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream( baos );

        this.getModelManager().getMarshaller( false, false ).marshal(
            this.getModelManager().getObjectFactory().createModule(
            this.getModules().getImplementationModule( implementation.getIdentifier() ) ), out );

        out.close();
        return baos.toByteArray();
    }

    /**
     * Decodes an encoded implementation.
     *
     * @param classFileName The name of the classfile to decode.
     * @param classFile The classfile to decode.
     *
     * @return The {@code Implementation} decoded from {@code classFile}.
     *
     * @throws NullPointerException if {@code classFile} is {@code null}.
     */
    public Module decodeImplementation( final String classFileName, final InputStream classFile )
        throws IOException, SAXException, JAXBException
    {
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        Module decoded = null;
        final byte[] attributeValue = this.getClassfileAttribute( classFileName, classFile, IMPLEMENTATION_ATTRIBUTE );

        if ( attributeValue != null )
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( attributeValue );
            final GZIPInputStream in = new GZIPInputStream( bais );
            decoded =
                ( (JAXBElement<Module>) this.getModelManager().getUnmarshaller( false ).unmarshal( in ) ).getValue();

            in.close();
        }

        return decoded;
    }

    /**
     * Gets an attribute from a classfile.
     *
     * @param classFile The classfile to process.
     * @param attributeName The name of the attribute to return.
     *
     * @return The value of attribute {@code attributeName} of {@code classFile} or {@code null} if no such attribute
     * exists.
     *
     * @throws IOException if processing the classfile fails.
     */
    public byte[] getClassfileAttribute( final String classFileName, final InputStream classFile,
                                         final String attributeName ) throws IOException
    {
        final ClassParser parser = new ClassParser( classFile, classFileName );
        final JavaClass clazz = parser.parse();
        Attribute[] attributes = clazz.getAttributes();

        for ( int i = attributes.length - 1; i >= 0; i-- )
        {
            final Constant constant = clazz.getConstantPool().getConstant( attributes[i].getNameIndex() );

            if ( constant instanceof ConstantUtf8 )
            {
                if ( attributeName.equals( ( (ConstantUtf8) constant ).getBytes() ) )
                {
                    final Unknown unknown = (Unknown) attributes[i];
                    return unknown.getBytes();
                }
            }
        }

        return null;
    }

    /**
     * Adds or updates an attribute in a classfile.
     *
     * @param classFile The classfile to process.
     * @param attributeName The name of the attribute to update.
     * @param meta The new data of the attribute to update the {@code classFile} with.
     *
     * @throws NullPointerException if {@code classFile} or {@code attributeName} is {@code null}.
     * @throws IOException if updating the classfile fails.
     */
    public void setClassfileAttribute( final File classFile, final String attributeName, final byte[] meta )
        throws IOException
    {
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
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
        final long length = classFile.length();
        final ClassParser parser = new ClassParser( new FileInputStream( classFile ), classFile.getName() );
        final JavaClass clazz = parser.parse();
        Attribute[] attributes = clazz.getAttributes();

        int attributeIndex = -1;
        int nameIndex = -1;

        for ( int i = attributes.length - 1; i >= 0; i-- )
        {
            final Constant constant = clazz.getConstantPool().getConstant( attributes[i].getNameIndex() );

            if ( constant instanceof ConstantUtf8 )
            {
                if ( attributeName.equals( ( (ConstantUtf8) constant ).getBytes() ) )
                {
                    attributeIndex = i;
                    nameIndex = attributes[i].getNameIndex();
                }
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

        final Unknown unknown = new Unknown( nameIndex, meta.length, meta, clazz.getConstantPool() );

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
        clazz.dump( classFile );

        final long delta = classFile.length() - length;
        if ( delta != 0 )
        {
            this.log( Level.INFO, this.getMessage( "writing", new Object[]
                {
                    classFile.getCanonicalPath(), new Long( delta )
                } ), null );

        }
    }

    private String getMessage( final String key, final Object args )
    {
        final ResourceBundle b = ResourceBundle.getBundle( "org/jomc/tools/JavaClasses" );
        final MessageFormat f = new MessageFormat( b.getString( key ) );
        return f.format( args );
    }

}
