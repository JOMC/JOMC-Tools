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

import org.apache.tools.ant.Location;
import org.jomc.ant.ResourceProcessingException;
import java.io.ObjectInputStream;
import junit.framework.TestCase;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test cases for class {@code org.jomc.ant.ResourceProcessingException}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ResourceProcessingExceptionTest extends TestCase
{

    /** Creates a new {@code ResourceProcessingExceptionTest} instance. */
    public ResourceProcessingExceptionTest()
    {
        super();
    }

    /**
     * Creates a new {@code ResourceProcessingExceptionTest} instance taking a name.
     *
     * @param name The name of the instance.
     */
    public ResourceProcessingExceptionTest( final String name )
    {
        super( name );
    }

    public void testResourceProcessingException() throws Exception
    {
        final ObjectInputStream in =
            new ObjectInputStream( this.getClass().getResourceAsStream( "ResourceProcessingException.ser" ) );

        final ResourceProcessingException e = (ResourceProcessingException) in.readObject();
        in.close();

        assertNotNull( e.getMessage() );
        assertEquals( Location.UNKNOWN_LOCATION, e.getLocation() );
        System.out.println( e );
    }

}
