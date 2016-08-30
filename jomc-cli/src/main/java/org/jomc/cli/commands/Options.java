/*
 * Copyright (C) 2009 Christian Schulte <cs@schulte.it>
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
package org.jomc.cli.commands;

import java.io.File;
import org.apache.commons.cli.Option;

/**
 * Command options.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 */
class Options
{

    /**
     * Command line option.
     */
    static final Option CLASSPATH_OPTION;

    /**
     * Command line option.
     */
    static final Option DOCUMENTS_OPTION;

    /**
     * Command line option.
     */
    static final Option MODEL_CONTEXT_FACTORY_CLASSNAME_OPTION;

    /**
     * Command line option.
     */
    static final Option MODEL_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_SCHEMA_SYSTEM_ID_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option PROVIDER_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option PLATFORM_PROVIDER_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_MODLET_RESOURCE_VALIDATION_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option TRANSFORMER_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_CLASSPATH_RESOLUTION_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_MODEL_PROCESSING_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_MODEL_RESOURCE_VALIDATION_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_JAVA_VALIDATION_OPTION;

    /**
     * Command line option.
     */
    static final Option TEMPLATE_PROFILE_OPTION;

    /**
     * Command line option.
     */
    static final Option DEFAULT_TEMPLATE_PROFILE_OPTION;

    /**
     * Command line option.
     */
    static final Option DEFAULT_TEMPLATE_ENCODING_OPTION;

    /**
     * Command line option.
     */
    static final Option TEMPLATE_LOCATION_OPTION;

    /**
     * Command line option.
     */
    static final Option OUTPUT_ENCODING_OPTION;

    /**
     * Command line option.
     */
    static final Option INPUT_ENCODING_OPTION;

    /**
     * Command line option.
     */
    static final Option INDENTATION_STRING_OPTION;

    /**
     * Command line option.
     */
    static final Option LINE_SEPARATOR_OPTION;

    /**
     * Command line option.
     */
    static final Option LANGUAGE_OPTION;

    /**
     * Command line option.
     */
    static final Option COUNTRY_OPTION;

    /**
     * Command line option.
     */
    static final Option LOCALE_VARIANT_OPTION;

    /**
     * Command line option.
     */
    static final Option IMPLEMENTATION_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_OPTION;

    /**
     * Command line option.
     */
    static final Option SPECIFICATION_OPTION;

    /**
     * Command line option.
     */
    static final Option CLASS_FILE_PROCESSOR_CLASSNAME_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_CLASS_PROCESSING_OPTION;

    /**
     * Command line option.
     */
    static final Option RESOURCE_FILE_PROCESSOR_CLASSNAME_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_RESOURCE_PROCESSING_OPTION;

    /**
     * Command line option.
     */
    static final Option RESOURCE_DIRECTORY_OPTION;

    /**
     * Command line option.
     */
    static final Option SOURCE_FILE_PROCESSOR_CLASSNAME_OPTION;

    /**
     * Command line option.
     */
    static final Option NO_SOURCE_PROCESSING_OPTION;

    /**
     * Command line option.
     */
    static final Option SOURCE_DIRECTORY_OPTION;

    /**
     * Command line option.
     */
    static final Option CLASSES_DIRECTORY_OPTION;

    /**
     * Command line option.
     */
    static final Option STYLESHEET_OPTION;

    /**
     * Command line option.
     */
    static final Option DOCUMENT_OPTION;

    /**
     * Command line option.
     */
    static final Option DOCUMENT_ENCODING_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_VERSION_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_VENDOR_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_INCLUDES_OPTION;

    /**
     * Command line option.
     */
    static final Option MODULE_EXCLUDES_OPTION;

    /**
     * Command line option.
     */
    static final Option RESOURCES_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_VERSION_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_VENDOR_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_INCLUDES_OPTION;

    /**
     * Command line option.
     */
    static final Option MODLET_EXCLUDES_OPTION;

