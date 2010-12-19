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
package org.jomc.tools.model.test;

import org.jomc.tools.model.ToolsType;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test cases for class {@code org.jomc.tools.model.ToolsType}.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
public class ToolsTypeTest
{

    /** Creates a new {@code ToolsTypeTest} instance. */
    public ToolsTypeTest()
    {
        super();
    }

    public static class TestToolsType extends ToolsType
    {

        @Override
        public <T> JAXBElement<T> getAnyElement( final List<Object> any, final String namespaceURI,
                                                 final String localPart, final Class<T> type )
        {
            return super.getAnyElement( any, namespaceURI, localPart, type );
        }

        @Override
        public <T> T getAnyObject( final List<Object> any, final Class<T> clazz )
        {
            return super.getAnyObject( any, clazz );
        }

    }

    @Test
    public final void testGetAnyElement() throws Exception
    {
        final TestToolsType toolsObject = new TestToolsType();
        final List<Object> any = new ArrayList<Object>( 10 );
        final QName name = new QName( "http://jomc.org/tools/model", "test" );
        final JAXBElement<Object> element = new JAXBElement<Object>( name, Object.class, null, null );
        any.add( element );
        any.add( element );

        try
        {
            toolsObject.getAnyElement( any, "http://jomc.org/tools/model", "test", Object.class );
            fail( "Expected 'IllegalStateException' not thrown." );
        }
        catch ( final IllegalStateException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

    @Test
    public final void testGetAnyObject() throws Exception
    {
        final TestToolsType toolsObject = new TestToolsType();
        final List<Object> any = new ArrayList<Object>( 10 );
        any.add( "TEST" );
        any.add( "TEST" );

        try
        {
            toolsObject.getAnyObject( any, String.class );
            fail( "Expected 'IllegalStateException' not thrown." );
        }
        catch ( final IllegalStateException e )
        {
            assertNotNull( e.getMessage() );
            System.out.println( e );
        }
    }

}
