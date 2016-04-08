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
import org.jomc.ant.ManageSourcesTask;
import org.jomc.ant.SourceProcessingException;
import org.jomc.ant.test.support.AntExecutionResult;
import static org.jomc.ant.test.support.Assert.assertException;
import static org.jomc.ant.test.support.Assert.assertExceptionMessage;
import static org.jomc.ant.test.support.Assert.assertMessageLogged;
import static org.jomc.ant.test.support.Assert.assertMessageNotLogged;
import static org.jomc.ant.test.support.Assert.assertNoException;
import org.junit.Test;

/**
 * Test cases for class {@code org.jomc.ant.ManageSourcesTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class ManageSourcesTaskTest extends SourceFileProcessorTaskTest
{

    /**
     * Creates a new {@code ManageSourcesTaskTest} instance.
     */
    public ManageSourcesTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManageSourcesTask getJomcTask()
    {
        return (ManageSourcesTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ManageSourcesTask newJomcTask()
    {
        return new ManageSourcesTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "manage-sources-test.xml";
    }

    @Test
    public final void testMissingSourcesDirectory() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-sources-directory" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'sourcesDirectory' is missing a value." );
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
    public final void testSourceProcessingDisabled() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-source-processing-disabled" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing disabled.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSourcesWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-with-redundant-resources" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageOneSpecification() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-one-specification" );
        assertNoException( r );
        assertMessageNotLogged( r, "Specification 'org.jomc.ant.test.JomcTask' not found." );
    }

    @Test
    public final void testManageOneImplementation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-one-implementation" );
        assertNoException( r );
        assertMessageNotLogged( r, "Implementation 'org.jomc.ant.test.JomcToolTask' not found." );
    }

    @Test
    public final void testManageOneModule() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-one-module" );
        assertNoException( r );
        assertMessageNotLogged( r, "Module 'JOMC Ant Tasks Tests' not found." );
    }

    @Test
    public final void testManageAntTaskSourcesWithClasspathref() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-with-classpathref" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSourcesWithNestedClasspath() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-with-nested-classpath" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSourcesDeprecatedAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-deprecated-attributes" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSourcesAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-all-attributes" );
        assertNoException( r );
        assertMessageLogged( r, "Source file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testManageAntTaskSourcesBrokenModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-manage-ant-task-sources-broken-model" );
        assertException( r, SourceProcessingException.class );
    }

}
