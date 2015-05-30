#include "checkin.h"
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

DB_ENTITY_DEF(Checkin)

Checkin::Checkin() :
	owner(""),
	name(""),
	latitude(0),
	longitude(0),
	timestamp(0)
{}

void Checkin::packKey(string& key){
	OSerializer(key) << ConstStrNoPrefix(this->owner) << '/' << ConstStrNoPrefix(string("checkin"));
}

void Checkin::packValue(string& value){
	OSerializer(value) << this->owner << this->name << this->latitude << this->longitude << this->timestamp;
}

bool Checkin::unPack(const string& key, const string& value){
	ISerializer keySerializer(key);
	ISerializer valueSerializer(value);
	keySerializer >> StrNoPrefix(this->owner, key.size() - 8);
	valueSerializer >> this->owner >> this->name >> this->latitude >> this->longitude >> this->timestamp;
	return ! (keySerializer.error() || valueSerializer.error());
}

Status Checkin::get(const string& key){
	return DbEntity::get(key+"/checkin");
}

Status Checkin::put(){
	this->timestamp = time(NULL);
	return DbEntity::put();
}

string Checkin::toJson() const{
	stringstream ss;
	ss << "{\"time\":" << this->timestamp << ",\"latitude\":" << this->latitude << ",\"longitude\":" << this->longitude << ",\"name\":\"" << this->name << "\"}";
	return ss.str();
}

void Checkin::setOwner(const std::string& owner){
	this->owner = owner;
}

void Checkin::setName(const std::string& name){
	this->name = name;
}

void Checkin::setPosition(const double& latitude, const double& longitude){
	this->latitude = latitude;
	this->longitude = longitude;
}

const std::string& Checkin::getOwner() const{
	return this->owner;
}

const std::string& Checkin::getName() const{
	return this->name;
}

const double& Checkin::getLatitude() const{
	return this->latitude;
}

const double& Checkin::getLongitude() const{
	return this->longitude;
}

const time_t& Checkin::getTimestamp() const{
	return this->timestamp;
}
