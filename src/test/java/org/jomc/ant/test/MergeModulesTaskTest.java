/*
 *   Copyright (C) Christian Schulte, 2005-206
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
package org.jomc.ant.test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.ant.MergeModulesTask;
import org.jomc.ant.test.support.AntExecutionResult;
import static org.jomc.ant.test.support.Assert.assertException;
import static org.jomc.ant.test.support.Assert.assertExceptionMessage;
import static org.jomc.ant.test.support.Assert.assertExceptionMessageContaining;
import static org.jomc.ant.test.support.Assert.assertMessageLogged;
import static org.jomc.ant.test.support.Assert.assertMessageLoggedContaining;
import static org.jomc.ant.test.support.Assert.assertNoException;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.ant.MergeModulesTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class MergeModulesTaskTest extends JomcModelTaskTest
{

    /** Creates a new {@code MergeModulesTaskTest} instance. */
    public MergeModulesTaskTest()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public MergeModulesTask getJomcTask()
    {
        return (MergeModulesTask) super.getJomcTask();
    }

    /** {@inheritDoc} */
    @Override
    protected MergeModulesTask newJomcTask()
    {
        return new MergeModulesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "merge-modules-test.xml";
    }

    @Test
    public final void testMissingModuleFile() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-module-file" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'moduleFile' is missing a value." );
    }

    @Test
    public final void testMissingModuleName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-module-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'moduleName' is missing a value." );
    }

    @Test
    public final void testExclusionMissingModuleName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-exclusion-missing-module-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'name' is missing a value." );
    }

    @Test
    public final void testInclusionMissingModuleName() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-inclusion-missing-module-name" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'name' is missing a value." );
    }

    @Test
    public final void testMergeModules() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModulesWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModulesWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-with-redundant-resources" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModulesWithIllegalTransformationResultStylesheet() throws Exception
    {
        final AntExecutionResult r =
            this.executeTarget( "test-merge-modules-with-illegal-transformation-result-stylesheet" );

        assertException( r, BuildException.class );
        assertExceptionMessageContaining( r, "Illegal transformation result" );
    }

    @Test
    public final void testMergeModulesUnsupportedResource() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-unsupported-resource" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "not supported.", Project.MSG_WARN );
    }

    @Test
    public final void testMergeModulesExclusion() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-exclusion" );
        assertNoException( r );
        assertMessageLogged( r, "Excluding module 'JOMC Ant Tasks Tests'.", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModulesInclusion() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-inclusion" );
        assertNoException( r );
        assertMessageLogged( r, "Including module 'JOMC Ant Tasks Tests'.", Project.MSG_INFO );
    }

    @Test
    public final void testMergeModulesAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-merge-modules-all-attributes" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

    @Test
    public final void testIsModuleExcluded() throws Exception
    {
        try
        {
            this.getJomcTask().isModuleExcluded( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testIsModuleIncluded() throws Exception
    {
        try
        {
            this.getJomcTask().isModuleIncluded( null );
            fail( "Expected 'NullPointerException' not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

}
