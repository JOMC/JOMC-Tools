<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (C) Christian Schulte, 2005-206
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
                xmlns:jomc="http://jomc.org/model"
                version="1.0">

  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"
              encoding="UTF-8" standalone="no"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="jomc:specifications/jomc:specification/@identifier">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:specifications/jomc:specification/@class">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:specifications/jomc:reference/@identifier">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:implementations/jomc:implementation/@identifier">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:implementations/jomc:reference/@identifier">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:implementations/jomc:implementation/@class">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template match="jomc:dependencies/jomc:dependency/@identifier">
    <xsl:call-template name="relocate-attribute"/>
  </xsl:template>

  <xsl:template name="relocate-attribute">
    <xsl:variable name="value" select="string(.)"/>
    <xsl:attribute name="{name()}">
      <xsl:choose>
        <xsl:when test="starts-with($value, 'org.jomc.ObjectManager')">
          <xsl:value-of select="'org.jomc.cli.util.ObjectManager'"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.ObjectManagerFactory')">
          <xsl:value-of select="'org.jomc.cli.util.ObjectManagerFactory'"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.ObjectManagementException')">
          <xsl:value-of select="'org.jomc.cli.util.ObjectManagementException'"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.spi.')">
          <xsl:value-of select="concat('org.jomc.cli.util.', substring-after($value, 'org.jomc.spi.'))"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.jomc.ri.')">
          <xsl:value-of select="concat('org.jomc.cli.util.', substring-after($value, 'org.jomc.ri.'))"/>
        </xsl:when>
        <xsl:when test="starts-with($value, 'org.apache.commons.cli.')">
          <xsl:value-of select="concat('org.jomc.cli.util.cli.', substring-after($value, 'org.apache.commons.cli.'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
