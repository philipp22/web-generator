package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.GlobalTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.RawDomains;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;
import com.philipp_kehrbusch.gen.webdomain.trafos.WebElements;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.stream.Collectors;

@GlobalTrafo
public class ConverterTrafo {

  @Transform
  public void transform(RawDomains allDomains, WebElements elements, GeneratorSettings settings) {
    var domains = TrafoUtils.getDomains(allDomains);
    domains.forEach(domain -> {
      var imports = ImportUtil.getDefaultImports();
      imports.add("com.philipp_kehrbusch.web.rte.*");
      imports.add(settings.getBasePackage(Targets.BACKEND) + ".domain.*");
      imports.add(settings.getBasePackage(Targets.BACKEND) + ".dto.*");
      imports.add("org.springframework.stereotype.Service");
      imports.add("org.springframework.beans.factory.annotation.Autowired");

      // add DAOs
      imports.addAll(domain.getAttributes().stream()
              .filter(attr -> TrafoUtils.isTypeDomain(attr.getType(), domains))
              .map(attr -> settings.getBasePackage(Targets.BACKEND) + ".dao." +
                      TrafoUtils.getPrimitiveType(attr.getType()) + "DAO")
              .collect(Collectors.toList()));

      var name = domain.getName() + "Converter";
      elements.add(new WebElement(Targets.BACKEND, name, "converter", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addClass(new CDClassBuilder()
                              .addModifier("public")
                              .name(name)
                              .addAnnotation("@Service")
                              .addInterface("IConverter<" + domain.getName() + ", " + domain.getName() + "DTO>")
                              .addAttributes(domain.getAttributes().stream()
                                      .filter(attr -> TrafoUtils.isTypeDomain(attr.getType(), domains))
                                      .map(attr -> TrafoUtils.getPrimitiveType(attr.getType()))
                                      .distinct()
                                      .map(type -> new CDAttributeBuilder()
                                              .type(type + "DAO")
                                              .addModifier("private")
                                              .name(StringUtil.firstLower(type) + "DAO")
                                              .addAnnotation("@Autowired")
                                              .build())
                                      .collect(Collectors.toList()))
                              .addMethod(createFromMethod(domain, domains))
                              .addMethod(createToMethod(domain, domains))
                              .build())
                      .build()));
    });
  }

  private CDMethod createFromMethod(RawDomain domain, RawDomains domains) {
    var res = new CDMethodBuilder()
            .addModifier("public")
            .returnType(domain.getName())
            .name("fromDTO")
            .addAnnotation("@Override")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName() + "DTO")
                    .name("dto")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/converter/FromDTO.ftl",
            domain, domains, new TrafoUtils());
    return res;
  }

  private CDMethod createToMethod(RawDomain domain, RawDomains domains) {
    var res = new CDMethodBuilder()
            .addModifier("public")
            .returnType(domain.getName() + "DTO")
            .name("toDTO")
            .addAnnotation("@Override")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName())
                    .name("domain")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/converter/ToDTO.ftl",
            domain, domains, new TrafoUtils());
    return res;
  }
}
