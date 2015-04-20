#ifndef __SINGUP_NODE_H__
#define __SINGUP_NODE_H__

#include "wa_method_auth_node.h"

class MessagesNode : public WAMethodAuthNode {
	public:
		MessagesNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
