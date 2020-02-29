${tc.signature("constructor")}
{
<#list constructor.arguments as arg>
	this.${arg.name} = ${arg.name};
</#list>
}
