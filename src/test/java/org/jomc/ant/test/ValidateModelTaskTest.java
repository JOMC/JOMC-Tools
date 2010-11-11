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

import org.jomc.ant.ValidateModelTask;

/**
 * Test cases for class {@code org.jomc.ant.ValidateModelTask}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public final class ValidateModelTaskTest extends JomcModelTaskTest
{

    /** Creates a new {@code ValidateModelTaskTest} instance. */
    public ValidateModelTaskTest()
    {
        super();
    }

    /**
     * Creates a new {@code ValidateModelTaskTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public ValidateModelTaskTest( final String name )
    {
        super( name );
    }

    /**
     * Gets the {@code ValidateModelTask} tests are performed with.
     *
     * @return The {@code ValidateModelTask} tests are performed with.
     *
     * @see #createJomcTask()
     */
    @Override
    public ValidateModelTask getJomcTask()
    {
        return (ValidateModelTask) super.getJomcTask();
    }

    public void testValidateModel() throws Exception
    {
        this.expectLogContaining( "test-validate-model", "Model validation successful." );
    }

    public void testValidateModelWithRedundantResources() throws Exception
    {
        this.expectLogContaining( "test-validate-model-with-redundant-resources", "Model validation successful." );
    }

    public void testValidateModelWithBrokenModel() throws Exception
    {
        this.expectSpecificBuildException( "test-validate-model-with-broken-model",
                                           "the \"validate-model\" task points to invalid module resources.",
                                           "Model validation failure." );

    }

    /**
     * Creates a new {@code ValidateModelTask} instance tests are performed with.
     *
     * @return A new {@code ValidateModelTask} instance tests are performed with.
     *
     * @see #getJomcTask()
     */
    @Override
    protected ValidateModelTask createJomcTask()
    {
        return new ValidateModelTask();
    }

    /** {@inheritDoc} */
    @Override
    protected String getBuildFileName()
    {
        return "validate-model-test.xml";
    }

}
