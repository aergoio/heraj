function constructor(key, arg1, arg2)
  system.setItem(key, {val1=arg1, val2=arg2})
end

function set(key, arg1, arg2)
  system.setItem(key, {val1=arg1, val2=arg2})
end

function get(key)
  return system.getItem(key)
end

abi.register(set, get)
