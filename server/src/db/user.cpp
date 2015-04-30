#include "user.h"

#include <rocksdb/slice.h>

using std::string;
using std::shared_ptr;

using rocksdb::DB;
using rocksdb::Slice;
using rocksdb::Status;
using rocksdb::ReadOptions;
using rocksdb::WriteOptions;
using rocksdb::ColumnFamilyHandle;

DB* User::db = NULL;
ColumnFamilyHandle* User::cf = NULL;

User::User() : username(""), password(""){
}

User::User(const string& u) : username(u), password(""){
}

Status User::Get(const string& username, User& u){
	u.username = username;
	u.password = "";
	return User::db->Get(ReadOptions(), User::cf, Slice(u.username), &u.password);
}

Status User::Put(const string& u, const string p, bool check){
	if(check){
		User user;
		Status s = User::Get(u, user);
		if(s.ok())
			return Status::InvalidArgument(Slice("Usuario ya existe"));

		if(!User::IsUsernameValid(u))
			return Status::InvalidArgument(Slice("Nombre de usuario invalido"));

		if(!User::IsPasswordValid(p))
			return Status::InvalidArgument(Slice("Password invalida"));
	}

	return User::db->Put(WriteOptions(), User::cf, Slice(u), Slice(p));
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

void User::SetDB(DB* db, ColumnFamilyHandle* cf){
	User::db = db;
	User::cf = cf;
}


const string& User::getUsername() const{
	return this->username;
}

const string& User::getPassword(bool forceFetch){
	if(forceFetch || this->password == "")
		User::db->Get(ReadOptions(), User::cf, Slice(this->username), &this->password);

	return this->password;
}

Status User::setPassword(const std::string& p){
	this->password = p;
	return User::db->Put(WriteOptions(), User::cf, Slice(this->username), Slice(p));
}
