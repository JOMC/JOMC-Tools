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
package org.jomc.tools.test;

import org.apache.commons.io.FileUtils;
import java.io.File;
import org.junit.Test;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.jomc.model.Argument;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Text;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.tools.JomcTool;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.JomcTool}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcToolTest
{

    /** Constant to prefix relative resource names with. */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/tools/test/";

    /** Constant for the name of the system property holding the name of the encoding of resources backing the test. */
    private static final String RESOURCE_ENCODING_PROPERTY_NAME = "jomc.test.resourceEncoding";

    /** Constant for the name of the system property holding the output directory for the test. */
    private static final String OUTPUT_DIRECTORY_PROPERTY_NAME = "jomc.test.outputDirectory";

    /** The {@code JomcTool} instance tests are performed with. */
    private JomcTool jomcTool;

    /** The {@code ModelContext} of the instance. */
    private ModelContext modelContext;

    /** The {@code Model} of the instance. */
    private Model model;

    /** The name of the encoding to use when reading or writing resources. */
    private String resourceEncoding;

    /** The output directory of the instance. */
    private File outputDirectory;

    /** Serial number of next output directories. */
    private volatile int outputDirectoryId;

    /** Creates a new {@code JomcToolTest} instance. */
    public JomcToolTest()
    {
        super();
    }

    /**
     * Gets the name of the encoding used when reading resources.
     *
     * @return The name of the encoding used when reading resources.
     *
     * @see #setResourceEncoding(java.lang.String)
     */
    public final String getResourceEncoding()
    {
        if ( this.resourceEncoding == null )
        {
            this.resourceEncoding = System.getProperty( RESOURCE_ENCODING_PROPERTY_NAME );
            assertNotNull( "Expected '" + RESOURCE_ENCODING_PROPERTY_NAME + "' system property not found.",
                           this.resourceEncoding );

        }

        return this.resourceEncoding;
    }

    /**
     * Sets the name of the encoding to use when reading resources.
     *
     * @param value The new name of the encoding to use when reading resources or {@code null}.
     *
     * @see #getResourceEncoding()
     */
    public final void setResourceEncoding( final String value )
    {
        this.resourceEncoding = value;
    }

    /**
     * Gets the output directory of instance.
     *
     * @return The output directory of instance.
     *
     * @see #setOutputDirectory(java.io.File)
     */
    public final File getOutputDirectory()
    {
        if ( this.outputDirectory == null )
        {
            final String name = System.getProperty( OUTPUT_DIRECTORY_PROPERTY_NAME );
            assertNotNull( "Expected '" + OUTPUT_DIRECTORY_PROPERTY_NAME + "' system property not found.", name );
            this.outputDirectory = new File( new File( name ), this.getClass().getSimpleName() );
            assertTrue( "Expected '" + OUTPUT_DIRECTORY_PROPERTY_NAME + "' system property to hold an absolute path.",
                        this.outputDirectory.isAbsolute() );

            if ( !this.outputDirectory.exists() )
            {
                assertTrue( this.outputDirectory.mkdirs() );
            }
        }

        return this.outputDirectory;
    }

    /**
     * Sets the output directory of instance.
     *
     * @param value The new output directory of instance or {@code null}.
     *
     * @see #getOutputDirectory()
     */
    public final void setOutputDirectory( final File value )
    {
        if ( value != null )
        {
            assertTrue( "Expected absolute 'outputDirectory'.", value.isAbsolute() );
        }

        this.outputDirectory = value;
    }

    /**
     * Gets the next output directory of the instance.
     *
     * @return The next output directory of the instance.
     */
    public final File getNextOutputDirectory()
    {
        try
        {
            final File nextOutputDirectory =
                new File( this.getOutputDirectory(), Integer.toString( this.outputDirectoryId++ ) );

            assertTrue( nextOutputDirectory.isAbsolute() );
            if ( nextOutputDirectory.exists() )
            {
                FileUtils.deleteDirectory( nextOutputDirectory );
            }

            return nextOutputDirectory;
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Gets the {@code JomcTool} instance tests are performed with.
     *
     * @return The {@code JomcTool} instance tests are performed with.
     *
     * @see #newJomcTool()
     */
    public JomcTool getJomcTool()
    {
        if ( this.jomcTool == null )
        {
            this.jomcTool = this.newJomcTool();
            this.jomcTool.setModel( this.getModel() );
            this.jomcTool.getListeners().add( new JomcTool.Listener()
            {

                @Override
                public void onLog( final Level level, final String message, final Throwable throwable )
                {
                    assertNotNull( level );

                    System.out.println( "[" + level.getLocalizedName() + "] " + message );

                    if ( throwable != null )
                    {
                        throwable.printStackTrace( System.out );
                    }
                }

            } );

        }

        return this.jomcTool;
    }

    /**
     * Creates a new {@code JomcTool} instance to test.
     *
     * @return A new {@code JomcTool} instance to test.
     *
     * @see #getJomcTool()
     */
    protected JomcTool newJomcTool()
    {
        return new JomcTool();
    }

    /**
     * Gets the {@code ModelContext} instance backing the test.
     *
     * @return The {@code ModelContext} instance backing the test.
     *
     * @see #newModelContext()
     */
    public ModelContext getModelContext()
    {
        if ( this.modelContext == null )
        {
            this.modelContext = this.newModelContext();
            this.modelContext.getListeners().add( new ModelContext.Listener()
            {

                @Override
                public void onLog( final Level level, String message, Throwable t )
                {
                    assertNotNull( level );

                    System.out.println( "[" + level.getLocalizedName() + "] " + message );

                    if ( t != null )
                    {
                        t.printStackTrace( System.out );
                    }
                }

            } );

        }

        return this.modelContext;
    }

    /**
     * Creates a new {@code ModelContext} instance backing the test.
     *
     * @return A new {@code ModelContext} instance backing the test.
     *
     * @see #getModelContext()
     */
    protected ModelContext newModelContext()
    {
        try
        {
            return ModelContext.createModelContext( this.getClass().getClassLoader() );
        }
        catch ( final ModelException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Gets the {@code Model} instance backing the test.
     *
     * @return The {@code Model} instance backing the test.
     *
     * @see #newModel()
     */
    public Model getModel()
    {
        if ( this.model == null )
        {
            this.model = this.newModel();
        }

        return this.model;
    }

    /**
     * Creates a new {@code Model} instance backing the test.
     *
     * @return A new {@code Model} instance backing the test.
     *
     * @see #getModel()
     */
    protected Model newModel()
    {
        try
        {
            final Unmarshaller u = this.getModelContext().createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
            u.setSchema( this.getModelContext().createSchema( ModelObject.MODEL_PUBLIC_ID ) );

            final JAXBElement<Module> module = (JAXBElement<Module>) u.unmarshal( this.getClass().getResource(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "jomc.xml" ) );

            final Modules modules = new Modules();
            modules.getModule().add( module.getValue() );

            final Module cp = modules.getClasspathModule(
                Modules.getDefaultClasspathModuleName(), this.getClass().getClassLoader() );

            if ( cp != null )
            {
                modules.getModule().add( cp );
            }

            final Model m = new Model();
            m.setIdentifier( ModelObject.MODEL_PUBLIC_ID );
            ModelHelper.setModules( m, modules );

            return m;
        }
        catch ( final JAXBException e )
        {
            throw new AssertionError( e );
        }
        catch ( final ModelException e )
        {
            throw new AssertionError( e );
        }
    }

    @Test
    public final void testJomcToolNullPointerException() throws Exception
    {
        assertNotNull( this.getJomcTool() );

        try
        {
            this.getJomcTool().getDisplayLanguage( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaClasspathLocation( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaClasspathLocation( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaGetterMethodName( (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaInterfaceNames( null, false );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaModifierName( null, (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavaModifierName( new Implementation(), (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaPackageName( (SpecificationReference) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Argument) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Implementation) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Property) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (Specification) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavaTypeName( (SpecificationReference) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getJavadocComment( null, 0, "\n" );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Text(), 0, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getJomcTool().getJavadocComment( new Text(), Integer.MIN_VALUE, "\n" );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        try
        {
            this.getJomcTool().getLongDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getLongDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getLongTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getShortTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getVelocityTemplate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getYears( null, Calendar.getInstance() );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getYears( Calendar.getInstance(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaDefaultPackage( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaDefaultPackage( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isJavaPrimitiveType( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().isLoggable( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Test
    public final void testJomcToolNotNull() throws Exception
    {
        final Specification specification = new Specification();
        specification.setClazz( "java.lang.Object" );
        specification.setIdentifier( "java.lang.Object" );

        final Specification defaultPackageSpecification = new Specification();
        defaultPackageSpecification.setClazz( "Object" );
        defaultPackageSpecification.setIdentifier( "Object" );

        final Implementation implementation = new Implementation();
        implementation.setIdentifier( "java.lang.Object" );
        implementation.setName( "java.lang.Object" );
        implementation.setClazz( "java.lang.Object" );

        final Implementation defaultPackageImplementation = new Implementation();
        defaultPackageImplementation.setIdentifier( "Object" );
        defaultPackageImplementation.setName( "Object" );
        defaultPackageImplementation.setClazz( "Object" );

        final Dependency d = new Dependency();
        d.setIdentifier( "java.util.Locale" );
        d.setName( "locale" );
        d.setImplementationName( "default" );

        final Property p = new Property();
        p.setName( "property" );
        p.setValue( "Test" );

        final Message m = new Message();
        m.setName( "message" );

        final Calendar now = Calendar.getInstance();
        final Calendar nextYear = Calendar.getInstance();
        nextYear.set( Calendar.YEAR, nextYear.get( Calendar.YEAR ) + 1 );

        assertNotNull( this.getJomcTool().getListeners() );
        assertNotNull( this.getJomcTool().getInputEncoding() );
        assertNotNull( this.getJomcTool().getModel() );
        assertNotNull( this.getJomcTool().getModules() );
        assertNotNull( this.getJomcTool().getOutputEncoding() );
        assertNotNull( this.getJomcTool().getTemplateProfile() );
        assertNotNull( this.getJomcTool().getTemplateEncoding() );
        assertNotNull( this.getJomcTool().getTemplateParameters() );
        assertNotNull( this.getJomcTool().getIndentation() );
        assertNotNull( this.getJomcTool().getLineSeparator() );
        assertNotNull( this.getJomcTool().getVelocityContext() );
        assertNotNull( this.getJomcTool().getVelocityEngine() );
        assertNotNull( JomcTool.getDefaultLogLevel() );
        assertNotNull( this.getJomcTool().getLongDate( now ) );
        assertNotNull( this.getJomcTool().getLongDateTime( now ) );
        assertNotNull( this.getJomcTool().getLongTime( now ) );
        assertNotNull( this.getJomcTool().getShortDate( now ) );
        assertNotNull( this.getJomcTool().getShortDateTime( now ) );
        assertNotNull( this.getJomcTool().getShortTime( now ) );
        assertNotNull( this.getJomcTool().getYears( now, now ) );
        assertNotNull( this.getJomcTool().getYears( now, nextYear ) );
        assertNotNull( this.getJomcTool().getYears( nextYear, now ) );
        assertNotNull( this.getJomcTool().getDisplayLanguage( "en" ) );
        assertEquals( this.getJomcTool().getYears( now, nextYear ), this.getJomcTool().getYears( nextYear, now ) );
        assertEquals( Locale.getDefault().getDisplayLanguage(),
                      this.getJomcTool().getDisplayLanguage( Locale.getDefault().getLanguage() ) );

        assertEquals( "java/lang/Object", this.getJomcTool().getJavaClasspathLocation( implementation ) );
        assertEquals( "Object", this.getJomcTool().getJavaClasspathLocation( defaultPackageImplementation ) );
        assertEquals( "java/lang/Object", this.getJomcTool().getJavaClasspathLocation( specification ) );
        assertEquals( "Object", this.getJomcTool().getJavaClasspathLocation( defaultPackageSpecification ) );
        assertEquals( "getLocale", this.getJomcTool().getJavaGetterMethodName( d ) );
        assertEquals( "getMessage", this.getJomcTool().getJavaGetterMethodName( m ) );
        assertEquals( "getProperty", this.getJomcTool().getJavaGetterMethodName( p ) );
        assertEquals( 0, this.getJomcTool().getJavaInterfaceNames( implementation, true ).size() );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, d ) );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, m ) );
        assertEquals( "private", this.getJomcTool().getJavaModifierName( implementation, p ) );
        assertEquals( "java.lang", this.getJomcTool().getJavaPackageName( implementation ) );
        assertEquals( "", this.getJomcTool().getJavaPackageName( defaultPackageImplementation ) );
        assertEquals( "java.lang", this.getJomcTool().getJavaPackageName( specification ) );
        assertEquals( "", this.getJomcTool().getJavaPackageName( defaultPackageSpecification ) );
        assertEquals( "java.util", this.getJomcTool().getJavaPackageName( d ) );
        assertEquals( "", this.getJomcTool().getJavaString( "" ) );
        assertEquals( this.getJomcTool().getIndentation(), this.getJomcTool().getIndentation( 1 ) );
    }

    @Test
    public final void testVelocityTemplates() throws Exception
    {
        assertNotNull( this.getJomcTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );
        assertNotNull( this.getJomcTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getJomcTool().setTemplateProfile( null );

        try
        {
            this.getJomcTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getJomcTool().setTemplateProfile( "DOES_NOT_EXIST" );
            this.getJomcTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    @Test
    public final void testDefaultLogLevel() throws Exception
    {
        final String testLogLevel = System.getProperty( "org.jomc.tools.JomcTool.defaultLogLevel" );

        assertNotNull( JomcTool.getDefaultLogLevel() );
        JomcTool.setDefaultLogLevel( null );
        System.setProperty( "org.jomc.tools.JomcTool.defaultLogLevel", "OFF" );
        assertEquals( Level.OFF, JomcTool.getDefaultLogLevel() );

        if ( testLogLevel != null )
        {
            System.setProperty( "org.jomc.tools.JomcTool.defaultLogLevel", testLogLevel );
        }
        else
        {
            System.clearProperty( "org.jomc.tools.JomcTool.defaultLogLevel" );
        }

        JomcTool.setDefaultLogLevel( null );
    }

    @Test
    public final void testLogLevel() throws Exception
    {
        JomcTool.setDefaultLogLevel( null );
        this.getJomcTool().setLogLevel( null );
        assertNotNull( this.getJomcTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( Level.OFF );
        this.getJomcTool().setLogLevel( null );
        assertEquals( Level.OFF, this.getJomcTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( null );
        this.getJomcTool().setLogLevel( null );
    }

    @Test
    public final void testDefaultTemplateProfile() throws Exception
    {
        assertNotNull( JomcTool.getDefaultTemplateProfile() );
        System.setProperty( "org.jomc.tools.JomcTool.defaultTemplateProfile", "TEST" );
        JomcTool.setDefaultTemplateProfile( null );
        assertEquals( "TEST", JomcTool.getDefaultTemplateProfile() );
        System.clearProperty( "org.jomc.tools.JomcTool.defaultTemplateProfile" );
        JomcTool.setDefaultTemplateProfile( null );
    }

    @Test
    public final void testTemplateProfile() throws Exception
    {
        JomcTool.setDefaultTemplateProfile( null );
        this.getJomcTool().setTemplateProfile( null );
        assertNotNull( this.getJomcTool().getTemplateProfile() );

        JomcTool.setDefaultTemplateProfile( "TEST" );
        this.getJomcTool().setTemplateProfile( null );
        assertEquals( "TEST", this.getJomcTool().getTemplateProfile() );

        JomcTool.setDefaultTemplateProfile( null );
        this.getJomcTool().setTemplateProfile( null );
    }

    @Test
    public final void testIndentation() throws Exception
    {
        assertEquals( "", this.getJomcTool().getIndentation( 0 ) );
        assertEquals( this.getJomcTool().getIndentation(), this.getJomcTool().getIndentation( 1 ) );

        try
        {
            this.getJomcTool().getIndentation( Integer.MIN_VALUE );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        this.getJomcTool().setIndentation( "    TEST    " );
        assertEquals( "    TEST    ", this.getJomcTool().getIndentation() );
        assertEquals( "    TEST    ", this.getJomcTool().getIndentation( 1 ) );
        this.getJomcTool().setIndentation( null );
    }

    @Test
    public final void testModel() throws Exception
    {
        final Model m = this.getJomcTool().getModel();
        this.getJomcTool().setModel( null );
        assertNotNull( this.getJomcTool().getModel() );
        this.getJomcTool().setModel( m );
    }

    @Test
    public final void testVelocityEngine() throws Exception
    {
        this.getJomcTool().setVelocityEngine( null );
        assertNotNull( this.getJomcTool().getVelocityEngine() );
        this.getJomcTool().setVelocityEngine( null );
    }

    @Test
    public final void testTemplateEncoding() throws Exception
    {
        this.getJomcTool().setTemplateEncoding( null );
        assertNotNull( this.getJomcTool().getTemplateEncoding() );
        this.getJomcTool().setTemplateEncoding( null );
    }

    @Test
    public final void testInputEncoding() throws Exception
    {
        this.getJomcTool().setInputEncoding( null );
        assertNotNull( this.getJomcTool().getInputEncoding() );
        this.getJomcTool().setInputEncoding( null );
    }

    @Test
    public final void testOutputEncoding() throws Exception
    {
        this.getJomcTool().setOutputEncoding( null );
        assertNotNull( this.getJomcTool().getOutputEncoding() );
        this.getJomcTool().setOutputEncoding( null );
    }

    @Test
    public final void testLineSeparator() throws Exception
    {
        this.getJomcTool().setLineSeparator( null );
        assertNotNull( this.getJomcTool().getLineSeparator() );
        this.getJomcTool().setLineSeparator( null );
    }

    public static void assertNullPointerException( final NullPointerException e )
    {
        assertNotNull( e );
        assertNotNull( e.getMessage() );
        System.out.println( e.toString() );
    }

    public static void assertIllegalArgumentException( final IllegalArgumentException e )
    {
        assertNotNull( e );
        assertNotNull( e.getMessage() );
        System.out.println( e.toString() );
    }

}
