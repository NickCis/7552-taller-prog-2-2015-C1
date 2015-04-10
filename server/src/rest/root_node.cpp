#include "root_node.h"
#include "signup_node.h"
#include "not_found_node.h"

#include <iostream>

using std::cout;
using std::endl;

RootNode::RootNode(){
	children.push_back(new SignupNode());
	children.push_back(new NotFoundNode());
}

bool RootNode::match(struct mg_connection* conn, const char*& url){
	cout << "rootnode - url '" << url << "'" << endl;
	WA_COMPARE_AND_SUM(url, "/");
	return false;
}
