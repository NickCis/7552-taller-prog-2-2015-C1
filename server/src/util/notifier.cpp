#include "notifier.h"

#include "../db/suscriber_list.h"

#include "../db/notification.h"
#include "../db/profile.h"
#include "../db/checkin.h"

#include <ctime>
#include <sstream>
#include <functional>

using std::time;
using std::string;
using std::stringstream;

MgServer* Notifier::mgServer = NULL;

bool sse_check(MgConnection& conn, Notification& n){
	if(conn.getParameter("sse") == "yes" && n.getOwner() == conn.getParameter("logged_user"))
		return true;
	return false;
}

void sse_push(MgConnection& conn, Notification& n){
	conn.printfData("id: %s\ndata: %s\n\n", n.getId().c_str(), n.toJson().c_str());
}

void Notifier::add_push_notification(const string& u, const Notification::NotificationType& type, const string& json){
	Notification n = Notification::Now(u, type, json);
	n.put();
	mgServer->doConnection(
		std::bind(sse_check, std::placeholders::_1, std::ref(n)),
		std::bind(sse_push, std::placeholders::_1, std::ref(n))
	);
}

void Notifier::OnMessage(const Message& msg){
	add_push_notification(msg.getTo(), Notification::NOTIFICATION_MESSAGE, msg.toJson());
}

void Notifier::OnChangeAvatar(const string& u){
	SuscriberList sl;
	sl.get(u);

	stringstream ss;
	ss << "{\"username\":\"" << u << "\",\"time\":" << time(NULL) << "}";
	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		add_push_notification(*it, Notification::NOTIFICATION_AVATAR, ss.str());
}

void Notifier::OnChangeProfile(const Profile& p){
	SuscriberList sl;
	sl.get(p.getOwner());

	string json = p.toJson();
	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		add_push_notification(*it, Notification::NOTIFICATION_PROFILE, json);
}

void Notifier::OnCheckIn(const Checkin& c){
	SuscriberList sl;
	sl.get(c.getOwner());

	string json = c.toJson();
	for(auto it=sl.getList().begin(); it != sl.getList().end(); it++)
		add_push_notification(*it, Notification::NOTIFICATION_CHECKIN, json);
}

void Notifier::setServer(MgServer* s){
	Notifier::mgServer = s;
}
