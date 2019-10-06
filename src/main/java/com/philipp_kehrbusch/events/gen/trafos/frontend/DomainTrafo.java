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
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GlobalTrafo(includeAnnotated = "Domain")
public class DomainTrafo {

  @Transform
  public void transform(RawDomains domains, WebElements elements, GeneratorSettings settings) {
    domains.forEach(domain -> {
      var imports = new ArrayList<String>();
      imports.add("import {IDomain} from '@core/domain'");
      imports.addAll(domain.getAttributes().stream()
              .map(RawAttribute::getType)
              .filter(type -> TrafoUtils.isTypeDomain(type, domains))
              .map(type -> {
                type = TrafoUtils.getPrimitiveType(type);
                return String.format("import {%s} from '@domain/%s'",
                        type,
                        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, type));
              })
              .collect(Collectors.toSet()));

      var name = domain.getName();
      elements.add(new WebElement(Targets.FRONTEND, name, "domain", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addInterface(new CDInterfaceBuilder()
                              .name(name)
                              .addModifier("export")
                              .addSuperInterface("IDomain")
                              .addAttributes(domain.getAttributes().stream()
                                      .map(attr -> createAttribute(attr, domains))
                                      .collect(Collectors.toList()))
                              .build())
                      .build()));
    });
  }

  private CDAttribute createAttribute(RawAttribute attribute, RawDomains domains) {
    if (TrafoUtils.isTypeDomain(attribute.getType(), domains)) {
      return new CDAttributeBuilder()
              .type(TrafoUtils.isTypeCollection(attribute.getType()) ? "number[]" : "number")
              .name(attribute.getName())
              .optional(attribute.isOptional())
              .build();
    } else {
      return new CDAttributeBuilder()
              .type(TypeUtil.javaToTypescript(attribute.getType()))
              .name(attribute.getName())
              .optional(attribute.isOptional())
              .build();
    }
  }
}
