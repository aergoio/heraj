-- simple smart contract with bignum set get

function set(key, x, y)
  local res = bignum.add(x, y)
  system.setItem(key, res)
end

function get(key)
  return system.getItem(key)
end

abi.register_view(get)
abi.register(set)