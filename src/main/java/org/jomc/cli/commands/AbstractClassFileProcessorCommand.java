// SECTION-START[License Header]
// <editor-fold defaultstate="collapsed" desc=" Generated License ">
/*
 *   Copyright (c) 2009 - 2011 The JOMC Project
 *   Copyright (c) 2005 - 2011 Christian Schulte <schulte2005@users.sourceforge.net>
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
package org.jomc.cli.commands;

import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.jomc.tools.ClassFileProcessor;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC CLI {@code ClassFileProcessor} based command implementation.
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Specifications</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Identifier</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Class</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Scope</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Version</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@code JOMC CLI Command}</td>
 *       <td align="left" nowrap>{@code org.jomc.cli.Command}</td>
 *       <td align="left" nowrap>{@code Multiton}</td>
 *       <td align="left" nowrap>{@code 1.0}</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Properties</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Type</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Documentation</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getAbbreviatedCommandName abbreviatedCommandName}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Abbreviated name of the command.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getApplicationModlet applicationModlet}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Name of the 'shaded' application modlet.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getCommandName commandName}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Name of the command.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getModletExcludes modletExcludes}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getProviderExcludes providerExcludes}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getSchemaExcludes schemaExcludes}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" nowrap>{@link #getServiceExcludes serviceExcludes}</td>
 *       <td align="left" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Dependencies</caption>
 *       <tr>
 *         <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *         <th align="left" class="TableHeader" scope="col" nowrap>Description</th>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getClassFileProcessorOption ClassFileProcessorOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getClasspathOption ClasspathOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getCountryOption CountryOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getDefaultTemplateProfileOption DefaultTemplateProfileOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getDocumentsOption DocumentsOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getImplementationOption ImplementationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getIndentationStringOption IndentationStringOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getInputEncodingOption InputEncodingOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getLanguageOption LanguageOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getLineSeparatorOption LineSeparatorOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getLocale Locale}</td>
 *         <td align="left">Dependency on {@code 'java.util.Locale'} {@code (java.util.Locale)} at specification level 1.1 bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getLocaleVariantOption LocaleVariantOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModelContextOption ModelContextOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModelOption ModelOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModletLocationOption ModletLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModletSchemaSystemIdOption ModletSchemaSystemIdOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModuleLocationOption ModuleLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getModuleNameOption ModuleNameOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getNoClassProcessingOption NoClassProcessingOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getNoClasspathResolutionOption NoClasspathResolutionOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getNoModelProcessingOption NoModelProcessingOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getOutputEncodingOption OutputEncodingOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getProviderLocationOption ProviderLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getSpecificationOption SpecificationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getTemplateEncodingOption TemplateEncodingOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getTemplateLocationOption TemplateLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getTemplateProfileOption TemplateProfileOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *       <tr class="TableRowColor">
 *         <td align="left" nowrap>{@link #getTransformerLocationOption TransformerLocationOption}</td>
 *         <td align="left">Dependency on {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} bound to an instance.</td>
 *       </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <caption class="TableCaption">Messages</caption>
 *     <tr>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Name</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Languages</th>
 *       <th align="left" class="TableHeader" scope="col" nowrap>Default Template</th>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getApplicationTitle applicationTitle}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code>JOMC CLI Version 1.2-SNAPSHOT Build 2011-01-21T10:56:30+0100</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCannotProcessMessage cannotProcessMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Cannot process ''{0}'': {1}</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getClasspathElementInfo classpathElementInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Classpath element: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getClasspathElementNotFoundWarning classpathElementNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Classpath element ''{0}'' ignored. File not found.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandFailureMessage commandFailureMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>{0} failure.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandInfoMessage commandInfoMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Executing command {0} ...</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandSuccessMessage commandSuccessMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>{0} successful.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDefaultLogLevelInfo defaultLogLevelInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Default log level: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDisabledMessage disabledMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Class file processing disabled.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentFileInfo documentFileInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Document file: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentFileNotFoundWarning documentFileNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Document file ''{0}'' ignored. File not found.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedModletInfo excludedModletInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Modlet ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedModuleFromClasspathInfo excludedModuleFromClasspathInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Module ''{0}'' from class path ignored. Module with identical name already loaded.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedProviderInfo excludedProviderInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Provider ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedSchemaInfo excludedSchemaInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Context ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedServiceInfo excludedServiceInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Service ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getImplementationNotFoundWarning implementationNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Implementation ''{0}'' not found.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getInvalidModelMessage invalidModelMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Invalid ''{0}'' model.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLongDescriptionMessage longDescriptionMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleInfo moduleInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Found module ''{0} {1}''.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleNotFoundWarning moduleNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Module ''{0}'' not found.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getSeparator separator}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code>--------------------------------------------------------------------------------</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getShortDescriptionMessage shortDescriptionMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getSpecificationNotFoundWarning specificationNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Specification ''{0}'' not found.</code></pre></td>
 *     </tr>
 *   </table>
 * </p>
 *
 * @author <a href="mailto:schulte2005@users.sourceforge.net">Christian Schulte</a> 1.2
 * @version $Id$
 */
