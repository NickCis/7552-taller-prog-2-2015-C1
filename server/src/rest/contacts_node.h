#ifndef __CONTACT_NODE_H__
#define __CONTACT_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de notificaciones: /notification
 */
class ContactsNode : public WAMethodAuthNode {
	public:
		ContactsNode();

	protected:
		void executeDelete(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
		void executePost(MgConnection& conn, const char* url);
};

#endif
