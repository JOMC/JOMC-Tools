
  ${project.organization.name} - ${project.name} - README.txt
  Version ${project.version} (${build.date})
  ${project.url}

  ${project.description}

    See the output of the 'jomc' application for further information. For
    launching the application use of the scripts found in the 'bin' directory
    is recommended. As a fallback, the Java archive found in the 'bin' directory
    can be executed using the standard Java application launcher.

    bin/jomc (Unix)
    bin/jomc.bat (Windows)
    java -jar bin/${project.build.finalName}.jar

    In case you still have questions regarding the application's usage, please
    feel free to contact the user mailing list. The posts to the mailing list
    are archived and could already contain the answer to your question as part
    of an older thread. Hence, it is also worth browsing/searching the mail
    archive.

    If you feel like the application is missing a feature or has a defect, you
    can file a feature request or bug report with the issue tracker. When
    creating a new issue, please provide a comprehensive description of your
    concern. Especially for fixing bugs it is crucial that a developer can
    reproduce your problem. For this reason, entire debug logs attached to the
    issue are very much appreciated. Of course, patches are welcome, too.

  Integration of the JOMC Tools

    The 'lib/tools' directory contains Java archives needed when integrating the
    JOMC tools. See the jomc-tools-${project.version}.jar archive.

  Apache Ant Tasks

    See ${project.parent.url}/jomc-ant-tasks

  Apache Maven 2 Plugin

    See ${project.parent.url}/maven-jomc-plugin

  JDK 1.5

    The 'lib/jdk5/ext' directory contains JDK extensions to setup using the
    'java.ext.dirs' system property or another mechanism compatible to the JDK
    in use.

    The 'lib/jdk5/endorsed' directory contains updates to libraries part of the
    JDK to setup via the 'java.endorsed.dirs' system property or another
    mechanism compatible to the JDK in use. Use of these libraries may become
    necessary when encountering problems with the XML parsers of the JDK.

    export JOMC_OPTS="-Djava.ext.dirs=lib/jdk5/ext \
                      -Djava.endorsed.dirs=lib/jdk5/endorsed"

    bin/jomc (Unix)
    bin/jomc.bat (Windows)

    java -Djava.ext.dirs=lib/jdk5/ext \
         -Djava.endorsed.dirs=lib/jdk5/endorsed \
         -jar bin/${project.build.finalName}.jar
