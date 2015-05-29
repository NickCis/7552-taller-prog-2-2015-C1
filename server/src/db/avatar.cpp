#include "avatar.h"
#include "../util/serializer.h"

#include <sstream>
#include <cstdlib>
#include <cstring>
#include <algorithm>

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

DB_ENTITY_DEF(Avatar)

Avatar::Avatar(){}

void Avatar::packKey(string& key){
	OSerializer(key) << ConstStrNoPrefix(this->owner) << '/' << ConstStrNoPrefix(string("avatar"));
}

void Avatar::packValue(string& value){
	this->t = time(NULL);
	OSerializer(value) << this->t << this->data;
}

bool Avatar::unPack(const string& key, const string& value){
	ISerializer valueSerializer(value);
	valueSerializer >> this->t >> this->data;

	return ! valueSerializer.error();
}

const time_t& Avatar::getTime() const {
	return this->t;
}

void Avatar::setOwner(const std::string& owner){
	this->owner = owner;
}

void Avatar::setData(const std::string& data){
	this->data = data;
}

const string& Avatar::getData() const{
	return this->data;
}

Status Avatar::get(const string& key){
	return DbEntity::get(key+"/avatar");
}

std::string Avatar::toJson() const{
	return "";
}
