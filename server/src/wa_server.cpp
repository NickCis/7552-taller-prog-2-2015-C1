#include "wa_server.h"

WAServer::WAServer(int threads) : MgServer(threads) {
}

int WAServer::handler(MgConnection& conn, enum mg_event ev){
	switch (ev) {
		case MG_AUTH:
			return MG_TRUE;

		case MG_REQUEST:
			if(root.handle(conn, conn->uri))
				return MG_TRUE;
			return MG_FALSE;
			break;

		default:
			return MG_FALSE;
			break;
	}
}
