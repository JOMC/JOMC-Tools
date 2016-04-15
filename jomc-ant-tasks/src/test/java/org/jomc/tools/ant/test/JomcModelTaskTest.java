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
import org.jomc.tools.ant.JomcModelTask;
import org.jomc.tools.ant.test.support.AntExecutionResult;
import static org.jomc.tools.ant.test.support.Assert.assertException;
import static org.jomc.tools.ant.test.support.Assert.assertExceptionMessage;
import org.junit.Test;

/**
 * Test cases for class {@code org.jomc.tools.ant.JomcModelTask}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class JomcModelTaskTest extends JomcTaskTest
{

    /**
     * Creates a new {@code JomcModelTaskTest} instance.
     */
    public JomcModelTaskTest()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JomcModelTask getJomcTask()
    {
        return (JomcModelTask) super.getJomcTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JomcModelTask newJomcTask()
    {
        return new JomcModelTask();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getBuildFileName()
    {
        return "jomc-model-task-test.xml";
    }

    @Test
    public final void testModuleResourceMissingLocation() throws Exception
    {
        final AntExecutionResult r = this.executeTarget( "test-module-resource-missing-location" );
        assertException( r, BuildException.class );
        assertExceptionMessage( r, "Mandatory attribute 'location' is missing a value." );
    }

}
