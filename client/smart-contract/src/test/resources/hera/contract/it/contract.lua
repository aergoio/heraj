function setNil(nilArg)
  system.setItem("nil", nilArg)
end

function getNil()
  return system.getItem("nil")
end

function setBoolean(booleanArg)
  system.setItem("boolean", booleanArg)
end

function getBoolean()
  return system.getItem("boolean")
end

function setNumber(numberArg)
  system.setItem("number", numberArg)
end

function getNumber()
  return system.getItem("number")
end

function setString(stringArg)
  system.setItem("string", stringArg)
end

function getString()
  return system.getItem("string")
end

abi.register(
  setNil, getNil,
  setBoolean, getBoolean,
  setNumber, getNumber,
  setString, getString
)
