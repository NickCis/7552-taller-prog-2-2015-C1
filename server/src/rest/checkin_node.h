#ifndef __CHECKIN_NODE_H__
#define __CHECKIN_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de perfil /user/<usuario>/checkin
 */
class CheckinNode : public WAMethodAuthNode {
	public:
		CheckinNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
