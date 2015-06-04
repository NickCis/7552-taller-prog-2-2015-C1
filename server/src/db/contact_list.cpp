#include "contact_list.h"
#include "suscriber_list.h"
#include "../util/serializer.h"

#include "profile.h"

#include <sstream>

using std::vector;
using std::string;
using std::stringstream;

using rocksdb::Status;

DB_ENTITY_DEF(ContactList)

ContactList::ContactList(){}

void ContactList::packKey(string& key){
	key = this->key;
}

bool ContactList::unPack(const string& key, const string& value){
	this->key = key;
	return DbList::unPack(key, value);
}

string ContactList::toJson() const {
	stringstream ss;
	ss << "{\"contacts\":[";

	bool first=true;
	Profile p;
	for(auto it=this->list.begin(); it!=this->list.end(); it++){
		if(first)
			first = false;
		else
			ss << ',';

		p.get((*it).c_str());
		ss << p.toJson();
	}

	return ss.str();
}

const string& ContactList::getOwner() const{
	return this->key;
}

void ContactList::setOwner(const string& o){
	this->key = o;
}

rocksdb::Status ContactList::push_back(const std::string& u){
	SuscriberList sl;
	sl.setOwner(u);
	Status s = sl.push_back(this->getOwner());
	if(!s.ok())
		return s;

	s = DbList::push_back(u);
	if(s.ok())
		return s;

	return sl.erase(this->getOwner());
}

rocksdb::Status ContactList::erase(const std::string& u){
	SuscriberList sl;
	sl.setOwner(u);

	Status s = sl.erase(this->getOwner());
	if(!s.ok())
		return s;

	s = DbList::erase(u);
	if(s.ok())
		return s;

	return sl.push_back(this->getOwner());
}
