#include "message.h"
#include "notification.h"
#include "../util/bin2hex.h"

#include <sstream>
#include <cstdlib>
#include <algorithm>

#include <iostream>

#include <rocksdb/write_batch.h>

#define _BSD_SOURCE
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

DB* Message::db = NULL;
ColumnFamilyHandle* Message::cf = NULL;

#define UNPACK(DST, SIZE, SRC, SRC_SIZE) \
	if(SRC_SIZE < SIZE) \
		return false; \
	copy(SRC, SRC+SIZE, DST); \
	SRC_SIZE -= SIZE; \
	SRC += SIZE

Message::Message(){
}

Status Message::Put(const string& to, const string& from, const string& msg, Message& a){
	struct timeval tv;
	gettimeofday(&tv, NULL);
	a.t = tv.tv_sec;
	a.id = htobe64(tv.tv_sec * (uint64_t)1000000 + tv.tv_usec);
	a.from = from;
	a.msg = msg;
	a.arrived = 0;
	a.read = 0;
	a.has_file = 0;

	Message::MessageHeader mh;
	memset(&mh, 0, sizeof(Message::MessageHeader));

	vector<char> key;
	vector<char> data;
	Message::GetKeyFromUser(key, from, to, a.id);

	WriteBatch batch;
	batch.Put(Message::cf, Slice(key.data(), key.size()), Message::Pack(data, mh, from, msg));

	fill(key.end()-sizeof(uint64_t), key.end(), 0);
	batch.Put(Message::cf, Slice(key.data(), key.size()), Slice((char*) &a.id, sizeof(uint64_t)));

	Notification n;
	Notification::Put(to, Notification::NOTIFICATION_MESSAGE, a.toJson(), n);

	return Message::db->Write(WriteOptions(), &batch);
}

Status Message::Get(const string& to, const string& from, const uint64_t& id, Message& a){
	vector<char> key;
	Message::GetKeyFromUser(key, from, to, id);

	string data;
	Status s = Message::db->Get(ReadOptions(), Message::cf, Slice(key.data(), key.size()), &data);
	if(!Message::UnPack(data, a)) // TODO: arreglar error de salida
		return Status::InvalidArgument(Slice("Error en la informacion guardada"));

	//a.from = from;
	//a.to = to;
	a.id = id;
	a.t = be64toh(id) / 1000000;
	return s;
}

void Message::SetDB(DB* db, ColumnFamilyHandle* cf){
	Message::db = db;
	Message::cf = cf;
}

void Message::GetConversationFromUser(vector<char>& data, const string& to, const string& from){
	// XXX: Esto es un ascoo!
	data.resize(to.size()+from.size()+2);
	const string& first = (from < to) ? from : to;
	const string& second = (from < to) ? to : from;
	auto it = data.begin();

	copy(first.begin(), first.end(), it);
	it += first.size();

	*it = '/';
	it++;
	copy(second.begin(), second.end(), it);
}

void Message::GetKeyFromUser(vector<char>& data, const string& to, const string& from, const uint64_t& id){
	// XXX: Esto es un ascoo!
	Message::GetConversationFromUser(data, to, from);

	size_t end = data.size();
	data.resize(end+1+sizeof(uint64_t));
	*(data.begin()+end) = '/';
	copy((char*) &id, ((char*) &id)+sizeof(uint64_t), data.begin()+end+1);
}

Slice Message::Pack(vector<char>& data, const Message::MessageHeader& mh, const string& from, const string& msg){
	// XXX: codigo feo, muy feo
	data.resize(sizeof(Message::MessageHeader) + sizeof(size_t) + from.size() + sizeof(size_t) + msg.size());
	auto it = data.begin();
	size_t size;

	// Pack: Header
	size = sizeof(Message::MessageHeader);
	copy((char*) &mh, ((char*) (&mh))+size, it);
	it += size;

	// Pack: From
	size = from.size();
	copy(&size, &size + 1, it);
	it += sizeof(size_t);
	copy(from.begin(), from.end(), it);
	it += size;

	// Pack: Message
	size = msg.size();
	copy(&size, &size + 1, it);
	it += sizeof(size_t);
	copy(msg.begin(), msg.end(), it);
	it += size;

	return Slice(data.data(), data.size());
}

bool Message::UnPack(const string& data, Message& msg){
	size_t data_size = data.size();
	auto it = data.begin();
	size_t size;

	Message::MessageHeader mh;
	UNPACK(((char*) &mh), sizeof(Message::MessageHeader), it, data_size);

	msg.arrived = mh.arrived;
	msg.read = mh.read;
	msg.has_file = mh.has_file;

	UNPACK((char*) &size, sizeof(size_t), it, data_size);
	msg.from.resize(size);
	UNPACK(msg.from.begin(), size, it, data_size);

	UNPACK((char*) &size, sizeof(size_t), it, data_size);
	msg.msg.resize(size);
	UNPACK(msg.msg.begin(), size, it, data_size);

	return true;
}

