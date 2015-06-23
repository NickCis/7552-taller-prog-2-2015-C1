var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing Auth", function(){
	var userCreatorResponse;
	before(function(){
		var u = def.randomUsername,
			p = "123456";
		userCreatorResponse = chakram.post(def.buildUrl("signup"), false, {form:{user:u, pass: p}}).then(function(r){
			return {user: u, pass: p};
		});
	});

	it("No Debe dejar pedir access_token con usuario inexistente", function(){
		return chakram.post(def.buildUrl("auth"), false, {form: { user: def.randomUsername, pass: "123456"}}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Debe poder autenticarse", function(){
		return userCreatorResponse.then(function(data){
			return chakram.post(def.buildUrl("auth"), false, {form: {user: data.user, pass: "123456"}});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.schema({
				properties: {
					access_token: "string"
				},
				required: ["access_token"]
			});
		});
	});
})
