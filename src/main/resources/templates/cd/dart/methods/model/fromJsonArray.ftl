${tc.signature("view")}
{
	return json.map((obj) => ${view.name}.fromJson(obj)).toList();
}