/*
 * Copyright (C) 2012 Christian Schulte <cs@schulte.it>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
 *
 */
package org.jomc.tools.cli.commands.test;

import java.io.ObjectInputStream;
import org.jomc.tools.cli.commands.CommandExecutionException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test cases for class {@code org.jomc.tools.cli.commands.CommandExecutionException}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
public class CommandExecutionExceptionTest
{

    /**
     * Constant to prefix relative resource names with.
     */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX =
        "/" + CommandExecutionExceptionTest.class.getPackage().getName().replace( '.', '/' ) + "/";

    /**
     * Creates a new {@code CommandExecutionExceptionTest} instance.
     */
    public CommandExecutionExceptionTest()
    {
        super();
    }

    @Test
    public final void testSerializable() throws Exception
    {
        final CommandExecutionException e;
        try ( final ObjectInputStream in = new ObjectInputStream( this.getClass().getResourceAsStream(
            ABSOLUTE_RESOURCE_NAME_PREFIX + "CommandExecutionException.ser" ) ) )
        {
            e = (CommandExecutionException) in.readObject();
        }

        assertEquals( "TEST", e.getMessage() );
    }

}