// </editor-fold>
// SECTION-END
// SECTION-START[Annotations]
// <editor-fold defaultstate="collapsed" desc=" Generated Annotations ">
@javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
// </editor-fold>
// SECTION-END
public abstract class AbstractClassFileProcessorCommand extends AbstractJomcToolCommand
{
    // SECTION-START[Command]
    // SECTION-END
    // SECTION-START[AbstractClassFileProcessorCommand]

    /**
     * Creates a new {@code ClassFileProcessor} instance taking a command line.
     *
     * @param commandLine The command line to process.
     *
     * @return A new {@code ClassFileProcessor} instance as specified by the given command line.
     *
     * @throws NullPointerException if {@code commandLine} is {@code null}.
     * @throws CommandExecutionException if creating a new instance fails.
     */
    protected ClassFileProcessor createClassFileProcessor( final CommandLine commandLine )
        throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        final String className = commandLine.hasOption( this.getClassFileProcessorOption().getOpt() )
                                 ? commandLine.getOptionValue( this.getClassFileProcessorOption().getOpt() )
                                 : ClassFileProcessor.class.getName();

        final ClassFileProcessor tool = this.createJomcTool( className, ClassFileProcessor.class, commandLine );
        return tool;
    }

    /** {@inheritDoc} */
    protected final void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        if ( commandLine.hasOption( this.getNoClassProcessingOption().getOpt() ) )
        {
            this.log( Level.INFO, this.getDisabledMessage( this.getLocale() ), null );
        }
        else
        {
            this.processClassFiles( commandLine );
        }
    }

    /**
     * Processes class files.
     *
     * @param commandLine The command line to execute.
     *
     * @throws CommandExecutionException if processing class files fails.
     */
    protected abstract void processClassFiles( final CommandLine commandLine ) throws CommandExecutionException;

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code AbstractClassFileProcessorCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    public AbstractClassFileProcessorCommand()
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
     * Gets the {@code ClassFileProcessorOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI ClassFileProcessor Class Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ClassFileProcessorOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getClassFileProcessorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClassFileProcessorOption" );
        assert _d != null : "'ClassFileProcessorOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ClasspathOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Classpath Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ClasspathOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getClasspathOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ClasspathOption" );
        assert _d != null : "'ClasspathOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code CountryOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Country Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code CountryOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getCountryOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "CountryOption" );
        assert _d != null : "'CountryOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DefaultTemplateProfileOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Default Template Profile Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DefaultTemplateProfileOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getDefaultTemplateProfileOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DefaultTemplateProfileOption" );
        assert _d != null : "'DefaultTemplateProfileOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentsOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Documents Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DocumentsOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentsOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentsOption" );
        assert _d != null : "'DocumentsOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ImplementationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Implementation Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ImplementationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getImplementationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ImplementationOption" );
        assert _d != null : "'ImplementationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code IndentationStringOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Indentation String Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code IndentationStringOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getIndentationStringOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "IndentationStringOption" );
        assert _d != null : "'IndentationStringOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code InputEncodingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Input Encoding Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code InputEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getInputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "InputEncodingOption" );
        assert _d != null : "'InputEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code LanguageOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Language Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code LanguageOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getLanguageOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "LanguageOption" );
        assert _d != null : "'LanguageOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code LineSeparatorOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Line Separator Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code LineSeparatorOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getLineSeparatorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "LineSeparatorOption" );
        assert _d != null : "'LineSeparatorOption' dependency not found.";
        return _d;
    }

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

    /**
     * Gets the {@code LocaleVariantOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Locale Variant Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code LocaleVariantOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getLocaleVariantOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "LocaleVariantOption" );
        assert _d != null : "'LocaleVariantOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModelContextOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI ModelContext Class Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModelContextOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelContextOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModelContextOption" );
        assert _d != null : "'ModelContextOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModelOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Model Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModelOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModelOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModelOption" );
        assert _d != null : "'ModelOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModletLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Modlet Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModletLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletLocationOption" );
        assert _d != null : "'ModletLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModletSchemaSystemIdOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Modlet Schema System Id Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModletSchemaSystemIdOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModletSchemaSystemIdOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModletSchemaSystemIdOption" );
        assert _d != null : "'ModletSchemaSystemIdOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleLocationOption" );
        assert _d != null : "'ModuleLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleNameOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleNameOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleNameOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleNameOption" );
        assert _d != null : "'ModuleNameOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClassProcessingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Class File Processing Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoClassProcessingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoClassProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoClassProcessingOption" );
        assert _d != null : "'NoClassProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClasspathResolutionOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Classpath Resolution Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoClasspathResolutionOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoClasspathResolutionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoClasspathResolutionOption" );
        assert _d != null : "'NoClasspathResolutionOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoModelProcessingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Model Processing Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code NoModelProcessingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getNoModelProcessingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "NoModelProcessingOption" );
        assert _d != null : "'NoModelProcessingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code OutputEncodingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Output Encoding Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code OutputEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getOutputEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "OutputEncodingOption" );
        assert _d != null : "'OutputEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Platform Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code PlatformProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getPlatformProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "PlatformProviderLocationOption" );
        assert _d != null : "'PlatformProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ProviderLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getProviderLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ProviderLocationOption" );
        assert _d != null : "'ProviderLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code SpecificationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Specification Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code SpecificationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getSpecificationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "SpecificationOption" );
        assert _d != null : "'SpecificationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TemplateEncodingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Template Encoding Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TemplateEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getTemplateEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TemplateEncodingOption" );
        assert _d != null : "'TemplateEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TemplateLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Template Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TemplateLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getTemplateLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TemplateLocationOption" );
        assert _d != null : "'TemplateLocationOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TemplateProfileOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Template Profile Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TemplateProfileOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getTemplateProfileOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TemplateProfileOption" );
        assert _d != null : "'TemplateProfileOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Transformer Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code TransformerLocationOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getTransformerLocationOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "TransformerLocationOption" );
        assert _d != null : "'TransformerLocationOption' dependency not found.";
        return _d;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Properties]
    // <editor-fold defaultstate="collapsed" desc=" Generated Properties ">

    /**
     * Gets the value of the {@code abbreviatedCommandName} property.
     * @return Abbreviated name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getAbbreviatedCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "abbreviatedCommandName" );
        assert _p != null : "'abbreviatedCommandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code applicationModlet} property.
     * @return Name of the 'shaded' application modlet.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getApplicationModlet()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "applicationModlet" );
        assert _p != null : "'applicationModlet' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code commandName} property.
     * @return Name of the command.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getCommandName()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "commandName" );
        assert _p != null : "'commandName' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code modletExcludes} property.
     * @return List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getModletExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "modletExcludes" );
        assert _p != null : "'modletExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code providerExcludes} property.
     * @return List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getProviderExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "providerExcludes" );
        assert _p != null : "'providerExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code schemaExcludes} property.
     * @return List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getSchemaExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "schemaExcludes" );
        assert _p != null : "'schemaExcludes' property not found.";
        return _p;
    }

    /**
     * Gets the value of the {@code serviceExcludes} property.
     * @return List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.
     * @throws org.jomc.ObjectManagementException if getting the property instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private java.lang.String getServiceExcludes()
    {
        final java.lang.String _p = (java.lang.String) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getProperty( this, "serviceExcludes" );
        assert _p != null : "'serviceExcludes' property not found.";
        return _p;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Messages]
    // <editor-fold defaultstate="collapsed" desc=" Generated Messages ">

    /**
     * Gets the text of the {@code applicationTitle} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>JOMC CLI Version 1.2-SNAPSHOT Build 2011-01-21T10:56:30+0100</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code applicationTitle} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getApplicationTitle( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "applicationTitle", locale );
        assert _m != null : "'applicationTitle' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code cannotProcessMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Cannot process ''{0}'': {1}</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Kann ''{0}'' nicht verarbeiten: {1}</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param itemInfo Format argument.
     * @param detailMessage Format argument.
     * @return The text of the {@code cannotProcessMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getCannotProcessMessage( final java.util.Locale locale, final java.lang.String itemInfo, final java.lang.String detailMessage )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "cannotProcessMessage", locale, itemInfo, detailMessage );
        assert _m != null : "'cannotProcessMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Classpath element: ''{0}''</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Klassenpfad-Element: ''{0}''</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param classpathElement Format argument.
     * @return The text of the {@code classpathElementInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getClasspathElementInfo( final java.util.Locale locale, final java.lang.String classpathElement )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementInfo", locale, classpathElement );
        assert _m != null : "'classpathElementInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code classpathElementNotFoundWarning} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Classpath element ''{0}'' ignored. File not found.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code classpathElementNotFoundWarning} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getClasspathElementNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "classpathElementNotFoundWarning", locale, fileName );
        assert _m != null : "'classpathElementNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandFailureMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0} failure.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0} fehlgeschlagen.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandFailureMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getCommandFailureMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandFailureMessage", locale, toolName );
        assert _m != null : "'commandFailureMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandInfoMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Executing command {0} ...</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>F&uuml;hrt Befehl {0} aus ... </code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandInfoMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getCommandInfoMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandInfoMessage", locale, toolName );
        assert _m != null : "'commandInfoMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code commandSuccessMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0} successful.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>{0} erfolgreich.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param toolName Format argument.
     * @return The text of the {@code commandSuccessMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getCommandSuccessMessage( final java.util.Locale locale, final java.lang.String toolName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "commandSuccessMessage", locale, toolName );
        assert _m != null : "'commandSuccessMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code defaultLogLevelInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Default log level: ''{0}''</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Standard-Protokollierungsstufe: ''{0}''</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param defaultLogLevel Format argument.
     * @return The text of the {@code defaultLogLevelInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDefaultLogLevelInfo( final java.util.Locale locale, final java.lang.String defaultLogLevel )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "defaultLogLevelInfo", locale, defaultLogLevel );
        assert _m != null : "'defaultLogLevelInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code disabledMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Class file processing disabled.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Klassendateiverarbeitung deaktiviert.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code disabledMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDisabledMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "disabledMessage", locale );
        assert _m != null : "'disabledMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Document file: ''{0}''</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Dokument-Datei: ''{0}''</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param documentFile Format argument.
     * @return The text of the {@code documentFileInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDocumentFileInfo( final java.util.Locale locale, final java.lang.String documentFile )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileInfo", locale, documentFile );
        assert _m != null : "'documentFileInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code documentFileNotFoundWarning} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Document file ''{0}'' ignored. File not found.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code documentFileNotFoundWarning} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getDocumentFileNotFoundWarning( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "documentFileNotFoundWarning", locale, fileName );
        assert _m != null : "'documentFileNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModletInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Modlet ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Modlet ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param modletIdentifier Format argument.
     * @return The text of the {@code excludedModletInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludedModletInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String modletIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModletInfo", locale, resourceName, modletIdentifier );
        assert _m != null : "'excludedModletInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedModuleFromClasspathInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Module ''{0}'' from class path ignored. Module with identical name already loaded.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code excludedModuleFromClasspathInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludedModuleFromClasspathInfo( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedModuleFromClasspathInfo", locale, moduleName );
        assert _m != null : "'excludedModuleFromClasspathInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedProviderInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Provider ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param providerName Format argument.
     * @return The text of the {@code excludedProviderInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludedProviderInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String providerName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedProviderInfo", locale, resourceName, providerName );
        assert _m != null : "'excludedProviderInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedSchemaInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Context ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param contextId Format argument.
     * @return The text of the {@code excludedSchemaInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludedSchemaInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String contextId )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedSchemaInfo", locale, resourceName, contextId );
        assert _m != null : "'excludedSchemaInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code excludedServiceInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Service ''{1}'' from class path resource ''{0}'' ignored.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param resourceName Format argument.
     * @param serviceName Format argument.
     * @return The text of the {@code excludedServiceInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludedServiceInfo( final java.util.Locale locale, final java.lang.String resourceName, final java.lang.String serviceName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludedServiceInfo", locale, resourceName, serviceName );
        assert _m != null : "'excludedServiceInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code implementationNotFoundWarning} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Implementation ''{0}'' not found.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Implementierung ''{0}'' nicht gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param implementationIdentifier Format argument.
     * @return The text of the {@code implementationNotFoundWarning} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getImplementationNotFoundWarning( final java.util.Locale locale, final java.lang.String implementationIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "implementationNotFoundWarning", locale, implementationIdentifier );
        assert _m != null : "'implementationNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code invalidModelMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Invalid ''{0}'' model.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Ung&uuml;ltiges ''{0}'' Modell.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param modelIdentifier Format argument.
     * @return The text of the {@code invalidModelMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getInvalidModelMessage( final java.util.Locale locale, final java.lang.String modelIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "invalidModelMessage", locale, modelIdentifier );
        assert _m != null : "'invalidModelMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code longDescriptionMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code longDescriptionMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getLongDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "longDescriptionMessage", locale );
        assert _m != null : "'longDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Found module ''{0} {1}''.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Modul ''{0} {1}'' gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @param moduleVersion Format argument.
     * @return The text of the {@code moduleInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getModuleInfo( final java.util.Locale locale, final java.lang.String moduleName, final java.lang.String moduleVersion )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleInfo", locale, moduleName, moduleVersion );
        assert _m != null : "'moduleInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code moduleNotFoundWarning} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Module ''{0}'' not found.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Modul ''{0}'' nicht gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code moduleNotFoundWarning} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getModuleNotFoundWarning( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "moduleNotFoundWarning", locale, moduleName );
        assert _m != null : "'moduleNotFoundWarning' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code separator} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>--------------------------------------------------------------------------------</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code separator} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getSeparator( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "separator", locale );
        assert _m != null : "'separator' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code shortDescriptionMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code></code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @return The text of the {@code shortDescriptionMessage} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getShortDescriptionMessage( final java.util.Locale locale )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "shortDescriptionMessage", locale );
        assert _m != null : "'shortDescriptionMessage' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code specificationNotFoundWarning} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" class="TableHeader" scope="col" nowrap>Language</th>
     *       <th align="left" class="TableHeader" scope="col" nowrap>Template</th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Specification ''{0}'' not found.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Spezifikation ''{0}'' nicht gefunden.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param specificationIdentifier Format argument.
     * @return The text of the {@code specificationNotFoundWarning} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getSpecificationNotFoundWarning( final java.util.Locale locale, final java.lang.String specificationIdentifier )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "specificationNotFoundWarning", locale, specificationIdentifier );
        assert _m != null : "'specificationNotFoundWarning' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}