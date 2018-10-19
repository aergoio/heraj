function constructor(arg0, arg1)
  system.setItem("key", {a=arg0, b=arg1})
end

function get()
  return system.getItem("key")
end

abi.register(get)