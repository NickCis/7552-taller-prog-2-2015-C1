#include "message.h"
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

shared_ptr<DB> Message::db = NULL;
shared_ptr<ColumnFamilyHandle> Message::cf = NULL;

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

	std::cout << "id: " << bin2hex(a.id) << std::endl;

	Message::MessageHeader mh;
	memset(&mh, 0, sizeof(Message::MessageHeader));

	vector<char> key;
	//vector<char> keyLast;

	Message::GetKeyFromUser(key, from, to, a.id);
	//Message::GetConversationFromUser(keyLast, from, to);

	vector<char> data;

	WriteBatch batch;
	batch.Put(Message::cf.get(), Slice(key.data(), key.size()), Message::Pack(data, mh, from, msg));
	//batch.Put(Message::cf.get(), Slice(keyLast.data(), keyLast.size()), Slice((char*) &a.t, sizeof(uint64_t)));

	std::cout << "key -> " << bin2hex(key.begin(), key.end()) << std::endl;

	return Message::db->Write(WriteOptions(), &batch);
}

Status Message::Get(const string& to, const string& from, const uint64_t& id, Message& a){
	vector<char> key;
	Message::GetKeyFromUser(key, from, to, id);

	string data;
	Status s = Message::db->Get(ReadOptions(), Message::cf.get(), Slice(key.data(), key.size()), &data);
	if(!Message::UnPack(data, a)) // TODO: arreglar error de salida
		return Status::InvalidArgument(Slice("Error en la informacion guardada"));

	//a.from = from;
	//a.to = to;
	a.id = id;
	a.t = be64toh(id) / 1000000;
	return s;
}

void Message::SetDB(shared_ptr<DB> &db, shared_ptr<ColumnFamilyHandle> &cf){
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
	return shared_ptr<Message::MessageIterator>(new Message::MessageIterator(Message::db->NewIterator(ReadOptions(), Message::cf.get())));
}

Message::MessageIterator::MessageIterator(Iterator* i) : it(i) {
}

Status Message::MessageIterator::seekToLast(const string& to, const string& from){
	Message::GetConversationFromUser(this->prefix, from, to);
	string data;
	Status s = Message::db->Get(ReadOptions(), Message::cf.get(), Slice(this->prefix.data(), this->prefix.size()), &data);

	if(s.ok()){
		Message::GetKeyFromUser(this->prefix, from, to, *((uint64_t*) data.data()));
		this->it->Seek(Slice(this->prefix.data(), this->prefix.size()));
		this->prefix.resize(this->prefix.size() - sizeof(uint64_t));
		if(this->valid())
			this->unPack();
	}

	return s;
}

void Message::MessageIterator::seekToFirst(){
	this->it->SeekToFirst();
	if(this->valid())
		this->unPack();
}

void Message::MessageIterator::seek(const string& to, const string& from){
	Message::GetConversationFromUser(this->prefix, from, to);
	this->it->Seek(Slice(this->prefix.data(), this->prefix.size()));

	if(this->valid())
		this->unPack();
}

void Message::MessageIterator::unPack(){
	Message::UnPack(this->it->value().ToString(), this->msg);
	auto keyIt = this->it->key().ToString().end();
	copy(keyIt-sizeof(uint64_t), keyIt, (char*) &this->msg.id);
	this->msg.t = be64toh(this->msg.id) / 1000000;
	std::cout << "key: " << this->it->key().ToString() << " id: " << bin2hex(this->msg.id) << std::endl;
}

void Message::MessageIterator::prev(){
	this->it->Prev();
	if(this->valid())
		this->unPack();
}

void Message::MessageIterator::next(){
	this->it->Next();
	if(this->valid())
		this->unPack();
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
	return this->it->Valid() && ( this->prefix.size() ? this->it->key().starts_with(Slice(this->prefix.data(), this->prefix.size())) : true);
	//return this->it->Valid();
}
