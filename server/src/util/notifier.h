#ifndef __NOTIFIER_H__
#define __NOTIFIER_H__

#include <string>
#include "../mg/mg_server.h"
#include "../db/notification.h"
#include "../db/message.h"

class Notifier {
	public:
		/** Notifica acerca de un cambio de avatar
		 * @param usuario que cambio de avatar
		 */
		static void OnMessage(const Message&);

		/** Notifica acerca de un cambio de avatar
		 * @param usuario que cambio de avatar
		 */
		static void OnChangeAvatar(const std::string&);

		/** Notifica acerca de un cambio en el perfil
		 * @param usuario que cambio cosas en el perfil
		 */
		static void OnChangeProfile(const std::string&);

		/** Notifica acerca de un checkin
		 * @param usuario que hizo un checkin
		 */
		static void OnCheckIn(const std::string&);

		static void setServer(MgServer*);

	protected:
		static MgServer* mgServer;
		static void add_push_notification(const std::string& u, const Notification::NotificationType& type, const std::string& json);

};

#endif
