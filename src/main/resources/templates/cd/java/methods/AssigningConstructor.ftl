${tc.signature("constructor")}
{
<#list constructor.arguments as arg>
	<#if arg.name != "id">
		this.${arg.name} = ${arg.name};
	</#if>
</#list>
}
