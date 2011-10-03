/*
 *   Copyright (C) Christian Schulte, 2005-07-25
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
package org.jomc.mojo;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Datatype describing a resource.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 * @since 1.2
 */
public class ResourceType implements Cloneable
{

    /** The location of the resource. */
    private String location;

    /** Flag indicating the resource is optional. */
    private boolean optional;

    /** Timeout value, in milliseconds, to be used when opening communications links to the resource. */
    private int connectTimeout = 60000;

    /** Timeout value, in milliseconds, to be used when reading the resource. */
    private int readTimeout = 60000;

    /** Creates a new {@code ResourceType} instance. */
    public ResourceType()
    {
        super();
    }

    /**
     * Gets the value of the {@code location} property.
     *
     * @return The value of the {@code location} property.
     */
    public final String getLocation()
    {
        return this.location;
    }

    /**
     * Sets the value of the {@code location} property.
     *
     * @param value The value of the {@code location} property.
     */
    public final void setLocation( final String value )
    {
        this.location = value;
    }

    /**
     * Gets a flag indicating the resource is optional.
     *
     * @return {@code true}, if the resource is optional; {@code false}, if the build fails when the resource is not
     * found.
     */
    public final boolean isOptional()
    {
        return this.optional;
    }

    /**
     * Sets the flag indicating the resource is optional.
     *
     * @param value {@code true}, to flag the resource optional; {@code false}, to fail the build when the resource is
     * not found.
     */
    public final void setOptional( final boolean value )
    {
        this.optional = value;
    }

    /**
     * Gets the timeout value, in milliseconds, to be used when opening communications links to the resource.
     * A timeout of zero is interpreted as an infinite timeout.
     *
     * @return The timeout value, in milliseconds, to be used when opening communications links to the resource.
     */
    public final int getConnectTimeout()
    {
        return this.connectTimeout;
    }

    /**
     * Sets the timeout value, in milliseconds, to be used when opening communications links to the resource.
     * A timeout of zero is interpreted as an infinite timeout.
     *
     * @param value The new timeout value, in milliseconds, to be used when opening communications links to the
     * resource.
     */
    public final void setConnectTimeout( final int value )
    {
        this.connectTimeout = value;
    }

    /**
     * Gets the timeout value, in milliseconds, to be used when reading the resource. A timeout of zero is interpreted
     * as an infinite timeout.
     *
     * @return The timeout value, in milliseconds, to be used when reading the resource.
     */
    public final int getReadTimeout()
    {
        return this.readTimeout;
    }

    /**
     * Sets the timeout value, in milliseconds, to be used when reading the resource. A timeout of zero is interpreted
     * as an infinite timeout.
     *
     * @param value The new timeout value, in milliseconds, to be used when reading the resource.
     */
    public final void setReadTimeout( final int value )
    {
        this.readTimeout = value;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public ResourceType clone()
    {
        try
        {
            return (ResourceType) super.clone();
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
