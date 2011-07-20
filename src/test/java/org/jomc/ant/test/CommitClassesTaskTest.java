/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jomc.ant.CommitClassesTask;
import org.jomc.ant.ClassProcessingException;
import org.junit.Test;
import static org.jomc.ant.test.Assert.assertException;
import static org.jomc.ant.test.Assert.assertExceptionMessage;
import static org.jomc.ant.test.Assert.assertMessageLogged;
import static org.jomc.ant.test.Assert.assertMessageNotLogged;
import static org.jomc.ant.test.Assert.assertNoException;

/**
 * Test cases for class {@code org.jomc.ant.CommitClassesTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class CommitClassesTaskTest extends ClassFileProcessorTaskTest
{

    /** Creates a new {@code CommitClassesTaskTest} instance. */
    public CommitClassesTaskTest()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public CommitClassesTask getJomcTask()
    {
        return (CommitClassesTask) super.getJomcTask();
    }

    /** {@inheritDoc} */
    @Override
    protected CommitClassesTask newJomcTask()
    {
        return new CommitClassesTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "commit-classes-test.xml";
    }

    @Test
    public final void testMissingClassesDirectory() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-missing-classes-directory" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'classesDirectory' is missing a value." );
    }

    @Test
    public final void testVelocityPropertyMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-velocity-property-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testTemplateParameterMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-template-parameter-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testStylesheetMissingLocationKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-stylesheet-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testStylesheetParameterResourceMissingLocationKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-stylesheet-parameter-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testStylesheetParameterMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-stylesheet-parameter-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testModuleResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-module-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testStylesheetNotFound() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-stylesheet-not-found" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "XSLT document 'DOES_NOT_EXIST' not found." );
    }

    @Test
    public final void testOptionalStylesheetNotFound() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-optional-stylesheet-not-found" );
        assertNoException( r );
        assertMessageLogged( r, "XSLT document 'DOES_NOT_EXIST' not found." );
    }

    @Test
    public final void testInvalidMultipleLocaleElements() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-invalid-multiple-locale-elements" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Multiple nested 'locale' elements." );
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
    public final void testCommitAntTasks() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitAntTasksWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitAntTasksWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-with-redundant-resources" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitOneSpecification() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-specification" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Specification 'org.jomc.ant.test.JomcTask' not found." );
    }

    @Test
    public final void testCommitOneImplementation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-implementation" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Implementation 'org.jomc.ant.test.JomcToolTask' not found." );
    }

    @Test
    public final void testCommitOneModule() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-module" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Module 'JOMC Ant Tasks Tests' not found." );
    }

    @Test
    public final void testCommitOneSpecificationWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-specification-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Specification 'org.jomc.ant.test.JomcTask' not found." );
    }

    @Test
    public final void testCommitOneImplementationWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-implementation-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Implementation 'org.jomc.ant.test.JomcToolTask' not found." );
    }

    @Test
    public final void testCommitOneModuleWithNoopStylesheet() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-one-module-with-no-op-stylesheet" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
        assertMessageNotLogged( r, "Module 'JOMC Ant Tasks Tests' not found." );
    }

    @Test
    public final void testCommitAntTasksWithClasspathref() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-with-classpathref" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitAntTasksWithNestedClasspath() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-with-nested-classpath" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitAntTasksAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-all-attributes" );
        assertNoException( r );
        assertMessageLogged( r, "Class file processing successful.", Project.MSG_INFO );
    }

    @Test
    public final void testCommitAntTasksBrokenModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-commit-ant-tasks-broken-model" );
        assertException( r, ClassProcessingException.class );
    }

}
