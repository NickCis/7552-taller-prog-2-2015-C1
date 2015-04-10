#ifndef __SINGUP_NODE_H__
#define __SINGUP_NODE_H__
#include "../wa/wa_parent_node.h"
class SignupNode : public WANode {
	public:
		SignupNode();

	protected:
		bool match(struct mg_connection* conn, const char*& url);
		void execute(struct mg_connection* conn, const char* url);
};

#endif
