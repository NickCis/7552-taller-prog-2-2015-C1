#ifndef __WA_METHOD_NODE_H__
#define __WA_METHOD_NODE_H__

#include "wa_str_node.h"
#include <string>

class WAMethodNode : public WAStrNode {
	public:
		/** Constructor
		 * @param str: ppcio de la url que manejara
		 */
		WAMethodNode(const std::string &str);
		WAMethodNode(const char *str);

	protected:
		/** Chequea el metodo (METHOD HTTP) de la connecion y ejecuta el metodo correspondiente
		 */
		void execute(MgConnection& conn, const char* url);

		/** Metodo que se ejecuta si la connecion es POST
		 */
		virtual void executePost(MgConnection& conn, const char* url);

		/** Metodo que se ejecuta si la connecion es GET
		 */
		virtual void executeGet(MgConnection& conn, const char* url);

		/** Metodo que se ejecuta si la connecion es DELETE
		 */
		virtual void executeDelete(MgConnection& conn, const char* url);

		/** Manejador de metodo no implementado
		 */
		virtual void methodNotAllowed(MgConnection& conn, const char* url);
};

#endif
