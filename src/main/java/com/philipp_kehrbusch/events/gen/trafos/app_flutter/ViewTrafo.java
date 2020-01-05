package com.philipp_kehrbusch.events.gen.trafos.app_flutter;

import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.events.gen.TrafoUtils;
import com.philipp_kehrbusch.gen.webdomain.FileNameResolvers;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.source.domain.RawRestMethod;
import com.philipp_kehrbusch.gen.webdomain.source.exceptions.DomainNotFoundException;
import com.philipp_kehrbusch.gen.webdomain.source.exceptions.InvalidViewAttributePathException;
import com.philipp_kehrbusch.gen.webdomain.source.view.RawView;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDArgument;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDAttribute;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDConstructor;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDMethod;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.*;
import com.philipp_kehrbusch.gen.webdomain.util.TypeUtil;
import com.philipp_kehrbusch.gen.webdomain.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GlobalViewTrafo
public class ViewTrafo {

  @Transform
  public void transform(RawViews views, RawDomains domains, WebElements elements, GeneratorSettings settings)
          throws InvalidViewAttributePathException, DomainNotFoundException {
    if (!settings.getTargets().keySet().contains(Targets.APP)) {
      return;
    }

    for (var view : views.stream().filter(view -> !TrafoUtils.hasAnnotation(view, "NoApp")).collect(Collectors.toList())) {
      var imports = new ArrayList<String>();
      var name = view.getName();

      CDConstructor constructor;

      if (view.getAttributes().size() > 0) {
        constructor = new CDConstructorBuilder()
                .name(name)
                .addArguments(view.getAttributes().stream()
                        .map(attr -> new CDArgumentBuilder()
                                .type(TypeUtil.javaToDart(attr.getType()))
                                .name(attr.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
      } else {
        var constArgs = new ArrayList<CDArgument>();
        for (var attr : view.getDerivedAttributes()) {
          constArgs.add(new CDArgumentBuilder()
                  .type(TypeUtil.javaToDart(ViewUtil.getAttributeType(domains, view, attr)))
                  .name(ViewUtil.getAttributeName(attr))
                  .build());
        }

        constructor = new CDConstructorBuilder()
                .name(name)
                .addArguments(constArgs)
                .build();
      }

      TemplateManager.getInstance().setTemplate(constructor, "dart/AssigningConstructor.ftl", constructor);

      var attributes = new ArrayList<CDAttribute>();
      var classBuilder = new CDClassBuilder()
              .name(name)
              .addAttributes(view.getAttributes().stream()
                      .map(attr -> {
                        if (TrafoUtils.isTypeDomain(attr.getType(), domains) || TrafoUtils.isTypeView(attr.getType(), views)) {
                          imports.add("'" + FileNameResolvers.DART.resolve(
                                  TrafoUtils.getPrimitiveType(attr.getType())) + "'");
                        }
                        var res = new CDAttributeBuilder()
                                .type(TypeUtil.javaToDart(attr.getType()))
                                .name(attr.getName())
                                .build();
                        attributes.add(res);
                        return res;
                      })
                      .collect(Collectors.toList()))
              .addConstructor(constructor);

      for (var attr : view.getDerivedAttributes()) {
        var type = ViewUtil.getAttributeType(domains, view, attr);
        if (TrafoUtils.isTypeDomain(type, domains) || TrafoUtils.isTypeView(type, views)) {
          imports.add("'" + FileNameResolvers.DART.resolve(TrafoUtils.getPrimitiveType(type)) + "'");
        }

        var cdAttr = new CDAttributeBuilder()
                .type(TypeUtil.javaToDart(type))
                .name(ViewUtil.getAttributeName(attr))
                .build();
        classBuilder.addAttribute(cdAttr);
        attributes.add(cdAttr);
      }

      classBuilder.addMethod(createToJson(attributes));
      classBuilder.addMethod(createFromJson(view, attributes, domains, views));
      classBuilder.addMethod(createFromJsonArray(view));

      elements.add(new WebElement(Targets.APP, name, "model", imports, new CDArtifactBuilder()
              .name(name)
              .addClass(classBuilder.build())
              .build()));
    }
  }

  private CDMethod createToJson(List<CDAttribute> attributes) {
    var toJson = new CDMethodBuilder()
            .returnType("Map<String, dynamic>")
            .name("toJson")
            .build();
    TemplateManager.getInstance().setTemplate(toJson, "dart/methods/model/toJson.ftl", attributes);
    return toJson;
  }

  private CDMethod createFromJson(RawView view, List<CDAttribute> attributes, RawDomains domains, RawViews views) {
    var fromJson = new CDMethodBuilder()
            .addModifier("static")
            .returnType(view.getName())
            .name("fromJson")
            .addArgument(new CDArgumentBuilder()
                    .type("Map<String, dynamic>")
                    .name("json")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(fromJson, "dart/methods/model/fromJson.ftl", view, attributes,
            new TrafoUtils(), domains, views);
    return fromJson;
  }

  private CDMethod createFromJsonArray(RawView view) {
    var fromJsonArray = new CDMethodBuilder()
            .addModifier("static")
            .returnType("List<" + view.getName() + ">")
            .name("fromJsonArray")
            .addArgument(new CDArgumentBuilder()
                    .type("List<dynamic>")
                    .name("json")
                    .build())
            .build();
    TemplateManager.getInstance().setTemplate(fromJsonArray, "dart/methods/model/fromJsonArray.ftl", view);
    return fromJsonArray;
  }
}
