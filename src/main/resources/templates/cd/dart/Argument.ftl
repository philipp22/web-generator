${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
${node.type} ${node.name}