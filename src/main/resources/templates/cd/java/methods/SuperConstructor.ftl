${tc.signature("constructor")}
{
super(
<#list constructor.arguments as arg>
	<#if (arg_index > 0)>,</#if>${arg.name}
</#list>
);
}
