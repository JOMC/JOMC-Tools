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
import org.jomc.model.bootstrap.BootstrapObject;
import org.jomc.model.bootstrap.Schema;
import org.jomc.model.bootstrap.Schemas;

/**
 * Relocates {@code BootstrapObject}s.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class BootstrapObjectRelocator
{

    /** Set of bootstrap object relocations of the instance. */
    private Set<BootstrapObjectRelocation> bootstrapObjectRelocations;

    /**
     * Gets the bootstrap object relocation patterns of the instance.
     *
     * @return The bootstrap object relocation patterns of the instance.
     */
    public Set<BootstrapObjectRelocation> getBootstrapObjectRelocations()
    {
        if ( this.bootstrapObjectRelocations == null )
        {
            this.bootstrapObjectRelocations = new HashSet<BootstrapObjectRelocation>();
        }

        return this.bootstrapObjectRelocations;
    }

    /**
     * Relocates a given bootstrap object according to the relocation patterns of the instance.
     *
     * @param bootstrapObject The {@code BootstrapObject} to relocate.
     * @param bootstrapObjectType The type of {@code bootstrapObject}.
     *
     * @return A new instance of {@code bootstrapObject} relocated according to the relocation patterns of the instance.
     *
     * @throws NullPointerException if {@code bootstrapObject} or {@code bootstrapObjectType} is {@code null}.
     */
    public <T> T relocateBootstrapObject( final BootstrapObject bootstrapObject, final Class<T> bootstrapObjectType )
    {
        if ( bootstrapObject == null )
        {
            throw new NullPointerException( "bootstrapObject" );
        }
        if ( bootstrapObjectType == null )
        {
            throw new NullPointerException( "bootstrapObjectType" );
        }

        final BootstrapObject copy = bootstrapObject.clone();

        if ( copy instanceof Schemas )
        {
            for ( Schema s : ( (Schemas) copy ).getSchema() )
            {
                this.relocate( s );
            }
        }
        if ( copy instanceof Schema )
        {
            this.relocate( (Schema) copy );
        }

        return (T) copy;
    }

    private void relocate( final Schema schema )
    {
        if ( schema.getClasspathId() != null )
        {
            final BootstrapObjectRelocation relocation = this.getRelocation( schema.getClasspathId() );
            if ( relocation != null )
            {
                schema.setClasspathId( schema.getClasspathId().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
        if ( schema.getContextId() != null )
        {
            final BootstrapObjectRelocation relocation = this.getRelocation( schema.getContextId() );
            if ( relocation != null )
            {
                schema.setContextId( schema.getContextId().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
        if ( schema.getSystemId() != null )
        {
            final BootstrapObjectRelocation relocation = this.getRelocation( schema.getSystemId() );
            if ( relocation != null )
            {
                schema.setSystemId( schema.getSystemId().replaceFirst(
                    relocation.getSourcePattern(), relocation.getReplacementPattern() ) );

            }
        }
    }

    private BootstrapObjectRelocation getRelocation( final String source )
    {
        BootstrapObjectRelocation relocation = null;

        for ( BootstrapObjectRelocation r : this.getBootstrapObjectRelocations() )
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
