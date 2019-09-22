${tc.signature("domain")}
<#assign generic = "LoadAll" + domain.name + "Action">
this.actions$.pipe(
	ofType<${generic}>(${domain.name}ActionTypes.LoadAll${domain.name}),
	switchMap(action => this.apiService.getAll().pipe(
		map(domains => new ${domain.name}LoadedAction(domains)),
	)),
)