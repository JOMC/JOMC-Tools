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
package org.jomc.ant.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

/**
 * Datatype holding a {@code key}, {@code value} and {@code type} property.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class KeyValueType implements Cloneable
{

    /** The key of the type. */
    private String key;

    /** The value of the type. */
    private String value;

    /** The class of the type of {@code value}. */
    private Class<?> type;

    /** Creates a new {@code KeyValueType} instance. */
    public KeyValueType()
    {
        super();
    }

    /**
     * Gets the value of the {@code key} property.
     *
     * @return The value of the {@code key} property.
     *
     * @see #setKey(java.lang.String)
     */
    public final String getKey()
    {
        return this.key;
    }

    /**
     * Sets the value of the {@code key} property.
     *
     * @param k The new value of the {@code key} property.
     *
     * @see #getKey()
     */
    public final void setKey( final String k )
    {
        this.key = k;
    }

    /**
     * Gets the value of the {@code value} property.
     *
     * @return The value of the {@code value} property or {@code null}.
     *
     * @see #setValue(java.lang.String)
     */
    public final String getValue()
    {
        return this.value;
    }

    /**
     * Sets the value of the {@code value} property.
     *
     * @param v The new value of the {@code value} property or {@code null}.
     *
     * @see #getValue()
     */
    public final void setValue( final String v )
    {
        this.value = v;
    }

    /**
     * Gets the value of the {@code type} property.
     *
     * @return The value of the {@code type} property or {@code null}.
     *
     * @see #setType(java.lang.Class)
     */
    public final Class<?> getType()
    {
        return this.type;
    }

    /**
     * Sets the value of the {@code type} property.
     *
     * @param t The new value of the {@code type} property or {@code null}.
     *
     * @see #getType()
     */
    public final void setType( final Class<?> t )
    {
        this.type = t;
    }

    /**
     * Gets the object of the instance.
     *
     * @param location The location the object is requested at.
     *
     * @return The object of the instance or {@code null}.
     *
     * @throws NullPointerException if {@code location} is {@code null}.
     * @throws BuildException if getting the object fails.
     *
     * @see #getType()
     * @see #getValue()
     */
    public Object getObject( final Location location ) throws BuildException
    {
        if ( location == null )
        {
            throw new NullPointerException( "location" );
        }

        try
        {
            Object o = this.getValue();

            if ( o != null )
            {
                if ( this.getType() != null && !String.class.equals( this.getType() ) )
                {
                    try
                    {
                        o = this.getType().getConstructor( String.class ).newInstance( o );
                    }
                    catch ( final NoSuchMethodException e )
                    {
                        final Method valueOf = this.getType().getMethod( "valueOf", String.class );

                        if ( Modifier.isStatic( valueOf.getModifiers() )
                             && valueOf.getReturnType().equals( this.getType() ) )
                        {
                            o = valueOf.invoke( null, o );
                        }
                        else
                        {
                            throw new BuildException(
                                Messages.getMessage( "noSuchMethodCreatingValueObject", this.getType(),
                                                     this.getValue(), this.getType().getSimpleName() ), e, location );

                        }
                    }
                }
            }
            else if ( this.getType() != null )
            {
                o = this.getType().newInstance();
            }

            return o;
        }
        catch ( final NoSuchMethodException e )
        {
            throw new BuildException(
                Messages.getMessage( "noSuchMethodCreatingValueObject", this.getType(),
                                     this.getValue(), this.getType().getSimpleName() ), e, location );

        }
        catch ( final InstantiationException e )
        {
            throw new BuildException( Messages.getMessage( "failureCreatingValueObject", this.getType(),
                                                           this.getValue() ), e, location );

        }
        catch ( final IllegalAccessException e )
        {
            throw new BuildException( Messages.getMessage( "failureCreatingValueObject", this.getType(),
                                                           this.getValue() ), e, location );

        }
        catch ( final InvocationTargetException e )
        {
            throw new BuildException( Messages.getMessage( "failureCreatingValueObject", this.getType(),
                                                           this.getValue() ), e, location );

        }
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public KeyValueType clone()
    {
        try
        {
            return (KeyValueType) super.clone();
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
