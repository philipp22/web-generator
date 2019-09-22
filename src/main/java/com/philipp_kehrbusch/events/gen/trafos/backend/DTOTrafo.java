package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.MethodUtil;

import java.util.List;
import java.util.stream.Collectors;

@GlobalTrafo
public class DTOTrafo {

  private CDAttribute createAttribute(CDAttribute attribute, List<CDClass> domains) {
    if (TrafoUtils.isTypeDomain(attribute.getType(), domains)) {
      return new CDAttributeBuilder().type("long").name(attribute.getName() + "Id").build();
    } else {
      return new CDAttributeBuilder().type(attribute.getType()).name(attribute.getName()).build();
    }
  }

  private CDMethod createGetter(CDAttribute attribute, List<CDClass> domains) {
    return MethodUtil.createGetter(
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? "long" : attribute.getType(),
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? attribute.getName() + "Id" : attribute.getName());
  }

  private CDMethod createSetter(CDAttribute attribute, List<CDClass> domains) {
    return MethodUtil.createSetter(
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? "long" : attribute.getType(),
            TrafoUtils.isTypeDomain(attribute.getType(), domains) ? attribute.getName() + "Id" : attribute.getName()
    );
  }

  @Transform
  public void transform(List<CDClass> allDomains, List<WebElement> elements, GeneratorSettings settings) {
    var domains = TrafoUtils.getDomains(allDomains);
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

  private CDConstructor createConstructor(CDClass domain, List<CDClass> domains) {
    var res = new CDConstructorBuilder()
            .addModifier("public")
            .name(domain.getName() + "DTO")
            .addArguments(domain.getAttributes().stream()
                    .map(attr -> new CDArgumentBuilder()
                            .type(TrafoUtils.isTypeDomain(attr.getType(), domains) ? "long" : attr.getType())
                            .name(TrafoUtils.isTypeDomain(attr.getType(), domains) ? attr.getName() + "Id" : attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }
}
