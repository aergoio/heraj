function exec(key, value)
  local item = { [key] = value }
  local root = {}
  root["item1"] = item
  system.setItem("root", root)
  return root
end

function query()
  return system.getItem("root")
end

abi.register(exec)
abi.register(query)
