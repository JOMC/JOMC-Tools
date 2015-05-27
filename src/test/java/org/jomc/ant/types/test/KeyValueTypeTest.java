/*
 *   Copyright (C) Christian Schulte <cs@schulte.it>, 2011-293
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
package org.jomc.ant.types.test;

import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.jomc.ant.types.KeyValueType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.ant.types.KeyValueType}.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 */
public class KeyValueTypeTest
{

    /**
     * Creates a new {@code KeyValueTypeTest} instance.
     */
    public KeyValueTypeTest()
    {
        super();
    }

    @Test
    public void testGetValue() throws Exception
    {
        final KeyValueType keyValueType = new KeyValueType();

        try
        {
            keyValueType.getObject( null );
            fail( "Expected NullPointerException not thrown." );
        }
        catch ( final NullPointerException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }

        assertNull( keyValueType.getObject( new Location( "TEST" ) ) );

        keyValueType.setValue( "TEST" );
        assertEquals( "TEST", keyValueType.getObject( new Location( "TEST" ) ) );

        keyValueType.setValue( null );
        keyValueType.setType( Object.class );
        assertEquals( Object.class, keyValueType.getObject( new Location( "TEST" ) ).getClass() );

        keyValueType.setValue( "file:///" );
        keyValueType.setType( URL.class );
        assertEquals( URL.class, keyValueType.getObject( new Location( "TEST" ) ).getClass() );

        keyValueType.setValue( "TEST" );
        keyValueType.setType( Object.class );

        try
        {
            keyValueType.getObject( new Location( "TEST" ) );
            fail( "Expected BuildException not thrown." );
        }
        catch ( final BuildException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e.toString() );
        }
    }

}
