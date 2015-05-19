#include "contact_list.h"
#include "../util/serializer.h"

#include <sstream>
#include <cstdlib>
#include <cstring>
#include <algorithm>

#include <rocksdb/write_batch.h>

extern "C" {
	#include <sys/time.h>
}

using std::copy;
using std::memcmp;
using std::vector;
using std::string;
using std::to_string;
using std::shared_ptr;
using std::stringstream;

using rocksdb::DB;
using rocksdb::Slice;
using rocksdb::Status;
using rocksdb::Iterator;
using rocksdb::WriteBatch;
using rocksdb::ReadOptions;
using rocksdb::WriteOptions;
using rocksdb::ColumnFamilyHandle;

DB_ENTITY_DEF(ContactList)

ContactList::ContactList(){}

void ContactList::packKey(string& key){
	key = this->key;
}

void ContactList::packValue(string& value){
	OSerializer ser(value);
	for(auto it=this->contactList.begin(); it !=  this->contactList.end(); it++)
		ser << (*it);
}

bool ContactList::unPack(const string& key, const string& value){
	this->key = key;

	ISerializer valueSerializer(value);
	string contact;
	while(!(valueSerializer >> contact).error()){
		this->contactList.push_back(contact);
	}

	return true;
}

string ContactList::toJson() const {
	stringstream ss;
	//ss << "{\"id\":\"" << this->getId() << "\",\"time\":\"" << this->t << "\",\"type\":\"" << Notification::TypeToStr(this->type) << "\",\"data\":" << this->data << "}";
	return ss.str();

}

const string& ContactList::getOwner() const{
	return this->key;
}

void ContactList::setOwner(const string& o){
	this->key = o;
}

Status ContactList::push_back(const std::string& user){
	this->contactList.push_back(user);
	return this->merge(string("p")+user);
}

Status ContactList::erase(const std::string& user){
	for(auto it=this->contactList.begin(); it != this->contactList.end(); it++){
		if((*it) == user){
			this->contactList.erase(it);
			break;
		}
	}

	return this->merge(string("e")+user);
}

const vector<string>& ContactList::getList() const {
	return this->contactList;
}
