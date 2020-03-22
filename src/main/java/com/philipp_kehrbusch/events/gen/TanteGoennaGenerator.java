package com.philipp_kehrbusch.events.gen;

import com.philipp_kehrbusch.gen.webdomain.FileNameResolvers;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.WebDomainGenerator;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebDomainGeneratorException;

import java.io.IOException;

public class TanteGoennaGenerator {

  public static void main(String[] args) throws WebDomainGeneratorException {
    var projectPath = args[0];

    var genSettings = new GeneratorSettings(
            "/home/philipp/Dokumente/programmierung/events-gen/eventsgen/src/main/resources/model-tantegoenna")
            .trafoBasePackage("com.philipp_kehrbusch.events.gen")
            .addTarget(Targets.FRONTEND, new Target(
                    Target.TYPESCRIPT,
                    "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-client/src/gen",
                    "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-client/src/hwc",
                    FileNameResolvers.TYPESCRIPT,
                    "ts/Artifact.ftl",
                    "",
                    "tantegoenna-client"))
            .addTarget(Targets.BACKEND, new Target(
                    Target.JAVA,
                    "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-backend/src/main/gen",
                    "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-backend/src/main/java",
                    FileNameResolvers.JAVA,
                    "java/Artifact.ftl",
                    "com.tantegoenna",
                    "tantegoenna-backend"))
//            .addTarget(Targets.APP, new Target(
//                    Target.DART,
//                    "/home/philipp/Dokumente/programmierung/events/eventsapp/lib/gen/",
//                    "/home/philipp/Dokumente/programmierung/events/eventsapp/lib/hwc/",
//                    FileNameResolvers.DART,
//                    "dart/Artifact.ftl",
//                    "com/philipp_kehrbusch/events",
//                    "eventsapp"));
            ;

    var gen = new WebDomainGenerator(genSettings);
    gen.generate();

    try {
      new ProcessBuilder(
              "prettier",
              "--write",
              "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-backend/src/main/gen/**/*.java"
      ).inheritIO().start().waitFor();

      new ProcessBuilder("prettier",
              "--single-quote",
              "--trailing-comma",
              "es5",
              "--write",
              "/home/philipp/Dokumente/programmierung/tantegoenna/tantegoenna-client/src/gen/**/*.ts"
      ).inheritIO().start().waitFor();

//      new ProcessBuilder(
//              "/home/philipp/flutter/bin/cache/dart-sdk/bin/dartfmt",
//              "-w",
//              "/home/philipp/Dokumente/programmierung/events/eventsapp/lib/gen/"
//      ).inheritIO().start().waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
