#ifndef __ROOT_NODE_H__
#define __ROOT_NODE_H__

#include "../node/wa_parent_node.h"

/** Nodo que representa el inicio de la web -> /
 * Contiene a todos los subnodos
 */
class RootNode : public WAParentNode {
	public:
		RootNode();
};

#endif
