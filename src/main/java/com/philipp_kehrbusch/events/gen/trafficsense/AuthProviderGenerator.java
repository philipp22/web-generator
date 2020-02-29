package com.philipp_kehrbusch.events.gen.trafficsense;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.FileNameResolvers;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.WebDomainGenerator;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebDomainGeneratorException;

import java.io.IOException;

public class AuthProviderGenerator {

  public static void main(String[] args) throws WebDomainGeneratorException {
    var projectPath = "/home/philipp/Dokumente/programmierung/traffic-sense/auth-provider";
    var projectPathSplitted = projectPath.split("/");
    var projectName = projectPathSplitted[projectPathSplitted.length - 1];
    var projectPathClient = "/home/philipp/Dokumente/programmierung/traffic-sense/auth-provider-client";

    var genSettings = new GeneratorSettings(
            "/home/philipp/Dokumente/programmierung/events-gen/eventsgen/src/main/resources/model-traffic-sense/auth-provider")
            .trafoBasePackage("com.philipp_kehrbusch.events.gen")
            .addTarget(Targets.BACKEND, new Target(
                    Target.JAVA,
                    projectPath + "/src/main/gen",
                    projectPath + "/src/main/java",
                    FileNameResolvers.JAVA,
                    "java/Artifact.ftl",
                    "com.philipp_kehrbusch.trafficsense.authprovider",
                    projectName))
            .addTarget(Targets.CLIENT_JAVA, new Target(
                    Target.JAVA,
                    projectPathClient + "/src/main/gen",
                    projectPathClient + "/src/main/java",
                    FileNameResolvers.JAVA,
                    "java/Artifact.ftl",
                    "com.philipp_kehrbusch.trafficsense.authprovider.client",
                    projectName + "-client"));

    var gen = new WebDomainGenerator(genSettings);
    gen.generate();

    try {
      new ProcessBuilder(
              "prettier",
              "--write",
              projectPath + "/src/main/gen/**/*.java"
      ).inheritIO().start().waitFor();
      new ProcessBuilder(
              "prettier",
              "--write",
              projectPathClient + "/src/main/gen/**/*.java"
      ).inheritIO().start().waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
