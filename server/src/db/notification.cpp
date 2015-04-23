#include "notification.h"
#include "../util/bin2hex.h"

#include <sstream>
#include <cstdlib>
#include <algorithm>

#include <rocksdb/write_batch.h>

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
	struct timeval tv;
	gettimeofday(&tv, NULL);
	return Notification::Put(to, type, data, tv.tv_sec * (uint64_t)1000000 + tv.tv_usec, n);
}

Status Notification::Put(const string& to, Notification::NotificationType type, const string& data, const uint64_t& tv, Notification &n){
	vector<char> key;
	Notification::GetKeyFromUser(key, to, n.t);
	n.t = tv;
	n.type = type;

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

	UNPACK(((char*) &m.t), sizeof(Notification::NotificationType), it, data_size);

	size_t size = data.size() - sizeof(Notification::NotificationType);
	m.data.resize(size);
	UNPACK(m.data.begin(), size, it, data_size);

	return true;
}

shared_ptr<Notification::NotificationIterator> Notification::NewIterator(){
	return shared_ptr<Notification::NotificationIterator>(new Notification::NotificationIterator(Notification::db->NewIterator(ReadOptions(), Notification::cf.get())));
}

const uint64_t& Notification::getUTime() const {
	return this->t;
}

time_t Notification::getTime() const {
	return this->t / 1000000;
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
	copy(keyIt-sizeof(uint64_t), keyIt, (char*) &this->notif.t);
}

void Notification::NotificationIterator::next(){
	this->it->Next();
	if(this->valid())
		this->unPack();
}

const Notification& Notification::NotificationIterator::value() const{
	return this->notif;
}

Status Notification::NotificationIterator::status() const{
	return this->it->status();
}

bool Notification::NotificationIterator::valid() const {
	return this->it->Valid() && ( this->prefix.size() ? this->it->key().starts_with(Slice(this->prefix)) : true);
}