    /**
     * Command line option.
     */
    @Deprecated
    static final Option TEMPLATE_ENCODING_OPTION;

    /**
     * Command line option.
     */
    static final Option THREADS_OPTION;

    static
    {
        CLASSPATH_OPTION =
            Option.builder( "cp" ).longOpt( "classpath" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "classpathOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "classpathOptionArgumentDescription" ) ).build();

        DOCUMENTS_OPTION =
            Option.builder( "df" ).longOpt( "documents" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "documentsOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "documentsOptionArgumentDescription" ) ).build();

        MODEL_CONTEXT_FACTORY_CLASSNAME_OPTION =
            Option.builder( "mcfc" ).longOpt( "model-context-factory-class" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modelContextFactoryClassnameOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modelContextFactoryClassnameOptionArgumentDescription" ) ).build();

        MODEL_OPTION =
            Option.builder( "m" ).longOpt( "model" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modelOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modelOptionArgumentDescription" ) ).build();

        MODLET_SCHEMA_SYSTEM_ID_OPTION =
            Option.builder( "mssid" ).longOpt( "modlet-schema-system-id" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modletSchemaSystemIdOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletSchemaSystemIdOptionArgumentDescription" ) ).build();

        MODLET_LOCATION_OPTION =
            Option.builder( "mdl" ).longOpt( "modlet-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modletLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletLocationOptionArgumentDescription" ) ).build();

        PROVIDER_LOCATION_OPTION =
            Option.builder( "pl" ).longOpt( "provider-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "providerLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "providerLocationOptionArgumentDescription" ) ).build();

        PLATFORM_PROVIDER_LOCATION_OPTION =
            Option.builder( "ppl" ).longOpt( "platform-provider-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "platformProviderLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "platformProviderLocationOptionArgumentDescription" ) ).build();

        NO_MODLET_RESOURCE_VALIDATION_OPTION =
            Option.builder( "nmdrv" ).longOpt( "no-modlet-resource-validation" ).hasArg( false ).
            desc( Messages.getMessage( "noModletResourceValidationOptionDescription", File.pathSeparator ) ).build();

        MODULE_LOCATION_OPTION =
            Option.builder( "ml" ).longOpt( "model-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modelLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modelLocationOptionArgumentDescription" ) ).build();

        TRANSFORMER_LOCATION_OPTION =
            Option.builder( "trl" ).longOpt( "transformer-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "transformerLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "transformerLocationOptionArgumentDescription" ) ).build();

        NO_CLASSPATH_RESOLUTION_OPTION =
            Option.builder( "ncr" ).longOpt( "no-classpath-resolution" ).hasArg( false ).
            desc( Messages.getMessage( "noClasspathResolutionOptionDescription", File.pathSeparator ) ).build();

        NO_MODEL_PROCESSING_OPTION =
            Option.builder( "nmp" ).longOpt( "no-model-processing" ).hasArg( false ).
            desc( Messages.getMessage( "noModelProcessingOptionDescription", File.pathSeparator ) ).build();

        NO_MODEL_RESOURCE_VALIDATION_OPTION =
            Option.builder( "nmrv" ).longOpt( "no-model-resource-validation" ).hasArg( false ).
            desc( Messages.getMessage( "noModelResourceValidationOptionDescription", File.pathSeparator ) ).build();

        NO_JAVA_VALIDATION_OPTION =
            Option.builder( "njv" ).longOpt( "no-java-validation" ).hasArg( false ).
            desc( Messages.getMessage( "noJavaValidationOptionDescription", File.pathSeparator ) ).build();

        TEMPLATE_PROFILE_OPTION =
            Option.builder( "tp" ).longOpt( "template-profile" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "templateProfileOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "templateProfileOptionArgumentDescription" ) ).build();

        DEFAULT_TEMPLATE_PROFILE_OPTION =
            Option.builder( "dtp" ).longOpt( "default-template-profile" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "defaultTemplateProfileOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "defaultTemplateProfileOptionArgumentDescription" ) ).build();

        DEFAULT_TEMPLATE_ENCODING_OPTION =
            Option.builder( "dte" ).longOpt( "default-template-encoding" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "defaultTemplateEncodingOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "defaultTemplateEncodingOptionArgumentDescription" ) ).build();

        TEMPLATE_LOCATION_OPTION =
            Option.builder( "tl" ).longOpt( "template-location" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "templateLocationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "templateLocationOptionArgumentDescription" ) ).build();

        OUTPUT_ENCODING_OPTION =
            Option.builder( "oe" ).longOpt( "output-encoding" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "outputEncodingOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "outputEncodingOptionArgumentDescription" ) ).build();

        INPUT_ENCODING_OPTION =
            Option.builder( "ie" ).longOpt( "input-encoding" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "inputEncodingOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "inputEncodingOptionArgumentDescription" ) ).build();

        INDENTATION_STRING_OPTION =
            Option.builder( "idt" ).longOpt( "indentation" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "indentationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "indentationOptionArgumentDescription" ) ).build();

        LINE_SEPARATOR_OPTION =
            Option.builder( "ls" ).longOpt( "line-separator" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "lineSeparatorOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "lineSeparatorOptionArgumentDescription" ) ).build();

        LANGUAGE_OPTION =
            Option.builder( "l" ).longOpt( "language" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "languageOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "languageOptionArgumentDescription" ) ).build();

        COUNTRY_OPTION =
            Option.builder( "c" ).longOpt( "country" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "countryOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "countryOptionArgumentDescription" ) ).build();

        LOCALE_VARIANT_OPTION =
            Option.builder( "lv" ).longOpt( "locale-variant" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "localeVariantOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "localeVariantOptionArgumentDescription" ) ).build();

        IMPLEMENTATION_OPTION =
            Option.builder( "impl" ).longOpt( "implementation" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "implementationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "implementationOptionArgumentDescription" ) ).build();

        MODULE_OPTION =
            Option.builder( "mn" ).longOpt( "module" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "moduleOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "moduleOptionArgumentDescription" ) ).build();

        SPECIFICATION_OPTION =
            Option.builder( "spec" ).longOpt( "specification" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "specificationOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "specificationOptionArgumentDescription" ) ).build();

        CLASS_FILE_PROCESSOR_CLASSNAME_OPTION =
            Option.builder( "cfpc" ).longOpt( "class-file-processor-class" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "classFileProcessorClassnameOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "classFileProcessorClassnameOptionArgumentDescription" ) ).build();

        NO_CLASS_PROCESSING_OPTION =
            Option.builder( "ncp" ).longOpt( "no-class-processing" ).hasArg( false ).
            desc( Messages.getMessage( "noClassProcessingOptionDescription", File.pathSeparator ) ).build();

        RESOURCE_FILE_PROCESSOR_CLASSNAME_OPTION =
            Option.builder( "rfpc" ).longOpt( "resource-file-processor-class" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "resourceFileProcessorClassnameOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "resourceFileProcessorClassnameOptionArgumentDescription" ) ).build();

        NO_RESOURCE_PROCESSING_OPTION =
            Option.builder( "nrp" ).longOpt( "no-resource-processing" ).hasArg( false ).
            desc( Messages.getMessage( "noResourceProcessingOptionDescription", File.pathSeparator ) ).build();

        RESOURCE_DIRECTORY_OPTION =
            Option.builder( "rd" ).longOpt( "resource-dir" ).hasArg().optionalArg( false ).required().
            desc( Messages.getMessage( "resourceDirectoryOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "resourceDirectoryOptionArgumentDescription" ) ).build();

        SOURCE_FILE_PROCESSOR_CLASSNAME_OPTION =
            Option.builder( "sfpc" ).longOpt( "source-file-processor-class" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "sourceFileProcessorClassnameOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "sourceFileProcessorClassnameOptionArgumentDescription" ) ).build();

        NO_SOURCE_PROCESSING_OPTION =
            Option.builder( "nsp" ).longOpt( "no-source-processing" ).hasArg( false ).
            desc( Messages.getMessage( "noSourceProcessingOptionDescription", File.pathSeparator ) ).build();

        SOURCE_DIRECTORY_OPTION =
            Option.builder( "sd" ).longOpt( "source-dir" ).hasArg().optionalArg( false ).required().
            desc( Messages.getMessage( "sourceDirectoryOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "sourceDirectoryOptionArgumentDescription" ) ).build();

        CLASSES_DIRECTORY_OPTION =
            Option.builder( "cd" ).longOpt( "classes-dir" ).hasArg().optionalArg( false ).required().
            desc( Messages.getMessage( "classesDirectoryOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "classesDirectoryOptionArgumentDescription" ) ).build();

        STYLESHEET_OPTION =
            Option.builder( "xs" ).longOpt( "stylesheet" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "stylesheetOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "stylesheetOptionArgumentDescription" ) ).build();

        DOCUMENT_OPTION =
            Option.builder( "d" ).longOpt( "document" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "documentOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "documentOptionArgumentDescription" ) ).build();

        DOCUMENT_ENCODING_OPTION =
            Option.builder( "de" ).longOpt( "document-encoding" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "documentEncodingOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "documentEncodingOptionArgumentDescription" ) ).build();

        MODULE_VERSION_OPTION =
            Option.builder( "mv" ).longOpt( "module-version" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "moduleVersionOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "moduleVersionOptionArgumentDescription" ) ).build();

        MODULE_VENDOR_OPTION =
            Option.builder( "mve" ).longOpt( "module-vendor" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "moduleVendorOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "moduleVendorOptionArgumentDescription" ) ).build();

        MODULE_INCLUDES_OPTION =
            Option.builder( "minc" ).longOpt( "module-includes" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "moduleIncludesOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "moduleIncludesOptionArgumentDescription" ) ).build();

        MODULE_EXCLUDES_OPTION =
            Option.builder( "mexc" ).longOpt( "module-excludes" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "moduleExcludesOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "moduleExcludesOptionArgumentDescription" ) ).build();

        RESOURCES_OPTION =
            Option.builder( "rn" ).longOpt( "resource-names" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "resourcesOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "resourcesOptionArgumentDescription" ) ).build();

        MODLET_OPTION =
            Option.builder( "mdn" ).longOpt( "modlet-name" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modletOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletOptionArgumentDescription" ) ).build();

        MODLET_VERSION_OPTION =
            Option.builder( "mdv" ).longOpt( "modlet-version" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modletVersionOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletVersionOptionArgumentDescription" ) ).build();

        MODLET_VENDOR_OPTION =
            Option.builder( "mdve" ).longOpt( "modlet-vendor" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "modletVendorOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletVendorOptionArgumentDescription" ) ).build();

        MODLET_INCLUDES_OPTION =
            Option.builder( "mdinc" ).longOpt( "modlet-includes" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "modletIncludesOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletIncludesOptionArgumentDescription" ) ).build();

        MODLET_EXCLUDES_OPTION =
            Option.builder( "mdexc" ).longOpt( "modlet-excludes" ).hasArgs().optionalArg( false ).
            valueSeparator( File.pathSeparatorChar ).
            desc( Messages.getMessage( "modletExcludesOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "modletExcludesOptionArgumentDescription" ) ).build();

        TEMPLATE_ENCODING_OPTION =
            Option.builder( "te" ).longOpt( "template-encoding" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "templateEncodingOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "templateEncodingOptionArgumentDescription" ) ).build();

        THREADS_OPTION =
            Option.builder( "T" ).longOpt( "threads" ).hasArg().optionalArg( false ).
            desc( Messages.getMessage( "threadsOptionOptionDescription", File.pathSeparator ) ).
            argName( Messages.getMessage( "threadsOptionArgumentDescription" ) ).build();

    }

}
