package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.MethodUtil;

import java.util.stream.Collectors;

@GlobalTrafo(includeAnnotated = {"Domain", "DTO"})
public class DTOTrafo {

  private CDAttribute createAttribute(RawAttribute attribute, RawDomains domains) {
    if (TrafoUtils.isTypeDomain(attribute.getType(), domains)) {
      return new CDAttributeBuilder()
              .type(TrafoUtils.isTypeCollection(attribute.getType()) ?
                      TrafoUtils.getLongCollection(attribute.getType()) : "long")
              .name(attribute.getName())
              .build();
    } else {
      return new CDAttributeBuilder().type(attribute.getType()).name(attribute.getName()).build();
    }
  }

  private CDMethod createGetter(RawAttribute attribute, RawDomains domains) {
    var type = TrafoUtils.isTypeCollection(attribute.getType()) ?
            TrafoUtils.getLongCollection(attribute.getType()) : "long";
    return MethodUtil.createGetter(
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? type : attribute.getType(),
            attribute.getName());
  }

  private CDMethod createSetter(RawAttribute attribute, RawDomains domains) {
    var type = TrafoUtils.isTypeCollection(attribute.getType()) ?
            TrafoUtils.getLongCollection(attribute.getType()) : "long";
    return MethodUtil.createSetter(
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? type : attribute.getType(),
            attribute.getName());
  }

  @Transform
  public void transform(RawDomains domains, WebElements elements) {
    var imports = ImportUtil.getDefaultImports();
    imports.add("com.philipp_kehrbusch.web.rte.*");
    domains.forEach(domain -> {
      var name = domain.getName() + "DTO";
      elements.add(new WebElement(Targets.BACKEND, name, "dto", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addClass(new CDClassBuilder()
                              .name(name)
                              .addModifier("public")
                              .addInterface("IDTO")
                              .addAttributes(domain.getAttributes().stream()
                                      .map(attr -> createAttribute(attr, domains))
                                      .collect(Collectors.toList()))
                              .addMethods(domain.getAttributes().stream()
                                      .map(attr -> createGetter(attr, domains))
                                      .collect(Collectors.toList()))
                              .addMethods(domain.getAttributes().stream()
                                      .map(attr -> createSetter(attr, domains))
                                      .collect(Collectors.toList()))
                              .addConstructor(createConstructor(domain, domains))
                              .addAttribute(new CDAttributeBuilder()
                                      .type("long")
                                      .name("id")
                                      .build())
                              .addMethod(MethodUtil.createGetter("long", "id"))
                              .addMethod(MethodUtil.createSetter("long", "id"))
                              .build())
                      .build()));
    });
  }

  private CDConstructor createConstructor(RawDomain domain, RawDomains domains) {
    var res = new CDConstructorBuilder()
            .addModifier("public")
            .name(domain.getName() + "DTO")
            .addArguments(domain.getAttributes().stream()
                    .map(attr -> new CDArgumentBuilder()
                            .type(TrafoUtils.isTypeDomain(attr.getType(), domains) ?
                                    (TrafoUtils.isTypeCollection(attr.getType()) ?
                                            TrafoUtils.getLongCollection(attr.getType()) : "long") : attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }
}
