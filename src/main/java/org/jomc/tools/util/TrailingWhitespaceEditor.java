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

/**
 * {@code LineEditor} removing trailing whitespace.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public class TrailingWhitespaceEditor extends LineEditor
{

    /** Creates a new {@code TrailingWhitespaceEditor} instance. */
    public TrailingWhitespaceEditor()
    {
        super();
    }

    /**
     * Creates a new {@code TrailingWhitespaceEditor} instance taking an editor to chain.
     *
     * @param editor The editor to chain.
     */
    public TrailingWhitespaceEditor( final LineEditor editor )
    {
        super( editor );
    }

    @Override
    public String getNextLine( String line )
    {
        if ( line != null )
        {
            StringBuffer whitespace = null;
            boolean sawWhitespace = false;
            final StringBuffer replacement = new StringBuffer( line.length() );
            final char[] chars = line.toCharArray();

            for ( int i = 0; i < chars.length; i++ )
            {
                if ( Character.isWhitespace( chars[i] ) )
                {
                    if ( whitespace == null )
                    {
                        whitespace = new StringBuffer();
                    }

                    whitespace.append( chars[i] );
                    sawWhitespace = true;
                }
                else
                {
                    if ( sawWhitespace )
                    {
                        replacement.append( whitespace );
                        sawWhitespace = false;
                        whitespace = null;
                    }
                    replacement.append( chars[i] );
                }
            }

            if ( !replacement.toString().equals( line ) )
            {
                this.setInputModified( true );
                line = replacement.toString();
            }
        }

        return line;
    }

}
