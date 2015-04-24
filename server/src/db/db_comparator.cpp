#include "db_comparator.h"

using rocksdb::Slice;

int DbComparator::Compare(const Slice& a, const Slice& b) const {
	size_t a_size = a.size();
	size_t b_size = b.size();
	const char* a_it = a.data();
	const char* b_it = b.data();

	for(;a_size > 0 && b_size > 0; a_size--, b_size--, a_it++, b_it++){
		if(*a_it == '/' && *b_it != '/')
			return -1;
		else if(*b_it == '/' && *a_it != '/')
			return 1;
		else if(*a_it > *b_it)
			return 1;
		else if(*a_it < *b_it)
			return -1;
	}

	if(a_size > b_size)
		return 1;
	else if(a_size < b_size)
		return -1;

	return 0;
}
