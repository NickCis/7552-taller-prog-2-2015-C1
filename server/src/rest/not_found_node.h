#ifndef __NOT_FOUND_NODE_H__
#define __NOT_FOUND_NODE_H__

#include "../node/wa_node.h"

/** Nodo que se usa como default. Imprime error de que no se encontro y match siempre da true.
 */
class NotFoundNode : public WANode {
	public:
		NotFoundNode();

	protected:
		bool match(const char* url); ///< Devuelve siempre true
		void execute(MgConnection& conn, const char* url);
};

#endif
