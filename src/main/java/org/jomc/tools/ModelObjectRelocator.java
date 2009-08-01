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
package org.jomc.tools;

import java.util.HashSet;
import java.util.Set;
import org.jomc.model.Dependencies;
import org.jomc.model.Dependency;
import org.jomc.model.Implementation;
import org.jomc.model.Implementations;
import org.jomc.model.ModelObject;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.Specification;
import org.jomc.model.SpecificationReference;
import org.jomc.model.Specifications;

/**
 * Relocates {@code ModelObject}s.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ModelObjectRelocator
{

    /** Set of model object relocations of the instance. */
    private Set<ModelObjectRelocation> modelObjectRelocations;

    /**
     * Gets the model object relocation patterns of the instance.
     *
     * @return The model object relocation patterns of the instance.
     */
    public Set<ModelObjectRelocation> getModelObjectRelocations()
    {
        if ( this.modelObjectRelocations == null )
        {
            this.modelObjectRelocations = new HashSet<ModelObjectRelocation>();
        }

        return this.modelObjectRelocations;
    }

    /**
     * Relocates a given model object according to the relocation patterns of the instance.
     *
     * @param modelObject The {@code ModelObject} to relocate.
     * @param modelObjectType The type of {@code modelObject}.
     * 
     * @return A new instance of {@code modelObject} relocated according to the relocation patterns of the instance.
     *
     * @throws NullPointerException if {@code modelObject} is {@code null}.
     */
    public <T> T relocateModelObject( final ModelObject modelObject, final Class<T> modelObjectType )
    {
        if ( modelObject == null )
        {
            throw new NullPointerException( "modelObject" );
        }
        if ( modelObjectType == null )
        {
            throw new NullPointerException( "modelObjectType" );
        }

        final ModelObject copy = modelObject.clone();

        if ( copy instanceof Modules )
        {
            for ( Module m : ( (Modules) copy ).getModule() )
            {
                this.relocate( m );
            }
        }
        if ( copy instanceof Module )
        {
            this.relocate( (Module) copy );
        }
        if ( copy instanceof Implementations )
        {
            for ( Implementation i : ( (Implementations) copy ).getImplementation() )
            {
                this.relocate( i );
            }
        }
        if ( copy instanceof Implementation )
        {
            this.relocate( (Implementation) copy );
        }
        if ( copy instanceof Specifications )
        {
            for ( Specification s : ( (Specifications) copy ).getSpecification() )
            {
                this.relocate( s );
            }
            for ( SpecificationReference r : ( (Specifications) copy ).getReference() )
            {
                this.relocate( r );
            }
        }
        if ( copy instanceof Specification )
        {
            this.relocate( (Specification) copy );
        }
        if ( copy instanceof SpecificationReference )
        {
            this.relocate( (SpecificationReference) copy );
        }
        if ( copy instanceof Dependencies )
        {
            for ( Dependency d : ( (Dependencies) copy ).getDependency() )
            {
                this.relocate( d );
            }
        }

        return (T) copy;
    }

    private Module relocate( final Module module )
    {
        if ( module.getImplementations() != null )
        {
            for ( Implementation i : module.getImplementations().getImplementation() )
            {
                this.relocate( i );
            }
        }
        if ( module.getSpecifications() != null )
        {
            for ( Specification s : module.getSpecifications().getSpecification() )
            {
                this.relocate( s );
            }
            for ( SpecificationReference r : module.getSpecifications().getReference() )
            {
                this.relocate( r );
            }
        }

        return module;
    }

    private void relocate( final Implementation implementation )
    {
        if ( implementation.getClazz() != null )
        {
            final ModelObjectRelocation relocation = this.getRelocation( implementation.getClazz() );
            if ( relocation != null )
            {
                implementation.setClazz( implementation.getClazz().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
        if ( implementation.getIdentifier() != null )
        {
            final ModelObjectRelocation relocation = this.getRelocation( implementation.getIdentifier() );
            if ( relocation != null )
            {
                implementation.setIdentifier( implementation.getIdentifier().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
        if ( implementation.getParent() != null )
        {
            final ModelObjectRelocation relocation = this.getRelocation( implementation.getParent() );
            if ( relocation != null )
            {
                implementation.setParent( implementation.getParent().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
        if ( implementation.getSpecifications() != null )
        {
            for ( Specification s : implementation.getSpecifications().getSpecification() )
            {
                this.relocate( s );
            }
            for ( SpecificationReference ref : implementation.getSpecifications().getReference() )
            {
                this.relocate( ref );
            }
        }
        if ( implementation.getDependencies() != null )
        {
            for ( Dependency d : implementation.getDependencies().getDependency() )
            {
                this.relocate( d );
            }
        }
    }

    private void relocate( final Specification specification )
    {
        if ( specification.getIdentifier() != null )
        {
            final ModelObjectRelocation relocation = this.getRelocation( specification.getIdentifier() );
            if ( relocation != null )
            {
                specification.setIdentifier( specification.getIdentifier().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
    }

    private void relocate( final SpecificationReference specification )
    {
        if ( specification.getIdentifier() != null )
        {
            final ModelObjectRelocation relocation = this.getRelocation( specification.getIdentifier() );
            if ( relocation != null )
            {
                specification.setIdentifier( specification.getIdentifier().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
    }

    private ModelObjectRelocation getRelocation( final String source )
    {
        ModelObjectRelocation relocation = null;

        for ( ModelObjectRelocation r : this.getModelObjectRelocations() )
        {
            if ( source.startsWith( r.getSourcePattern() ) )
            {
                if ( relocation == null || relocation.getSourcePattern().length() < r.getSourcePattern().length() )
                {
                    relocation = r;
                }
            }
        }

        if ( relocation != null )
        {
            for ( String exlusion : relocation.getExclusionPatterns() )
            {
                if ( source.startsWith( exlusion ) )
                {
                    relocation = null;
                    break;
                }
            }
        }

        return relocation;
    }

}
