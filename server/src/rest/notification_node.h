#ifndef __NOTIFICATION_NODE_H__
#define __NOTIFICATION_NODE_H__

#include "wa_method_auth_node.h"

class NotificationNode : public WAMethodAuthNode {
	public:
		NotificationNode();

	protected:
		void executeDelete(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
