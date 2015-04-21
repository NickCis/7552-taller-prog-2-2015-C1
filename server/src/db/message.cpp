#include "message.h"

#include <algorithm>
#include <cstdlib>

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
	a.t = tv.tv_sec * (uint64_t)1000000 + tv.tv_usec;

	Message::MessageHeader mh;
	memset(&mh, 0, sizeof(Message::MessageHeader));

	vector<char> key;
	Message::GetKeyFromUser(key, from, to, a.t);

	vector<char> data;
	Message::Pack(data, mh, from, msg);

	return Message::db->Put(WriteOptions(), Message::cf.get(), Slice(key.data(), key.size()), Slice(data.data(), data.size()));
}

Status Message::Get(const string& to, const string& from, const uint64_t& t, Message& a){
	vector<char> key;
	Message::GetKeyFromUser(key, from, to, t);

	string data;
	Status s = Message::db->Get(ReadOptions(), Message::cf.get(), Slice(key.data(), key.size()), &data);
	if(!Message::UnPack(data, a)) // TODO: arreglar error de salida
		return Status::InvalidArgument(Slice("Error en la informacion guardada"));

	a.from = from;
	a.to = to;
	a.t = t;
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

void Message::GetKeyFromUser(vector<char>& data, const string& to, const string& from, const uint64_t& tv){
	// XXX: Esto es un ascoo!
	Message::GetConversationFromUser(data, to, from);

	size_t end = data.size();
	data.resize(end+1+sizeof(uint64_t));
	*(data.begin()+end) = '/';
	copy(&tv, &tv+sizeof(uint64_t), data.begin()+end+1);
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

	/* // Pack: From
	size = from.size();
	copy(&size, &size + sizeof(size_t), it);
	it += sizeof(size_t);
	copy(to.begin(), to.end(), it);
	it += size; */

	// Pack: Message
	size = msg.size();
	copy(&size, &size + sizeof(size_t), it);
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

	/*UNPACK(&size, sizeof(size_t), it, data_size);
	msg.from.resize(size);
	UNPACK(msg.from.begin(), size, it, data_size);*/

	UNPACK(&size, sizeof(size_t), it, data_size);
	msg.msg.resize(size);
	UNPACK(msg.msg.begin(), size, it, data_size);

	return true;
}

const string& Message::getFrom(){
	return this->from;
}

const string& Message::getTo(){
	return this->to;
}

const string& Message::getMsg(){
	return this->msg;
}

const uint64_t& Message::getTime(){
	return this->t;
}