/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.ant.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.jomc.ant.JomcTask;
import org.jomc.ant.types.KeyValueType;
import org.jomc.ant.types.NameType;
import org.jomc.ant.types.PropertiesResourceType;
import org.jomc.ant.types.TransformerResourceType;
import org.jomc.model.ModelObject;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.Modlet;
import org.jomc.modlet.ModletObject;
import org.jomc.modlet.Modlets;
import org.jomc.modlet.ObjectFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.jomc.ant.test.Assert.assertMessageLogged;
import static org.jomc.ant.test.Assert.assertMessageNotLogged;
import static org.jomc.ant.test.Assert.assertNoException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.ant.JomcTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcTaskTest
{

    /** Constant to prefix relative resource names with. */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/ant/test/";

    /** Cached default locale. */
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /** Constant for the name of the system property holding the output directory for the test. */
    private static final String OUTPUT_DIRECTORY_PROPERTY_NAME = "jomc.test.outputDirectory";

    /** The {@code JomcTask} instance tests are performed with. */
    private JomcTask jomcTask;

    /** The {@code Project} backing the test. */
    private Project project;

    /** The {@code AntExecutor} backing the test. */
    private AntExecutor antExecutor;

    /** The output directory of the instance. */
    private File outputDirectory;

    /** Creates a new {@code JomcTaskTest} instance. */
    public JomcTaskTest()
    {
        super();
    }

    /**
     * Gets the output directory of instance.
     *
     * @return The output directory of instance.
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
     * Gets the {@code JomcTask} instance tests are performed with.
     *
     * @return The {@code JomcTask} instance tests are performed with.
     *
     * @see #newJomcTask()
     */
    public JomcTask getJomcTask()
    {
        if ( this.jomcTask == null )
        {
            this.jomcTask = this.newJomcTask();
            this.jomcTask.setProject( new Project() );
            this.jomcTask.getProject().init();
        }

        return this.jomcTask;
    }

    /**
     * Creates a new {@code JomcTask} instance to test.
     *
     * @return A new {@code JomcTask} instance to test.
     *
     * @see #getJomcTask()
     */
    protected JomcTask newJomcTask()
    {
        return new JomcTask();
    }

    /**
     * Gets the {@code Project} backing the test.
     *
     * @return The {@code Project} backing the test.
     *
     * @see #newProject()
     */
    public Project getProject()
    {
        if ( this.project == null )
        {
            this.project = this.newProject();
        }

        return this.project;
    }

    /**
     * Creates and configures a new {@code Project} instance backing the test.
     *
     * @return A new {@code Project} instance backing the test.
     *
     * @see #getProject()
     * @see #getBuildFileName()
     */
    protected Project newProject()
    {
        try
        {
            final Project p = new Project();
            p.init();

            final URL buildFileResource =
                this.getClass().getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + this.getBuildFileName() );

            assertNotNull( "Expected '" + this.getBuildFileName() + "' resource not found.", buildFileResource );
            final File buildFile = new File( this.getOutputDirectory(), this.getBuildFileName() );
            assertTrue( buildFile.isAbsolute() );
            FileUtils.copyURLToFile( buildFileResource, buildFile );

            final URL classfilesResource =
                this.getClass().getResource( ABSOLUTE_RESOURCE_NAME_PREFIX + "classfiles.zip" );

            assertNotNull( "Expected 'classfiles.zip' resource not found.", classfilesResource );
            final File classfilesZip = new File( this.getOutputDirectory(), "classfiles.zip" );
            assertTrue( classfilesZip.isAbsolute() );
            FileUtils.copyURLToFile( classfilesResource, classfilesZip );

            final File classpathDirectory = new File( new File( this.getOutputDirectory(), "redundant" ), "META-INF" );
            assertTrue( classpathDirectory.isAbsolute() );

            if ( !classpathDirectory.exists() )
            {
                assertTrue( classpathDirectory.mkdirs() );
            }

            final ModelContext modelContext = ModelContext.createModelContext( this.getClass().getClassLoader() );
            final Modlets modlets = modelContext.getModlets();
            final Modlet redundantModlet =
                modlets.getMergedModlet( "JOMC Ant Tasks Tests", ModelObject.MODEL_PUBLIC_ID );

            final Marshaller marshaller = modelContext.createMarshaller( ModletObject.MODEL_PUBLIC_ID );
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            marshaller.marshal( new ObjectFactory().createModlet( redundantModlet ),
                                new File( classpathDirectory, "jomc-modlet.xml" ) );

            final File servicesDir = new File( classpathDirectory, "services" );
            assertTrue( servicesDir.isAbsolute() );

            if ( !servicesDir.exists() )
            {
                assertTrue( servicesDir.mkdirs() );
            }

            final File modletProviderService = new File( servicesDir, "org.jomc.modlet.ModletProvider" );
            FileUtils.writeStringToFile( modletProviderService, "org.jomc.modlet.DefaultModletProvider\n", "UTF-8" );

            p.setProperty( "basedir", buildFile.getParentFile().getAbsolutePath() );
            p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
            p.setUserProperty( "output.dir", this.getOutputDirectory().getAbsolutePath() );
            p.setUserProperty( "test.output.dir", new File( this.getOutputDirectory(), "work" ).getAbsolutePath() );
            p.setUserProperty( "test.classpath.dir", classpathDirectory.getParentFile().getAbsolutePath() );
            ProjectHelper.configureProject( p, buildFile );
            return p;
        }
        catch ( final IOException e )
        {
            throw new AssertionError( e );
        }
        catch ( final ModelException e )
        {
            throw new AssertionError( e );
        }
        catch ( final JAXBException e )
        {
            throw new AssertionError( e );
        }
        catch ( final BuildException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Gets the {@code AntExecutor} instance backing the test.
     *
     * @return The {@code AntExecutor} instance backing the test.
     *
     * @see #newAntExecutor()
     */
    public AntExecutor getAntExecutor()
    {
        if ( this.antExecutor == null )
        {
            this.antExecutor = this.newAntExecutor();
        }

        return this.antExecutor;
    }

    /**
     * Creates a new {@code AntExecutor} instance backing the test.
     *
     * @return A new {@code AntExecutor} instance backing the test.
     *
     * @see #getAntExecutor()
     */
    protected AntExecutor newAntExecutor()
    {
        return new DefaultAntExecutor();
    }

    /**
     * Gets the name of the build file backing the test.
     *
     * @return The the name of the build file backing the test.
     *
     * @see #newProject()
     */
    protected String getBuildFileName()
    {
        return "jomc-task-test.xml";
    }

    @Before
    public void setUp() throws Exception
    {
        Locale.setDefault( Locale.ENGLISH );

        if ( this.getProject().getTargets().containsKey( "before-test" ) )
        {
            assertNoException( this.executeTarget( "before-test" ) );
        }
    }

    @After
    public void tearDown() throws Exception
    {
        Locale.setDefault( DEFAULT_LOCALE );

        if ( this.getProject().getTargets().containsKey( "after-test" ) )
        {
            assertNoException( this.executeTarget( "after-test" ) );
        }
    }

    /**
     * Executes an Ant target.
     *
     * @param target The name of the target to execute.
     *
     * @return The result of the execution.
     *
     * @throws NullPointerException if {@code target} is {@code null}.
     */
    public AntExecutionResult executeTarget( final String target )
    {
        if ( target == null )
        {
            throw new NullPointerException( "target" );
        }

        final AntExecutionResult r =
            this.getAntExecutor().executeAnt( new AntExecutionRequest( this.getProject(), target ) );

        System.out.println( "======================================================================" );
        System.out.println( "Target: " + target );
        System.out.println( "======================================================================" );
        System.out.println( "System output:" );
        System.out.println( r.getSystemOutput() );
        System.out.println();
        System.out.println( "======================================================================" );
        System.out.println( "System error:" );
        System.out.println( r.getSystemError() );
        System.out.println();

        if ( r.getThrowable() != null )
        {
            System.out.println( "======================================================================" );
            System.out.println( "Exception:" );
            r.getThrowable().printStackTrace();
            System.out.println();
        }

        System.out.println( "======================================================================" );
        System.out.println( "Log:" );

        for ( BuildEvent e : r.getMessageLoggedEvents() )
        {
            System.out.println( ToStringBuilder.reflectionToString( e ) );
        }

        System.out.println();
        System.out.println();

        return r;
    }

    @Test
    public final void testAssertNotNull() throws Exception
    {
        try
        {
            this.getJomcTask().assertNotNull( null, null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTask().assertNotNull( "TEST", null );
            fail( "Expected 'BuildException' not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testAssertNamesNotNull() throws Exception
    {
        try
        {
            this.getJomcTask().assertNamesNotNull( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        final Collection<NameType> names = new ArrayList<NameType>( 1 );
        names.add( new NameType() );

        try
        {
            this.getJomcTask().assertNamesNotNull( names );
            fail( "Expected 'BuildException' not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testAssertKeysNotNull() throws Exception
    {
        try
        {
            this.getJomcTask().assertKeysNotNull( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        final Collection<KeyValueType<Object, Object>> keys = new ArrayList<KeyValueType<Object, Object>>( 1 );
        keys.add( new KeyValueType<Object, Object>() );

        try
        {
            this.getJomcTask().assertKeysNotNull( keys );
            fail( "Expected 'BuildException' not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testAssertLocationsNotNull() throws Exception
    {
        try
        {
            this.getJomcTask().assertLocationsNotNull( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        final Collection<PropertiesResourceType> locations = new ArrayList<PropertiesResourceType>( 1 );
        locations.add( new PropertiesResourceType() );

        try
        {
            this.getJomcTask().assertLocationsNotNull( locations );
            fail( "Expected 'BuildException' not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testGetTransformer() throws Exception
    {
        try
        {
            this.getJomcTask().getTransformer( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        final TransformerResourceType r = new TransformerResourceType();
        r.setLocation( "DOES_NOT_EXIST" );
        r.setOptional( true );
        assertNull( this.getJomcTask().getTransformer( r ) );
    }

    @Test
    public final void testGetResource() throws Exception
    {
        try
        {
            this.getJomcTask().getResource( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        assertNull( this.getJomcTask().getResource( "DOES_NOT_EXIST" ) );
        assertNotNull( this.getJomcTask().getResource( "file://DOES_NOT_EXIST" ) );
    }

    @Test
    public final void testGetProperties() throws Exception
    {
        try
        {
            this.getJomcTask().getProperties( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        final PropertiesResourceType r = new PropertiesResourceType();
        r.setLocation( "DOES_NOT_EXIST" );
        r.setOptional( true );

        final Properties p = this.getJomcTask().getProperties( r );
        assertNotNull( p );
        assertTrue( p.isEmpty() );
    }

    @Test
    public final void testGetModel() throws Exception
    {
        try
        {
            this.getJomcTask().getModel( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testExecuteTask() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-execute-task" );
        assertNoException( r );
        assertMessageLogged( r, "--------------------------------------------------------------------------------" );
    }

    @Test
    public final void testExecuteTaskIf() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-execute-task-if" );
        assertNoException( r );
        assertMessageLogged( r, "--------------------------------------------------------------------------------" );
    }

    @Test
    public final void testExecuteTaskUnless() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-execute-task-unless" );
        assertNoException( r );
        assertMessageNotLogged( r, "--------------------------------------------------------------------------------" );
    }

    @Test
    public final void testCloneable() throws Exception
    {
        assertTrue( this.getJomcTask() == this.getJomcTask() );
        assertFalse( this.getJomcTask() == this.getJomcTask().clone() );
    }

}
