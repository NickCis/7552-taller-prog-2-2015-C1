var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing contacts", function(){
	var userCreatorResponse;
	before(function(){
		userCreatorResponse = def.userCredentials();
	});

	it("No debo poder acceder a lista de contactos sin access_token", function(){
		return chakram.get(def.buildUrl("contacts")).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Contact list inicial debe ser vacia", function(){
		return userCreatorResponse.then(function(data){
			return chakram.get(def.buildUrl("contacts", def.args({access_token: data.access_token})));
		}).then(function(response){
			expect(response).to.have.status(200);
			expect(response).to.comprise.of.json({contacts:[]});
		});
	});

	it("Contactos inexistentes deben dar error", function(){
		var users = [ def.randomUsername, def.randomUsername ];
		return userCreatorResponse.then(function(data){
			return chakram.post(def.buildUrl("contacts"), def.urlEncode({access_token: data.access_token, users: users}), {json: false});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json(function(e){
				if(typeof(e) == "string")
					e = JSON.parse(e);
				expect(e).to.have.property("success").that.is.an("array");
				expect(e).to.have.property("error").that.is.an("array");
				expect(e.error).to.include(users[0]);
				expect(e.error).to.include(users[1]);
			});
		});
	});

	it("Se tiene que poder agregar un usuario a la lista de contactos", function(){
		var username,
			access_token;
		return chakram.all([userCreatorResponse, def.userCredentials()]).then(function(responses){
			username = responses[1].user;
			access_token = responses[0].access_token;
			return chakram.post(def.buildUrl("contacts"), def.urlEncode({
				access_token: access_token,
				users: [username]
			}), {json: false});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json(function(e){
				if(typeof(e) == "string")
					e = JSON.parse(e);
				expect(e).to.have.property("success").that.is.an("array");
				expect(e).to.have.property("error").that.is.an("array");
				expect(e.success).to.include(username);
			});
			return chakram.get(def.buildUrl("contacts", def.args({
				access_token: access_token
			})));
		}).then(function(response){
			expect(response).to.have.status(200);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({contacts: [{username:username}]});
		});
	});
});
