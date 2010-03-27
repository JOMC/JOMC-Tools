// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2010 The JOMC Project
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
// </editor-fold>
// SECTION-END
package org.jomc.cli;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * Command.
 *
 * <p>
 *   This specification declares a multiplicity of {@code Many}.
 *   An application assembler may provide multiple implementations of this specification (including none).
 * </p>
 *
 * <p>
 *   Use of class {@link org.jomc.ObjectManager ObjectManager} is supported for accessing implementations.
 *   <pre>
 * Command[] objects = ObjectManagerFactory.getObjectManager( getClass().getClassLoader() ).getObject( Command[].class );
 * Command object = ObjectManagerFactory.getObjectManager( getClass().getClassLoader() ).getObject( Command.class, "<i>implementation name</i>" );
 *   </pre>
 * </p>
 *
 * <p>
 *   This specification does not apply to any scope. A new object is returned whenever requested.
 * </p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-19-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public interface Command
{
    // SECTION-START[Command]

    /** Listener interface. */
    public interface Listener
    {

        /**
         * Get called on logging.
         *
         * @param level The level of the event.
         * @param message The message of the event or {@code null}.
         * @param t The throwable of the event or {@code null}.
         *
         * @throws NullPointerException if {@code level} is {@code null}.
         */
        void onLog( Level level, String message, Throwable t );

    }

    /** Status code when the command completed successfully. */
    int STATUS_SUCCESS = 0;

    /** Status code when the command failed. */
    int STATUS_FAILURE = 1;

    /**
     * Gets the list of registered listeners.
     *
     * @return The list of registered listeners.
     */
    List<Listener> getListeners();

    /**
     * Gets the log level of the instance.
     *
     * @return The log level of the instance.
     *
     * @see #setLogLevel(java.util.logging.Level)
     */
    Level getLogLevel();

    /**
     * Sets the log level of the instance.
     *
     * @param value The new log level of the instance or {@code null}.
     *
     * @see #getLogLevel()
     */
    void setLogLevel( Level value );

    /**
     * Gets the name of the command.
     *
     * @return The name of the command.
     */
    String getName();

    /**
     * Gets the abbreviated name of the command.
     *
     * @return The abbreviated  name of the command.
     */
    String getAbbreviatedName();

    /**
     * Gets the short description of the command.
     *
     * @param locale The locale of the short description to return.
     *
     * @return The short description of the command.
     *
     * @throws NullPointerException if {@code locale} is {@code null}.
     */
    String getShortDescription( Locale locale ) throws NullPointerException;

    /**
     * Gets the long description of the command.
     *
     * @param locale The locale of the long description to return.
     *
     * @return The long description of the command.
     *
     * @throws NullPointerException if {@code locale} is {@code null}.
     */
    String getLongDescription( Locale locale ) throws NullPointerException;

    /**
     * Gets the options of the command.
     *
     * @return The options of the command.
     */
    Options getOptions();

    /**
     * Executes the command.
     *
     * @param commandLine Command line to execute.
     *
     * @return The status code of the command.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     *
     * @see #STATUS_SUCCESS
     * @see #STATUS_FAILURE
     */
    int execute( CommandLine commandLine ) throws NullPointerException;

    // SECTION-END
}
