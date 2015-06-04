#ifndef __LIST_MERGE_OPERATOR_H__
#define __LIST_MERGE_OPERATOR_H__

#include <string>

#include <rocksdb/slice.h>
#include <rocksdb/merge_operator.h>

class ListMergeOperator : public rocksdb::AssociativeMergeOperator {
	public:
		bool Merge(const rocksdb::Slice& key,
				const rocksdb::Slice* existing_value,
				const rocksdb::Slice& value,
				std::string* new_value,
				rocksdb::Logger* logger) const;

		const char* Name() const;

	protected:
		bool push_back(const rocksdb::Slice* existing_value,
				const rocksdb::Slice& value,
				std::string* new_value) const;

		bool erase(const rocksdb::Slice* existing_value,
				const rocksdb::Slice& value,
				std::string* new_value) const;
};
#endif
