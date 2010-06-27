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
import java.util.List;
import java.util.ResourceBundle;
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
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.ObjectFactory;
import org.jomc.model.Properties;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;
import org.jomc.modlet.ModelValidationReport;
import org.jomc.util.ParseException;
import org.jomc.util.TokenMgrError;
import org.jomc.util.VersionParser;

/**
 * Processes class files.
 *
 * <p><b>Use cases</b><br/><ul>
 * <li>{@link #commitModelObjects(Marshaller, File) }</li>
 * <li>{@link #commitModelObjects(Module, Marshaller, File) }</li>
 * <li>{@link #commitModelObjects(Specification, Marshaller, File) }</li>
 * <li>{@link #commitModelObjects(Implementation, Marshaller, File) }</li>
 * <li>{@link #validateModelObjects(Unmarshaller, File) }</li>
 * <li>{@link #validateModelObjects(Unmarshaller, ClassLoader) }</li>
 * <li>{@link #validateModelObjects(Module, Unmarshaller, File) }</li>
 * <li>{@link #validateModelObjects(Module, Unmarshaller, ClassLoader) }</li>
 * <li>{@link #validateModelObjects(Specification, Unmarshaller, JavaClass) }</li>
 * <li>{@link #validateModelObjects(Implementation, Unmarshaller, JavaClass) }</li>
 * <li>{@link #transformModelObjects(Marshaller, Unmarshaller, File, List) }</li>
 * <li>{@link #transformModelObjects(Module, Marshaller, Unmarshaller, File, List) }</li>
 * <li>{@link #transformModelObjects(Specification, Marshaller, Unmarshaller, JavaClass, List) }</li>
 * <li>{@link #transformModelObjects(Implementation, Marshaller, Unmarshaller, JavaClass, List) }</li>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 *
 * @see #getModules()
 */
public class ClassFileProcessor extends JomcTool
{

    /** Creates a new {@code ClassFileProcessor} instance. */
    public ClassFileProcessor()
    {
        super();
    }

