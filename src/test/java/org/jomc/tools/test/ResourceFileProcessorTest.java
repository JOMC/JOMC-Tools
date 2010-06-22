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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.tools.ResourceFileProcessor;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.ResourceFileProcessor}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ResourceFileProcessorTest extends JomcToolTest
{

    /** Serial number of the test resources directory. */
    private int testResourcesId;

    /** The {@code ResourceFileProcessor} instance tests are performed with. */
    private ResourceFileProcessor testTool;

    @Override
    public ResourceFileProcessor getTestTool() throws IOException
    {
        if ( this.testTool == null )
        {
            this.testTool = new ResourceFileProcessor();
            this.testTool.setModules( this.getTestModules() );
        }

        return this.testTool;
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
        final File testResourcesDirectory =
            new File( this.getTestProperty( "testResourcesDirectory" ), Integer.toString( this.testResourcesId++ ) );

        if ( testResourcesDirectory.exists() )
        {
            FileUtils.cleanDirectory( testResourcesDirectory );
        }

        return testResourcesDirectory;
    }

    private String getTestProperty( final String key ) throws IOException
    {
        final java.util.Properties p = new java.util.Properties();
        final InputStream in = this.getClass().getResourceAsStream( "ResourceFileProcessorTest.properties" );
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
            this.getTestTool().getResourceBundleResources( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().getResourceBundleResources( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( (Module) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( new Module(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( (Specification) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( new Specification(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( (Implementation) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getTestTool().writeResourceBundleResourceFiles( new Implementation(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Override
    public void testNotNull() throws Exception
    {
        super.testNotNull();

        final Specification s = new Specification();
        s.setIdentifier( "TEST" );
        s.setClazz( "TEST" );

        final Implementation i = new Implementation();
        i.setIdentifier( "TEST" );
        i.setClazz( "TEST" );

        assertNotNull( this.getTestTool().getResourceBundleDefaultLocale() );
        assertNotNull( this.getTestTool().getResourceBundleResources( s ) );
        assertNotNull( this.getTestTool().getResourceBundleResources( i ) );
    }

    public void testWriteResourceBundleResourceFiles() throws Exception
    {
        this.getTestTool().writeResourceBundleResourceFiles( this.getTestResourcesDirectory() );

        this.getTestTool().writeResourceBundleResourceFiles(
            this.getTestTool().getModules().getModule( "Module" ), this.getTestResourcesDirectory() );

        this.getTestTool().writeResourceBundleResourceFiles(
            this.getTestTool().getModules().getSpecification( "Specification" ), this.getTestResourcesDirectory() );

        this.getTestTool().writeResourceBundleResourceFiles(
            this.getTestTool().getModules().getImplementation( "Implementation" ), this.getTestResourcesDirectory() );

    }

    public void testCopyConstructor() throws Exception
    {
        try
        {
            new ResourceFileProcessor( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        new ResourceFileProcessor( this.getTestTool() );
    }

}
