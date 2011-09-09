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
import org.jomc.ant.ValidateModelTask;
import org.jomc.ant.test.support.AntExecutionResult;
import org.junit.Test;
import static org.jomc.ant.test.support.Assert.assertException;
import static org.jomc.ant.test.support.Assert.assertExceptionMessage;
import static org.jomc.ant.test.support.Assert.assertMessageLogged;
import static org.jomc.ant.test.support.Assert.assertNoException;

/**
 * Test cases for class {@code org.jomc.ant.ValidateModelTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ValidateModelTaskTest extends JomcModelTaskTest
{

    /** Creates a new {@code ValidateModelTaskTest} instance. */
    public ValidateModelTaskTest()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public ValidateModelTask getJomcTask()
    {
        return (ValidateModelTask) super.getJomcTask();
    }

    /** {@inheritDoc} */
    @Override
    protected ValidateModelTask newJomcTask()
    {
        return new ValidateModelTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "validate-model-test.xml";
    }

    @Test
    public final void testModuleResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-module-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

    @Test
    public final void testModelContextAttributeMissingKey() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-model-context-attribute-missing-key" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'key' is missing a value." );
    }

    @Test
    public final void testValidateModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-model" );
        assertNoException( r );
        assertMessageLogged( r, "Model validation successful.", Project.MSG_INFO );
    }

    @Test
    public final void testValidateModelWithRedundantResources() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-model-with-redundant-resources" );
        assertNoException( r );
        assertMessageLogged( r, "Model validation successful.", Project.MSG_INFO );
    }

    @Test
    public final void testValidateModelWithBrokenModel() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-validate-model-with-broken-model" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Model validation failure." );
    }

}
