package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDAttributeBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class DomainTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings) {
    domains.forEach(domain -> {
      var imports = domain.getAttributes().stream()
              .map(RawAttribute::getType)
              .filter(type -> TrafoUtils.isTypeDomain(TrafoUtils.getPrimitiveType(type), domains) ||
                      TrafoUtils.isTypeView(TrafoUtils.getPrimitiveType(type), views))
              .map(type -> {
                type = TrafoUtils.getPrimitiveType(type);
                return String.format("import {%s} from './%s'",
                        type,
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, type));
              }).distinct().collect(Collectors.toCollection(ArrayList::new));

      var name = domain.getName();
      elements.add(new WebElement(Targets.FRONTEND, name, "domain", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addInterface(new CDInterfaceBuilder()
                              .name(name)
                              .addModifier("export")
                              .addAttribute(new CDAttributeBuilder()
                                      .type("number")
                                      .name("id")
                                      .build())
                              .addAttributes(domain.getAttributes().stream()
                                      .map(this::createAttribute)
                                      .collect(Collectors.toList()))
                              .build())
                      .build()));
    });

    elements.add(new WebElement(Targets.FRONTEND, "index", "domain", new ArrayList<>(),
            new CDArtifactBuilder()
                    .name("index")
                    .addExports(domains.stream()
                            .map(domain -> "* from './" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()) + "'")
                            .collect(Collectors.toList()))
                    .addExports(views.stream()
                            .map(view -> "* from './" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, view.getName()) + "'")
                            .collect(Collectors.toList()))
                    .build()));
  }

  private CDAttribute createAttribute(RawAttribute attribute) {
//    if (TrafoUtils.isTypeDomain(attribute.getType(), domains)) {
//      return new CDAttributeBuilder()
//              .type(TrafoUtils.isTypeCollection(attribute.getType()) ? "number[]" : "number")
//              .name(attribute.getName())
//              .optional(attribute.isOptional())
//              .build();
//    } else {
    return new CDAttributeBuilder()
            .type(TypeUtil.javaToTypescript(attribute.getType()))
            .name(attribute.getName())
            .optional(attribute.isOptional())
            .build();
//    }
  }
}
