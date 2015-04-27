#ifndef __WA_NODE_H__
#define __WA_NODE_H__

#include "../mg/mg_connection.h"

/** Nodo de la web
 */
class WANode {
	public:
		virtual ~WANode();
		/** Manejador del nodo.
		 * @param conn: coneccion
		 * @param url: url actual (o parte, los nodos adelantaran el ppcio antes de pasarselas a subnodos)
		 * @return true: si fue manejado, false si no
		 */
		bool handle(MgConnection& conn, const char* url);

	protected:
		/** metodo que dice si este nodo manejara la url proporcionada
		 * @param url
		 * @return true: si la debe manejar, false: si no
		 */
		virtual bool match(const char* url) = 0;

		/** Metodo que raliza las acciones que el nodo deba hacer
		 * @param conn coneccion
		 * @param url: url actual (o parte, los nodos adelantaran el ppcio antes de pasarselas a subnodos)
		 */
		virtual void execute(MgConnection& conn, const char* url) = 0;
};


#endif
