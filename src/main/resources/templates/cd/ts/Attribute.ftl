${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
<#list node.modifiers as modifier>${modifier} </#list>${node.name}<#if node.optional>?</#if><#if node.type??>: ${node.type}</#if>
<#if node.initialValue??> = ${node.initialValue}${tc.defineHookPoint(node, null)}</#if>;