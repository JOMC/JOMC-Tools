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
package org.jomc.tools.maven;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;

/**
 * Datatype holding a {@code key}, {@code value} and {@code type} property.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @since 1.2
 */
public class KeyValueType implements Cloneable
{

    /**
     * The key of the type.
     */
    private String key;

    /**
     * The value of the type.
     */
    private String value;

    /**
     * The name of the class of the type of {@code value}.
     */
    private String type;

    /**
     * Creates a new {@code KeyValueType} instance.
     */
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
     * @see #setType(java.lang.String)
     */
    public final String getType()
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
    public final void setType( final String t )
    {
        this.type = t;
    }

    /**
     * Gets the object of the instance.
     *
     * @param modelContext The context to use for getting the object of the instance.
     *
     * @return The object of the instance or {@code null}.
     *
     * @throws NullPointerException if {@code modelContext} is {@code null}.
     * @throws ReflectiveOperationException if getting the object of the instance fails.
     *
     * @see #getType()
     * @see #getValue()
     *
     * @since 1.8
     */
    public Object getObject( final ModelContext modelContext ) throws ReflectiveOperationException
    {
        if ( modelContext == null )
        {
            throw new NullPointerException( "modelContext" );
        }

        Class<?> javaClass;
        Object o = this.getValue();

        try
        {
            if ( o != null )
            {
                if ( this.getType() != null && !String.class.getName().equals( this.getType() ) )
                {
                    javaClass = modelContext.findClass( this.getType() );

                    if ( javaClass == null )
                    {
                        throw new ReflectiveOperationException( Messages.getMessage( "classNotFound",
                                                                                     this.getType() ) );

                    }

                    try
                    {
                        o = javaClass.getConstructor( String.class ).newInstance( o );
                    }
                    catch ( final NoSuchMethodException e )
                    {
                        final Method valueOf = javaClass.getMethod( "valueOf", String.class );

                        if ( Modifier.isStatic( valueOf.getModifiers() )
                                 && valueOf.getReturnType().equals( javaClass ) )
                        {
                            o = valueOf.invoke( null, o );
                        }
                        else
                        {
                            throw new ReflectiveOperationException(
                                Messages.getMessage( "noSuchMethodCreatingObject", this.getType(),
                                                     this.getValue(), javaClass.getSimpleName() ), e );

                        }
                    }
                }
            }
            else if ( this.getType() != null )
            {
                javaClass = modelContext.findClass( this.getType() );

                if ( javaClass == null )
                {
                    throw new ReflectiveOperationException( Messages.getMessage( "classNotFound", this.getType() ) );
                }

                o = javaClass.newInstance();
            }

            return o;
        }
        catch ( final ModelException e )
        {
            throw new ReflectiveOperationException( Messages.getMessage( "failedSearchingClass", this.getType() ), e );
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
