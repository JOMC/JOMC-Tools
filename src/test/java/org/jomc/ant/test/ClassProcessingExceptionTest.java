/*
 *   Copyright (C) Christian Schulte, 2005-206
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

import java.io.IOException;
import java.io.ObjectInputStream;
import org.junit.Test;

/**
 * Test cases for class {@code org.jomc.ant.ClassProcessingException}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class ClassProcessingExceptionTest
{

    /** Constant to prefix relative resource names with. */
    private static final String ABSOLUTE_RESOURCE_NAME_PREFIX = "/org/jomc/ant/test/";

    /** Creates a new {@code ClassProcessingExceptionTest} instance. */
    public ClassProcessingExceptionTest()
    {
        super();
    }

    @Test
    public final void testClassProcessingException() throws Exception
    {
        ObjectInputStream in = null;
        boolean suppressExceptionOnClose = true;

        try
        {
            in = new ObjectInputStream( this.getClass().getResourceAsStream(
                ABSOLUTE_RESOURCE_NAME_PREFIX + "ClassProcessingException.ser" ) );

            System.out.println( in.readObject() );
            suppressExceptionOnClose = false;
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
            }
            catch ( final IOException ex )
            {
                if ( !suppressExceptionOnClose )
                {
                    throw ex;
                }
            }
        }
    }

}
