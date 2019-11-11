-- simple smart contract with constructor and get, set and event on set abi

function constructor(key, arg1, arg2)
  if key ~= nil then
    system.setItem(key, {intVal=arg1, stringVal=arg2})
  end
end

function set(key, arg1, arg2)
  system.setItem(key, {intVal=arg1, stringVal=arg2})
end

function get(key)
  return system.getItem(key)
end

function newGet(key)
  -- new abi
  return system.getItem(key)
end

abi.register_view(get, newGet)
abi.register(set)
