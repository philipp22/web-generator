package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.events.gen.trafos.backend.util.ImportPaths;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
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
public class DomainTrafo {

  @Transform
  public void transform(List<CDClass> allDomains, List<WebElement> elements, GeneratorSettings settings) {
    var domains = TrafoUtils.getDomains(allDomains);
    var imports = ImportUtil.getDefaultImports();
    imports.add(ImportPaths.getRTEImport());
    imports.add("javax.persistence.*");

    domains.forEach(domain -> elements.add(new WebElement(Targets.BACKEND, domain.getName(), "domain", imports,
            new CDArtifactBuilder()
                    .name(domain.getName())
                    .addClass(new CDClassBuilder()
                            .addModifier("public")
                            .name(domain.getName())
                            .addAnnotation("@Entity")
                            .addInterface("IDomain<" + domain.getName() + ">")
                            .addAttributes(domain.getAttributes().stream()
                                    .map(attr -> new CDAttributeBuilder()
                                            .type(attr.getType())
                                            .name(attr.getName())
                                            .build())
                                    .collect(Collectors.toList()))
                            .addAttribute(new CDAttributeBuilder()
                                    .type("long")
                                    .name("id")
                                    .addAnnotation("@Id")
                                    .addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)")
                                    .build())
                            .addMethod(MethodUtil.createGetter("long", "id"))
                            .addMethod(MethodUtil.createSetter("long", "id"))
                            .addMethods(domain.getMethods())
                            .addMethod(createMergeMethod(domain, domains))
                            .addConstructor(createConstructor(domain))
                            .addConstructor(new CDConstructorBuilder()
                                    .name(domain.getName())
                                    .build())
                            .build())
                    .build())));
  }

  private CDConstructor createConstructor(CDClass domain) {
    var res = new CDConstructorBuilder()
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

  private CDMethod createMergeMethod(CDClass domain, List<CDClass> domains) {
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
}
