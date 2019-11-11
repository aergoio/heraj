-- an smart contract throwing event

function throwEvent()
  -- array, map, string, number(float), number(integer), boolean
  -- FIXME: throw error
  -- contract.event("simpleEvent", { 1, 2 }, { key="value" }, "string", 123.123, 123, true);

  -- array, map, string, number(float), number(integer), boolean
  contract.event("simpleEvent", { key="value" }, "text", 123.123, 123, true);
  -- nested event
  contract.event("nestedEvent", { key={ innerKey="123" } })
  -- bignum
  contract.event("bignumEvent", { _bignum="123" })
end

abi.register(throwEvent)
