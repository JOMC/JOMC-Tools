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
package org.jomc.cli.model;

import java.io.File;
import org.apache.commons.cli.Option;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 *
 * Creates {@code Option} objects describing a single command-line option by maintaining information regarding the
 * short-name, the long-name, a flag indicating if an argument is required, and a self-documenting description.
 *
 * <p><b>Specifications</b><ul>
 * <li>{@code org.apache.commons.cli.Option} {@code Multiton}</li>
 * </ul></p>
 * <p><b>Properties</b><ul>
 * <li>"{@link #getLongOpt longOpt}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Long name of the option.</p>
 * </blockquote></li>
 * <li>"{@link #getNumberOfArgs numberOfArgs}"
 * <blockquote>Property of type {@code int}.
 * <p>Number of argument values the option can take.</p>
 * </blockquote></li>
 * <li>"{@link #getOpt opt}"
 * <blockquote>Property of type {@code java.lang.String}.
 * <p>Name of the option.</p>
 * </blockquote></li>
 * <li>"{@link #isOptionalArg optionalArg}"
 * <blockquote>Property of type {@code boolean}.
 * <p>Query to see if the option can have an optional argument.</p>
 * </blockquote></li>
 * <li>"{@link #isRequired required}"
 * <blockquote>Property of type {@code boolean}.
 * <p>Query to see if the option is required.</p>
 * </blockquote></li>
 * <li>"{@link #getValueSeparator valueSeparator}"
 * <blockquote>Property of type {@code char}.
 * <p>Value separator of the option.</p>
 * </blockquote></li>
 * </ul></p>
 * <p><b>Dependencies</b><ul>
 * <li>"{@link #getLocale Locale}"<blockquote>
 * Dependency on {@code java.util.Locale} at specification level 1.1 bound to an instance.</blockquote></li>
 * </ul></p>
 * <p><b>Messages</b><ul>
 * <li>"{@link #getArgumentDescriptionMessage argumentDescription}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * <li>"{@link #getDescriptionMessage description}"<table>
 * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
 * </table>
 * </ul></p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                             comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
// </editor-fold>
// SECTION-END
public class OptionFactory
{
    // SECTION-START[Option]
    // SECTION-END
    // SECTION-START[OptionFactory]

    public Option getObject()
    {
        final char valueSeparator = this.getValueSeparator() == ':' ? File.pathSeparatorChar : this.getValueSeparator();

        final Option option = new Option( this.getOpt(), this.getDescriptionMessage(
            this.getLocale(), Character.toString( valueSeparator ) ) );

        option.setArgs( this.getNumberOfArgs() );
        option.setLongOpt( this.getLongOpt() );
        option.setOptionalArg( this.isOptionalArg() );
        option.setRequired( this.isRequired() );

        if ( ( option.getArgs() > 0 || option.getArgs() == Option.UNLIMITED_VALUES ) &&
             this.getArgumentDescriptionMessage( this.getLocale() ).trim().length() > 0 )
        {
            option.setArgName( this.getArgumentDescriptionMessage( this.getLocale() ) );
        }

        if ( option.getArgs() > 1 || option.getArgs() == Option.UNLIMITED_VALUES )
        {
            option.setValueSeparator( valueSeparator );
        }

        return option;
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code OptionFactory} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    public OptionFactory()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Dependencies]
    // <editor-fold defaultstate="collapsed" desc=" Generated Dependencies ">

    /**
     * Gets the {@code Locale} dependency.
     * <p>This method returns the "{@code default}" object of the {@code java.util.Locale} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private java.util.Locale getLocale()
    {
        final java.util.Locale _d = (java.util.Locale) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "Locale" );
        assert _d != null : "'Locale' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">

    /**
     * Gets the value of the {@code longOpt} property.
     * @return Long name of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private java.lang.String getLongOpt()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "longOpt" );
        assert _p != null : "'longOpt' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code numberOfArgs} property.
     * @return Number of argument values the option can take.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private int getNumberOfArgs()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "numberOfArgs" );
        assert _p != null : "'numberOfArgs' property not found.";
        return _p.intValue();
    }

    /**
     * Gets the value of the {@code opt} property.
     * @return Name of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private java.lang.String getOpt()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "opt" );
        assert _p != null : "'opt' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code optionalArg} property.
     * @return Query to see if the option can have an optional argument.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private boolean isOptionalArg()
    {
        final java.lang.Boolean _p = (java.lang.Boolean) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "optionalArg" );
        assert _p != null : "'optionalArg' property not found.";
        return _p.booleanValue();
    }

    /**
     * Gets the value of the {@code required} property.
     * @return Query to see if the option is required.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private boolean isRequired()
    {
        final java.lang.Boolean _p = (java.lang.Boolean) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "required" );
        assert _p != null : "'required' property not found.";
        return _p.booleanValue();
    }

    /**
     * Gets the value of the {@code valueSeparator} property.
     * @return Value separator of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private char getValueSeparator()
    {
        final java.lang.Character _p = (java.lang.Character) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "valueSeparator" );
        assert _p != null : "'valueSeparator' property not found.";
        return _p.charValue();
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code argumentDescription} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @return Display name for the argument value of the option.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getArgumentDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "argumentDescription", locale );
        assert _m != null : "'argumentDescription' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code description} message.
     * <p><b>Templates</b><br/><table>
     * <tr><td valign="top">English:</td><td valign="top"><pre></pre></td></tr>
     * </table></p>
     * @param locale The locale of the message to return.
     * @param valueSeparator Format argument.
     * @return Display description of the option.
     *
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor",
                                 comments = "See http://jomc.sourceforge.net/jomc/1.0-alpha-18-SNAPSHOT/jomc-tools" )
    private String getDescriptionMessage( final java.util.Locale locale, final java.lang.String valueSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "description", locale, valueSeparator );
        assert _m != null : "'description' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
