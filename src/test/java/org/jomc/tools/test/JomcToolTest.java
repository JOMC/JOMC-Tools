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

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;
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
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Base tool test class.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public abstract class JomcToolTest extends TestCase
{

    /** Test listener. */
    private static final JomcTool.Listener LISTENER = new JomcTool.Listener()
    {

        public void onLog( final Level level, final String message, final Throwable throwable )
        {
            assertNotNull( level );

            final StringBuilder b = new StringBuilder();
            b.append( '[' ).append( level.getLocalizedName() ).append( "] " );

            if ( message != null )
            {
                b.append( message );
            }

            System.out.println( b.toString() );
        }

    };

    /** The {@code ModelContext} of the instance. */
    private ModelContext modelContext;

    /** The test {@code Model} of the instance. */
    private Model testModel;

    /**
     * Gets the tool tests are performed with.
     *
     * @return The tool tests are performed with.
     *
     * @throws IOException if getting the tool fails.
     */
    public abstract JomcTool getTestTool() throws IOException;

    /**
     * Gets the {@code ModelContext} of the instance.
     *
     * @return The {@code ModelContext} of the instance.
     *
     * @throws ModelException if creating a new {@code ModelContext} instance fails.
     */
    public ModelContext getModelContext() throws ModelException
    {
        if ( this.modelContext == null )
        {
            this.modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
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
     * Gets the {@code Model} tests are performed with.
     *
     * @return The {@code Model} tests are performed with.
     *
     * @throws IOException if getting the model fails.
     */
    public Model getTestModel() throws IOException
    {
        try
        {
            if ( this.testModel == null )
            {
                final Unmarshaller u = this.getModelContext().createUnmarshaller( ModelObject.MODEL_PUBLIC_ID );
                u.setSchema( this.getModelContext().createSchema( ModelObject.MODEL_PUBLIC_ID ) );

                final JAXBElement<Module> m =
                    (JAXBElement<Module>) u.unmarshal( this.getClass().getResource( "jomc.xml" ) );

                final Modules modules = new Modules();
                modules.getModule().add( m.getValue() );

                final Module cp = modules.getClasspathModule(
                    Modules.getDefaultClasspathModuleName(), this.getClass().getClassLoader() );

                if ( cp != null )
                {
                    modules.getModule().add( cp );
                }

                this.testModel = new Model();
                this.testModel.setIdentifier( ModelObject.MODEL_PUBLIC_ID );
                ModelHelper.setModules( this.testModel, modules );
            }

            return this.testModel;
        }
        catch ( final JAXBException e )
        {
            String message = getMessage( e );
            if ( message == null && e.getLinkedException() != null )
            {
                message = getMessage( e.getLinkedException() );
            }

            throw (IOException) new IOException( message ).initCause( e );
        }
        catch ( final ModelException e )
        {
            throw (IOException) new IOException( getMessage( e ) ).initCause( e );
        }
    }

    @Override
    public void setUp() throws Exception
    {
        this.getTestTool().getListeners().add( LISTENER );
    }

    @Override
    public void tearDown() throws Exception
    {
        this.getTestTool().getListeners().remove( LISTENER );
    }

    /**
     * Tests that methods declaring to throw a {@code NullPointerException} do not throw an instance with a {@code null}
     * message.
     */
    public void testNullPointerException() throws Exception
    {
        assertNotNull( this.getTestTool() );

        try
        {
            this.getTestTool().getDisplayLanguage( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClasspathLocation( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClasspathLocation( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaInterfaceNames( null, false );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavaModifierName( new Implementation(), (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavaModifierName( new Implementation(), (Message) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavaModifierName( new Implementation(), (Property) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (SpecificationReference) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Argument) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Dependency) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Implementation) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Property) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Specification) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (SpecificationReference) null, true );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavadocComment( null, 0, "\n" );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavadocComment( new Text(), 0, null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
        try
        {
            this.getTestTool().getJavadocComment( new Text(), Integer.MIN_VALUE, "\n" );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        try
        {
            this.getTestTool().getLongDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getLongDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getLongTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortDate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortDateTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortTime( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getVelocityTemplate( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getYears( null, Calendar.getInstance() );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getYears( Calendar.getInstance(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaDefaultPackage( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaDefaultPackage( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaPrimitiveType( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isLoggable( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    /** Tests that method declaring not to return {@code null} do not return {@code null}. */
    public void testNotNull() throws Exception
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

        assertNotNull( this.getTestTool().getListeners() );
        assertNotNull( this.getTestTool().getInputEncoding() );
        assertNotNull( this.getTestTool().getModel() );
        assertNotNull( this.getTestTool().getModules() );
        assertNotNull( this.getTestTool().getOutputEncoding() );
        assertNotNull( this.getTestTool().getTemplateProfile() );
        assertNotNull( this.getTestTool().getTemplateEncoding() );
        assertNotNull( this.getTestTool().getIndentation() );
        assertNotNull( this.getTestTool().getLineSeparator() );
        assertNotNull( this.getTestTool().getVelocityContext() );
        assertNotNull( this.getTestTool().getVelocityEngine() );
        assertNotNull( JomcTool.getDefaultLogLevel() );
        assertNotNull( this.getTestTool().getLongDate( now ) );
        assertNotNull( this.getTestTool().getLongDateTime( now ) );
        assertNotNull( this.getTestTool().getLongTime( now ) );
        assertNotNull( this.getTestTool().getShortDate( now ) );
        assertNotNull( this.getTestTool().getShortDateTime( now ) );
        assertNotNull( this.getTestTool().getShortTime( now ) );
        assertNotNull( this.getTestTool().getYears( now, now ) );
        assertNotNull( this.getTestTool().getYears( now, nextYear ) );
        assertNotNull( this.getTestTool().getYears( nextYear, now ) );
        assertNotNull( this.getTestTool().getDisplayLanguage( "en" ) );
        assertEquals( this.getTestTool().getYears( now, nextYear ), this.getTestTool().getYears( nextYear, now ) );
        assertEquals( Locale.getDefault().getDisplayLanguage(),
                      this.getTestTool().getDisplayLanguage( Locale.getDefault().getLanguage() ) );

        assertEquals( "java/lang/Object", this.getTestTool().getJavaClasspathLocation( implementation ) );
        assertEquals( "Object", this.getTestTool().getJavaClasspathLocation( defaultPackageImplementation ) );
        assertEquals( "java/lang/Object", this.getTestTool().getJavaClasspathLocation( specification ) );
        assertEquals( "Object", this.getTestTool().getJavaClasspathLocation( defaultPackageSpecification ) );
        assertEquals( "getLocale", this.getTestTool().getJavaGetterMethodName( d ) );
        assertEquals( "getMessage", this.getTestTool().getJavaGetterMethodName( m ) );
        assertEquals( "getProperty", this.getTestTool().getJavaGetterMethodName( p ) );
        assertEquals( 0, this.getTestTool().getJavaInterfaceNames( implementation, true ).size() );
        assertEquals( "private", this.getTestTool().getJavaModifierName( implementation, d ) );
        assertEquals( "private", this.getTestTool().getJavaModifierName( implementation, m ) );
        assertEquals( "private", this.getTestTool().getJavaModifierName( implementation, p ) );
        assertEquals( "java.lang", this.getTestTool().getJavaPackageName( implementation ) );
        assertEquals( "", this.getTestTool().getJavaPackageName( defaultPackageImplementation ) );
        assertEquals( "java.lang", this.getTestTool().getJavaPackageName( specification ) );
        assertEquals( "", this.getTestTool().getJavaPackageName( defaultPackageSpecification ) );
        assertEquals( "java.util", this.getTestTool().getJavaPackageName( d ) );
        assertEquals( "", this.getTestTool().getJavaString( "" ) );
        assertEquals( this.getTestTool().getIndentation(), this.getTestTool().getIndentation( 1 ) );
    }

    /** Tests the {@code getVelocityTemplate} method. */
    public void testVelocityTemplate() throws Exception
    {
        assertNotNull( this.getTestTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getTestTool().setTemplateProfile( "DOES_NOT_EXIST" );
        assertNotNull( this.getTestTool().getVelocityTemplate( "Implementation.java.vm" ) );
        this.getTestTool().setTemplateProfile( null );

        try
        {
            this.getTestTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getTestTool().setTemplateProfile( "DOES_NOT_EXIST" );
            this.getTestTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    public void testDefaultLogLevel() throws Exception
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

    public void testLogLevel() throws Exception
    {
        JomcTool.setDefaultLogLevel( null );
        this.getTestTool().setLogLevel( null );
        assertNotNull( this.getTestTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( Level.OFF );
        this.getTestTool().setLogLevel( null );
        assertEquals( Level.OFF, this.getTestTool().getLogLevel() );

        JomcTool.setDefaultLogLevel( null );
        this.getTestTool().setLogLevel( null );
    }

    public void testDefaultTemplateProfile() throws Exception
    {
        assertNotNull( JomcTool.getDefaultTemplateProfile() );
        System.setProperty( "org.jomc.tools.JomcTool.defaultTemplateProfile", "TEST" );
        JomcTool.setDefaultTemplateProfile( null );
        assertEquals( "TEST", JomcTool.getDefaultTemplateProfile() );
        System.clearProperty( "org.jomc.tools.JomcTool.defaultTemplateProfile" );
        JomcTool.setDefaultTemplateProfile( null );
    }

    public void testTemplateProfile() throws Exception
    {
        JomcTool.setDefaultTemplateProfile( null );
        this.getTestTool().setTemplateProfile( null );
        assertNotNull( this.getTestTool().getTemplateProfile() );

        JomcTool.setDefaultTemplateProfile( "TEST" );
        this.getTestTool().setTemplateProfile( null );
        assertEquals( "TEST", this.getTestTool().getTemplateProfile() );

        JomcTool.setDefaultTemplateProfile( null );
        this.getTestTool().setTemplateProfile( null );
    }

    public void testIndentation() throws Exception
    {
        assertEquals( "", this.getTestTool().getIndentation( 0 ) );
        assertEquals( this.getTestTool().getIndentation(), this.getTestTool().getIndentation( 1 ) );

        try
        {
            this.getTestTool().getIndentation( Integer.MIN_VALUE );
            fail( "Expected IllegalArgumentException not thrown." );
        }
        catch ( final IllegalArgumentException e )
        {
            assertIllegalArgumentException( e );
        }

        this.getTestTool().setIndentation( "    TEST    " );
        assertEquals( "    TEST    ", this.getTestTool().getIndentation() );
        assertEquals( "    TEST    ", this.getTestTool().getIndentation( 1 ) );
        this.getTestTool().setIndentation( null );
    }

    public void testModel() throws Exception
    {
        final Model model = this.getTestTool().getModel();
        this.getTestTool().setModel( null );
        assertNotNull( this.getTestTool().getModel() );
        this.getTestTool().setModel( model );
    }

    public void testVelocityEngine() throws Exception
    {
        this.getTestTool().setVelocityEngine( null );
        assertNotNull( this.getTestTool().getVelocityEngine() );
        this.getTestTool().setVelocityEngine( null );
    }

    public void testTemplateEncoding() throws Exception
    {
        this.getTestTool().setTemplateEncoding( null );
        assertNotNull( this.getTestTool().getTemplateEncoding() );
        this.getTestTool().setTemplateEncoding( null );
    }

    public void testInputEncoding() throws Exception
    {
        this.getTestTool().setInputEncoding( null );
        assertNotNull( this.getTestTool().getInputEncoding() );
        this.getTestTool().setInputEncoding( null );
    }

    public void testOutputEncoding() throws Exception
    {
        this.getTestTool().setOutputEncoding( null );
        assertNotNull( this.getTestTool().getOutputEncoding() );
        this.getTestTool().setOutputEncoding( null );
    }

    public void testLineSeparator() throws Exception
    {
        this.getTestTool().setLineSeparator( null );
        assertNotNull( this.getTestTool().getLineSeparator() );
        this.getTestTool().setLineSeparator( null );
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

    private static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
