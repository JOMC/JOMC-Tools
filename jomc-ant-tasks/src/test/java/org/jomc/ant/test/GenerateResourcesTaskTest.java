/*
 *   Copyright (C) Christian Schulte <cs@schulte.it>, 2005-206
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
import org.jomc.ant.GenerateResourcesTask;
import org.jomc.ant.ResourceProcessingException;
import org.jomc.ant.test.support.AntExecutionResult;
import static org.jomc.ant.test.support.Assert.assertException;
import static org.jomc.ant.test.support.Assert.assertExceptionMessage;
import static org.jomc.ant.test.support.Assert.assertMessageLogged;
import static org.jomc.ant.test.support.Assert.assertMessageNotLogged;
import static org.jomc.ant.test.support.Assert.assertNoException;
import org.junit.Test;

/**
 * Test cases for class {@code org.jomc.ant.GenerateResourcesTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class GenerateResourcesTaskTest extends ResourceFileProcessorTaskTest
{

    /**
     * Creates a new {@code GenerateResourcesTaskTest} instance.
     */
    public GenerateResourcesTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenerateResourcesTask getJomcTask()
    {
        return (GenerateResourcesTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GenerateResourcesTask newJomcTask()
    {
        return new GenerateResourcesTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "generate-resources-test.xml";
    }

    @Test
    public final void testMissingResourcesDirectory() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-resources-directory" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'resourcesDirectory' is missing a value." );
    }

    @Test
    public final void testSpecificationNotFound() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-specification-not-found" );
        assertNoException( r );
        assertMessageLogged( r, "Specification 'DOES NOT EXIST' not found.", Project.MSG_WARN );
    }

    @Test
    public final void testImplementationNotFound() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-implementation-not-found" );
        assertNoException( r );
        assertMessageLogged( r, "Implementation 'DOES NOT EXIST' not found.", Project.MSG_WARN );
    }

    @Test
    public final void testModuleNotFound() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-module-not-found" );
        assertNoException( r );
        assertMessageLogged( r, "Module 'DOES NOT EXIST' not found.", Project.MSG_WARN );
    }

    @Test
    public final void testResourceProcessingDisabled() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-resource-processing-disabled" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing disabled.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-with-redundant-resources" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateOneSpecification() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-one-specification" );
        assertNoException( r );
        assertMessageNotLogged( r, "Specification 'org.jomc.ant.test.JomcTask' not found." );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateOneImplementation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-one-implementation" );
        assertNoException( r );
        assertMessageNotLogged( r, "Implementation 'org.jomc.ant.test.JomcToolTask' not found." );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateOneModule() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-one-module" );
        assertNoException( r );
        assertMessageNotLogged( r, "Module 'JOMC Ant Tasks Tests' not found." );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesWithClasspathref() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-with-classpathref" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesWithNestedClasspath() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-with-nested-classpath" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesDeprecatedAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-deprecated-attributes" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-all-attributes" );
        assertNoException( r );
        assertMessageLogged( r, "Resource file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testGenerateAntTaskResourcesBrokenModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-generate-ant-task-resources-broken-model" );
        assertException( r, ResourceProcessingException.class );
    }

}
