${tc.signature("node")}

<#list node.annotations as annotation>
	${annotation}
</#list>
<#list node.modifiers as modifier>${modifier} </#list>${node.returnType} ${node.name}
(
<#list node.arguments as argument>
	${tc.include("java/Argument.ftl", argument)}
</#list>
);
