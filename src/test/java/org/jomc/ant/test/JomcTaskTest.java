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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.jomc.ant.types.NameType;
import org.apache.tools.ant.BuildException;
import org.jomc.ant.JomcTask;
import java.util.Locale;
import org.apache.tools.ant.BuildFileTest;
import static junit.framework.Assert.assertNotNull;

/**
 * Test cases for class {@code org.jomc.ant.JomcTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class JomcTaskTest extends BuildFileTest
{

    /** Cached default locale. */
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /** The {@code JomcTask} instance tests are performed with. */
    private JomcTask jomcTask;

    /** Creates a new {@code JomcTaskTest} instance. */
    public JomcTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code JomcTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public JomcTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code JomcTask} instance tests are performed with.
     *
     * @return The {@code JomcTask} instance tests are performed with.
     *
     * @see #createJomcTask()
     */
    public JomcTask getJomcTask()
    {
        if ( this.jomcTask == null )
        {
            this.jomcTask = this.createJomcTask();
        }

        return this.jomcTask;
    }

    @Override
    public void setUp() throws Exception
    {
        Locale.setDefault( Locale.ENGLISH );
        configureProject( this.getBuildFileName() );
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        Locale.setDefault( DEFAULT_LOCALE );
    }

    @Override
    public void expectSpecificBuildException( final String target, final String cause, final String message )
    {
        super.expectSpecificBuildException( target, cause, message );
        assertNotNull( this.getBuildException() );
        assertNotNull( this.getBuildException().getMessage() );
        System.out.println( this.getBuildException() );
    }

    @Override
    public void expectBuildExceptionContaining( final String target, final String cause, final String needle )
    {
        super.expectBuildExceptionContaining( target, cause, needle );
        assertNotNull( this.getBuildException() );
        assertNotNull( this.getBuildException().getMessage() );
        System.out.println( this.getBuildException() );
    }

    public void testAssertNotNull() throws Exception
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

    public void testAssertNamesNotNull() throws Exception
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

    public void testAssertDirectory() throws Exception
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

    public void testNewTransformer() throws Exception
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

    public void testGetModel() throws Exception
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

    public void testExecuteTask() throws Exception
    {
        this.executeTarget( "test-execute-task" );
        assertNull( this.getBuildException() );
    }

    /**
     * Creates a new {@code JomcTask} instance tests are performed with.
     *
     * @return A new {@code JomcTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    protected JomcTask createJomcTask()
    {
        return new JomcTask();
    }

    /**
     * Gets the name of the build file backing the test.
     *
     * @return The name of the build file backing the test.
     */
    protected String getBuildFileName()
    {
        return "jomc-task-test.xml";
    }

}
