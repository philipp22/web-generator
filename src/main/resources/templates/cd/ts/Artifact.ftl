<#list element.imports as import>
	${import};
</#list>

<#list element.artifact.types as type>
	<#list type.modifiers as modifier>${modifier} </#list>declare type ${type.name}<#if type.type??>: ${type.type}</#if>
	= ${tc.defineHookPoint(type, null)};
</#list>

<#list element.artifact.constants as constant>
	<#list constant.annotations as annotation>
		${annotation}
	</#list>
	<#list constant.modifiers as modifier>${modifier} </#list>const ${constant.name}<#if constant.type??>: ${constant.type}</#if>
	= ${tc.defineHookPoint(constant, null)};
</#list>

<#list element.artifact.functions as function>
	<#list function.annotations as annotation>
		${annotation}
	</#list>
	<#list function.modifiers as modifier>${modifier} </#list>function ${function.name}
	(
	<#list function.arguments as arg>
		<#if (arg_index > 0)>,</#if>${arg.name}<#if arg.type??>: ${arg.type}</#if>
		<#if arg.defaultValue??> = ${arg.defaultValue}</#if>
	</#list>
	)<#if function.returnType??>: ${function.returnType}</#if>
	${tc.defineHookPoint(function, "ts/MethodBody.ftl")}
</#list>

<#list element.artifact.enums as enum>
	${tc.include("ts/Enum.ftl", enum)}
</#list>

<#list element.artifact.classes as class>
	<#list class.annotations as annotation>
		${annotation}
	</#list>
	<#list class.modifiers as modifier>${modifier} </#list>class ${class.name}
	<#if class.superClass??>extends ${class.superClass}</#if>
	<#if (class.interfaces?size > 0)>implements </#if>
	<#list class.interfaces as interface>
		<#if (interface_index > 0)>,</#if>${interface}
	</#list>{
	<#list class.attributes as attr>
		${tc.include("ts/Attribute.ftl", attr)}
	</#list>

	<#list class.constructors as constructor>
		<#list class.modifiers as modifier>${modifier} </#list>constructor(
		<#list constructor.arguments as arg>
			<#if (arg_index > 0)>,</#if>
			<#list arg.annotations as annotation>${annotation} </#list>
			<#list arg.modifiers as modifier>${modifier} </#list>${arg.name}<#if arg.type??>: ${arg.type}</#if>
		</#list>
		)
		${tc.defineHookPoint(constructor, "ts/MethodBody.ftl")}
	</#list>

	<#list class.methods as method>
		${tc.include("ts/Method.ftl", method)}
	</#list>
	}
</#list>

<#list element.artifact.interfaces as interface>
	${tc.include("ts/Interface.ftl", interface)}
</#list>
