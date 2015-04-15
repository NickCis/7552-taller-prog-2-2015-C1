#include "access_token.h"

#include "../util/random_number.h"
#include "../util/md5.h"

#include <ctime>
#include <sstream>

using std::string;
using std::shared_ptr;
using std::stringstream;

using rocksdb::DB;
using rocksdb::Slice;
using rocksdb::Status;
using rocksdb::ReadOptions;
using rocksdb::WriteOptions;
using rocksdb::ColumnFamilyHandle;

AccessToken::AccessToken() : token(""), username("") {
}

Status AccessToken::Get(const string& token, AccessToken& a){
	a.token = token;
	return AccessToken::db->Get(ReadOptions(), AccessToken::cf.get(), Slice(a.token), &a.username);
}

Status AccessToken::Put(const string& u, AccessToken& a){
	a.username = u;
	a.token = AccessToken::createToken(u);
	return AccessToken::db->Put(WriteOptions(), AccessToken::cf.get(), Slice(a.token), Slice(u));
}


void AccessToken::setDB(shared_ptr<DB> &db, shared_ptr<ColumnFamilyHandle> &cf){
	AccessToken::db = db;
	AccessToken::cf = cf;
}

shared_ptr<DB> AccessToken::db = NULL;
shared_ptr<ColumnFamilyHandle> AccessToken::cf = NULL;

std::string AccessToken::createToken(const string& username){
	stringstream ss;
	ss << randomNumber(1000) << username << time(NULL) << randomNumber(9999) ;
	string out;
	md5Str(out, ss.str());
	return out;
}
const std::string& AccessToken::getToken(){
	return token;
}
