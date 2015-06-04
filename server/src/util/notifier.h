#ifndef __NOTIFIER_H__
#define __NOTIFIER_H__

#include <string>

class Notifier {
	public:
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
};

#endif
