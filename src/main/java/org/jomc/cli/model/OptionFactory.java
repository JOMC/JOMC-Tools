// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (C) 2009 - 2011 The JOMC Project
 *   Copyright (C) 2005 - 2011 Christian Schulte <schulte2005@users.sourceforge.net>
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
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="3" nowrap><font size="+2">Properties</font></th>
 *     </tr>
 *     <tr class="TableSubHeadingColor">
 *       <td align="left" scope="col" nowrap><b>Name</b></td>
 *       <td align="left" scope="col" nowrap><b>Type</b></td>
 *       <td align="left" scope="col" nowrap><b>Documentation</b></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLongOpt longOpt}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Long name of the option.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getNumberOfArgs numberOfArgs}</td>
 *       <td align="left" valign="top" nowrap>{@code int}</td>
 *       <td align="left" valign="top">Number of argument values the option can take.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getOpt opt}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Name of the option.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #isOptionalArg optionalArg}</td>
 *       <td align="left" valign="top" nowrap>{@code boolean}</td>
 *       <td align="left" valign="top">Query to see if the option can have an optional argument.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #isRequired required}</td>
 *       <td align="left" valign="top" nowrap>{@code boolean}</td>
 *       <td align="left" valign="top">Query to see if the option is required.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getValueSeparator valueSeparator}</td>
 *       <td align="left" valign="top" nowrap>{@code char}</td>
 *       <td align="left" valign="top">Value separator of the option.</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="2" nowrap><font size="+2">Dependencies</font></th>
 *     </tr>
 *     <tr class="TableSubHeadingColor">
 *       <td align="left" scope="col" nowrap><b>Name</b></td>
 *       <td align="left" scope="col" nowrap><b>Description</b></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLocale Locale}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1 bound to an instance.</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="3" nowrap><font size="+2">Messages</font></th>
 *     </tr>
 *     <tr class="TableSubHeadingColor">
 *       <td align="left" scope="col" nowrap><b>Name</b></td>
 *       <td align="left" scope="col" nowrap><b>Languages</b></td>
 *       <td align="left" scope="col" nowrap><b>Default Templates</b></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getArgumentDescription argumentDescription}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDescription description}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
 *     </tr>
 *   </table>
 * </p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.0
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public final class OptionFactory
{
    // SECTION-START[OptionFactory]

    public Option getObject()
    {
        final char valueSeparator = this.getValueSeparator() == ':' ? File.pathSeparatorChar : this.getValueSeparator();

        final Option option =
            new Option( this.getOpt(), this.getDescription( this.getLocale(), Character.toString( valueSeparator ) ) );

        option.setArgs( this.getNumberOfArgs() );
        option.setLongOpt( this.getLongOpt() );
        option.setOptionalArg( this.isOptionalArg() );
        option.setRequired( this.isRequired() );

        if ( ( option.getArgs() > 0 || option.getArgs() == Option.UNLIMITED_VALUES )
             && this.getArgumentDescription( this.getLocale() ).trim().length() > 0 )
        {
            option.setArgName( this.getArgumentDescription( this.getLocale() ) );
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
     * <p>This method returns the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code Locale} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
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
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return Display name for the argument value of the option.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getArgumentDescription( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "argumentDescription", locale );
        assert _m != null : "'argumentDescription' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code description} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param valueSeparator Format argument.
     * @return Display description of the option.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDescription( final java.util.Locale locale, final java.lang.String valueSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "description", locale, valueSeparator );
        assert _m != null : "'description' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
