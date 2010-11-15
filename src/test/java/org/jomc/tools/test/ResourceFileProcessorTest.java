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

import org.junit.Test;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.jomc.model.Implementation;
import org.jomc.model.Module;
import org.jomc.model.Specification;
import org.jomc.tools.ResourceFileProcessor;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    /** Properties backing the instance. */
    private Properties testProperties;

    /** {@inheritDoc} */
    @Override
    public ResourceFileProcessor getJomcTool()
    {
        return (ResourceFileProcessor) super.getJomcTool();
    }

    /** {@inheritDoc} */
    @Override
    protected ResourceFileProcessor newJomcTool()
    {
        return new ResourceFileProcessor();
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

        assertTrue( testResourcesDirectory.isAbsolute() );

        if ( testResourcesDirectory.exists() )
        {
            FileUtils.deleteDirectory( testResourcesDirectory );
        }

        return testResourcesDirectory;
    }

    private String getTestProperty( final String key ) throws IOException
    {
        if ( this.testProperties == null )
        {
            this.testProperties = new java.util.Properties();
            final InputStream in = this.getClass().getResourceAsStream( "ResourceFileProcessorTest.properties" );
            this.testProperties.load( in );
            in.close();
        }

        final String value = this.testProperties.getProperty( key );
        assertNotNull( value );
        return value;
    }

    @Test
    public final void testResourceFileProcessorNullPointerException() throws Exception
    {
        try
        {
            this.getJomcTool().getResourceBundleResources( (Specification) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().getResourceBundleResources( (Implementation) null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( (Module) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( new Module(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( (Specification) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( new Specification(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( (Implementation) null, new File( "/" ) );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( new Implementation(), null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNullPointerException( e );
        }
    }

    @Test
    public final void testResourceFileProcessorNotNull() throws Exception
    {
        assertNotNull( this.getJomcTool().getResourceBundleDefaultLocale() );
        assertNotNull( this.getJomcTool().getResourceBundleResources(
            this.getJomcTool().getModules().getSpecification( "Specification" ) ) );

        assertNotNull( this.getJomcTool().getResourceBundleResources(
            this.getJomcTool().getModules().getImplementation( "Implementation" ) ) );

    }

    @Test
    public final void testResourceBundleDefaultLocale() throws Exception
    {
        this.getJomcTool().setResourceBundleDefaultLocale( null );
        assertNotNull( this.getJomcTool().getResourceBundleDefaultLocale() );
        this.getJomcTool().setResourceBundleDefaultLocale( null );
    }

    @Test
    public final void testWriteResourceBundleResourceFiles() throws Exception
    {
        final File nonExistentDirectory = this.getTestResourcesDirectory();
        if ( nonExistentDirectory.exists() )
        {
            FileUtils.deleteDirectory( nonExistentDirectory );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles( nonExistentDirectory );
            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles(
                this.getJomcTool().getModules().getModule( "Module" ), nonExistentDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles(
                this.getJomcTool().getModules().getSpecification( "Specification" ), nonExistentDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTool().writeResourceBundleResourceFiles(
                this.getJomcTool().getModules().getImplementation( "Implementation" ), nonExistentDirectory );

            fail( "Expected IOException not thrown." );
        }
        catch ( final IOException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        File resourcesDirectory = this.getTestResourcesDirectory();
        assertTrue( resourcesDirectory.mkdirs() );
        this.getJomcTool().writeResourceBundleResourceFiles( resourcesDirectory );

        resourcesDirectory = this.getTestResourcesDirectory();
        assertTrue( resourcesDirectory.mkdirs() );
        this.getJomcTool().writeResourceBundleResourceFiles(
            this.getJomcTool().getModules().getModule( "Module" ), resourcesDirectory );

        resourcesDirectory = this.getTestResourcesDirectory();
        assertTrue( resourcesDirectory.mkdirs() );
        this.getJomcTool().writeResourceBundleResourceFiles(
            this.getJomcTool().getModules().getSpecification( "Specification" ), resourcesDirectory );

        resourcesDirectory = this.getTestResourcesDirectory();
        assertTrue( resourcesDirectory.mkdirs() );
        this.getJomcTool().writeResourceBundleResourceFiles(
            this.getJomcTool().getModules().getImplementation( "Implementation" ), resourcesDirectory );

    }

    @Test
    public final void testCopyConstructor() throws Exception
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

        new ResourceFileProcessor( this.getJomcTool() );
    }

}
