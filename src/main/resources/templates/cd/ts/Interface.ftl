${tc.signature("interface")}

<#list interface.annotations as annotation>
	${annotation}
</#list>
<#list interface.modifiers as modifier>${modifier} </#list>interface ${interface.name}
<#if (interface.superInterfaces?size > 0)>extends </#if>
<#list interface.superInterfaces as superInterface>
	<#if (superInterface_index > 0)>,</#if>${superInterface}
</#list> {
<#list interface.attributes as attr>
	${tc.include("ts/Attribute.ftl", attr)}
</#list>
}