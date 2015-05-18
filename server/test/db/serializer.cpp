#include "../catch.hpp"
#include "../../src/db/serializer.h"

#include <string>
#include <cstdlib>
#include <cstring>

using namespace std;

TEST_CASE( "Serializador de Salida  (class -> binario) ", "[OSerializer]" ) {
	SECTION( "Serializo un char" ) {
		char c = 't';
		string out;
		OSerializer(out) << c;

		REQUIRE( out.size() == sizeof(c) );
		REQUIRE( out[0] == c );
	}

	SECTION( "Serializo un int" ) {
		int num = 0xCCCCCCCC;
		string out;
		OSerializer(out) << num;

		REQUIRE( out.size() == sizeof(int) );
		char * num_buf = (char*) &num;
		for(size_t i=0; i < sizeof(int); i++){
			REQUIRE( out[i] == num_buf[i] );
		}
	}

	SECTION( "Serializo un uint64_t" ) {
		uint64_t num = 0xCCCCCCCCCCCCCCCC;
		string out;
		OSerializer(out) << num;

		REQUIRE( out.size() == sizeof(num) );
		char * num_buf = (char*) &num;
		for(size_t i=0; i < sizeof(num); i++){
			REQUIRE( out[i] == num_buf[i] );
		}
	}

	SECTION( "Serializo un string" ) {
		string data = "esto es un texto de prueba";
		string out;
		OSerializer(out) << data;

		REQUIRE( out.size() == data.size()+sizeof(size_t) );
		size_t size = data.size();
		char * num_buf = (char*) &size;
		for(size_t i=0; i < sizeof(size); i++){
			REQUIRE( out[i] == num_buf[i] );
		}
		int i =0;
		for(auto it=out.begin()+sizeof(size_t); it != out.end(); it++){
			REQUIRE( *(it) == data[i++] );
		}
	}

	SECTION( "Serializo un vector" ) {
		vector<char> data(10, 0xCC);
		string out;
		OSerializer(out) << data;

		REQUIRE( out.size() == data.size()+sizeof(size_t) );
		size_t size = data.size();
		char * num_buf = (char*) &size;
		for(size_t i=0; i < sizeof(size); i++){
			REQUIRE( out[i] == num_buf[i] );
		}
		int i =0;
		for(auto it=out.begin()+sizeof(size_t); it != out.end(); it++){
			REQUIRE( *(it) == data[i++] );
		}
	}

	SECTION( "Serializo un string sin prefijo de tama~no" ) {
		string data = "esto es un texto de prueba";
		string out;

		OSerializer(out) << ConstStrNoPrefix(data);
		REQUIRE( out.size() == data.size());

		int i =0;
		for(auto it=out.begin(); it != out.end(); it++){
			REQUIRE( *(it) == data[i++] );
		}
	}

	SECTION( "Serializo algo complejo" ) {
		string nombre = "pepe";
		uint64_t num = 0xCCCCCCCCCCCCCCCC;
		string out;

		OSerializer(out) << nombre << '/' << ConstStrNoPrefix(nombre) << '/' << num;
		REQUIRE( out.size() == sizeof(size_t)+nombre.size()+sizeof(char)+nombre.size()+sizeof(char)+sizeof(num));

		size_t size = nombre.size();
		char * size_char = (char*) &size;
		auto it = out.begin();
		for(size_t i=0; i < sizeof(size); i++, it++, size_char++){
			REQUIRE((*it) == (*size_char));
		}

		for(auto it_n=nombre.begin(); it_n != nombre.end(); it++, it_n++){
			REQUIRE( *it == *it_n);
		}

		REQUIRE( *it == '/');
		it++;

		for(auto it_n=nombre.begin(); it_n != nombre.end(); it++, it_n++){
			REQUIRE( *it == *it_n);
		}

		REQUIRE( *it == '/');
		it++;

		char * num_char = (char*) &num;
		for(size_t i=0; i < sizeof(num); i++, it++, num_char++){
			REQUIRE((*it) == (*num_char));
		}

		REQUIRE( memcmp((void*) (out.data()+out.size()-sizeof(num)), (void*) &num, sizeof(num)) == 0 );
	}
}

TEST_CASE( "Serializador de Entrada  (binario -> class) ", "[ISerializer]" ) {
	SECTION( "Deserializo un char" ) {
		const unsigned char buf[] = {
			't',
			0
		};
		string in((const char*) buf);

		char c;
		REQUIRE( (ISerializer(in) >> c).error() == false );
		REQUIRE( c == 't' );
	}

	SECTION( "Deserializo un int" ) {
		const unsigned char buf[] = {
			0xCC,
			0xCC,
			0xCC,
			0xCC
		};
		string in((const char*) buf, sizeof(buf));

		int num;
		REQUIRE( (ISerializer(in) >> num).error() == false);
		REQUIRE( num == 0xCCCCCCCC );
	}

	SECTION( "Deserializo un uint64_t" ) {
		const unsigned char buf[] = {
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC
		};
		string in((const char*) buf, sizeof(buf));

		uint64_t num;
		REQUIRE( (ISerializer(in) >> num).error() == false);

		REQUIRE( num == 0xCCCCCCCCCCCCCCCC );
	}

	SECTION( "Deserializo un string" ) {
		const unsigned char buf[] = {
			0x0A, 0, 0, 0,
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9'
		};
		string in((const char*) buf, sizeof(buf));

		string out;
		REQUIRE( (ISerializer(in) >> out).error() == false);

		REQUIRE( out == "0123456789" );
	}

	SECTION( "Deserializo un vector" ) {
		const unsigned char buf[] = {
			0x0A, 0, 0, 0,
			'1',
			'1',
			'1',
			'1',
			'1',
			'1',
			'1',
			'1',
			'1',
			'1'
		};
		string in((const char*) buf, sizeof(buf));

		vector<char> out;
		REQUIRE( (ISerializer(in) >> out).error() == false);

		vector<char> cmp(10, '1');

		REQUIRE( out.size() == cmp.size() );

		for(size_t s=0; s < cmp.size(); s++){
			REQUIRE( out[s] == cmp[s] );
		}
	}

	SECTION( "Deserializo un string sin prefijo de tama~no" ) {
		string in = "0123456789";

		string data;
		REQUIRE( (ISerializer(in) >> StrNoPrefix(data, 10)).error() == false);
		REQUIRE( in == data);
	}

	SECTION( "Deserializo algo complejo" ) {
		const unsigned char buf[] = {
			0x04, 0, 0, 0,
			'p',
			'e',
			'p',
			'e',
			'/',
			'p',
			'a',
			'p',
			'a',
			'/',
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC,
			0xCC
		};
		string in((const char*) buf, sizeof(buf));

		string n1;
		string n2;
		uint64_t num = 0;
		REQUIRE( (ISerializer(in) >> n1 >> Ignore('/') >> StrNoPrefix(n2, 4) >> Ignore('/') >> num).error() == false);

		REQUIRE( n1 == "pepe");
		REQUIRE( n2 == "papa");
		REQUIRE( num == 0xCCCCCCCCCCCCCCCC );

		num = 0;
		REQUIRE( (ISerializer(in) >> Ignore(sizeof(size_t)) >> Ignore(4) >> Ignore('/') >> Ignore("papa") >> Ignore('/') >> num).error() == false);
		REQUIRE( num == 0xCCCCCCCCCCCCCCCC );

		num = 0;
		REQUIRE( (ISerializer(in) >> Ignore(sizeof(size_t)+4+1+4+1) >> num).error() == false);
		REQUIRE( num == 0xCCCCCCCCCCCCCCCC );
	}
}
