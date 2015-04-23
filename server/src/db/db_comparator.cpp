#include "db_comparator.h"
#include <iostream>

using rocksdb::Slice;

int DbComparator::Compare(const Slice& a, const Slice& b) const {
	size_t a_size = a.size();
	size_t b_size = b.size();
	const char* a_it = a.data();
	const char* b_it = b.data();

	for(;a_size > 0 && b_size > 0; a_size--, b_size--, a_it++, b_it++){
		if(*a_it == '/' && *b_it != '/'){
			std::cout << "/ -1 a " << a.ToString() << " b " << b.ToString() << std::endl;
			return -1;
		}else if(*b_it == '/' && *a_it != '/'){
			std::cout << "/ 1 a " << a.ToString() << " b " << b.ToString() << std::endl;
			return 1;
		}else if(*a_it > *b_it){
			std::cout << "1 a " << a.ToString() << " b " << b.ToString() << std::endl;
			return 1;
		}else if(*a_it < *b_it){
			std::cout << "-1 a " << a.ToString() << " b " << b.ToString() << std::endl;
			return -1;
		}
	}

	if(a_size > b_size){
		std::cout << "size 1 a " << a.ToString() << " b " << b.ToString() << std::endl;
		return 1;
	}else if(a_size < b_size){
		std::cout << "size -1 a " << a.ToString() << " b " << b.ToString() << std::endl;
		return -1;
	}

	std::cout << "0 a " << a.ToString() << " b " << b.ToString() << std::endl;

	return 0;
}
