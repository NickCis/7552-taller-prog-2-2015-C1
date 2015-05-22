#ifndef __SERIALIZER_H__
#define __SERIALIZER_H__

#include <string>
#include <algorithm>
#include <type_traits>

template <typename T>
class NoPrefix {
	public:
		NoPrefix(T& t, size_t s = 0) :
			str(t),
			_size(s) {}

		T* operator->(){
			return &this->str;
		}

		size_t size(){
			return this->_size;
		}

	protected:
		T& str;
		size_t _size;
};

typedef NoPrefix<std::string> StrNoPrefix;
typedef NoPrefix<const std::string> ConstStrNoPrefix;

class Ignore {
	public:
		Ignore();

		Ignore(const char*);
		Ignore(const size_t);
		Ignore(const int);
		Ignore(const char);

		//template <typename T>
		//Ignore(const T&) :
		//	_size(sizeof(T)) {}

		size_t size();

	protected:
		size_t _size;
};

class OSerializer {
	public:
		OSerializer(std::string &buffer, size_t size=0);

		OSerializer& operator<<(const std::string&);
		OSerializer& operator<<(const std::vector<char>&);
		OSerializer& operator<<(StrNoPrefix) __attribute__ ((deprecated));
		OSerializer& operator<<(ConstStrNoPrefix);

		/** Debe finalizar con '\0' como toda string en c!
		 */
		OSerializer& operator<<(const char* &);

		template<typename T>
		OSerializer& operator<<(const T& t){
			buffer.reserve(buffer.size()+sizeof(T));
			buffer.append((char*) &t, ((char*) &t)+sizeof(T));
			return (*this);
		}

	protected:
		std::string& buffer;
};

class ISerializer {
	public:
		ISerializer(const std::string&);

		ISerializer& operator>>(std::string&);
		ISerializer& operator>>(std::vector<char>&);

		/** Pide memoria al sistema, el usuario debe liberarla!
		 */
		ISerializer& operator>>(char* &);

		ISerializer& operator>>(StrNoPrefix);
		ISerializer& operator>>(Ignore);

		template<typename T>
		ISerializer& operator>>(T& t){
			size_t size = sizeof(t);
			if(! this->errorCheck(size)){
				std::copy(it, it+size, (char*) &t);
				this->advance(size);
			}
			return (*this);
		}

		bool error();

	protected:
		const std::string& buffer;
		std::string::const_iterator it;
		size_t bufferSize;
		bool _error;

		bool errorCheck(size_t);
		void advance(size_t);
};

#endif
