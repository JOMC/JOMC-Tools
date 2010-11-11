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

import org.jomc.ant.MergeModletsTask;
import static junit.framework.Assert.assertNotNull;

/**
 * Test cases for class {@code org.jomc.ant.MergeModletsTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class MergeModletsTaskTest extends JomcTaskTest
{

    /** Creates a new {@code MergeModletsTaskTest} instance. */
    public MergeModletsTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code MergeModletsTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public MergeModletsTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code MergeModletsTask} tests are performed with.
     *
     * @return The {@code MergeModletsTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public MergeModletsTask getJomcTask()
    {
        return (MergeModletsTask) super.getJomcTask();
    }

    public void testMissingModletFile() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-modlet-file",
            "the \"merge-modlets\" task is missing the mandatory \"modletFile\" attribute",
            "Mandatory attribute 'modletFile' is missing a value." );

    }

    public void testMissingModletName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-modlet-name",
            "the \"merge-modlets\" task is missing the mandatory \"modletName\" attribute",
            "Mandatory attribute 'modletName' is missing a value." );

    }

    public void testExclusionMissingModletName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-exclusion-missing-modlet-name",
            "the \"modletExclude\" element of the \"merge-modlets\" task is missing the mandatory \"name\" attribute",
            "Mandatory attribute 'name' is missing a value." );

    }

    public void testInclusionMissingModletName() throws Exception
    {
        this.expectSpecificBuildException(
            "test-inclusion-missing-modlet-name",
            "the \"modletInclude\" element of the \"merge-modlets\" task is missing the mandatory \"name\" attribute",
            "Mandatory attribute 'name' is missing a value." );

    }

    public void testMergeModlets() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets", "Writing" );
    }

    public void testMergeModletsWithNoopStylesheet() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets-with-no-op-stylesheet", "Writing" );
    }

    public void testMergeModletsWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets-with-redundant-resources", "Writing" );
    }

    public void testMergeModletsWithIllegalTransformationResultStylesheet() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-merge-modlets-with-illegal-transformation-result-stylesheet",
            "the \"modletObjectStylesheet\" attribute of the \"merge-modlets\" task points to a stylesheet"
            + " producing an illegal transformation result",
            "Illegal transformation result" );

    }

    public void testMergeModletsExclusion() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets-exclusion", "Excluding modlet 'JOMC Tools'." );
    }

    public void testMergeModletsInclusion() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets-inclusion", "Including modlet 'JOMC Tools'." );
    }

    public void testMergeModletsAllAttributes() throws Exception
    {
        this.expectLogContaining( "test-merge-modlets-all-attributes", "Writing" );
    }

    public void testIsModletExcluded() throws Exception
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

    public void testIsModletIncluded() throws Exception
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

    /**
     * Creates a new {@code MergeModletsTask} instance tests are performed with.
     *
     * @return A new {@code MergeModletsTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected MergeModletsTask createJomcTask()
    {
        return new MergeModletsTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "merge-modlets-test.xml";
    }

}
