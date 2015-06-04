#include "contact_list.h"
#include "../util/serializer.h"

#include "profile.h"

using std::vector;
using std::string;

using rocksdb::Status;

void DbList::packValue(string& value){
	OSerializer ser(value);
	for(auto it=this->list.begin(); it != this->list.end(); it++)
		ser << (*it);
}

bool DbList::unPack(const string&, const string& value){
	ISerializer valueSerializer(value);
	string item;
	while(!(valueSerializer >> item).error()){
		this->list.push_back(item);
	}

	return true;
}

Status DbList::push_back(const std::string& user){
	bool add = true;
	for(auto it=this->list.begin(); it != this->list.end(); it++){
		if((*it) == user){
			add = false;
			break;
		}
	}

	if(add)
		this->list.push_back(user);

	return this->merge(string("p")+user);
}

Status DbList::erase(const std::string& user){
	for(auto it=this->list.begin(); it != this->list.end(); it++){
		if((*it) == user){
			this->list.erase(it);
			break;
		}
	}

	return this->merge(string("e")+user);
}

const vector<string>& DbList::getList() const {
	return this->list;
}
