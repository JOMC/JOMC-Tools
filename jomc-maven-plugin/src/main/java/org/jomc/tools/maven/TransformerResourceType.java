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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Datatype describing a XSLT document resource.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $JOMC$
 * @since 1.2
 */
public class TransformerResourceType extends ResourceType
{

    /**
     * Transformation parameter resources.
     */
    private List<TransformationParameterResource> transformationParameterResources;

    /**
     * Transformation parameters.
     */
    private List<TransformationParameter> transformationParameters;

    /**
     * Transformation output properties.
     */
    private List<TransformationOutputProperty> transformationOutputProperties;

    /**
     * Creates a new {@code TransformerResourceType} instance.
     */
    public TransformerResourceType()
    {
        super();
    }

    /**
     * Gets the transformation parameter resource to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameter resources property.
     * </p>
     *
     * @return The transformation parameter resources to apply.
     */
    public final List<TransformationParameterResource> getTransformationParameterResources()
    {
        if ( this.transformationParameterResources == null )
        {
            this.transformationParameterResources = new LinkedList<>();
        }

        return this.transformationParameterResources;
    }

    /**
     * Gets the transformation parameters to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameters property.
     * </p>
     *
     * @return The transformation parameters to apply.
     */
    public final List<TransformationParameter> getTransformationParameters()
    {
        if ( this.transformationParameters == null )
        {
            this.transformationParameters = new LinkedList<>();
        }

        return this.transformationParameters;
    }

    /**
     * Gets the transformation output properties to apply.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation output properties property.
     * </p>
     *
     * @return The transformation output properties to apply.
     */
    public final List<TransformationOutputProperty> getTransformationOutputProperties()
    {
        if ( this.transformationOutputProperties == null )
        {
            this.transformationOutputProperties = new LinkedList<>();
        }

        return this.transformationOutputProperties;
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
            clone.transformationOutputProperties = new ArrayList<>( this.transformationOutputProperties.size() );

            for ( final TransformationOutputProperty e : this.transformationOutputProperties )
            {
                clone.transformationOutputProperties.add( e.clone() );
            }
        }

        if ( this.transformationParameterResources != null )
        {
            clone.transformationParameterResources = new ArrayList<>( this.transformationParameterResources.size() );

            for ( final TransformationParameterResource e : this.transformationParameterResources )
            {
                clone.transformationParameterResources.add( e.clone() );
            }
        }

        if ( this.transformationParameters != null )
        {
            clone.transformationParameters = new ArrayList<>( this.transformationParameters.size() );

            for ( final TransformationParameter e : this.transformationParameters )
            {
                clone.transformationParameters.add( e.clone() );
            }
        }

        return clone;
    }

}
