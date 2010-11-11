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

import org.jomc.ant.GenerateResourcesTask;

/**
 * Test cases for class {@code org.jomc.ant.GenerateResourcesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class GenerateResourcesTaskTest extends ResourceFileProcessorTaskTest
{

    /** Creates a new {@code GenerateResourcesTaskTest} instance. */
    public GenerateResourcesTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code GenerateResourcesTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public GenerateResourcesTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code GenerateResourcesTask} tests are performed with.
     *
     * @return The {@code GenerateResourcesTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public GenerateResourcesTask getJomcTask()
    {
        return (GenerateResourcesTask) super.getJomcTask();
    }

    public void testMissingResourcesDirectory() throws Exception
    {
        this.expectSpecificBuildException(
            "test-missing-resources-directory",
            "the \"generate-resources\" task is missing the mandatory \"resourcesDirectory\" attribute",
            "Mandatory attribute 'resourcesDirectory' is missing a value." );

    }

    public void testNonExistentResourcesDirectory() throws Exception
    {
        this.expectBuildExceptionContaining(
            "test-non-existing-resources-directory",
            "the \"resourcesDirectory\" attribute of the \"generate-resources\" task specifies a non-existent directory",
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

    public void testResourceProcessingDisabled() throws Exception
    {
        this.expectLogContaining( "test-resource-processing-disabled", "Resource file processing disabled." );
    }

    public void testGenerateAntTaskResources() throws Exception
    {
        this.expectLogContaining( "test-generate-ant-task-resources", "Resource file processing successful." );
    }

    public void testGenerateAntTaskResourcesWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-generate-ant-task-resources-with-redundant-resources",
                                  "Resource file processing successful." );

    }

    public void testCommitOneSpecification() throws Exception
    {
        this.expectLogNotContaining( "test-generate-one-specification",
                                     "Specification 'org.jomc.ant.JomcTask' not found." );

    }

    public void testCommitOneImplementation() throws Exception
    {
        this.expectLogNotContaining( "test-generate-one-implementation",
                                     "Implementation 'org.jomc.ant.JomcToolTask' not found." );

    }

    public void testCommitOneModule() throws Exception
    {
        this.expectLogNotContaining( "test-generate-one-module",
                                     "Module 'JOMC Ant Tasks Tests' not found." );

    }

    public void testGenerateAntTaskResourcesWithClasspathref() throws Exception
    {
        this.expectLogContaining( "test-generate-ant-task-resources-with-classpathref",
                                  "Resource file processing successful." );

    }

    public void testGenerateAntTaskResourcesWithNestedClasspath() throws Exception
    {
        this.expectLogContaining( "test-generate-ant-task-resources-with-nested-classpath",
                                  "Resource file processing successful." );

    }

    public void testGenerateAntTaskResourcesAllAttributes() throws Exception
    {
        this.expectLogContaining( "test-generate-ant-task-resources-all-attributes",
                                  "Resource file processing successful." );

    }

    public void testGenerateAntTaskResourcesBrokenModel() throws Exception
    {
        this.expectSpecificBuildException(
            "test-generate-ant-task-resources-broken-model",
            "the \"moduleLocation\" attribute of the \"generate-resources\" task points to broken test model resources",
            "Resource file processing failure." );

    }

    /**
     * Creates a new {@code GenerateResourcesTask} instance tests are performed with.
     *
     * @return A new {@code GenerateResourcesTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected GenerateResourcesTask createJomcTask()
    {
        return new GenerateResourcesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "generate-resources-test.xml";
    }

}
