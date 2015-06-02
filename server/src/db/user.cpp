#include "user.h"

#include <rocksdb/slice.h>
#include "../util/md5.h"

using std::string;
using std::shared_ptr;

using rocksdb::DB;
using rocksdb::Slice;
using rocksdb::Status;
using rocksdb::ReadOptions;
using rocksdb::WriteOptions;
using rocksdb::ColumnFamilyHandle;

DB_ENTITY_DEF(User)

User::User() :
	username(""),
	password("") {
}

const string& User::getUsername() const{
	return this->username;
}

Status User::put(){
	if(!User::IsUsernameValid(this->username))
		return Status::InvalidArgument(Slice("Nombre de usuario invalido"));

	if(this->password == "")
		return Status::InvalidArgument(Slice("Password invalida"));

	return DbEntity::put();
}

bool User::isPassword(const string& p) const{
	string pass;
	User::HashPassword(pass, p);
	return this->password == pass;
}

bool User::setPassword(const std::string& p){
	if(!User::IsPasswordValid(p))
		return false;

	User::HashPassword(this->password, p);
	return true;
}

bool User::setUsername(const string& user){
	User u;
	if(! u.get(user).IsNotFound())
		return false;
	this->username = user;

	return true;
}

bool User::unPack(const std::string& key, const std::string& value){
	this->username = key;
	this->password = value;
	return true;
}

void User::packKey(std::string& key){
	key = this->username;
}

void User::packValue(std::string& value){
	value = this->password;
}

string User::toJson() const {
	return "";
}

bool User::IsUsernameValid(const string& username){
	if(username.length() < 1)
		return false;

	if(username == "me")
		return false;

	for(auto it=username.begin(); it != username.end(); it++){
		if(('a' <= *it && *it <= 'z') || ('0' <= *it && *it <= '9')){
			continue;
		}

		switch(*it){
			case '.':
			case ',':
			case '-':
			case '_':
				continue;
				break;

			default:
				return false;
				break;
		}
	}

	return true;
}

bool User::IsPasswordValid(const string& password){
	return password.length() >= 6;
}

void User::HashPassword(std::string& hashed, const std::string& password){
	md5Str(hashed, password);
}
