#ifndef __WA_PARENT_NODE_H__
#define __WA_PARENT_NODE_H__

#include "wa_str_node.h"
#include <vector>

/** Nodo que tienesubnodos
 */
class WAParentNode : public WAStrNode {
	public:
		/** Constructor
		 * @param str: ppcio de la url que manejara
		 */
		WAParentNode(const std::string &str);
		WAParentNode(const char *str);
		~WAParentNode();

	protected:
		/** Meotodo que maneja las acciones, itera por la lista de subnodos fijandose quien debe manejar la uri
		 */
		void execute(MgConnection& conn, const char* url);
		/** Funcion que se ejecuta antes de chequear si algun subnodo debe manejar la connecion
		 */
		virtual void preExecute(MgConnection& conn, const char*& url);
		std::vector<WANode*> children; ///< Lista de subnodos
};

#endif
