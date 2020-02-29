package com.philipp_kehrbusch.events.gen;

import com.philipp_kehrbusch.gen.webdomain.FileNameResolvers;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.WebDomainGenerator;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebDomainGeneratorException;

import java.io.IOException;

public class DeployServerGenerator {

  public static void main(String[] args) throws WebDomainGeneratorException {
    var projectPath = args[0];

    var genSettings = new GeneratorSettings(
            "/home/philipp/Dokumente/programmierung/events-gen/eventsgen/src/main/resources/model-deploy/server")
            .trafoBasePackage("com.philipp_kehrbusch.events.gen")
            .addTarget(Targets.BACKEND, new Target(
                    Target.JAVA,
                    "/home/philipp/Dokumente/programmierung/pk-tools/deploy/deploy-server/src/main/gen",
                    "/home/philipp/Dokumente/programmierung/pk-tools/deploy/deploy-server/src/main/java",
                    FileNameResolvers.JAVA,
                    "java/Artifact.ftl",
                    "com.philipp_kehrbusch.tools.deploy.server",
                    "deploy-server"));

    var gen = new WebDomainGenerator(genSettings);
    gen.generate();

    try {
      new ProcessBuilder(
              "prettier",
              "--write",
              "/home/philipp/Dokumente/programmierung/pk-tools/deploy/deploy-server/src/main/gen/**/*.java"
      ).inheritIO().start().waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
