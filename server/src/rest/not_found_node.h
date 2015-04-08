#ifndef __NOT_FOUND_NODE_H__
#define __NOT_FOUND_NODE_H__
#include "../wa/wa_parent_node.h"

class NotFoundNode : public WAParentNode {
	public:
		NotFoundNode();

	protected:
		bool match(struct mg_connection* conn, const char*& url);
		void execute(struct mg_connection* conn, const char* url);
};

#endif
