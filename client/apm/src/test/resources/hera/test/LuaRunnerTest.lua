function a()
  b()
  return "a"
end

function b()
 a()
 return "a"
end

b()