package com.philipp_kehrbusch.events.gen.trafos.client;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.events.gen.trafos.backend.util.ImportPaths;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawView;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GlobalDomainTrafo
public class ClientDomainTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings) {
    var imports = ImportUtil.getDefaultImports();
    imports.add(ImportPaths.getRTEImport());

    domains.forEach(domain -> elements.add(new WebElement(Targets.CLIENT_JAVA, domain.getName(), "domain", imports,
            new CDArtifactBuilder()
                    .name(domain.getName())
                    .addClass(createDomainClass(domain, domains, settings.getTargets().get(Targets.BACKEND)))
                    .build())));

    views.forEach(view -> elements.add(new WebElement(Targets.CLIENT_JAVA, view.getName(), "domain", imports,
            new CDArtifactBuilder()
                    .name(view.getName())
                    .addClass(createViewClass(view, domains, settings.getTargets().get(Targets.BACKEND)))
                    .build())));
  }

  private CDClass createDomainClass(RawDomain domain, RawDomains domains, Target target) {
    var builder = new CDClassBuilder()
            .addModifier("public")
            .name(domain.getName())
            .addInterface("IDomain<" + domain.getName() + ">")
            .addAttributes(domain.getAttributes().stream()
                    .map(attr -> new CDAttributeBuilder()
                            .addModifier("private")
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .addMethods(domain.getAttributes().stream()
                    .map(MethodUtil::createGetter)
                    .collect(Collectors.toList()))
            .addMethods(domain.getAttributes().stream()
                    .map(MethodUtil::createSetter)
                    .collect(Collectors.toList()))
            .addConstructor(createConstructor(domain))
            .addConstructor(new CDConstructorBuilder()
                    .addModifier("protected")
                    .name(domain.getName())
                    .build());

    if (!TrafoUtils.hasHandcodedJavaClass(domain, target)) {
      builder
              .addAttribute(new CDAttributeBuilder()
                      .addModifier("private")
                      .type("long")
                      .name("id")
                      .build())
              .addMethod(MethodUtil.createGetter("long", "id"))
              .addMethod(MethodUtil.createSetter("long", "id"));
    } else {
      builder.addModifier("abstract");
    }

    return builder.build();
  }

  private CDClass createViewClass(RawView view, RawDomains domains, Target target) {
    var builder = new CDClassBuilder()
            .addModifier("public")
            .name(view.getName())
            .addInterface("IDomain<" + view.getName() + ">")
            .addAttributes(view.getAttributes().stream()
                    .map(attr -> new CDAttributeBuilder()
                            .addModifier("private")
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .addMethods(view.getAttributes().stream()
                    .map(MethodUtil::createGetter)
                    .collect(Collectors.toList()))
            .addMethods(view.getAttributes().stream()
                    .map(MethodUtil::createSetter)
                    .collect(Collectors.toList()))
            .addConstructor(createConstructor(view))
            .addConstructor(new CDConstructorBuilder()
                    .addModifier("protected")
                    .name(view.getName())
                    .build());

    if (!TrafoUtils.hasHandcodedJavaClass(view, target)) {
      builder
              .addAttribute(new CDAttributeBuilder()
                      .addModifier("private")
                      .type("long")
                      .name("id")
                      .build())
              .addMethod(MethodUtil.createGetter("long", "id"))
              .addMethod(MethodUtil.createSetter("long", "id"));
    } else {
      builder.addModifier("abstract");
    }

    return builder.build();
  }

  private CDConstructor createConstructor(RawDomain domain) {
    var res = new CDConstructorBuilder()
            .addModifier("public")
            .name(domain.getName())
            .addArguments(domain.getAttributes().stream()
                    .filter(attr -> !attr.getName().equals("id"))
                    .map(attr -> new CDArgumentBuilder()
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }

  private CDConstructor createConstructor(RawView view) {
    var res = new CDConstructorBuilder()
            .addModifier("public")
            .name(view.getName())
            .addArguments(view.getAttributes().stream()
                    .filter(attr -> !attr.getName().equals("id"))
                    .map(attr -> new CDArgumentBuilder()
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }
}
