var chakram = require("chakram");

function Args(c){
	this.c = c || {}
}

Args.prototype.toString = function(){
	var args = [];
	Object.keys(this.c).forEach(function(k){
		if(this.c[k] instanceof Array)
			this.c[k].forEach(function(e){
				args.push(encodeURIComponent(k)+'[]='+encodeURIComponent(e));
			}, this);
		else
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
	urlEncode: function(c){
		return (new Args(c)).toString().substr(1);
	},
	get errorSchema() {
		return {
			properties: {
				error: {
					type: "object",
					properties: {
						code: "number",
						msg: "string",
						error_user_msg: "string"
					},
					required: [ "code", "message", "error_user_msg" ]
				}
			},
			required: ["error"]
		};
	},
	get randomUsername() {
		return "username_" + parseInt(Math.random() * 10000);
	},
	userCredentials: function(u, p){
		var user = u || this.randomUsername,
			pass = p || "123456";

		return chakram.post(this.buildUrl("signup"), false, {form: {user: user, pass: pass}}).then((function(response){
			return chakram.post(this.buildUrl("auth"), false, {form:{user: user, pass: pass}});
		}).bind(this)).then(function(response){
			return {user: user, pass: pass, access_token: response.body.access_token};
		});
	}
};
