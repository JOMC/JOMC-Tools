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

import org.jomc.ant.CommitClassesTask;

/**
 * Test cases for class {@code org.jomc.ant.CommitClassesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class CommitClassesTaskTest extends ClassFileProcessorTaskTest
{

    /** Creates a new {@code CommitClassesTaskTest} instance. */
    public CommitClassesTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code CommitClassesTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public CommitClassesTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code CommitClassesTask} tests are performed with.
     *
     * @return The {@code CommitClassesTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public CommitClassesTask getJomcTask()
    {
        return (CommitClassesTask) super.getJomcTask();
    }

    public void testMissingClassesDirectory() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-classes-directory",
            "the \"commit-classes\" task is missing the mandatory \"classesDirectory\" attribute",
            "Mandatory attribute 'classesDirectory' is missing a value." );

    }

    public void testNonExistentClassesDirectory() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-non-existing-classes-directory",
            "the \"classesDirectory\" attribute of the \"commit-classes\" task specifies a non-existent directory",
            "DOES_NOT_EXIST" );

    }

    public void testStylesheetNotFound() throws Exception
    {
        this.expectSpecificBuildException(
            "test-stylesheet-not-found",
            "the \"modelObjectStylesheet\" attribute of the \"commit-classes\" task specifies a non-existent file",
            "No style sheet found at 'DOES_NOT_EXIST'." );

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

    public void testClassProcessingDisabled() throws Exception
    {
        this.expectLogContaining( "test-class-processing-disabled", "Class file processing disabled." );
    }

    public void testCommitAntTasks() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks", "Class file processing successful." );
    }

    public void testCommitAntTasksWithNoopStylesheet() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks-with-no-op-stylesheet",
                                  "Class file processing successful." );

    }

    public void testCommitAntTasksWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks-with-redundant-resources",
                                  "Class file processing successful." );

    }

    public void testCommitOneSpecification() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-specification",
                                     "Specification 'org.jomc.ant.JomcTask' not found." );

    }

    public void testCommitOneImplementation() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-implementation",
                                     "Implementation 'org.jomc.ant.JomcToolTask' not found." );

    }

    public void testCommitOneModule() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-module",
                                     "Module 'JOMC Ant Tasks Tests' not found." );

    }

    public void testCommitOneSpecificationWithNoopStylesheet() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-specification-with-no-op-stylesheet",
                                     "Specification 'org.jomc.ant.JomcTask' not found." );

    }

    public void testCommitOneImplementationWithNoopStylesheet() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-implementation-with-no-op-stylesheet",
                                     "Implementation 'org.jomc.ant.JomcToolTask' not found." );

    }

    public void testCommitOneModuleWithNoopStylesheet() throws Exception
    {
        this.expectLogNotContaining( "test-commit-one-module-with-no-op-stylesheet",
                                     "Module 'JOMC Ant Tasks Tests' not found." );

    }

    public void testCommitAntTasksWithClasspathref() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks-with-classpathref", "Class file processing successful." );
    }

    public void testCommitAntTasksWithNestedClasspath() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks-with-nested-classpath",
                                  "Class file processing successful." );

    }

    public void testCommitAntTasksAllAttributes() throws Exception
    {
        this.expectLogContaining( "test-commit-ant-tasks-all-attributes",
                                  "Class file processing successful." );

    }

    public void testCommitAntTasksBrokenModel() throws Exception
    {
        this.expectSpecificBuildException(
            "test-commit-ant-tasks-broken-model",
            "the \"moduleLocation\" attribute of the \"commit-classes\" task points to broken test model resources",
            "Class file processing failure." );

    }

    /**
     * Creates a new {@code CommitClassesTask} instance tests are performed with.
     *
     * @return A new {@code CommitClassesTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected CommitClassesTask createJomcTask()
    {
        return new CommitClassesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "commit-classes-test.xml";
    }

}
