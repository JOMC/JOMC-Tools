
  ${pom.organization.name} - ${pom.name} - README.txt
  Version ${pom.version} Build ${buildNumber}
  ${pom.url}

  ${pom.description}

    See the output of the 'jomc' application for further information. For
    launching that application use of the scripts found in the 'bin' directory
    is recommended. As a fallback, the Java archive found in the 'lib' directory
    can be executed using the standard Java application launcher.

    The 'lib/ext' directory contains Java archives needed to run the application
    with JDK 1.5. You need to set the environment variable 'JOMC_OPTS' to
    contain the system property 'java.ext.dirs' pointing to that directory.

      export JOMC_OPTS="-Djava.ext.dirs='path to lib/ext directory'".

      java -jar ${project.build.finalName}.jar
      java -Djava.ext.dirs=lib/ext -jar ${project.build.finalName}.jar
