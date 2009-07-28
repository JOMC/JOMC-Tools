// SECTION-START[License Header]
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
// SECTION-END
package org.jomc.tools.cli;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

// SECTION-START[Implementation Comment]
/**
 * JOMC command line interface.
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getCommands Commands}"<blockquote>
 * Dependency on {@code org.jomc.tools.cli.Command} at specification level 1.0-alpha-1-SNAPSHOT applying to Multiton scope.</blockquote></li>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 applying to Multiton scope bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getIllegalArgumentsMessage illegalArguments}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Illegal arguments. Try ''jomc {0} help''.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ungültige Argumente. Versuchen Sie ''jomc {0} help''.</pre></td></tr>
 * </table>
 * <li>"{@link #getUsageMessage usage}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre>Type ''jomc <command> help'' for further information.</pre></td></tr>
 * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Geben Sie ''jomc <Befehl> help'' für weitere Hilfe ein.</pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// SECTION-END
// SECTION-START[Annotations]
@javax.annotation.Generated
(
    value = "org.jomc.tools.JavaSources",
    comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
)
// SECTION-END
public class Jomc
{
    // SECTION-START[Jomc]

    /** Constant for a status code indicating success. */
    private static final int STATUS_SUCCESS = 0;

    /** Constant for a status code indicating failure. */
    private static final int STATUS_FAILURE = 1;

    /** Print stream of the instance. */
    private PrintStream printStream;

    /**
     * Gets the print stream of the instance.
     *
     * @return The print stream of the instance.
     */
    public PrintStream getPrintStream()
    {
        if ( this.printStream == null )
        {
            this.printStream = System.out;
        }

        return this.printStream;
    }

    /**
     * Sets the print stream of the instance.
     *
     * @param value The new print stream of the instance.
     */
    public void setPrintStream( final PrintStream value )
    {
        this.printStream = value;
    }

    /**
     * Processes the given arguments and executes the corresponding command.
     *
     * @param args Arguments to process.
     *
     * @return Status code.
     */
    public int jomc( final String[] args )
    {
        Command cmd = null;

        try
        {
            final StringBuffer commandInfo = new StringBuffer();

            for ( Command c : this.getCommands() )
            {
                if ( cmd == null && args != null && args.length > 0 && args[0].equals( c.getName() ) )
                {
                    cmd = c;
                }

                commandInfo.append( c.getName() ).append( '\t' ).append( c.getDescription( this.getLocale() ) ).
                    append( System.getProperty( "line.separator" ) );

            }

            if ( cmd == null )
            {
                this.getPrintStream().println( this.getUsageMessage( this.getLocale() ) );
                this.getPrintStream().println();
                this.getPrintStream().println( commandInfo.toString() );
                return STATUS_FAILURE;
            }

            final String[] commandArguments = new String[ args.length - 1 ];
            System.arraycopy( args, 1, commandArguments, 0, commandArguments.length );

            if ( commandArguments.length > 0 && "help".equals( commandArguments[0] ) )
            {
                final StringWriter usage = new StringWriter();
                final StringWriter opts = new StringWriter();
                final HelpFormatter formatter = new HelpFormatter();

                PrintWriter pw = new PrintWriter( usage );
                formatter.printUsage( pw, 72, cmd.getName(), cmd.getOptions() );
                pw.close();

                pw = new PrintWriter( opts );
                formatter.printOptions( pw, 72, cmd.getOptions(), 5, 5 );
                pw.close();

                this.getPrintStream().println( usage.toString() );
                this.getPrintStream().println( opts.toString() );
                return STATUS_SUCCESS;
            }

            final CommandLineParser parser = new GnuParser();
            final CommandLine commandLine = parser.parse( cmd.getOptions(), commandArguments );
            return cmd.execute( this.getPrintStream(), commandLine );
        }
        catch ( ParseException e )
        {
            this.getPrintStream().println( e.getMessage() );
            this.getPrintStream().println( this.getIllegalArgumentsMessage( this.getLocale(), cmd.getName() ) );
            return STATUS_FAILURE;
        }
        catch ( Throwable t )
        {
            t.printStackTrace( this.getPrintStream() );
            return STATUS_FAILURE;
        }
    }

    /**
     * Main entry point.
     *
     * @param args The command arguments.
     */
    public static void main( final String[] args )
    {
        System.exit( new Jomc().jomc( args ) );
    }

    // SECTION-END
    // SECTION-START[Constructors]

    /** Default implementation constructor. */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    public Jomc()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // SECTION-END
    // SECTION-START[Dependencies]

    /**
     * Gets the {@code Commands} dependency.
     * <p>This method returns any available object of the {@code org.jomc.tools.cli.Command} specification at specification level 1.0-alpha-1-SNAPSHOT.</p>
     * @return The {@code Commands} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private org.jomc.tools.cli.Command[] getCommands() throws org.jomc.ObjectManagementException
    {
        return (org.jomc.tools.cli.Command[]) org.jomc.ObjectManagerFactory.getObjectManager().getDependency( this, "Commands" );
    }

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private java.util.Locale getLocale() throws org.jomc.ObjectManagementException
    {
        return (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager().getDependency( this, "Locale" );
    }
    // SECTION-END
    // SECTION-START[Properties]
    // SECTION-END
    // SECTION-START[Messages]

    /**
     * Gets the text of the {@code illegalArguments} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Illegal arguments. Try ''jomc {0} help''.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Ungültige Argumente. Versuchen Sie ''jomc {0} help''.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param command Format argument.
     * @return The text of the {@code illegalArguments} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getIllegalArgumentsMessage( final java.util.Locale locale, final java.lang.String command ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "illegalArguments", locale, new Object[] { command, null } );
    }

    /**
     * Gets the text of the {@code usage} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre>Type ''jomc <command> help'' for further information.</pre></td></tr>
     * <tr><td valign="top">Deutsch:</td><td valign="top"><pre>Geben Sie ''jomc <Befehl> help'' für weitere Hilfe ein.</pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return The text of the {@code usage} message.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated
    (
        value = "org.jomc.tools.JavaSources",
        comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-1-SNAPSHOT/jomc-tools"
    )
    private String getUsageMessage( final java.util.Locale locale ) throws org.jomc.ObjectManagementException
    {
        return org.jomc.ObjectManagerFactory.getObjectManager().getMessage( this, "usage", locale,  null );
    }
    // SECTION-END
}
