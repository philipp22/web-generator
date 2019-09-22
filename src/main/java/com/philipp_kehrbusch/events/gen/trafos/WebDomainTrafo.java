package com.philipp_kehrbusch.events.gen.trafos;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.WebDomainParser;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.DomainTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.MethodUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DomainTrafo
public class WebDomainTrafo {

  @Transform
  public void transform(List<WebDomainParser.DomainContext> domains,
                        List<WebElement> elements,
                        GeneratorSettings settings) {
    domains.forEach(domain -> {
      var backendImports = ImportUtil.getDefaultImports();
      backendImports.add("com.philipp_kehrbusch.web.rte.*");

      elements.add(new WebElement(Targets.BACKEND, domain.name.getText(), "domain", backendImports,
              new CDArtifactBuilder()
                      .name(domain.name.getText())
                      .addClass(new CDClassBuilder()
                              .name(domain.name.getText())
                              .addModifier("public")
                              .addInterface("IDomain")
                              .addAnnotations(domain.ANNOTATION().stream()
                                      .map(annotation -> annotation.getSymbol().getText())
                                      .collect(Collectors.toList()))
                              .addAttributes(domain.attribute().stream()
                                      .map(attr -> new CDAttributeBuilder()
                                              .name(attr.name.getText())
                                              .type(attr.type.getText())
                                              .addModifier("private")
                                              .addAnnotations(attr.ANNOTATION().stream()
                                                      .map(annotation -> annotation.getSymbol().getText())
                                                      .collect(Collectors.toList()))
                                              .optional(attr.optional != null && attr.optional.getText().equals("?"))
                                              .build())
                                      .collect(Collectors.toList()))
                              .addMethods(domain.attribute().stream()
                                      .map(MethodUtil::createGetter)
                                      .collect(Collectors.toList()))
                              .addMethods(domain.attribute().stream()
                                      .map(MethodUtil::createSetter)
                                      .collect(Collectors.toList()))
                              .addConstructor(createConstructor(domain))
                              .build())
                      .build()));

      elements.add(new WebElement(Targets.FRONTEND, domain.name.getText(), "domain", new ArrayList<>(),
              new CDArtifactBuilder()
                      .name(domain.name.getText())
                      .addInterface(new CDInterfaceBuilder()
                              .name(domain.name.getText())
                              .addAttributes(domain.attribute().stream()
                                      .map(attr -> createFrontendAttribute(attr, domains))
                                      .collect(Collectors.toList()))
                              .addModifier("export")
                              .build())
                      .build()));
    });
  }

  private CDConstructor createConstructor(WebDomainParser.DomainContext domain) {
    var res = new CDConstructorBuilder()
            .name(domain.name.getText())
            .addModifier("public")
            .addArguments(domain.attribute().stream()
                    .map(attr -> new CDArgumentBuilder()
                            .type(attr.type.getText())
                            .name(attr.name.getText())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }

  private CDAttribute createFrontendAttribute(WebDomainParser.AttributeContext attr,
                                              List<WebDomainParser.DomainContext> domains) {
    if (isTypeDomain(attr.type.getText(), domains)) {
      return new CDAttributeBuilder()
              .name(attr.name.getText() + "Id")
              .type("number")
              .build();
    } else {
      return new CDAttributeBuilder()
              .name(attr.name.getText())
              .type(TypeUtil.javaToTypescript(attr.type.getText()))
              .build();
    }
  }

  private boolean isTypeDomain(String type, List<WebDomainParser.DomainContext> domains) {
    return domains.stream()
            .map(domain -> domain.name.getText())
            .collect(Collectors.toList())
            .contains(type);
  }
}
