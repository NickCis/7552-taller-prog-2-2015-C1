#ifndef __MESSAGE_H__
#define __MESSAGE_H__

#include <ctime>
#include <string>
#include <memory>
#include <rocksdb/db.h>
#include <rocksdb/status.h>
#include <rocksdb/iterator.h>

class Message {
	public:
		class MessageIterator;

		Message();

		static rocksdb::Status Put(const std::string& to, const std::string& from, const std::string& msg, Message& a);
		static rocksdb::Status Get(const std::string& to, const std::string& from, const uint64_t& id, Message& a);

		static std::shared_ptr<Message::MessageIterator> NewIterator();
		static void SetDB(std::shared_ptr<rocksdb::DB> &db, std::shared_ptr<rocksdb::ColumnFamilyHandle> &cf);

		const std::string& getFrom() const;
		//const std::string& getTo() const;
		const std::string& getMsg() const;
		const time_t& getTime() const;
		std::string getId() const;
		//const uint64_t& getId() const;
		std::string toJson() const;

	protected:
		typedef struct MessageHeader {
			time_t arrived;
			time_t read;
			int has_file;
		} MessageHeader;

		static std::shared_ptr<rocksdb::DB> db;
		static std::shared_ptr<rocksdb::ColumnFamilyHandle> cf;
		static void GetConversationFromUser(std::vector<char>& data, const std::string& to, const std::string& from);
		static void GetKeyFromUser(std::vector<char>& data, const std::string& to, const std::string& from, const uint64_t& id);

		static rocksdb::Slice Pack(std::vector<char>& data, const Message::MessageHeader&, const std::string& from, const std::string& msg);
		static bool UnPack(const std::string& data, Message& m);

		//std::string to;
		std::string from;
		std::string msg;
		time_t arrived;
		time_t read;
		time_t t;
		uint64_t id;
		int has_file;
};

class Message::MessageIterator {
	public:
		void seek(const std::string& to, const std::string& from);
		void seekToFirst();
		rocksdb::Status seekToLast(const std::string& to, const std::string& from);
		void prev();
		void next();
		//rocksdb::Slice key() const;
		//rocksdb::Slice value() const;
		const Message& value() const;
		rocksdb::Status status() const;
		bool valid() const;
		MessageIterator(rocksdb::Iterator*);

	protected:
		std::shared_ptr<rocksdb::Iterator> it;
		Message msg;
		void unPack();
		std::vector<char> prefix;
		bool _valid() const;
};

#endif
