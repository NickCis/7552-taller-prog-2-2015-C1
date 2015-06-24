var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing messages", function(){
	var userCreatorResponseOne,
		userCreatorResponseTwo;

	before(function(){
		userCreatorResponseOne = def.userCredentials();
		userCreatorResponseTwo = def.userCredentials();
	});

	it("No debo poder mandar mensajes sin usar accesstoken", function(){
		return userCreatorResponseOne.then(function(data){
			return chakram.post(def.buildUrl("user", data.user, "messages"), false, { form: {
				message: "este es un lindo mensaje"
			}});
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No debo poder leer mensajes sin usar accesstoken", function(){
		return userCreatorResponseOne.then(function(data){
			return chakram.get(def.buildUrl("user", data.user, "messages"));
		}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No debo poder mandar mensajes a usuario inexistente", function(){
		return userCreatorResponseOne.then(function(data){
			return chakram.post(def.buildUrl("user", def.randomUsername, "messages"), false, { form: {
				message: "este es un lindo mensaje",
				access_token: data.access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(404);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Debo poder mandar mensajes", function(){
		var userOne, userTwo, msgId, msgTime,
			msgStr = "este es un lindo mensaje";

		return chakram.all([userCreatorResponseOne, userCreatorResponseTwo]).then(function(responses){
			userOne = responses[0];
			userTwo = responses[1];

			return chakram.post(def.buildUrl("user", userTwo.user, "messages"), false, { form: {
				message: msgStr,
				access_token: userOne.access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json(function(json){
				expect(json).to.have.property("id").that.is.a("string");
				expect(json).to.have.property("time").that.is.a("number");
			});
			return response;
		}).then(function(response){
			msgId = response.body.id;
			msgTime = response.body.time;

			return chakram.all([
				chakram.get(def.buildUrl("user", userTwo.user, "messages", def.args({access_token: userOne.access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						messages: [{
							id: msgId,
							from: userOne.user,
							to: userTwo.user,
							time: msgTime,
							arrived: 0,
							read: 0,
							message: msgStr
						}]
					});

					return response;
				}),
				chakram.get(def.buildUrl("user", userOne.user, "messages", def.args({access_token: userTwo.access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						messages: [{
							id: msgId,
							from: userOne.user,
							to: userTwo.user,
							time: msgTime,
							arrived: 0,
							read: 0,
							message: msgStr
						}]
					});

					return response;
				}),
				chakram.get(def.buildUrl("notification", def.args({access_token: userTwo.access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						notifications: [{
							type: "message",
							data: {
								id: msgId,
								from: userOne.user,
								to: userTwo.user,
								time: msgTime,
								arrived: 0,
								read: 0,
								message: msgStr
							}
						}]
					});

					return response;
				})
			]);
		});
	});
});
