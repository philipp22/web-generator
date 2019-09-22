${tc.signature("domainName")}
{
	return createSelector(
		select${domainName},
		(state: EntityState<${domainName}>) => state.entities[id],
	);
}
