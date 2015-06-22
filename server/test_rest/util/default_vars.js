var frisby = require('frisby');

function Args(c){
	this.c = c || {}
}

Args.prototype.toString = function(){
	var args = [];
	Object.keys(this.c).forEach(function(k){
		args.push(encodeURIComponent(k)+'='+encodeURIComponent(this.c[k]));
	}, this);

	return "?"+args.join("&");
}

module.exports = {
	protocol: process.env.WA_PROTOCOL || "http",
	domain: process.env.WA_DOMAIN || "localhost",
	port: process.env.WA_PORT || 8000,
	get url() {
		return this.protocol+"://"+this.domain+":"+this.port+"/";
	},
	buildUrl: function(){
		return this.url + (Array.prototype.join.call(arguments, '/'));
	},
	args: function(c){
		return new Args(c);
	},
	get randomUsername() {
		return "username_" + parseInt(Math.random() * 10000);
	},
	loginCredentials: function(cb){
		var username = this.randomUsername;

		frisby.create('Creo Usuario')
			.post(this.buildUrl('signup'), {
				user: username,
				pass: '123456'
			})
			.expectStatus(201)
			.expectJSONTypes({
				success: Boolean
			})
			.expectJSON({
				success: true
			})
			.afterJSON((function(){
				frisby.create('Obtengo Access Token')
					.post(this.buildUrl('auth'), {
						user: username,
						pass: '123456'
					})
					.expectStatus(201)
					.expectJSONTypes({
						access_token: String
					})
					.afterJSON(function(json){
						cb(username, json.access_token);
					})
					.toss();
			}).bind(this))
			.toss();
	}
};
