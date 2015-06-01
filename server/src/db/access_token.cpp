#include "access_token.h"

#include "../util/random_number.h"
#include "../util/md5.h"

#include <ctime>
#include <sstream>

using std::string;
using std::stringstream;

using rocksdb::Slice;
using rocksdb::Status;
using rocksdb::ReadOptions;
using rocksdb::WriteOptions;
using rocksdb::ColumnFamilyHandle;


DB_ENTITY_DEF(AccessToken)

AccessToken::AccessToken() :
	token(""),
	owner("") {
}

string AccessToken::CreateToken(const string& owner){
	stringstream ss;
	ss << randomNumber(1000) << owner << time(NULL) << randomNumber(9999) ;
	string out;
	md5Str(out, ss.str());
	return out;
}

const string& AccessToken::getToken() const{
	return token;
}

const string& AccessToken::getOwner() const{
	return owner;
}

bool AccessToken::IsLoggedIn(const string& t){
	return AccessToken().get(t).ok();
}

bool AccessToken::unPack(const string& key, const string& value){
	this->token = key;
	this->owner = value;
	return true;
}

void AccessToken::packKey(std::string& key){
	key = token;
}

void AccessToken::packValue(std::string& value){
	value = owner;
}

Status AccessToken::put(){
	this->token = CreateToken(this->owner);
	return DbEntity::put();
}

void AccessToken::setOwner(const std::string& o){
	this->owner = o;
}

string AccessToken::toJson() const {
	stringstream ss;
	ss << "{\"token\":\"" << this->token << "\"}";
	return ss.str();
}
