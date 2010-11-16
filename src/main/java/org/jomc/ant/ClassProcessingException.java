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
package org.jomc.ant;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

/**
 * Gets thrown whenever processing class files fails.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $Id$
 */
public class ClassProcessingException extends BuildException
{

    /** Serial version UID for backwards compatibility with 1.2.x object streams. */
    private static final long serialVersionUID = -8803284087787885858L;

    /**
     * Creates a new {@code ClassProcessingException} instance taking a location.
     *
     * @param location The location this exception occured.
     */
    public ClassProcessingException( final Location location )
    {
        super( getMessage( "classProcessingFailure" ), location == null ? Location.UNKNOWN_LOCATION : location );
    }

    /**
     * Creates a new {@code ClassProcessingException} instance taking a causing throwable and a location.
     *
     * @param t The {@code Throwable} causing the throwable.
     * @param location The location this exception occured.
     */
    public ClassProcessingException( final Throwable t, final Location location )
    {
        super( getMessage( "classProcessingFailure" ), t, location == null ? Location.UNKNOWN_LOCATION : location );
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format( ResourceBundle.getBundle(
            ClassProcessingException.class.getName().replace( '.', '/' ) ).getString( key ), args );

    }

}
