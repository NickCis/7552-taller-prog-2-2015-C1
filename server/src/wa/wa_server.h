#ifndef __WA_SERVER_H__
#define __WA_SERVER_H__

#include "../mg/mg_server.h"
#include "../rest/root_node.h"

class WAServer : public MgServer {
	public:
		WAServer(int threads=1);

	protected:
		RootNode root;
		int handler(MgConnection& conn, enum mg_event ev);
};
#endif
