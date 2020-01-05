package com.philipp_kehrbusch.events.gen.trafos.backend;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.events.gen.trafos.backend.util.ImportPaths;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawDerivedAttribute;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawView;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.ImportUtil;
import com.philipp_kehrbusch.gen.webdomain.util.MethodUtil;
import com.philipp_kehrbusch.gen.webdomain.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GlobalViewTrafo
public class ViewTrafo {

  private CDAttribute createAttribute(RawAttribute attribute, RawDomains domains) {
    if (TrafoUtils.isTypeDomain(attribute.getType(), domains)) {
      return new CDAttributeBuilder()
              .type(TrafoUtils.isTypeCollection(attribute.getType()) ?
                      TrafoUtils.getLongCollection(attribute.getType()) : "long")
              .name(attribute.getName())
              .addModifier("private")
              .build();
    } else {
      return new CDAttributeBuilder()
              .type(attribute.getType())
              .name(attribute.getName())
              .addModifier("private")
              .build();
    }
  }

  private CDMethod createGetter(CDAttribute attribute, RawDomains domains) {
    return MethodUtil.createGetter(
            attribute.getType(),
            attribute.getName());
  }

  private CDMethod createSetter(CDAttribute attribute, RawDomains domains) {
    return MethodUtil.createSetter(
            attribute.getType(),
            attribute.getName());
  }

  @Transform
  public void transform(RawDomains domains, RawViews views, WebElements elements, GeneratorSettings settings)
          throws WebDomainGeneratorException {
    for (var view : views) {

      var imports = ImportUtil.getDefaultImports();
      imports.add(ImportPaths.getRTEImport());
      imports.add(settings.getBasePackage(Targets.BACKEND) + ".domain.*");

      var name = view.getName();
      var builder = new CDClassBuilder()
              .name(name)
              .addModifier("public");

      var attributes = new ArrayList<CDAttribute>();

      if (view.getDerivedAttributes().size() > 0) {
        for (var attr : view.getDerivedAttributes()) {
          var cdAttr = createViewAttribute(domains, view, attr);
          attributes.add(cdAttr);
        }
      }

      attributes.addAll(view.getAttributes().stream()
              .map(attr -> new CDAttributeBuilder()
                      .addModifier("private")
                      .type(attr.getType())
                      .name(attr.getName())
                      .build())
              .collect(Collectors.toList()));
      builder.addAttributes(attributes);

      builder.addConstructor(createConstructor(name, attributes));
      builder.addMethods(attributes.stream().map(attr -> createGetter(attr, domains)).collect(Collectors.toList()));
      builder.addMethods(attributes.stream().map(attr -> createSetter(attr, domains)).collect(Collectors.toList()));

      elements.add(new WebElement(Targets.BACKEND, name, "view", imports, new CDArtifactBuilder()
              .name(name)
              .addClass(builder.build())
              .build()));
    }
  }

  private CDConstructor createConstructor(String name, List<CDAttribute> attributes) {
    var res = new CDConstructorBuilder()
            .addModifier("public")
            .name(name)
            .addArguments(attributes.stream()
                    .map(attr -> new CDArgumentBuilder()
                            .type(attr.getType())
                            .name(attr.getName())
                            .build())
                    .collect(Collectors.toList()))
            .build();
    TemplateManager.getInstance().setTemplate(res, "java/methods/AssigningConstructor.ftl", res);
    return res;
  }

  private CDAttribute createViewAttribute(RawDomains domains, RawView view, RawDerivedAttribute attr)
          throws WebDomainGeneratorException {
    var attrName = ViewUtil.getAttributeName(attr);
    var pathElements = ViewUtil.getAttributePathSplitted(attr);
    var attrType = ViewUtil.getAttributePathElementType(domains, view, attr, pathElements[pathElements.length - 1]);

    return new CDAttributeBuilder()
            .addModifier("private")
            .name(attrName)
            .type(attrType)
            .build();
  }
}
