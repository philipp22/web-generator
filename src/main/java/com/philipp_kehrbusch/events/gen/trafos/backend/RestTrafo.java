package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
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
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;

import java.util.Arrays;
import java.util.List;

@GlobalTrafo
public class RestTrafo {

  @Transform
  public void transform(List<CDClass> allDomains, List<WebElement> elements, GeneratorSettings settings) {
    var domains = TrafoUtils.getDomains(allDomains);
    var imports = ImportUtil.getDefaultImports();
    imports.add("org.springframework.web.bind.annotation.*");
    imports.add("com.philipp_kehrbusch.web.rte.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".dao.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".dto.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".domain.*");
    imports.add(settings.getBasePackage(Targets.BACKEND) + ".converter.*");

    domains.forEach(domain -> {
      var name = domain.getName() + "Controller";
      elements.add(new WebElement(Targets.BACKEND, name, "rest", imports,
              new CDArtifactBuilder()
                      .name(name)
                      .addClass(new CDClassBuilder()
                              .name(name)
                              .addModifier("public")
                              .superClass(String.format("AbstractRestController<%s, %s, %s, %s>",
                                      domain.getName(),
                                      domain.getName() + "DTO",
                                      domain.getName() + "DAO",
                                      domain.getName() + "Converter"))
                              .addAnnotation("@RestController")
                              .addAnnotation("@RequestMapping(\"/api/v1/" + StringUtil.firstLower(domain.getName()) + "\")")
                              .addConstructor(createConstructor(domain))
                              .addMethods(createMethods(domain))
                              .build())
                      .build()));
    });
  }

  private CDConstructor createConstructor(CDClass domain) {
    var constructor = new CDConstructorBuilder()
            .name(domain.getName() + "Controller")
            .addModifier("public")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName() + "DAO")
                    .name("dao")
                    .build())
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName() + "Converter")
                    .name("converter")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(constructor, "java/methods/SuperConstructor.ftl",
            constructor);
    return constructor;
  }

  private List<CDMethod> createMethods(CDClass domain) {
    var create = new CDMethodBuilder()
            .addModifier("public")
            .name("create")
            .returnType(domain.getName() + "DTO")
            .addAnnotation("@Override")
            .addAnnotation("@PostMapping")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName() + "DTO")
                    .name("dto")
                    .addAnnotation("@RequestBody")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(create, "java/methods/rest/Create.ftl");

    var get = new CDMethodBuilder()
            .addModifier("public")
            .name("get")
            .returnType(domain.getName() + "DTO")
            .addAnnotation("@Override")
            .addAnnotation("@GetMapping(\"/{id}\")")
            .addArgument(new CDArgumentBuilder()
                    .type("long")
                    .name("id")
                    .addAnnotation("@PathVariable(\"id\")")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(get, "java/methods/rest/Get.ftl");

    var getAll = new CDMethodBuilder()
            .addModifier("public")
            .name("getAll")
            .returnType("List<" + domain.getName() + "DTO>")
            .addAnnotation("@Override")
            .addAnnotation("@GetMapping")
            .addArgument(new CDArgumentBuilder()
                    .type("Integer")
                    .name("page")
                    .addAnnotation("@RequestParam(\"page\")")
                    .build())
            .addArgument(new CDArgumentBuilder()
                    .type("Integer")
                    .name("count")
                    .addAnnotation("@RequestParam(\"count\")")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(getAll, "java/methods/rest/GetAll.ftl");

    var update = new CDMethodBuilder()
            .addModifier("public")
            .name("update")
            .returnType(domain.getName() + "DTO")
            .addAnnotation("@Override")
            .addAnnotation("@PutMapping")
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName() + "DTO")
                    .name("dto")
                    .addAnnotation("@RequestBody")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(update, "java/methods/rest/Update.ftl");

    var delete = new CDMethodBuilder()
            .addModifier("public")
            .name("delete")
            .returnType("void")
            .addAnnotation("@Override")
            .addAnnotation("@DeleteMapping(\"/{id}\")")
            .addArgument(new CDArgumentBuilder()
                    .type("long")
                    .name("id")
                    .addAnnotation("@PathVariable(\"id\")")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(delete, "java/methods/rest/Delete.ftl");

    return Arrays.asList(create, get, getAll, update, delete);
  }
}
