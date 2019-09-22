package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDAttributeBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SingleTrafo(includeAnnotated = "Domain")
public class DomainTrafo {

  @Transform
  public void transform(CDClass domain, List<WebElement> elements, GeneratorSettings settings) {
    var imports = new ArrayList<String>();
    imports.add("import {IDomain} from '@core/domain'");
    var name = domain.getName();
    elements.add(new WebElement(Targets.FRONTEND, name, "domain", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addInterface(new CDInterfaceBuilder()
                            .name(name)
                            .addModifier("export")
                            .addSuperInterface("IDomain")
                            .addAttributes(domain.getAttributes().stream()
                                    .map(attr -> new CDAttributeBuilder()
                                            .type(TypeUtil.javaToTypescript(attr.getType()))
                                            .name(attr.getName())
                                            .optional(attr.isOptional())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .build()));
  }
}
