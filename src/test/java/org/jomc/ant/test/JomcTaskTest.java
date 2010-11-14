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
package org.jomc.ant.test;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildEvent;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Project;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.jomc.ant.types.NameType;
import org.apache.tools.ant.BuildException;
import org.jomc.ant.JomcTask;
import java.util.Locale;
import static org.jomc.ant.test.Assert.assertNoException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.ant.JomcTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcTaskTest
{

    /** Cached default locale. */
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /** The {@code JomcTask} instance tests are performed with. */
    private JomcTask jomcTask;

    /** The {@code Project} backing the test. */
    private Project project;

    /** The {@code AntExecutor} backing the test. */
    private AntExecutor antExecutor;

    /** Creates a new {@code JomcTaskTest} instance. */
    public JomcTaskTest()
    {
        super();
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

            final File buildFile =
                new File( System.getProperty( JomcTaskTest.class.getName() + ".buildFilesDirectory" ),
                          this.getBuildFileName() );

            p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
            ProjectHelper.configureProject( p, buildFile );
            return p;
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
    }

    @After
    public void tearDown() throws Exception
    {
        Locale.setDefault( DEFAULT_LOCALE );
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
    public final void testAssertDirectory() throws Exception
    {
        try
        {
            this.getJomcTask().assertDirectory( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTask().assertDirectory( new File( "DOES_NOT_EXIST" ) );
            fail( "Expected 'BuildException' not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testNewTransformer() throws Exception
    {
        try
        {
            this.getJomcTask().newTransformer( null, null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }

        try
        {
            this.getJomcTask().newTransformer( "TEST", null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
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
        assertNoException( this.executeTarget( "test-execute-task" ) );
    }

}
