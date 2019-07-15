-- simple smart contract with constructor and get, set and event on set abi

function constructor(key, arg1, arg2)
  if key ~= nil then
    system.setItem(key, {intVal=arg1, stringVal=arg2})
  end
end

function set(key, arg1, arg2)
  -- event
  contract.event("set", key, arg1, arg2)
  system.setItem(key, {intVal=arg1, stringVal=arg2})
end

function set2(key, arg1, arg2)
  -- event
  contract.event("set2", key, arg1, arg2)
  system.setItem(key, {intVal=arg1, stringVal=arg2})
end

function get(key)
  return system.getItem(key)
end

abi.register_view(get)
abi.register(set, set2)
