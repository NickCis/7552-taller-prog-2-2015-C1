#ifndef __PROFILE_NODE_H__
#define __PROFILE_NODE_H__

#include "wa_method_auth_node.h"

/** Nodo que representa el endpoint de perfil /user/<usuario>/profile
 */
class ProfileNode : public WAMethodAuthNode {
	public:
		ProfileNode();

	protected:
		void executePost(MgConnection& conn, const char* url);
		void executeGet(MgConnection& conn, const char* url);
};

#endif
