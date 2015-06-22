var frisby = require('frisby'),
	def = require('./util/default_vars.js');

def.loginCredentials(function(username, access_token){
	frisby.create('Cambio mi perfil')
		.post(def.buildUrl('user', username, 'profile'), {
			access_token: access_token,
			nickname: "me llamo pepe",
			online: false,
			status: "hola como te va"
		})
		.expectStatus(201)
		.expectJSON({
			success: true
		})
		.afterJSON(function(){
			frisby.create('Test de mi perfil')
				.get(def.buildUrl('user', username, 'profile', def.args({access_token: access_token})))
				.expectStatus(200)
				.expectJSONTypes({
					username: String,
					nickname: String,
					online: Boolean,
					last_activity: Number,
					status: Object
				})
				.expectJSON({
					username: username,
					nickname: "me llamo pepe",
					online: false,
					status: {
						text: "hola como te va"
					}
				})
			.toss()
		})
	.toss()
});

