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
package org.jomc.tools.maven;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Datatype describing a properties resource.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @since 1.2
 */
public class PropertiesResourceType extends ResourceType
{

    /**
     * Constant for the plain properties file format.
     */
    public static final String PLAIN_FORMAT = "plain";

    /**
     * Constant for the XML properties file format.
     */
    public static final String XML_FORMAT = "xml";

    /**
     * Supported properties file format values.
     */
    private static final String[] FORMAT_VALUES =
    {
        PLAIN_FORMAT, XML_FORMAT
    };

    /**
     * The format of the properties resource.
     */
    private String format;

    /**
     * Creates a new {@code PropertiesResourceType} instance.
     */
    public PropertiesResourceType()
    {
        super();
    }

    /**
     * Gets the value of the {@code format} property.
     *
     * @return The value of the {@code format} property.
     */
    public final String getFormat()
    {
        if ( this.format == null )
        {
            this.format = PLAIN_FORMAT;
        }

        return this.format;
    }

    /**
     * Sets the value of the {@code format} property.
     *
     * @param value The new value of the {@code format} property or {@code null}.
     */
    public final void setFormat( final String value )
    {
        this.format = value;
    }

    /**
     * Gets a list holding supported format values.
     *
     * @return An unmodifiable list holding supported format values.
     *
     * @see #isFormatSupported(java.lang.String)
     */
    public static List<String> getSupportedFormats()
    {
        return Collections.unmodifiableList( Arrays.asList( FORMAT_VALUES ) );
    }

    /**
     * Tests a given format value.
     *
     * @param value The format value to test.
     *
     * @return {@code true}, if the given format value is supported; {@code false}, if the given format value is not
     * supported.
     *
     * @see #getSupportedFormats()
     */
    public static boolean isFormatSupported( final String value )
    {
        if ( value != null )
        {
            for ( int i = FORMAT_VALUES.length - 1; i >= 0; i-- )
            {
                if ( value.equalsIgnoreCase( FORMAT_VALUES[i] ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public PropertiesResourceType clone()
    {
        return (PropertiesResourceType) super.clone();
    }

}
