var chakram = require("chakram"),
	expect = chakram.expect,
	def = require("./util/default_vars.js");

describe("Testing broadcast", function(){
	var userResponse;
	before(function(){
		userResponse = def.userCredentials();
	});

	it("No debo poder mandar un broadcast sin access_token", function(){
		return chakram.post(def.buildUrl("broadcast"), false, { form: {
			message: "lindo mensaje"
		}}).then(function(response){
			expect(response).to.have.status(401);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("No debo poder mandar un broadcast sin contenido", function(){
		return userResponse.then(function(data){
			return chakram.post(def.buildUrl("broadcast"), false, { form: {
				access_token: data.access_token
			}})
		}).then(function(response){
			expect(response).to.have.status(400);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.have.schema(def.errorSchema);
		});
	});

	it("Debo poder mandar un broadcast", function(){
		var userDatas, msgId, msgTime,
			msgText = "lindo mensaje";
		return chakram.all([
			userResponse,
			def.userCredentials(),
			def.userCredentials()
		]).then(function(datas){
			userDatas = datas;
			return chakram.all([
				chakram.post(def.buildUrl("contacts"), def.urlEncode({
					users: [userDatas[1].user, userDatas[2].user],
					access_token: userDatas[0].access_token
				}), {json:false}),
				chakram.post(def.buildUrl("user", userDatas[1].user, "profile"), false, { form: {
					online: false,
					access_token: userDatas[1].access_token
				}}),
				chakram.post(def.buildUrl("user", userDatas[2].user, "profile"), false, { form: {
					online: true,
					access_token: userDatas[2].access_token
				}})
			]);
		}).then(function(responses){
			expect(responses[0]).to.comprise.of.json(function(e){
				if(typeof(e) == "string")
					e = JSON.parse(e);
				expect(e).to.have.property("success").that.is.an("array");
				expect(e).to.have.property("error").that.is.an("array");
				expect(e.success).to.include(userDatas[1].user);
				expect(e.success).to.include(userDatas[2].user);
			});
			return chakram.post(def.buildUrl("broadcast"), false, { form: {
				message: msgText,
				access_token: userDatas[0].access_token
			}});
		}).then(function(response){
			expect(response).to.have.status(201);
			expect(response).to.have.header('content-type', 'application/json');
			expect(response).to.comprise.of.json({
				messages: [ { to: userDatas[2].user } ]
			});
			expect(response.body).to.have.property("time").that.is.a("number");
			msgId = response.body.messages[0].id;
			msgTime = response.body.time;
			return chakram.all([
				chakram.get(def.buildUrl("user", userDatas[2].user, "messages", def.args({access_token: userDatas[0].access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						messages: [{
							id: msgId,
							from: userDatas[0].user,
							to: userDatas[2].user,
							time: msgTime,
							arrived: 0,
							read: 0,
							message: msgText
						}]
					});
					return response;
				}),
				chakram.get(def.buildUrl("user", userDatas[0].user, "messages", def.args({access_token: userDatas[2].access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						messages: [{
							id: msgId,
							from: userDatas[0].user,
							to: userDatas[2].user,
							time: msgTime,
							arrived: 0,
							read: 0,
							message: msgText
						}]
					});
					return response;
				}),
				chakram.get(def.buildUrl("notification", def.args({access_token: userDatas[2].access_token}))).then(function(response){
					expect(response).to.have.status(200);
					expect(response).to.have.header('content-type', 'application/json');
					expect(response).to.comprise.of.json({
						notifications: [{
							type: "message",
							data: {
								id: msgId,
								from: userDatas[0].user,
								to: userDatas[2].user,
								time: msgTime,
								arrived: 0,
								read: 0,
								message: msgText
							}
						}]
					});
					return response;
				})
			]);
		});
	});
});
