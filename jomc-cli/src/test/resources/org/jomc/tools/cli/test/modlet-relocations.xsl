<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (C) Christian Schulte <cs@schulte.it>, 2005-206
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

    o Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    o Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in
      the documentation and/or other materials provided with the
      distribution.

  THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
  AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

  $JOMC$

-->
<xsl:stylesheet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:modlet="http://jomc.org/modlet"
                version="1.0">

  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"
              encoding="UTF-8" standalone="no"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:schemas/modlet:schema/@public-id">
    <xsl:call-template name="relocate-public-id"/>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:schemas/modlet:schema/@system-id">
    <xsl:call-template name="relocate-system-id"/>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:schemas/modlet:schema/@context-id">
    <xsl:call-template name="relocate-context-id"/>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:schemas/modlet:schema/@classpath-id">
    <xsl:call-template name="relocate-classpath-id"/>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:services/modlet:service/@identifier">
    <xsl:call-template name="relocate-classname"/>
  </xsl:template>

  <xsl:template match="modlet:modlet/modlet:services/modlet:service/@class">
    <xsl:call-template name="relocate-classname"/>
  </xsl:template>

  <xsl:template name="relocate-classname">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'org.jomc.model.')">
          <xsl:value-of select="concat('org.jomc.cli.util.model.', substring-after($value, 'org.jomc.model.'))"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.tools.model.')">
          <xsl:value-of select="concat('org.jomc.cli.util.tools.model.', substring-after($value, 'org.jomc.tools.model.'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="relocate-public-id">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'http://jomc.org/model')">
          <xsl:value-of select="concat('http://jomc.org/model', substring-after($value, 'http://jomc.org/model'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="relocate-system-id">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'http://www.jomc.org/model')">
          <xsl:value-of select="concat('http://www.jomc.org/model', substring-after($value, 'http://www.jomc.org/model'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="relocate-context-id">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'org.jomc.model')">
          <xsl:value-of select="concat('org.jomc.cli.util.model', substring-after($value, 'org.jomc.model'))"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.tools')">
          <xsl:value-of select="concat('org.jomc.cli.util.tools', substring-after($value, 'org.jomc.tools'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="relocate-classpath-id">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'org/jomc/model')">
          <xsl:value-of select="concat('org/jomc/cli/util/model', substring-after($value, 'org/jomc/model'))"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org/jomc/tools')">
          <xsl:value-of select="concat('org/jomc/cli/util/tools', substring-after($value, 'org/jomc/tools'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

</xsl:stylesheet>
