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

import org.jomc.ant.MergeModulesTask;
import static junit.framework.Assert.assertNotNull;

/**
 * Test cases for class {@code org.jomc.ant.MergeModulesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class MergeModulesTaskTest extends JomcModelTaskTest
{

    /** Creates a new {@code MergeModulesTaskTest} instance. */
    public MergeModulesTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code MergeModulesTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public MergeModulesTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code MergeModulesTask} tests are performed with.
     *
     * @return The {@code MergeModulesTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public MergeModulesTask getJomcTask()
    {
        return (MergeModulesTask) super.getJomcTask();
    }

    public void testMissingModuleFile() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-module-file",
            "the \"merge-modules\" task is missing the mandatory \"moduleFile\" attribute",
            "Mandatory attribute 'moduleFile' is missing a value." );

    }

    public void testMissingModuleName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-module-name",
            "the \"merge-modules\" task is missing the mandatory \"moduleName\" attribute",
            "Mandatory attribute 'moduleName' is missing a value." );

    }

    public void testExclusionMissingModuleName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-exclusion-missing-module-name",
            "the \"moduleExclude\" element of the \"merge-modules\" task is missing the mandatory \"name\" attribute",
            "Mandatory attribute 'name' is missing a value." );

    }

    public void testInclusionMissingModuleName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-inclusion-missing-module-name",
            "the \"moduleInclude\" element of the \"merge-modules\" task is missing the mandatory \"name\" attribute",
            "Mandatory attribute 'name' is missing a value." );

    }

    public void testMergeModules() throws Exception
    {
        this.expectLogContaining( "test-merge-modules", "Writing" );
    }

    public void testMergeModulesWithNoopStylesheet() throws Exception
    {
        this.expectLogContaining( "test-merge-modules-with-no-op-stylesheet", "Writing" );
    }

    public void testMergeModulesWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-merge-modules-with-redundant-resources", "Writing" );
    }

    public void testMergeModulesWithIllegalTransformationResultStylesheet() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-merge-modules-with-illegal-transformation-result-stylesheet",
            "the \"modelObjectStylesheet\" attribute of the \"merge-modules\" task points to a stylesheet"
            + " producing an illegal transformation result",
            "Illegal transformation result" );

    }

    public void testMergeModulesExclusion() throws Exception
    {
        this.expectLogContaining( "test-merge-modules-exclusion", "Excluding module 'JOMC Ant Tasks Tests'." );
    }

    public void testMergeModulesInclusion() throws Exception
    {
        this.expectLogContaining( "test-merge-modules-inclusion", "Including module 'JOMC Ant Tasks Tests'." );
    }

    public void testMergeModulesAllAttributes() throws Exception
    {
        this.expectLogContaining( "test-merge-modules-all-attributes", "Writing" );
    }

    public void testIsModuleExcluded() throws Exception
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

    public void testIsModuleIncluded() throws Exception
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

    /**
     * Creates a new {@code MergeModulesTask} instance tests are performed with.
     *
     * @return A new {@code MergeModulesTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected MergeModulesTask createJomcTask()
    {
        return new MergeModulesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "merge-modules-test.xml";
    }

}
