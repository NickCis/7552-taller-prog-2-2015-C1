#include "notification.h"
#include "../util/bin2hex.h"

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

#define UNPACK(DST, SIZE, SRC, SRC_SIZE) \
	if(SRC_SIZE < SIZE) \
		return false; \
	copy(SRC, SRC+SIZE, DST); \
	SRC_SIZE -= SIZE; \
	SRC += SIZE

shared_ptr<DB> Notification::db = NULL;
shared_ptr<ColumnFamilyHandle> Notification::cf = NULL;

void Notification::SetDB(shared_ptr<DB> &db, shared_ptr<ColumnFamilyHandle> &cf){
	Notification::db = db;
	Notification::cf = cf;
}

Notification::Notification(){}

Status Notification::Put(const string& to, Notification::NotificationType type, const string& data, Notification &n){
	vector<char> key;
	struct timeval tv;
	gettimeofday(&tv, NULL);
	n.t = tv.tv_sec;
	n.id = htobe64(tv.tv_sec * (uint64_t)1000000 + tv.tv_usec);
	n.type = type;
	Notification::GetKeyFromUser(key, to, n.id);
	return Notification::db->Put(WriteOptions(), Notification::cf.get(), Slice(key.data(), key.size()), Notification::Pack(n.data, type, data));
}

void Notification::GetKeyFromUser(vector<char>& data, const string& from, const uint64_t& tv){
	// XXX: Esto es un ascoo!
	data.resize(from.size()+1+sizeof(uint64_t));
	auto it = data.begin();

	copy(from.begin(), from.end(), it);
	it += from.size();

	*it = '/';
	copy((char*) &tv, ((char*) &tv)+sizeof(uint64_t), it+1);
}

Slice Notification::Pack(string& data, const Notification::NotificationType& type, const string& n_data){
	data.resize(sizeof(Notification::NotificationType)+n_data.size());
	auto it = data.begin();
	copy(&type, &type+1, it);
	it+=sizeof(Notification::NotificationType);
	copy(n_data.begin(), n_data.end(), it);
	return Slice(data);
}

bool Notification::UnPack(const string& data, Notification& m){
	size_t data_size = data.size();
	auto it = data.begin();

	UNPACK(((char*) &m.type), sizeof(Notification::NotificationType), it, data_size);

	size_t size = data.size() - sizeof(Notification::NotificationType);
	m.data.resize(size);
	UNPACK(m.data.begin(), size, it, data_size);

	return true;
}

shared_ptr<Notification::NotificationIterator> Notification::NewIterator(){
	return shared_ptr<Notification::NotificationIterator>(new Notification::NotificationIterator(Notification::db->NewIterator(ReadOptions(), Notification::cf.get())));
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
		default:
			return "unknown";
	}
}

Status Notification::DeleteUpTo(const std::string& from, const uint64_t& id){
	auto it = Notification::NewIterator();
	WriteBatch batch;

	for(it->seek(from); it->valid() && memcmp((char*) &id, (char*) &(it->value().getIdBin()), sizeof(uint64_t)) >= 0; it->next())
		batch.Delete(Notification::cf.get(), it->key());

	return Notification::db->Write(WriteOptions(), &batch);
}

Status Notification::DeleteUpTo(const std::string& from, const string& id){
	vector<char> data = hex2bin(id);
	return Notification::DeleteUpTo(from, *( (uint64_t*) data.data()));
}

Notification::NotificationIterator::NotificationIterator(Iterator* i) : it(i) {
}

void Notification::NotificationIterator::seek(const string& from){
	this->prefix = from+"/";
	this->it->Seek(Slice(this->prefix));

	if(this->valid())
		this->unPack();
}

void Notification::NotificationIterator::unPack(){
	Notification::UnPack(this->it->value().ToString(), this->notif);
	auto keyIt = this->it->key().ToString().end();
	copy(keyIt-sizeof(uint64_t), keyIt, (char*) &this->notif.id);
	this->notif.t = be64toh(this->notif.id) / 1000000;
}

void Notification::NotificationIterator::next(){
	this->it->Next();
	if(this->valid())
		this->unPack();
}

const Notification& Notification::NotificationIterator::value() const{
	return this->notif;
}

Slice Notification::NotificationIterator::key() const{
	return this->it->key();
}

Status Notification::NotificationIterator::status() const{
	return this->it->status();
}

bool Notification::NotificationIterator::valid() const {
	return this->it->Valid() && ( this->prefix.size() ? this->it->key().starts_with(Slice(this->prefix)) : true);
}

void Notification::NotificationIterator::seekToFirst(){
	this->it->SeekToFirst();
	if(this->valid())
		this->unPack();
}
