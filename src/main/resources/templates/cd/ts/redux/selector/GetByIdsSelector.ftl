${tc.signature("domainName")}
{
	return createSelector(
		select${domainName},
		(state: EntityState<${domainName}>) => Object.values(state.entities).filter(domain => ids.includes(domain.id)),
	);
}
