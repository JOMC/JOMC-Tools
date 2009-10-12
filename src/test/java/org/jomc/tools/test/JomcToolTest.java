/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.jomc.model.Argument;
import org.jomc.model.DefaultModelManager;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Message;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Property;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Text;
import org.jomc.tools.JomcTool;
import org.xml.sax.SAXException;

/**
 * Base tool test class.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public abstract class JomcToolTest extends TestCase
{

    /** Test listener. */
    private static final JomcTool.Listener LISTENER = new JomcTool.Listener()
    {

        public void onLog( final Level level, final String message, final Throwable throwable )
        {
            Assert.assertNotNull( level );

            final StringBuilder b = new StringBuilder();
            b.append( '[' ).append( level.getLocalizedName() ).append( "] " );

            if ( message != null )
            {
                b.append( message );
            }

            System.out.println( b.toString() );
        }

    };

    /** The test {@code Modules} of the instance. */
    private Modules testModules;

    /**
     * Gets the tool tests are performed with.
     *
     * @return The tool tests are performed with.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources fails.
     */
    public abstract JomcTool getTestTool() throws IOException, SAXException, JAXBException;

    /**
     * Gets the {@code Modules} tests are performed with.
     *
     * @return The {@code Modules} tests are performed with.
     *
     * @throws IOException if reading schema resources fails.
     * @throws SAXException if parsing schema resources fails.
     * @throws JAXBException if unmarshalling schema resources fails.
     */
    public Modules getTestModules() throws IOException, SAXException, JAXBException
    {
        if ( this.testModules == null )
        {
            this.testModules = new Modules();
            final DefaultModelManager defaultModelManager = new DefaultModelManager();
            final Unmarshaller u = defaultModelManager.getUnmarshaller( false );
            final JAXBElement<Module> m =
                (JAXBElement<Module>) u.unmarshal( this.getClass().getResource( "jomc.xml" ) );

            this.testModules.getModule().add( m.getValue() );

            final Module cp = defaultModelManager.getClasspathModule( this.testModules );
            if ( cp != null )
            {
                this.testModules.getModule().add( cp );
            }
        }

        return this.testModules;
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
        Assert.assertNotNull( this.getTestTool() );

        try
        {
            this.getTestTool().getDisplayLanguage( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClasspathLocation( (Implementation) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaClasspathLocation( (Specification) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Dependency) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Message) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaGetterMethodName( (Property) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaInterfaceNames( null, false );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Dependency) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Message) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaModifierName( null, (Property) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (Implementation) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (Specification) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaPackageName( (SpecificationReference) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Argument) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Dependency) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Implementation) null, true );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Property) null, true );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (Specification) null, true );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavaTypeName( (SpecificationReference) null, true );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavadocComment( null, "\n" );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getJavadocComment( new Text(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getLongDate( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getLongDateTime( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getLongTime( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortDate( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortDateTime( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getShortTime( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getVelocityTemplate( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getYears( null, Calendar.getInstance() );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getYears( Calendar.getInstance(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaClassDeclaration( (Implementation) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaClassDeclaration( (Specification) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaDefaultPackage( (Implementation) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaDefaultPackage( (Specification) null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().isJavaPrimitiveType( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    /** Tests that method declaring not to return {@code null} do not return {@code null}. */
    public void testNotNull() throws Exception
    {
        Assert.assertNotNull( this.getTestTool().getListeners() );
        Assert.assertNotNull( this.getTestTool().getInputEncoding() );
        Assert.assertNotNull( this.getTestTool().getModelManager() );
        Assert.assertNotNull( this.getTestTool().getModules() );
        Assert.assertNotNull( this.getTestTool().getOutputEncoding() );
        Assert.assertNotNull( this.getTestTool().getProfile() );
        Assert.assertNotNull( this.getTestTool().getTemplateEncoding() );
        Assert.assertNotNull( this.getTestTool().getVelocityContext() );
        Assert.assertNotNull( this.getTestTool().getVelocityEngine() );
    }

    /** Tests the {@code getVelocityTemplate} method. */
    public void testVelocityTemplate() throws Exception
    {
        Assert.assertNotNull( this.getTestTool().getVelocityTemplate( "Bundle.java.vm" ) );
        this.getTestTool().setProfile( "DOES_NOT_EXIST" );
        Assert.assertNotNull( this.getTestTool().getVelocityTemplate( "Bundle.java.vm" ) );
        this.getTestTool().setProfile( null );

        try
        {
            this.getTestTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            Assert.fail( "Expected IOException not thrown." );
        }
        catch ( IOException e )
        {
            Assert.assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        try
        {
            this.getTestTool().setProfile( "DOES_NOT_EXIST" );
            this.getTestTool().getVelocityTemplate( "DOES_NOT_EXIST" );
            Assert.fail( "Expected IOException not thrown." );
        }
        catch ( IOException e )
        {
            Assert.assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

    public static void assertNullPointerException( final NullPointerException e )
    {
        Assert.assertNotNull( e );
        Assert.assertNotNull( e.getMessage() );
        System.out.println( e.toString() );
    }

}
