var frisby = require('frisby'),
	def = require('./util/default_vars.js');

var randomUsername = def.randomUsername;
frisby.create('Se debe poder crear un usuario')
	.post(def.buildUrl('signup'), {
		user: randomUsername,
		pass: '123456'
	})
	.expectStatus(201)
	.expectJSONTypes({
		success: Boolean
	})
	.expectJSON({
		success: true
	})
	.afterJSON(function(){
		frisby.create('No se tiene que poder crear un usuario ya existente')
			.post(def.buildUrl('signup'), {
				user: randomUsername,
				pass: '123456'
			})
			.expectStatus(400)
			.expectJSONTypes({
				error: Object
			})
			.toss();
	})
	.toss();

frisby.create('Los usuarios que se crean deben tener contrase~na')
	.post(def.buildUrl('signup'), {
		user: def.randomUsername,
		pass: ''
	})
	.expectStatus(400)
	.expectJSONTypes({
		error: Object
	})
	.toss();
