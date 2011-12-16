// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
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
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC CLI Command</dd>
 *   <dt><b>Multiplicity:</b></dt><dd>Many</dd>
 *   <dt><b>Scope:</b></dt><dd>None</dd>
 * </dl>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version 1.0
 * @see org.jomc.ObjectManager#getObject(java.lang.Class) getObject(Command[].class)
 * @see org.jomc.ObjectManager#getObject(java.lang.Class,java.lang.String) getObject(Command.class, "<i>implementation name</i>")
 * @see org.jomc.ObjectManagerFactory
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public interface Command
{
    // SECTION-START[Command]

    /** Listener interface. */
    public interface Listener
    {

        /**
         * Gets called on logging.
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
     * @return The status code to report.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     *
     * @see #STATUS_SUCCESS
     * @see #STATUS_FAILURE
     */
    int execute( CommandLine commandLine ) throws NullPointerException;

    // SECTION-END
}
