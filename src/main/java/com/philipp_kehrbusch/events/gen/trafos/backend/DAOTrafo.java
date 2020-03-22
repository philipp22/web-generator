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

import java.util.List;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class DAOTrafo {

  @Transform
  public void transform(RawDomains domains, WebElements elements, GeneratorSettings settings) {
    domains.stream()
            .filter(domain -> !TrafoUtils.hasAnnotation(domain, "NoDAO"))
            .forEach(domain -> {
              var imports = ImportUtil.getDefaultImports();
              imports.add(ImportPaths.getDomainImport(settings.getBasePackage(Targets.BACKEND)));
              imports.add(settings.getBasePackage(Targets.BACKEND) + ".rte.*");
              imports.add(settings.getBasePackage(Targets.BACKEND) + ".rte.exceptions.*");
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
                                      .addMethods(createGetByMethods(domain, domains))
                                      .build())
                              .build()));
            });
  }

  private List<CDMethod> createGetByMethods(RawDomain domain, RawDomains domains) {
    var res = domain.getAttributes().stream()
            .filter(attr -> TrafoUtils.hasAnnotation(attr, "GetOneBy"))
            .map(attr -> {
              var name = StringUtil.firstUpper(attr.getName());
              if (TrafoUtils.isTypeDomain(attr.getType(), domains)) {
                name += "Id";
              }
              var type = TrafoUtils.isTypeDomain(attr.getType(), domains) ? "long" : attr.getType();

              return new CDMethodBuilder()
                      .name("findBy" + name)
                      .returnType("Optional<" + domain.getName() + ">")
                      .addArgument(new CDArgumentBuilder()
                              .type(type)
                              .name(attr.getName())
                              .build())
                      .build();
            })
            .collect(Collectors.toList());

    res.addAll(domain.getAttributes().stream()
            .filter(attr -> TrafoUtils.hasAnnotation(attr, "GetAllBy"))
            .map(attr -> {
              var name = StringUtil.firstUpper(attr.getName());
              var type = attr.getType();

              return new CDMethodBuilder()
                      .name("findBy" + name)
                      .returnType("List<" + domain.getName() + ">")
                      .addArgument(new CDArgumentBuilder()
                              .type(type)
                              .name(attr.getName())
                              .build())
                      .build();
            })
            .collect(Collectors.toList()));

    return res;
  }
}
