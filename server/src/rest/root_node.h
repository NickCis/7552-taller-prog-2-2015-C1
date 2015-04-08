#ifndef __ROOT_NODE_H__
#define __ROOT_NODE_H__
#include "../wa/wa_parent_node.h"

class RootNode : public WAParentNode {
	public:
		RootNode();

	protected:
		bool match(struct mg_connection* conn, const char*& url);
};

#endif
