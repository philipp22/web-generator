package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.events.gen.trafos.backend.util.ImportPaths;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArgumentBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDMethodBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalDomainTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class BLTrafo {

  @Transform
  public void transform(RawDomains allDomains, WebElements elements, GeneratorSettings settings) {
    allDomains.stream()
            .filter(domain -> !TrafoUtils.hasAnnotation(domain, "NoBL"))
            .forEach(domain -> {
              var imports = ImportUtil.getDefaultImports();
              imports.add(ImportPaths.getDomainImport(settings.getBasePackage(Targets.BACKEND)));
              imports.add(ImportPaths.getViewImport(settings.getBasePackage(Targets.BACKEND)));
              imports.add("com.philipp_kehrbusch.web.rte.exceptions.*");

              var name = "I" + domain.getName() + "BL";
              elements.add(new WebElement(Targets.BACKEND, name, "bl", imports,
                      new CDArtifactBuilder()
                              .name(name)
                              .addInterface(new CDInterfaceBuilder()
                                      .addModifier("public")
                                      .name(name)
                                      .addMethods(domain.getRestMethods().stream()
                                              .map(restMethod -> {
                                                var builder = new CDMethodBuilder()
                                                        .name(restMethod.getName())
                                                        .returnType(restMethod.getReturnType())
                                                        .addException("PermissionDeniedException")
                                                        .addException("ServerErrorException")
                                                        .addException("ResourceNotFoundException")
                                                        .addException("ValidationException");

                                                if (!StringUtils.isEmpty(restMethod.getBodyType())) {
                                                  builder.addArgument(new CDArgumentBuilder()
                                                          .type(restMethod.getBodyType())
                                                          .name(restMethod.getBodyTypeName())
                                                          .build());
                                                }
                                                builder.addArguments(restMethod.getRouteVariables().entrySet().stream()
                                                        .map(entry -> new CDArgumentBuilder()
                                                                .type(entry.getValue())
                                                                .name(entry.getKey())
                                                                .build())
                                                        .collect(Collectors.toList()));
                                                return builder.build();
                                              })
                                              .collect(Collectors.toList()))
                                      .build())
                              .build()));
            });
  }
}
