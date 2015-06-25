var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing signup", function(){
	it("Debe dejar crear un usuario", function(){
		var user = def.randomUsername;
		return chakram.post(def.buildUrl("signup"), false, {form: { user: user, pass: "123456"}}).then(function(response){
			expect(response).to.comprise.of.json({
				success: true
			});
		});
	});

	it("No debe dejar crear el mismo usuario dos veces", function(){
		return def.userCredentials().then(function(data){
			expect(data).to.have.property("user").that.is.an("string");
			expect(data).to.have.property("pass").that.is.an("string");
			expect(data).to.have.property("access_token").that.is.an("string");
			return chakram.post(def.buildUrl("signup"), false, {form:{user: data.user, pass: "123456"}});
		}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("El username debe ser case insensitive", function(){
		return def.userCredentials().then(function(data){
			expect(data).to.have.property("user").that.is.an("string");
			expect(data).to.have.property("pass").that.is.an("string");
			expect(data).to.have.property("access_token").that.is.an("string");
			return chakram.post(def.buildUrl("signup"), false, {form:{user: data.user.toUpperCase(), pass: "123456"}});
		}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No debe dejar registrarse con contrase~nas de menos de 6 caracteres", function(){
		return chakram.post(def.buildUrl("signup"), false, {form: { user: def.randomUsername, pass: "12345"}}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No debe dejar registrar usuarios con /", function(){
		return chakram.post(def.buildUrl("signup"), false, {form: { user: def.randomUsername+"/pepe", pass: "12345"}}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.schema(def.errorSchema);
		});
	});
})
