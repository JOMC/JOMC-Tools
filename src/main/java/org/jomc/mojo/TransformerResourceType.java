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
package org.jomc.mojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Datatype describing a XSLT document resource.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 * @since 1.2
 */
public class TransformerResourceType extends ResourceType
{

    /** Transformation parameter resources. */
    private List<TransformationParameterResource> transformationParameterResources;

    /** Transformation parameters. */
    private Map<String, Object> transformationParameters;

    /** Transformation output properties. */
    private Map<String, String> transformationOutputProperties;

    /** Creates a new {@code TransformerResourceType} instance. */
    public TransformerResourceType()
    {
        super();
    }

    /**
     * Gets the transformation parameter resource to apply.
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make
     * to the returned list will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameter resources property.</p>
     *
     * @return The transformation parameter resources to apply.
     */
    public final List<TransformationParameterResource> getTransformationParameterResources()
    {
        if ( this.transformationParameterResources == null )
        {
            this.transformationParameterResources = new LinkedList<TransformationParameterResource>();
        }

        return this.transformationParameterResources;
    }

    /**
     * Gets the transformation parameters to apply.
     * <p>This accessor method returns a reference to the live map, not a snapshot. Therefore any modification you make
     * to the returned map will be present inside the object. This is why there is no {@code set} method for the
     * transformation parameters property.</p>
     *
     * @return The transformation parameters to apply.
     */
    public final Map<String, Object> getTransformationParameters()
    {
        if ( this.transformationParameters == null )
        {
            this.transformationParameters = new HashMap<String, Object>();
        }

        return this.transformationParameters;
    }

    /**
     * Gets the transformation output properties to apply.
     * <p>This accessor method returns a reference to the live map, not a snapshot. Therefore any modification you make
     * to the returned map will be present inside the object. This is why there is no {@code set} method for the
     * transformation output properties property.</p>
     *
     * @return The transformation output properties to apply.
     */
    public final Map<String, String> getTransformationOutputProperties()
    {
        if ( this.transformationOutputProperties == null )
        {
            this.transformationOutputProperties = new HashMap<String, String>();
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
        clone.transformationParameterResources =
            this.transformationParameterResources != null
            ? new ArrayList<TransformationParameterResource>( this.transformationParameterResources ) : null;

        clone.transformationParameters = this.transformationParameters != null
                                         ? new HashMap<String, Object>( this.transformationParameters ) : null;

        clone.transformationOutputProperties = this.transformationOutputProperties != null
                                               ? new HashMap<String, String>( this.transformationOutputProperties )
                                               : null;

        return clone;
    }

}
