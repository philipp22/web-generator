${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
<#list node.modifiers as modifier>
	${modifier}
</#list>
${node.returnType} ${node.name}
(
<#list node.arguments as argument>
	<#if (argument_index > 0)>,</#if>${tc.include("dart/Argument.ftl", argument)}
</#list>
)
<#if (node.exceptions?size > 0)>throws <#list node.exceptions as ex><#if (ex_index > 0)>, </#if>${ex}</#list></#if>
${tc.defineHookPoint(node, "dart/MethodBody.ftl")}
