// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 * Java Object Management and Configuration
 * Copyright (C) Christian Schulte <cs@schulte.it>, 2009-206
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $JOMC$
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
 * Creates {@code Option} objects describing a single command-line option by maintaining information regarding the short-name, the long-name, a flag indicating if an argument is required, and a self-documenting description.
 *
 * <dl>
 *   <dt><b>Identifier:</b></dt><dd>JOMC ⁑ CLI ⁑ Option Factory</dd>
 *   <dt><b>Name:</b></dt><dd>JOMC ⁑ CLI ⁑ Option Factory</dd>
 *   <dt><b>Abstract:</b></dt><dd>Yes</dd>
 *   <dt><b>Final:</b></dt><dd>No</dd>
 *   <dt><b>Stateless:</b></dt><dd>No</dd>
 * </dl>
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a> 1.0
 * @version 1.10-SNAPSHOT
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
// </editor-fold>
// SECTION-END
public final class OptionFactory
{
    // SECTION-START[OptionFactory]

    public Option getObject()
    {
        final char valueSeparator = this.getValueSeparator() == ':'
                                        ? File.pathSeparatorChar
                                        : this.getValueSeparator();

        final Option option = new Option( this.getOpt(), this.getDescription( this.getLocale(),
                                                                              Character.toString( valueSeparator ) ) );

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
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
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
     * Gets the {@code <Locale>} dependency.
     * <p>
     *   This method returns the {@code <default>} object of the {@code <java.util.Locale>} specification at specification level 1.1.
     *   That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.
     * </p>
     * <dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl>
     * @return The {@code <Locale>} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
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
     * Gets the value of the {@code <Long Opt>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Long name of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private java.lang.String getLongOpt()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Long Opt" );
        assert _p != null : "'Long Opt' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Number Of Args>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Number of argument values the option can take.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private int getNumberOfArgs()
    {
        final java.lang.Integer _p = (java.lang.Integer) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Number Of Args" );
        assert _p != null : "'Number Of Args' property not found.";
        return _p.intValue();
    }
    /**
     * Gets the value of the {@code <Opt>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Name of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private java.lang.String getOpt()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Opt" );
        assert _p != null : "'Opt' property not found.";
        return _p;
    }
    /**
     * Gets the value of the {@code <Optional Arg>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Query to see if the option can have an optional argument.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private boolean isOptionalArg()
    {
        final java.lang.Boolean _p = (java.lang.Boolean) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Optional Arg" );
        assert _p != null : "'Optional Arg' property not found.";
        return _p.booleanValue();
    }
    /**
     * Gets the value of the {@code <Required>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Query to see if the option is required.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private boolean isRequired()
    {
        final java.lang.Boolean _p = (java.lang.Boolean) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Required" );
        assert _p != null : "'Required' property not found.";
        return _p.booleanValue();
    }
    /**
     * Gets the value of the {@code <Value Separator>} property.
     * <p><dl>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @return Value separator of the option.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private char getValueSeparator()
    {
        final java.lang.Character _p = (java.lang.Character) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "Value Separator" );
        assert _p != null : "'Value Separator' property not found.";
        return _p.charValue();
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">
    /**
     * Gets the text of the {@code <Argument Description>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @return Display name for the argument value of the option.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private String getArgumentDescription( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Argument Description", locale );
        assert _m != null : "'Argument Description' message not found.";
        return _m;
    }
    /**
     * Gets the text of the {@code <Description>} message.
     * <p><dl>
     *   <dt><b>Languages:</b></dt>
     *     <dd>English (default)</dd>
     *   <dt><b>Final:</b></dt><dd>No</dd>
     * </dl></p>
     * @param locale The locale of the message to return.
     * @param valueSeparator Format argument.
     * @return Display description of the option.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @SuppressWarnings({"unchecked", "unused", "PMD.UnnecessaryFullyQualifiedName"})
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.9", comments = "See http://www.jomc.org/jomc/1.9/jomc-tools-1.9" )
    private String getDescription( final java.util.Locale locale, final java.lang.String valueSeparator )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "Description", locale, valueSeparator );
        assert _m != null : "'Description' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END

}
