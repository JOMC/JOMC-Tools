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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.tools.JavaBundles;
import org.xml.sax.SAXException;

/**
 * Tests {@code JavaBundles} implementations.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class JavaBundlesTest extends JomcToolTest
{

    /** Serial number of the test sources directory. */
    private int testSourcesId;

    /** Serial number of the test resources directory. */
    private int testResourcesId;

    /** The {@code JavaBundles} instance tests are performed with. */
    private JavaBundles testTool;

    @Override
    public JavaBundles getTestTool() throws IOException, SAXException, JAXBException
    {
        if ( this.testTool == null )
        {
            this.testTool = new JavaBundles();
            this.testTool.setModules( this.getTestModules() );
        }

        return this.testTool;
    }

    /**
     * Gets the directory to write sources to.
     *
     * @return The directory to write sources to.
     *
     * @throws IOException if getting the directory fails.
     */
    public File getTestSourcesDirectory() throws IOException
    {
        return new File( this.getTestProperty( "testSourcesDirectory" ), Integer.toString( this.testSourcesId++ ) );
    }

    /**
     * Gets the directory to write resources to.
     *
     * @return The directory to write resources to.
     *
     * @throws IOException if getting the directory fails.
     */
    public File getTestResourcesDirectory() throws IOException
    {
        return new File( this.getTestProperty( "testResourcesDirectory" ), Integer.toString( this.testResourcesId++ ) );
    }

    private String getTestProperty( final String key ) throws IOException
    {
        final java.util.Properties p = new java.util.Properties();
        final InputStream in = this.getClass().getResourceAsStream( "JavaBundlesTest.properties" );
        p.load( in );
        in.close();

        return p.getProperty( key );
    }

    @Override
    public void testNullPointerException() throws Exception
    {
        super.testNullPointerException();

        try
        {
            this.getTestTool().getResourceBundleResources( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getResourceBundleSources( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleResources( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleResources( (Module) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleResources( new Module(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleResources( (Implementation) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleResources( new Implementation(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleSources( null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleSources( (Implementation) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleSources( new Implementation(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleSources( (Module) null, new File( "/" ) );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeBundleSources( new Module(), null );
            Assert.fail( "Expected NullPointerException not thrown." );
        }
        catch ( NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Override
    public void testNotNull() throws Exception
    {
        super.testNotNull();

        final Implementation i = new Implementation();
        i.setIdentifier( "TEST" );
        i.setClazz( "TEST" );

        Assert.assertNotNull( this.getTestTool().getDefaultLocale() );
        Assert.assertNotNull( this.getTestTool().getResourceBundleResources( i ) );
        Assert.assertNotNull( this.getTestTool().getResourceBundleSources( i ) );
    }

    public void testWriteBundleSources() throws Exception
    {
        this.getTestTool().writeBundleSources( this.getTestSourcesDirectory() );
        this.getTestTool().writeBundleSources( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                               this.getTestSourcesDirectory() );

        this.getTestTool().writeBundleSources( this.getTestTool().getModules().getModule( "Module" ),
                                               this.getTestSourcesDirectory() );

        this.getTestTool().setProfile( "DOES_NOT_EXIST" );

        this.getTestTool().writeBundleSources( this.getTestSourcesDirectory() );
        this.getTestTool().writeBundleSources( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                               this.getTestSourcesDirectory() );

        this.getTestTool().writeBundleSources( this.getTestTool().getModules().getModule( "Module" ),
                                               this.getTestSourcesDirectory() );

    }

    public void testWriteBundleResources() throws Exception
    {
        this.getTestTool().writeBundleResources( this.getTestResourcesDirectory() );
        this.getTestTool().writeBundleResources( this.getTestTool().getModules().getImplementation( "Implementation" ),
                                                 this.getTestResourcesDirectory() );

        this.getTestTool().writeBundleResources( this.getTestTool().getModules().getModule( "Module" ),
                                                 this.getTestResourcesDirectory() );

    }

}
