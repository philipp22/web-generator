${tc.signature("node")}
<#list node.annotations as annotation>
	${annotation}
</#list>
class ${node.generatedName}
<#if node.superClass??>extends ${node.superClass}</#if>
<#if (node.interfaces?size > 0)>implements </#if>
<#list node.interfaces as interface>
	<#if (interface_index > 0)>,</#if>${interface}
</#list> {
<#list node.arbitraryCode as code>
	${tc.defineHookPoint(code, "")}
</#list>

<#list node.attributes as attr>
	${tc.include("dart/Attribute.ftl", attr)}
</#list>

<#list node.constructors as constructor>
	${tc.defineHookPoint(constructor, "dart/Constructor.ftl")}
</#list>

<#list node.methods as method>
	${tc.include("dart/Method.ftl", method)}
</#list>
	}