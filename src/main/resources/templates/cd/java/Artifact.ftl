package ${element.basePath}.${element.path};

<#list element.imports as import>
import ${import};
</#list>

<#list element.artifact.classes as class>
	<#list class.annotations as annotation>
		${annotation}
	</#list>
	<#list class.modifiers as modifier>${modifier} </#list>class ${class.generatedName}
	<#if class.superClass??>extends ${class.superClass}</#if>
	<#if (class.interfaces?size > 0)>implements </#if>
	<#list class.interfaces as interface>
		<#if (interface_index > 0)>,</#if>${interface}
	</#list> {
	<#list class.attributes as attr>
		${tc.include("java/Attribute.ftl", attr)}
	</#list>

	<#list class.constructors as constructor>
		<#list constructor.modifiers as modifier>${modifier} </#list>${class.generatedName}
		(
		<#list constructor.arguments as arg>
			<#if (arg_index > 0)>,</#if>${arg.type} ${arg.name}
		</#list>
		)
		${tc.defineHookPoint(constructor, "java/MethodBody.ftl")}
	</#list>

	<#list class.methods as method>
		${tc.include("java/Method.ftl", method)}
	</#list>
	}
</#list>

<#list element.artifact.interfaces as interface>
	<#list interface.annotations as annotation>
		${annotation}
	</#list>
	<#list interface.modifiers as modifier>${modifier} </#list>interface ${interface.generatedName}
	<#if (interface.superInterfaces?size > 0)>extends </#if>
	<#list interface.superInterfaces as superInterface>
		<#if (superInterface_index > 0)>,</#if>${superInterface}
	</#list> {

	<#list interface.methods as method>
		${tc.include("java/InterfaceMethod.ftl", method)}
	</#list>
	}
</#list>