    /**
     * Creates a new {@code ClassFileProcessor} instance taking a {@code ClassFileProcessor} instance to initialize the
     * instance with.
     *
     * @param tool The instance to initialize the new instance with,
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
     * @param marshaller The marshaller to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code marshaller} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Module, javax.xml.bind.Marshaller, java.io.File)
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void commitModelObjects( final Marshaller marshaller, final File classesDirectory ) throws IOException
    {
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.commitModelObjects( m, marshaller, classesDirectory );
        }
    }

    /**
     * Commits model objects of a given module of the modules of the instance to class files.
     *
     * @param module The module to process.
     * @param marshaller The marshaller to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code module}, {@code marshaller} or {@code classesDirectory} is {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see #commitModelObjects(org.jomc.model.Specification, javax.xml.bind.Marshaller, java.io.File)
     * @see #commitModelObjects(org.jomc.model.Implementation, javax.xml.bind.Marshaller, java.io.File)
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void commitModelObjects( final Module module, final Marshaller marshaller, final File classesDirectory )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.commitModelObjects( s, marshaller, classesDirectory );
            }
        }
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.commitModelObjects( i, marshaller, classesDirectory );
            }
        }
    }

    /**
     * Commits model objects of a given specification of the modules of the instance to class files.
     *
     * @param specification The specification to process.
     * @param marshaller The marshaller to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code specification}, {@code marshaller} or {@code classesDirectory} is
     * {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void commitModelObjects( final Specification specification, final Marshaller marshaller,
                                    final File classesDirectory ) throws IOException
    {
        if ( specification == null )
        {
            throw new NullPointerException( "specification" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( specification.isClassDeclaration() )
        {
            final String classLocation = specification.getClazz().replace( '.', File.separatorChar ) + ".class";
            final File classFile = new File( classesDirectory, classLocation );
            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, getMessage( "committing", classFile.getAbsolutePath() ), null );
            }

            final JavaClass javaClass = this.getJavaClass( classFile );
            this.setClassfileAttribute( javaClass, Specification.class.getName(), this.encodeModelObject(
                marshaller, new ObjectFactory().createSpecification( specification ) ) );

            javaClass.dump( classFile );
        }
    }

    /**
     * Commits model objects of a given implementation of the modules of the instance to class files.
     *
     * @param implementation The implementation to process.
     * @param marshaller The marshaller to use for committing the model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @throws NullPointerException if {@code implementation}, {@code marshaller} or {@code classesDirectory} is
     * {@code null}.
     * @throws IOException if committing model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void commitModelObjects( final Implementation implementation, final Marshaller marshaller,
                                    final File classesDirectory ) throws IOException
    {
        if ( implementation == null )
        {
            throw new NullPointerException( "implementation" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( implementation.isClassDeclaration() )
        {
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

            for ( SpecificationReference r : specifications.getReference() )
            {
                if ( specifications.getSpecification( r.getIdentifier() ) == null && this.isLoggable( Level.WARNING ) )
                {
                    this.log( Level.WARNING, getMessage( "unresolvedSpecification", r.getIdentifier(),
                                                         implementation.getIdentifier() ), null );

                }
            }

            for ( Dependency d : dependencies.getDependency() )
            {
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

            final String classLocation = implementation.getClazz().replace( '.', File.separatorChar ) + ".class";
            final File classFile = new File( classesDirectory, classLocation );

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, getMessage( "committing", classFile.getAbsolutePath() ), null );
            }

            final JavaClass javaClass = this.getJavaClass( classFile );
            final ObjectFactory of = new ObjectFactory();

            this.setClassfileAttribute( javaClass, Dependencies.class.getName(), this.encodeModelObject(
                marshaller, of.createDependencies( dependencies ) ) );

            this.setClassfileAttribute( javaClass, Properties.class.getName(), this.encodeModelObject(
                marshaller, of.createProperties( properties ) ) );

            this.setClassfileAttribute( javaClass, Messages.class.getName(), this.encodeModelObject(
                marshaller, of.createMessages( messages ) ) );

            this.setClassfileAttribute( javaClass, Specifications.class.getName(), this.encodeModelObject(
                marshaller, of.createSpecifications( specifications ) ) );

            javaClass.dump( classFile );
        }
    }

    /**
     * Validates model objects of class files.
     *
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code unmarshaller} or {@code classesDirectory} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Module, javax.xml.bind.Unmarshaller, java.io.File)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Unmarshaller unmarshaller, final File classesDirectory )
        throws IOException
    {
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        final ModelValidationReport report = new ModelValidationReport();

        for ( Module m : this.getModules().getModule() )
        {
            final ModelValidationReport current = this.validateModelObjects( m, unmarshaller, classesDirectory );
            report.getDetails().addAll( current.getDetails() );
        }

        return report;
    }

    /**
     * Validates model objects of class files.
     *
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classLoader The class loader to search for class files.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code unmarshaller} or {@code classLoader} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Module, javax.xml.bind.Unmarshaller, java.lang.ClassLoader)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Unmarshaller unmarshaller, final ClassLoader classLoader )
        throws IOException
    {
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        final ModelValidationReport report = new ModelValidationReport();

        for ( Module m : this.getModules().getModule() )
        {
            final ModelValidationReport current = this.validateModelObjects( m, unmarshaller, classLoader );
            report.getDetails().addAll( current.getDetails() );
        }

        return report;
    }

    /**
     * Validates model objects of class files.
     *
     * @param module The module to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classesDirectory The directory holding the class files.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code module}, {@code unmarshaller} or {@code classesDirectory} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     * @see #validateModelObjects(org.jomc.model.Implementation, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Module module, final Unmarshaller unmarshaller,
                                                       final File classesDirectory ) throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        final ModelValidationReport report = new ModelValidationReport();

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                if ( s.isClassDeclaration() )
                {
                    final String classLocation = s.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );
                    final ModelValidationReport current =
                        this.validateModelObjects( s, unmarshaller, this.getJavaClass( classFile ) );

                    report.getDetails().addAll( current.getDetails() );
                }
            }
        }

        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( i.isClassDeclaration() )
                {
                    final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );
                    final JavaClass javaClass = this.getJavaClass( classFile );
                    final ModelValidationReport current =
                        this.validateModelObjects( i, unmarshaller, javaClass );

                    report.getDetails().addAll( current.getDetails() );
                }
            }
        }

        return report;
    }

    /**
     * Validates model objects of class files.
     *
     * @param module The module to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classLoader The class loader to search for class files.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code module}, {@code unmarshaller} or {@code classLoader} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see #validateModelObjects(org.jomc.model.Specification, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     * @see #validateModelObjects(org.jomc.model.Implementation, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Module module, final Unmarshaller unmarshaller,
                                                       final ClassLoader classLoader ) throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( classLoader == null )
        {
            throw new NullPointerException( "classLoader" );
        }

        final ModelValidationReport report = new ModelValidationReport();

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                if ( s.isClassDeclaration() )
                {
                    final String classLocation = s.getClazz().replace( '.', '/' ) + ".class";
                    final URL classUrl = classLoader.getResource( classLocation );

                    if ( classUrl == null )
                    {
                        throw new IOException( getMessage( "resourceNotFound", classLocation ) );
                    }

                    final JavaClass javaClass = this.getJavaClass( classUrl, classLocation );
                    final ModelValidationReport current =
                        this.validateModelObjects( s, unmarshaller, javaClass );

                    report.getDetails().addAll( current.getDetails() );
                }
            }
        }

        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( i.isClassDeclaration() )
                {
                    final String classLocation = i.getClazz().replace( '.', '/' ) + ".class";
                    final URL classUrl = classLoader.getResource( classLocation );

                    if ( classUrl == null )
                    {
                        throw new IOException( getMessage( "resourceNotFound", classLocation ) );
                    }

                    final JavaClass javaClass = this.getJavaClass( classUrl, classLocation );
                    final ModelValidationReport current = this.validateModelObjects( i, unmarshaller, javaClass );
                    report.getDetails().addAll( current.getDetails() );
                }
            }
        }

        return report;
    }

    /**
     * Validates model objects of class files.
     *
     * @param specification The specification to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classFile The class file to validate model objects of.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code specification}, {@code unmarshaller} or {@code classFile} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Specification specification,
                                                       final Unmarshaller unmarshaller, final JavaClass classFile )
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
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "validatingSpecification", specification.getIdentifier() ), null );
        }

        final ModelValidationReport report = new ModelValidationReport();

        Specification decoded = null;
        final byte[] bytes = this.getClassfileAttribute( classFile, Specification.class.getName() );
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
                    "illegalMultiplicity", specification.getIdentifier(), specification.getMultiplicity().value(),
                    decoded.getMultiplicity().value() ), new ObjectFactory().createSpecification( specification ) ) );

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

        return report;
    }

    /**
     * Validates model objects of class files.
     *
     * @param implementation The implementation to process.
     * @param unmarshaller The unmarshaller to use for validating model objects.
     * @param classFile The class file to validate model objects of.
     *
     * @return The report of the validation.
     *
     * @throws NullPointerException if {@code implementation}, {@code unmarshaller} or {@code classFile} is {@code null}.
     * @throws IOException if validating model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public ModelValidationReport validateModelObjects( final Implementation implementation,
                                                       final Unmarshaller unmarshaller, final JavaClass classFile )
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
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

        if ( this.isLoggable( Level.INFO ) )
        {
            this.log( Level.INFO, getMessage( "validatingImplementation", implementation.getIdentifier() ), null );
        }

        final ModelValidationReport report = new ModelValidationReport();

        try
        {
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
            byte[] bytes = this.getClassfileAttribute( classFile, Dependencies.class.getName() );
            if ( bytes != null )
            {
                decodedDependencies = this.decodeModelObject( unmarshaller, bytes, Dependencies.class );
            }

            Properties decodedProperties = null;
            bytes = this.getClassfileAttribute( classFile, Properties.class.getName() );
            if ( bytes != null )
            {
                decodedProperties = this.decodeModelObject( unmarshaller, bytes, Properties.class );
            }

            Messages decodedMessages = null;
            bytes = this.getClassfileAttribute( classFile, Messages.class.getName() );
            if ( bytes != null )
            {
                decodedMessages = this.decodeModelObject( unmarshaller, bytes, Messages.class );
            }

            Specifications decodedSpecifications = null;
            bytes = this.getClassfileAttribute( classFile, Specifications.class.getName() );
            if ( bytes != null )
            {
                decodedSpecifications = this.decodeModelObject( unmarshaller, bytes, Specifications.class );
            }

            if ( decodedDependencies != null )
            {
                for ( Dependency decodedDependency : decodedDependencies.getDependency() )
                {
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
                            "incompatibleDependency", classFile.getClassName(),
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
                for ( Property decodedProperty : decodedProperties.getProperty() )
                {
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
                for ( Message decodedMessage : decodedMessages.getMessage() )
                {
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
                this.log( Level.WARNING, getMessage( "cannotValidateImplementation",
                                                     implementation.getIdentifier(), Messages.class.getName() ), null );

            }

            if ( decodedSpecifications != null )
            {
                for ( Specification decodedSpecification : decodedSpecifications.getSpecification() )
                {
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
                                decodedSpecification.getScope() == null ? "Multiton" : decodedSpecification.getScope() ),
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

                for ( SpecificationReference decodedReference : decodedSpecifications.getReference() )
                {
                    final Specification specification =
                        specifications.getSpecification( decodedReference.getIdentifier() );

                    if ( specification == null )
                    {
                        report.getDetails().add( new ModelValidationReport.Detail(
                            "CLASS_MISSING_SPECIFICATION", Level.SEVERE, getMessage(
                            "missingSpecification", implementation.getIdentifier(), decodedReference.getIdentifier() ),
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
                            "incompatibleImplementation", classFile.getClassName(),
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

            return report;
        }
        catch ( final ParseException e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
        catch ( final TokenMgrError e )
        {
            throw (IOException) new IOException( e.getMessage() ).initCause( e );
        }
    }

    /**
     * Transforms model objects of class files.
     *
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming model objects.
     *
     * @throws NullPointerException if {@code marshaller}, {@code unmarshaller}, {@code classesDirectory} or
     * {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Module, javax.xml.bind.Marshaller, javax.xml.bind.Unmarshaller, java.io.File, java.util.List)
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void transformModelObjects( final Marshaller marshaller, final Unmarshaller unmarshaller,
                                       final File classesDirectory, final List<Transformer> transformers )
        throws IOException
    {
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        for ( Module m : this.getModules().getModule() )
        {
            this.transformModelObjects( m, marshaller, unmarshaller, classesDirectory, transformers );
        }
    }

    /**
     * Transforms model objects of class files.
     *
     * @param module The module to process.
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param classesDirectory The directory holding the class files.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code module}, {@code marshaller}, {@code unmarshaller},
     * {@code classesDirectory} or {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see #transformModelObjects(org.jomc.model.Specification, javax.xml.bind.Marshaller, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass, java.util.List)
     * @see #transformModelObjects(org.jomc.model.Implementation, javax.xml.bind.Marshaller, javax.xml.bind.Unmarshaller, org.apache.bcel.classfile.JavaClass, java.util.List)
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void transformModelObjects( final Module module, final Marshaller marshaller, final Unmarshaller unmarshaller,
                                       final File classesDirectory, final List<Transformer> transformers )
        throws IOException
    {
        if ( module == null )
        {
            throw new NullPointerException( "module" );
        }
        if ( marshaller == null )
        {
            throw new NullPointerException( "marshaller" );
        }
        if ( unmarshaller == null )
        {
            throw new NullPointerException( "unmarshaller" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }
        if ( classesDirectory == null )
        {
            throw new NullPointerException( "classesDirectory" );
        }

        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                if ( s.isClassDeclaration() )
                {
                    final String classLocation = s.getIdentifier().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, getMessage( "transforming", classFile.getAbsolutePath() ), null );
                    }

                    final JavaClass javaClass = this.getJavaClass( classFile );
                    this.transformModelObjects( s, marshaller, unmarshaller, javaClass, transformers );
                    javaClass.dump( classFile );
                }
            }
        }

        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                if ( i.isClassDeclaration() )
                {
                    final String classLocation = i.getClazz().replace( '.', File.separatorChar ) + ".class";
                    final File classFile = new File( classesDirectory, classLocation );

                    if ( this.isLoggable( Level.INFO ) )
                    {
                        this.log( Level.INFO, getMessage( "transforming", classFile.getAbsolutePath() ), null );
                    }

                    final JavaClass javaClass = this.getJavaClass( classFile );
                    this.transformModelObjects( i, marshaller, unmarshaller, javaClass, transformers );
                    javaClass.dump( classFile );
                }
            }
        }
    }

    /**
     * Transforms model objects of class files.
     *
     * @param specification The specification to process.
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param classFile The class file to transform model objects of.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code specification}, {@code marshaller}, {@code unmarshaller},
     * {@code classFile} or {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void transformModelObjects( final Specification specification, final Marshaller marshaller,
                                       final Unmarshaller unmarshaller, final JavaClass classFile,
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
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }

        try
        {
            Specification decodedSpecification = null;
            final ObjectFactory objectFactory = new ObjectFactory();
            final byte[] bytes = this.getClassfileAttribute( classFile, Specification.class.getName() );
            if ( bytes != null )
            {
                decodedSpecification = this.decodeModelObject( unmarshaller, bytes, Specification.class );
            }

            if ( decodedSpecification != null )
            {
                for ( Transformer transformer : transformers )
                {
                    final JAXBSource source =
                        new JAXBSource( marshaller, objectFactory.createSpecification( decodedSpecification ) );

                    final JAXBResult result = new JAXBResult( unmarshaller );
                    transformer.transform( source, result );
                    decodedSpecification = ( (JAXBElement<Specification>) result.getResult() ).getValue();
                }

                this.setClassfileAttribute( classFile, Specification.class.getName(), this.encodeModelObject(
                    marshaller, objectFactory.createSpecification( decodedSpecification ) ) );

            }
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final TransformerException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getException() != null )
            {
                message = e.getException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
    }

    /**
     * Transforms model objects of class files.
     *
     * @param implementation The implementation to process.
     * @param marshaller The marshaller to use for transforming model objects.
     * @param unmarshaller The unmarshaller to use for transforming model objects.
     * @param classFile The class file to transform model objects of.
     * @param transformers The transformers to use for transforming the model objects.
     *
     * @throws NullPointerException if {@code implementation}, {@code marshaller}, {@code unmarshaller},
     * {@code classFile} or {@code transformers} is {@code null}.
     * @throws IOException if transforming model objects fails.
     *
     * @see org.jomc.modlet.ModelContext#createMarshaller(java.lang.String)
     * @see org.jomc.modlet.ModelContext#createUnmarshaller(java.lang.String)
     * @see org.jomc.model.Modules#MODEL_PUBLIC_ID
     */
    public void transformModelObjects( final Implementation implementation, final Marshaller marshaller,
                                       final Unmarshaller unmarshaller, final JavaClass classFile,
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
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }
        if ( transformers == null )
        {
            throw new NullPointerException( "transformers" );
        }

