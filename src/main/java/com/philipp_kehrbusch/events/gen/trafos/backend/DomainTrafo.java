package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.events.gen.trafos.backend.util.ImportPaths;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.Target;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawDomain;
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
public class DomainTrafo {

  @Transform
  public void transform(RawDomains domains, WebElements elements, GeneratorSettings settings) {
    var imports = ImportUtil.getDefaultImports();
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".rte.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".rte.exceptions.*");
    imports.add("javax.persistence.*");
    imports.add("org.hibernate.annotations.Type");

    domains.forEach(domain -> elements.add(new WebElement(Targets.BACKEND, domain.getName(), "domain", imports,
            new CDArtifactBuilder()
                    .name(domain.getName())
                    .addClass(createDomainClass(domain, domains, settings.getTargets().get(Targets.BACKEND)))
                    .build())));
  }

  private CDClass createDomainClass(RawDomain domain, RawDomains domains, Target target) {
    var builder = new CDClassBuilder()
            .addModifier("public")
            .name(domain.getName())
            .addAnnotation("@Entity")
            .addInterface("IDomain<" + domain.getName() + ">")
            .addAttributes(domain.getAttributes().stream()
                    .map(attr -> new CDAttributeBuilder()
                            .addModifier("private")
                            .type(attr.getType())
                            .name(attr.getName())
                            .addAnnotations(createAttributeAnnotations(attr))
                            .build())
                    .collect(Collectors.toList()))
            .addMethods(domain.getAttributes().stream()
                    .map(MethodUtil::createGetter)
                    .collect(Collectors.toList()))
            .addMethods(domain.getAttributes().stream()
                    .map(MethodUtil::createSetter)
                    .collect(Collectors.toList()))
//            .addMethod(createMergeMethod(domain, domains))
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
                      .addAnnotation("@Id")
                      .addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)")
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

  private CDMethod createMergeMethod(RawDomain domain, RawDomains domains) {
    var res = new CDMethodBuilder()
            .addModifier("public")
            .returnType("void")
            .name("merge")
            .addAnnotation("@Override")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName())
                    .name("other")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/domain/Merge.ftl",
            domain, domains, new TrafoUtils());
    return res;
  }

  private List<String> createAttributeAnnotations(RawAttribute attr) {
    var res = new ArrayList<String>();

    if (TrafoUtils.hasAnnotation(attr, "OneToOne")) {
      res.add("@OneToOne");
    } else if (TrafoUtils.hasAnnotation(attr, "ManyToOne")) {
      res.add("@ManyToOne");
    } else if (TrafoUtils.hasAnnotation(attr, "OneToMany")) {
      res.add("@OneToMany");
    } else if (TrafoUtils.hasAnnotation(attr, "ManyToMany")) {
      res.add("@ManyToMany");
    } else if (TrafoUtils.hasAnnotation(attr, "ElementCollection")) {
      res.add("@ElementCollection");
    }

    if (TrafoUtils.hasAnnotation(attr, "Text")) {
      res.add("@Type(type = \"text\")");
    }

    return res;
  }
}
