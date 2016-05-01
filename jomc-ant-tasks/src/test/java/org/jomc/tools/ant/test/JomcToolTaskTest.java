/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
 *   THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 *   AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *   NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *   THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   $JOMC$
 *
 */
package org.jomc.tools.ant.test;

import org.apache.tools.ant.BuildException;
import org.jomc.tools.ant.JomcToolTask;
import org.jomc.tools.ant.test.support.AntExecutionResult;
import org.junit.Test;
import static org.jomc.tools.ant.test.support.Assert.assertException;
import static org.jomc.tools.ant.test.support.Assert.assertExceptionMessage;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.ant.JomcToolTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcToolTaskTest extends JomcModelTaskTest
{

    /**
     * Creates a new {@code JomcToolTaskTest} instance.
     */
    public JomcToolTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JomcToolTask getJomcTask()
    {
        return (JomcToolTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JomcToolTask newJomcTask()
    {
        return new JomcToolTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "jomc-tool-task-test.xml";
    }

    @Test
    public final void testConfigureJomcTool() throws Exception
    {
        try
        {
            this.getJomcTask().configureJomcTool( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testGetSpecification() throws Exception
    {
        try
        {
            this.getJomcTask().getSpecification( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testGetImplementation() throws Exception
    {
        try
        {
            this.getJomcTask().getImplementation( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testGetModule() throws Exception
    {
        try
        {
            this.getJomcTask().getModule( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testVelocityPropertyMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-velocity-property-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testVelocityPropertyResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-velocity-property-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testTemplateParameterMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-template-parameter-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testTemplateParameterResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-template-parameter-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testInvalidMultipleLocaleElements() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-invalid-multiple-locale-elements" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Multiple nested 'locale' elements." );
    }

}
