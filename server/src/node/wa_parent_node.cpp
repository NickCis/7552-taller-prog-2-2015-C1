#include "wa_parent_node.h"

using std::string;

WAParentNode::WAParentNode(const std::string &str) : WAStrNode(str){
}
WAParentNode::WAParentNode(const char *str) : WAStrNode(str){
}

WAParentNode::~WAParentNode(){
	for(auto it=children.begin(); it != children.end(); it++)
		delete (*it);
}

void WAParentNode::execute(MgConnection& conn, const char* url){
	this->preExecute(conn, url);
	for(auto it=children.begin(); it != children.end(); it++)
		if((*it)->handle(conn, url))
			break;
}

void WAParentNode::preExecute(MgConnection& conn, const char*& url){
	url += this->uri.length();
}
