#ifndef __BROADCAST_NODE_H__
#define __BROADCAST_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de broadcast: /broadcast
 */
class BroadcastNode : public WAMethodAuthNode {
	public:
		BroadcastNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
};

#endif
