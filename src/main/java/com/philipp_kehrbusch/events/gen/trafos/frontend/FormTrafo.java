package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDAttributeBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDInterfaceBuilder;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@SingleTrafo(includeAnnotated = "Form")
public class FormTrafo {

  @Transform
  public void transform(RawDomain domain, WebElements elements) {
    var name = domain.getName() + "FormValue";
    var imports = new ArrayList<String>();

    elements.add(new WebElement(Targets.FRONTEND, name, "forms", imports,
            new CDArtifactBuilder()
                    .name(name)
                    .addInterface(new CDInterfaceBuilder()
                            .name(name)
                            .addModifier("export")
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
