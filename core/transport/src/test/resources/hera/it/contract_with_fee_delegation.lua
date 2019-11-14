-- simple smart contract with constructor and get, set

function constructor()
end

function set(key, value)
  system.setItem(key, value)
end

function get(key)
  return system.getItem(key)
end

function check_delegation(fname, arg0)
  return true
end

abi.register_view(get)
abi.payable(constructor, set)
abi.register(set)
abi.fee_delegation(set)
