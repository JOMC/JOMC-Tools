/*
 *  JOMC Tools
 *  Copyright (c) 2005 Christian Schulte <cs@schulte.it>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.jomc.tools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Interface to line based editing.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public class LineEditor
{

    /** Flag indicating that this editor changed its input. */
    private boolean inputModified;

    /** Editor to chain. */
    private LineEditor editor;

    /** Creates a new {@code LineEditor} instance. */
    public LineEditor()
    {
        this( null );
    }

    /**
     * Creates a new {@code LineEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public LineEditor( final LineEditor editor )
    {
        super();
        this.editor = editor;
    }

    /**
     * Edits strings.
     * <p>This method splits the given string into lines and passes every line to method {@code getNextLine} in order of
     * occurence in the given string.</p>
     *
     * @param string The string to edit.
     *
     * @return The edited string.
     */
    public final String edit( String string )
    {
        try
        {
            final BufferedReader reader = new BufferedReader( new StringReader( string ) );
            final StringBuffer edited = new StringBuffer();

            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                final String replacement = this.getNextLine( line );
                if ( replacement != null )
                {
                    edited.append( replacement ).append( "\n" );
                }
            }

            final String replacement = this.getNextLine( line );
            if ( replacement != null )
            {
                edited.append( replacement );
            }

            string = edited.toString();

            if ( this.editor != null )
            {
                final String chained = this.editor.edit( string );
                if ( !string.equals( chained ) )
                {
                    this.inputModified = true;
                    string = chained;
                }
            }

            return string;
        }
        catch ( IOException e )
        {
            throw new AssertionError( e );
        }
    }

    /**
     * Processes the next line of input.
     *
     * @param line The next line of input or {@code null}, if the end of the input has been reached.
     *
     * @return The string to replace {@code line} with, or {@code null} to replace {@code line} with nothing.
     */
    public String getNextLine( final String line )
    {
        return line;
    }

    /**
     * Flag indicating that this editor changed its input.
     *
     * @return {@code true} if this editor changed its input; {@code false} if not.
     */
    public final boolean isInputModified()
    {
        return this.inputModified;
    }

    /**
     * Sets the flag indicating that this editor changed its input.
     *
     * @param value {@code true} if this editor changed its input; {@code false} if not.
     */
    public final void setInputModified( final boolean value )
    {
        this.inputModified = value;
    }

}
