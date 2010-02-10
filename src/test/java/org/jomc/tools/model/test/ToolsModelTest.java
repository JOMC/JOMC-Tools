/*
 *   Copyright (c) 2009 The JOMC Project
 *   Copyright (c) 2005 Christian Schulte <cs@jomc.org>
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

import javax.xml.XMLConstants;
import javax.xml.bind.Marshaller;
import junit.framework.Assert;
import org.jomc.tools.model.ToolsModel;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;

/**
 * Test cases for class {@code org.jomc.tools.model.ToolsModel}.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public class ToolsModelTest
{

    public void testCreateContext() throws Exception
    {
        Assert.assertNotNull( ToolsModel.createContext() );
    }

    public void testCreateEntityResolver() throws Exception
    {
        final EntityResolver entityResolver = ToolsModel.createEntityResolver();
        Assert.assertNotNull( entityResolver );
        Assert.assertNull( entityResolver.resolveEntity( null, "TEST" ) );
        Assert.assertNotNull( entityResolver.resolveEntity(
            null, "http://jomc.org/tools/model/jomc-tools-1.0.xsd" ) );

        Assert.assertNotNull( entityResolver.resolveEntity(
            ToolsModel.TOOLS_NS_URI, "http://jomc.org/tools/model/jomc-tools-1.0.xsd" ) );

        Assert.assertNotNull( entityResolver.resolveEntity(
            "TEST", "http://jomc.org/tools/model/jomc-tools-1.0.xsd" ) );

    }

    public void testCreateMarshaller() throws Exception
    {
        final Marshaller m = ToolsModel.createMarshaller();
        Assert.assertNotNull( m );
        Assert.assertNotNull( m.getProperty( Marshaller.JAXB_SCHEMA_LOCATION ) );
    }

    public void testCreateResourceResolver() throws Exception
    {
        final LSResourceResolver resourceResolver = ToolsModel.createResourceResolver();
        Assert.assertNotNull( resourceResolver );
        Assert.assertNull( resourceResolver.resolveResource( null, null, null, null, null ) );
        Assert.assertNotNull( resourceResolver.resolveResource(
            XMLConstants.W3C_XML_SCHEMA_NS_URI, ToolsModel.TOOLS_NS_URI, null, null, null ) );

        Assert.assertNotNull( resourceResolver.resolveResource(
            XMLConstants.W3C_XML_SCHEMA_NS_URI, null, ToolsModel.TOOLS_NS_URI, null, null ) );

        Assert.assertNotNull( resourceResolver.resolveResource(
            XMLConstants.W3C_XML_SCHEMA_NS_URI, null, null, "http://jomc.org/tools/model/jomc-tools-1.0.xsd", null ) );

    }

    public void testCreateSchema() throws Exception
    {
        Assert.assertNotNull( ToolsModel.createSchema() );
    }

    public void testCreateUnmarshaller() throws Exception
    {
        Assert.assertNotNull( ToolsModel.createUnmarshaller() );
    }

}
