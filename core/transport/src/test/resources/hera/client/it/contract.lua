function constructor(arg0, arg1)
  system.setItem("root", {})
  system.setItem("initKey", {intVal=arg0, stringVal=arg1})
end

function getConsVal()
  return system.getItem("initKey")
end

function set(key, value)
  local root = system.getItem("root")
  root[key] = value
  system.setItem("root", root)
  return root
end

function get(key)
  return system.getItem("root")[key]
end

abi.register(getConsVal, set, get)
