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
package org.jomc.cli.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.cli.CommandLine;
import org.jomc.model.Module;
import org.jomc.model.Modules;
import org.jomc.model.ObjectFactory;
import org.jomc.model.modlet.ModelHelper;
import org.jomc.modlet.Model;
import org.jomc.modlet.ModelContext;
import org.jomc.modlet.ModelException;
import org.jomc.modlet.ModelValidationReport;

// SECTION-START[Documentation]
// <editor-fold defaultstate="collapsed" desc=" Generated Documentation ">
/**
 * JOMC CLI {@code merge-modules} command implementation.
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="2" nowrap><font size="+2">Implementation</font></th>
 *     </tr>
 *     <tr>
 *       <td class="TableSubHeadingColor" align="left" nowrap><b>Identifier:</b></td>
 *       <td class="TableRowColor" align="left" nowrap>{@code JOMC CLI merge-modules Command}</td>
 *     </tr>
 *     <tr>
 *       <td class="TableSubHeadingColor" align="left" nowrap><b>Name:</b></td>
 *       <td class="TableRowColor" align="left" nowrap>{@code default-merge-modules}</td>
 *     </tr>
 *     <tr>
 *       <td class="TableSubHeadingColor" align="left" nowrap><b>Flags:</b></td>
 *       <td class="TableRowColor" align="left" nowrap>{@code none}</td>
 *     </tr>
 *     <tr>
 *       <td class="TableSubHeadingColor" align="left" nowrap><b>Version:</b></td>
 *       <td class="TableRowColor" align="left" nowrap>{@code 1.2-SNAPSHOT}</td>
 *     </tr>
 *   </table>
 * </p>
 * <p>
 *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="4" nowrap><font size="+2">Specifications</font></th>
 *     </tr>
 *     <tr class="TableSubHeadingColor">
 *       <td align="left" scope="col" nowrap><b>Identifier</b></td>
 *       <td align="left" scope="col" nowrap><b>Class</b></td>
 *       <td align="left" scope="col" nowrap><b>Scope</b></td>
 *       <td align="left" scope="col" nowrap><b>Version</b></td>
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
 *     <tr class="TableHeadingColor">
 *       <th align="left" scope="col" colspan="3" nowrap><font size="+2">Properties</font></th>
 *     </tr>
 *     <tr class="TableSubHeadingColor">
 *       <td align="left" scope="col" nowrap><b>Name</b></td>
 *       <td align="left" scope="col" nowrap><b>Type</b></td>
 *       <td align="left" scope="col" nowrap><b>Documentation</b></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getAbbreviatedCommandName abbreviatedCommandName}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Abbreviated name of the command.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getApplicationModlet applicationModlet}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Name of the 'shaded' application modlet.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandName commandName}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">Name of the command.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModletExcludes modletExcludes}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of modlet names to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getProviderExcludes providerExcludes}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of providers to exclude from any {@code META-INF/services} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getSchemaExcludes schemaExcludes}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of schema context-ids to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getServiceExcludes serviceExcludes}</td>
 *       <td align="left" valign="top" nowrap>{@code java.lang.String}</td>
 *       <td align="left" valign="top">List of service classes to exclude from any {@code META-INF/jomc-modlet.xml} file separated by {@code :}.</td>
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
 *       <td align="left" valign="top" nowrap>{@link #getClasspathOption ClasspathOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Classpath Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentEncodingOption DocumentEncodingOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Document Encoding Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentOption DocumentOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Document Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentsOption DocumentsOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Documents Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLocale Locale}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'default'} object of the {@code 'java.util.Locale'} {@code (java.util.Locale)} specification at specification level 1.1 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModelContextOption ModelContextOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI ModelContext Class Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModelOption ModelOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Model Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModletLocationOption ModletLocationOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Modlet Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModletSchemaSystemIdOption ModletSchemaSystemIdOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Modlet Schema System Id Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleExcludesOption ModuleExcludesOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Excludes Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleIncludesOption ModuleIncludesOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Includes Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleLocationOption ModuleLocationOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleNameOption ModuleNameOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleVendorOption ModuleVendorOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Vendor Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleVersionOption ModuleVersionOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Module Version Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getNoClasspathResolutionOption NoClasspathResolutionOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI No Classpath Resolution Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getNoModelProcessingOption NoModelProcessingOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI No Model Processing Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getPlatformProviderLocationOption PlatformProviderLocationOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Platform Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getProviderLocationOption ProviderLocationOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getStylesheetOption StylesheetOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Stylesheet Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getTransformerLocationOption TransformerLocationOption}</td>
 *       <td align="left" valign="top">Dependency on the {@code 'JOMC CLI Transformer Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2 bound to an instance.</td>
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
 *       <td align="left" valign="top" nowrap>{@link #getApplicationTitle applicationTitle}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code>JOMC CLI Version 1.2-SNAPSHOT Build 2011-03-27T16:56:16+0200</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCannotProcessMessage cannotProcessMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Cannot process ''{0}'': {1}</code></pre><hr/><pre><code>Kann ''{0}'' nicht verarbeiten: {1}</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getClasspathElementInfo classpathElementInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Classpath element: ''{0}''</code></pre><hr/><pre><code>Klassenpfad-Element: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getClasspathElementNotFoundWarning classpathElementNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Classpath element ''{0}'' ignored. File not found.</code></pre><hr/><pre><code>Klassenpfad-Element ''{0}'' ignoriert. Datei nicht gefunden.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandFailureMessage commandFailureMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>{0} failure.</code></pre><hr/><pre><code>{0} fehlgeschlagen.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandInfoMessage commandInfoMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Executing command {0} ...</code></pre><hr/><pre><code>F&uuml;hrt Befehl {0} aus ... </code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getCommandSuccessMessage commandSuccessMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>{0} successful.</code></pre><hr/><pre><code>{0} erfolgreich.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDefaultLogLevelInfo defaultLogLevelInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Default log level: ''{0}''</code></pre><hr/><pre><code>Standard-Protokollierungsstufe: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentFileInfo documentFileInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Document file: ''{0}''</code></pre><hr/><pre><code>Dokument-Datei: ''{0}''</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getDocumentFileNotFoundWarning documentFileNotFoundWarning}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Document file ''{0}'' ignored. File not found.</code></pre><hr/><pre><code>Dokument-Datei ''{0}'' ignoriert. Datei nicht gefunden.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedModletInfo excludedModletInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Modlet ''{1}'' from class path resource ''{0}'' ignored.</code></pre><hr/><pre><code>Modlet ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedModuleFromClasspathInfo excludedModuleFromClasspathInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Module ''{0}'' from class path ignored. Module with identical name already loaded.</code></pre><hr/><pre><code>Modul ''{0}'' aus Klassenpfad ignoriert. Modul mit identischem Namen bereits geladen.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedProviderInfo excludedProviderInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Provider ''{1}'' from class path resource ''{0}'' ignored.</code></pre><hr/><pre><code>Provider ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedSchemaInfo excludedSchemaInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Context ''{1}'' from class path resource ''{0}'' ignored.</code></pre><hr/><pre><code>Kontext ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludedServiceInfo excludedServiceInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Service ''{1}'' from class path resource ''{0}'' ignored.</code></pre><hr/><pre><code>Service ''{1}'' aus Klassenpfad-Ressource ''{0}'' ignoriert.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getExcludingModuleInfo excludingModuleInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Excluding module ''{0}''.</code></pre><hr/><pre><code>Schlie&szlig;t Modul ''{0}'' aus.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getIllegalTransformationResultError illegalTransformationResultError}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Failure transforming result document using ''{0}''. Illegal transformation result.</code></pre><hr/><pre><code>Fehler bei der Transformation des Ergebniss-Dokuments mit ''{0}''. Ung&uuml;ltiges Transformations-Ergebniss.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getIncludingModuleInfo includingModuleInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Including module ''{0}''.</code></pre><hr/><pre><code>Schlie&szlig;t Modul ''{0}'' ein.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getInvalidModelMessage invalidModelMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Invalid ''{0}'' model.</code></pre><hr/><pre><code>Ung&uuml;ltiges ''{0}'' Modell.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getLongDescriptionMessage longDescriptionMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Example:
 *   jomc merge-modules -cp examples/lib/commons-cli-1.2.jar \
 *                      -df examples/xml/jomc-cli.xml \
 *                      -xs examples/xslt/model-relocations.xsl \
 *                      -mn &quot;Merged Name&quot; \
 *                      -d /tmp/jomc.xml \
 *                      -v</code></pre><hr/><pre><code>Beispiel:
 *   jomc merge-modules -cp examples/lib/commons-cli-1.2.jar \
 *                      -df examples/xml/jomc-cli.xml \
 *                      -xs examples/xslt/model-relocations.xsl \
 *                      -mn &quot;Merged Name&quot; \
 *                      -d /tmp/jomc.xml \
 *                      -v</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getModuleInfo moduleInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Found module ''{0} {1}''.</code></pre><hr/><pre><code>Modul ''{0} {1}'' gefunden.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getSeparator separator}</td>
 *       <td align="left" valign="top" nowrap>English (default)</td>
 *       <td align="left" valign="top" nowrap><pre><code>--------------------------------------------------------------------------------</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getShortDescriptionMessage shortDescriptionMessage}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Merges modules.</code></pre><hr/><pre><code>F&uuml;gt Module zusammen.</code></pre></td>
 *     </tr>
 *     <tr class="TableRowColor">
 *       <td align="left" valign="top" nowrap>{@link #getWriteInfo writeInfo}</td>
 *       <td align="left" valign="top" nowrap>English (default),&nbsp;Deutsch</td>
 *       <td align="left" valign="top" nowrap><pre><code>Writing ''{0}''.</code></pre><hr/><pre><code>Schreibt ''{0}''.</code></pre></td>
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
public final class MergeModulesCommand extends AbstractModelCommand
{
    // SECTION-START[MergeModulesCommand]

    protected void executeCommand( final CommandLine commandLine ) throws CommandExecutionException
    {
        if ( commandLine == null )
        {
            throw new NullPointerException( "commandLine" );
        }

        try
        {
            final ClassLoader classLoader = new CommandLineClassLoader( commandLine );
            final ModelContext context = this.createModelContext( classLoader );
            final Model model = this.getModel( context, commandLine );
            final Marshaller marshaller = context.createMarshaller( model.getIdentifier() );
            final Unmarshaller unmarshaller = context.createUnmarshaller( model.getIdentifier() );
            final ModelValidationReport validationReport = context.validateModel( model );
            this.log( validationReport, marshaller );

            if ( !validationReport.isModelValid() )
            {
                throw new CommandExecutionException( this.getInvalidModelMessage(
                    this.getLocale(), this.getModel( commandLine ) ) );

            }

            final Modules modules = ModelHelper.getModules( model );
            final Module classpathModule = modules.getModule( Modules.getDefaultClasspathModuleName() );

            if ( classpathModule != null )
            {
                modules.getModule().remove( classpathModule );
            }

            File stylesheetFile = null;
            if ( commandLine.hasOption( this.getStylesheetOption().getOpt() ) )
            {
                stylesheetFile = new File( commandLine.getOptionValue( this.getStylesheetOption().getOpt() ) );
            }

            String moduleVersion = null;
            if ( commandLine.hasOption( this.getModuleVersionOption().getOpt() ) )
            {
                moduleVersion = commandLine.getOptionValue( this.getModuleVersionOption().getOpt() );
            }

            String moduleVendor = null;
            if ( commandLine.hasOption( this.getModuleVendorOption().getOpt() ) )
            {
                moduleVendor = commandLine.getOptionValue( this.getModuleVendorOption().getOpt() );
            }

            if ( commandLine.hasOption( this.getModuleIncludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModuleIncludesOption().getOpt() );

                if ( values != null )
                {
                    final List<String> includes = Arrays.asList( values );

                    for ( final Iterator<Module> it = modules.getModule().iterator(); it.hasNext(); )
                    {
                        final Module m = it.next();
                        if ( !includes.contains( m.getName() ) )
                        {
                            this.log( Level.INFO, this.getExcludingModuleInfo( this.getLocale(), m.getName() ), null );
                            it.remove();
                        }
                        else
                        {
                            this.log( Level.INFO, this.getIncludingModuleInfo( this.getLocale(), m.getName() ), null );
                        }
                    }
                }
            }

            if ( commandLine.hasOption( this.getModuleExcludesOption().getOpt() ) )
            {
                final String[] values = commandLine.getOptionValues( this.getModuleExcludesOption().getOpt() );

                if ( values != null )
                {
                    for ( String exclude : values )
                    {
                        final Module m = modules.getModule( exclude );

                        if ( m != null )
                        {
                            this.log( Level.INFO, this.getExcludingModuleInfo( this.getLocale(), m.getName() ), null );
                            modules.getModule().remove( m );
                        }
                    }
                }
            }

            Module mergedModule =
                modules.getMergedModule( commandLine.getOptionValue( this.getModuleNameOption().getOpt() ) );

            mergedModule.setVersion( moduleVersion );
            mergedModule.setVendor( moduleVendor );

            final File moduleFile = new File( commandLine.getOptionValue( this.getDocumentOption().getOpt() ) );

            if ( stylesheetFile != null )
            {
                final Transformer transformer = this.createTransformer( new StreamSource( stylesheetFile ) );
                final JAXBSource source =
                    new JAXBSource( marshaller, new ObjectFactory().createModule( mergedModule ) );

                final JAXBResult result = new JAXBResult( unmarshaller );
                transformer.transform( source, result );

                if ( result.getResult() instanceof JAXBElement<?>
                     && ( (JAXBElement<?>) result.getResult() ).getValue() instanceof Module )
                {
                    mergedModule = (Module) ( (JAXBElement<?>) result.getResult() ).getValue();
                }
                else
                {
                    throw new CommandExecutionException( this.getIllegalTransformationResultError(
                        this.getLocale(), stylesheetFile.getAbsolutePath() ) );

                }
            }

            marshaller.setSchema( context.createSchema( model.getIdentifier() ) );

            if ( commandLine.hasOption( this.getDocumentEncodingOption().getOpt() ) )
            {
                marshaller.setProperty( Marshaller.JAXB_ENCODING,
                                        commandLine.getOptionValue( this.getDocumentEncodingOption().getOpt() ) );

            }

            marshaller.marshal( new ObjectFactory().createModule( mergedModule ), moduleFile );

            if ( this.isLoggable( Level.INFO ) )
            {
                this.log( Level.INFO, this.getWriteInfo( this.getLocale(), moduleFile.getAbsolutePath() ), null );
            }
        }
        catch ( final TransformerException e )
        {
            String message = getExceptionMessage( e );
            if ( message == null )
            {
                message = getExceptionMessage( e.getException() );
            }

            throw new CommandExecutionException( message, e );
        }
        catch ( final JAXBException e )
        {
            String message = getExceptionMessage( e );
            if ( message == null )
            {
                message = getExceptionMessage( e.getLinkedException() );
            }

            throw new CommandExecutionException( message, e );
        }
        catch ( final ModelException e )
        {
            throw new CommandExecutionException( getExceptionMessage( e ), e );
        }
    }

    // SECTION-END
    // SECTION-START[Constructors]
    // <editor-fold defaultstate="collapsed" desc=" Generated Constructors ">

    /** Creates a new {@code MergeModulesCommand} instance. */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    public MergeModulesCommand()
    {
        // SECTION-START[Default Constructor]
        super();
        // SECTION-END
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Command]
    // <editor-fold defaultstate="collapsed" desc=" Generated Command ">
    /**
     * Gets the options of the command.
     *
     * <p><strong>Options:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Specification</b></th>
     *       <th align="left" scope="col" nowrap><b>Implementation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Classpath Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Document Encoding Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Document Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Documents Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI ModelContext Class Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Model Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Modlet Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Modlet Schema System Id Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Excludes Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Includes Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Name Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Vendor Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Module Version Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI No Classpath Resolution Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI No Model Processing Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Platform Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Provider Location Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Stylesheet Option</td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>JOMC CLI Command Option {@code (org.apache.commons.cli.Option)} @ 1.2</td>
     *       <td align="left" valign="top" nowrap>JOMC CLI Transformer Location Option</td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @return The options of the command.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    public org.apache.commons.cli.Options getOptions()
    {
        final org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
        options.addOption( this.getClasspathOption() );
        options.addOption( this.getDocumentEncodingOption() );
        options.addOption( this.getDocumentOption() );
        options.addOption( this.getDocumentsOption() );
        options.addOption( this.getModelContextOption() );
        options.addOption( this.getModelOption() );
        options.addOption( this.getModletLocationOption() );
        options.addOption( this.getModletSchemaSystemIdOption() );
        options.addOption( this.getModuleExcludesOption() );
        options.addOption( this.getModuleIncludesOption() );
        options.addOption( this.getModuleLocationOption() );
        options.addOption( this.getModuleNameOption() );
        options.addOption( this.getModuleVendorOption() );
        options.addOption( this.getModuleVersionOption() );
        options.addOption( this.getNoClasspathResolutionOption() );
        options.addOption( this.getNoModelProcessingOption() );
        options.addOption( this.getPlatformProviderLocationOption() );
        options.addOption( this.getProviderLocationOption() );
        options.addOption( this.getStylesheetOption() );
        options.addOption( this.getTransformerLocationOption() );
        return options;
    }
    // </editor-fold>
    // SECTION-END
    // SECTION-START[Dependencies]
    // <editor-fold defaultstate="collapsed" desc=" Generated Dependencies ">

    /**
     * Gets the {@code ClasspathOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Classpath Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * Gets the {@code DocumentEncodingOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Document Encoding Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code DocumentEncodingOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentEncodingOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentEncodingOption" );
        assert _d != null : "'DocumentEncodingOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Document Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * <p><strong>Properties:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Name</b></th>
     *       <th align="left" scope="col" nowrap><b>Type</b></th>
     *       <th align="left" scope="col" nowrap><b>Documentation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>{@code required}</td>
     *       <td align="left" valign="top" nowrap>{@code boolean}</td>
     *       <td align="left" valign="top"></td>
     *     </tr>
     *   </table>
     * </p>
     * @return The {@code DocumentOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getDocumentOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "DocumentOption" );
        assert _d != null : "'DocumentOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code DocumentsOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Documents Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * Gets the {@code ModelContextOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI ModelContext Class Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI Model Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI Modlet Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI Modlet Schema System Id Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * Gets the {@code ModuleExcludesOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Excludes Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleExcludesOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleExcludesOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleExcludesOption" );
        assert _d != null : "'ModuleExcludesOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleIncludesOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Includes Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleIncludesOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleIncludesOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleIncludesOption" );
        assert _d != null : "'ModuleIncludesOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI Module Name Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * <p><strong>Properties:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Name</b></th>
     *       <th align="left" scope="col" nowrap><b>Type</b></th>
     *       <th align="left" scope="col" nowrap><b>Documentation</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>{@code required}</td>
     *       <td align="left" valign="top" nowrap>{@code boolean}</td>
     *       <td align="left" valign="top"></td>
     *     </tr>
     *   </table>
     * </p>
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
     * Gets the {@code ModuleVendorOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Vendor Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleVendorOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleVendorOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleVendorOption" );
        assert _d != null : "'ModuleVendorOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code ModuleVersionOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Module Version Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code ModuleVersionOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getModuleVersionOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "ModuleVersionOption" );
        assert _d != null : "'ModuleVersionOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code NoClasspathResolutionOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI No Classpath Resolution Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI No Model Processing Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * Gets the {@code PlatformProviderLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Platform Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * <p>This method returns the {@code 'JOMC CLI Provider Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     * Gets the {@code StylesheetOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Stylesheet Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
     * <p>That specification does not apply to any scope. A new object is returned whenever requested and bound to this instance.</p>
     * @return The {@code StylesheetOption} dependency.
     * @throws org.jomc.ObjectManagementException if getting the dependency instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private org.apache.commons.cli.Option getStylesheetOption()
    {
        final org.apache.commons.cli.Option _d = (org.apache.commons.cli.Option) org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getDependency( this, "StylesheetOption" );
        assert _d != null : "'StylesheetOption' dependency not found.";
        return _d;
    }

    /**
     * Gets the {@code TransformerLocationOption} dependency.
     * <p>This method returns the {@code 'JOMC CLI Transformer Location Option'} object of the {@code 'JOMC CLI Command Option'} {@code (org.apache.commons.cli.Option)} specification at specification level 1.2.</p>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>JOMC CLI Version 1.2-SNAPSHOT Build 2011-03-27T16:56:16+0200</code></pre></td>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     * Gets the text of the {@code documentFileInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     * Gets the text of the {@code excludingModuleInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Excluding module ''{0}''.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Schlie&szlig;t Modul ''{0}'' aus.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code excludingModuleInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getExcludingModuleInfo( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "excludingModuleInfo", locale, moduleName );
        assert _m != null : "'excludingModuleInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code illegalTransformationResultError} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Failure transforming result document using ''{0}''. Illegal transformation result.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Fehler bei der Transformation des Ergebniss-Dokuments mit ''{0}''. Ung&uuml;ltiges Transformations-Ergebniss.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param stylesheetInfo Format argument.
     * @return The text of the {@code illegalTransformationResultError} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getIllegalTransformationResultError( final java.util.Locale locale, final java.lang.String stylesheetInfo )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "illegalTransformationResultError", locale, stylesheetInfo );
        assert _m != null : "'illegalTransformationResultError' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code includingModuleInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Including module ''{0}''.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Schlie&szlig;t Modul ''{0}'' ein.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param moduleName Format argument.
     * @return The text of the {@code includingModuleInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getIncludingModuleInfo( final java.util.Locale locale, final java.lang.String moduleName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "includingModuleInfo", locale, moduleName );
        assert _m != null : "'includingModuleInfo' message not found.";
        return _m;
    }

    /**
     * Gets the text of the {@code invalidModelMessage} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Example:
     *   jomc merge-modules -cp examples/lib/commons-cli-1.2.jar \
     *                      -df examples/xml/jomc-cli.xml \
     *                      -xs examples/xslt/model-relocations.xsl \
     *                      -mn &quot;Merged Name&quot; \
     *                      -d /tmp/jomc.xml \
     *                      -v</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Beispiel:
     *   jomc merge-modules -cp examples/lib/commons-cli-1.2.jar \
     *                      -df examples/xml/jomc-cli.xml \
     *                      -xs examples/xslt/model-relocations.xsl \
     *                      -mn &quot;Merged Name&quot; \
     *                      -d /tmp/jomc.xml \
     *                      -v</code></pre></td>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     * Gets the text of the {@code separator} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
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
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Merges modules.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>F&uuml;gt Module zusammen.</code></pre></td>
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
     * Gets the text of the {@code writeInfo} message.
     * <p><strong>Templates:</strong>
     *   <table border="1" width="100%" cellpadding="3" cellspacing="0">
     *     <tr class="TableSubHeadingColor">
     *       <th align="left" scope="col" nowrap><b>Language</b></th>
     *       <th align="left" scope="col" nowrap><b>Template</b></th>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>English (default)</td>
     *       <td align="left" valign="top" nowrap><pre><code>Writing ''{0}''.</code></pre></td>
     *     </tr>
     *     <tr class="TableRow">
     *       <td align="left" valign="top" nowrap>Deutsch</td>
     *       <td align="left" valign="top" nowrap><pre><code>Schreibt ''{0}''.</code></pre></td>
     *     </tr>
     *   </table>
     * </p>
     *
     * @param locale The locale of the message to return.
     * @param fileName Format argument.
     * @return The text of the {@code writeInfo} message for {@code locale}.
     * @throws org.jomc.ObjectManagementException if getting the message instance fails.
     */
    @javax.annotation.Generated( value = "org.jomc.tools.SourceFileProcessor 1.2-SNAPSHOT", comments = "See http://jomc.sourceforge.net/jomc/1.2/jomc-tools-1.2-SNAPSHOT" )
    private String getWriteInfo( final java.util.Locale locale, final java.lang.String fileName )
    {
        final String _m = org.jomc.ObjectManagerFactory.getObjectManager( this.getClass().getClassLoader() ).getMessage( this, "writeInfo", locale, fileName );
        assert _m != null : "'writeInfo' message not found.";
        return _m;
    }
    // </editor-fold>
    // SECTION-END
}
