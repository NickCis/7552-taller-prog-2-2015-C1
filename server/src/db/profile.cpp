#include "profile.h"
#include "../util/serializer.h"

#include <sstream>
#include <cstdlib>
#include <cstring>
#include <algorithm>
#include "../util/log.h"

extern "C" {
	#include <sys/time.h>
}

/** Tiempo de ultima actividad para suponer que el usuario esta desconectado
 * 5 * 60 = 300
 */
#define ONLINE_TIMEOUT (300)

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

DB_ENTITY_DEF(Profile)

Profile::Profile() :
	owner(""),
	nick(""),
	online(true),
	status(""),
	status_time(0),
	last_activity(0)
{}

void Profile::packKey(string& key){
	OSerializer(key) << ConstStrNoPrefix(this->owner) << '/' << ConstStrNoPrefix(string("profile"));
}

void Profile::packValue(string& value){
	OSerializer(value) << this->owner << this->nick << this->online << this->status << this->status_time << this->last_activity;
}

bool Profile::unPack(const string& key, const string& value){
	ISerializer keySerializer(key);
	ISerializer valueSerializer(value);
	keySerializer >> StrNoPrefix(this->owner, key.size() - 8);
	valueSerializer >> this->owner >> this->nick >> this->online >> this->status >> this->status_time >> this->last_activity;
	return ! (keySerializer.error() || valueSerializer.error());
}

Status Profile::get(const string& key){
	this->owner = key;
	return DbEntity::get(key+"/profile");
}

string Profile::toJson() const{
	stringstream ss;
	ss << "{\"username\":\"" << this->owner << "\",\"nickname\":\"" << this->nick << "\",\"online\":" << (this->getOnlineStatus() ? "true" : "false")
		<< ",\"last_activity\":" << this->last_activity << ",\"status\":{\"time\":" << this->status_time << ",\"text\":\"" << this->status << "\"}}";
	return ss.str();
}

void Profile::setOwner(const std::string& owner){
	this->owner = owner;
}

void Profile::setNick(const std::string& nick){
	this->nick = nick;
}

void Profile::setOnline(const bool& online){
	this->online = online;
}

void Profile::setStatus(const std::string& status){
	this->status = status;
	this->status_time = time(NULL);
}

const std::string& Profile::getOwner() const{
	return this->owner;
}

const std::string& Profile::getNick() const{
	return this->nick;
}

const bool& Profile::getOnline() const{
	return this->online;
}

bool Profile::getOnlineStatus() const{
	return this->online && ( (time(NULL) - this->last_activity) < ONLINE_TIMEOUT);
}

const std::string& Profile::getStatus() const{
	return this->status;
}

const time_t& Profile::getStatusTime() const{
	return this->status_time;
}

const time_t& Profile::getLastActivity() const{
	return this->last_activity;
}

Status Profile::UpdateLastActivity(const string& user){
	string value;
	Profile profile;

	profile.setOwner(user);
	profile.packKey();

	OSerializer(value) << time(NULL);
	return profile.merge(value);
}
