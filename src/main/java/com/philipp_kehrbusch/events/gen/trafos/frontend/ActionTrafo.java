package com.philipp_kehrbusch.events.gen.trafos.frontend;

import com.google.common.base.CaseFormat;
import com.philipp_kehrbusch.events.gen.Targets;
import com.philipp_kehrbusch.gen.webdomain.GeneratorSettings;
import com.philipp_kehrbusch.gen.webdomain.target.WebElement;
import com.philipp_kehrbusch.gen.webdomain.target.builders.*;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDClass;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDEnum;
import com.philipp_kehrbusch.gen.webdomain.target.cd.CDType;
import com.philipp_kehrbusch.gen.webdomain.templates.TemplateManager;
import com.philipp_kehrbusch.gen.webdomain.trafos.SingleTrafo;
import com.philipp_kehrbusch.gen.webdomain.trafos.Transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SingleTrafo(includeAnnotated = "Domain")
public class ActionTrafo {

  @Transform
  public void transform(CDClass domain, List<WebElement> elements, GeneratorSettings settings) {
    var actions = new ArrayList<CDClass>();

    // load by id
    actions.add(new CDClassBuilder()
            .name("Load" + domain.getName() + "ByIdAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.Load" + domain.getName() + "ById")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name("Load" + domain.getName() + "ByIdAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("id")
                            .type("number")
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // load by ids
    actions.add(new CDClassBuilder()
            .name("Load" + domain.getName() + "ByIdsAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.Load" + domain.getName() + "ByIds")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name("Load" + domain.getName() + "ByIdsAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("ids")
                            .type("number[]")
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // load all
    actions.add(new CDClassBuilder()
            .name("LoadAll" + domain.getName() + "Action")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.LoadAll" + domain.getName())
                    .build())
            .addModifier("export")
            .build());

    // create
    actions.add(new CDClassBuilder()
            .name("Create" + domain.getName() + "Action")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.Create" + domain.getName())
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name("Create" + domain.getName() + "Action")
                    .addArgument(new CDArgumentBuilder()
                            .name("domain")
                            .type(domain.getName())
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // update
    actions.add(new CDClassBuilder()
            .name("Update" + domain.getName() + "Action")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.Update" + domain.getName())
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name("Update" + domain.getName() + "Action")
                    .addArgument(new CDArgumentBuilder()
                            .name("domain")
                            .type(domain.getName())
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // delete
    actions.add(new CDClassBuilder()
            .name("Delete" + domain.getName() + "Action")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes.Delete" + domain.getName())
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name("Delete" + domain.getName() + "Action")
                    .addArgument(new CDArgumentBuilder()
                            .name("id")
                            .type("number")
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // loaded
    actions.add(new CDClassBuilder()
            .name(domain.getName() + "LoadedAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes." + domain.getName() + "Loaded")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name(domain.getName() + "LoadedAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("domains")
                            .type(domain.getName() + "[]")
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // created
    actions.add(new CDClassBuilder()
            .name(domain.getName() + "CreatedAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes." + domain.getName() + "Created")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name(domain.getName() + "CreatedAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("domain")
                            .type(domain.getName())
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // updated
    actions.add(new CDClassBuilder()
            .name(domain.getName() + "UpdatedAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes." + domain.getName() + "Updated")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name(domain.getName() + "UpdatedAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("domain")
                            .type(domain.getName())
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    // deleted
    actions.add(new CDClassBuilder()
            .name(domain.getName() + "DeletedAction")
            .addInterface("Action")
            .addAttribute(new CDAttributeBuilder()
                    .name("type")
                    .type("string")
                    .addModifier("public")
                    .addModifier("readonly")
                    .initialValue(domain.getName() + "ActionTypes." + domain.getName() + "Deleted")
                    .build())
            .addConstructor(new CDConstructorBuilder()
                    .name(domain.getName() + "DeletedAction")
                    .addArgument(new CDArgumentBuilder()
                            .name("id")
                            .type("number")
                            .addModifier("public")
                            .build())
                    .build())
            .addModifier("export")
            .build());

    elements.add(new WebElement(Targets.FRONTEND, domain.getName() + "Actions", "redux/actions",
            Arrays.asList(
                    "import {Action} from '@ngrx/store'",
                    String.format("import {%s} from '@domain/%s'",
                            domain.getName(),
                            CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, domain.getName()))
            ),
            new CDArtifactBuilder()
                    .name(domain.getName() + "Actions")
                    .addClasses(actions)
                    .addEnum(createActionTypes(domain))
                    .addType(createActions(domain))
                    .build()));
  }

  private CDType createActions(CDClass domain) {
    var actions = new CDTypeBuilder()
            .name(domain.getName() + "Actions")
            .addModifier("export")
            .build();
    TemplateManager.getInstance().setTemplate(actions, "ts/redux/Actions.ftl", domain.getName());
    return actions;
  }

  private CDEnum createActionTypes(CDClass domain) {
    var types = new CDEnumBuilder()
            .name(domain.getName() + "ActionTypes")
            .addModifier("export")
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("Load%sById", domain.getName()))
                    .value(String.format("'@domain/%s/load-by-id'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("Load%sByIds", domain.getName()))
                    .value(String.format("'@domain/%s/load-by-ids'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("LoadAll%s", domain.getName()))
                    .value(String.format("'@domain/%s/load-all'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("Create%s", domain.getName()))
                    .value(String.format("'@domain/%s/create'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("Update%s", domain.getName()))
                    .value(String.format("'@domain/%s/update'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("Delete%s", domain.getName()))
                    .value(String.format("'@domain/%s/delete'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("%sLoaded", domain.getName()))
                    .value(String.format("'@domain/%s/loaded'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("%sCreated", domain.getName()))
                    .value(String.format("'@domain/%s/created'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("%sUpdated", domain.getName()))
                    .value(String.format("'@domain/%s/updated'", domain.getName()))
                    .build())
            .addConstant(new CDEnumConstantBuilder()
                    .name(String.format("%sDeleted", domain.getName()))
                    .value(String.format("'@domain/%s/deleted'", domain.getName()))
                    .build())
            .build();
    return types;
  }
}
