#include "message.h"
#include "../util/bin2hex.h"
#include "../util/serializer.h"

#include <sstream>
#include <cstdlib>
#include <algorithm>

#include <iostream>

#include <rocksdb/write_batch.h>

#ifndef _BSD_SOURCE
#define _BSD_SOURCE
#endif
#include <endian.h>

extern "C" {
	#include <sys/time.h>
}

using std::copy;
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

DB_ENTITY_DEF(Message)

Message::Message() :
	id(0),
	to(""),
	from(""),
	msg(""),
	arrived(0),
	read(0),
	t(0),
	has_file(0)
{
}

Message Message::Now(){
	Message m;
	struct timeval tv;

	gettimeofday(&tv, NULL);
	m.t = tv.tv_sec;
	m.id = htobe64(tv.tv_sec * (uint64_t)1000000 + tv.tv_usec);

	return m;
}

Message Message::Now(const string& to, const string& from, const string& msg){
	Message m = Message::Now();
	m.setTo(to);
	m.setFrom(from);
	m.setMsg(msg);

	return m;
}

void Message::packKey(string& key){
	OSerializer oser(key);

	if(this->from < this->to)
		oser << ConstStrNoPrefix(this->from) << '/' << ConstStrNoPrefix(this->to);
	else
		oser << ConstStrNoPrefix(this->to) << '/' << ConstStrNoPrefix(this->from);

	oser << '/' << this->id;
}

void Message::packValue(string& value){
	OSerializer(value) << this->id << this->to << this->from << this->msg << this->arrived << this->read << this->t << this->has_file;
}

bool Message::unPack(const string& key, const string& value){
	ISerializer keySerializer(key);
	keySerializer >> Ignore(key.size() - sizeof(this->id)) >> this->id;

	ISerializer valueSerializer(value);
	valueSerializer >> this->id >> this->to >> this->from >> this->msg >> this->arrived >> this->read >> this->t >> this->has_file;

	return ! (keySerializer.error() || valueSerializer.error() );
}

const string& Message::getFrom() const{
	return this->from;
}

const string& Message::getTo() const {
	return this->to;
}

const string& Message::getMsg() const {
	return this->msg;
}

const time_t& Message::getTime() const {
	return this->t;
}

const uint64_t& Message::getIdBin() const {
	return this->id;
}

string Message::getId() const {
	return bin2hex(this->id);
}

string Message::toJson() const {
	stringstream ss;
	ss << "{\"id\":\"" << this->getId() << "\",\"to\":\"" << this->to << "\",\"from\":\"" << this->from << "\",\"message\":\"" << this->msg << "\",\"arrived\":" << this->arrived << ",\"read\":" << this->read << ",\"time\":" << this->getTime();

	if(this->has_file) // TODO:
		ss << ",\"file\":\"proximamente\",\"file_type\":\"proximamente\"";

	ss << "}";
	return ss.str();
}

void Message::setFrom(const string& from) {
	this->from = from;
}

void Message::setTo(const string& to) {
	this->to = to;
}

void Message::setMsg(const string& msg) {
	this->msg = msg;
}

Status Message::get(const string& id) {
	vector<char> bin = hex2bin(id);

	if(bin.size() != sizeof(this->id))
		return Status::InvalidArgument("id invalido");

	memcpy((void*) &this->id, (void*) bin.data(), sizeof(this->id));

	this->packKey();

	return DbEntity::get(this->key);
}

Message::Iterator Message::NewIterator(){
	return Message::Iterator(Message::db->NewIterator(ReadOptions(), Message::cf), Message::db, Message::cf);
}

Message::Iterator::Iterator(rocksdb::Iterator* i, DB* d, ColumnFamilyHandle* c) :
	DbIterator<Message>(i, d, c)
{
}

void Message::Iterator::seek(const string& u1, const string& u2){
	string key;
	OSerializer oser(key);
	if(u1 < u2)
		oser << ConstStrNoPrefix(u1) << '/' << ConstStrNoPrefix(u2);
	else
		oser << ConstStrNoPrefix(u2) << '/' << ConstStrNoPrefix(u1);

	oser << '/';

	DbIterator<Message>::seek(key);
}

void Message::Iterator::seek(const string& u1, const string& u2, const string& id){
	string key;
	OSerializer oser(key);
	if(u1 < u2)
		oser << ConstStrNoPrefix(u1) << '/' << ConstStrNoPrefix(u2);
	else
		oser << ConstStrNoPrefix(u2) << '/' << ConstStrNoPrefix(u1);

	oser << '/';

	vector<char> bin = hex2bin(id);
	uint64_t _id;
	if(bin.size() != sizeof(_id)){
		this->prefix = "/////////";
		return ;
	}

	memcpy((void*) &_id, (void*) bin.data(), sizeof(_id));

	oser << _id;

	DbIterator<Message>::seek(key);
	this->prefix.resize(this->prefix.size() - sizeof(_id));
}
