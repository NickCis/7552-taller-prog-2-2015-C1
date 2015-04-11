#include "root_node.h"
#include "signup_node.h"
#include "user_node.h"
#include "not_found_node.h"

RootNode::RootNode() : WAParentNode("/") {
	children.push_back(new SignupNode());
	children.push_back(new UserNode());
	children.push_back(new NotFoundNode());
}
