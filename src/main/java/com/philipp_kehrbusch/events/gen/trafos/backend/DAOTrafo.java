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
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.List;
import java.util.stream.Collectors;

@GlobalTrafo
public class DAOTrafo {

  @Transform
  public void transform(RawDomains allDomains, WebElements elements, GeneratorSettings settings) {
    var domains = TrafoUtils.getDomains(allDomains);
    domains.stream()
            .filter(domain -> !TrafoUtils.hasAnnotation(domain, "NoDAO"))
            .forEach(domain -> {
              var imports = ImportUtil.getDefaultImports();
              imports.add(ImportPaths.getDomainImport(settings.getBasePackage(Targets.BACKEND)));
              imports.add(ImportPaths.getRTEImport());
              imports.add("org.springframework.data.jpa.repository.JpaRepository");

              var name = domain.getName() + "DAO";
              elements.add(new WebElement(Targets.BACKEND, name, "dao", imports,
                      new CDArtifactBuilder()
                              .name(name)
                              .addInterface(new CDInterfaceBuilder()
                                      .addModifier("public")
                                      .name(name)
                                      .addSuperInterface("IDAO")
                                      .addSuperInterface("JpaRepository<" + domain.getName() + ", Long>")
                                      .addMethods(createGetByMethods(domain))
                                      .build())
                              .build()));
            });
  }

  private List<CDMethod> createGetByMethods(RawDomain domain) {
    return domain.getAttributes().stream()
            .filter(attr -> TrafoUtils.hasAnnotation(attr, "GetOneBy"))
            .map(attr -> new CDMethodBuilder()
                    .name("findBy" + StringUtil.firstUpper(attr.getName()))
                    .returnType("Optional<" + domain.getName() + ">")
                    .addArgument(new CDArgumentBuilder()
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .build())
            .collect(Collectors.toList());
  }
}
