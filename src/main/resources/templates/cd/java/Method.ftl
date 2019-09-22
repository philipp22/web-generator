${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
<#list node.modifiers as modifier>${modifier} </#list>${node.returnType} ${node.name}
(
<#list node.arguments as argument>
	<#if (argument_index > 0)>,</#if>${tc.include("java/Argument.ftl", argument)}
</#list>
)
${tc.defineHookPoint(node, "java/MethodBody.ftl")}
