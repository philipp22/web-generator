${tc.signature("domain")}
<#assign generic = "Load" + domain.name + "ByIdAction">
this.actions$.pipe(
	ofType<${generic}>(${domain.name}ActionTypes.Load${domain.name}ById),
	switchMap(action => this.apiService.get(action.id).pipe(
		map(domain => new ${domain.name}LoadedAction([domain])),
	)),
)