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

User::User() : username(""), password(""){
}

Status User::Get(const string& username, User& u, bool check){
	u.username = username;
	u.password = "";
	if(check)
		return User::db->Get(ReadOptions(), User::cf.get(), Slice(u.username), &u.password);

	return rocksdb::Status::OK();
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

	return User::db->Put(WriteOptions(), User::cf.get(), Slice(u), Slice(p));
}

bool User::IsUsernameValid(const string& username){
	return username.length() > 1 && username.find_first_of("/\" ") == string::npos;
}

bool User::IsPasswordValid(const string& password){
	return password.length() > 6;
}

void User::setDB(shared_ptr<DB> &db, shared_ptr<ColumnFamilyHandle> &cf){
	User::db = db;
	User::cf = cf;
}

shared_ptr<DB> User::db = NULL;
shared_ptr<ColumnFamilyHandle> User::cf = NULL;

string& User::getUsername(){
	return this->username;
}

string& User::getPassword(bool forceFetch){
	if(forceFetch || this->password == "")
		User::db->Get(ReadOptions(), User::cf.get(), Slice(this->username), &this->password);

	return this->password;
}

Status User::setPassword(const std::string& p){
	this->password = p;
	return User::db->Put(WriteOptions(), User::cf.get(), Slice(this->username), Slice(p));
}
