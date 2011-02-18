/*
 *   Copyright (C) 2009 The JOMC Project
 *   Copyright (C) 2005 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.ant.types;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Datatype holding a {@code language}, {@code country} and {@code variant} property.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class LocaleType implements Cloneable
{

    /** The language. */
    private String language;

    /** The country. */
    private String country;

    /** The variant. */
    private String variant;

    /** Creates a new {@code LocaleType} instance. */
    public LocaleType()
    {
        super();
    }

    /**
     * Gets the value of the {@code language} property.
     *
     * @return The value of the {@code language} property.
     *
     * @see #setLanguage(java.lang.String)
     */
    public final String getLanguage()
    {
        return this.language;
    }

    /**
     * Sets the value of the {@code language} property.
     *
     * @param value The new value of the {@code language} property or {@code null}.
     *
     * @see #getLanguage()
     */
    public final void setLanguage( final String value )
    {
        this.language = value;
    }

    /**
     * Gets the value of the {@code country} property.
     *
     * @return The value of the {@code country} property.
     *
     * @see #setCountry(java.lang.String)
     */
    public final String getCountry()
    {
        return this.country;
    }

    /**
     * Sets the value of the {@code country} property.
     *
     * @param value The new value of the {@code country} property or {@code null}.
     *
     * @see #getCountry()
     */
    public final void setCountry( final String value )
    {
        this.country = value;
    }

    /**
     * Gets the value of the {@code variant} property.
     *
     * @return The value of the {@code variant} property.
     *
     * @see #setVariant(java.lang.String)
     */
    public final String getVariant()
    {
        return this.variant;
    }

    /**
     * Sets the value of the {@code variant} property.
     *
     * @param value The new value of the {@code variant} property or {@code null}.
     *
     * @see #getVariant()
     */
    public final void setVariant( final String value )
    {
        this.variant = value;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public LocaleType clone()
    {
        try
        {
            return (LocaleType) super.clone();
        }
        catch ( final CloneNotSupportedException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Creates and returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

}
