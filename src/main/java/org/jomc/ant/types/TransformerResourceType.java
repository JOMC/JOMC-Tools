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
package org.jomc.ant.types;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Datatype describing a XSLT document resource.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class TransformerResourceType extends ResourceType
{

    /** The transformation parameter resources to apply. */
    private List<PropertiesResourceType> transformationParameterResources;

    /** The transformation parameters to apply. */
    private List<KeyValueType<String, Object>> transformationParameters;

    /** The transformation output properties to apply. */
    private List<KeyValueType<String, String>> transformationOutputProperties;

    /** Creates a new {@code TransformerResourceType}. */
    public TransformerResourceType()
    {
        super();
    }

    /**
     * Gets the transformation parameters to apply.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameters property.</p>
     *
     * @return The transformation parameters to apply.
     *
     * @see #createTransformationParameter()
     */
    public final List<KeyValueType<String, Object>> getTransformationParameters()
    {
        if ( this.transformationParameters == null )
        {
            this.transformationParameters = new LinkedList<KeyValueType<String, Object>>();
        }

        return this.transformationParameters;
    }

    /**
     * Creates a new {@code transformationParameter} element instance.
     *
     * @return A new {@code transformationParameter} element instance.
     *
     * @see #getTransformationParameters()
     */
    public KeyValueType<String, Object> createTransformationParameter()
    {
        final KeyValueType<String, Object> transformationParameter = new KeyValueType<String, Object>();
        this.getTransformationParameters().add( transformationParameter );
        return transformationParameter;
    }

    /**
     * Gets the transformation parameter resources to apply.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameter resources property.</p>
     *
     * @return The transformation parameter resources to apply.
     *
     * @see #createTransformationParameterResource()
     */
    public final List<PropertiesResourceType> getTransformationParameterResources()
    {
        if ( this.transformationParameterResources == null )
        {
            this.transformationParameterResources = new LinkedList<PropertiesResourceType>();
        }

        return this.transformationParameterResources;
    }

    /**
     * Creates a new {@code transformationParameterResource} element instance.
     *
     * @return A new {@code transformationParameterResource} element instance.
     *
     * @see #getTransformationParameterResources()
     */
    public PropertiesResourceType createTransformationParameterResource()
    {
        final PropertiesResourceType transformationParameterResource = new PropertiesResourceType();
        this.getTransformationParameterResources().add( transformationParameterResource );
        return transformationParameterResource;
    }

    /**
     * Gets the transformation output properties to apply.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation output properties property.</p>
     *
     * @return The transformation output properties to apply.
     *
     * @see #createTransformationOutputProperty()
     */
    public final List<KeyValueType<String, String>> getTransformationOutputProperties()
    {
        if ( this.transformationOutputProperties == null )
        {
            this.transformationOutputProperties = new LinkedList<KeyValueType<String, String>>();
        }

        return this.transformationOutputProperties;
    }

    /**
     * Creates a new {@code transformationOutputProperty} element instance.
     *
     * @return A new {@code transformationOutputProperty} element instance.
     *
     * @see #getTransformationOutputProperties()
     */
    public KeyValueType<String, String> createTransformationOutputProperty()
    {
        final KeyValueType<String, String> transformationOutputProperty = new KeyValueType<String, String>();
        this.getTransformationOutputProperties().add( transformationOutputProperty );
        return transformationOutputProperty;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return A copy of this object.
     */
    @Override
    public TransformerResourceType clone()
    {
        final TransformerResourceType clone = (TransformerResourceType) super.clone();

        if ( this.transformationOutputProperties != null )
        {
            clone.transformationOutputProperties =
                new ArrayList<KeyValueType<String, String>>( this.transformationOutputProperties.size() );

            for ( KeyValueType<String, String> e : this.transformationOutputProperties )
            {
                clone.transformationOutputProperties.add( e.clone() );
            }
        }

        if ( this.transformationParameterResources != null )
        {
            clone.transformationParameterResources =
                new ArrayList<PropertiesResourceType>( this.transformationParameterResources.size() );

            for ( PropertiesResourceType e : this.transformationParameterResources )
            {
                clone.transformationParameterResources.add( e.clone() );
            }
        }

        if ( this.transformationParameters != null )
        {
            clone.transformationParameters =
                new ArrayList<KeyValueType<String, Object>>( this.transformationParameters.size() );

            for ( KeyValueType<String, Object> e : this.transformationParameters )
            {
                clone.transformationParameters.add( e.clone() );
            }
        }

        return clone;
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
