extern "C" {
	#include <signal.h>
}
#include <iostream>

#include "server_config.h"
#include "mongoose/mongoose.h"
#include "rest/root_node.h"

using std::cout;
using std::endl;

sig_atomic_t running = 1;

void sig_handler(int sig)
{
	running = 0;
}

static int ev_handler(struct mg_connection *conn, enum mg_event ev) {
	RootNode *root = (RootNode*) conn->server_param;

	switch (ev) {
		case MG_AUTH:
			return MG_TRUE;

		case MG_REQUEST:
			if(root->handle(conn, conn->uri))
				return MG_TRUE;
			//return MG_MORE;
			return MG_FALSE;

			//if (!strcmp(conn->uri, "/api/sum")) {
			//	handle_restful_call(conn);
			//	return MG_TRUE;
			//}
			//mg_send_file(conn, "index.html", s_no_cache_header);
			//return MG_MORE;
			break;

		default:
			return MG_FALSE;
			break;
	}
}

int main(int, char**){
	// Registro se~nales para cerrar de manera linda
	signal(SIGHUP, sig_handler);
	signal(SIGQUIT, sig_handler);
	signal(SIGKILL, sig_handler);
	signal(SIGINT, sig_handler);

	cout << "Version: " << SERVER_VERSION_MAJOR << "." << SERVER_VERSION_MINOR << endl;
	cout << "Mongoose: " << MONGOOSE_VERSION << endl;

	cout << "Run!" << endl;

	RootNode root;
	struct mg_server *server;

	server = mg_create_server(&root, ev_handler);
	mg_set_option(server, "listening_port", "8000");

	while(running)
		mg_poll_server(server, 1000);

	mg_destroy_server(&server);

	return 0;
}
