-- simple smart contract with constructor and get, set

function constructor(key, arg1, arg2)
  if key ~= nil then
    system.setItem(key, {intVal=arg1, stringVal=arg2})
  end
end

function set(key, arg1, arg2)
  system.setItem(key, {intVal=arg1, stringVal=arg2})
  return get(key)
end

function get(key)
  return system.getItem(key)
end

abi.register_view(get)
abi.register(set)
