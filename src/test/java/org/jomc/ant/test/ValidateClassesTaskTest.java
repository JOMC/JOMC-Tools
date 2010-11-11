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

import org.jomc.ant.ValidateClassesTask;

/**
 * Test cases for class {@code org.jomc.ant.ValidateClassesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class ValidateClassesTaskTest extends ClassFileProcessorTaskTest
{

    /** Creates a new {@code ValidateClassesTaskTest} instance. */
    public ValidateClassesTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code ValidateClassesTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public ValidateClassesTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code ValidateClassesTask} tests are performed with.
     *
     * @return The {@code ValidateClassesTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public ValidateClassesTask getJomcTask()
    {
        return (ValidateClassesTask) super.getJomcTask();
    }

    public void testMissingClassesDirectory() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-classes-directory",
            "the \"validate-classes\" task is missing the mandatory \"classesDirectory\" attribute",
            "Mandatory attribute 'classesDirectory' is missing a value." );

    }

    public void testNonExistentClassesDirectory() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-non-existing-classes-directory",
            "the \"classesDirectory\" attribute of the \"validate-classes\" task specifies a non-existent directory",
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

    public void testClassProcessingDisabled() throws Exception
    {
        this.expectLogContaining( "test-class-processing-disabled", "Class file processing disabled." );
    }

    public void testValidateAntTasks() throws Exception
    {
        this.expectLogNotContaining( "test-validate-ant-tasks", "Class file processing failure." );
    }

    public void testValidateAntTasksWithRedundantResources() throws Exception
    {
        this.expectLogNotContaining( "test-validate-ant-tasks-with-redundant-resources",
                                     "Class file processing failure." );

    }

    public void testValidateIllegalAntTasks() throws Exception
    {
        this.expectSpecificBuildException( "test-validate-illegal-ant-tasks",
                                           "all test specifications got updated to illegal multiplicities",
                                           "Class file processing failure." );

    }

    public void testValidateOneSpecification() throws Exception
    {
        this.expectLogNotContaining( "test-validate-one-specification",
                                     "Specification 'org.jomc.ant.JomcTask' not found." );

    }

    public void testValidateOneIllegalSpecification() throws Exception
    {
        this.expectSpecificBuildException( "test-validate-one-illegal-specification",
                                           "all test specifications got updated to illegal multiplicities",
                                           "Class file processing failure." );

    }

    public void testValidateOneImplementation() throws Exception
    {
        this.expectLogNotContaining( "test-validate-one-implementation",
                                     "Implementation 'org.jomc.ant.JomcToolTask' not found." );

    }

    public void testValidateOneIllegalImplementation() throws Exception
    {
        this.expectSpecificBuildException( "test-validate-one-illegal-implementation",
                                           "all test specifications got updated to illegal multiplicities",
                                           "Class file processing failure." );

    }

    public void testValidateOneModule() throws Exception
    {
        this.expectLogNotContaining( "test-validate-one-module",
                                     "Module 'JOMC Ant Tasks Tests' not found." );

    }

    public void testValidateOneIllegalModule() throws Exception
    {
        this.expectSpecificBuildException( "test-validate-one-illegal-module",
                                           "all test specifications got updated to illegal multiplicities",
                                           "Class file processing failure." );

    }

    public void testValidateAntTasksWithClasspathref() throws Exception
    {
        this.expectLogNotContaining( "test-validate-ant-tasks-with-classpathref", "Class file processing failure." );
    }

    public void testValidateAntTasksWithNestedClasspath() throws Exception
    {
        this.expectLogNotContaining( "test-validate-ant-tasks-with-nested-classpath",
                                     "Class file processing failure." );

    }

    public void testValidateAntTasksAllAttributes() throws Exception
    {
        this.expectLogNotContaining( "test-validate-ant-tasks-all-attributes",
                                     "Class file processing failure." );

    }

    public void testValidateAntTasksBrokenModel() throws Exception
    {
        this.expectSpecificBuildException(
            "test-validate-ant-tasks-broken-model",
            "the \"moduleLocation\" attribute of the \"validate-classes\" task points to broken test model resources",
            "Class file processing failure." );

    }

    /**
     * Creates a new {@code ValidateClassesTask} instance tests are performed with.
     *
     * @return A new {@code ValidateClassesTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected ValidateClassesTask createJomcTask()
    {
        return new ValidateClassesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "validate-classes-test.xml";
    }

}
