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
package org.jomc.tools.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Object management and configuration tools model.
 *
 * @author <a href="mailto:cs@jomc.org">Christian Schulte</a>
 * @version $Id$
 */
public abstract class ToolsModel
{

    /** Constant for the {@code http://jomc.org/tools/model} namespace URI. */
    public static final String TOOLS_NS_URI = "http://jomc.org/tools/model";

    /** Constant for the {@code http://jomc.org/tools/model/jomc-tools-1.0.xsd} system identifier. */
    private static final String TOOLS_SYSTEM_ID = "http://jomc.org/tools/model/jomc-tools-1.0.xsd";

    /** Constant for the {@code jomc-tools-1.0.xsd} schema resource name. */
    private static final String TOOLS_SCHEMA_RESOURCE = "jomc-tools-1.0.xsd";

    /** Creates a new {@code ToolsModel} instance. */
    public ToolsModel()
    {
        super();
    }

    /**
     * Creates a new {@code JAXBContext} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @return A new {@code JAXBContext} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @throws JAXBException if creating a new {@code JAXBContext} instance fails.
     */
    public static JAXBContext createContext() throws JAXBException
    {
        return JAXBContext.newInstance( ToolsModel.class.getPackage().getName() );
    }

    /**
     * Creates a new JAXB {@code Marshaller} instance for the {@code http://jomc.org/tools/model} XML namespace.
     * <p>The {@code Marshaller.JAXB_SCHEMA_LOCATION} property will be set to the schema location of the
     * {@code http://jomc.org/tools/model} namespace.</p>
     *
     * @return A new JAXB {@code Marshaller} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @throws JAXBException if creating a new {@code Marshaller} instance fails.
     */
    public static Marshaller createMarshaller() throws JAXBException
    {
        final Marshaller m = createContext().createMarshaller();
        m.setProperty( Marshaller.JAXB_SCHEMA_LOCATION, TOOLS_NS_URI + " " + TOOLS_SYSTEM_ID );
        return m;
    }

    /**
     * Creates a new JAXB {@code Unmarshaller} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @return A new JAXB {@code Unmarshaller} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @throws JAXBException if creating a new {@code Unmarshaller} instance fails.
     */
    public static Unmarshaller createUnmarshaller() throws JAXBException
    {
        return createContext().createUnmarshaller();
    }

    /**
     * Creates a new JAXP {@code Schema} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @return A new JAXP {@code Schema} instance for the {@code http://jomc.org/tools/model} XML namespace.
     *
     * @throws SAXException if creating a new {@code Schema} instance fails.
     */
    public static Schema createSchema() throws SAXException
    {
        final URL toolsSchema = ToolsModel.class.getResource( TOOLS_SCHEMA_RESOURCE );
        assert toolsSchema != null : "Expected '" + TOOLS_SCHEMA_RESOURCE + "' not found.";
        return SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI ).newSchema( toolsSchema );
    }

    /**
     * Creates a new JAXP {@code EntityResolver} instance for resolving entities of the
     * {@code http://jomc.org/tools/model} XML namespace.
     *
     * @return A new JAXP {@code EntityResolver} instance for resolving entities of the
     * {@code http://jomc.org/tools/model} XML namespace.
     *
     * @throws SAXException if creating a new {@code EntityResolver} instance fails.
     */
    public static EntityResolver createEntityResolver() throws SAXException
    {
        return new EntityResolver()
        {

            public InputSource resolveEntity( final String publicId, final String systemId )
                throws SAXException, IOException
            {
                InputSource inputSource = null;

                if ( TOOLS_NS_URI.equals( publicId ) || TOOLS_SYSTEM_ID.equals( systemId ) )
                {
                    final URL toolsSchema = ToolsModel.class.getResource( TOOLS_SCHEMA_RESOURCE );
                    assert toolsSchema != null : "Expected '" + TOOLS_SCHEMA_RESOURCE + "' not found.";

                    inputSource = new InputSource();
                    inputSource.setByteStream( toolsSchema.openStream() );
                    inputSource.setPublicId( TOOLS_NS_URI );
                    inputSource.setSystemId( toolsSchema.toExternalForm() );
                }

                return inputSource;
            }

        };
    }

    /**
     * Creates a new JAXP {@code LSResourceResolver} instance for resolving entities of the
     * {@code http://jomc.org/tools/model} XML namespace.
     *
     * @return A new JAXP {@code LSResourceResolver} instance for resolving entities of the
     * {@code http://jomc.org/tools/model} XML namespace.
     */
    public static LSResourceResolver createResourceResolver()
    {
        return new LSResourceResolver()
        {

            public LSInput resolveResource( final String type, final String namespaceURI, final String publicId,
                                            final String systemId, final String baseURI )
            {
                if ( XMLConstants.W3C_XML_SCHEMA_NS_URI.equals( type ) &&
                     ( TOOLS_NS_URI.equals( namespaceURI ) || TOOLS_NS_URI.equals( publicId ) ||
                       TOOLS_SYSTEM_ID.equals( systemId ) ) )
                {
                    final URL toolsSchema = ToolsModel.class.getResource( TOOLS_SCHEMA_RESOURCE );
                    assert toolsSchema != null : "Expected '" + TOOLS_SCHEMA_RESOURCE + "' not found.";

                    return new LSInput()
                    {

                        public Reader getCharacterStream()
                        {
                            return null;
                        }

                        public void setCharacterStream( final Reader characterStream )
                        {
                        }

                        public InputStream getByteStream()
                        {
                            return null;
                        }

                        public void setByteStream( final InputStream byteStream )
                        {
                        }

                        public String getStringData()
                        {
                            return null;
                        }

                        public void setStringData( final String stringData )
                        {
                        }

                        public String getSystemId()
                        {
                            return toolsSchema.toExternalForm();
                        }

                        public void setSystemId( final String systemId )
                        {
                        }

                        public String getPublicId()
                        {
                            return TOOLS_NS_URI;
                        }

                        public void setPublicId( final String publicId )
                        {
                        }

                        public String getBaseURI()
                        {
                            return baseURI;
                        }

                        public void setBaseURI( final String baseURI )
                        {
                        }

                        public String getEncoding()
                        {
                            return null;
                        }

                        public void setEncoding( final String encoding )
                        {
                        }

                        public boolean getCertifiedText()
                        {
                            return false;
                        }

                        public void setCertifiedText( final boolean certifiedText )
                        {
                        }

                    };
                }

                return null;
            }

        };
    }

}
