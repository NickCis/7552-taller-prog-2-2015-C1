#ifndef __DB_COMPARATOR_H__
#define __DB_COMPARATOR_H__

#include <rocksdb/slice.h>
#include <rocksdb/comparator.h>

class DbComparator : public rocksdb::Comparator {
	public:
		// Three-way comparison function:
		// if a < b: negative result
		// if a > b: positive result
		// else: zero result
		int Compare(const rocksdb::Slice& a, const rocksdb::Slice& b) const;

		// Ignore the following methods for now:
		const char* Name() const { return "DbComparator"; }
		void FindShortestSeparator(std::string*, const rocksdb::Slice&) const { }
		void FindShortSuccessor(std::string*) const { }
};

#endif
