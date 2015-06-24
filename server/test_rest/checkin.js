var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing checkin", function(){
	var userResponse;
	before(function(){
		userResponse = def.userCredentials();
	});

	it("No se debe poder acceder a un checkin de usuario inexistente", function(){
		return userResponse.then(function(data){
			return chakram.get(def.buildUrl("user", def.randomUsername, "checkin", def.args({ access_token: data.access_token})));
		}).then(function(response){
			expect(response).to.have.status(404);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No se debe poder acceder a un checkin de usuario sin usar access token", function(){
		return userResponse.then(function(data){
			return chakram.get(def.buildUrl("user", data.user, "checkin"));
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No se debe poder cambiar un checkin de usuario sin usar access token", function(){
		return userResponse.then(function(data){
			return chakram.post(def.buildUrl("user", data.user, "checkin"), false, { form: {
				latitude: 22.5,
				longitude: 44.1,
				name: "Casa de Juan"
			}});
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Solo uno mismo debe poder cambiar su checkin", function(){
		return chakram.all([userResponse, def.userCredentials()]).then(function(datas){
			return chakram.post(def.buildUrl("user", datas[0].user, "checkin"), false, { form: {
				latitude: 22.5,
				longitude: 44.1,
				name: "Casa de Juan",
				access_token: datas[1].access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(403);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Debo poder cambiar datos del checkin", function(){
		var userData,
			latitude = 22.5,
			longitude = 44.1,
			name = "Casa de Juan";
		return userResponse.then(function(data){
			userData = data;
			return chakram.post(def.buildUrl("user", data.user, "checkin"), false, { form: {
				latitude: latitude,
				longitude: longitude,
				name: name,
				access_token: data.access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({
				success: true
			});
			return chakram.get(def.buildUrl("user", userData.user, "checkin", def.args({ access_token: userData.access_token})));
		}).then(function(response){
			expect(response).to.have.status(200);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({
				latitude: latitude,
				longitude: longitude,
				name: name,
				username: userData.user
			});
		});
	});
});
