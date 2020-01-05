${tc.signature("node")}
<#list node.annotations as annotation>
	${annotation}
</#list>
${node.name} (
<#list node.arguments as argument>
	<#if (argument_index > 0)>,</#if>${tc.include("java/Argument.ftl", argument)}
</#list>
)
<#if (node.exceptions?size > 0)>throws <#list node.exceptions as ex><#if (ex_index > 0)>, </#if>${ex}</#list></#if>
${tc.defineHookPoint(node, "java/MethodBody.ftl")}
