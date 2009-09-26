
  ${pom.organization.name} - ${pom.name} - README.txt
  Version ${pom.version} Build ${buildNumber}
  ${pom.url}

  ${pom.description}

    See the output of the 'jomc' application for further information. For
    launching that application use of the scripts found in the 'bin' directory
    is recommended. As a fallback, the Java archive found in the 'bin' directory
    can be executed using the standard Java application launcher.

    bin/jomc (Unix)
    bin/jomc.bat (Windows)
    java -jar bin/${project.build.finalName}.jar

    The 'jomc/lib/ext' directory contains Java archives needed with JDK 1.5.

    export JOMC_OPTS="-Djava.ext.dirs='path to jomc/lib/ext directory'".
    bin/jomc (Unix)
    bin/jomc.bat (Windows)
    java -Djava.ext.dirs=jomc/lib/ext -jar bin/${project.build.finalName}.jar
