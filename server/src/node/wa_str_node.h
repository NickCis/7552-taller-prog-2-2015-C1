#ifndef __WA_STR_NODE_H__
#define __WA_STR_NODE_H__

#include "wa_node.h"
#include <string>

/** Nodo asociado a una seccion de url
 */
class WAStrNode : public WANode{
	public:
		/** Constructor
		 * @param str: ppcio de la url que manejara
		 */
		WAStrNode(const std::string &str);
		WAStrNode(const char *str);

	protected:
		/** Devuelve verdadero si url comienza con uri
		 */
		bool match(const char* url);
		std::string uri; ///< ppcio de url que manejara
};

#endif
