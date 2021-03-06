/*
 *   Copyright (C) 2005 Christian Schulte <cs@schulte.it>
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
package org.jomc.tools.ant.test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.tools.ant.ClassProcessingException;
import org.jomc.tools.ant.ValidateClassesTask;
import org.jomc.tools.ant.test.support.AntExecutionResult;
import org.junit.Test;
import static org.jomc.tools.ant.test.support.Assert.assertException;
import static org.jomc.tools.ant.test.support.Assert.assertExceptionMessage;
import static org.jomc.tools.ant.test.support.Assert.assertMessageLogged;
import static org.jomc.tools.ant.test.support.Assert.assertMessageNotLogged;
import static org.jomc.tools.ant.test.support.Assert.assertNoException;

/**
 * Test cases for class {@code org.jomc.tools.ant.ValidateClassesTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class ValidateClassesTaskTest extends ClassFileProcessorTaskTest
{

    /**
     * Creates a new {@code ValidateClassesTaskTest} instance.
     */
    public ValidateClassesTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidateClassesTask getJomcTask()
    {
        return (ValidateClassesTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ValidateClassesTask newJomcTask()
    {
        return new ValidateClassesTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "validate-classes-test.xml";
    }

    @Test
    public final void testMissingClassesDirectory() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-classes-directory" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'classesDirectory' is missing a value." );
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
    public final void testClassProcessingDisabled() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-class-processing-disabled" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing disabled.", Project.MSG_INFO );
    }

    @Test
    public final void testValidateAntTasks() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks" );
        assertNoException( r );
    }

    @Test
    public final void testValidateAntTasksWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks-with-redundant-resources" );
        assertNoException( r );
    }

    @Test
    public final void testValidateIllegalAntTasks() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-illegal-ant-tasks" );
        assertException( r, ClassProcessingException.class );
    }

    @Test
    public final void testValidateOneSpecification() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-specification" );
        assertNoException( r );
        assertMessageNotLogged( r, "Specification 'org.jomc.tools.ant.test.JomcTask' not found." );
    }

    @Test
    public final void testValidateOneIllegalSpecification() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-illegal-specification" );
        assertException( r, ClassProcessingException.class );
    }

    @Test
    public final void testValidateOneImplementation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-implementation" );
        assertNoException( r );
        assertMessageNotLogged( r, "Implementation 'org.jomc.tools.ant.test.JomcToolTask' not found." );
    }

    @Test
    public final void testValidateOneIllegalImplementation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-illegal-implementation" );
        assertException( r, ClassProcessingException.class );
    }

    @Test
    public final void testValidateOneModule() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-module" );
        assertNoException( r );
        assertMessageNotLogged( r, "Module 'JOMC Ant Tasks Tests' not found." );
    }

    @Test
    public final void testValidateOneIllegalModule() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-one-illegal-module" );
        assertException( r, ClassProcessingException.class );
    }

    @Test
    public final void testValidateAntTasksWithClasspathref() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks-with-classpathref" );
        assertNoException( r );
    }

    @Test
    public final void testValidateAntTasksWithNestedClasspath() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks-with-nested-classpath" );
        assertNoException( r );
    }

    @Test
    public final void testValidateAntTasksAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks-all-attributes" );
        assertNoException( r );
    }

    @Test
    public final void testValidateAntTasksBrokenModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-ant-tasks-broken-model" );
        assertException( r, ClassProcessingException.class );
    }

}
