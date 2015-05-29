#ifndef __AVATAR_NODE_H__
#define __AVATAR_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de mensajes /user/<usuario>/messages
 */
class AvatarNode : public WAMethodAuthNode {
	public:
		AvatarNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
