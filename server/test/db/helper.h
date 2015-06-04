#ifndef __DB_HELPER_H__
#include <rocksdb/db.h>
#include <rocksdb/options.h>
#include "../src/db/list_merge_operator.h"

extern "C" {
	#include <sys/time.h>
}

template<typename T>
rocksdb::DB* NewDB(rocksdb::Comparator* comparator, T* mergeOperator){
	char path[255];
	struct timeval tv;
	gettimeofday(&tv, NULL);
	sprintf(path, "/tmp/testdb_%ld_%ld", tv.tv_sec, tv.tv_usec);

	rocksdb::DB* db;
	rocksdb::Options options;
	options.create_if_missing = true;

	if(comparator)
		options.comparator = comparator;

	if(mergeOperator)
		options.merge_operator.reset(mergeOperator);

	rocksdb::DB::Open(options, path, &db);
	return db;
}

rocksdb::DB* NewDB(){
	return NewDB((rocksdb::Comparator*) NULL, (ListMergeOperator*) NULL);
}
#endif
