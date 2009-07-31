
  ${pom.organization.name} - ${pom.name} - README.txt
  Version ${pom.version} Build ${buildNumber}
  ${pom.url}

  ${pom.description}

      See the output of 'java -jar ${project.build.finalName}.jar' for further
      information. The 'ext' directory contains Java archives needed to run the
      application with JDK 1.5.

      java -Djava.ext.dirs=ext -jar ${project.build.finalName}.jar
