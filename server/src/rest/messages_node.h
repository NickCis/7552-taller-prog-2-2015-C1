#ifndef __MESSAGES_NODE_H__
#define __MESSAGES_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de mensajes /user/<usuario>/messages
 */
class MessagesNode : public WAMethodAuthNode {
	public:
		MessagesNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
		bool executeAck(MgConnection& conn, const char*, const std::string& user, const std::string& loggedUser);
		void executeAckArrivedRead(MgConnection& conn, bool read, const std::string& user, const std::string& loggedUser);
};

#endif
