#include "../db/helper.h"
#include "../catch.hpp"
#include "../../src/db/message.h"
#include "../../src/db/notification.h"
#include "../../src/db/db_comparator.h"
#include "../../src/db/db_list.h"
#include "../../src/db/suscriber_list.h"
#include "../../src/mg/mg_server.h"
#include "../../src/util/notifier.h"

#include <vector>
#include <string>

using namespace std;
using namespace rocksdb;

// Mg mock ---------------
MgServer::MgServer(){}
MgServer::~MgServer(){}
void MgServer::doConnection(std::function<bool(MgConnection&)>, std::function<void(MgConnection&)>){}
enum mg_result MgServer::handler(MgConnection*,enum mg_event){return MG_TRUE;}
enum mg_result MgServer::handlerAuth(MgConnection&){return MG_TRUE;}
enum mg_result MgServer::handlerClose(MgConnection*){return MG_TRUE;}
class MockServer : public MgServer {
	protected:
		enum mg_result handlerRequest(MgConnection&){return MG_TRUE;}
};
// mg Mock

// MgConnection
const std::string& MgConnection::getParameter(const std::string&){return this->parameters[""];}
size_t MgConnection::printfData(const char*, ...){return 0;}
// ---

// DbList Mock
const std::vector<std::string>& DbList::getList() const { if(this->list.size() == 0) ((DbList*)this)->list.push_back("foo"); return this->list;}
rocksdb::Status DbList::push_back(const std::string&){return Status::OK();}
rocksdb::Status DbList::erase(const std::string&){return Status::OK();}
bool DbList::unPack(const std::string&, const std::string&){return true;}
void DbList::packValue(std::string&){}
// ----------------------

// SuscriberList mock -----
DB_ENTITY_DEF(SuscriberList)
SuscriberList::SuscriberList(){ this->owner = "pepe"; }
std::string SuscriberList::toJson() const {return "";}
rocksdb::Status SuscriberList::get(const std::string&){return Status::OK();}
const std::string& SuscriberList::getOwner() const { return this->owner; }
void SuscriberList::setOwner(const std::string&){}
bool SuscriberList::unPack(const std::string&, const std::string&){return true;}
void SuscriberList::packKey(std::string&){}
// ----------------------

TEST_CASE( "Probando Notifier", "[Notifier]" ) {
	DbComparator cmp;
	DB* db = NewDB(&cmp, (ListMergeOperator*) NULL);
	Notification::SetDB(db, db->DefaultColumnFamily());
	MockServer mgServer;

	Notifier::setServer(&mgServer);

	SECTION("OnMessage"){
		Message msg = Message::Now("foo", "pepe", "message");
		Notifier::OnMessage(msg);
		auto it = Notification::NewIterator();
		it.seek("foo");

		int i;
		for(i=0; it.valid(); it.next(), i++){
			REQUIRE( it.value().getData() == msg.toJson() );
		}

		REQUIRE( i == 1 );
	}

	SECTION("OnChangeProfile"){
		Profile p;
		p.setOwner("pepe");
		p.setNick("soy pepe");
		p.setStatus("mi estado");
		p.setOnline(false);
		Notifier::OnChangeProfile(p);

		auto it = Notification::NewIterator();
		it.seek("foo");
		int i;
		for(i=0; it.valid(); it.next(), i++){
			REQUIRE( it.value().getData() == p.toJson() );
		}

		REQUIRE( i == 1 );
	}

	SECTION("OnChangeAvatar"){
		Notifier::OnChangeAvatar("pepe");

		auto it = Notification::NewIterator();
		it.seek("foo");
		int i;
		for(i=0; it.valid(); it.next(), i++){
		}

		REQUIRE( i == 1 );
	}

	SECTION("OnCheckIn"){
		Checkin c;
		c.setOwner("pepe");
		c.setName("lugar");
		c.setPosition(42, 54);

		Notifier::OnCheckIn(c);

		auto it = Notification::NewIterator();
		it.seek("foo");
		int i;
		for(i=0; it.valid(); it.next(), i++){
			REQUIRE( it.value().getData() == c.toJson() );
		}

		REQUIRE( i == 1 );
	}
}
