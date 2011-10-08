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
package org.jomc.ant;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utilities for accessing messages.
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a>
 * @version $JOMC$
 */
abstract class Messages
{

    /** Constant for the name of the resource bundle backing the tasks. */
    private static final String RESOURCE_BUNDLE_NAME =
        Messages.class.getPackage().getName().replace( '.', '/' ) + "/Messages";

    /** Creates a new {@code Messages} instance. */
    Messages()
    {
        super();
    }

    /**
     * Gets a message for a given key from the resource bundle backing the tasks formatted using the given arguments.
     *
     * @param key The key of the string to get.
     * @param arguments The arguments to format the string with.
     *
     * @return The string matching {@code key} formatted using {@code arguments}.
     *
     * @throws NullPointerException if {@code key} is {@code null}.
     */
    static String getMessage( final String key, final Object... arguments )
    {
        if ( key == null )
        {
            throw new NullPointerException( "key" );
        }

        try
        {
            return MessageFormat.format( ResourceBundle.getBundle( RESOURCE_BUNDLE_NAME ).getString( key ), arguments );
        }
        catch ( final MissingResourceException e )
        {
            throw new AssertionError( e );
        }
        catch ( final ClassCastException e )
        {
            throw new AssertionError( e );
        }
        catch ( final IllegalArgumentException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Gets the message of a given {@code Throwable} recursively.
     *
     * @param t The {@code Throwable} to get the message of or {@code null}.
     *
     * @return The message of {@code t} or {@code null}.
     */
    static String getMessage( final Throwable t )
    {
        return t != null ? t.getMessage() != null ? t.getMessage() : getMessage( t.getCause() ) : null;
    }

}
