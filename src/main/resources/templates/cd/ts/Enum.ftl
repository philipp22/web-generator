${tc.signature("enum")}
<#list enum.annotations as annotation>
	${annotation}
</#list>
<#list enum.modifiers as modifier>${modifier} </#list>enum ${enum.name} {
<#list enum.constants as const>
	<#if (const_index > 0)>,</#if>
	${tc.include("ts/EnumConstant.ftl", const)}
</#list>
}