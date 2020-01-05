package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.exceptions.DomainNotFoundException;
import com.philipp_kehrbusch.gen.webdomain.source.exceptions.InvalidViewAttributePathException;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawView;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawDerivedAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArgumentBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDArtifactBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDClassBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.builders.CDMethodBuilder;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.StringUtil;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;
import com.philipp_kehrbusch.gen.webdomain.util.ViewUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

@GlobalViewTrafo
public class ConverterTrafo {

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings)
          throws WebDomainGeneratorException {

    for (var view : views) {
      if (view.getDerivedAttributes().size() > 0) {
        var imports = ImportUtil.getDefaultImports();
        imports.add(settings.getBasePackage(Targets.BACKEND) + ".domain.*");
        imports.add(settings.getBasePackage(Targets.BACKEND) + ".view.*");
        imports.add("org.springframework.stereotype.Service");
        imports.add("org.springframework.beans.factory.annotation.Autowired");
        var getters = new ArrayList<CDMethod>();

        for (var attr : view.getDerivedAttributes()) {
          getters.add(createInternalGetter(domains, view, attr));
        }

        var name = view.getName() + "Converter";
        elements.add(new WebElement(Targets.BACKEND, name, "converter", imports, new CDArtifactBuilder()
                .name(name)
                .addClass(new CDClassBuilder()
                        .addModifier("public")
                        .addAnnotation("@Service")
                        .name(name)
                        .addMethods(getters)
                        .addMethod(createConvertMethod(domains, view))
                        .build())
                .build()));

      }
    }
  }

  private CDMethod createConvertMethod(RawDomains domains, RawView view) {
    var convert = new CDMethodBuilder()
            .addModifier("public")
            .returnType(view.getName())
            .name("convert")
            .addArguments(view.getViewFroms().entrySet().stream()
                    .map(entry -> new CDArgumentBuilder()
                            .type(entry.getKey())
                            .name(StringUtil.firstLower(entry.getKey()))
                            .build())
                    .collect(Collectors.toList()))
            .build();

    TemplateManager.getInstance().setTemplate(convert, "java/methods/converter/Convert.ftl",
            domains,
            view,
            new ViewUtil(),
            view.getViewFroms().keySet());
    return convert;
  }

  private CDMethod createInternalGetter(RawDomains domains, RawView view, RawDerivedAttribute attribute)
          throws DomainNotFoundException, InvalidViewAttributePathException {

    var pathElements = ViewUtil.getAttributePathSplitted(attribute);
    var attrType = ViewUtil.getAttributePathElementType(domains, view, attribute, pathElements[pathElements.length - 1]);
    var domain = ViewUtil.getAttributeSourceDomain(domains, view, attribute);
    var getterName = "get" + StringUtil.firstUpper(ViewUtil.joinAttributePathElements(attribute));

    var getter = new CDMethodBuilder()
            .addModifier("private")
            .name(getterName)
            .returnType(attrType)
            .addArgument(new CDArgumentBuilder()
                    .type(domain.getName())
                    .name("domain")
                    .build())
            .build();

    TemplateManager.getInstance().setTemplate(getter, "java/methods/converter/GetAttribute.ftl",
            domains,
            view,
            attribute,
            new ViewUtil(),
            new TypeUtil());
    return getter;
  }
}
