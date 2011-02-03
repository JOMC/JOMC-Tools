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

import org.apache.tools.ant.Project;
import org.jomc.ant.ShowModelTask;
import org.junit.Test;
import static org.jomc.ant.test.Assert.assertMessageLogged;
import static org.jomc.ant.test.Assert.assertMessageLoggedContaining;
import static org.jomc.ant.test.Assert.assertNoException;

/**
 * Test cases for class {@code org.jomc.ant.ShowModelTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ShowModelTaskTest extends JomcModelTaskTest
{

    /** Creates a new {@code ShowModelTaskTest} instance. */
    public ShowModelTaskTest()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public ShowModelTask getJomcTask()
    {
        return (ShowModelTask) super.getJomcTask();
    }

    /** {@inheritDoc} */
    @Override
    protected ShowModelTask newJomcTask()
    {
        return new ShowModelTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "show-model-task-test.xml";
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
    public final void testShowModelAllAttributes() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-show-model-all-attributes" );
        assertNoException( r );
        assertMessageLoggedContaining( r, "Writing", Project.MSG_INFO );
    }

}
