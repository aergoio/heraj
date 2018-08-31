function hello(say)
  print("Hello " .. say)
end

function helloReturn(say)
  return "Hello " .. say
end

function justReturn()
  return "Hello i'm just return"
end

abi.register(hello, helloReturn, justReturn)
