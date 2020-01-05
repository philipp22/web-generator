${tc.signature("constructor")}
${constructor.name} ({
<#list constructor.arguments as arg>
	<#if (arg_index > 0)>, </#if>this.${arg.name}
</#list>
});