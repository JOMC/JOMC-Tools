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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Relocation of {@code ModelObject}s.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ModelObjectRelocation implements Serializable
{

    /** Serial version UID for compatibility with 1.0.x object streams. */
    private static final long serialVersionUID = 3748603564132530461L;

    /**
     * The source pattern to relocate.
     * @serial
     */
    private String sourcePattern;

    /**
     * The replacement pattern to replace the source pattern with.
     * @serial
     */
    private String replacementPattern;

    /**
     * Set of relocation exclusion patterns.
     * @serial
     */
    private Set<String> exclusionPatterns;

    /**
     * Gets the source pattern to relocate.
     *
     * @return The source pattern to relocate.
     */
    public String getSourcePattern()
    {
        return this.sourcePattern;
    }

    /**
     * Sets the source pattern to relocate.
     *
     * @param value The new source pattern to relocate.
     */
    public void setSourcePattern( final String value )
    {
        this.sourcePattern = value;
    }

    /**
     * Gets the replacement pattern to replace the source pattern with.
     *
     * @return The replacement pattern to replace the source pattern with.
     */
    public String getReplacementPattern()
    {
        return this.replacementPattern;
    }

    /**
     * Sets the replacement pattern to replace the source pattern with.
     *
     * @param value The new replacement pattern to replace the source pattern with.
     */
    public void setReplacementPattern( final String value )
    {
        this.replacementPattern = value;
    }

    /**
     * Gets a set of relocation exclusion patterns.
     *
     * @return A set of relocation exclusion patterns.
     */
    public Set<String> getExclusionPatterns()
    {
        if ( this.exclusionPatterns == null )
        {
            this.exclusionPatterns = new HashSet<String>();
        }

        return this.exclusionPatterns;
    }

    /**
     * Creates and returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    private String toStringInternal()
    {
        final StringBuffer b = new StringBuffer().append( '{' ).
            append( "sourcePattern=" ).append( this.sourcePattern ).
            append( ", replacementPattern=" ).append( this.replacementPattern ).
            append( ", exclusionPatterns={" );

        for ( Iterator<String> it = this.getExclusionPatterns().iterator(); it.hasNext(); )
        {
            b.append( it.next() );
            if ( it.hasNext() )
            {
                b.append( ", " );
            }
        }
        b.append( "}}" );

        return b.toString();
    }

    /**
     * Creates and returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return super.toString() + this.toStringInternal();
    }

}
