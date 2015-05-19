#include "helper.h"

#include <rocksdb/db.h>

extern "C" {
	#include <sys/time.h>
}

using namespace std;
using namespace rocksdb;

rocksdb::DB* NewDB(){
	char path[255];
	struct timeval tv;
	gettimeofday(&tv, NULL);
	sprintf(path, "/tmp/testdb_%ld_%ld", tv.tv_sec, tv.tv_usec);

	DB* db;
	Options options;
	options.create_if_missing = true;
	DB::Open(options, path, &db);
	return db;
}
