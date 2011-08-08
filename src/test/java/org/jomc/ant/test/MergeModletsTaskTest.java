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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.ant.MergeModletsTask;
import org.junit.Test;
import static org.jomc.ant.test.Assert.assertException;
import static org.jomc.ant.test.Assert.assertExceptionMessage;
import static org.jomc.ant.test.Assert.assertExceptionMessageContaining;
import static org.jomc.ant.test.Assert.assertMessageLogged;
import static org.jomc.ant.test.Assert.assertMessageLoggedContaining;
import static org.jomc.ant.test.Assert.assertNoException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.ant.MergeModletsTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class MergeModletsTaskTest extends JomcTaskTest
{

    /** Creates a new {@code MergeModletsTaskTest} instance. */
    public MergeModletsTaskTest()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public MergeModletsTask getJomcTask()
    {
        return (MergeModletsTask) super.getJomcTask();
    }

    /** {@inheritDoc} */
    @Override
    protected MergeModletsTask newJomcTask()
    {
        return new MergeModletsTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "merge-modlets-test.xml";
    }

    @Test
    public final void testMissingModletFile() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-modlet-file" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'modletFile' is missing a value." );
    }

    @Test
    public final void testMissingModletName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-modlet-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'modletName' is missing a value." );
    }

    @Test
    public final void testExclusionMissingModletName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-exclusion-missing-modlet-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'name' is missing a value." );
    }

    @Test
    public final void testInclusionMissingModletName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-inclusion-missing-modlet-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'name' is missing a value." );
    }

    @Test
    public final void testModletResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-modlet-resource-missing-locatiom" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testModelContextAttributeMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-model-context-attribute-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testMergeModlets() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing" );
    }

    @Test
    public final void testMergeModletsWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing" );
    }

    @Test
    public final void testMergeModletsWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-with-redundant-resources" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing" );
    }

    @Test
    public final void testMergeModletsWithIllegalTransformationResultStylesheet() throws Exception
    {
        final AntExecutionResult r =
            this.executeTarget( "test-merge-modlets-with-illegal-transformation-result-stylesheet" );

        assertException( r, BuildException.class );
        assertExceptionMessageContaining( r, "Illegal transformation result" );
    }

    @Test
    public final void testMergeModletsUnsupportedResource() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-unsupported-resource" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "not supported.", Project.MSG_WARN );
    }

    @Test
    public final void testMergeModletsExclusion() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-exclusion" );
        assertNoException( r );
        assertMessageLogged( r, "Excluding modlet 'JOMC Ant Tasks Tests'.", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModletsInclusion() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-inclusion" );
        assertNoException( r );
        assertMessageLogged( r, "Including modlet 'JOMC Ant Tasks Tests'.", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModletsAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modlets-all-attributes" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

    @Test
    public final void testIsModletExcluded() throws Exception
    {
        try
        {
            this.getJomcTask().isModletExcluded( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testIsModletIncluded() throws Exception
    {
        try
        {
            this.getJomcTask().isModletIncluded( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

}
