${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
<#list node.modifiers as modifier>${modifier} </#list>${node.name}
(
<#list node.arguments as argument>
	${tc.include("ts/Argument.ftl", argument)}
</#list>
)<#if node.returnType??>: ${node.returnType}</#if>
${tc.defineHookPoint(node, "ts/MethodBody.ftl")}
