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

import org.jomc.ant.ManageSourcesTask;

/**
 * Test cases for class {@code org.jomc.ant.ManageSourcesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class ManageSourcesTaskTest extends SourceFileProcessorTaskTest
{

    /** Creates a new {@code ManageSourcesTaskTest} instance. */
    public ManageSourcesTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code ManageSourcesTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public ManageSourcesTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code ManageSourcesTask} tests are performed with.
     *
     * @return The {@code ManageSourcesTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public ManageSourcesTask getJomcTask()
    {
        return (ManageSourcesTask) super.getJomcTask();
    }

    public void testMissingSourcesDirectory() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-sources-directory",
            "the \"manage-sources\" task is missing the mandatory \"sourcesDirectory\" attribute",
            "Mandatory attribute 'sourcesDirectory' is missing a value." );

    }

    public void testNonExistentSourcesDirectory() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-non-existing-sources-directory",
            "the \"sourcesDirectory\" attribute of the \"manage-sources\" task specifies a non-existent directory",
            "DOES_NOT_EXIST" );

    }

    public void testSpecificationNotFound() throws Exception
    {
        this.expectLogContaining( "test-specification-not-found",
                                  "Specification 'DOES NOT EXIST' not found." );

    }

    public void testImplementationNotFound() throws Exception
    {
        this.expectLogContaining( "test-implementation-not-found",
                                  "Implementation 'DOES NOT EXIST' not found." );

    }

    public void testModuleNotFound() throws Exception
    {
        this.expectLogContaining( "test-module-not-found",
                                  "Module 'DOES NOT EXIST' not found." );

    }

    public void testSourceProcessingDisabled() throws Exception
    {
        this.expectLogContaining( "test-source-processing-disabled", "Source file processing disabled." );
    }

    public void testManageAntTaskSources() throws Exception
    {
        this.expectLogContaining( "test-manage-ant-task-sources", "Source file processing successful." );
    }

    public void testManageAntTaskSourcesWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-manage-ant-task-sources-with-redundant-resources",
                                  "Source file processing successful." );

    }

    public void testManageOneSpecification() throws Exception
    {
        this.expectLogNotContaining( "test-manage-one-specification",
                                     "Specification 'org.jomc.ant.JomcTask' not found." );

    }

    public void testManageOneImplementation() throws Exception
    {
        this.expectLogNotContaining( "test-manage-one-implementation",
                                     "Implementation 'org.jomc.ant.JomcToolTask' not found." );

    }

    public void testManageOneModule() throws Exception
    {
        this.expectLogNotContaining( "test-manage-one-module",
                                     "Module 'JOMC Ant Tasks Tests' not found." );

    }

    public void testManageAntTaskSourcesWithClasspathref() throws Exception
    {
        this.expectLogContaining( "test-manage-ant-task-sources-with-classpathref",
                                  "Source file processing successful." );

    }

    public void testManageAntTaskSourcesWithNestedClasspath() throws Exception
    {
        this.expectLogContaining( "test-manage-ant-task-sources-with-nested-classpath",
                                  "Source file processing successful." );

    }

    public void testManageAntTaskSourcesAllAttributes() throws Exception
    {
        this.expectLogContaining( "test-manage-ant-task-sources-all-attributes",
                                  "Source file processing successful." );

    }

    public void testManageAntTaskSourcesBrokenModel() throws Exception
    {
        this.expectSpecificBuildException(
            "test-manage-ant-task-sources-broken-model",
            "the \"moduleLocation\" attribute of the \"manage-sources\" task points to broken test model resources",
            "Source file processing failure." );

    }

    /**
     * Creates a new {@code ManageSourcesTask} instance tests are performed with.
     *
     * @return A new {@code ManageSourcesTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected ManageSourcesTask createJomcTask()
    {
        return new ManageSourcesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "manage-sources-test.xml";
    }

}