        try
        {
            Dependencies decodedDependencies = null;
            byte[] bytes = this.getClassfileAttribute( classFile, Dependencies.class.getName() );
            if ( bytes != null )
            {
                decodedDependencies = this.decodeModelObject( unmarshaller, bytes, Dependencies.class );
            }

            Messages decodedMessages = null;
            bytes = this.getClassfileAttribute( classFile, Messages.class.getName() );
            if ( bytes != null )
            {
                decodedMessages = this.decodeModelObject( unmarshaller, bytes, Messages.class );
            }

            Properties decodedProperties = null;
            bytes = this.getClassfileAttribute( classFile, Properties.class.getName() );
            if ( bytes != null )
            {
                decodedProperties = this.decodeModelObject( unmarshaller, bytes, Properties.class );
            }

            Specifications decodedSpecifications = null;
            bytes = this.getClassfileAttribute( classFile, Specifications.class.getName() );
            if ( bytes != null )
            {
                decodedSpecifications = this.decodeModelObject( unmarshaller, bytes, Specifications.class );
            }

            final ObjectFactory of = new ObjectFactory();
            for ( Transformer transformer : transformers )
            {
                if ( decodedDependencies != null )
                {
                    final JAXBSource source = new JAXBSource( marshaller, of.createDependencies( decodedDependencies ) );
                    final JAXBResult result = new JAXBResult( unmarshaller );
                    transformer.transform( source, result );
                    decodedDependencies = ( (JAXBElement<Dependencies>) result.getResult() ).getValue();
                }

                if ( decodedMessages != null )
                {
                    final JAXBSource source = new JAXBSource( marshaller, of.createMessages( decodedMessages ) );
                    final JAXBResult result = new JAXBResult( unmarshaller );
                    transformer.transform( source, result );
                    decodedMessages = ( (JAXBElement<Messages>) result.getResult() ).getValue();
                }

                if ( decodedProperties != null )
                {
                    final JAXBSource source = new JAXBSource( marshaller, of.createProperties( decodedProperties ) );
                    final JAXBResult result = new JAXBResult( unmarshaller );
                    transformer.transform( source, result );
                    decodedProperties = ( (JAXBElement<Properties>) result.getResult() ).getValue();
                }

                if ( decodedSpecifications != null )
                {
                    final JAXBSource source =
                        new JAXBSource( marshaller, of.createSpecifications( decodedSpecifications ) );

                    final JAXBResult result = new JAXBResult( unmarshaller );
                    transformer.transform( source, result );
                    decodedSpecifications = ( (JAXBElement<Specifications>) result.getResult() ).getValue();
                }
            }

            if ( decodedDependencies != null )
            {
                this.setClassfileAttribute( classFile, Dependencies.class.getName(), this.encodeModelObject(
                    marshaller, of.createDependencies( decodedDependencies ) ) );

            }

            if ( decodedMessages != null )
            {
                this.setClassfileAttribute( classFile, Messages.class.getName(), this.encodeModelObject(
                    marshaller, of.createMessages( decodedMessages ) ) );

            }

            if ( decodedProperties != null )
            {
                this.setClassfileAttribute( classFile, Properties.class.getName(), this.encodeModelObject(
                    marshaller, of.createProperties( decodedProperties ) ) );

            }

            if ( decodedSpecifications != null )
            {
                this.setClassfileAttribute( classFile, Specifications.class.getName(), this.encodeModelObject(
                    marshaller, of.createSpecifications( decodedSpecifications ) ) );

            }
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final TransformerException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getException() != null )
            {
                message = e.getException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
    }

