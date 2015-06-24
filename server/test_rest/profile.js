var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing profile", function(){
	var userResponse;
	before(function(){
		userResponse = def.userCredentials();
	});

	it("No se debe poder acceder a un perfil de usuario inexistente", function(){
		return userResponse.then(function(data){
			return chakram.get(def.buildUrl("user", def.randomUsername, "profile", def.args({ access_token: data.access_token})));
		}).then(function(response){
			expect(response).to.have.status(404);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No se debe poder acceder a un perfil de usuario sin usar access token", function(){
		return userResponse.then(function(data){
			return chakram.get(def.buildUrl("user", data.user, "profile"));
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No se debe poder cambiar un perfil de usuario sin usar access token", function(){
		return userResponse.then(function(data){
			return chakram.post(def.buildUrl("user", data.user, "profile"), false, { form: {
				nickname: "pepe",
				online: false,
				status: "este es mi estado"
			}});
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Solo uno mismo debe poder cambiar su perfil", function(){
		return chakram.all([userResponse, def.userCredentials()]).then(function(datas){
			return chakram.post(def.buildUrl("user", datas[0].user, "profile"), false, { form: {
				nickname: "pepe",
				online: false,
				status: "este es mi estado",
				access_token: datas[1].access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(403);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Debo poder cambiar datos del perfil", function(){
		var userData,
			nickname = "pepe",
			online = false,
			status = "este es mi estado";
		return userResponse.then(function(data){
			userData = data;
			return chakram.post(def.buildUrl("user", data.user, "profile"), false, { form: {
				nickname: nickname,
				online: online,
				status: status,
				access_token: data.access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({
				success: true
			});
			return chakram.get(def.buildUrl("user", userData.user, "profile", def.args({ access_token: userData.access_token})));
		}).then(function(response){
			expect(response).to.have.status(200);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({
				username: userData.user,
				nickname: nickname,
				online: online,
				status: {
					text: status
				}
			});
		});
	});

});
