/*
 *   Copyright (C) Christian Schulte (schulte2005@users.sourceforge.net), 2005-07-25
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
package org.jomc.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

/**
 * Gets thrown whenever processing source files fails.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
public class SourceProcessingException extends BuildException
{

    /** Serial version UID for backwards compatibility with 1.2.x object streams. */
    private static final long serialVersionUID = -104232960403079592L;

    /** Creates a new {@code SourceProcessingException} instance without descriptive information. */
    public SourceProcessingException()
    {
        super();
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a message.
     *
     * @param message A message describing the exception or {@code null}.
     */
    public SourceProcessingException( final String message )
    {
        super( message );
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a causing {@code Throwable}.
     *
     * @param t A {@code Throwable} causing the exception or {@code null}.
     */
    public SourceProcessingException( final Throwable t )
    {
        super( t );
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a message and a causing {@code Throwable}.
     *
     * @param message A message describing the exception or {@code null}.
     * @param t A {@code Throwable} causing the exception or {@code null}.
     */
    public SourceProcessingException( final String message, final Throwable t )
    {
        super( message, t );
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a message and a location.
     *
     * @param message A message describing the exception or {@code null}.
     * @param location A location the exception occurred or {@code null}.
     */
    public SourceProcessingException( final String message, final Location location )
    {
        super( message, location );
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a causing {@code Throwable} and a location.
     *
     * @param t A {@code Throwable} causing the exception or {@code null}.
     * @param location A location the exception occurred or {@code null}.
     */
    public SourceProcessingException( final Throwable t, final Location location )
    {
        super( t, location );
    }

    /**
     * Creates a new {@code SourceProcessingException} instance taking a message, a causing {@code Throwable} and a
     * location.
     *
     * @param message A message describing the exception or {@code null}.
     * @param t A {@code Throwable} causing the exception or {@code null}.
     * @param location A location the exception occurred or {@code null}.
     */
    public SourceProcessingException( final String message, final Throwable t, final Location location )
    {
        super( message, t, location );
    }

}
