#include "suscriber_list.h"
#include "../util/serializer.h"

#include <sstream>

using std::vector;
using std::string;
using std::stringstream;

using rocksdb::Status;

DB_ENTITY_DEF(SuscriberList)

SuscriberList::SuscriberList(){}

void SuscriberList::packKey(string& key){
	OSerializer(key) << ConstStrNoPrefix(this->owner) << ConstStrNoPrefix("/suscriber");
}

bool SuscriberList::unPack(const string& key, const string& value){
	// /suscriber -> largo 10
	ISerializer(key) >> StrNoPrefix(this->key, key.size()-10);
	return DbList::unPack(key, value);
}

string SuscriberList::toJson() const {
	return "";
}

const string& SuscriberList::getOwner() const{
	return this->owner;
}

void SuscriberList::setOwner(const string& o){
	this->owner = o;
}

Status SuscriberList::get(const std::string& key){
	this->owner = key;
	string k;
	this->packKey(k);
	return DbEntity::get(k);
}
