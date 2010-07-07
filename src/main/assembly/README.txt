
  ${project.organization.name} - ${project.name} - README.txt
  Version ${project.version} Build ${buildNumber}
  ${project.url}

  ${project.description}

    See the output of the 'jomc' application for further information. For
    launching the application use of the scripts found in the 'bin' directory
    is recommended. As a fallback, the Java archive found in the 'bin' directory
    can be executed using the standard Java application launcher.

    bin/jomc (Unix)
    bin/jomc.bat (Windows)
    java -jar bin/${project.build.finalName}.jar

  Integration of the JOMC Tools

    The 'lib/tools' directory contains Java archives needed when integrating the
    JOMC tools. See the jomc-tools-${project.version}.jar archive.

  Maven 2 Plugin

    See http://jomc.sourceforge.net/jomc/${jomc-parent.version}/maven-jomc-plugin

  JDK 1.5

    The 'lib/ext' directory contains JDK extensions to setup using the
    'java.ext.dirs' system property or another mechanism compatible to the JDK
    in use.

    The 'lib/endorsed' directory contains updates to libraries part of the JDK
    to setup via the 'java.endorsed.dirs' system property or another mechanism
    compatible to the JDK in use. Use of these libraries may become necessary
    when encountering problems with the XML parsers of the JDK.

    export JOMC_OPTS="-Djava.ext.dirs='path to lib/ext directory' \
                      -Djava.endorsed.dirs='path to lib/endorsed directory'"

    bin/jomc (Unix)
    bin/jomc.bat (Windows)

    java -Djava.ext.dirs=lib/ext \
         -Djava.endorsed.dirs=lib/endorsed \
         -jar bin/${project.build.finalName}.jar
