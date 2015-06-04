#include "notification.h"
#include "../util/bin2hex.h"
#include "../util/serializer.h"

#include <sstream>
#include <cstdlib>
#include <cstring>
#include <algorithm>

#ifndef _BSD_SOURCE
#define _BSD_SOURCE
#endif
#include <endian.h>

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

DB_ENTITY_DEF(Notification)

Notification::Notification(){}

Notification Notification::Now(){
	Notification n;
	struct timeval tv;

	gettimeofday(&tv, NULL);
	n.t = tv.tv_sec;
	n.id = htobe64(tv.tv_sec * (uint64_t)1000000 + tv.tv_usec);

	return n;
}

Notification Notification::Now(const string& owner, Notification::NotificationType type, const string& data){
	Notification n = Notification::Now();
	n.setOwner(owner);
	n.setType(type);
	n.setData(data);

	return n;
}

void Notification::packKey(string& key){
	OSerializer(key) << ConstStrNoPrefix(this->owner) << '/' << this->id;
}

void Notification::packValue(string& value){
	OSerializer(value) << this->t << this->type << this->data;
}

bool Notification::unPack(const string& key, const string& value){
	ISerializer keySerializer(key);
	keySerializer >> StrNoPrefix(this->owner, key.size()-1-sizeof(this->id)) >> Ignore('/') >> this->id;

	ISerializer valueSerializer(value);
	valueSerializer >> this->t >> this->type >> this->data;

	return ! (keySerializer.error() || valueSerializer.error() );
}

string Notification::getId() const {
	return bin2hex(this->id);
}

const uint64_t& Notification::getIdBin() const {
	return this->id;
}

const time_t& Notification::getTime() const {
	return this->t;
}

void Notification::setOwner(const std::string& owner){
	this->owner = owner;
}

void Notification::setType(const Notification::NotificationType type){
	this->type = type;
}

void Notification::setData(const std::string& data){
	this->data = data;
}

const string& Notification::getData() const{
	return this->data;
}

string Notification::toJson() const {
	stringstream ss;
	ss << "{\"id\":\"" << this->getId() << "\",\"time\":\"" << this->t << "\",\"type\":\"" << Notification::TypeToStr(this->type) << "\",\"data\":" << this->data << "}";
	return ss.str();

}

string Notification::TypeToStr(const NotificationType& type){
	switch(type){
		case NOTIFICATION_MESSAGE:
			return "message";
		case NOTIFICATION_ACK:
			return "ack";
		case NOTIFICATION_AVATAR:
			return "avatar";
		case NOTIFICATION_PROFILE:
			return "profile";
		case NOTIFICATION_CHECKIN:
			return "checkin";
		default:
			return "unknown";
	}
}

Status Notification::DeleteUpTo(const std::string& from, const uint64_t& id){
	auto it = Notification::NewIterator();

	for(it.seek(from); it.valid() && memcmp((char*) &id, (char*) &(it.value().getIdBin()), sizeof(uint64_t)) >= 0; it.next())
		it.dlt();

	return it.write();
}

Status Notification::DeleteUpTo(const std::string& from, const string& id){
	vector<char> data = hex2bin(id);
	return Notification::DeleteUpTo(from, *( (uint64_t*) data.data()));
}

Notification::Iterator Notification::NewIterator(){
	return Notification::Iterator(Notification::db->NewIterator(ReadOptions(), Notification::cf), Notification::db, Notification::cf);
}

Notification::Iterator::Iterator(rocksdb::Iterator* i, DB* d, ColumnFamilyHandle* c) :
	DbIterator<Notification>(i, d, c) {}

void Notification::Iterator::seek(const string& from){
	DbIterator<Notification>::seek(from+"/");
}