const string& Message::getFrom() const {
	return this->from;
}

/*const string& Message::getTo() const {
	return this->to;
}*/

const string& Message::getMsg() const {
	return this->msg;
}

const time_t& Message::getTime() const {
	return this->t;
}

//const uint64_t& Message::getId() const {
//	return this->id;
//}

string Message::getId() const {
	return bin2hex(this->id);
}

string Message::toJson() const {
	stringstream ss;
	ss << "{\"id\":\"" << this->getId() << "\",\"from\":\"" << this->from << "\",\"message\":\"" << this->msg << "\",\"arrived\":" << this->arrived << ",\"read\":" << this->read << ",\"time\":" << this->getTime();

	if(this->has_file) // TODO: 
		ss << ",\"file\":\"proximamente\",\"file_type\":\"proximamente\"";

	ss << "}";
	return ss.str();
}

shared_ptr<Message::MessageIterator> Message::NewIterator(){
	return shared_ptr<Message::MessageIterator>(new Message::MessageIterator(Message::db->NewIterator(ReadOptions(), Message::cf)));
}

Message::MessageIterator::MessageIterator(Iterator* i) : it(i) {
}

Status Message::MessageIterator::seekToLast(const string& to, const string& from){
	Message::GetKeyFromUser(this->prefix, from, to, 0);
	string data;
	Status s = Message::db->Get(ReadOptions(), Message::cf, Slice(this->prefix.data(), this->prefix.size()), &data);

	if(s.ok()){
		Message::GetKeyFromUser(this->prefix, from, to, *((uint64_t*) data.data()));
		this->it->Seek(Slice(this->prefix.data(), this->prefix.size()));
		this->prefix.resize(this->prefix.size() - sizeof(uint64_t));
		if(this->_valid()){
			this->unPack();
			if(this->msg.t == 0)
				this->prev();
		}
	}

	return s;
}

void Message::MessageIterator::seekToFirst(){
	this->it->SeekToFirst();
	if(this->_valid()){
		this->unPack();
		if(this->msg.t == 0)
			this->next();
	}
}

void Message::MessageIterator::seek(const string& to, const string& from, const string& id_str){
	uint64_t id;
	vector<char> id_data = hex2bin(id_str);
	copy(id_data.begin(), id_data.begin()+sizeof(uint64_t), (char*) &id);
	Message::MessageIterator::seek(to, from, id);

}
void Message::MessageIterator::seek(const string& to, const string& from, const uint64_t& id){
	Message::GetKeyFromUser(this->prefix, from, to, id);
	this->it->Seek(Slice(this->prefix.data(), this->prefix.size()));
	this->prefix.resize(this->prefix.size() - sizeof(uint64_t));

	if(this->_valid()){
		this->unPack();
		if(this->msg.t == 0)
			this->next();
	}
}

void Message::MessageIterator::seek(const string& to, const string& from){
	Message::GetConversationFromUser(this->prefix, from, to);
	this->it->Seek(Slice(this->prefix.data(), this->prefix.size()));

	if(this->_valid()){
		this->unPack();
		if(this->msg.t == 0)
			this->next();
	}
}

void Message::MessageIterator::unPack(){
	Message::UnPack(this->it->value().ToString(), this->msg);
	auto keyIt = this->it->key().ToString().end();
	copy(keyIt-sizeof(uint64_t), keyIt, (char*) &this->msg.id);
	this->msg.t = be64toh(this->msg.id) / 1000000;
}

void Message::MessageIterator::prev(){
	this->it->Prev();
	if(this->_valid()){
		this->unPack();
		if(this->msg.t == 0)
			this->prev();
	}
}

void Message::MessageIterator::next(){
	this->it->Next();
	if(this->_valid()){
		this->unPack();
		if(this->msg.t == 0)
			this->next();
	}
}

//Slice Message::MessageIterator::key() const{
//	return this->it->key();
//}

//Slice Message::MessageIterator::value() const{
//	return this->it->value();
//}

const Message& Message::MessageIterator::value() const{
	return this->msg;
}

Status Message::MessageIterator::status() const{
	return this->it->status();
}

bool Message::MessageIterator::valid() const {
	return this->_valid() && this->msg.t;
}

bool Message::MessageIterator::_valid() const {
	return this->it->Valid() && ( this->prefix.size() ? this->it->key().starts_with(Slice(this->prefix.data(), this->prefix.size())) : true);
	//return this->it->Valid();
}
