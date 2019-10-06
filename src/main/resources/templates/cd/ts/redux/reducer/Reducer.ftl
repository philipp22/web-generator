${tc.signature("domainName")}
{
  switch (action.type) {
      case ${domainName}ActionTypes.${domainName}Loaded:
          return ${domainName?uncap_first}Adapter.upsertMany((action as ${domainName}LoadedAction).domains, state);
      case ${domainName}ActionTypes.${domainName}Created:
          const createdAction = action as ${domainName}CreatedAction;
          return createdAction.domain && ${domainName?uncap_first}Adapter.addOne(createdAction.domain, state) || state;
      case ${domainName}ActionTypes.${domainName}Updated:
          const updatedAction = action as ${domainName}UpdatedAction;
          return updatedAction.domain && ${domainName?uncap_first}Adapter.updateOne({
            id: updatedAction.domain.id,
            changes: updatedAction.domain,
          }, state) || state;
      case ${domainName}ActionTypes.${domainName}Deleted:
          return ${domainName?uncap_first}Adapter.removeOne((action as ${domainName}DeletedAction).id, state);
      default:
          return state;
  }
}
