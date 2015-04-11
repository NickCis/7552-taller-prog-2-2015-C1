#include "wa_str_node.h"
#include <cstring>

using std::string;
using std::strncmp;

WAStrNode::WAStrNode(const char* str) : uri(str) {
}

WAStrNode::WAStrNode(const string &str) : uri(str){
}

bool WAStrNode::match(const char* url){
	return strncmp(url, this->uri.c_str(), this->uri.length()) == 0;
}