    /**
     * Parses a class file.
     *
     * @param classFile The class file to parse.
     *
     * @return The parsed class file.
     *
     * @throws NullPointerException if {@code classFile} is {@code null}.
     * @throws IOException if parsing {@code classFile} fails.
     *
     * @see JavaClass
     */
    public JavaClass getJavaClass( final File classFile ) throws IOException
    {
        if ( classFile == null )
        {
            throw new NullPointerException( "classFile" );
        }

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
     * @throws NullPointerException if {@code url} or {@code className} is {@code null}.
     * @throws IOException if parsing fails.
     *
     * @see JavaClass
     */
    public JavaClass getJavaClass( final URL url, final String className ) throws IOException
    {
        if ( url == null )
        {
            throw new NullPointerException( "url" );
        }
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }

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
     * @throws NullPointerException if {@code stream} or {@code className} is {@code null}.
     * @throws IOException if parsing fails.
     *
     * @see JavaClass
     */
    public JavaClass getJavaClass( final InputStream stream, final String className ) throws IOException
    {
        if ( stream == null )
        {
            throw new NullPointerException( "stream" );
        }
        if ( className == null )
        {
            throw new NullPointerException( "className" );
        }

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
     * @param clazz The class to update.
     * @param attributeName The name of the attribute to update.
     * @param data The new data of the attribute to update the {@code classFile} with.
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
     * @return GZIP compressed XML document for {@code modelObject}.
     *
     * @throws NullPointerException if {@code marshaller} or {@code modelObject} is {@code null}.
     * @throws IOException if encoding {@code modelObject} fails.
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

        try
        {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final GZIPOutputStream out = new GZIPOutputStream( baos );
            marshaller.marshal( modelObject, out );
            out.close();
            return baos.toByteArray();
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
    }

    /**
     * Decodes a model object from a byte array.
     *
     * @param unmarshaller The unmarshaller to use for decoding the object.
     * @param bytes The encoded model object to decode.
     * @param type The type of the encoded model object.
     * @param <T> The type of the decoded model object.
     *
     * @return Model object decoded from {@code bytes}.
     *
     * @throws NullPointerException if {@code unmarshaller}, {@code bytes} or {@code type} is {@code null}.
     * @throws IOException if decoding {@code bytes} fails.
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

        try
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
            final GZIPInputStream in = new GZIPInputStream( bais );
            final JAXBElement<T> element = (JAXBElement<T>) unmarshaller.unmarshal( in );
            in.close();
            return element.getValue();
        }
        catch ( final JAXBException e )
        {
            String message = e.getMessage();
            if ( message == null && e.getLinkedException() != null )
            {
                message = e.getLinkedException().getMessage();
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
    }

    private static String getMessage( final String key, final Object... arguments )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        return MessageFormat.format( ResourceBundle.getBundle( ClassFileProcessor.class.getName().replace( '.', '/' ) ).
            getString( key ), arguments );

    }

}
